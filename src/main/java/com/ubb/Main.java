package com.ubb;

import com.ubb.domain.*;
import com.ubb.repo.*;
import com.ubb.service.SocialNetworkService;
import com.ubb.ui.ConsoleUI;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
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
        ConsoleUI ui = new ConsoleUI(service);
        ui.run();
    }
}