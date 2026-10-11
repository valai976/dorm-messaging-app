package edu.westga.comp4420.dorm_messaging.test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import org.junit.jupiter.api.Test;

import edu.westga.comp4420.dorm_messaging.model.Account;

import edu.westga.comp4420.dorm_messaging.model.AccountRegistry;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestAccountRegistry {
	
	@Test
	public void testRegisterWithValidValues() {
		AccountRegistry registry = new AccountRegistry();
		
		Account account =  registry.register(1L, "resident", "secret");
		
		assertEquals(1L, account.getId());
		assertEquals("resident", account.getUsername());
				
	}
	
	@Test
	public void testRegisterRejectsDuplicateUsername() {
		AccountRegistry registry = new AccountRegistry();
		registry.register(1L, "resident", "secret");

	    assertThrows(IllegalArgumentException.class, () -> {
	        registry.register(2L, "resident", "secret");
	    });
	}
	@Test
	public void testRegisterRejectsDuplicateUsernameWithWhitespace() {
	    AccountRegistry registry = new AccountRegistry();
	    registry.register(1L, "resident", "secret");

	    assertThrows(IllegalArgumentException.class, () -> {
	        registry.register(2L, "  resident  ", "secret");
	    });
	}
	@Test
	public void testRegisterAllowsDifferentUsernames() {
	    AccountRegistry registry = new AccountRegistry();

	    Account first = registry.register(1L, "alex", "secret");
	    Account second = registry.register(2L, "jordan", "secret");

	    assertEquals("alex", first.getUsername());
	    assertEquals("jordan", second.getUsername());
	    assertEquals(1L, first.getId());
	    assertEquals(2L, second.getId());
	}
	@Test
	public void testFailedRegistrationDoesNotReserveUsername() {
	    AccountRegistry registry = new AccountRegistry();

	    assertThrows(IllegalArgumentException.class, () -> {
	        registry.register(0L, "resident", "secret");
	    });

	    Account account = registry.register(1L, "resident", "secret");

	    assertEquals(1L, account.getId());
	    assertEquals("resident", account.getUsername());
	}
	@Test
	public void testRegisterRejectsBlankUsername() {
	    AccountRegistry registry = new AccountRegistry();

	    assertThrows(IllegalArgumentException.class, () -> {
	        registry.register(1L, "   ", "secret");
	    });
	}


    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n", "\u2003"})
    public void testInvalidPasswordDoesNotReserveUsername(String password) {
        AccountRegistry registry = new AccountRegistry();
        assertThrows(IllegalArgumentException.class, () -> registry.register(1L, "resident", password));
        Account account = registry.register(1L, "resident", "valid password");
        assertTrue(account.matchesPassword("valid password"));
    }

    @Test
    public void testRegisteredAccountsHaveIndependentPasswords() {
        AccountRegistry registry = new AccountRegistry();
        Account first = registry.register(1L, "alex", "first password");
        Account second = registry.register(2L, "jordan", "second password");
        assertTrue(first.matchesPassword("first password"));
        assertTrue(second.matchesPassword("second password"));
        assertFalse(first.matchesPassword("second password"));
        assertFalse(second.matchesPassword("first password"));
    }

    @Test
    public void testRegistrationPreservesPasswordWhitespace() {
        AccountRegistry registry = new AccountRegistry();
        Account account = registry.register(1L, " resident ", " secret ");
        assertEquals("resident", account.getUsername());
        assertTrue(account.matchesPassword(" secret "));
        assertFalse(account.matchesPassword("secret"));
    }
}
