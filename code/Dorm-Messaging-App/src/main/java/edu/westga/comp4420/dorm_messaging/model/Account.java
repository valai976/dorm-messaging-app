package edu.westga.comp4420.dorm_messaging.model;

/**
 * Identifies a resident and protects their password with a salted credential.
 *
 * @author Valai Willie
 */
public class Account {
    private final long id;
    private final String username;
    private final PasswordCredential credential;

    /**
     * Creates an account with a case-sensitive username and password.
     * Username whitespace is stripped; password whitespace is preserved.
     *
     * @param id the positive account identifier
     * @param username the nonblank resident login name
     * @param password the nonblank password to protect
     * @throws IllegalArgumentException if the ID is not positive or a required field is missing
     * @throws IllegalStateException if password hashing is unavailable
     */
    public Account(long id, String username, String password) {
        if (id <= 0) {
            throw new IllegalArgumentException(" Account ID must be positive.");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(" Username is required.");
        }
        this.id = id;
        this.username = username.strip();
        this.credential = new PasswordCredential(password);
    }

    /**
     * Gets the account identifier.
     *
     * @return the positive account ID
     */
    public long getId() {
        return this.id;
    }

    /**
     * Gets the resident's login name.
     *
     * @return the username without surrounding whitespace
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Checks a password without exposing the stored credential.
     *
     * @param candidate the password to check, which may be null or blank
     * @return true only when the candidate exactly matches the original password
     * @throws IllegalStateException if password hashing is unavailable
     */
    public boolean matchesPassword(String candidate) {
        return this.credential.matches(candidate);
    }
    /**
     * Gets the encoded credential for local storage.
     *
     * @return the encoded salt and password hash
     */
    public String getEncodedCredential() {
        return this.credential.encode();
    }

    /**
     * Restores an account from saved values.
     *
     * @param id the saved account identifier
     * @param username the saved username
     * @param encodedCredential the saved password credential
     * @return the restored account
     * @throws IllegalArgumentException if any saved value is invalid
     */
    public static Account restore(long id, String username,
            String encodedCredential) {
        PasswordCredential credential =
            PasswordCredential.decode(encodedCredential);

        return new Account(id, username, credential);
    }
    /**
     * Creates an account using an already restored credential.
     *
     * @param id the positive account identifier
     * @param username the nonblank login name
     * @param credential the restored password credential
     * @throws IllegalArgumentException if an account value is invalid
     */
    private Account(long id, String username,
            PasswordCredential credential) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                "Account ID must be positive.");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (credential == null) {
            throw new IllegalArgumentException(
                "A password credential is required.");
        }

        this.id = id;
        this.username = username.strip();
        this.credential = credential;
    }
}
