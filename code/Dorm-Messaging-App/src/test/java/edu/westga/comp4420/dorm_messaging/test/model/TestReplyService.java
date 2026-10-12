package edu.westga.comp4420.dorm_messaging.test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import edu.westga.comp4420.dorm_messaging.model.Account;
import edu.westga.comp4420.dorm_messaging.model.AccountRegistry;
import edu.westga.comp4420.dorm_messaging.model.Dorm;
import edu.westga.comp4420.dorm_messaging.model.DormService;
import edu.westga.comp4420.dorm_messaging.model.LoginService;
import edu.westga.comp4420.dorm_messaging.model.Message;
import edu.westga.comp4420.dorm_messaging.model.MessageService;
import edu.westga.comp4420.dorm_messaging.model.Reply;
import edu.westga.comp4420.dorm_messaging.model.ReplyService;

public class TestReplyService {

    private AccountRegistry registry;
    private LoginService loginService;
    private DormService dormService;
    private MessageService messageService;
    private ReplyService replyService;
    private Account author;
    private Message parentMessage;

    @BeforeEach
    public void setUp() {
        this.registry = new AccountRegistry();
        this.author = this.registry.register(1L, "alex", "secret");
        this.loginService = new LoginService(this.registry);
        this.dormService = new DormService(this.loginService);
        this.messageService =
            new MessageService(this.loginService, this.dormService);
        this.replyService =
            new ReplyService(this.loginService, this.messageService);

        this.loginService.login("alex", "secret");
        this.dormService.selectDorm(Dorm.NORTH_HALL);
        this.parentMessage =
            this.messageService.postMessage("Original message");
    }

    @Test
    public void testConstructorRejectsNullLoginService() {
        assertThrows(IllegalArgumentException.class,
            () -> new ReplyService(null, this.messageService));
    }

    @Test
    public void testConstructorRejectsNullMessageService() {
        assertThrows(IllegalArgumentException.class,
            () -> new ReplyService(this.loginService, null));
    }

    @Test
    public void testNewMessageHasNoReplies() {
        assertTrue(this.replyService
            .getReplies(this.parentMessage).isEmpty());
    }

    @Test
    public void testPostStoresAuthorParentContentAndTime() {
        LocalDateTime before = LocalDateTime.now();

        Reply reply =
            this.replyService.postReply(this.parentMessage, " Hello ");

        LocalDateTime after = LocalDateTime.now();
        assertEquals(1L, reply.getId());
        assertSame(this.author, reply.getAuthor());
        assertSame(this.parentMessage, reply.getParentMessage());
        assertEquals("Hello", reply.getContent());
        assertFalse(reply.getPostedAt().isBefore(before));
        assertFalse(reply.getPostedAt().isAfter(after));
        assertEquals(List.of(reply),
            this.replyService.getReplies(this.parentMessage));
    }

    @Test
    public void testRepliesAppearOldestFirst() {
        Reply first =
            this.replyService.postReply(this.parentMessage, "First");
        Reply second =
            this.replyService.postReply(this.parentMessage, "Second");

        assertEquals(2L, second.getId());
        assertEquals(List.of(first, second),
            this.replyService.getReplies(this.parentMessage));
    }

    @Test
    public void testRepliesBelongToTheirParentMessage() {
        Message other = this.messageService.postMessage("Other post");
        Reply reply =
            this.replyService.postReply(this.parentMessage, "Answer");

        assertEquals(List.of(reply),
            this.replyService.getReplies(this.parentMessage));
        assertTrue(this.replyService.getReplies(other).isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    public void testInvalidContentDoesNotAddReplyOrConsumeId(
            String content) {
        assertThrows(IllegalArgumentException.class,
            () -> this.replyService.postReply(
                this.parentMessage, content));

        assertTrue(this.replyService
            .getReplies(this.parentMessage).isEmpty());

        Reply reply =
            this.replyService.postReply(this.parentMessage, "Valid");
        assertEquals(1L, reply.getId());
    }

    @Test
    public void testRejectsNullParent() {
        assertThrows(IllegalArgumentException.class,
            () -> this.replyService.postReply(null, "Reply"));
        assertThrows(IllegalArgumentException.class,
            () -> this.replyService.getReplies(null));
    }

    @Test
    public void testRejectsMessageNotStoredInFeed() {
        Message unstored = new Message(
            this.parentMessage.getId(), this.author,
            Dorm.NORTH_HALL, "Unstored", LocalDateTime.now());

        assertThrows(IllegalArgumentException.class,
            () -> this.replyService.postReply(unstored, "Reply"));
        assertThrows(IllegalArgumentException.class,
            () -> this.replyService.getReplies(unstored));
    }

    @Test
    public void testRejectsMessageFromAnotherDorm() {
        this.dormService.selectDorm(Dorm.SOUTH_HALL);

        assertThrows(IllegalArgumentException.class,
            () -> this.replyService.postReply(
                this.parentMessage, "Reply"));
        assertThrows(IllegalArgumentException.class,
            () -> this.replyService.getReplies(this.parentMessage));
    }

    @Test
    public void testReplyAccessRequiresLogin() {
        this.loginService.logout();

        assertThrows(IllegalStateException.class,
            () -> this.replyService.postReply(
                this.parentMessage, "Reply"));
        assertThrows(IllegalStateException.class,
            () -> this.replyService.getReplies(this.parentMessage));
    }

    @Test
    public void testReplyAccessRequiresDormSelection() {
        this.registry.register(2L, "jordan", "other");
        this.loginService.logout();
        this.loginService.login("jordan", "other");

        assertThrows(IllegalStateException.class,
            () -> this.replyService.postReply(
                this.parentMessage, "Reply"));
        assertThrows(IllegalStateException.class,
            () -> this.replyService.getReplies(this.parentMessage));
    }

    @Test
    public void testAnotherResidentCanReplyInSameDorm() {
        Account jordan =
            this.registry.register(2L, "jordan", "other");
        this.loginService.logout();
        this.loginService.login("jordan", "other");
        this.dormService.selectDorm(Dorm.NORTH_HALL);

        Reply reply =
            this.replyService.postReply(this.parentMessage, "Answer");

        assertSame(jordan, reply.getAuthor());
        assertEquals(List.of(reply),
            this.replyService.getReplies(this.parentMessage));

        this.loginService.logout();
        this.loginService.login("alex", "secret");

        assertEquals(List.of(reply),
            this.replyService.getReplies(this.parentMessage));
    }

    @Test
    public void testReturnedRepliesCannotBeModified() {
        this.replyService.postReply(this.parentMessage, "Keep this");
        List<Reply> replies =
            this.replyService.getReplies(this.parentMessage);

        assertThrows(UnsupportedOperationException.class,
            () -> replies.clear());
        assertEquals(1,
            this.replyService.getReplies(this.parentMessage).size());
    }
}