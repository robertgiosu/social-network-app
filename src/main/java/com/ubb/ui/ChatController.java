package com.ubb.ui;

import com.ubb.domain.Message;
import com.ubb.domain.User;
import com.ubb.service.SocialNetworkService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.util.List;

public class ChatController {

    @FXML private Label replyLabel;
    @FXML private ListView<String> messagesList;
    @FXML private TextArea messageBox;
    @FXML private Label chatTitle;

    private SocialNetworkService service;
    private User loggedUser;
    private User otherUser;

    public void setService(SocialNetworkService service, User loggedUser, User otherUser) {
        this.service = service;
        this.loggedUser = loggedUser;
        this.otherUser = otherUser;

        chatTitle.setText("Chat with " + otherUser.getUserName());
        loadConversation();
    }

    /**
     * Sends a message to the other user.
     */
    public void onSend() {
        String text = messageBox.getText().trim();
        if (text.isEmpty()) return;

        service.sendMessage(loggedUser.getId(),
                List.of(otherUser.getId()),
                text,
                null);

        messageBox.clear();
        loadConversation();
    }

    /**
     * Loads the conversation between the logged user and the other user.
     */
    @FXML
    private void loadConversation() {
        messagesList.getItems().clear();

        for (Message m : service.getConversation(loggedUser.getId(), otherUser.getId())) {
            String prefix = m.getFrom().getId().equals(loggedUser.getId()) ? "Me: " : otherUser.getUserName() + ": ";
            messagesList.getItems().add(prefix + m.getMessage());
        }
    }
}
