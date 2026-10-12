package edu.westga.comp4420.dorm_messaging.test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edu.westga.comp4420.dorm_messaging.model.AppState;
import edu.westga.comp4420.dorm_messaging.model.Dorm;
import edu.westga.comp4420.dorm_messaging.model.Message;

public class TestAppState {

    @TempDir
    Path directory;

    @Test
    public void testSaveAndRestartApplication() throws IOException {
        Path file = this.directory.resolve("app.xml");
        AppState original = new AppState(file);

        assertNull(original.getLogin().getCurrentAccount());
        assertEquals(1L, original.register("alex", "password").getId());
        original.getLogin().login("alex", "password");
        original.getDorms().selectDorm(Dorm.NORTH_HALL);
        Message message = original.getMessages().postMessage("Hello");
        original.getReplies().postReply(message, "Welcome");
        original.save();

        AppState restored = new AppState(file);
        assertNull(restored.getLogin().getCurrentAccount());
        restored.getLogin().login("alex", "password");

        assertEquals(Dorm.NORTH_HALL, restored.getDorms().getSelectedDorm());
        Message restoredMessage = restored.getMessages().getFeed().get(0);
        assertEquals("Hello", restoredMessage.getContent());
        assertEquals("Welcome",
            restored.getReplies().getReplies(restoredMessage).get(0).getContent());
        assertEquals(2L, restored.register("sam", "other-password").getId());
    }

    @Test
    public void testRejectDuplicateRegistration() throws IOException {
        AppState state = new AppState(this.directory.resolve("app.xml"));
        state.register("alex", "password");

        assertThrows(IllegalArgumentException.class,
            () -> state.register("alex", "another-password"));
    }
}