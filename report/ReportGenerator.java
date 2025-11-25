package com.bank.report;

import com.bank.model.Transaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportGenerator {

    private static final String FILE_PATH = "src/main/resources/data/transactions.txt";

    public ReportGenerator() {
        try {
            File file = new File(FILE_PATH);

            // Create directory if not exists
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
                System.out.println("📁 Created directory: " + parentDir.getPath());
            }

            // Create file if not exists
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("📄 Created file: " + file.getPath());
            }

        } catch (IOException e) {
            System.out.println("❌ Error initializing report file: " + e.getMessage());
        }
    }

    public void generateReport(List<Transaction> transactions) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true)) {

            fw.write("------ Transaction Report (" + java.time.LocalDateTime.now() + ") ------\n");
            for (Transaction t : transactions) {
                fw.write(t.toString() + "\n");
            }
            fw.write("------------------------------------------------------------\n\n");

            System.out.println("✅ Report written to: " + FILE_PATH);

        } catch (IOException e) {
            System.out.println("❌ Error writing report: " + e.getMessage());
        }
    }
}
