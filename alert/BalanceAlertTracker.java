package com.bank.alert;

import com.bank.account.AccountManager;
import com.bank.model.Account;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class BalanceAlertTracker {
    private final AccountManager accountManager;
    private final EmailService emailService;
    private final double threshold;
    private final long intervalMs;
    private Timer timer;

    public BalanceAlertTracker(AccountManager accountManager) {
        this.accountManager = accountManager;
        this.emailService = new EmailService();

        double th = 1000.0;
        long intervalSeconds = 60;
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            Properties p = new Properties();
            p.load(fis);
            th = Double.parseDouble(p.getProperty("balance.alert.threshold", "1000"));
            intervalSeconds = Long.parseLong(p.getProperty("balance.alert.interval.seconds", "60"));
        } catch (Exception ignored) {}

        this.threshold = th;
        this.intervalMs = intervalSeconds * 1000L;
    }

    public void start() {
        if (timer != null) return;
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkBalances();
            }
        }, 0, intervalMs);
        System.out.println("✅ BalanceAlertTracker started (threshold=" + threshold + ", intervalMs=" + intervalMs + ")");
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            System.out.println("🛑 BalanceAlertTracker stopped");
        }
    }

    private void checkBalances() {
        List<Account> accounts = accountManager.getAllAccounts();
        for (Account acc : accounts) {
            if (acc.getBalance() < threshold) {
                System.out.println("⚠️ Low balance for account " + acc.getAccountNumber() + " : " + acc.getBalance());
                emailService.sendLowBalanceAlert(acc, threshold);
            }
        }
    }
}
