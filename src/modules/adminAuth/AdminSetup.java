package modules.adminAuth;

import java.io.Console;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;

import modules.candidateAuth.PasswordUtil;

public class AdminSetup {

    public static void main(String[] args) {
        Console console = System.console();

        if (console == null) {
            System.out.println("Run AdminSetup from Windows CMD.");
            return;
        }

        String fullName = console.readLine("Full name: ");
        String username = console.readLine("Username: ");

        if (fullName == null || username == null) {
            return;
        }

        fullName = fullName.trim();
        username = username.trim();

        if (fullName.isEmpty() || fullName.length() > 100
                || username.isEmpty() || username.length() > 50) {
            System.out.println(
                    "Name: 1-100 characters; username: 1-50 characters.");
            return;
        }

        char[] password = console.readPassword("Password: ");
        char[] confirmation = null;

        try {
            if (password == null) {
                return;
            }

            confirmation = console.readPassword("Confirm password: ");

            if (confirmation == null) {
                return;
            }

            if (password.length < 8
                    || new String(password).isBlank()) {
                System.out.println(
                        "Use at least 8 characters, not only spaces.");
                return;
            }

            if (!Arrays.equals(password, confirmation)) {
                System.out.println("Passwords do not match.");
                return;
            }

            String hash = PasswordUtil.hash(new String(password));

            AdminAuthDAO dao = new AdminAuthDAO();
            boolean created = dao.createAdmin(fullName, username, hash);

            System.out.println(created
                    ? "Admin account created successfully."
                    : "Admin account was not created.");

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println(
                    "Account could not be created. Username may already exist.");
        } catch (SQLException e) {
            System.out.println(
                    "Database error. Check MySQL and CODEJUDGE_DB_PASSWORD.");
        } catch (GeneralSecurityException e) {
            System.out.println("Password hashing failed.");
        } finally {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
            if (confirmation != null) {
                Arrays.fill(confirmation, '\0');
            }
        }
    }
}