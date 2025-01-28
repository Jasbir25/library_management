package U3_EP1;

import java.sql.*;
import java.util.Scanner;

/**
 * Library Management System
 * 
 * This system manages books, members, and transactions in a library.
 * 
 */
public class Library_Management_System {

    public static void main(String[] args) {
        try {
            String url = "jdbc:sqlite:D:/PES/3rd Sem/Java/Codes/netbeans/test/test.db";
            Connection conn = DriverManager.getConnection(url);
            Scanner sc = new Scanner(System.in);
            String choice;

            do {
                System.out.println("\n--- Library Management System ---");
                System.out.println("1. Book Management");
                System.out.println("2. Member Management");
                System.out.println("3. Transaction Management");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                choice = sc.nextLine();

                switch (choice) {
                    case "1":
                        BMS.bookManagement(conn, sc);
                        break;
                    case "2":
                        MMS.memberManagement(conn, sc);
                        break;
                    case "3":
                        TMS.transactionManagement(conn, sc);
                        break;
                    case "4":
                        System.out.println("Exiting Library Management System...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } while (!choice.equals("4"));

            conn.close();
        } catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}