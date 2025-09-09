# 🏦 Mini Payment System (Java + MySQL + Swing)

A **role-based fintech simulation project** built in **Java** with **MySQL database** and a **Swing-based GUI**.  
The system demonstrates **OOP concepts**, **JDBC integration**, and **role-based dashboards** for Admin, Employee, and End Users.


## 🚀 Features

### 🔑 Authentication
- Login with username + password
- Sign Up option for new users (creates a USER account)
- Role-based redirection to dashboards

### 👤 User Dashboard
- Deposit money
- Withdraw money
- Transfer money to another user by ID
- View Mini Statement (last 5 transactions)

### 👔 Employee Dashboard
- View all **User (Customer)** accounts
- Search users by name
- (Future scope: view transactions of specific customers)

### 🛠️ Admin (SuperAdmin) Dashboard
- View all system users in a clean table
- Add Employee accounts
- Remove Users (except themselves)
- Refresh User list
- Logout

---

## 🏗️ Tech Stack
- **Java 17**
- **MySQL 8**
- **Swing** (UI framework)
- **JDBC (MySQL Connector/J)** for DB connectivity

---

## Project Structure 
│
├── bin
│
├── lib
│   └── mysql-connector-j-9.4...  (MySQL Connector Java Library)
│
├── sql
│   └── schema.sql                (Database schema definitions)
│
├── src
│   ├── app
│   │   ├── Dashboard.java       (Dashboard UI and logic)
│   │   ├── LoginUI.java         (Login user interface)
│   │   └── Main.java            (Main application entry point)
│   │
│   ├── db
│   │   └── DatabaseConnection.java  (Database connection setup)
│   │
│   ├── models
│   │   ├── Transaction.java     (Transaction data model)
│   │   └── User.java            (User data model)
│   │
│   └── services
│       ├── AuthService.java     (Authentication services)
│       └── UserService.java     (User-related business logic)

