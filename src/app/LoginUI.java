package app;

import models.User;
import services.AuthService;
import services.UserService;

import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {
    private AuthService authService;
    private UserService userService;

    public LoginUI() {
        authService = new AuthService();
        userService = new UserService();

        setTitle("Mini Payment System");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center screen

        // Main panel with tabs
        JTabbedPane tabPane = new JTabbedPane();

        // Login Panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);
        JButton loginBtn = new JButton("Login");

        gbc.gridx = 0; gbc.gridy = 0; loginPanel.add(userLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; loginPanel.add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; loginPanel.add(passLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; loginPanel.add(passField, gbc);
        gbc.gridx = 1; gbc.gridy = 2; loginPanel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            User user = authService.login(username, password);
            if (user != null) {
                dispose();
                new Dashboard(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Sign Up Panel
        JPanel signupPanel = new JPanel(new GridBagLayout());
        signupPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(8, 8, 8, 8);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Username:");
        JTextField nameField = new JTextField(15);
        JLabel passLabel2 = new JLabel("Password:");
        JPasswordField passField2 = new JPasswordField(15);
        JLabel typeLabel = new JLabel("Type:");
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"CUSTOMER", "MERCHANT"});
        JLabel roleLabel = new JLabel("Role:");
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"USER"}); // only USER can sign up here
        JButton signupBtn = new JButton("Sign Up");

        gbc2.gridx = 0; gbc2.gridy = 0; signupPanel.add(nameLabel, gbc2);
        gbc2.gridx = 1; gbc2.gridy = 0; signupPanel.add(nameField, gbc2);
        gbc2.gridx = 0; gbc2.gridy = 1; signupPanel.add(passLabel2, gbc2);
        gbc2.gridx = 1; gbc2.gridy = 1; signupPanel.add(passField2, gbc2);
        gbc2.gridx = 0; gbc2.gridy = 2; signupPanel.add(typeLabel, gbc2);
        gbc2.gridx = 1; gbc2.gridy = 2; signupPanel.add(typeBox, gbc2);
        gbc2.gridx = 0; gbc2.gridy = 3; signupPanel.add(roleLabel, gbc2);
        gbc2.gridx = 1; gbc2.gridy = 3; signupPanel.add(roleBox, gbc2);
        gbc2.gridx = 1; gbc2.gridy = 4; signupPanel.add(signupBtn, gbc2);

        signupBtn.addActionListener(e -> {
            String name = nameField.getText();
            String password = new String(passField2.getPassword());
            String type = (String) typeBox.getSelectedItem();
            String role = (String) roleBox.getSelectedItem();

            if (name.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User newUser = new User(name, type, 0.0, role, password);
            userService.createUser(newUser);
            JOptionPane.showMessageDialog(this, "Account created successfully! Please log in.");
            tabPane.setSelectedIndex(0); // go back to Login tab
        });

        // Add panels to tabs
        tabPane.addTab("Login", loginPanel);
        tabPane.addTab("Sign Up", signupPanel);

        add(tabPane);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginUI::new);
    }
}
