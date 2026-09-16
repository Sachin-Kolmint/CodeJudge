package common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Shared helpers for safely closing JDBC resources.
 * Prefer try-with-resources in new code; use these only where
 * try-with-resources isn't practical (e.g. closing in a finally
 * block after partial success).
 */
public final class DBUtil {

    private DBUtil() {
        // utility class, not instantiable
    }

    public static void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception ignored) {
                // nothing to do
            }
        }
    }

    public static void closeQuietly(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception ignored) {
                // nothing to do
            }
        }
    }

    public static void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ignored) {
                // nothing to do
            }
        }
    }
}
