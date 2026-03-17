package com.ubb.service;

import com.ubb.domain.*;
import com.ubb.exceptions.EntityNotFoundException;
import com.ubb.repo.*;
import com.ubb.utils.PasswordHasher;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SocialNetworkService {
    private final UserRepository repo;
    private final FriendshipRepository repo_friends;
    private final DatabaseCardRepository repo_card;
    private final DatabaseEventRepository repo_event;
    private final DatabaseMessageRepository repo_message;
    private final FriendRequestRepository repo_friend_requests;
    private User loggedInUser = null;

    public SocialNetworkService(UserRepository repo, FriendshipRepository repo_friends, DatabaseCardRepository repo_card, DatabaseEventRepository repo_event, DatabaseMessageRepository repo_message, FriendRequestRepository repo_friend_requests) {
        this.repo = repo;
        this.repo_friends = repo_friends;
        this.repo_card = repo_card;
        this.repo_event = repo_event;
        this.repo_message = repo_message;
        this.repo_friend_requests = repo_friend_requests;
    }

    public void addUser(User user) {
        repo.addUser(user);
    }

    public void removeUser(String id) {
        repo.removeUser(id);
    }

    public User getUser(String id) {
        return repo.getUser(id);
    }

    public List<User> getPage(int pageNumber, int pageSize) {
        return repo.getUsersPage(pageNumber, pageSize);
    }

    public int getUserCount() {
        return repo.countUsers();
    }

    public List<User> getAllUsers() {
        return repo.getAllUsers();
    }

    public List<Duck> getAllDucks() {
        return repo.getAllDucks();
    }

    public List<FlyingDuck> getAllFlyingDucks() {
        return repo.getAllDucks()
                .stream()
                .filter(duck -> duck.getType().equals(UserType.FLYING))
                .map(duck -> (FlyingDuck)duck).collect(Collectors.toList());
    }

    public List<SwimmingDuck> getAllSwimmingDucks() {
        return repo.getAllDucks()
                .stream()
                .filter(duck -> duck.getType().equals(UserType.SWIMMING))
                .map(duck -> (SwimmingDuck)duck).collect(Collectors.toList());
    }

    public List<FlyingAndSwimmingDuck> getAllFlyingAndSwimmingDucks() {
        return repo.getAllDucks()
                .stream()
                .filter(duck -> duck.getType().equals(UserType.FLYING_AND_SWIMMING))
                .map(duck -> (FlyingAndSwimmingDuck)duck).collect(Collectors.toList());
    }

    public Map<String, Set<String>> getAdjacency() {
        return repo_friends.getAdjacency();
    }

    public void addFriendship(String id1, String id2) {
        if (!repo.userExists(id1)) throw new EntityNotFoundException("User with ID does not exist: " + id1);
        if (!repo.userExists(id2)) throw new EntityNotFoundException("User with ID does not exist: " + id2);
        repo_friends.addFriendship(id1, id2);
    }

    public void removeFriendship(String id1, String id2) {
        if (!repo.userExists(id1)) throw new EntityNotFoundException("User with ID does not exist: " + id1);
        if (!repo.userExists(id2)) throw new EntityNotFoundException("User with ID does not exist: " + id2);
        repo_friends.removeFriendship(id1, id2);
    }

    public Set<String> getFriendsIds(String id) {
        if (!repo.userExists(id)) throw new EntityNotFoundException("User with ID does not exist: " + id);
        return repo_friends.getFriendsIds(id);
    }

    public List<User> getFriends(String id) {
        if (!repo.userExists(id)) throw new EntityNotFoundException("User with ID does not exist: " + id);
        Set<String> friendsIds = repo_friends.getFriendsIds(id);
        if (friendsIds == null) {
            return Collections.emptyList();
        }
        else {
            return friendsIds.stream().map(this::getUser).toList();
        }
    }

    /**
     * Recursive DFS implementation.
     * @param adjacency the adjacency representation of the users and their relationships.
     * @param visited a set of IDs representing the nodes that have already been visited.
     * @param id the ID of the node to be visited.
     */
    private void DFS(Map<String, Set<String>> adjacency, Set<String> visited, String id) {
        visited.add(id);
        for (var friend : adjacency.get(id)) {
            if (!visited.contains(friend)) {
                DFS(adjacency, visited, friend);
            }
        }
    }

    /**
     * Counts the number of connected components in the social network.
     * @return number of connected components.
     */
    public int numberOfCommunities() {
        Map<String, Set<String>> adjacency = repo_friends.getAdjacency();
        Set<String> visited = new HashSet<>();
        int components = 0;
        for (var id : adjacency.keySet()) {
            if (visited.contains(id))
                    continue;
            components++;
            DFS(adjacency, visited, id);
        }
        return components;
    }

    /**
     * Computes the most sociable community from the adjacency relationships of the social network.
     * The sociability of a community is determined by computing the largest diameter (longest shortest
     * path between any two nodes) among all connected components. The method identifies the component
     * with the largest diameter.
     * @return a list of user IDs representing the most sociable community. If no communities
     *         exist, returns an empty list.
     */
    public List<String> computeMostSociableFromAdj() {
        Map<String, Set<String>> adj = repo_friends.getAdjacency();
        Set<String> visited = new HashSet<>();
        List<String> bestComponent = Collections.emptyList();
        int bestDiameter = -1;

        //determin fiecare componenta conexa prin BFS
        //pentru fiecare componenta conexa rulez BFS pentru fiecare nod
        //dupa fiecare BFS rulat pe un nod actualizez maximul local
        //dupa ce termin de rulat BFS pentru comp. conexa curenta aflu diametrul c.c. (este maximul dintre maximele locale)
        //dupa fiecare determinare a diametrului unei c.c. actualizez diametrul maxim si c.c. cu diam. maxim, dupa caz
        for (String start : adj.keySet()) {
            if (visited.contains(start)) continue;
            List<String> component = new ArrayList<>();
            Queue<String> q = new ArrayDeque<>();
            q.add(start);
            visited.add(start);
            while (!q.isEmpty()) {
                String cur = q.poll();
                component.add(cur);
                for (String nb : adj.getOrDefault(cur, Set.of())) {
                    if (!visited.contains(nb)) {
                        visited.add(nb);
                        q.add(nb);
                    }
                }
            }

            int diameter = computeDiameterForComponent(component, adj);
            if (diameter > bestDiameter) {
                bestDiameter = diameter;
                bestComponent = component;
            }
        }

        return bestComponent;
    }

    /**
     * Computes the diameter of a connected component in a graph. The diameter is defined
     * as the longest shortest path between any two nodes in the component.
     * @param component the connected component represented as a list of node identifiers.
     * @param adj the adjacency list representing the graph where keys are node identifiers
     *            and values are sets of adjacent nodes.
     * @return the diameter of the connected component.
     */
    private int computeDiameterForComponent(List<String> component, Map<String, Set<String>> adj) {
        int diameter = 0;
        for (String node : component) {
            Map<String, Integer> dist = bfsDistances(node, adj, component);
            int localMax = dist.values().stream().mapToInt(Integer::intValue).max().orElse(0);
            if (localMax > diameter) diameter = localMax;
        }
        return diameter;
    }

    /**
     * Computes the distances between all nodes in a connected component using BFS.
     * @param src the identifier of the source node.
     * @param adj the adjacency list representing the graph where keys are node identifiers
     * @param component the connected component represented as a list of node identifiers.
     * @return a map where keys are node identifiers and values are the distances between the source and the respective node.
     */
    private Map<String, Integer> bfsDistances(String src, Map<String, Set<String>> adj, List<String> component) {
        Set<String> compSet = new HashSet<>(component);
        Map<String, Integer> dist = new HashMap<>();
        Queue<String> q = new ArrayDeque<>();
        q.add(src);
        dist.put(src, 0);
        while (!q.isEmpty()) {
            String cur = q.poll();
            int cd = dist.get(cur);
            for (String nb : adj.getOrDefault(cur, Set.of())) {
                if (!compSet.contains(nb)) continue;
                if (!dist.containsKey(nb)) {
                    dist.put(nb, cd + 1);
                    q.add(nb);
                }
            }
        }
        return dist;
    }

    /**
     * Creates a new flock of ducks.
     * @param cardId the ID of the flock.
     * @param name the name of the flock.
     * @param <T> the type of ducks in the flock.
     */
    public <T extends Duck> void createCard(String cardId, String name) {
        repo_card.createCard(cardId, name);
    }

    /**
     * Adds a duck to a flock.
     * @param cardId the ID of the flock.
     * @param duckId the ID of the duck to be added.
     */
    public void addDuckToCard(String cardId, String duckId) {
        repo_card.addDuckToCard(cardId, duckId);
    }

    /**
     * Removes a duck from a flock.
     * @param cardId the ID of the flock.
     * @param duckId the ID of the duck to be removed.
     */
    public void removeDuckFromCard(String cardId, String duckId) {
        repo_card.removeDuckFromCard(cardId, duckId);
    }

    /**
     * Returns all flocks.
     * @return a map where keys are flock IDs and values are flock objects.
     */
    public Map<String, List<Duck>> getAllCards() {
        return repo_card.getAllCards();
    }

    /**
     * Creates a new event.
     * @param eventId the ID of the event.
     * @param title the title of the event.
     */
    public void createEvent(String eventId, String title) {
        repo_event.createEvent(eventId, title);
    }

    /**
     * Creates a new race event.
     * @param eventId the ID of the race event.
     * @param title the title of the race event.
     */
    public void createRaceEvent(String eventId, String title) {
        repo_event.createRaceEvent(eventId, title);
    }

    /**
     * Subscribes a user to an event.
     * @param eventId the ID of the event.
     * @param userId the ID of the user to be subscribed.
     */
    public void subscribeToEvent(String eventId, String userId) {
        repo_event.subscribeToEvent(eventId, userId);
    }

    /**
     * Unsubscribes a user from an event.
     * @param eventId the ID of the event.
     * @param userId the ID of the user to be unsubscribed.
     */
    public void unsubscribeFromEvent(String eventId, String userId) {
        repo_event.unsubscribeFromEvent(eventId, userId);
    }

    public int howManyUsersSubscribedToEvent(String eventId) {
        return repo_event.howManyUsersSubscribedToEvent(eventId);
    }

    public boolean isUserSubscribedToEvent(String userId, String eventId) {
        return repo_event.userIsSubscribedToEvent(userId, eventId);
    }

    /**
     * Runs a race event.
     * @param eventId the ID of the race event.
     */
    public String runRaceEvent(String eventId) {
        return repo_event.runRaceEvent(eventId);
    }

    /**
     * Returns all events.
     * @return a map where keys are event IDs and values are event objects.
     */
    public Map<String, Event> getAllEvents() {
        return repo_event.getAllEvents();
    }

    /**
     * Creates a new message and sends it to repository to be saved.
     * @param fromId the ID of the sender.
     * @param toIds a list of IDs of the recipients.
     * @param text the text of the message.
     * @param replyToId the ID of the message to which this message is a reply, or null if this message is not a reply.
     */
    public void sendMessage(String fromId, List<String> toIds, String text, Long replyToId) {
        User from = getUser(fromId);
        List<User> to = toIds.stream()
                             .map(this::getUser)
                             .toList();
        Message replyTo = replyToId == null ? null : repo_message.getMessage(replyToId);
        Message msg = new Message(null, from, to, text, LocalDateTime.now(), replyTo);
        Long msgId = repo_message.saveMessage(msg);
        msg.setId(msgId);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Authenticates the user using username and password.
     */
    public User login(String username, String password) {
        User user = repo.getByUsername(username);
        if (user == null)
            throw new RuntimeException("User not found.");

        String storedHash = repo.getPasswordHash(user.getId());
        String providedHash = PasswordHasher.hash(password);

        if (!storedHash.equals(providedHash))
            throw new RuntimeException("Incorrect password.");

        loggedInUser = user;
        return user;
    }

    /**
     * Returns the conversation between two users (all messages both ways).
     */
    public List<Message> getConversation(String user1, String user2) {
        return repo_message.getConversation(user1, user2)
                .stream()
                .sorted(Comparator.comparing(Message::getDate))
                .toList();
    }

    public void sendFriendRequest(String fromId, String toId) {
        repo_friend_requests.save(new FriendRequest(fromId, toId, FriendRequestStatus.PENDING));
        FriendRequestEventBus.notifyAllObservers();
    }

    public void acceptFriendRequest(String fromId, String toId) {
        repo_friend_requests.setStatus(fromId, toId, FriendRequestStatus.APPROVED);
        repo_friends.addFriendship(fromId, toId);
        FriendRequestEventBus.notifyAllObservers();
    }

    public void rejectFriendRequest(String fromId, String toId) {
        repo_friend_requests.setStatus(fromId, toId, FriendRequestStatus.REJECTED);
        FriendRequestEventBus.notifyAllObservers();
    }

    public List<FriendRequest> getPendingFriendRequests(String userId) {
        return repo_friend_requests.getPendingForUser(userId);
    }
}
