package U3_EP1;

import java.sql.*;
import java.util.Scanner;

/**
 * Transaction Management System
 * 
 * Handles transaction-related operations.
 * 
 */
public class TMS {

    public static void transactionManagement(Connection conn, Scanner sc) {
        String choice;
        do {
            System.out.println("\n--- Transaction Management ---");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. Query Transactions");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            choice = sc.nextLine();

            switch (choice) {
                case "1":
                    borrowBook(conn, sc);
                    break;
                case "2":
                    returnBook(conn, sc);
                    break;
                case "3":
                    queryTransaction(conn, sc);
                    break;
                case "4":
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (!choice.equals("4"));
    }

    public static void borrowBook(Connection conn, Scanner sc) {
        System.out.print("Enter member ID: ");
        int memberId = sc.nextInt();
        sc.nextLine();

        try {
            if (!memberExists(conn, memberId)) {
                System.out.println("Error: Member does not exist.");
                return;
            }

            System.out.print("Enter transaction ID: ");
            int transactionId = sc.nextInt();
            sc.nextLine();

            if (transactionIdExists(conn, transactionId)) {
                System.out.println("Error: Transaction ID already exists. Please use a unique ID.");
                return;
            }

            System.out.print("Enter book ID: ");
            int bookId = sc.nextInt();
            sc.nextLine();

            if (!bookAvailable(conn, bookId)) {
                System.out.println("Error: Book is not available.");
                return;
            }

            String borrowTransaction = "INSERT INTO Transactions(transaction_id, book_id, member_id, borrow_date, status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(borrowTransaction)) {
                System.out.print("Enter borrow date (YYYY-MM-DD): ");
                String borrowDate = sc.nextLine();

                ps.setInt(1, transactionId);
                ps.setInt(2, bookId);
                ps.setInt(3, memberId);
                ps.setString(4, borrowDate);
                ps.setString(5, "borrowed");

                int rowsInserted = ps.executeUpdate();
                if (rowsInserted > 0) {
                    updateBookCopies(conn, bookId, -1); // Decrease available copies
                    System.out.println("Book Borrowed Successfully.");
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static void returnBook(Connection conn, Scanner sc) {
        System.out.print("Enter transaction ID: ");
        int transactionId = sc.nextInt();
        sc.nextLine();

        String returnTransaction = "UPDATE Transactions SET return_date = ?, status = ? WHERE transaction_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(returnTransaction)) {
            System.out.print("Enter return date (YYYY-MM-DD): ");
            String returnDate = sc.nextLine();

            ps.setString(1, returnDate);
            ps.setString(2, "returned");
            ps.setInt(3, transactionId);

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                int bookId = getBookIdFromTransaction(conn, transactionId);
                updateBookCopies(conn, bookId, 1); // Increase available copies
                System.out.println("Book Returned Successfully.");
            } else {
                System.out.println("No transaction found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static void queryTransaction(Connection conn, Scanner sc) {
        System.out.print("Enter member ID or book ID to query transactions: ");
        int id = sc.nextInt();
        sc.nextLine();

        String query = "SELECT * FROM Transactions WHERE member_id = ? OR book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.setInt(2, id);
            ResultSet rs = ps.executeQuery();

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                System.out.println("Transaction ID: " + rs.getInt("transaction_id"));
                System.out.println("Book ID: " + rs.getInt("book_id"));
                System.out.println("Member ID: " + rs.getInt("member_id"));
                System.out.println("Borrow Date: " + rs.getString("borrow_date"));
                System.out.println("Return Date: " + rs.getString("return_date"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("-----------------------------");
            }

            if (!hasResults) {
                System.out.println("No transactions found for the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static boolean memberExists(Connection conn, int memberId) throws SQLException {
        String query = "SELECT 1 FROM Members WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private static boolean transactionIdExists(Connection conn, int transactionId) throws SQLException {
        String query = "SELECT 1 FROM Transactions WHERE transaction_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, transactionId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private static boolean bookAvailable(Connection conn, int bookId) throws SQLException {
        String query = "SELECT available_copies FROM Books WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("available_copies") > 0;
            }
            System.out.println("Error: Book ID not found.");
            return false;
        }
    }

    private static void updateBookCopies(Connection conn, int bookId, int change) throws SQLException {
        String updateCopies = "UPDATE Books SET available_copies = available_copies + ? WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateCopies)) {
            ps.setInt(1, change);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        }
    }

    private static int getBookIdFromTransaction(Connection conn, int transactionId) throws SQLException {
        String query = "SELECT book_id FROM Transactions WHERE transaction_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, transactionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("book_id");
            }
            throw new SQLException("Transaction ID not found.");
        }
    }
}