package edu.westga.comp4420.dorm_messaging.model;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Registers password-protected accounts with unique, case-sensitive usernames.
 *
 * @author Valai Willie
 */
public class AccountRegistry {
    private final Map<String, Account> accounts;

    /** Creates an empty registry of resident accounts. */
    public AccountRegistry() {
        this.accounts = new HashMap<>();
    }

    /**
     * Registers an account after validating its details and username uniqueness.
     * Failure leaves registered accounts unchanged and does not reserve a username.
     *
     * @param id the positive account identifier supplied by the caller
     * @param username the requested case-sensitive username
     * @param password the nonblank password, preserved exactly
     * @return the newly registered account with its protected password
     * @throws IllegalArgumentException if account details are invalid or the username is taken
     * @throws IllegalStateException if password hashing is unavailable
     */
    public Account register(long id, String username, String password) {
        Account account = new Account(id, username, password);
        String normalizedUsername = account.getUsername();
        if (this.accounts.containsKey(normalizedUsername)) {
            throw new IllegalArgumentException(" That username is already registered.");
        }
        this.addRestoredAccount(account);
        return account;
    }
    
    /**
     * Finds an account by its case-sensitive username.
     *
     * @param username the username to find
     * @return the matching account, or null if no account matches
     */
    public Account findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        return this.accounts.get(username.strip());
    }
    /**
     * Adds an account restored from local storage.
     *
     * @param account the restored account
     * @throws IllegalArgumentException if account is null or its
     *         username or identifier is already registered
     */
    public void addRestoredAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException(
                "An account is required.");
        }

        if (this.accounts.containsKey(account.getUsername())) {
            throw new IllegalArgumentException(
                "That username is already registered.");
        }

        boolean duplicateId = this.accounts.values().stream()
            .anyMatch(existing -> existing.getId() == account.getId());

        if (duplicateId) {
            throw new IllegalArgumentException(
                "That account ID is already registered.");
        }

        this.accounts.put(account.getUsername(), account);
    }
    /**
     * Gets a snapshot of all registered accounts.
     *
     * @return an unmodifiable list of registered accounts
     */
    public List<Account> getAccounts() {
        return List.copyOf(this.accounts.values());
    }
}
