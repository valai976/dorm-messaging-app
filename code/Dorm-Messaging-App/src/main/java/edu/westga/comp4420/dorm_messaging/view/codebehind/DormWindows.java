package edu.westga.comp4420.dorm_messaging.view.codebehind;

import java.io.IOException;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import edu.westga.comp4420.dorm_messaging.model.AppState;
import edu.westga.comp4420.dorm_messaging.model.Dorm;
import edu.westga.comp4420.dorm_messaging.model.Message;
import edu.westga.comp4420.dorm_messaging.model.Reply;

/**
 * Displays account and community windows using the application's services.
 *
 * @author Valai Willie
 */
public class DormWindows {

    private static final String AUTHOR_SEPARATOR = " | ";
    private final AppState state;
    private final Stage accountWindow;
    private final Stage communityWindow;
    private final TextField username;
    private final PasswordField password;
    private final Label accountStatus;
    private final Label communityStatus;
    private final ComboBox<Dorm> dormChoice;
    private final ListView<Message> feed;
    private final ListView<String> replyList;
    private final TextArea postText;
    private final TextField replyText;

    /**
     * Creates the two application windows.
     *
     * @param state the loaded application services
     * @param accountWindow the initial JavaFX stage
     */
    public DormWindows(AppState state, Stage accountWindow) {
        this.state = state;
        this.accountWindow = accountWindow;
        this.communityWindow = new Stage();
        this.username = new TextField();
        this.password = new PasswordField();
        this.accountStatus = new Label();
        this.communityStatus = new Label();
        this.dormChoice = new ComboBox<>();
        this.feed = new ListView<>();
        this.replyList = new ListView<>();
        this.postText = new TextArea();
        this.replyText = new TextField();
        this.buildAccountWindow();
        this.buildCommunityWindow();
    }

    /** Shows the account window. */
    public void show() {
        this.accountWindow.show();
    }

    private void buildAccountWindow() {
        this.username.setPromptText("Username");
        this.password.setPromptText("Password");
        Button register = new Button("Register");
        Button login = new Button("Log in");
        Button save = new Button("Retry save");
        register.setOnAction(event -> this.register());
        login.setOnAction(event -> this.login());
        save.setOnAction(event -> this.save(this.accountStatus));
        this.password.setOnAction(event -> this.login());
        this.accountStatus.setWrapText(true);
        VBox root = new VBox(12, new Label("Dorm Messaging"),
            new Label("Create an account or log in."), this.username, this.password,
            new HBox(10, register, login, save), this.accountStatus);
        root.setPadding(new Insets(20));
        this.accountWindow.setTitle("Dorm Messaging - Accounts");
        this.accountWindow.setScene(new Scene(root, 470, 340));
    }

    private void register() {
        try {
            this.state.register(this.username.getText(), this.password.getText());
            this.password.clear();
            this.save(this.accountStatus);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            this.accountStatus.setText(exception.getMessage());
        }
    }

    private void login() {
        try {
            this.state.getLogin().login(this.username.getText(), this.password.getText());
            this.password.clear();
            this.dormChoice.setValue(this.state.getDorms().getSelectedDorm());
            this.refreshFeed();
            this.communityStatus.setText("Welcome, "
                + this.state.getLogin().getCurrentAccount().getUsername());
            this.communityWindow.show();
            this.accountWindow.hide();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            this.accountStatus.setText(exception.getMessage());
        }
    }

