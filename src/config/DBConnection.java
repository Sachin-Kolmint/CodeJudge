package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides JDBC connections to the CodeJudge database.
 *
 * Usage in a DAO:
 *   try (Connection conn = DBConnection.getConnection();
 *        PreparedStatement ps = conn.prepareStatement(sql)) {
 *       ...
 *   }
 *
 * Every DAO should open a connection, use it, and close it via
 * try-with-resources -- do not hold a shared long-lived
 * Connection across requests.
 *
 * Requires the MySQL Connector/J JAR on the classpath.
 */
public final class DBConnection {

    private DBConnection() {
        // utility class, not instantiable
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DBConfig.getUrl(),
                DBConfig.getUser(),
                DBConfig.getPassword()
        );
    }
}
