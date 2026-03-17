package com.ubb.domain;

import com.ubb.service.SocialNetworkService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

public class RacePage {
    private SocialNetworkService service;
    private User loggedUser;
    private BorderPane root;
    private VBox eventsList;

    public RacePage(SocialNetworkService service, User loggedUser) {
        this.service = service;
        this.loggedUser = loggedUser;
    }

    public void build(BorderPane root) {
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Header
        VBox header = createHeader();

        //center: events list
        VBox center = createEventsPanel();

        root.setTop(header);
        root.setCenter(center);

        BorderPane.setMargin(header, new Insets(0, 0, 20, 0));
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #2b8cd5; -fx-padding: 20;");

        Label title = new Label("🏁 Race Events");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: white;");

        Label subtitle = new Label("Subscribe to races!");
        subtitle.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 14px;");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private VBox createEventsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );

        Label title = new Label("🏆 Available Races");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setOnAction(e -> loadEvents());

        HBox titleBar = new HBox(10, title, refreshBtn);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(title, Priority.ALWAYS);

        Separator sep = new Separator();

        eventsList = new VBox(15);
        loadEvents();

        ScrollPane scroll = new ScrollPane(eventsList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        scroll.setPrefHeight(500);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        panel.getChildren().addAll(titleBar, sep, scroll);
        return panel;
    }

    private void loadEvents() {
        eventsList.getChildren().clear();

        try {
            Map<String, Event> allEvents = service.getAllEvents();
            List<RaceEvent> races = allEvents.values().stream().filter(e -> e instanceof RaceEvent).map(e -> (RaceEvent)e).toList();

            if (races.isEmpty()) {
                Label noRaces = new Label("No race events yet. Create one below!");
                noRaces.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
                eventsList.getChildren().add(noRaces);
                return;
            }

            for (RaceEvent race : races) {
                eventsList.getChildren().add(createRaceCard(race));
            }
        }
        catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            ex.printStackTrace();
        }
    }

    private VBox createRaceCard(RaceEvent race) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: #ecf0f1; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: #3498db; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
        );

        // Title
        Label name = new Label("🏁 " + race.getTitle());
        name.setFont(Font.font("System", FontWeight.BOLD, 18));
        name.setStyle("-fx-text-fill: #2c3e50;");

        // Spectators which receive notifications about the race
        HBox spectatorsBox = new HBox(10);
        spectatorsBox.setAlignment(Pos.CENTER_LEFT);

        try {
            Label spectatorsLabel = new Label("📋 Spectators: " + service.howManyUsersSubscribedToEvent(race.getId()) + " users");
            spectatorsLabel.setStyle("-fx-text-fill: #7f8c8d;");

            boolean isSubscribed = service.isUserSubscribedToEvent(loggedUser.getId(), race.getId());

            Button subscribeBtn = new Button(isSubscribed ? "❌ Unsubscribe" : "✅ Subscribe");
            subscribeBtn.setStyle(isSubscribed ?
                    "-fx-background-color: #e74c3c; -fx-text-fill: white;" :
                    "-fx-background-color: #27ae60; -fx-text-fill: white;"
            );

            subscribeBtn.setOnAction(e -> handleSubscription(race, isSubscribed));

            spectatorsBox.getChildren().addAll(spectatorsLabel, subscribeBtn);

        } catch (Exception ex) {
            spectatorsBox.getChildren().add(new Label("Error loading spectators!"));
        }

        // Action buttons
        HBox actions = new HBox(10);

        Button startRaceBtn = new Button("🏁 Start Race");
        startRaceBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        startRaceBtn.setOnAction(e -> startRace(race));

        actions.getChildren().addAll(startRaceBtn);

        card.getChildren().addAll(name, new Separator(), spectatorsBox, actions);
        return card;
    }

    private void handleSubscription(RaceEvent race, boolean isSubscribed) {
        try {
            if (isSubscribed) {
                // Unsubscribe e permis oricui (dacă printr-o eroare a ajuns subscris)
                service.unsubscribeFromEvent(race.getId(), loggedUser.getId());
                //appendNotification("Unsubscribed from: " + race.getNume());
            } else {
                service.subscribeToEvent(race.getId(), loggedUser.getId());
                //appendNotification("✅ Subscribed to: " + race.getNume());
                //race.notifySubscribers(currentUser.getUsername() + " joined the race!");
            }
            loadEvents();
        }
        catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            ex.printStackTrace();
        }
    }

    private void startRace(RaceEvent race) {
        // Create race window
        Stage raceStage = new Stage();
        VBox raceLayout = new VBox(20);
        raceLayout.setPadding(new Insets(20));
        raceLayout.setAlignment(Pos.TOP_CENTER);
        raceLayout.setStyle("-fx-background-color: #2c3e50;");

        Label title = new Label("🏁 Racing: " + race.getTitle());
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: white;");

        TextArea raceLog = new TextArea();
        raceLog.setEditable(false);
        raceLog.setPrefHeight(400);
        raceLog.setStyle("-fx-font-family: monospace;");

        ProgressBar overallProgress = new ProgressBar(0);
        overallProgress.setPrefWidth(500);

        raceLayout.getChildren().addAll(title, raceLog, overallProgress);

        Scene scene = new Scene(raceLayout, 600, 500);
        raceStage.setScene(scene);
        raceStage.setTitle("Race in Progress");
        raceStage.show();

        // dam start la race cu threading
        runRaceConcurrently(race, raceLog, overallProgress);
    }

    private void runRaceConcurrently(RaceEvent race, TextArea raceLog, ProgressBar progressBar) {
        try {
            ExecutorService pool = Executors.newFixedThreadPool(4);

            // Task cu rezultat
            Future<String> future = pool.submit(() -> {
                return service.runRaceEvent(race.getId());
            });

            // Așteptăm rezultatul
            String result = future.get();
            raceLog.appendText(result + "\n");

            // Oprire corectă
            pool.shutdown();

            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        }
        catch (InterruptedException | ExecutionException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            ex.printStackTrace();
        }
    }
}
