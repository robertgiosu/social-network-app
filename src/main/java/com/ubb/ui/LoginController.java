package com.ubb.ui;

import com.ubb.domain.User;
import com.ubb.service.SocialNetworkService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private SocialNetworkService service;
    private Stage chatStage;
    private User otherUser;

    public void setService(SocialNetworkService service, Stage chatStage, User otherUser) {
        this.service = service;
        this.chatStage = chatStage;
        this.otherUser = otherUser;
    }

    /**
     * Attempts to login the user.
     */
    @FXML
    public void onLogin() {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();

            User logged = service.login(username, password);

            // dupa login, deschid chat-ul
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat-view.fxml"));
            Parent root = loader.load();

            ChatController ctrl = loader.getController();
            ctrl.setService(service, logged, otherUser);

            chatStage.setScene(new Scene(root));
            chatStage.setTitle("Chat – " + logged.getUserName());
            chatStage.show();

            // închidem fereastra de login
            ((Stage) usernameField.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Login failed: " + e.getMessage());
        }
    }
}
