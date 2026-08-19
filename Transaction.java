import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final String type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final double resultingBalance;

    public Transaction(String type, double amount, double resultingBalance) {
        this(type, amount, resultingBalance, LocalDateTime.now());
    }

    public static Transaction fromRecord(String type, double amount, double resultingBalance,
                                          LocalDateTime timestamp) {
        return new Transaction(type, amount, resultingBalance, timestamp);
    }

    private Transaction(String type, double amount, double resultingBalance, LocalDateTime timestamp) {
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.resultingBalance = resultingBalance;
    }

    public String getDetails() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format(
                "%-13s amount=%10.2f  balance_after=%10.2f  at=%s",
                type.toUpperCase(), amount, resultingBalance, timestamp.format(fmt)
        );
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public double getResultingBalance() { return resultingBalance; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return getDetails();
    }
}
