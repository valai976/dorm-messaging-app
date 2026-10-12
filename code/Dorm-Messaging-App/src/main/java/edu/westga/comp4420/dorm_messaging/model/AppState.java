package edu.westga.comp4420.dorm_messaging.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Holds the application's services and coordinates local persistence.
 *
 * @author Valai Willie
 */
public class AppState {

    private final LocalStorage storage;
    private final AccountRegistry accounts;
    private final LoginService login;
    private final DormService dorms;
    private final MessageService messages;
    private final ReplyService replies;

    /**
     * Loads saved data and creates services with nobody signed in.
     *
     * @param file the application's XML file
     * @throws IOException if saved data cannot be read
     * @throws IllegalArgumentException if the file or saved records are invalid
     */
    public AppState(Path file) throws IOException {
        this.storage = new LocalStorage(file);
        Properties data = this.storage.load();
        this.accounts = new AccountStorage().loadAccounts(data);
        this.login = new LoginService(this.accounts);
        this.dorms = new DormService(this.login);
        this.messages = new MessageService(this.login, this.dorms);
        this.replies = new ReplyService(this.login, this.messages);
        new CommunityStorage().load(this.accounts, this.dorms,
            this.messages, this.replies, data);
    }

    /**
     * Registers a resident using the next available account ID.
     *
     * @param username the requested username
     * @param password the requested password
     * @return the registered account
     * @throws IllegalArgumentException if the account details are invalid
     */
    public Account register(String username, String password) {
        long nextId = this.accounts.getAccounts().stream()
            .mapToLong(Account::getId).max().orElse(0L) + 1L;
        return this.accounts.register(nextId, username, password);
    }

    /**
     * Saves all accounts and community data.
     *
     * @throws IOException if the data cannot be written
     */
    public void save() throws IOException {
        Properties data = new Properties();
        new AccountStorage().saveAccounts(this.accounts, data);
        new CommunityStorage().save(this.dorms, this.messages, this.replies, data);
        this.storage.save(data);
    }

    /**
     * Gets the application's login service.
     *
     * @return the login service
     */
    public LoginService getLogin() {
        return this.login;
    }

    /**
     * Gets the application's dorm service.
     *
     * @return the dorm service
     */
    public DormService getDorms() {
        return this.dorms;
    }

    /**
     * Gets the application's message service.
     *
     * @return the message service
     */
    public MessageService getMessages() {
        return this.messages;
    }

    /**
     * Gets the application's reply service.
     *
     * @return the reply service
     */
    public ReplyService getReplies() {
        return this.replies;
    }
}