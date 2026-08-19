import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/** @noinspection ALL*/
public class MainGUI extends JFrame {

    private final Bank bank = new Bank();
    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = new JPanel(cards);

    private Customer currentUser;

    private JLabel dashGreeting;
    private JLabel dashBalance;
    private JTextArea historyArea;


    private static final String WELCOME = "WELCOME";
    private static final String LOGIN = "LOGIN";
    private static final String REGISTER = "REGISTER";
    private static final String FORGOT = "FORGOT";
    private static final String DASHBOARD = "DASHBOARD";

    public MainGUI() {
        super("City Trust Bank");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 480);
        setMinimumSize(new Dimension(460, 420));
        setLocationRelativeTo(null);

        cardPanel.add(buildWelcomePanel(), WELCOME);
        cardPanel.add(buildLoginPanel(), LOGIN);
        cardPanel.add(buildRegisterPanel(), REGISTER);
        cardPanel.add(buildForgotPanel(), FORGOT);
        cardPanel.add(buildDashboardPanel(), DASHBOARD);

        add(cardPanel);
        show(WELCOME);
    }

    private void show(String card) {
        cards.show(cardPanel, card);
    }

    private JPanel buildWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = baseGbc();

        JLabel title = new JLabel("City Trust Bank", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        gbc.gridy = 0;
        panel.add(title, gbc);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        JButton forgotBtn = new JButton("Forgot Password");
        JButton exitBtn = new JButton("Exit");

        loginBtn.addActionListener(e -> { clearLoginFields(); show(LOGIN); });
        registerBtn.addActionListener(e -> { clearRegisterFields(); show(REGISTER); });
        forgotBtn.addActionListener(e -> { clearForgotFields(); show(FORGOT); });
        exitBtn.addActionListener(e -> System.exit(0));

        int y = 1;
        for (JButton b : new JButton[]{loginBtn, registerBtn, forgotBtn, exitBtn}) {
            gbc.gridy = y++;
            gbc.insets = new Insets(8, 0, 8, 0);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(b, gbc);
        }
        return panel;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.weightx = 1;
        return gbc;
    }
