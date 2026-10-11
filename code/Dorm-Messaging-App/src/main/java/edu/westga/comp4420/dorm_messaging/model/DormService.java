package edu.westga.comp4420.dorm_messaging.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages dorm selections for registered residents.
 *
 * @author Valai Willie
 */
public class DormService {

    private final LoginService loginService;
    private final Map<Long, Dorm> selections;

    /**
     * Creates a dorm service with no saved selections.
     *
     * @param loginService the service identifying the signed-in resident
     * @throws IllegalArgumentException if loginService is null
     */
    public DormService(LoginService loginService) {
        if (loginService == null) {
            throw new IllegalArgumentException(
                "A login service is required.");
        }

        this.loginService = loginService;
        this.selections = new HashMap<>();
    }
    
    /**
     * Selects a dorm for the signed-in resident.
     *
     * @param dorm the community to join
     * @throws IllegalStateException if nobody is signed in
     * @throws IllegalArgumentException if dorm is null
     */
    public void selectDorm(Dorm dorm) {
        Account account = this.requireCurrentAccount();

        if (dorm == null) {
            throw new IllegalArgumentException("A dorm is required.");
        }

        this.selections.put(account.getId(), dorm);
    }

    /**
     * Gets the signed-in resident's selected dorm.
     *
     * @return the selected dorm, or null if none has been selected
     * @throws IllegalStateException if nobody is signed in
     */
    public Dorm getSelectedDorm() {
        Account account = this.requireCurrentAccount();
        return this.selections.get(account.getId());
    }

    private Account requireCurrentAccount() {
        Account account = this.loginService.getCurrentAccount();

        if (account == null) {
            throw new IllegalStateException("Please log in first.");
        }

        return account;
    }
}