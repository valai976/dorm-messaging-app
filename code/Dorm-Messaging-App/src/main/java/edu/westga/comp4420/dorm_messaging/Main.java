package edu.westga.comp4420.dorm_messaging;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import edu.westga.comp4420.dorm_messaging.model.AppState;
import edu.westga.comp4420.dorm_messaging.view.codebehind.DormWindows;

/**
 * Starts the dorm messaging application.
 *
 * @author Valai Willie
 */
public class Main extends Application {

    /**
     * Loads saved data and opens the account window.
     *
     * @param primaryStage the initial application window
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            Path file = Path.of(System.getProperty("user.home"),
                ".dorm-messaging-app", "data.xml");
            AppState state = new AppState(file);
            new DormWindows(state, primaryStage).show();
        } catch (IOException | IllegalArgumentException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unable to load saved data");
            alert.setHeaderText("The application could not open its data file.");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
            Platform.exit();
        }
    }

    /**
     * Launches the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}