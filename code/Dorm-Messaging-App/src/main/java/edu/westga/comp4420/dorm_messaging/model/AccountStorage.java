package edu.westga.comp4420.dorm_messaging.model;

import java.util.Properties;

/**
 * Converts registered accounts to and from storage properties.
 *
 * @author Valai Willie
 */
public class AccountStorage {
	
	private static final String ACCOUNT_PREFIX = "account.";
	private static final String PROPERTY_SEPARATOR = ".";

    /**
     * Writes accounts into the supplied properties.
     *
     * @param registry the accounts to save
     * @param data the properties receiving the account records
     * @throws IllegalArgumentException if either argument is null
     */
    public void saveAccounts(AccountRegistry registry, Properties data) {
        if (registry == null || data == null) {
            throw new IllegalArgumentException(
                "Registry and storage data are required.");
        }

        int index = 0;

        for (Account account : registry.getAccounts()) {
        	String prefix = ACCOUNT_PREFIX + index + PROPERTY_SEPARATOR;

            data.setProperty(prefix + "id",
                Long.toString(account.getId()));
            data.setProperty(prefix + "username",
                account.getUsername());
            data.setProperty(prefix + "credential",
                account.getEncodedCredential());

            index++;
        }

        data.setProperty("account.count", Integer.toString(index));
    }

    /**
     * Restores accounts into a new registry.
     *
     * @param data the saved account properties
     * @return the restored registry
     * @throws IllegalArgumentException if data is null or invalid
     */
    public AccountRegistry loadAccounts(Properties data) {
        if (data == null) {
            throw new IllegalArgumentException(
                "Storage data is required.");
        }

        int count = Integer.parseInt(
            data.getProperty("account.count", "0"));

        if (count < 0) {
            throw new IllegalArgumentException(
                "Account count cannot be negative.");
        }

        AccountRegistry registry = new AccountRegistry();

        for (int index = 0; index < count; index++) {
        	String prefix = ACCOUNT_PREFIX + index + PROPERTY_SEPARATOR;

            Account account = Account.restore(
                Long.parseLong(data.getProperty(prefix + "id")),
                data.getProperty(prefix + "username"),
                data.getProperty(prefix + "credential"));

            registry.addRestoredAccount(account);
        }

        return registry;
    }
}