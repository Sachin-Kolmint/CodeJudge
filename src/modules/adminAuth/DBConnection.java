package modules.adminAuth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            System.getenv().getOrDefault("CODEJUDGE_DB_URL",
                    "jdbc:mysql://localhost:3306/codejudge");

    private static final String USER =
            System.getenv().getOrDefault("CODEJUDGE_DB_USER", "root");

    private static final String PASSWORD =
            System.getenv("CODEJUDGE_DB_PASSWORD");

    public static Connection getConnection() throws SQLException {

        if (PASSWORD == null || PASSWORD.isEmpty()) {
            throw new SQLException(
                    "CODEJUDGE_DB_PASSWORD environment variable is not set."
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
