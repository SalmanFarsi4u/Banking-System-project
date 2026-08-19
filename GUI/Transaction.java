import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction { //similar to reciept in actual bank
    private final String type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final double resultingBalance;
    private final String note; //transfer to or transfer from

    public Transaction(String type, double amount, double resultingBalance) {
        this(type, amount, resultingBalance, "", LocalDateTime.now());
     }
     // constructor overloading
    public Transaction(String type, double amount, double resultingBalance, String note) {
        this(type, amount, resultingBalance, note, LocalDateTime.now()); // using note when transfarred to or from any account
    }

    public static Transaction fromRecord(String type, double amount, double resultingBalance,
                                          String note, LocalDateTime timestamp) {
        return new Transaction(type, amount, resultingBalance, note, timestamp);
    }

    private Transaction(String type, double amount, double resultingBalance, String note, LocalDateTime timestamp) {
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.resultingBalance = resultingBalance;
        this.note = note;
    }

    public String getDetails() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String base = String.format(
                "%-13s amount=%10.2f  balance_after=%10.2f  at=%s",
                type.toUpperCase(), amount, resultingBalance, timestamp.format(fmt)
        );
        if (note != null && !note.isEmpty()) {
            base += "  (" + note + ")";
        }
        return base;
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public double getResultingBalance() { return resultingBalance; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getNote() { return note; }

    @Override
    public String toString() {
        return getDetails();
    }
}
