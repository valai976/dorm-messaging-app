package edu.westga.comp4420.dorm_messaging.test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import edu.westga.comp4420.dorm_messaging.model.Account;
import edu.westga.comp4420.dorm_messaging.model.Dorm;
import edu.westga.comp4420.dorm_messaging.model.Message;
import edu.westga.comp4420.dorm_messaging.model.Reply;

public class TestReply {

    private Account author;
    private Message parentMessage;
    private LocalDateTime postedAt;

    @BeforeEach
    public void setUp() {
        this.author = new Account(1L, "alex", "secret");
        this.postedAt = LocalDateTime.of(2026, 10, 11, 18, 0);
        this.parentMessage = new Message(1L, this.author,
            Dorm.NORTH_HALL, "Original message", this.postedAt);
    }

    @Test
    public void testConstructorStoresValuesAndTrimsContent() {
        Reply reply = new Reply(1L, this.parentMessage,
            this.author, "  My reply  ", this.postedAt);

        assertEquals(1L, reply.getId());
        assertSame(this.parentMessage, reply.getParentMessage());
        assertSame(this.author, reply.getAuthor());
        assertEquals("My reply", reply.getContent());
        assertEquals(this.postedAt, reply.getPostedAt());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    public void testRejectsNonPositiveId(long id) {
        assertThrows(IllegalArgumentException.class,
            () -> new Reply(id, this.parentMessage,
                this.author, "Reply", this.postedAt));
    }

    @Test
    public void testRejectsNullParentMessage() {
        assertThrows(IllegalArgumentException.class,
            () -> new Reply(1L, null,
                this.author, "Reply", this.postedAt));
    }

    @Test
    public void testRejectsNullAuthor() {
        assertThrows(IllegalArgumentException.class,
            () -> new Reply(1L, this.parentMessage,
                null, "Reply", this.postedAt));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    public void testRejectsMissingContent(String content) {
        assertThrows(IllegalArgumentException.class,
            () -> new Reply(1L, this.parentMessage,
                this.author, content, this.postedAt));
    }

    @Test
    public void testRejectsNullPostingTime() {
        assertThrows(IllegalArgumentException.class,
            () -> new Reply(1L, this.parentMessage,
                this.author, "Reply", null));
    }
}