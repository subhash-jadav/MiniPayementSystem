package models;

import java.sql.Timestamp;

public class Transaction {
    private int id;
    private int senderId;
    private int receiverId;
    private double amount;
    private Timestamp timestamp;
    private String status;

    public Transaction(int id, int senderId, int receiverId, double amount, Timestamp timestamp, String status) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    public Transaction(int senderId, int receiverId, double amount, String status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", sender=" + senderId + ", receiver=" + receiverId +
                ", amount=" + amount + ", status=" + status + ", timestamp=" + timestamp + "}";
    }
}
