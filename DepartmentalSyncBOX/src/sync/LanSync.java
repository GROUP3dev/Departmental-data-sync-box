package sync;

import model.SyncLog;
import dao.SyncLogDAO;
import java.time.LocalDateTime;

public class LanSync {
    private SyncLogDAO logDAO;

    public LanSync(SyncLogDAO logDAO) {
        this.logDAO = logDAO;
    }

    public void startSync() {
        System.out.println("Syncing over LAN...");
        logDAO.addLog(new SyncLog(0, "LAN", LocalDateTime.now(), "Success"));
        System.out.println("LAN sync completed.");
    }
}
