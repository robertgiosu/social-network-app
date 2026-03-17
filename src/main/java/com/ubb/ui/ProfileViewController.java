package com.ubb.ui;

import com.ubb.domain.Duck;
import com.ubb.domain.Person;
import com.ubb.domain.ProfilePage;
import com.ubb.domain.User;
import com.ubb.service.SocialNetworkService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ProfileViewController {
    private SocialNetworkService service;
    private User loggedUser;
    @FXML private BorderPane root;
    private ProfilePage profilePage;

    public void setService(SocialNetworkService service, User loggedUser) {
        this.service = service;
        this.loggedUser = loggedUser;
        profilePage = new ProfilePage(service, loggedUser);
        profilePage.build(root);
    }
}
