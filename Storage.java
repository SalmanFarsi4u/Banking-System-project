import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Storage {
    private static final String ACCOUNTS_FILE = "accounts.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SEP = "|";

    public static Map<String, Customer> loadAccounts() {
        Map<String, Customer> customers = new HashMap<>();
        File file = new File(ACCOUNTS_FILE);
        if (!file.exists()) {
            return customers;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\" + SEP, -1);
                if (parts.length < 5) continue;

                String accountId = parts[0];
                String name = parts[1];
                String contactInfo = parts[2];
                String password = parts[3];
                double balance = Double.parseDouble(parts[4]);

                Customer customer = new Customer(accountId, name, contactInfo, password, balance);
                customers.put(accountId, customer);
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read " + ACCOUNTS_FILE + " (" + e.getMessage() + ")");
        }
        return customers;
    }

    public static void saveAllAccounts(Collection<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (Customer c : customers) {
                writer.write(String.join(SEP,
                        c.getAccountId(),
                        c.getName(),
                        c.getContactInfo(),
                        c.getPasswordForStorage(),
                        String.valueOf(c.getBalance())
                ));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Warning: could not save " + ACCOUNTS_FILE + " (" + e.getMessage() + ")");
        }
    }

    public static void appendTransaction(String accountId, Transaction txn) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            writer.write(String.join(SEP,
                    accountId,
                    txn.getType(),
                    String.valueOf(txn.getAmount()),
                    String.valueOf(txn.getResultingBalance()),
                    txn.getTimestamp().format(TS_FORMAT)
            ));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Warning: could not save transaction (" + e.getMessage() + ")");
        }
    }

    public static void loadTransactions(Map<String, Customer> customers) {
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\" + SEP, -1);
                if (parts.length < 5) continue;

                String accountId = parts[0];
                String type = parts[1];
                double amount = Double.parseDouble(parts[2]);
                double resultingBalance = Double.parseDouble(parts[3]);
                LocalDateTime timestamp = LocalDateTime.parse(parts[4], TS_FORMAT);

                Customer customer = customers.get(accountId);
                if (customer != null) {
                    Transaction txn = Transaction.fromRecord(type, amount, resultingBalance, timestamp);
                    customer.loadHistoryEntry(txn);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read " + TRANSACTIONS_FILE + " (" + e.getMessage() + ")");
        }
    }
}