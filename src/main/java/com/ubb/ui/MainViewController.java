package com.ubb.ui;

import com.ubb.domain.*;
import com.ubb.exceptions.DuplicateEntityException;
import com.ubb.exceptions.EntityNotFoundException;
import com.ubb.exceptions.ValidationException;
import com.ubb.service.SocialNetworkService;
import com.ubb.validation.DuckValidator;
import com.ubb.validation.PersonValidator;
import com.ubb.validation.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MainViewController {
    @FXML public Label labelForId;
    @FXML public TextField idField;
    @FXML public Label labelForUsername;
    @FXML public TextField usernameField;
    @FXML public Label labelForFirstname;
    @FXML public TextField firstnameField;
    @FXML public Label labelForLastname;
    @FXML public TextField lastnameField;
    @FXML public Label labelForOccupation;
    @FXML public TextField occupationField;
    @FXML public Label labelForDuckType;
    @FXML public TextField duckTypeField;
    @FXML public Label labelForSpeed;
    @FXML public TextField speedField;
    @FXML public Label labelForEndurance;
    @FXML public TextField enduranceField;
    @FXML public Label pageLabel;
    @FXML public Label labelForRelationship1;
    @FXML public TextField relationship1Field;
    @FXML public Label labelForRelationship2;
    @FXML public TextField relationship2Field;
    @FXML public TextArea textBox;
    @FXML public Label labelForCommunities;
    @FXML private TextField messageRecipientIdField;
    @FXML private Label labelForMessageRecipientId;
    @FXML private Label labelForChat;
    @FXML private TableView<UserTableRow> usersTable;
    @FXML private TableColumn<UserTableRow, String> idColumn;
    @FXML private TableColumn<UserTableRow, String> usernameColumn;
    @FXML public TableColumn<UserTableRow, String> firstnameColumn;
    @FXML public TableColumn<UserTableRow, String> lastnameColumn;
    @FXML public TableColumn<UserTableRow, String> occupationColumn;
    @FXML private TableColumn<UserTableRow, String> typeColumn;
    @FXML private TableColumn<UserTableRow, String> speedColumn;
    @FXML private TableColumn<UserTableRow, String> enduranceColumn;
    @FXML private ComboBox<String> duckTypeCBox;
    @FXML private Label labelForDuckCBox;
    private SocialNetworkService service;

    private Validator<Person> personValidator = new PersonValidator();
    private Validator<Duck> duckValidator = new DuckValidator();

    private int currentPage = 1;
    private final int pageSize = 5;
    private int totalPages;

    @FXML
    public void initialize() {
        configureColumns();
        configureTextBox();
        configureComboBox();
        configureUserFields();
    }

    private void configureColumns() {
        idColumn.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getId()));
        usernameColumn.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getUsername()));

        firstnameColumn.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getFirstName()));
        lastnameColumn.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getLastName()));
        occupationColumn.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getOccupation()));

        typeColumn.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getType()));
        speedColumn.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getSpeed()));
        enduranceColumn.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getEndurance()));
    }

    private void configureTextBox() {
        textBox.setWrapText(true);
        textBox.setEditable(false);
    }

    private void configureUserFields() {
        labelForId.setText("User ID:");
        labelForUsername.setText("Username:");
        labelForFirstname.setText("First name (Person):");
        labelForLastname.setText("Last name (Person):");
        labelForOccupation.setText("Occupation (Person):");
        labelForDuckType.setText("Duck type (Duck):");
        labelForSpeed.setText("Speed (Duck):");
        labelForEndurance.setText("Endurance (Duck):");
        labelForRelationship1.setText("ID1 of User:");
        labelForRelationship2.setText("ID2 of User:");
        labelForCommunities.setText("Communities");
        labelForChat.setText("Chatroom");
        labelForMessageRecipientId.setText("Recipient ID:");
    }

    /**
     * Configures the combo box with 3 options: All, Flying, Swimming, Flying and Swimming.
     */
    private void configureComboBox() {
        labelForDuckCBox.setText("Select duck type:");
        duckTypeCBox.getItems().addAll("All users", "Flying", "Swimming", "Flying and swimming");
    }

    public void setService(SocialNetworkService serv) {
        this.service = serv;
        initializePagination();
        initializeData();
        updatePageLabel();
    }

    /**
     * Computes the total number of pages and sets the total number of pages in the pagination control.
     */
    private void initializePagination() {
        totalPages = (int) Math.ceil(service.getUserCount() / (double) pageSize);
    }

    private void initializeData() {
        loadPage(1);
        handleDuckTypeSelection();
    }

    private void updatePageLabel() {
        pageLabel.setText("Page " + currentPage + " / " + totalPages);
    }

    /**
     * Loads the given page into the table.
     * @param page the page to load.
     */
    private void loadPage(int page) {
        if (page < 1 || page > totalPages) return;
        currentPage = page;
        List<User> pageUsers = service.getPage(page, pageSize);
        List<UserTableRow> rows = pageUsers.stream().map(this::toRow).toList();
        usersTable.setItems(FXCollections.observableArrayList(rows));
        updatePageLabel();
    }

    private UserTableRow toRow(User u) {
        if (u.getType().equals(UserType.PERSON)) {
            Person p = (Person) u;
            return new UserTableRow(
                    p.getId(), p.getUserName(),
                    p.getFirstName(), p.getLastName(), p.getOccupation(),
                    "Person", "", ""
            );
        }
        else if (u.getType().equals(UserType.FLYING)) {
            FlyingDuck d = (FlyingDuck) u;
            return new UserTableRow(
                    d.getId(), d.getUserName(),
                    "", "", "",
                    "Flying", String.valueOf(d.getSpeed()), String.valueOf(d.getEndurance())
            );
        }
        else if (u.getType().equals(UserType.SWIMMING)) {
            SwimmingDuck d = (SwimmingDuck) u;
            return new UserTableRow(
                    d.getId(), d.getUserName(),
                    "", "", "",
                    "Swimming", String.valueOf(d.getSpeed()), String.valueOf(d.getEndurance())
            );
        }
        else if (u.getType().equals(UserType.FLYING_AND_SWIMMING)) {
            FlyingAndSwimmingDuck d = (FlyingAndSwimmingDuck) u;
            return new UserTableRow(
                    d.getId(), d.getUserName(),
                    "", "", "",
                    "Flying and swimming", String.valueOf(d.getSpeed()), String.valueOf(d.getEndurance())
            );
        }
        throw new IllegalStateException("Unknown user type: " + u.getClass());
    }

    /**
     * Maps the selection of a duck type in the combo box to the corresponding filtered table and updates the table.
     */
    private void handleDuckTypeSelection() {
        duckTypeCBox.setOnAction(e -> {
            String selected = duckTypeCBox.getValue();
            System.out.println("Selected: " + selected);
            if (selected.equals("Flying")) {
                List<UserTableRow> rows = service.getAllFlyingDucks().stream().map(this::toRow).toList();
                usersTable.setItems(FXCollections.observableArrayList(rows));
            }
            else if (selected.equals("Swimming")) {
                List<UserTableRow> rows = service.getAllSwimmingDucks().stream().map(this::toRow).toList();
                usersTable.setItems(FXCollections.observableArrayList(rows));
            }
            else if (selected.equals("Flying and swimming")) {
                List<UserTableRow> rows = service.getAllFlyingAndSwimmingDucks().stream().map(this::toRow).toList();
                usersTable.setItems(FXCollections.observableArrayList(rows));
            }
            else if (selected.equals("All users"))
                loadUsers();
        });
    }

    /**
     * Loads the next page of users into the table, if there is a next page.
     */
    @FXML
    private void onNextPage() {
        if (currentPage < totalPages)
            loadPage(currentPage + 1);
    }

    /**
     * Loads the previous page of users into the table, if there is a previous page.
     */
    @FXML
    private void onPreviousPage() {
        if (currentPage > 1)
            loadPage(currentPage - 1);
    }

    /**
     * Adds a person to the social network.
     */
    @FXML
    public void addPerson() {
        try {
            String id = idField.getText();
            String username = usernameField.getText();
            String firstName = firstnameField.getText();
            String lastName = lastnameField.getText();
            String occupation = occupationField.getText();
            Person p = new Person(id, username, firstName, lastName, occupation);
            personValidator.validate(p);
            service.addUser(p);
            idField.clear();
            usernameField.clear();
            firstnameField.clear();
            lastnameField.clear();
            occupationField.clear();
            loadPage(1);
        }
        catch (ValidationException | DuplicateEntityException exc) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("User invalid or duplicate");
            alert.setContentText(exc.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Adds a duck to the social network.
     */
    @FXML
    public void addDuck() {
        try {
            String id = idField.getText();
            String username = usernameField.getText();
            String type = duckTypeField.getText();
            String speed = speedField.getText();
            String endurance = enduranceField.getText();
            if (type.equalsIgnoreCase("Swimming")) {
                SwimmingDuck sd = new SwimmingDuck(id, username, Double.parseDouble(speed), Double.parseDouble(endurance));
                duckValidator.validate(sd);
                service.addUser(sd);
            }
            else if (type.equalsIgnoreCase("Flying")) {
                FlyingDuck fd = new FlyingDuck(id, username, Double.parseDouble(speed), Double.parseDouble(endurance));
                duckValidator.validate(fd);
                service.addUser(fd);
            }
            else if (type.equalsIgnoreCase("Flying and swimming")) {
                FlyingAndSwimmingDuck fsd = new FlyingAndSwimmingDuck(id, username, Double.parseDouble(speed), Double.parseDouble(endurance));
                duckValidator.validate(fsd);
                service.addUser(fsd);
            }
            idField.clear();
            usernameField.clear();
            duckTypeField.clear();
            speedField.clear();
            enduranceField.clear();
            loadPage(1);
        }
        catch (ValidationException | DuplicateEntityException exc) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("User invalid or duplicate");
            alert.setContentText(exc.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Removes a user from the social network.
     */
    @FXML
    public void removeUser() {
        try {
            String id = idField.getText();
            service.removeUser(id);
            idField.clear();
            loadPage(1);
        }
        catch (EntityNotFoundException exc) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("User not found");
            alert.setContentText(exc.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Adds a friendship between two users, extracting the IDs from the relationship text fields.
     */
    @FXML
    public void addRelationship() {
        try {
            String id1 = relationship1Field.getText();
            String id2 = relationship2Field.getText();
            service.addFriendship(id1, id2);
            textBox.setText("Friendship added between " + id1 + " and " + id2);
            relationship1Field.clear();
            relationship2Field.clear();
        }
        catch (EntityNotFoundException exc) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Users do not exist");
            alert.setContentText(exc.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Deletes a friendship between two users, extracting the IDs from the relationship text fields.
     */
    @FXML
    public void removeRelationship() {
        try {
            String id1 = relationship1Field.getText();
            String id2 = relationship2Field.getText();
            service.removeFriendship(id1, id2);
            textBox.setText("Friendship removed  between " + id1 + " and " + id2);
            relationship1Field.clear();
            relationship2Field.clear();
        }
        catch (EntityNotFoundException exc) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Users do not exist");
            alert.setContentText(exc.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Shows the number of communities in the social network.
     */
    @FXML
    public void nrOfCommunities() {
        String result = "Number of communities: " + service.numberOfCommunities();
        textBox.setText(result);
    }

    /**
     * Shows the most sociable community in the social network.
     */
    @FXML
    public void mostSociableCommunity() {
        List<String> idsOfUsersInCommunity = service.computeMostSociableFromAdj();
        if (idsOfUsersInCommunity == null || idsOfUsersInCommunity.isEmpty()) {
            textBox.setText("No communities");

        }
        else {
            String res = "Most sociable community (ids):\n";
            res += idsOfUsersInCommunity.stream().collect(Collectors.joining(", "));
            textBox.setText(res);
        }
    }

    /**
     * Shows all friendships in the social network.
     */
    @FXML
    private void showAllFriendships() {
        String result = "All friendships:\n";
        Map<String, Set<String>> friendships = service.getAdjacency();
        for (String user : friendships.keySet()) {
            result += "User with id " + user + ": " + friendships.get(user).stream().collect(Collectors.joining(", ")) + '\n';
        }
        textBox.setText(result);
    }

    @FXML
    private void openChatroom() {
        try {
            String otherUserId = messageRecipientIdField.getText();
            if (Objects.equals(otherUserId, ""))
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Enter a user ID to chat with!");
                alert.setContentText("No user ID entered!");
                alert.showAndWait();
                return;
            }

            User otherUser = service.getUser(otherUserId);
            Stage chatStage = new Stage();

            // deschidem fereastra de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();

            LoginController loginCtrl = loader.getController();
            loginCtrl.setService(service, chatStage, otherUser);

            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root, 400, 200));
            loginStage.setTitle("Login");
            loginStage.show();
            messageRecipientIdField.clear();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            ex.printStackTrace();
        }
    }

    /**
     * Opens the friends manager window.
     */
    @FXML
    private void openFriendsManager() {
        try {
            Stage friendsManagerStage = new Stage();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-for-friends-manager-view.fxml"));
            Parent root = loader.load();

            LoginForFriendManagementController loginCtrl = loader.getController();
            loginCtrl.setService(service, friendsManagerStage);

            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root, 400, 200));
            loginStage.setTitle("Login");
            loginStage.show();
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

    @FXML
    private void openProfile() {
        try {
            Stage profileStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-for-profile-view.fxml"));
            Parent root = loader.load();
            LoginForProfileViewController loginCtrl = loader.getController();
            loginCtrl.setService(service, profileStage);
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root, 400, 200));
            loginStage.setTitle("Login");
            loginStage.show();
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

    @FXML
    private void openRaceEvents() {
        try {
            Stage profileStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-for-race-view.fxml"));
            Parent root = loader.load();
            LoginForRaceViewController loginCtrl = loader.getController();
            loginCtrl.setService(service, profileStage);
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root, 400, 200));
            loginStage.setTitle("Login");
            loginStage.show();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Loads all users into the table.
     */
    private void loadUsers() {
        if (service == null)
            return;
        List<UserTableRow> rows = service.getAllUsers().stream().map(this::toRow).toList();
        usersTable.setItems(FXCollections.observableArrayList(rows));
    }


}
