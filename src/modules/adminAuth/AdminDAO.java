package modules.adminAuth;

import modules.adminAuth.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDAO {

    public Admin login(String username, String password) {

        String sql = "SELECT admin_id, username, email FROM admins "
                   + "WHERE username = ? AND password = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                return new Admin(
                    resultSet.getInt("admin_id"),
                    resultSet.getString("username"),
                    resultSet.getString("email")
                );
            }

        } catch (Exception e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return null;
    }
}