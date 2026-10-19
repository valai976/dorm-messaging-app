package edu.westga.comp4420.dorm_messaging.test.model;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import edu.westga.comp4420.dorm_messaging.model.Account;
import edu.westga.comp4420.dorm_messaging.model.AccountRegistry;
import edu.westga.comp4420.dorm_messaging.model.LoginService;

/**
 * Checks authentication and session transitions without a user interface.
 *
 * @author Valai Willie
 */
public class TestLoginService {
    private AccountRegistry registry;
    private LoginService service;

    @BeforeEach
    public void setUp() {
        this.registry = new AccountRegistry();
        this.service = new LoginService(this.registry);
    }

    @Test
    public void testConstructorRejectsNullRegistry() {
        assertThrows(IllegalArgumentException.class, () -> new LoginService(null));
    }

    @Test
    public void testNewServiceStartsLoggedOut() {
        assertNull(this.service.getCurrentAccount());
    }

    @Test
    public void testLoginUsesAnAccountRegisteredAfterServiceCreation() {
        Account account = this.registry.register(1L, "resident", "secret");

        assertSame(account, this.service.login("resident", "secret"));
        assertSame(account, this.service.getCurrentAccount());
    }

    @Test
    public void testLoginRejectsUnknownUsername() {
        this.registry.register(1L, "resident", "secret");

        assertThrows(IllegalArgumentException.class, () -> this.service.login("unknown", "secret"));
        assertNull(this.service.getCurrentAccount());
    }

    @Test
    public void testLoginRejectsEmptyRegistry() {
        assertThrows(IllegalArgumentException.class, () -> this.service.login("resident", "secret"));
        assertNull(this.service.getCurrentAccount());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n", "\u2003"})
    public void testLoginRejectsMissingUsername(String username) {
        assertThrows(IllegalArgumentException.class, () -> this.service.login(username, "secret"));
        assertNull(this.service.getCurrentAccount());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n", "\u2003"})
    public void testLoginRejectsMissingPassword(String password) {
        this.registry.register(1L, "resident", "secret");

        assertThrows(IllegalArgumentException.class, () -> this.service.login("resident", password));
        assertNull(this.service.getCurrentAccount());
    }

    @Test
    public void testIncorrectPasswordClearsPreviousSession() {
        this.registry.register(1L, "resident", "secret");
        this.service.login("resident", "secret");

        assertThrows(IllegalArgumentException.class, () -> this.service.login("resident", "incorrect"));
        assertNull(this.service.getCurrentAccount());
    }

    @Test
    public void testUnknownUsernameClearsPreviousSession() {
        this.registry.register(1L, "resident", "secret");
        this.service.login("resident", "secret");

        assertThrows(IllegalArgumentException.class, () -> this.service.login("unknown", "secret"));
        assertNull(this.service.getCurrentAccount());
    }

    @Test
    public void testLoginStripsUsernameSpacesButPreservesPasswordSpaces() {
        Account account = this.registry.register(1L, "resident", " secret ");

        assertSame(account, this.service.login("  resident  ", " secret "));
        assertThrows(IllegalArgumentException.class, () -> this.service.login("resident", "secret"));
        assertNull(this.service.getCurrentAccount());
    }

    @Test
    public void testUsernameAndPasswordMatchingAreCaseSensitive() {
        Account account = this.registry.register(1L, "Resident", "Secret");

        assertThrows(IllegalArgumentException.class, () -> this.service.login("resident", "Secret"));
        assertThrows(IllegalArgumentException.class, () -> this.service.login("Resident", "secret"));
        assertSame(account, this.service.login("Resident", "Secret"));
    }

    @Test
    public void testLogoutPreservesAccountAndAllowsLoginAgain() {
        Account account = this.registry.register(1L, "resident", "secret");
        this.service.login("resident", "secret");

        this.service.logout();
        assertNull(this.service.getCurrentAccount());
        assertSame(account, this.registry.findByUsername("resident"));
        assertSame(account, this.service.login("resident", "secret"));
    }

    @Test
    public void testLogoutWhenAlreadyLoggedOutIsSafe() {
        this.service.logout();
        this.service.logout();

        assertNull(this.service.getCurrentAccount());
    }

    @Test
    public void testSuccessfulLoginSwitchesCurrentAccount() {
        Account first = this.registry.register(1L, "alex", "first password");
        Account second = this.registry.register(2L, "jordan", "second password");

        assertSame(first, this.service.login("alex", "first password"));
        assertSame(second, this.service.login("jordan", "second password"));
        assertSame(second, this.service.getCurrentAccount());
    }
}
