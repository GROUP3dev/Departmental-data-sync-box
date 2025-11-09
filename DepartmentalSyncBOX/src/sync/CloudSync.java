package sync;

import model.SyncLog;
import dao.SyncLogDAO;
import java.time.LocalDateTime;

public class CloudSync {
    private SyncLogDAO logDAO;

    public CloudSync(SyncLogDAO logDAO) {
        this.logDAO = logDAO;
    }

    public void startSync() {
        System.out.println("Syncing data to cloud...");
        logDAO.addLog(new SyncLog(0, "CLOUD", LocalDateTime.now(), "Success"));
        System.out.println("Cloud sync completed.");
    }
}
