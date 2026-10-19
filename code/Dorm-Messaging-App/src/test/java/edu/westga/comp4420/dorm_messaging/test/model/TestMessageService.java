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

public class TestMessageService {

    private AccountRegistry registry;
    private LoginService loginService;
    private DormService dormService;
    private MessageService messageService;
    private Account author;

    @BeforeEach
    public void setUp() {
        this.registry = new AccountRegistry();
        this.author = this.registry.register(1L, "alex", "secret");
        this.loginService = new LoginService(this.registry);
        this.dormService = new DormService(this.loginService);
        this.messageService =
            new MessageService(this.loginService, this.dormService);

        this.loginService.login("alex", "secret");
        this.dormService.selectDorm(Dorm.NORTH_HALL);
    }

    @Test
    public void testConstructorRejectsNullLoginService() {
        assertThrows(IllegalArgumentException.class,
            () -> new MessageService(null, this.dormService));
    }

    @Test
    public void testConstructorRejectsNullDormService() {
        assertThrows(IllegalArgumentException.class,
            () -> new MessageService(this.loginService, null));
    }

    @Test
    public void testNewFeedIsEmpty() {
        assertTrue(this.messageService.getFeed().isEmpty());
    }

    @Test
    public void testPostUsesCurrentAuthorDormAndTime() {
        LocalDateTime before = LocalDateTime.now();

        Message message = this.messageService.postMessage(" Hello ");

        LocalDateTime after = LocalDateTime.now();
        assertEquals(1L, message.getId());
        assertSame(this.author, message.getAuthor());
        assertEquals(Dorm.NORTH_HALL, message.getDorm());
        assertEquals("Hello", message.getContent());
        assertFalse(message.getPostedAt().isBefore(before));
        assertFalse(message.getPostedAt().isAfter(after));
        assertSame(message, this.messageService.getFeed().get(0));
    }

    @Test
    public void testFeedShowsNewestMessageFirst() {
        Message first = this.messageService.postMessage("First");
        Message second = this.messageService.postMessage("Second");

        assertEquals(2L, second.getId());
        assertEquals(List.of(second, first),
            this.messageService.getFeed());
    }

    @Test
    public void testFeedSeparatesDorms() {
        Message north = this.messageService.postMessage("North message");

        this.dormService.selectDorm(Dorm.SOUTH_HALL);
        assertTrue(this.messageService.getFeed().isEmpty());

        Message south = this.messageService.postMessage("South message");
        assertEquals(List.of(south), this.messageService.getFeed());

        this.dormService.selectDorm(Dorm.NORTH_HALL);
        assertEquals(List.of(north), this.messageService.getFeed());
    }

    @Test
    public void testResidentsInSameDormShareFeed() {
        Message first = this.messageService.postMessage("From Alex");
        Account jordan =
            this.registry.register(2L, "jordan", "other");

        this.loginService.logout();
        this.loginService.login("jordan", "other");
        this.dormService.selectDorm(Dorm.NORTH_HALL);

        assertEquals(List.of(first), this.messageService.getFeed());

        Message second = this.messageService.postMessage("From Jordan");
        assertSame(jordan, second.getAuthor());
        assertEquals(List.of(second, first),
            this.messageService.getFeed());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    public void testInvalidContentDoesNotAddMessageOrConsumeId(
            String content) {
        assertThrows(IllegalArgumentException.class,
            () -> this.messageService.postMessage(content));

        assertTrue(this.messageService.getFeed().isEmpty());
        Message message = this.messageService.postMessage("Valid");
        assertEquals(1L, message.getId());
    }

    @Test
    public void testPostingRequiresLogin() {
        this.loginService.logout();

        assertThrows(IllegalStateException.class,
            () -> this.messageService.postMessage("Hello"));
    }

    @Test
    public void testViewingFeedRequiresLogin() {
        this.loginService.logout();

        assertThrows(IllegalStateException.class,
            () -> this.messageService.getFeed());
    }

    @Test
    public void testPostingAndViewingRequireDormSelection() {
        this.registry.register(2L, "jordan", "other");
        this.loginService.logout();
        this.loginService.login("jordan", "other");

        assertThrows(IllegalStateException.class,
            () -> this.messageService.postMessage("Hello"));
        assertThrows(IllegalStateException.class,
            () -> this.messageService.getFeed());
    }

    @Test
    public void testFeedCannotBeModifiedByCaller() {
        this.messageService.postMessage("Keep this message");
        List<Message> feed = this.messageService.getFeed();

        assertThrows(UnsupportedOperationException.class,
            () -> feed.clear());
        assertEquals(1, this.messageService.getFeed().size());
    }
}