package dao;

import db.DBConnection;
import model.DatabaseSession;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MonitoringDAO {

    public List<DatabaseSession> getActiveSessions() {
        List<DatabaseSession> sessions = new ArrayList<>();
        String sql = "SELECT ID, USER, HOST, DB, COMMAND, TIME, STATE, INFO FROM information_schema.processlist WHERE DB = 'dept_sync_db' ORDER BY TIME DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sessions.add(new DatabaseSession(
                        rs.getLong("ID"),
                        rs.getString("USER"),
                        rs.getString("HOST"),
                        rs.getString("DB"),
                        rs.getString("COMMAND"),
                        rs.getInt("TIME"),
                        rs.getString("STATE"),
                        rs.getString("INFO")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessions;
    }
}
