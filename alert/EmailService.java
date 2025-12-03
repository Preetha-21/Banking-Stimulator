package com.bank.alert;

import com.bank.model.Account;
import com.bank.model.Transaction;

import java.io.FileInputStream;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {
    private final Properties config = new Properties();
    private Session session;
    private boolean configured = false;

    public EmailService() {
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            config.load(fis);
            String host = config.getProperty("mail.smtp.host");
            if (host == null || host.isBlank()) { configured = false; return; }

            Properties props = new Properties();
            props.put("mail.smtp.host", config.getProperty("mail.smtp.host"));
            props.put("mail.smtp.port", config.getProperty("mail.smtp.port", "587"));
            props.put("mail.smtp.auth", config.getProperty("mail.smtp.auth", "true"));
            props.put("mail.smtp.starttls.enable", config.getProperty("mail.smtp.starttls.enable", "true"));

            final String user = config.getProperty("mail.username");
            final String pass = config.getProperty("mail.password");
            session = Session.getInstance(props, new jakarta.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass);
                }
            });
            configured = true;
            System.out.println("✅ EmailService configured.");
        } catch (Exception e) {
            configured = false;
            System.out.println("⚠️ EmailService not configured (config.properties missing or invalid). Alerts printed to console.");
        }
    }

    public void sendTransactionEmail(Account acc, Transaction tx) {
        String to = config.getProperty("alert.email.to");
        if (to == null || to.isBlank()) to = System.getProperty("alert.email.to", "admin@example.com");

        String subject = "Transaction Alert - Account " + acc.getAccountNumber();
        String body = String.format("Account: %d (%s)\nType: %s\nAmount: %.2f\nBalance: %.2f\nTime: %s",
                acc.getAccountNumber(), acc.getAccountHolderName(), tx.getType(), tx.getAmount(), acc.getBalance(), tx.getTimestamp());

        sendEmail(to, subject, body);
    }

    public void sendLowBalanceAlert(Account acc, double threshold) {
        String to = config.getProperty("alert.email.to");
        if (to == null || to.isBlank()) to = System.getProperty("alert.email.to", "admin@example.com");

        String subject = "Low Balance Alert - Account " + acc.getAccountNumber();
        String body = String.format("Account %d balance %.2f is below threshold %.2f", acc.getAccountNumber(), acc.getBalance(), threshold);
        sendEmail(to, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        if (!configured) {
            System.out.println("ALERT (console): " + subject + " -> " + body);
            return;
        }
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getProperty("mail.from")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("📧 Email sent to " + to + " (" + subject + ")");
        } catch (Exception e) {
            System.out.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
