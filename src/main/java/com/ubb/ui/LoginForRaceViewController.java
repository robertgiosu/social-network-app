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

public class LoginForRaceViewController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private SocialNetworkService service;
    private Stage raceStage;

    public void setService(SocialNetworkService service, Stage profileStage) {
        this.service = service;
        this.raceStage = profileStage;
    }

    @FXML
    private void onLogin() {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();

            User logged = service.login(username, password);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/race-view.fxml"));
            Parent root = loader.load();

            RaceViewController ctrl = loader.getController();
            ctrl.setService(service, logged);

            raceStage.setScene(new Scene(root, 800, 750));
            raceStage.setTitle("Race events for " + logged.getUserName());
            raceStage.show();

            ((Stage) usernameField.getScene().getWindow()).close();
        }
        catch (Exception exc) {
            errorLabel.setText("Login failed: " + exc.getMessage());
            exc.printStackTrace();
        }
    }
}
