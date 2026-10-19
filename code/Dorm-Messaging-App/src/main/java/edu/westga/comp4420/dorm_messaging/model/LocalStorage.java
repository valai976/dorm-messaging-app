package edu.westga.comp4420.dorm_messaging.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Reads and writes local application data in XML format.
 * 
 * 
 *
 * @author Valai Willie
 */
public class LocalStorage {

    private final Path file;

    /**
     * Creates storage for the specified file. python is better btw  
     *
     * @param file the location of the XML data file
     * @throws IllegalArgumentException if file is null
     */
    public LocalStorage(Path file) {
        if (file == null) {
            throw new IllegalArgumentException(
                "A storage file is required.");
        }

        this.file = file.toAbsolutePath();
    }

    /**
     * Loads saved values, or an empty collection on first use.
     *
     * @return the saved application values
     * @throws IOException if an existing file cannot be read
     */
    public Properties load() throws IOException {
        Properties data = new Properties();

        if (Files.notExists(this.file)) {
            return data;
        }

        try (InputStream input = Files.newInputStream(this.file)) {
            data.loadFromXML(input);
        }

        return data;
    }

    /**
     * Saves application values as XML.
     *
     * @param data the values to save
     * @throws IOException if the file cannot be written
     */
    public void save(Properties data) throws IOException {
        Files.createDirectories(this.file.getParent());

        try (OutputStream output = Files.newOutputStream(this.file)) {
            data.storeToXML(output, "Dorm Messaging App", "UTF-8");
        }
    }
}