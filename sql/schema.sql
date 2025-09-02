CREATE DATABASE payment_system;
USE payment_system;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type ENUM('CUSTOMER', 'MERCHANT') NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0
);

CREATE TABLE transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sender_id INT,
    receiver_id INT,
    amount DECIMAL(10,2),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('SUCCESS', 'FAILED'),
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id)
);
-- ALTER TABLE users ADD COLUMN role ENUM('ADMIN', 'EMPLOYEE', 'USER') NOT NULL DEFAULT 'USER';
-- ALTER TABLE users ADD COLUMN password VARCHAR(255) NOT NULL;
