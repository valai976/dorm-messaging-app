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

public class TestMessage {

    private Account author;
    private LocalDateTime postedAt;

    @BeforeEach
    public void setUp() {
        this.author = new Account(1L, "alex", "secret");
        this.postedAt = LocalDateTime.of(2026, 10, 11, 18, 0);
    }

    @Test
    public void testConstructorStoresValuesAndTrimsContent() {
        Message message = new Message(1L, this.author,
            Dorm.NORTH_HALL, "  Hello dorm!  ", this.postedAt);

        assertEquals(1L, message.getId());
        assertSame(this.author, message.getAuthor());
        assertEquals(Dorm.NORTH_HALL, message.getDorm());
        assertEquals("Hello dorm!", message.getContent());
        assertEquals(this.postedAt, message.getPostedAt());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    public void testRejectsNonPositiveId(long id) {
        assertThrows(IllegalArgumentException.class,
            () -> new Message(id, this.author,
                Dorm.NORTH_HALL, "Hello", this.postedAt));
    }

    @Test
    public void testRejectsNullAuthor() {
        assertThrows(IllegalArgumentException.class,
            () -> new Message(1L, null,
                Dorm.NORTH_HALL, "Hello", this.postedAt));
    }

    @Test
    public void testRejectsNullDorm() {
        assertThrows(IllegalArgumentException.class,
            () -> new Message(1L, this.author,
                null, "Hello", this.postedAt));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    public void testRejectsMissingContent(String content) {
        assertThrows(IllegalArgumentException.class,
            () -> new Message(1L, this.author,
                Dorm.NORTH_HALL, content, this.postedAt));
    }

    @Test
    public void testRejectsNullPostingTime() {
        assertThrows(IllegalArgumentException.class,
            () -> new Message(1L, this.author,
                Dorm.NORTH_HALL, "Hello", null));
    }
}