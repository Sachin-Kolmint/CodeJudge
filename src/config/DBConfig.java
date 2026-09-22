package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads database connection settings from config/db.properties
 * (a classpath resource) so credentials stay out of Java code.
 *
 * Setup: copy db.properties.example to db.properties in this
 * same package/folder and fill in your local MySQL credentials.
 * db.properties is git-ignored.
 */
public final class DBConfig {

    private static final String CONFIG_FILE = "/config/db.properties";

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = DBConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException(
                        "Missing config/db.properties. Copy db.properties.example to "
                                + "db.properties and set your local MySQL credentials.");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load db.properties", e);
        }
    }

    private DBConfig() {
        // utility class, not instantiable
    }

    public static String getUrl() {
        return PROPERTIES.getProperty("db.url");
    }

    public static String getUser() {
        return PROPERTIES.getProperty("db.user");
    }

    public static String getPassword() {
        return PROPERTIES.getProperty("db.password");
    }
}
