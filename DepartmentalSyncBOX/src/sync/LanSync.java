package sync;

import dao.SyncLogDAO;
import model.SyncLog;

import java.time.LocalDateTime;

public class LanSync {
    private final SyncLogDAO logDAO;
    private final SyncManager syncManager;

    public LanSync(SyncLogDAO logDAO, SyncManager syncManager) {
        this.logDAO = logDAO;
        this.syncManager = syncManager;
    }

    public void startSync() {
        log("Started");
        try {
            syncManager.sync();
            log("Completed successfully");
        } catch (RuntimeException ex) {
            log("Failed: " + ex.getMessage());
        }
    }

    private void log(String status) {
        logDAO.addLog(new SyncLog(0, "LAN", LocalDateTime.now(), status));
    }

    public static void main(String[] args) {
        SyncManager manager = new SyncManager(
                args.length > 0 ? args[0] : "data/source",
                args.length > 1 ? args[1] : "data/destination"
        );

        LanSync sync = new LanSync(new dao.SyncLogDAO(), manager);
        sync.startSync();
    }
}
