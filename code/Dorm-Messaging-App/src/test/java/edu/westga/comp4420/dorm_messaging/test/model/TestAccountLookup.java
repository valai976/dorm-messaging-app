package edu.westga.comp4420.dorm_messaging.test.model;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import edu.westga.comp4420.dorm_messaging.model.Account;
import edu.westga.comp4420.dorm_messaging.model.AccountRegistry;

/**
 * Tests username lookup without changing account registration or authentication.
 *
 * @author Valai Willie
 */
public class TestAccountLookup {

    @Test
    public void testFindReturnsTheMatchingRegisteredAccount() {
        AccountRegistry registry = new AccountRegistry();
        Account first = registry.register(1L, "alex", "first password");
        Account second = registry.register(2L, "jordan", "second password");

        assertSame(first, registry.findByUsername("alex"));
        assertSame(second, registry.findByUsername("jordan"));
    }

    @Test
    public void testFindInEmptyRegistryReturnsNull() {
        AccountRegistry registry = new AccountRegistry();

        assertNull(registry.findByUsername("resident"));
    }

    @Test
    public void testFindUnknownUsernameReturnsNullWithoutChangingExistingAccount() {
        AccountRegistry registry = new AccountRegistry();
        Account account = registry.register(1L, "resident", "secret");

        assertNull(registry.findByUsername("unknown"));
        assertSame(account, registry.findByUsername("resident"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  resident  ", "\tresident\n", "\u2003resident\u2003"})
    public void testFindStripsSurroundingWhitespace(String username) {
        AccountRegistry registry = new AccountRegistry();
        Account account = registry.register(1L, " resident ", "secret");

        assertSame(account, registry.findByUsername(username));
    }

    @Test
    public void testFindDistinguishesUsernameCase() {
        AccountRegistry registry = new AccountRegistry();
        Account lowercase = registry.register(1L, "alex", "first password");
        Account capitalized = registry.register(2L, "Alex", "second password");

        assertSame(lowercase, registry.findByUsername("alex"));
        assertSame(capitalized, registry.findByUsername("Alex"));
        assertNull(registry.findByUsername("ALEX"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n", "\u2003"})
    public void testFindNullOrBlankUsernameReturnsNull(String username) {
        AccountRegistry registry = new AccountRegistry();

        assertNull(registry.findByUsername(username));
    }
}
