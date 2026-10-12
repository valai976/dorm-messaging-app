package edu.westga.comp4420.dorm_messaging.model;

import java.time.LocalDateTime;

/**
 * Represents a resident's reply to a dorm message.
 *
 * @author Valai Willie
 */
public class Reply {

    private final long id;
    private final Message parentMessage;
    private final Account author;
    private final String content;
    private final LocalDateTime postedAt;
    
    
    /**
     * Creates a reply linked to an existing message.
     *
     * @param id the positive reply identifier
     * @param parentMessage the message receiving the reply
     * @param author the resident writing the reply
     * @param content the nonblank reply text
     * @param postedAt the time the reply was posted
     * @throws IllegalArgumentException if the ID is not positive,
     *         content is blank, or any required value is null
     */
    public Reply(long id, Message parentMessage, Account author,
            String content, LocalDateTime postedAt) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                "Reply ID must be positive.");
        }
        if (parentMessage == null) {
            throw new IllegalArgumentException(
                "A parent message is required.");
        }
        if (author == null) {
            throw new IllegalArgumentException("An author is required.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Reply content is required.");
        }
        if (postedAt == null) {
            throw new IllegalArgumentException("A posting time is required.");
        }

        this.id = id;
        this.parentMessage = parentMessage;
        this.author = author;
        this.content = content.strip();
        this.postedAt = postedAt;
        
        
    }
    
    /**
     * Gets the reply identifier.
     *
     * @return the reply ID
     */
    public long getId() {
        return this.id;
    }

    /**
     * Gets the message receiving this reply.
     *
     * @return the parent message
     */
    public Message getParentMessage() {
        return this.parentMessage;
    }

    /**
     * Gets the resident who wrote the reply.
     *
     * @return the author
     */
    public Account getAuthor() {
        return this.author;
    }

    /**
     * Gets the reply text.
     *
     * @return the content without surrounding whitespace
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Gets the posting time.
     *
     * @return the time the reply was posted
     */
    public LocalDateTime getPostedAt() {
        return this.postedAt;
    }
}