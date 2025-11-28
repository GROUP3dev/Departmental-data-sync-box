package model;

public class DatabaseSession {
    private long id;
    private String user;
    private String host;
    private String db;
    private String command;
    private int time;
    private String state;
    private String info;

    public DatabaseSession(long id, String user, String host, String db, String command, int time, String state,
            String info) {
        this.id = id;
        this.user = user;
        this.host = host;
        this.db = db;
        this.command = command;
        this.time = time;
        this.state = state;
        this.info = info;
    }

    public long getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getHost() {
        return host;
    }

    public String getDb() {
        return db;
    }

    public String getCommand() {
        return command;
    }

    public int getTime() {
        return time;
    }

    public String getState() {
        return state;
    }

    public String getInfo() {
        return info;
    }
}
