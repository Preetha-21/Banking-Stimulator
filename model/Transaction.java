package com.bank.model;

import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private int accountNumber;
    private String type;
    private double amount;
    private LocalDateTime timestamp;

    public Transaction() {}

    public Transaction(int accountNumber, String type, double amount) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(int id, int accountNumber, String type, double amount, LocalDateTime timestamp) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return id + " | " + accountNumber + " | " + type + " | " + amount + " | " + timestamp;
    }
}
