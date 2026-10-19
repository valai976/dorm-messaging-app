package edu.westga.comp4420.dorm_messaging.model;

import java.time.LocalDateTime;

/**
 * Represents a message posted by a resident in a dorm community.
 *
 * @author Valai Willie
 */
public class Message {

    private final long id;
    private final Account author;
    private final Dorm dorm;
    private final String content;
    private final LocalDateTime postedAt;
    
    
    /**
     * Creates a dorm message with its author and posting time.
     * 
     * @param id
     * @param author
     * @param dorm
     * @param content
     * @param postedAt
     */
    public Message(long id, Account author, Dorm dorm,
            String content, LocalDateTime postedAt) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                "Message ID must be positive.");
        }
        if (author == null) {
            throw new IllegalArgumentException("An author is required.");
        }
        if (dorm == null) {
            throw new IllegalArgumentException("A dorm is required.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content is required.");
        }
        if (postedAt == null) {
            throw new IllegalArgumentException("A posting time is required.");
        }

        this.id = id;
        this.author = author;
        this.dorm = dorm;
        this.content = content.strip();
        this.postedAt = postedAt;
    }
    
    /**
     * Gets the message identifier.
     *
     * @return the message ID
     */
    public long getId() {
        return this.id;
    }

    /**
     * Gets the resident who posted the message.
     *
     * @return the author
     */
    public Account getAuthor() {
        return this.author;
    }

    /**
     * Gets the community receiving the message.
     *
     * @return the dorm
     */
    public Dorm getDorm() {
        return this.dorm;
    }

    /**
     * Gets the message text.
     *
     * @return the content without surrounding whitespace
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Gets the posting time.
     *
     * @return the time the message was posted
     */
    public LocalDateTime getPostedAt() {
        return this.postedAt;
    }
    
}