package edu.westga.comp4420.dorm_messaging.model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Manages posting and viewing messages in dorm communities.
 *
 * @author Valai Willie
 */
public class MessageService {

    private final LoginService loginService;
    private final DormService dormService;
    private final List<Message> messages;
    private long nextMessageId;

    /**
     * Creates an empty message service.
     *
     * @param loginService the service identifying the signed-in resident
     * @param dormService the service tracking dorm selections
     * @throws IllegalArgumentException if either service is null
     */
    public MessageService(LoginService loginService,
            DormService dormService) {
        if (loginService == null || dormService == null) {
            throw new IllegalArgumentException(
                "Login and dorm services are required.");
        }

        this.loginService = loginService;
        this.dormService = dormService;
        this.messages = new ArrayList<>();
        this.nextMessageId = 1L;
    }
    
    /**
     * Posts a message to the signed-in resident's selected dorm.
     *
     * @param content the nonblank message text
     * @return the newly created message
     * @throws IllegalStateException if nobody is signed in or no dorm is selected
     * @throws IllegalArgumentException if content is null or blank
     */
    public Message postMessage(String content) {
        Account author = this.loginService.getCurrentAccount();

        if (author == null) {
            throw new IllegalStateException("Please log in first.");
        }

        Dorm dorm = this.dormService.getSelectedDorm();

        if (dorm == null) {
            throw new IllegalStateException(
                "Please select a dorm before posting.");
        }

        Message message = new Message(this.nextMessageId, author,
            dorm, content, LocalDateTime.now());

        this.messages.add(message);
        this.nextMessageId++;
        return message;
    }
    
    /**
     * Gets messages belonging to the signed-in resident's selected dorm.
     *
     * @return an unmodifiable list of messages, newest first
     * @throws IllegalStateException if nobody is signed in or no dorm is selected
     */
    public List<Message> getFeed() {
        Dorm dorm = this.dormService.getSelectedDorm();

        if (dorm == null) {
            throw new IllegalStateException(
                "Please select a dorm before viewing messages.");
        }

        return this.messages.stream()
            .filter(message -> message.getDorm() == dorm)
            .sorted(Comparator.comparing(Message::getPostedAt)
                .thenComparingLong(Message::getId)
                .reversed())
            .toList();
    }
    /**
     * Gets a snapshot of messages across all dorms for storage.
     *
     * @return all stored messages
     */
    public List<Message> getAllMessages() {
        return List.copyOf(this.messages);
    }

    /**
     * Restores a saved message and advances the next available ID.
     *
     * @param message the message to restore
     * @throws IllegalArgumentException if the message is null or its ID exists
     */
    public void restoreMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("A message is required.");
        }
        if (this.messages.stream().anyMatch(
            existing -> existing.getId() == message.getId())) {
            throw new IllegalArgumentException("The message ID already exists.");
        }
        this.messages.add(message);
        this.nextMessageId = Math.max(this.nextMessageId, message.getId() + 1);
    }
    
}