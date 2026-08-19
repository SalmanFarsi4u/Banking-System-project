import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Customer {
    private final String accountId;
    private final String name;
    private final String contactInfo;
    private String password;
    private double balance;
    private final List<Transaction> transactionHistory;

    private static final Random RANDOM = new Random();

    private static String generateAccountId() {
        int randomPart = RANDOM.nextInt(100_000_000);
        return String.format("2020%08d", randomPart);
    }

    public Customer(String name, String contactInfo, String password) {
        this.accountId = generateAccountId();
        this.name = name;
        this.contactInfo = contactInfo;
        this.password = password;
        this.balance = 500.0;
        this.transactionHistory = new ArrayList<>();
    }

    public Customer(String accountId, String name, String contactInfo, String password, double balance) {
        this.accountId = accountId; //loaded from the text file
        this.name = name;
        this.contactInfo = contactInfo;
        this.password = password;
        this.balance = balance;
        this.transactionHistory = new ArrayList<>();
    }

    public Transaction deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        if (amount >= 50000) {
            throw new IllegalArgumentException("Deposit cannot exceed 50000 at a time.");
        }
        balance += amount;
        Transaction txn = new Transaction("deposit", amount, balance);
        transactionHistory.add(txn);
        return txn;
    }

    public Transaction withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount >= 50000) {
            throw new IllegalArgumentException("Withdrawal denied: cannot withdraw more than 50000.");
        }
        if (amount + 500 > balance) {
            throw new IllegalStateException(
                    String.format("Withdrawal denied: amount (%.2f) exceeds available balance (%.2f).", amount, balance - 500)
            );
        }
        balance -= amount;
        Transaction txn = new Transaction("withdrawal", amount, balance);
        transactionHistory.add(txn);
        return txn;
    }

    public void transfer(double amount, Customer recipient) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient account not found.");
        }
        if (recipient.getAccountId().equals(this.accountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }
        if (amount > 5000) {
            throw new IllegalArgumentException("Transfer denied: cannot transfer more than 5000 at a time.");
        }
        if (amount + 500 > balance) {
            throw new IllegalStateException(String.format("Transfer denied: amount (%.2f) exceeds available balance (%.2f).", amount, balance - 500));
        }
        this.balance -= amount;
        Transaction outTxn = new Transaction(
                "transfer_out", amount, this.balance, "to account " + recipient.getAccountId()
        );
        this.transactionHistory.add(outTxn);

        recipient.balance += amount;
        Transaction inTxn = new Transaction(
                "transfer_in", amount, recipient.balance, "from account " + this.accountId
        );
        recipient.transactionHistory.add(inTxn);
    }

    public boolean checkPassword(String attemptedPassword) {
        return this.password.equals(attemptedPassword);
    }

    public boolean verifyContactInfo(String attemptedContactInfo) {
        return this.contactInfo.equalsIgnoreCase(attemptedContactInfo.trim());
    }

    public void resetPassword(String newPassword) {
        this.password = newPassword;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public Transaction getLastTransaction() {
        if (transactionHistory.isEmpty()) return null;
        return transactionHistory.get(transactionHistory.size() - 1) ;
    }

    public void loadHistoryEntry(Transaction transaction) {
        transactionHistory.add(transaction);
    }

    public String getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getPasswordForStorage() {
        return password;
    }

    @Override
    public String toString() {
        return String.format("Customer(%s)", name);
    }
}
