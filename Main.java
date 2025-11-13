package com.bank;

import com.bank.account.AccountManager;
import com.bank.transaction.TransactionManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        AccountManager accountManager = new AccountManager();
        TransactionManager transactionManager = new TransactionManager(accountManager);

        while (true) {
            System.out.println("\n====== BANKING SIMULATOR ======");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Show All Accounts");
            System.out.println("6. Show All Transactions");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> accountManager.createAccount();
                case 2 -> accountManager.deposit();
                case 3 -> accountManager.withdraw();
                case 4 -> transactionManager.transfer();
                case 5 -> accountManager.showAllAccounts();
                case 6 -> transactionManager.showAllTransactions();
                case 7 -> {
                    System.out.println("👋 Exiting Banking Simulator. Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Try again!");
            }
        }
    }
}
