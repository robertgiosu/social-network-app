package com.ubb.ui;

import com.ubb.domain.*;
import com.ubb.service.FriendRequestEventBus;
import com.ubb.service.FriendRequestObserver;
import com.ubb.service.SocialNetworkService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FriendManagementController implements FriendRequestObserver {
    @FXML private TableView<UserTableRow> usersTable;
    @FXML private TableColumn<UserTableRow, String> idColumn;
    @FXML private TableColumn<UserTableRow, String> usernameColumn;
    @FXML private TableColumn<UserTableRow, String> typeColumn;
    @FXML private ListView<User> friendsList;
    @FXML private ListView<FriendRequest> requestsList;
    private SocialNetworkService service;
    private User loggedUser;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("Id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("Username"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("Type"));
    }

    public void setService(SocialNetworkService service, User loggedUser) {
        this.service = service;
        this.loggedUser = loggedUser;
        FriendRequestEventBus.subscribe(this);
        loadUsersTable();
        refreshFriends();
    }

    /**
     * Manages the send friend request button.
     */
    @FXML
    private void onSendRequest() {
        if (usersTable.getSelectionModel().getSelectedItem() == null) return;
        String selectedUserId = usersTable.getSelectionModel().getSelectedItem().getId();
        User selectedUser = service.getUser(selectedUserId);
        service.sendFriendRequest(loggedUser.getId(), selectedUser.getId());
    }

    /**
     * Manages the accept friend request button.
     */
    @FXML
    private void onAcceptRequest() {
        FriendRequest req = requestsList.getSelectionModel().getSelectedItem();
        if (req == null) return;
        service.acceptFriendRequest(req.getFromId(), req.getToId());
    }

    /**
     * Manages the reject friend request button.
     */
    @FXML
    private void onRejectRequest() {
        FriendRequest req = requestsList.getSelectionModel().getSelectedItem();
        if (req == null) return;
        service.rejectFriendRequest(req.getFromId(), req.getToId());
    }

    /**
     * Refreshes everything when a friend request status changes.
     */
    @Override
    public void onFriendRequestChanged() {
        loadAll();
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

    private void loadAll() {
        loadUsersTable();
        refreshFriends();
        loadFriendRequests();
    }

    /**
     * Loads the users table with all the users except the logged user.
     */
    private void loadUsersTable() {
        if (service == null)
            return;
        List<UserTableRow> users = service.getAllUsers().
                stream().
                filter(u -> ! u.getId().equals(loggedUser.getId())).
                map(this::toRow).
                toList();
        usersTable.setItems(FXCollections.observableArrayList(users));
    }

    /**
     * Loads the friends list with all the friends of the logged user.
     */
    private void refreshFriends() {
        Set<String> idsOfFriendsOfLoggedUser = service.getFriendsIds(loggedUser.getId());
        if (idsOfFriendsOfLoggedUser == null) return;
        List<User> friends = new ArrayList<>();
        for (String id : idsOfFriendsOfLoggedUser) {
            User u = service.getUser(id);
            friends.add(u);
        }
        friendsList.setItems(FXCollections.observableArrayList(friends));
    }

    /**
     * Loads the friend requests list with all the friend requests sent by the logged user.
     */
    private void loadFriendRequests() {
        requestsList.setItems(FXCollections.observableArrayList(service.getPendingFriendRequests(loggedUser.getId())));
    }
}
