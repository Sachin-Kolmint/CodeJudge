package modules.adminAuth;

import modules.adminAuth.AdminService;
import java.util.Scanner;

public class AdminLoginTest {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        AdminService adminService = new AdminService();

        System.out.println("===== CodeJudge Admin Login =====");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        boolean success = adminService.login(username, password);

        if (success) {
            System.out.println("Admin access granted.");
            System.out.println("Logged in as: "
                    + adminService.getLoggedInAdmin().getUsername());

            System.out.println("\nLogging out...");
            adminService.logout();

            System.out.println("Is logged in? "
                    + adminService.isLoggedIn());
        } else {
            System.out.println("Admin access denied.");
        }

        scanner.close();
    }
}