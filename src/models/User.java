package models;

public class User {
    private int id;
    private String name;
    private String type;   // CUSTOMER / MERCHANT
    private double balance;
    private String role;   // ADMIN / EMPLOYEE / USER
    private String password;

    public User(int id, String name, String type, double balance, String role, String password) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.role = role;
        this.password = password;
    }

    public User(String name, String type, double balance, String role, String password) {
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.role = role;
        this.password = password;
    }

    // Getters & Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getBalance() { return balance; }
    public String getRole() { return role; }
    public String getPassword() { return password; }

    public void setId(int id) { this.id = id; }
    public void setBalance(double balance) { this.balance = balance; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', type='" + type + "', balance=" + balance + ", role=" + role + "}";
    }
}
