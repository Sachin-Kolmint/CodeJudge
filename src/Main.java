import config.DBConnection;

import java.sql.Connection;

/**
 * Temporary entry point / sanity check for the shared database
 * foundation. Each module's real UI/controller will eventually
 * replace or call into this. For now it just verifies that the
 * JDBC connection is configured correctly.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("CodeJudge - checking database connection...");
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection successful: " + conn.getCatalog());
            }
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
            System.err.println("Did you copy src/config/db.properties.example to "
                    + "src/config/db.properties and set your MySQL credentials?");
        }
    }
}
