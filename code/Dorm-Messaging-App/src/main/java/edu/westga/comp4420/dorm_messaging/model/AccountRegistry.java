package edu.westga.comp4420.dorm_messaging.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages resident accounts and their unique usernames.
 *
 * @author Valai Willie
 */
public class AccountRegistry {
	
	private final Map<String, Account> accounts;
	
	/**
	 * Creates an empty registry of resident accounts.
	 */
	public AccountRegistry() {
	    this.accounts = new HashMap<>();
	}
	
	
	/**
	 * 
	 * Registers an account with a unique username.
	 * 
	 * @param id
	 * @param username
	 * @return the newly registered account
	 * @throw  IllegalArgumentException if the account details are invalid
	 * 								or the username is already registered	
	 */
	public Account register(long id, String username) {
		Account account = new Account(id, username);
		String normalizedUsername = account.getUsername();
		
		if (this.accounts.containsKey(normalizedUsername)) {
			throw new  IllegalArgumentException(
		            "That username is already registered.");
		}
		
		this.accounts.put(normalizedUsername, account);
		return account;
	}

}
