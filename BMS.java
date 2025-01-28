package U3_EP1;

import java.sql.*;
import java.util.Scanner;

/**
 * Book Management System
 * 
 * Handles book-related operations.
 * 
 */
public class BMS {

    public static void bookManagement(Connection conn, Scanner sc) {
        String choice;
        do {
            System.out.println("\n--- Book Management ---");
            System.out.println("1. Add Book");
            System.out.println("2. Update Book");
            System.out.println("3. Delete Book");
            System.out.println("4. Display Books");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            choice = sc.nextLine();

            try {
                switch (choice) {
                    case "1":
                        addBook(conn, sc);
                        break;
                    case "2":
                        updateBook(conn, sc);
                        break;
                    case "3":
                        deleteBook(conn, sc);
                        break;
                    case "4":
                        displayBooks(conn);
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

    public static void addBook(Connection conn, Scanner sc) throws SQLException {
        String insertBooks = "INSERT INTO Books(book_id, title, author, publisher, year_published, isbn, available_copies) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(insertBooks)) {
            System.out.print("Enter book ID: ");
            int book_id = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter title: ");
            String title = sc.nextLine();
            System.out.print("Enter author: ");
            String author = sc.nextLine();
            System.out.print("Enter publisher: ");
            String publisher = sc.nextLine();
            System.out.print("Enter year published: ");
            int year_published = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter ISBN: ");
            String isbn = sc.nextLine();
            System.out.print("Enter available copies: ");
            int available_copies = sc.nextInt();
            sc.nextLine();

            ps.setInt(1, book_id);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setString(4, publisher);
            ps.setInt(5, year_published);
            ps.setString(6, isbn);
            ps.setInt(7, available_copies);

            int rowInserted = ps.executeUpdate();
            if (rowInserted > 0) {
                System.out.println("Book Added Successfully");
            }
        }
    }

    public static void updateBook(Connection conn, Scanner sc) throws SQLException {
        String updateBooks = "UPDATE Books SET title = ?, author = ?, publisher = ?, year_published = ?, isbn = ?, available_copies = ? WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateBooks)) {
            System.out.print("Enter book ID to update: ");
            int book_id = sc.nextInt();
            sc.nextLine(); // Consume newline

            if (!bookExists(conn, book_id)) {
                System.out.println("Error: Book ID not found.");
                return;
            }

            System.out.print("Enter new title: ");
            String title = sc.nextLine();
            System.out.print("Enter new author: ");
            String author = sc.nextLine();
            System.out.print("Enter new publisher: ");
            String publisher = sc.nextLine();
            System.out.print("Enter new year published: ");
            int year_published = sc.nextInt();
            sc.nextLine(); // Consume newline
            System.out.print("Enter new ISBN: ");
            String isbn = sc.nextLine();
            System.out.print("Enter new available copies: ");
            int available_copies = sc.nextInt();
            sc.nextLine();

            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, publisher);
            ps.setInt(4, year_published);
            ps.setString(5, isbn);
            ps.setInt(6, available_copies);
            ps.setInt(7, book_id);

            int rowUpdated = ps.executeUpdate();
            if (rowUpdated > 0) {
                System.out.println("Book Updated Successfully");
            }
        }
    }

    public static void deleteBook(Connection conn, Scanner sc) throws SQLException {
        String deleteBooks = "DELETE FROM Books WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteBooks)) {
            System.out.print("Enter book ID to delete: ");
            int book_id = sc.nextInt();
            sc.nextLine();

            if (!bookExists(conn, book_id)) {
                System.out.println("Error: Book ID not found.");
                return;
            }

            int rowDeleted = ps.executeUpdate();
            if (rowDeleted > 0) {
                System.out.println("Book Deleted Successfully");
            }
        }
    }

    public static void displayBooks(Connection conn) throws SQLException {
        String selectBooks = "SELECT * FROM Books";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(selectBooks)) {
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                System.out.println("Book ID: " + rs.getInt("book_id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Author: " + rs.getString("author"));
                System.out.println("Publisher: " + rs.getString("publisher"));
                System.out.println("Year Published: " + rs.getInt("year_published"));
                System.out.println("ISBN: " + rs.getString("isbn"));
                System.out.println("Available Copies: " + rs.getInt("available_copies"));
                System.out.println("-----------------------------");
            }
            if (!hasResults) {
                System.out.println("No books found.");
            }
        }
    }

    private static boolean bookExists(Connection conn, int bookId) throws SQLException {
        String query = "SELECT * FROM Books WHERE book_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }
}