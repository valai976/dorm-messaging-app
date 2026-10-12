package edu.westga.comp4420.dorm_messaging.test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edu.westga.comp4420.dorm_messaging.model.LocalStorage;

public class TestLocalStorage {

    @TempDir
    Path directory;

    @Test
    public void testRejectNullFile() {
        assertThrows(IllegalArgumentException.class,
            () -> new LocalStorage(null));
    }

    @Test
    public void testMissingFileReturnsEmptyData() throws IOException {
        LocalStorage storage =
            new LocalStorage(this.directory.resolve("missing.xml"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void testSaveCreatesParentDirectories() throws IOException {
        Path file = this.directory.resolve("nested/data.xml");
        Properties data = new Properties();
        data.setProperty("username", "alex");

        LocalStorage storage = new LocalStorage(file);
        storage.save(data);

        assertTrue(Files.exists(file));
        assertEquals("alex", storage.load().getProperty("username"));
    }

    @Test
    public void testSaveReplacesPreviousValues() throws IOException {
        LocalStorage storage =
            new LocalStorage(this.directory.resolve("data.xml"));
        Properties original = new Properties();
        original.setProperty("old", "original");
        storage.save(original);

        Properties replacement = new Properties();
        replacement.setProperty("new", "updated");
        storage.save(replacement);

        assertEquals(replacement, storage.load());
    }

    @Test
    public void testRejectMalformedXml() throws IOException {
        Path file = this.directory.resolve("broken.xml");
        Files.writeString(file, "This is not XML.");
        LocalStorage storage = new LocalStorage(file);

        assertThrows(IOException.class, () -> storage.load());
    }

    @Test
    public void testReportWriteFailure() throws IOException {
        Path parentFile = this.directory.resolve("file.txt");
        Files.writeString(parentFile, "A file cannot be a directory.");
        LocalStorage storage =
            new LocalStorage(parentFile.resolve("data.xml"));

        assertThrows(IOException.class,
            () -> storage.save(new Properties()));
    }
}