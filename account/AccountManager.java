package com.bank.account;

import com.bank.db.DatabaseConnection;
import com.bank.model.Account;

import java.sql.*;
import java.util.*;
import java.util.Scanner;

public class AccountManager {
    private List<Account> accounts = new ArrayList<>();

    public AccountManager() {
        loadAccountsFromDatabase();
    }

    private void loadAccountsFromDatabase() {
        String sql = "SELECT * FROM accounts";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                accounts.add(new Account(
                        rs.getInt("account_number"),
                        rs.getString("account_holder_name"),
                        rs.getDouble("balance")
                ));
            }
            System.out.println("✅ Loaded " + accounts.size() + " accounts from the database.");
        } catch (SQLException e) {
            System.out.println("❌ Error loading accounts: " + e.getMessage());
        }
    }

    public void createAccount() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account Number: ");
        int accNum = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Initial Balance: ");
        double bal = sc.nextDouble();

        Account acc = new Account(accNum, name, bal);
        accounts.add(acc);

        String sql = "INSERT INTO accounts (account_number, account_holder_name, balance) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accNum);
            stmt.setString(2, name);
            stmt.setDouble(3, bal);
            stmt.executeUpdate();

            System.out.println("✅ Account created and saved to database.");
        } catch (SQLException e) {
            System.out.println("❌ Error saving account to database: " + e.getMessage());
        }

        System.out.println("✅ Account created successfully!");
    }

    public Account findAccount(int accountNumber) {
        return accounts.stream()
                .filter(a -> a.getAccountNumber() == accountNumber)
                .findFirst()
                .orElse(null);
    }

    public void showAllAccounts() {
        accounts.forEach(System.out::println);
    }

    public void deposit() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account Number: ");
        int accNum = sc.nextInt();
        System.out.print("Enter Deposit Amount: ");
        double amount = sc.nextDouble();

        Account acc = findAccount(accNum);
        if (acc != null) {
            acc.setBalance(acc.getBalance() + amount);
            updateBalanceInDatabase(acc);
            System.out.println("✅ Deposit successful. New Balance: " + acc.getBalance());
        } else {
            System.out.println("❌ Account not found.");
        }
    }

    public void withdraw() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account Number: ");
        int accNum = sc.nextInt();
        System.out.print("Enter Withdraw Amount: ");
        double amount = sc.nextDouble();

        Account acc = findAccount(accNum);
        if (acc != null) {
            if (acc.getBalance() >= amount) {
                acc.setBalance(acc.getBalance() - amount);
                updateBalanceInDatabase(acc);
                System.out.println("✅ Withdrawal successful. New Balance: " + acc.getBalance());
            } else {
                System.out.println("❌ Insufficient balance.");
            }
        } else {
            System.out.println("❌ Account not found.");
        }
    }

    private void updateBalanceInDatabase(Account acc) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, acc.getBalance());
            stmt.setInt(2, acc.getAccountNumber());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error updating balance: " + e.getMessage());
        }
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}
