package com.ubb.ui;

import com.ubb.domain.RacePage;
import com.ubb.domain.User;
import com.ubb.service.SocialNetworkService;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class RaceViewController {
    private SocialNetworkService service;
    private User loggedUser;
    @FXML private BorderPane root;
    private RacePage racePage;

    public void setService(SocialNetworkService service, User loggedUser) {
        this.service = service;
        this.loggedUser = loggedUser;
        racePage = new RacePage(service, loggedUser);
        racePage.build(root);
    }
}
