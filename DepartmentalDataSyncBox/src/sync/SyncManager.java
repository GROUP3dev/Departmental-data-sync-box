package sync;

import util.NodeConfig;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * The central orchestrator for synchronization.
 */
public class SyncManager {
    private static final Logger LOGGER = Logger.getLogger(SyncManager.class.getName());
    private static SyncManager instance;

    private final LanSync lanSync = new LanSync();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public enum SyncType {
        LAN, CLOUD, ALL
    }

    private SyncManager() {
        // LanSync server must be started early to listen for incoming changes
        lanSync.startServer();
    }

    public static synchronized SyncManager getInstance() {
        if (instance == null) {
            instance = new SyncManager();
        }
        return instance;
    }

    public void startPeriodicSync() {
        // Schedule sync task to run every 30 seconds
        scheduler.scheduleAtFixedRate(this::performPeriodicSync, 5, 30, TimeUnit.SECONDS);
    }

    private void performPeriodicSync() {
        LOGGER.info("--- Periodic Sync Cycle Started on Node: " + NodeConfig.NODE_ID + " ---");
        try {
            java.util.List<model.NodeInfo> nodes = new dao.DepartmentDAO().findAllNodes();
            for (model.NodeInfo node : nodes) {
                // Don't sync to self if IP matches (assuming NodeConfig has IP or we check
                // localhost)
                // For now, just sync to all other active nodes found in DB
                if (node.getNodeIp() != null && !node.getNodeIp().isEmpty()) {
                    LOGGER.info("Syncing to peer: " + node.getNodeIp());
                    lanSync.sendToRemoteNode(node.getNodeIp());
                }
            }
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Error during periodic sync", e);
        }
        LOGGER.info("--- Periodic Sync Cycle Finished ---");
    }

    public void initiateSync(SyncType type, String targetIp) {
        new Thread(() -> {
            LOGGER.info("Manual Sync initiated: " + type);
            if (type == SyncType.LAN || type == SyncType.ALL) {
                if (targetIp != null && !targetIp.isEmpty()) {
                    lanSync.sendToRemoteNode(targetIp);
                } else {
                    LOGGER.warning("No target IP specified for LAN sync.");
                }
            }
        }).start();
    }
}