package com.ubb;
import com.ubb.repo.*;
import com.ubb.service.SocialNetworkService;
import com.ubb.ui.MainViewController;
import com.ubb.utils.PasswordHasher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/main-view.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 800);

        Properties props = new Properties();
        try {
            props.load(new FileReader("bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }

        DatabaseUserRepository repo = new DatabaseUserRepository(props);
        FriendshipRepository repo_friends = new DatabaseFriendshipRepository(props);
        DatabaseCardRepository repo_card = new DatabaseCardRepository(props);
        DatabaseEventRepository repo_event = new DatabaseEventRepository(props);
        DatabaseMessageRepository repo_messages = new DatabaseMessageRepository(props, repo);
        FriendRequestRepository repo_friend_requests = new FriendRequestRepository(props);

        SocialNetworkService service = new SocialNetworkService(repo, repo_friends, repo_card, repo_event, repo_messages, repo_friend_requests);

        MainViewController controller = loader.getController();
        controller.setService(service);

        stage.setTitle("Duck Social Network");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        launch();
    }
}
