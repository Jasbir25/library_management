package U3_EP1;
import java.sql.*;
import java.util.Scanner;

public class MMS {

    public static void memberManagement(Connection conn, Scanner sc) {
        String choice;
        do {
            System.out.println("\n--- Member Management ---");
            System.out.println("1. Add Member");
            System.out.println("2. Update Member");
            System.out.println("3. Delete Member");
            System.out.println("4. Display Members");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            choice = sc.nextLine();

            try {
                switch (choice) {
                    case "1":
                        addMember(conn, sc);
                        break;
                    case "2":
                        updateMember(conn, sc);
                        break;
                    case "3":
                        deleteMember(conn, sc);
                        break;
                    case "4":
                        displayMembers(conn);
                        break;
                    case "5":
                        System.out.println("Returning to Main Menu...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } catch (SQLException e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        } while (!choice.equals("5"));
    }

    public static void addMember(Connection conn, Scanner sc) throws SQLException {
        String insertMember = "INSERT INTO Members(member_id, name, email, phone, membership_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertMember)) {
            System.out.print("Enter member ID: ");
            int memberId = sc.nextInt();
            sc.nextLine(); // Consume newline
            System.out.print("Enter name: ");
            String name = sc.nextLine();
            System.out.print("Enter email: ");
            String email = sc.nextLine();
            System.out.print("Enter phone: ");
            String phone = sc.nextLine();
            System.out.print("Enter membership date (YYYY-MM-DD): ");
            String membershipDate = sc.nextLine();

            ps.setInt(1, memberId);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, membershipDate);

            int rowInserted = ps.executeUpdate();
            if (rowInserted > 0) {
                System.out.println("Member Added Successfully");
            }
        }
    }

    public static void updateMember(Connection conn, Scanner sc) throws SQLException {
        String updateMember = "UPDATE Members SET name = ?, email = ?, phone = ?, membership_date = ? WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateMember)) {
            System.out.print("Enter member ID to update: ");
            int memberId = sc.nextInt();
            sc.nextLine(); // Consume newline

            if (!memberExists(conn, memberId)) {
                System.out.println("Error: Member ID not found.");
                return;
            }

            System.out.print("Enter new name: ");
            String name = sc.nextLine();
            System.out.print("Enter new email: ");
            String email = sc.nextLine();
            System.out.print("Enter new phone: ");
            String phone = sc.nextLine();
            System.out.print("Enter new membership date (YYYY-MM-DD): ");
            String membershipDate = sc.nextLine();

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, membershipDate);
            ps.setInt(5, memberId);

            int rowUpdated = ps.executeUpdate();
            if (rowUpdated > 0) {
                System.out.println("Member Updated Successfully");
            }
        }
    }

    public static void deleteMember(Connection conn, Scanner sc) throws SQLException {
        String deleteMember = "DELETE FROM Members WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteMember)) {
            System.out.print("Enter member ID to delete: ");
            int memberId = sc.nextInt();
            sc.nextLine();

            if (!memberExists(conn, memberId)) {
                System.out.println("Error: Member ID not found.");
                return;
            }

            int rowDeleted = ps.executeUpdate();
            if (rowDeleted > 0) {
                System.out.println("Member Deleted Successfully");
            }
        }
    }

    public static void displayMembers(Connection conn) throws SQLException {
        String selectMembers = "SELECT * FROM Members";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(selectMembers)) {
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                System.out.println("Member ID: " + rs.getInt("member_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("Membership Date: " + rs.getString("membership_date"));
                System.out.println("-----------------------------");
            }
            if (!hasResults) {
                System.out.println("No members found.");
            }
        }
    }

    private static boolean memberExists(Connection conn, int memberId) throws SQLException {
        String query = "SELECT * FROM Members WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }
}
