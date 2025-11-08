package dao;

import model.SyncLog;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SyncLogDAO {
    private List<SyncLog> logs = new ArrayList<>();

    public SyncLogDAO() {
        logs.add(new SyncLog(1, "LAN", LocalDateTime.now(), "Success"));
    }

    public List<SyncLog> getAllLogs() {
        return logs;
    }

    public void addLog(SyncLog log) {
        logs.add(log);
    }
}
