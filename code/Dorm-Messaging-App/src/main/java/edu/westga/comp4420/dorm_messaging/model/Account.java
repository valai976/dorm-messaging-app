/**
 * Provides account and messaging models for the dorm messaging application.
 */
package edu.westga.comp4420.dorm_messaging.model;

public class Account {
	private long id;
	private String username;
	
	
	
	
	/**
	 *   Creates a resident account.
	 * 
	 * @param id
	 * @param username
	 * @throws IllegalArgumentException if id is not positive or 
	 * username is null or blank 
	 *         
	 */
	public Account(long id, String username) {
	    if (id <= 0) {
	        throw new IllegalArgumentException("Account ID must be positive.");
	    }
	    if (username == null || username.isBlank()) {
	        throw new IllegalArgumentException("Username is required.");
	    }

	    this.id = id;
	    this.username = username.strip();
	}
	
	/**
	 * Gets the account id
	 * 
	 * @return account id
	 */
	public long getId() {
		return this.id;
	}
	
	/**
	 * Gets the resident's login name. 
	 * 
	 * @return username with out whitespace 
	 */
	public String getUsername() {
		return this.username;
	}

}





