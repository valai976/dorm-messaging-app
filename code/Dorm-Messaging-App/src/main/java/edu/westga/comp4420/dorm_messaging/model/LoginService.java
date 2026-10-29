package edu.westga.comp4420.dorm_messaging.model;


/**
 * Manages authentication and the currently signed-in account.
 *
 * @author Valai Willie
 */
public class LoginService {

	
	private final AccountRegistry registry;
    private Account currentAccount;
    
    
    /**
     * Creates a login service with no account signed in.
     *
     * @param registry the registry containing registered accounts
     * @throws IllegalArgumentException if registry is null
     */
    public LoginService(AccountRegistry registry) {
    	if (registry == null) {
    		throw new IllegalArgumentException(
    				"An account registry is required.");
    	}
    	this.registry = registry;
    	this.currentAccount = null;
    }
    /**
     * Signs in an account using its username and password.
     * A failed attempt leaves no account signed in.
     *
     * @param username the resident's username
     * @param password the password to check
     * @return the signed-in account
     * @throws IllegalArgumentException if the credentials are incorrect
     */
    public Account login(String username, String password) {
        this.currentAccount = null;
        Account account = this.registry.findByUsername(username);

        if (account == null || !account.matchesPassword(password)) {
            throw new IllegalArgumentException(
                "Username or password is incorrect.");
        }

        this.currentAccount = account;
        return account;
    }

    /**
     * Ends the current session without deleting the account.
     */
    public void logout() {
        this.currentAccount = null;
    }

    /**
     * Gets the currently signed-in account.
     *
     * @return the signed-in account, or null if nobody is signed in
     */
    public Account getCurrentAccount() {
        return this.currentAccount;
    }
    
}
