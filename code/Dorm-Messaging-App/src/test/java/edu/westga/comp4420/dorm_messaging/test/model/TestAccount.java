package edu.westga.comp4420.dorm_messaging.test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.westga.comp4420.dorm_messaging.model.Account;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestAccount {
	
	@Test
	public void testConstructorWithValidValues() {
		Account account = new Account(1L, "resident");
		
		assertEquals(1L, account.getId());
		assertEquals("resident", account.getUsername());
	}
	
	@Test
	public void testConstructorRejectsZeroId() {
		assertThrows(IllegalArgumentException.class, () -> {
	        new Account(0L, "resident");
	    });
	}
	@Test
	public void testConstructorRejectsNegativeId() {
	    assertThrows(IllegalArgumentException.class, () -> {
	        new Account(-1L, "resident");
	    });
	}
	@Test
	public void testConstructorRejectsNullUsername() {
	    assertThrows(IllegalArgumentException.class, () -> {
	        new Account(1L, null);
	    });
	}
	
	@Test
	public void testConstructorRejectsEmptyUsername() {
	    assertThrows(IllegalArgumentException.class, () -> {
	        new Account(1L, "");
	    });
	}
	@Test
	public void testConstructorRejectsWhitespaceUsername() {
	    assertThrows(IllegalArgumentException.class, () -> {
	        new Account(1L, "   ");
	    });
	}
	@Test
	public void testConstructorRemovesSurroundingWhitespace() {
	    Account account = new Account(1L, "  resident  ");

	    assertEquals("resident", account.getUsername());
	}

}
