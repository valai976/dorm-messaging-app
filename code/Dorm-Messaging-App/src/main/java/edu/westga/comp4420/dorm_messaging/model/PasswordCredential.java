package edu.westga.comp4420.dorm_messaging.model;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Protects a password with a random salt and checks candidate passwords.
 * The original password is never retained as an instance field.
 *
 * @author Valai Willie
 */
public class PasswordCredential {
    private static final int SALT_BYTES = 16;
    private static final int HASH_ITERATIONS = 600000;
    private static final int HASH_BITS = 256;

    private final byte[] salt;
    private final byte[] passwordHash;

    /**
     * Creates a salted password credential. Password spaces are preserved exactly.
     *
     * @param password the password to protect
     * @throws IllegalArgumentException if password is null or blank
     * @throws IllegalStateException if password hashing is unavailable
     */
    public PasswordCredential(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }
        this.salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(this.salt);
        this.passwordHash = this.hashPassword(password);
    }

    /**
     * Checks whether a candidate matches the original password exactly.
     *
     * @param candidate the password to check
     * @return true if the password matches; false if it is null, blank, or incorrect
     * @throws IllegalStateException if password hashing is unavailable
     */
    public boolean matches(String candidate) {
        if (candidate == null || candidate.isBlank()) {
            return false;
        }
        byte[] candidateHash = this.hashPassword(candidate);
        return MessageDigest.isEqual(this.passwordHash, candidateHash);
    }

    private byte[] hashPassword(String password) {
        PBEKeySpec specification = new PBEKeySpec(password.toCharArray(), this.salt,
          HASH_ITERATIONS, HASH_BITS);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(specification).getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Password hashing is unavailable.", exception);
        } finally {
            specification.clearPassword();
        }
    }
}
