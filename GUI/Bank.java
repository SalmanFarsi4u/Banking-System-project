import java.util.HashMap;
import java.util.Map;

public class Bank {
    private final Map<String, Customer> customers;

    public Bank() {
        this.customers = new HashMap<>(Storage.loadAccounts());
        Storage.loadTransactions(this.customers);
    }

    public void registerCustomer(Customer customer) {
        customers.put(customer.getAccountId(), customer);
        persistAccounts();
    }

    public Customer login(String accountId, String password) {
        Customer customer = customers.get(accountId);
        if (customer == null || !customer.checkPassword(password)) {
            return null;
        }
        return customer;
    }

    public boolean resetPassword(String accountId, String contactInfo, String newPassword) {
        Customer customer = customers.get(accountId);
        if (customer == null || !customer.verifyContactInfo(contactInfo)) {
            return false;
        }
        customer.resetPassword(newPassword);
        persistAccounts();
        return true;
    }

    public Customer findCustomer(String accountId) {
        return customers.get(accountId);
    }

    public void persistAccounts() {
        Storage.saveAllAccounts(customers.values());
    }

    public void persistTransaction(Customer customer) {
        Transaction latest = customer.getLastTransaction();
        if (latest != null) {
            Storage.appendTransaction(customer.getAccountId(), latest);
        }
    }
}