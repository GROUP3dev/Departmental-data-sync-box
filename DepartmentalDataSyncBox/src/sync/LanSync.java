package sync;

import dao.SyncQueueDAO;
import dao.SyncLogDAO;
import model.SyncQueue;
import model.SyncLog;
import util.NodeConfig;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles synchronization over the Local Area Network (LAN) using TCP sockets.
 */
public class LanSync {
    private static final Logger LOGGER = Logger.getLogger(LanSync.class.getName());
    private static final int PORT = 8888;
    private ServerSocket serverSocket;
    private final SyncQueueDAO syncQueueDAO = new SyncQueueDAO();
    private final SyncLogDAO syncLogDAO = new SyncLogDAO();
    private final DatabaseSyncService syncService = new DatabaseSyncService();

    // 1. LAN Server (Inbound Sync Listener)
    public void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                LOGGER.info("LAN Sync Server started on port " + PORT + ". Node ID: " + NodeConfig.NODE_ID);
                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new ClientHandler(clientSocket, syncService, syncLogDAO)).start();
                }
            } catch (IOException e) {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    LOGGER.log(Level.SEVERE, "LAN Sync Server error", e);
                }
            }
        }, "LanSync-Server-Thread").start();
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final DatabaseSyncService syncService;
        private final SyncLogDAO syncLogDAO;

        public ClientHandler(Socket socket, DatabaseSyncService service, SyncLogDAO logDAO) {
            this.clientSocket = socket;
            this.syncService = service;
            this.syncLogDAO = logDAO;
        }

        @Override
        public void run() {
            String remoteIp = clientSocket.getInetAddress().getHostAddress();
            int processedCount = 0;
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                @SuppressWarnings("unchecked")
                List<SyncPayload> payloads = (List<SyncPayload>) in.readObject();
                LOGGER.info("Received " + payloads.size() + " payloads from " + remoteIp);

                for (SyncPayload payload : payloads) {
                    syncService.applyPayload(payload);
                    processedCount++;
                }

                // Log Success
                syncLogDAO.logSyncEvent(new SyncLog(
                        remoteIp, "INBOUND", "SUCCESS", processedCount, 0,
                        "Received " + processedCount + " items from " + remoteIp));

            } catch (IOException | ClassNotFoundException e) {
                LOGGER.log(Level.WARNING, "Error handling inbound LAN sync connection.", e);
                // Log Failure
                syncLogDAO.logSyncEvent(new SyncLog(
                        remoteIp, "INBOUND", "FAILURE", processedCount, 0,
                        "Error: " + e.getMessage()));
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    // 2. LAN Client (Outbound Sync Sender)
    public void sendToRemoteNode(String remoteIpAddress) {
        // Check for pending items BEFORE opening a connection
        List<SyncQueue> pendingItems = syncQueueDAO.getPendingItems(50);
        if (pendingItems.isEmpty()) {
            return;
        }

        int maxRetries = 3;
        int attempt = 0;
        boolean success = false;

        while (attempt < maxRetries && !success) {
            attempt++;
            try (Socket socket = new Socket(remoteIpAddress, PORT);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                List<SyncPayload> payloads = convertQueueToPayloads(pendingItems);

                out.writeObject(payloads);
                out.flush();
                LOGGER.info("Sent " + payloads.size() + " payloads to " + remoteIpAddress);

                // Update queue status after successful send
                for (SyncQueue item : pendingItems) {
                    syncQueueDAO.updateStatus(item.getQueueId(), "COMPLETED");
                }
                success = true;

                // Log Success
                syncLogDAO.logSyncEvent(new SyncLog(
                        remoteIpAddress, "OUTBOUND", "SUCCESS", payloads.size(), 0,
                        "Sent " + payloads.size() + " items to " + remoteIpAddress));

            } catch (IOException e) {
                LOGGER.log(Level.WARNING,
                        "Attempt " + attempt + " failed to send to " + remoteIpAddress + ": " + e.getMessage());
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(2000); // Wait 2 seconds before retry
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    LOGGER.log(Level.SEVERE,
                            "Failed to send to " + remoteIpAddress + " after " + maxRetries + " attempts.");

                    // Log Failure
                    syncLogDAO.logSyncEvent(new SyncLog(
                            remoteIpAddress, "OUTBOUND", "FAILURE", 0, 0,
                            "Failed after " + maxRetries + " attempts. Error: " + e.getMessage()));
                }
            }
        }
    }

    private List<SyncPayload> convertQueueToPayloads(List<SyncQueue> queueItems) {
        return queueItems.stream()
                .map(item -> new SyncPayload(
                        item.getEntityName(),
                        item.getOperationType(),
                        item.getSyncUUID(),
                        item.getLastModifiedTs(),
                        item.getSourceActorId(),
                        item.getPayloadJson()))
                .toList();
    }
}