package edu.westga.comp4420.dorm_messaging.test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edu.westga.comp4420.dorm_messaging.model.Account;
import edu.westga.comp4420.dorm_messaging.model.AccountRegistry;
import edu.westga.comp4420.dorm_messaging.model.AccountStorage;
import edu.westga.comp4420.dorm_messaging.model.LocalStorage;
import edu.westga.comp4420.dorm_messaging.model.LoginService;
import static org.junit.jupiter.api.Assertions.assertThrows;
public class TestAccountStorage {

    @TempDir
    Path directory;

    @Test
    public void testAccountCanLoginAfterSavingAndLoading()
            throws IOException {
        AccountRegistry original = new AccountRegistry();
        original.register(1L, "alex", "DormPassword123");

        AccountStorage accountStorage = new AccountStorage();
        Properties data = new Properties();
        accountStorage.saveAccounts(original, data);

        Path file = this.directory.resolve("accounts.xml");
        new LocalStorage(file).save(data);

        Properties loadedData = new LocalStorage(file).load();
        AccountRegistry restored =
            accountStorage.loadAccounts(loadedData);

        Account account = restored.findByUsername("alex");

        assertNotNull(account);
        assertEquals(1L, account.getId());
        assertEquals("alex", account.getUsername());
        assertTrue(account.matchesPassword("DormPassword123"));
        assertFalse(account.matchesPassword("wrong"));

        LoginService loginService = new LoginService(restored);
        assertEquals(account,
            loginService.login("alex", "DormPassword123"));
    }
    @Test
    public void testLoadEmptyStorage() {
        AccountRegistry restored =
            new AccountStorage().loadAccounts(new Properties());

        assertTrue(restored.getAccounts().isEmpty());
    }

    @Test
    public void testSaveEmptyRegistry() {
        Properties data = new Properties();

        new AccountStorage().saveAccounts(new AccountRegistry(), data);

        assertEquals("0", data.getProperty("account.count"));
    }

    @Test
    public void testRejectNullRegistry() {
        assertThrows(IllegalArgumentException.class,
            () -> new AccountStorage().saveAccounts(null, new Properties()));
    }

    @Test
    public void testRejectNullSaveData() {
        assertThrows(IllegalArgumentException.class,
            () -> new AccountStorage().saveAccounts(new AccountRegistry(), null));
    }

    @Test
    public void testRejectNullLoadData() {
        assertThrows(IllegalArgumentException.class,
            () -> new AccountStorage().loadAccounts(null));
    }

    @Test
    public void testRejectNegativeAccountCount() {
        Properties data = new Properties();
        data.setProperty("account.count", "-1");

        assertThrows(IllegalArgumentException.class,
            () -> new AccountStorage().loadAccounts(data));
    }
}