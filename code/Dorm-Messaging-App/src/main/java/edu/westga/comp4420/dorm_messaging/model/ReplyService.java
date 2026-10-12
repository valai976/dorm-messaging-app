package edu.westga.comp4420.dorm_messaging.model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Manages replies to messages in the selected dorm community.
 *
 * @author Valai Willie
 */
public class ReplyService {

    private final LoginService loginService;
    private final MessageService messageService;
    private final List<Reply> replies;
    private long nextReplyId;

    /**
     * Creates a service with no replies.
     *
     * @param loginService the service identifying the signed-in resident
     * @param messageService the service providing the current dorm's feed
     * @throws IllegalArgumentException if either service is null
     */
    public ReplyService(LoginService loginService,
            MessageService messageService) {
        if (loginService == null || messageService == null) {
            throw new IllegalArgumentException(
                "Login and message services are required.");
        }

        this.loginService = loginService;
        this.messageService = messageService;
        this.replies = new ArrayList<>();
        this.nextReplyId = 1L;
    }
    
    /**
     * Adds a reply to a message in the selected dorm.
     *
     * @param parentMessage the message receiving the reply
     * @param content the nonblank reply text
     * @return the newly created reply
     * @throws IllegalStateException if login or dorm selection is missing
     * @throws IllegalArgumentException if the parent is unavailable
     *         or the content is null or blank
     */
    public Reply postReply(Message parentMessage, String content) {
        this.requireAvailableMessage(parentMessage);
        Account author = this.loginService.getCurrentAccount();

        Reply reply = new Reply(this.nextReplyId, parentMessage,
            author, content, LocalDateTime.now());

        this.replies.add(reply);
        this.nextReplyId++;
        return reply;
    }

    private void requireAvailableMessage(Message parentMessage) {
        List<Message> feed = this.messageService.getFeed();

        if (parentMessage == null || !feed.contains(parentMessage)) {
            throw new IllegalArgumentException(
                "Select an existing message from your current dorm.");
        }
    }
    /**
     * Gets replies to an available message, oldest first.
     *
     * @param parentMessage the message whose replies should be shown
     * @return an unmodifiable list of replies to that message
     * @throws IllegalStateException if login or dorm selection is missing
     * @throws IllegalArgumentException if the parent message is unavailable
     */
    public List<Reply> getReplies(Message parentMessage) {
        this.requireAvailableMessage(parentMessage);

        return this.replies.stream()
            .filter(reply -> reply.getParentMessage() == parentMessage)
            .sorted(Comparator.comparing(Reply::getPostedAt)
                .thenComparingLong(Reply::getId))
            .toList();
    }
}