package com.ubb.ui;

import com.ubb.domain.User;
import com.ubb.service.SocialNetworkService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginForFriendManagementController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private SocialNetworkService service;
    private Stage friendsManagerStage;

    public void setService(SocialNetworkService service, Stage friendsManagerStage) {
        this.service = service;
        this.friendsManagerStage = friendsManagerStage;
    }

    @FXML
    private void onLogin() {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();

            //open friends manager window
            User logged = service.login(username, password);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/friend-management-view.fxml"));
            Parent root = loader.load();

            FriendManagementController ctrl = loader.getController();
            ctrl.setService(service, logged);

            friendsManagerStage.setScene(new Scene(root, 800, 600));
            friendsManagerStage.setTitle("Friend Management - " + logged.getUserName());
            friendsManagerStage.show();

            ((Stage) usernameField.getScene().getWindow()).close();
        }
        catch (Exception exc) {
            errorLabel.setText("Login failed: " + exc.getMessage());
            exc.printStackTrace();
        }
    }
}
