package com.ubb.repo;

import com.ubb.exceptions.EntityNotFoundException;

import java.sql.*;
import java.util.*;

public class DatabaseFriendshipRepository implements FriendshipRepository{
    private String url;
    private String username;
    private String password;

    public DatabaseFriendshipRepository(Properties props) {
        this.url = props.getProperty("jdbc.url");
        this.username = props.getProperty("jdbc.user");
        this.password = props.getProperty("jdbc.pass");
    }

    /**
     * Establishes a bi-directional friendship between two users.
     * @param id1 The ID of the first user.
     * @param id2 The ID of the second user.
     * @throws EntityNotFoundException if either user does not exist.
     */
    public void addFriendship(String id1, String id2) {
        if (id1.equals(id2)) return;
        if (id1.compareTo(id2) > 0) {
            String temp = id1;
            id1 = id2;
            id2 = temp;
        }

        String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?) ON CONFLICT DO NOTHING;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id1);
            ps.setString(2, id2);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error from adding a friendship: " + e.getMessage(), e);
        }
    }

    /**
     * Removes a bi-directional friendship between two users identified by their IDs.
     * @param id1 The ID of the first user.
     * @param id2 The ID of the second user.
     * @throws EntityNotFoundException if either user does not exist.
     */
    public void removeFriendship(String id1, String id2) {
        if (id1.compareTo(id2) > 0) {
            String temp = id1;
            id1 = id2;
            id2 = temp;
        }

        String sql = "DELETE FROM friendships WHERE user_id=? AND friend_id=?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id1);
            ps.setString(2, id2);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error from deleting a friendship: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the IDs of all friends of a specified user.
     * @param id The ID of the user whose friends are to be retrieved.
     * @return A Set of IDs representing the friends of the specified user.
     * @throws EntityNotFoundException if the user does not exist.
     */
    public Set<String> getFriendsIds(String id) {
        return getAdjacency().get(id);
    }

    /**
     * Returns the adjacency representation of the users and their relationships.
     * @return A Map where each key is a user's ID and the associated value is a Set of IDs representing all of the user's friends.
     */
    public Map<String, Set<String>> getAdjacency() {
        Map<String, Set<String>> adjacency = new HashMap<>();
        String sql = "SELECT user_id, friend_id FROM friendships;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id1 = rs.getString("user_id");
                String id2 = rs.getString("friend_id");
                adjacency.computeIfAbsent(id1, k -> new HashSet<>()).add(id2);
                adjacency.computeIfAbsent(id2, k -> new HashSet<>()).add(id1);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Error from retrieving the adjacency representation of the users and their relationships: " + e.getMessage(), e);
        }
        return adjacency;
    }
}
