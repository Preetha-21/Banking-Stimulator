package com.bank.transaction;

import com.bank.account.AccountManager;
import com.bank.dao.TransactionDao;
import com.bank.model.Account;
import com.bank.model.Transaction;

import java.util.List;
import java.util.Scanner;

public class TransactionManager {
    private final AccountManager accountManager;
    private final TransactionDao transactionDao = new TransactionDao();

    public TransactionManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public void transfer() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter From Account Number: ");
        int fromAcc = sc.nextInt();
        System.out.print("Enter To Account Number: ");
        int toAcc = sc.nextInt();
        System.out.print("Enter Amount to Transfer: ");
        double amount = sc.nextDouble();

        Account from = accountManager.findAccount(fromAcc);
        Account to = accountManager.findAccount(toAcc);

        if (from == null || to == null) {
            System.out.println("❌ One of the accounts does not exist!");
            return;
        }

        if (from.getBalance() < amount) {
            System.out.println("❌ Insufficient balance!");
            return;
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        // Update both in DB
        accountManager.getAccounts().forEach(acc -> {
            String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        });

        transactionDao.saveTransaction(new Transaction(fromAcc, "TRANSFER_OUT", amount));
        transactionDao.saveTransaction(new Transaction(toAcc, "TRANSFER_IN", amount));

        System.out.println("✅ Transfer successful!");
        System.out.println("From Account New Balance: " + from.getBalance());
        System.out.println("To Account New Balance: " + to.getBalance());
    }

    public void showAllTransactions() {
        List<Transaction> txns = transactionDao.getAllTransactions();
        if (txns.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            txns.forEach(System.out::println);
        }
    }
}
