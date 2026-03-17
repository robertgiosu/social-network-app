package com.ubb.repo;

import com.ubb.domain.*;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseEventRepository {
    private String url;
    private String username;
    private String password;

    public DatabaseEventRepository(Properties props) {
        this.url = props.getProperty("jdbc.url");
        this.username = props.getProperty("jdbc.user");
        this.password = props.getProperty("jdbc.pass");
    }

    /**
     * Creates a new event.
     * @param eventId the ID of the event.
     * @param title the title of the event.
     */
    public void createEvent(String eventId, String title) {
        if (eventExists(eventId)) throw new IllegalArgumentException("Event already exists, id=" + eventId);
        String sql = "INSERT INTO events (event_id, title, type) VALUES (?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ps.setString(2, title);
            ps.setString(3, "basic");
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new race event.
     * @param eventId the ID of the race event.
     * @param title the title of the race event.
     */
    public void createRaceEvent(String eventId, String title) {
        if (eventExists(eventId)) throw new IllegalArgumentException("Event already exists, id=" + eventId);
        String sql = "INSERT INTO events (event_id, title, type) VALUES (?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ps.setString(2, title);
            ps.setString(3, "race");
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if an event with the specified ID exists.
     * @param eventId the ID of the event.
     * @return true if the event exists, false otherwise.
     */
    private boolean eventExists(String eventId) {
        String sql = "SELECT * FROM events WHERE event_id = ?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return false;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * Retrieves an event from the repository with the specified ID.
     * @param eventId the ID of the event.
     * @return the event with the specified ID, or null if the event does not exist.
     */
    private Event getEvent(String eventId) {
        if (!eventExists(eventId)) throw new IllegalArgumentException("Event does not exist, id=" + eventId);
        String sql = "SELECT * FROM events WHERE event_id = ?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            String type = rs.getString("type");
            if (type.equals("basic")) {
                return new Event(eventId, rs.getString("title"));
            }
            else if (type.equals("race")) {
                return new RaceEvent(eventId, rs.getString("title"));
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Checks if a user with the specified ID exists in the repository.
     * @param id The ID of the user to be checked.
     * @return true if a user with the given ID exists, false otherwise.
     */
    public boolean userExists(String id) {
        String sql = "SELECT * FROM users WHERE id=?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return false;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * Returns the number of users subscribed to an event.
     * @param eventId The ID of the event.
     * @return the number of users subscribed to the event.
     */
    public int howManyUsersSubscribedToEvent(String eventId) {
        String sql = "SELECT COUNT(*) FROM users_events WHERE event_id = ?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return 0;
            return rs.getInt(1);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a user is subscribed to an event.
     * @param userId The ID of the user.
     * @param eventId The ID of the event.
     * @return true if the user is subscribed to the event, false otherwise.
     */
    public boolean userIsSubscribedToEvent(String userId, String eventId) {
        String sql = "SELECT * FROM users_events WHERE user_id = ? AND event_id = ?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, eventId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return false;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * Subscribes a user to an event.
     * @param eventId the ID of the event.
     * @param userId the ID of the user to be subscribed.
     */
    public void subscribeToEvent(String eventId, String userId) {
        if (!eventExists(eventId)) throw new IllegalArgumentException("Event does not exist, id=" + eventId);
        if (!userExists(userId)) throw new IllegalArgumentException("User does not exist, id=" + userId);
        if (userIsSubscribedToEvent(userId, eventId))
            throw new IllegalArgumentException("User is already subscribed to event with id=" + eventId);

        String sql = "INSERT INTO users_events (user_id, event_id) VALUES (?, ?);";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, eventId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Unsubscribes a user from an event.
     * @param eventId the ID of the event.
     * @param userId the ID of the user to be unsubscribed.
     */
    public void unsubscribeFromEvent(String eventId, String userId) {
        if (!eventExists(eventId)) throw new IllegalArgumentException("Event does not exist, id=" + eventId);
        if (!userExists(userId)) throw new IllegalArgumentException("User does not exist, id=" + userId);
        if (!userIsSubscribedToEvent(userId, eventId))
            throw new IllegalArgumentException("User is already unsubscribed from event with id=" + eventId);
        String sql = "DELETE FROM users_events WHERE user_id = ? AND event_id = ?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, eventId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Notifies all subscribers of an event which is about to start.
     * @param eventId the ID of the event.
     */
    private void notifySubscribers(String eventId) {
        if (!eventExists(eventId)) throw new IllegalArgumentException("Event does not exist, id=" + eventId);

        String sql = "SELECT user_id FROM users_events WHERE event_id = ?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String user = rs.getString(1);
                System.out.printf("Notify (%s): %s%n", user, "New event!");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Runs a race event.
     * @param eventId the ID of the race event.
     */
    public String runRaceEvent(String eventId) {
        Event e = getEvent(eventId);
        if (e == null) throw new IllegalArgumentException("Event does not exist, id=" + eventId);
        if (!(e instanceof RaceEvent)) throw new IllegalArgumentException("Event is not a RaceEvent, id=" + eventId);
        List<SwimmingDuck> ducks = getAllUsers()
                .stream()
                .filter(u -> u.getType().equals(UserType.SWIMMING))
                .map(u -> (SwimmingDuck) u)
                .collect(Collectors.toList());
        RaceEvent re = (RaceEvent) e;
        notifySubscribers(eventId);
        return re.startEvent(ducks, getAllLanes());
    }

    /**
     * Returns all events.
     * @return a map where keys are event IDs and values are event objects.
     */
    public Map<String, Event> getAllEvents() {
        String sql = "SELECT * FROM events;";
        Map<String, Event> events = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("event_id");
                String type = rs.getString("type");
                if (type.equals("basic")) {
                    events.put(id, new Event(id, rs.getString("title")));
                }
                else if (type.equals("race")) {
                    events.put(id, new RaceEvent(id, rs.getString("title")));
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return events;
    }

    /**
     * Retrieves all users from the repository.
     * @return a list of all users present in the repository
     */
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users;";
        List<User> users = new java.util.ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String userName = rs.getString("username");
                String type = rs.getString("type");
                if (type.equals("PERSON")) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String occupation = rs.getString("occupation");
                    User p = new Person(id, userName, firstName, lastName, occupation);
                    users.add(p);
                }
                else if (type.equals("SWIMMING_DUCK")) {
                    double speed = rs.getDouble("speed");
                    double endurance = rs.getDouble("endurance");
                    User sd = new SwimmingDuck(id, userName, speed, endurance);
                    users.add(sd);
                }
                else if (type.equals("FLYING_DUCK")) {
                    double speed = rs.getDouble("speed");
                    double endurance = rs.getDouble("endurance");
                    User fd = new FlyingDuck(id, userName, speed, endurance);
                    users.add(fd);
                }
                else if (type.equals("FLYING_AND_SWIMMING_DUCK")) {
                    double speed = rs.getDouble("speed");
                    double endurance = rs.getDouble("endurance");
                    User fsd = new FlyingAndSwimmingDuck(id, userName, speed, endurance);
                    users.add(fsd);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public List<Lane> getAllLanes() {
        String sql = "SELECT * FROM lanes;";
        List<Lane> lanes = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                double distance = rs.getDouble("distance");
                Lane lane = new Lane(id, distance);
                lanes.add(lane);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lanes;
    }
}
