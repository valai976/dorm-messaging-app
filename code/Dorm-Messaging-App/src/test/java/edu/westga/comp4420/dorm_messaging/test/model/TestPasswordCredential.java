package edu.westga.comp4420.dorm_messaging.test.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import edu.westga.comp4420.dorm_messaging.model.PasswordCredential;

/**
 * Tests password validation, matching, and unavailable hashing support.
 *
 * @author Valai Willie
 */
public class TestPasswordCredential {

    @Test
    public void testMatchesOriginalPassword() {
        PasswordCredential credential = new PasswordCredential("DormPassword123");
        assertTrue(credential.matches("DormPassword123"));
    }

    @Test
    public void testRejectsIncorrectPassword() {
        PasswordCredential credential = new PasswordCredential("DormPassword123");
        assertFalse(credential.matches("WrongPassword"));
    }

    @Test
    public void testPasswordMatchingIsCaseSensitive() {
        PasswordCredential credential = new PasswordCredential("DormPassword123");
        assertFalse(credential.matches("dormpassword123"));
    }

    @Test
    public void testPreservesSurroundingPasswordWhitespace() {
        PasswordCredential credential = new PasswordCredential(" secret ");
        assertTrue(credential.matches(" secret "));
        assertFalse(credential.matches("secret"));
    }

    @Test
    public void testMatchesUnicodePassword() {
        PasswordCredential credential = new PasswordCredential("caf\u00e9 password");
        assertTrue(credential.matches("caf\u00e9 password"));
        assertFalse(credential.matches("cafe password"));
    }

    @Test
    public void testCredentialsRemainIndependent() {
        PasswordCredential first = new PasswordCredential("first password");
        PasswordCredential second = new PasswordCredential("second password");
        assertTrue(first.matches("first password"));
        assertTrue(second.matches("second password"));
        assertFalse(first.matches("second password"));
        assertFalse(second.matches("first password"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n", "\u2003"})
    public void testConstructorRejectsMissingPassword(String password) {
        assertThrows(IllegalArgumentException.class, () -> new PasswordCredential(password));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n", "\u2003"})
    public void testRejectsMissingCandidatePassword(String candidate) {
        PasswordCredential credential = new PasswordCredential("secret");
        assertFalse(credential.matches(candidate));
    }

    @Test
    public void testReportsUnavailableHashing() {
        Provider[] providers = Security.getProviders();
        try {
            for (Provider provider : providers) {
                if (provider.getService("SecretKeyFactory", "PBKDF2WithHmacSHA256") != null) {
                    Security.removeProvider(provider.getName());
                }
            }
            IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> new PasswordCredential("secret"));
            assertTrue(exception.getCause() instanceof NoSuchAlgorithmException);
        } finally {
            // Restore JVM security providers even when the assertion fails.
            for (int index = 0; index < providers.length; index++) {
                if (Security.getProvider(providers[index].getName()) == null) {
                    Security.insertProviderAt(providers[index], index + 1);
                }
            }
        }
        assertTrue(new PasswordCredential("secret").matches("secret"));
    }
}