    private void buildCommunityWindow() {
        this.dormChoice.getItems().setAll(Dorm.values());
        this.dormChoice.setPromptText("Select your dorm");
        this.dormChoice.setOnAction(event -> this.selectDorm());
        Button logout = new Button("Log out");
        logout.setOnAction(event -> this.logout());
        Button save = new Button("Save");
        save.setOnAction(event -> this.save(this.communityStatus));
        this.configureFeed();
        this.communityStatus.setWrapText(true);
        VBox root = new VBox(10,
            new HBox(12, this.dormChoice, save, logout),
            new Label("Dorm posts"), this.feed, this.buildPostControls(),
            new Label("Replies to selected post"), this.replyList,
            this.buildReplyControls(), this.communityStatus);
        root.setPadding(new Insets(16));
        this.communityWindow.setTitle("Dorm Messaging - Community");
        this.communityWindow.setScene(new Scene(root, 760, 740));
        this.communityWindow.setOnCloseRequest(event -> {
            event.consume();
            this.logout();
        });
    }

    private void configureFeed() {
        this.feed.setPrefHeight(210);
        this.replyList.setPrefHeight(150);
        this.feed.setPlaceholder(new Label("No posts yet. Select a dorm and write a post."));
        this.feed.setCellFactory(list -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                this.setText(null);
                if (!empty && message != null) {
                    this.setText(message.getAuthor().getUsername() + AUTHOR_SEPARATOR
                        + message.getPostedAt() + "\n" + message.getContent());
                }
                this.setWrapText(true);
            }
        });
        this.feed.getSelectionModel().selectedItemProperty()
            .addListener((observable, previous, current) -> this.refreshReplies());
    }

    private VBox buildPostControls() {
        this.postText.setPromptText("Write a post for your dorm");
        this.postText.setPrefRowCount(2);
        this.postText.setWrapText(true);
        Button post = new Button("Post message");
        post.setOnAction(event -> this.postMessage());
        return new VBox(6, this.postText, post);
    }

    private HBox buildReplyControls() {
        this.replyText.setPromptText("Write a reply");
        this.replyText.setPrefWidth(540);
        Button reply = new Button("Reply");
        reply.setOnAction(event -> this.postReply());
        return new HBox(10, this.replyText, reply);
    }

    private void selectDorm() {
        if (this.dormChoice.getValue() == null) {
            return;
        }
        try {
            this.state.getDorms().selectDorm(this.dormChoice.getValue());
            this.refreshFeed();
            this.save(this.communityStatus);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            this.communityStatus.setText(exception.getMessage());
        }
    }

    private void refreshFeed() {
        this.feed.getItems().clear();
        this.replyList.getItems().clear();
        if (this.state.getDorms().getSelectedDorm() != null) {
            this.feed.getItems().setAll(this.state.getMessages().getFeed());
        }
    }

    private void refreshReplies() {
        this.replyList.getItems().clear();
        Message selected = this.feed.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        for (Reply reply : this.state.getReplies().getReplies(selected)) {
            this.replyList.getItems().add(reply.getAuthor().getUsername() + AUTHOR_SEPARATOR
                + reply.getPostedAt() + ": " + reply.getContent());
        }
    }

    private void postMessage() {
        try {
            Message message = this.state.getMessages().postMessage(this.postText.getText());
            this.postText.clear();
            this.refreshFeed();
            this.feed.getSelectionModel().select(message);
            this.save(this.communityStatus);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            this.communityStatus.setText(exception.getMessage());
        }
    }

    private void postReply() {
        try {
            Message selected = this.feed.getSelectionModel().getSelectedItem();
            this.state.getReplies().postReply(selected, this.replyText.getText());
            this.replyText.clear();
            this.refreshReplies();
            this.save(this.communityStatus);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            this.communityStatus.setText(exception.getMessage());
        }
    }

    private void logout() {
        this.state.getLogin().logout();
        this.postText.clear();
        this.replyText.clear();
        this.feed.getItems().clear();
        this.replyList.getItems().clear();
        this.accountStatus.setText("Logged out.");
        this.accountWindow.show();
        this.communityWindow.hide();
    }

    private void save(Label status) {
        try {
            this.state.save();
            status.setText("Saved. Use your username and password to log in when needed.");
        } catch (IOException exception) {
            status.setText("Changes are in memory but could not be saved. "
                + "Retry saving before closing. " + exception.getMessage());
        }
    }
}