// Here is the login panel
    private JTextField loginIdField;
    private JPasswordField loginPassField;

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = baseGbc();

        JLabel title = sectionTitle("Login");
        gbc.gridy = 0;
        panel.add(title, gbc);

        loginIdField = new JTextField();
        loginPassField = new JPasswordField();

        addLabeledField(panel, gbc, 1, "Account ID:", loginIdField);
        addLabeledField(panel, gbc, 3, "Password:", loginPassField);

        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("Back");

        loginBtn.addActionListener(e -> doLogin());
        backBtn.addActionListener(e -> show(WELCOME));
        loginPassField.addActionListener(e -> doLogin()); // Enter key submits

        gbc.gridy = 5;
        panel.add(buttonRow(loginBtn, backBtn), gbc);

        return panel;
    }

    private void doLogin() {
        String accountId = loginIdField.getText().trim();
        String password = new String(loginPassField.getPassword());

        if (accountId.isEmpty() || password.isEmpty()) {
            error("Account ID and password are both required.");
            return;
        }

        Customer customer = bank.login(accountId, password);
        if (customer == null) {
            error("Invalid account ID or password.");
            return;
        }

        currentUser = customer;
        refreshDashboard();
        show(DASHBOARD);
    }

    private void clearLoginFields() {
        if (loginIdField != null) loginIdField.setText("");
        if (loginPassField != null) loginPassField.setText("");
    }

    // Register panel
    private JTextField regNameField;
    private JTextField regContactField;
    private JPasswordField regPassField;
    private JPasswordField regConfirmField;

    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = baseGbc();

        gbc.gridy = 0;
        panel.add(sectionTitle("Register"), gbc);

        regNameField = new JTextField();
        regContactField = new JTextField();
        regPassField = new JPasswordField();
        regConfirmField = new JPasswordField();

        addLabeledField(panel, gbc, 1, "Full name:", regNameField);
        addLabeledField(panel, gbc, 3, "Contact info (email/phone):", regContactField);
        addLabeledField(panel, gbc, 5, "Password:", regPassField);
        addLabeledField(panel, gbc, 7, "Confirm password:", regConfirmField);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        registerBtn.addActionListener(e -> doRegister());
        backBtn.addActionListener(e -> show(WELCOME));

        gbc.gridy = 9;
        panel.add(buttonRow(registerBtn, backBtn), gbc);

        return panel;
    }

    private void doRegister() {
        String name = regNameField.getText().trim();
        String contact = regContactField.getText().trim();
        String password = new String(regPassField.getPassword());
        String confirm = new String(regConfirmField.getPassword());

        if (name.isEmpty() || contact.isEmpty() || password.isEmpty()) {
            error("All fields are required.");
            return;
        }
        if (password.length() < 4) {
            error("Password must be at least 4 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            error("Passwords do not match.");
            return;
        }

        Customer customer = new Customer(name, contact, password);
        bank.registerCustomer(customer);

        clearRegisterFields();
        JOptionPane.showMessageDialog(this,
                "Registration successful!\n\nYour account ID is: " + customer.getAccountId()
                        + "\nSave this - you'll need it to log in.\nStarting balance: 500.00",
                "Registered", JOptionPane.INFORMATION_MESSAGE);
        show(WELCOME);
    }

    private void clearRegisterFields() {
        if (regNameField != null) regNameField.setText("");
        if (regContactField != null) regContactField.setText("");
        if (regPassField != null) regPassField.setText("");
        if (regConfirmField != null) regConfirmField.setText("");
    }

  // Forgot password
    private JTextField forgotIdField;
    private JTextField forgotContactField;
    private JPasswordField forgotNewPassField;
    private JPasswordField forgotConfirmField;

    private JPanel buildForgotPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = baseGbc();

        gbc.gridy = 0;
        panel.add(sectionTitle("Forgot Password"), gbc);

        forgotIdField = new JTextField();
        forgotContactField = new JTextField();
        forgotNewPassField = new JPasswordField();
        forgotConfirmField = new JPasswordField();

        addLabeledField(panel, gbc, 1, "Account ID:", forgotIdField);
        addLabeledField(panel, gbc, 3, "Contact info used at registration:", forgotContactField);
        addLabeledField(panel, gbc, 5, "New password:", forgotNewPassField);
        addLabeledField(panel, gbc, 7, "Confirm new password:", forgotConfirmField);

        JButton resetBtn = new JButton("Reset Password");
        JButton backBtn = new JButton("Back");

        resetBtn.addActionListener(e -> doForgotPassword());
        backBtn.addActionListener(e -> show(WELCOME));

        gbc.gridy = 9;
        panel.add(buttonRow(resetBtn, backBtn), gbc);

        return panel;
    }

    private void doForgotPassword() {
        String accountId = forgotIdField.getText().trim();
        String contact = forgotContactField.getText().trim();
        String newPassword = new String(forgotNewPassField.getPassword());
        String confirm = new String(forgotConfirmField.getPassword());

        if (accountId.isEmpty() || contact.isEmpty() || newPassword.isEmpty()) {
            error("All fields are required.");
            return;
        }
        if (newPassword.length() < 4) {
            error("Password must be at least 4 characters.");
            return;
        }
        if (!newPassword.equals(confirm)) {
            error("Passwords do not match.");
            return;
        }

        boolean success = bank.resetPassword(accountId, contact, newPassword);
        if (success) {
            clearForgotFields();
            JOptionPane.showMessageDialog(this,
                    "Password reset successful. You can now log in with your new password.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            show(WELCOME);
        } else {
            error("Could not verify your identity. Check your account ID and contact info.");
        }
    }

    private void clearForgotFields() {
        if (forgotIdField != null) forgotIdField.setText("");
        if (forgotContactField != null) forgotContactField.setText("");
        if (forgotNewPassField != null) forgotNewPassField.setText("");
        if (forgotConfirmField != null) forgotConfirmField.setText("");
    }

//Dashboard
    private JPanel buildDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel top = new JPanel(new GridLayout(2, 1));
        dashGreeting = new JLabel("Welcome!");
        dashGreeting.setFont(dashGreeting.getFont().deriveFont(Font.BOLD, 18f));
        dashBalance = new JLabel("Balance: 0.00");
        dashBalance.setFont(dashBalance.getFont().deriveFont(16f));
        top.add(dashGreeting);
        top.add(dashBalance);
        panel.add(top, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 8));
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton transferBtn = new JButton("Transfer");
        JButton logoutBtn = new JButton("Logout");

        depositBtn.addActionListener(e -> doDeposit());
        withdrawBtn.addActionListener(e -> doWithdraw());
        transferBtn.addActionListener(e -> doTransfer());
        logoutBtn.addActionListener(e -> doLogout());

        for (JButton b : new JButton[]{depositBtn, withdrawBtn, transferBtn, logoutBtn}) {
            buttons.add(b);
        }

        JPanel west = new JPanel(new BorderLayout());
        west.add(buttons, BorderLayout.NORTH);
        panel.add(west, BorderLayout.WEST);

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(historyArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Transaction History"));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void refreshDashboard() {
        dashGreeting.setText("Welcome, " + currentUser.getName() + "  (ID: " + currentUser.getAccountId() + ")");
        dashBalance.setText(String.format("Balance: %.2f", currentUser.getBalance()));
        refreshHistory();
    }

    private void refreshHistory() {
        List<Transaction> history = currentUser.getHistory();
        if (history.isEmpty()) {
            historyArea.setText("No transactions yet.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Transaction t : history) {
            sb.append(t.getDetails()).append("\n");
        }
        historyArea.setText(sb.toString());
        historyArea.setCaretPosition(0);
    }

    private void doDeposit() {
        String input = JOptionPane.showInputDialog(this, "Amount to deposit:", "Deposit", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return; // user cancelled
        Double amount = parseAmount(input);
        if (amount == null) return;

        try {
            Transaction txn = currentUser.deposit(amount);
            bank.persistAccounts();
            bank.persistTransaction(currentUser);
            refreshDashboard();
            JOptionPane.showMessageDialog(this, "Deposit successful.\n" + txn.getDetails(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            error("Deposit failed: " + e.getMessage());
        }
    }

    private void doWithdraw() {
        String input = JOptionPane.showInputDialog(this, "Amount to withdraw:", "Withdraw", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;
        Double amount = parseAmount(input);
        if (amount == null) return;

        try {
            Transaction txn = currentUser.withdraw(amount);
            bank.persistAccounts();
            bank.persistTransaction(currentUser);
            refreshDashboard();
            JOptionPane.showMessageDialog(this, "Withdrawal successful.\n" + txn.getDetails(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException e) {
            error("Withdrawal failed: " + e.getMessage());
        }
    }

    private void doTransfer() {
        JTextField idField = new JTextField();
        JTextField amountField = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
        form.add(new JLabel("Recipient account ID:"));
        form.add(idField);
        form.add(new JLabel("Amount to transfer:"));
        form.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, form, "Transfer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String targetAccountId = idField.getText().trim();
        if (targetAccountId.isEmpty()) {
            error("Recipient account ID is required.");
            return;
        }

        Double amount = parseAmount(amountField.getText());
        if (amount == null) return;

        Customer recipient = bank.findCustomer(targetAccountId);
        if (recipient == null) {
            error("Transfer failed: recipient account not found.");
            return;
        }

        try {
            currentUser.transfer(amount, recipient);
            bank.persistAccounts();
            bank.persistTransaction(currentUser);
            bank.persistTransaction(recipient);
            refreshDashboard();
            JOptionPane.showMessageDialog(this,
                    String.format("Transfer successful.%nNew balance: %.2f", currentUser.getBalance()),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException e) {
            error("Transfer failed: " + e.getMessage());
        }
    }

    private void doLogout() {
        currentUser = null;
        historyArea.setText("");
        show(WELCOME);
    }

    private Double parseAmount(String raw) {
        if (raw == null) return null;
        raw = raw.trim();
        if (raw.isEmpty()) {
            error("Amount is required.");
            return null;
        }
        try {
            double value = Double.parseDouble(raw);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                error("Invalid amount entered.");
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            error("Invalid amount entered.");
            return null;
        }
    }


    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 20f));
        return label;
    }

    private void addLabeledField(JPanel panel, GridBagConstraints gbc, int startY, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        gbc.gridy = startY;
        gbc.insets = new Insets(10, 0, 2, 0);
        panel.add(label, gbc);

        gbc.gridy = startY + 1;
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(field, gbc);
    }

    private JPanel buttonRow(JButton... buttons) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        for (JButton b : buttons) row.add(b);
        return row;
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainGUI().setVisible(true);
        });
    }
}