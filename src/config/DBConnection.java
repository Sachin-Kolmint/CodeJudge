package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() throws SQLException {
        String password = System.getenv("CODEJUDGE_DB_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new SQLException("CODEJUDGE_DB_PASSWORD is not set.");
        }

        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/codejudge",
            "root",
            password
        );
    }
}