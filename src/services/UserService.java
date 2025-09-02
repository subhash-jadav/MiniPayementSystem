package services;

import db.DatabaseConnection;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class UserService {

    // Create a new user in the DB
    public void createUser(User user) {
        String query = "INSERT INTO users (name, type, balance, role, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getType());
            stmt.setDouble(3, user.getBalance());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getPassword());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
            System.out.println("✅ User created: " + user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get one user by ID
    public User getUserById(int id) {
        String query = "SELECT * FROM users WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getDouble("balance"),
                        rs.getString("role"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getDouble("balance"),
                        rs.getString("role"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    // 
    public void createEmployee(String name, String password) {
    String query = "INSERT INTO users (name, type, balance, role, password) VALUES (?, 'CUSTOMER', 0.0, 'EMPLOYEE', ?)";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.executeUpdate();
        System.out.println("✅ Employee created: " + name);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
// 
// Add in UserService.java
public boolean deposit(int userId, double amount) {
    String update = "UPDATE users SET balance = balance + ? WHERE id = ?";
    String insertTxn = "INSERT INTO transactions (sender_id, receiver_id, amount, status) VALUES (NULL, ?, ?, 'SUCCESS')";
    try (Connection conn = DatabaseConnection.getConnection()) {
        conn.setAutoCommit(false);

        try (PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement(insertTxn)) {
            stmt.setInt(1, userId);
            stmt.setDouble(2, amount);
            stmt.executeUpdate();
        }

        conn.commit();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

public boolean withdraw(int userId, double amount) {
    String checkBalance = "SELECT balance FROM users WHERE id=?";
    String update = "UPDATE users SET balance = balance - ? WHERE id = ?";
    String insertTxn = "INSERT INTO transactions (sender_id, receiver_id, amount, status) VALUES (?, NULL, ?, 'SUCCESS')";
    try (Connection conn = DatabaseConnection.getConnection()) {
        conn.setAutoCommit(false);

        // Check balance
        try (PreparedStatement stmt = conn.prepareStatement(checkBalance)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getDouble("balance") < amount) {
                JOptionPane.showMessageDialog(null, "❌ Insufficient balance");
                return false;
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement(insertTxn)) {
            stmt.setInt(1, userId);
            stmt.setDouble(2, amount);
            stmt.executeUpdate();
        }

        conn.commit();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

public boolean transfer(int senderId, int receiverId, double amount) {
    String checkBalance = "SELECT balance FROM users WHERE id=?";
    String deduct = "UPDATE users SET balance = balance - ? WHERE id=?";
    String add = "UPDATE users SET balance = balance + ? WHERE id=?";
    String insertTxn = "INSERT INTO transactions (sender_id, receiver_id, amount, status) VALUES (?, ?, ?, 'SUCCESS')";
    try (Connection conn = DatabaseConnection.getConnection()) {
        conn.setAutoCommit(false);

        // Check sender balance
        try (PreparedStatement stmt = conn.prepareStatement(checkBalance)) {
            stmt.setInt(1, senderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getDouble("balance") < amount) {
                JOptionPane.showMessageDialog(null, "❌ Insufficient balance");
                return false;
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement(deduct)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, senderId);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement(add)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, receiverId);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement(insertTxn)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setDouble(3, amount);
            stmt.executeUpdate();
        }

        conn.commit();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

public List<String[]> getMiniStatement(int userId) {
    List<String[]> txns = new ArrayList<>();
    String query = "SELECT * FROM transactions WHERE sender_id=? OR receiver_id=? ORDER BY timestamp DESC LIMIT 5";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, userId);
        stmt.setInt(2, userId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            txns.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    String.valueOf(rs.getInt("sender_id")),
                    String.valueOf(rs.getInt("receiver_id")),
                    rs.getDouble("amount") + "",
                    rs.getString("timestamp"),
                    rs.getString("status")
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return txns;
}
public boolean deleteUser(int userId) {
    String query = "DELETE FROM users WHERE id=?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, userId);
        int rows = stmt.executeUpdate();
        return rows > 0;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

}
