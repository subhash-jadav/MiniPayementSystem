package app;

import java.sql.Connection;
import db.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Main App: Database connection established!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
