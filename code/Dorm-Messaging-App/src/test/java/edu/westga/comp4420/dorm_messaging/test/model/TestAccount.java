package edu.westga.comp4420.dorm_messaging.test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import org.junit.jupiter.api.Test;

import edu.westga.comp4420.dorm_messaging.model.Account;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestAccount {
	
	@Test
	public void testConstructorWithValidValues() {
		Account account = new Account(1L, "resident", "secret");
		
		assertEquals(1L, account.getId());
		assertEquals("resident", account.getUsername());
	}
	
	@Test
	public void testConstructorRejectsZeroId() {
		assertThrows(IllegalArgumentException.class, () -> {
	        new Account(0L, "resident", "secret");
	    });
	}
	@Test
	public void testConstructorRejectsNegativeId() {
	    assertThrows(IllegalArgumentException.class, () -> {
	        new Account(-1L, "resident", "secret");
	    });
	}
	@Test
	public void testConstructorRejectsNullUsername() {
	    assertThrows(IllegalArgumentException.class, () -> {
	        new Account(1L, null, "secret");
	    });
	}
	
	@Test
	public void testConstructorRejectsEmptyUsername() {
	    assertThrows(IllegalArgumentException.class, () -> {
	        new Account(1L, "", "secret");
	    });
	}
	@Test
	public void testConstructorRejectsWhitespaceUsername() {
	    assertThrows(IllegalArgumentException.class, () -> {
	        new Account(1L, "   ", "secret");
	    });
	}
	@Test
	public void testConstructorRemovesSurroundingWhitespace() {
	    Account account = new Account(1L, "  resident  ", "secret");

	    assertEquals("resident", account.getUsername());
	}


    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n", "\u2003"})
    public void testConstructorRejectsMissingPassword(String password) {
        assertThrows(IllegalArgumentException.class, () -> new Account(1L, "resident", password));
    }

    @Test
    public void testAccountChecksItsPassword() {
        Account account = new Account(1L, "resident", "DormPassword123");
        assertTrue(account.matchesPassword("DormPassword123"));
        assertFalse(account.matchesPassword("WrongPassword"));
        assertFalse(account.matchesPassword("dormpassword123"));
        assertFalse(account.matchesPassword(null));
        assertFalse(account.matchesPassword(" "));
    }

    @Test
    public void testAccountPreservesPasswordWhitespace() {
        Account account = new Account(1L, " resident ", " secret ");
        assertEquals("resident", account.getUsername());
        assertTrue(account.matchesPassword(" secret "));
        assertFalse(account.matchesPassword("secret"));
    }
}
