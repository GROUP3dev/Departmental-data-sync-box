package util;

import db.DBConnection;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseDumper {

    public static boolean dumpDatabase(String outputFilePath) {
        try (Connection conn = DBConnection.getInstance().getConnection();
                PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath))) {

            writer.println("-- Departmental Data Sync Box Database Dump");
            writer.println("-- Generated at: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            writer.println("-- ------------------------------------------------------");
            writer.println();

            // Disable foreign key checks for import
            writer.println("SET FOREIGN_KEY_CHECKS=0;");
            writer.println();

            List<String> tables = getTableNames(conn);

            for (String table : tables) {
                dumpTableStructure(conn, table, writer);
                dumpTableData(conn, table, writer);
                writer.println();
            }

            // Re-enable foreign key checks
            writer.println("SET FOREIGN_KEY_CHECKS=1;");

            return true;

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static List<String> getTableNames(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        }
        return tables;
    }

    private static void dumpTableStructure(Connection conn, String tableName, PrintWriter writer) throws SQLException {
        writer.println("-- Structure for table `" + tableName + "`");
        writer.println("DROP TABLE IF EXISTS `" + tableName + "`;");

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE `" + tableName + "`")) {
            if (rs.next()) {
                writer.println(rs.getString(2) + ";");
            }
        }
        writer.println();
    }

    private static void dumpTableData(Connection conn, String tableName, PrintWriter writer) throws SQLException {
        writer.println("-- Dumping data for table `" + tableName + "`");
        writer.println("LOCK TABLES `" + tableName + "` WRITE;");

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM `" + tableName + "`")) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                writer.print("INSERT INTO `" + tableName + "` VALUES (");
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1)
                        writer.print(",");

                    Object value = rs.getObject(i);
                    if (value == null) {
                        writer.print("NULL");
                    } else if (value instanceof Number) {
                        writer.print(value);
                    } else if (value instanceof Boolean) {
                        writer.print((Boolean) value ? 1 : 0);
                    } else {
                        String strValue = value.toString();
                        // Escape special characters for SQL
                        strValue = strValue.replace("\\", "\\\\")
                                .replace("'", "\\'")
                                .replace("\r", "\\r")
                                .replace("\n", "\\n");
                        writer.print("'" + strValue + "'");
                    }
                }
                writer.println(");");
            }
        }

        writer.println("UNLOCK TABLES;");
        writer.println();
    }
}
