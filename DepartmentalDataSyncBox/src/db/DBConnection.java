package db;

import util.ConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        try {
            // Load MySQL Driver
            Class.forName(ConfigUtil.getProperty("db.driver"));
            this.connection = createConnection();
            LOGGER.info("Database connection established successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to database.", e);
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        } else {
            try {
                if (instance.connection == null || instance.connection.isClosed()) {
                    instance = new DBConnection();
                }
            } catch (SQLException e) {
                instance = new DBConnection();
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                LOGGER.info("Connection closed, reconnecting...");
                this.connection = createConnection();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting connection.", e);
        }
        return connection;
    }

    public Connection createConnectionForTest() throws SQLException {
        return createConnection();
    }

    private Connection createConnection() throws SQLException {
        String url = "jdbc:mysql://" + ConfigUtil.getProperty("db.host") + ":" +
                ConfigUtil.getProperty("db.port") + "/" +
                ConfigUtil.getProperty("db.name") +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=5000"; // Added timeout
        return DriverManager.getConnection(url, ConfigUtil.getProperty("db.user"),
                ConfigUtil.getProperty("db.password"));
    }
}