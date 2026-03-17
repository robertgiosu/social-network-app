package com.ubb.domain;

import com.ubb.service.SocialNetworkService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class ProfilePage {
    private SocialNetworkService service;
    private User loggedUser;
    private BorderPane root;
    private VBox friendsList;
    private Label statsLabel;

    public ProfilePage(SocialNetworkService service, User loggedUser) {
        this.service = service;
        this.loggedUser = loggedUser;
    }

    public void build(BorderPane root) {
        root.getChildren().clear();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        //header with user info
        VBox header = createHeader();

        //middle
        HBox middle = new HBox(20);
        middle.getChildren().addAll(
                createUserInfoPanel(),
                createFriendsPanel()
        );
        HBox.setHgrow(createFriendsPanel(), Priority.ALWAYS);

        //bottom
        VBox statistics = createStatsPanel();

        root.setTop(header);
        root.setCenter(middle);
        root.setBottom(statistics);

        BorderPane.setMargin(header, new Insets(0, 0, 20, 0));
        BorderPane.setMargin(middle, new Insets(0, 0, 20, 0));
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #3a5168; -fx-padding: 20;");

        //picture (first letter of username)
        Label avatar = new Label(loggedUser.getUserName().substring(0, 1).toUpperCase());
        avatar.setStyle(
                "-fx-background-color: #3498db; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 48px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 50; " +
                        "-fx-min-width: 100; " +
                        "-fx-min-height: 100; " +
                        "-fx-alignment: center;"
        );

        // Username
        Label username = new Label(loggedUser.getUserName());
        username.setFont(Font.font("System", FontWeight.BOLD, 28));
        username.setStyle("-fx-text-fill: white;");

        // User type
        String userType = loggedUser.getType().equals(UserType.PERSON) ? "👤 Person User" : "🦆 Duck User";
        Label typeBadge = new Label(userType);
        typeBadge.setStyle(
                "-fx-background-color: #e74c3c; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 5 15; " +
                        "-fx-background-radius: 15; " +
                        "-fx-font-size: 12px;"
        );

        //occupation
        if(loggedUser.getType().equals(UserType.PERSON)) {
            Label occupation = new Label(((Person) loggedUser).getOccupation());
            occupation.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 14px;");
            header.getChildren().addAll(avatar, username, occupation, typeBadge);
        }
        else {
            header.getChildren().addAll(avatar, username, typeBadge);
        }
        return header;
    }

    private VBox createUserInfoPanel() {
        VBox userInfoPanel = new VBox(15);
        userInfoPanel.setPadding(new Insets(20));
        userInfoPanel.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        userInfoPanel.setPrefWidth(350);

        Label title = new Label("📋 User Information");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Separator sep = new Separator();

        VBox details = new VBox(10);

        // Common info
        details.getChildren().addAll(
                createInfoRow("🆔 User ID:", String.valueOf(loggedUser.getId())),
                createInfoRow("👤 Username:", loggedUser.getUserName())
        );

        // Specific info based on type
        if (loggedUser.getType().equals(UserType.SWIMMING)) {
            Duck duck = (Duck) loggedUser;
            details.getChildren().addAll(
                    createInfoRow("🦆 Duck Type:", "Swimming"),
                    createInfoRow("⚡ Speed:", String.format("%.2f", duck.getSpeed())),
                    createInfoRow("💪 Resistance:", String.format("%.2f", duck.getEndurance()))
            );
        }
        else if (loggedUser.getType().equals(UserType.FLYING)) {
            Duck duck = (Duck) loggedUser;
            details.getChildren().addAll(
                    createInfoRow("🦆 Duck Type:", "Flying"),
                    createInfoRow("⚡ Speed:", String.format("%.2f", duck.getSpeed())),
                    createInfoRow("💪 Resistance:", String.format("%.2f", duck.getEndurance()))
            );
        }
        else if (loggedUser.getType().equals(UserType.FLYING_AND_SWIMMING)) {
            Duck duck = (Duck) loggedUser;
            details.getChildren().addAll(
                    createInfoRow("🦆 Duck Type:", "Flying and Swimming"),
                    createInfoRow("⚡ Speed:", String.format("%.2f", duck.getSpeed())),
                    createInfoRow("💪 Resistance:", String.format("%.2f", duck.getEndurance()))
            );
        }
        else if (loggedUser.getType().equals(UserType.PERSON)) {
            Person person = (Person) loggedUser;
            details.getChildren().addAll(
                    createInfoRow("📝 First Name:", person.getFirstName()),
                    createInfoRow("📝 Last Name:", person.getLastName()),
                    createInfoRow("💼 Occupation:", person.getOccupation())
            );
        }
        userInfoPanel.getChildren().addAll(title, sep, details);
        return userInfoPanel;
    }

    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
        labelNode.setPrefWidth(150);

        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-text-fill: #2c3e50;");
        valueNode.setWrapText(true);

        row.getChildren().addAll(labelNode, valueNode);
        return row;
    }

    private VBox createFriendsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );

        Label title = new Label("👥 Friends");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Separator sep = new Separator();

        friendsList = new VBox(10);

        //added
        friendsList.setFillWidth(true);

        loadFriends();


        ScrollPane scroll = new ScrollPane(friendsList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        panel.getChildren().addAll(title, sep, scroll);
        return panel;
    }

    private void loadFriends() {
        friendsList.getChildren().clear();

        List<User> friends = service.getFriends(loggedUser.getId());

        if (friends.isEmpty()) {
            Label noFriends = new Label("No friends yet. Send some friend requests!");
            noFriends.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
            friendsList.getChildren().add(noFriends);
            return;
        }

        for (User friend : friends) {
            friendsList.getChildren().add(createFriendCard(friend));
        }
    }

    private HBox createFriendCard(User friend) {
        HBox card = new HBox(12);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(
                "-fx-background-color: #ecf0f1; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        );

        // Avatar
        Label avatar = new Label(friend.getUserName().substring(0, 1).toUpperCase());
        avatar.setStyle(
                "-fx-background-color: #3498db; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 20px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 25; " +
                        "-fx-min-width: 50; " +
                        "-fx-min-height: 50; " +
                        "-fx-alignment: center;"
        );

        // Info
        VBox info = new VBox(5);
        Label name = new Label(friend.getUserName());
        name.setFont(Font.font("System", FontWeight.BOLD, 14));
        name.setStyle("-fx-text-fill: #2c3e50;");

        String typeIcon = friend.getType().equals(UserType.PERSON) ? "👤" : "🦆";
        Label type = new Label(typeIcon);
        type.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        info.getChildren().addAll(name, type);
        HBox.setHgrow(info, Priority.ALWAYS);

        card.getChildren().addAll(avatar, info);

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #d5dbdb; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: #ecf0f1; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        ));

        return card;
    }

    private VBox createStatsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );

        Label title = new Label("📊 Statistics");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Separator sep = new Separator();

        statsLabel = new Label();
        loadStats();

        panel.getChildren().addAll(title, sep, statsLabel);
        return panel;
    }

    private void loadStats() {
        List<User> friends = service.getFriends(loggedUser.getId());
        List<FriendRequest> pending = service.getPendingFriendRequests(loggedUser.getId());

        long duckFriends = friends
                .stream()
                .filter(f -> f.getType().equals(UserType.SWIMMING) || f.getType().equals(UserType.FLYING) || f.getType().equals(UserType.FLYING_AND_SWIMMING))
                .count();
        long personFriends = friends
                .stream()
                .filter(f -> f.getType().equals(UserType.PERSON))
                .count();

        String stats = String.format(
                "👥 Total Friends: %d\n" +
                        "🦆 Duck Friends: %d\n" +
                        "👤 Person Friends: %d\n" +
                        "📬 Pending Requests: %d",
                friends.size(),
                duckFriends,
                personFriends,
                pending.size()
        );

        statsLabel.setText(stats);
        statsLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px; -fx-line-spacing: 5px;");
    }

    public BorderPane getContent() {
        return root;
    }

    public void refresh() {
        loadFriends();
        loadStats();
    }

    public String getTitle() {
        return "Profile - " + loggedUser.getUserName();
    }
}
