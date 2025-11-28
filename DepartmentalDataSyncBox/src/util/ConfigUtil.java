package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {
    private static final String CONFIG_FILE = "resources/config/db.properties";
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            // Config file might not exist yet, which is fine
            System.out.println("Config file not found, creating new one on save.");
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
        saveProperties();
    }

    private static void saveProperties() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Departmental Data Sync Box Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
