package edu.westga.comp4420.dorm_messaging.test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import edu.westga.comp4420.dorm_messaging.model.AccountRegistry;
import edu.westga.comp4420.dorm_messaging.model.Dorm;
import edu.westga.comp4420.dorm_messaging.model.DormService;
import edu.westga.comp4420.dorm_messaging.model.LoginService;

public class TestDormService {

    private AccountRegistry registry;
    private LoginService loginService;
    private DormService dormService;

    @BeforeEach
    public void setUp() {
        this.registry = new AccountRegistry();
        this.loginService = new LoginService(this.registry);
        this.dormService = new DormService(this.loginService);
    }

    private void registerAndLogin() {
        this.registry.register(1L, "alex", "secret");
        this.loginService.login("alex", "secret");
    }

    @Test
    public void testConstructorRejectsNullLoginService() {
        assertThrows(IllegalArgumentException.class,
            () -> new DormService(null));
    }

    @Test
    public void testSelectionRequiresLogin() {
        assertThrows(IllegalStateException.class,
            () -> this.dormService.selectDorm(Dorm.NORTH_HALL));
    }

    @Test
    public void testReadingSelectionRequiresLogin() {
        assertThrows(IllegalStateException.class,
            () -> this.dormService.getSelectedDorm());
    }

    @Test
    public void testResidentStartsWithoutSelection() {
        this.registerAndLogin();

        assertNull(this.dormService.getSelectedDorm());
    }

    @ParameterizedTest
    @EnumSource(Dorm.class)
    public void testSelectsEachAvailableDorm(Dorm dorm) {
        this.registerAndLogin();
        this.dormService.selectDorm(dorm);

        assertEquals(dorm, this.dormService.getSelectedDorm());
    }

    @Test
    public void testNullSelectionPreservesPreviousDorm() {
        this.registerAndLogin();
        this.dormService.selectDorm(Dorm.NORTH_HALL);

        assertThrows(IllegalArgumentException.class,
            () -> this.dormService.selectDorm(null));

        assertEquals(Dorm.NORTH_HALL,
            this.dormService.getSelectedDorm());
    }

    @Test
    public void testResidentCanChangeDorm() {
        this.registerAndLogin();
        this.dormService.selectDorm(Dorm.NORTH_HALL);
        this.dormService.selectDorm(Dorm.SOUTH_HALL);

        assertEquals(Dorm.SOUTH_HALL,
            this.dormService.getSelectedDorm());
    }

    @Test
    public void testResidentsHaveSeparateSelections() {
        this.registerAndLogin();
        this.dormService.selectDorm(Dorm.NORTH_HALL);

        this.registry.register(2L, "jordan", "other");
        this.loginService.logout();
        this.loginService.login("jordan", "other");

        assertNull(this.dormService.getSelectedDorm());

        this.dormService.selectDorm(Dorm.SOUTH_HALL);
        this.loginService.logout();
        this.loginService.login("alex", "secret");

        assertEquals(Dorm.NORTH_HALL,
            this.dormService.getSelectedDorm());

        this.loginService.logout();
        this.loginService.login("jordan", "other");

        assertEquals(Dorm.SOUTH_HALL,
            this.dormService.getSelectedDorm());
    }

    @Test
    public void testLogoutBlocksDormAccess() {
        this.registerAndLogin();
        this.dormService.selectDorm(Dorm.NORTH_HALL);
        this.loginService.logout();

        assertThrows(IllegalStateException.class,
            () -> this.dormService.getSelectedDorm());
        assertThrows(IllegalStateException.class,
            () -> this.dormService.selectDorm(Dorm.EAST_HALL));
    }
}