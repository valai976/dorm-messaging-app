package edu.westga.comp4420.dorm_messaging.test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edu.westga.comp4420.dorm_messaging.model.AccountRegistry;
import edu.westga.comp4420.dorm_messaging.model.AccountStorage;
import edu.westga.comp4420.dorm_messaging.model.CommunityStorage;
import edu.westga.comp4420.dorm_messaging.model.Dorm;
import edu.westga.comp4420.dorm_messaging.model.DormService;
import edu.westga.comp4420.dorm_messaging.model.LocalStorage;
import edu.westga.comp4420.dorm_messaging.model.LoginService;
import edu.westga.comp4420.dorm_messaging.model.Message;
import edu.westga.comp4420.dorm_messaging.model.MessageService;
import edu.westga.comp4420.dorm_messaging.model.Reply;
import edu.westga.comp4420.dorm_messaging.model.ReplyService;

public class TestCommunityStorage {

    @TempDir
    Path directory;

    private AccountRegistry accounts = new AccountRegistry();
    private LoginService login = new LoginService(this.accounts);
    private DormService dorms = new DormService(this.login);
    private MessageService messages = new MessageService(this.login, this.dorms);
    private ReplyService replies = new ReplyService(this.login, this.messages);

    private void load(Properties data) {
        new CommunityStorage().load(this.accounts, this.dorms,
            this.messages, this.replies, data);
    }

    private void createResident() {
        this.accounts.register(1L, "alex", "DormPassword123");
        this.login.login("alex", "DormPassword123");
        this.dorms.selectDorm(Dorm.NORTH_HALL);
    }

    @Test
    public void testCommunitySurvivesXmlRoundTrip() throws IOException {
        this.createResident();
        Message originalMessage = this.messages.postMessage("Study tonight?");
        Reply originalReply = this.replies.postReply(originalMessage, "Yes!");

        this.accounts.register(2L, "sam", "AnotherPassword123");
        this.login.login("sam", "AnotherPassword123");
        this.dorms.selectDorm(Dorm.SOUTH_HALL);
        this.messages.postMessage("South hall meeting");

        Properties data = new Properties();
        new AccountStorage().saveAccounts(this.accounts, data);
        new CommunityStorage().save(this.dorms, this.messages, this.replies, data);

        LocalStorage file = new LocalStorage(this.directory.resolve("community.xml"));
        file.save(data);
        Properties loaded = file.load();

        this.accounts = new AccountStorage().loadAccounts(loaded);
        this.login = new LoginService(this.accounts);
        this.dorms = new DormService(this.login);
        this.messages = new MessageService(this.login, this.dorms);
        this.replies = new ReplyService(this.login, this.messages);
        this.load(loaded);

        this.login.login("alex", "DormPassword123");
        assertEquals(Dorm.NORTH_HALL, this.dorms.getSelectedDorm());
        assertEquals(1, this.messages.getFeed().size());

        Message restoredMessage = this.messages.getFeed().get(0);
        assertEquals(originalMessage.getId(), restoredMessage.getId());
        assertEquals(originalMessage.getContent(), restoredMessage.getContent());
        assertEquals(originalMessage.getPostedAt(), restoredMessage.getPostedAt());
        assertSame(this.accounts.findByUsername("alex"), restoredMessage.getAuthor());

        Reply restoredReply = this.replies.getReplies(restoredMessage).get(0);
        assertEquals(originalReply.getId(), restoredReply.getId());
        assertEquals(originalReply.getContent(), restoredReply.getContent());
        assertEquals(originalReply.getPostedAt(), restoredReply.getPostedAt());
        assertSame(restoredMessage, restoredReply.getParentMessage());
        assertSame(restoredMessage.getAuthor(), restoredReply.getAuthor());

        assertEquals(3L, this.messages.postMessage("Another post").getId());
        assertEquals(2L, this.replies.postReply(restoredMessage, "See you!").getId());

        this.login.login("sam", "AnotherPassword123");
        assertEquals(Dorm.SOUTH_HALL, this.dorms.getSelectedDorm());
        assertEquals("South hall meeting", this.messages.getFeed().get(0).getContent());
    }

    @Test
    public void testEmptyCommunityRoundTrip() {
        Properties data = new Properties();
        new CommunityStorage().save(this.dorms, this.messages, this.replies, data);
        this.load(data);

        assertTrue(this.dorms.getSelections().isEmpty());
        assertTrue(this.messages.getAllMessages().isEmpty());
        assertTrue(this.replies.getAllReplies().isEmpty());
    }

    @Test
    public void testMissingCommunityDataIsEmpty() {
        this.load(new Properties());

        assertTrue(this.messages.getAllMessages().isEmpty());
        assertTrue(this.replies.getAllReplies().isEmpty());
    }

    @Test
    public void testRejectNegativeRecordCount() {
        Properties data = new Properties();
        data.setProperty("messagecount", "-1");

        assertThrows(IllegalArgumentException.class, () -> this.load(data));
    }

    @Test
    public void testRejectMissingRecordField() {
        Properties data = new Properties();
        data.setProperty("messagecount", "1");

        assertThrows(IllegalArgumentException.class, () -> this.load(data));
    }

    @Test
    public void testRejectDormForUnknownAccount() {
        Properties data = new Properties();
        data.setProperty("dormcount", "1");
        data.setProperty("dorm.0.id", "99");
        data.setProperty("dorm.0.dorm", "NORTH_HALL");

        assertThrows(IllegalArgumentException.class, () -> this.load(data));
    }

    @Test
    public void testRejectInvalidDormRestoration() {
        assertThrows(IllegalArgumentException.class,
            () -> this.dorms.restoreSelection(0L, Dorm.NORTH_HALL));
        assertThrows(IllegalArgumentException.class,
            () -> this.dorms.restoreSelection(1L, null));
    }

    @Test
    public void testRejectNullMessageRestoration() {
        assertThrows(IllegalArgumentException.class,
            () -> this.messages.restoreMessage(null));
    }

    @Test
    public void testRejectDuplicateMessageRestoration() {
        this.createResident();
        Message message = this.messages.postMessage("Hello");

        assertThrows(IllegalArgumentException.class,
            () -> this.messages.restoreMessage(message));
    }

    @Test
    public void testRejectNullReplyRestoration() {
        assertThrows(IllegalArgumentException.class,
            () -> this.replies.restoreReply(null));
    }

    @Test
    public void testRejectDuplicateReplyRestoration() {
        this.createResident();
        Message message = this.messages.postMessage("Hello");
        Reply reply = this.replies.postReply(message, "Hi");

        assertThrows(IllegalArgumentException.class,
            () -> this.replies.restoreReply(reply));
    }

    @Test
    public void testRejectReplyWithUnavailableParent() {
        this.createResident();
        Message message = this.messages.postMessage("Hello");
        Reply reply = this.replies.postReply(message, "Hi");
        MessageService emptyMessages = new MessageService(this.login, this.dorms);
        ReplyService emptyReplies = new ReplyService(this.login, emptyMessages);

        assertThrows(IllegalArgumentException.class,
            () -> emptyReplies.restoreReply(reply));
    }
}