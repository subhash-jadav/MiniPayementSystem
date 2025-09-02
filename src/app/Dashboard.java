package app;

import models.User;
import services.UserService;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {
    private final User currentUser;
    private final UserService userService;
    private JTextArea dataArea;
    private JTable userTable;
    private JScrollPane adminScrollPane;

    public Dashboard(User user) {
        this.currentUser = user;
        this.userService = new UserService();

        setTitle("Dashboard - " + user.getRole());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center window
        setLayout(new BorderLayout());

        // Header
        JLabel welcome = new JLabel("Welcome, " + user.getName() + " (" + user.getRole() + ")", JLabel.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 18));
        welcome.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(welcome, BorderLayout.NORTH);

        // Text area for displaying data
        dataArea = new JTextArea();
        dataArea.setEditable(false);
        dataArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(dataArea);
        add(scrollPane, BorderLayout.CENTER);

        // Role-based dashboard
        if (user.getRole().equals("ADMIN")) {
            setupAdminDashboard();
        } else if (user.getRole().equals("EMPLOYEE")) {
            setupEmployeeDashboard();
        } else {
            setupUserDashboard();
        }

        setVisible(true);
    }
private void setupAdminDashboard() {
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

    JButton refreshBtn = new JButton("Refresh Users");
    JButton addEmployeeBtn = new JButton("Add Employee");
    JButton removeUserBtn = new JButton("Remove User");
    JButton logoutBtn = new JButton("Logout");

    refreshBtn.addActionListener(e -> loadAllUsers());
    addEmployeeBtn.addActionListener(e -> addEmployee());
    removeUserBtn.addActionListener(e -> removeUser());
    logoutBtn.addActionListener(e -> {
        dispose();
        new LoginUI();
    });

    buttonPanel.add(refreshBtn);
    buttonPanel.add(addEmployeeBtn);
    buttonPanel.add(removeUserBtn);
    buttonPanel.add(logoutBtn);

    add(buttonPanel, BorderLayout.SOUTH);

    // Create empty table initially
    String[] columns = {"ID", "Name", "Type", "Balance", "Role"};
    userTable = new JTable(new Object[0][5], columns);
    adminScrollPane = new JScrollPane(userTable);

    add(adminScrollPane, BorderLayout.CENTER);

    loadAllUsers(); // load initial data
}


    private void setupEmployeeDashboard() {
        JPanel empPanel = new JPanel(new BorderLayout());

        // Top search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search User");
        searchPanel.add(new JLabel("Search by Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        empPanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Name", "Type", "Balance" };
        var users = userService.getAllUsers();
        var filtered = users.stream().filter(u -> u.getRole().equals("USER")).toList();

        Object[][] data = new Object[filtered.size()][4];
        for (int i = 0; i < filtered.size(); i++) {
            var u = filtered.get(i);
            data[i][0] = u.getId();
            data[i][1] = u.getName();
            data[i][2] = u.getType();
            data[i][3] = u.getBalance();
        }

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        empPanel.add(scrollPane, BorderLayout.CENTER);

        // Search function
        searchBtn.addActionListener(e -> {
            String searchName = searchField.getText().trim().toLowerCase();
            var searched = filtered.stream()
                    .filter(u -> u.getName().toLowerCase().contains(searchName))
                    .toList();

            Object[][] newData = new Object[searched.size()][4];
            for (int i = 0; i < searched.size(); i++) {
                var u = searched.get(i);
                newData[i][0] = u.getId();
                newData[i][1] = u.getName();
                newData[i][2] = u.getType();
                newData[i][3] = u.getBalance();
            }

            table.setModel(new javax.swing.table.DefaultTableModel(newData, columns));
        });

        add(empPanel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel();
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginUI();
        });
        bottomPanel.add(logoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);

    }

    private void setupUserDashboard() {
        JPanel userPanel = new JPanel(new BorderLayout());

        // Balance label
        JLabel balanceLabel = new JLabel("Balance: " + currentUser.getBalance(), JLabel.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userPanel.add(balanceLabel, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton transferBtn = new JButton("Transfer");
        JButton statementBtn = new JButton("Mini Statement");

        buttonPanel.add(depositBtn);
        buttonPanel.add(withdrawBtn);
        buttonPanel.add(transferBtn);
        buttonPanel.add(statementBtn);
        userPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Table for statement
        JTable table = new JTable(new Object[0][0],
                new String[] { "TxnID", "Sender", "Receiver", "Amount", "Time", "Status" });
        JScrollPane scrollPane = new JScrollPane(table);
        userPanel.add(scrollPane, BorderLayout.CENTER);

        // Actions
        depositBtn.addActionListener(e -> {
            String amt = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
            if (amt != null && !amt.isEmpty()) {
                if (userService.deposit(currentUser.getId(), Double.parseDouble(amt))) {
                    JOptionPane.showMessageDialog(this, "✅ Deposit successful!");
                    refreshUserBalance(balanceLabel);
                }
            }
        });

        withdrawBtn.addActionListener(e -> {
            String amt = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
            if (amt != null && !amt.isEmpty()) {
                if (userService.withdraw(currentUser.getId(), Double.parseDouble(amt))) {
                    JOptionPane.showMessageDialog(this, "✅ Withdraw successful!");
                    refreshUserBalance(balanceLabel);
                }
            }
        });

        transferBtn.addActionListener(e -> {
            String receiverId = JOptionPane.showInputDialog(this, "Enter Receiver User ID:");
            String amt = JOptionPane.showInputDialog(this, "Enter amount to transfer:");
            if (receiverId != null && amt != null) {
                if (userService.transfer(currentUser.getId(), Integer.parseInt(receiverId), Double.parseDouble(amt))) {
                    JOptionPane.showMessageDialog(this, "✅ Transfer successful!");
                    refreshUserBalance(balanceLabel);
                }
            }
        });

        statementBtn.addActionListener(e -> {
            var txns = userService.getMiniStatement(currentUser.getId());
            Object[][] data = new Object[txns.size()][6];
            for (int i = 0; i < txns.size(); i++) {
                data[i] = txns.get(i);
            }
            table.setModel(new javax.swing.table.DefaultTableModel(data,
                    new String[] { "TxnID", "Sender", "Receiver", "Amount", "Time", "Status" }));
        });

        add(userPanel, BorderLayout.CENTER);
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginUI();
        });
        buttonPanel.add(logoutBtn);

    }

    private void refreshUserBalance(JLabel balanceLabel) {
        User refreshed = userService.getUserById(currentUser.getId());
        currentUser.setBalance(refreshed.getBalance());
        balanceLabel.setText("Balance: " + currentUser.getBalance());
    }

 private void loadAllUsers() {
    String[] columns = {"ID", "Name", "Type", "Balance", "Role"};

    var users = userService.getAllUsers();
    Object[][] data = new Object[users.size()][5];

    for (int i = 0; i < users.size(); i++) {
        var u = users.get(i);
        data[i][0] = u.getId();
        data[i][1] = u.getName();
        data[i][2] = u.getType();
        data[i][3] = u.getBalance();
        data[i][4] = u.getRole();
    }

    userTable.setModel(new javax.swing.table.DefaultTableModel(data, columns));
}

    private void addEmployee() {
        String empName = JOptionPane.showInputDialog(this, "Enter Employee Name:");
        if (empName == null || empName.trim().isEmpty())
            return;

        String empPass = JOptionPane.showInputDialog(this, "Enter Employee Password:");
        if (empPass == null || empPass.trim().isEmpty())
            return;

        userService.createEmployee(empName, empPass);
        JOptionPane.showMessageDialog(this, "✅ Employee created successfully!");
        loadAllUsers();
    }

    // remove user
    private void removeUser() {
    String idStr = JOptionPane.showInputDialog(this, "Enter User ID to remove:");
    if (idStr != null && !idStr.trim().isEmpty()) {
        try {
            int userId = Integer.parseInt(idStr);

            if (userId == currentUser.getId()) {
                JOptionPane.showMessageDialog(this, "❌ Cannot remove yourself (SuperAdmin).");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete user with ID " + userId + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (userService.deleteUser(userId)) {
                    JOptionPane.showMessageDialog(this, "✅ User removed successfully!");
                    loadAllUsers(); // refresh the table only
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed to remove user. Check ID.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Invalid ID entered.");
        }
    }
}

    // logout
    private void addLogoutButton(JPanel parentPanel) {
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose(); // close dashboard
            new LoginUI(); // reopen login screen
        });
        parentPanel.add(logoutBtn);
    }

}
