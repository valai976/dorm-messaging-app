package edu.westga.comp4420.dorm_messaging.test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.westga.comp4420.dorm_messaging.model.Account;

import edu.westga.comp4420.dorm_messaging.model.AccountRegistry;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestAccountRegistry {
	
	@Test
	public void testRegisterWithValidValues() {
		AccountRegistry registry = new AccountRegistry();
		
		Account account =  registry.register(1L, "resident");
		
		assertEquals(1L, account.getId());
		assertEquals("resident", account.getUsername());
				
	}
	
	@Test
	public void testRegisterRejectsDuplicateUsername() {
		AccountRegistry registry = new AccountRegistry();
		registry.register(1L, "resident");

	    assertThrows(IllegalArgumentException.class, () -> {
	        registry.register(2L, "resident");
	    });
	}
	@Test
	public void testRegisterRejectsDuplicateUsernameWithWhitespace() {
	    AccountRegistry registry = new AccountRegistry();
	    registry.register(1L, "resident");

	    assertThrows(IllegalArgumentException.class, () -> {
	        registry.register(2L, "  resident  ");
	    });
	}
	@Test
	public void testRegisterAllowsDifferentUsernames() {
	    AccountRegistry registry = new AccountRegistry();

	    Account first = registry.register(1L, "alex");
	    Account second = registry.register(2L, "jordan");

	    assertEquals("alex", first.getUsername());
	    assertEquals("jordan", second.getUsername());
	    assertEquals(1L, first.getId());
	    assertEquals(2L, second.getId());
	}
	@Test
	public void testFailedRegistrationDoesNotReserveUsername() {
	    AccountRegistry registry = new AccountRegistry();

	    assertThrows(IllegalArgumentException.class, () -> {
	        registry.register(0L, "resident");
	    });

	    Account account = registry.register(1L, "resident");

	    assertEquals(1L, account.getId());
	    assertEquals("resident", account.getUsername());
	}
	@Test
	public void testRegisterRejectsBlankUsername() {
	    AccountRegistry registry = new AccountRegistry();

	    assertThrows(IllegalArgumentException.class, () -> {
	        registry.register(1L, "   ");
	    });
	}

}
