import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Bank bank = new Bank("City Trust Bank");

    public static void main(String[] args) {
        System.out.println("=== Welcome to " + bank.getName() + " ===");

        boolean running = true;
        while (running) {
            System.out.println("\n1. Register");
            System.out.println("2. Login");
            System.out.println("3. Forgot Password");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleRegister();
                    break;
                case "2":
                    handleLogin();
                    break;
                case "3":
                    handleForgotPassword();
                    break;
                case "4":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private static void handleRegister() {
        System.out.println("\n--- Register ---");
        System.out.print("Full name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Contact info (email/phone): ");
        String contact = scanner.nextLine().trim();

        System.out.print("Choose a password: ");
        String password = scanner.nextLine().trim();

        Customer customer = new Customer(name, contact, password);
        bank.registerCustomer(customer);   // saves to file internally
        System.out.println("Registration successful!");
        System.out.println("Your account ID is: " + customer.getAccountId());
    }
    private static void handleLogin() {
        System.out.println("\n--- Login ---");
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        Customer customer = bank.login(accountId, password);
        if (customer == null) {
            System.out.println("Invalid account ID or password.");
            return;
        }

        System.out.println("Login successful. Welcome, " + customer.getName() + "!");
        showTransactionMenu(customer);
    }
    private static void handleForgotPassword() {
        System.out.println("\n--- Forgot Password ---");
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();

        System.out.print("Confirm the contact info used at registration: ");
        String contact = scanner.nextLine().trim();

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine().trim();

        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Please try again.");
            return;
        }

        boolean success = bank.resetPassword(accountId, contact, newPassword);
        if (success) {
            System.out.println("Password reset successful. You can now log in with your new password.");
        } else {
            System.out.println("Could not verify your identity. Check your account ID and contact info.");
        }
    }
    private static void showTransactionMenu(Customer customer) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- Account Menu (" + customer.getName() + ") ---");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. View Balance");
            System.out.println("5. View Transaction History");
            System.out.println("6. Logout");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleDeposit(customer);
                    break;
                case "2":
                    handleWithdraw(customer);
                    break;
                case "3":
                    handleTransfer(customer);
                    break;
                case "4":
                    handleViewBalance(customer);
                    break;
                case "5":
                    handleViewHistory(customer);
                    break;
                case "6":
                    loggedIn = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void handleDeposit(Customer customer) {
        System.out.print("Enter amount to deposit: ");
        double amount = readAmount();
        if (Double.isNaN(amount)) return;

        try {
            Transaction txn = customer.deposit(amount);
            bank.persistAccounts();
            bank.persistTransaction(customer);
            System.out.println("Deposit successful.");
            System.out.println(txn.getDetails());
        } catch (IllegalArgumentException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    private static void handleWithdraw(Customer customer) {
        System.out.print("Enter amount to withdraw: ");
        double amount = readAmount();
        if (Double.isNaN(amount)) return;

        try {
            Transaction txn = customer.withdraw(amount);
            bank.persistAccounts();
            bank.persistTransaction(customer);
            System.out.println("Withdrawal successful.");
            System.out.println(txn.getDetails());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }

    private static void handleTransfer(Customer customer) {
        System.out.print("Enter recipient's account ID: ");
        String targetAccountId = scanner.nextLine().trim();

        System.out.print("Enter amount to transfer: ");
        double amount = readAmount();
        if (Double.isNaN(amount)) return;

        Customer recipient = bank.findCustomer(targetAccountId);
        if (recipient == null) {
            System.out.println("Transfer failed: recipient account not found.");
            return;
        }

        try {
            customer.transfer(amount, recipient);
            bank.persistAccounts();
            bank.persistTransaction(customer);
            bank.persistTransaction(recipient);
            System.out.println("Transfer successful.");
            System.out.printf("New balance: %.2f%n", customer.getBalance());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    private static void handleViewBalance(Customer customer) {
        System.out.printf("Current balance: %.2f%n", customer.getBalance());
    }

    private static void handleViewHistory(Customer customer) {
        List<Transaction> history = customer.getHistory();
        if (history.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        System.out.println("Transaction history:");
        for (Transaction txn : history) {
            System.out.println("  " + txn);
        }
    }
    private static double readAmount() {
        String input = scanner.nextLine().trim();
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
            return Double.NaN;
        }
    }
}
