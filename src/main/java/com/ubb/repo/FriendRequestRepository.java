package com.ubb.repo;

import com.ubb.domain.FriendRequest;
import com.ubb.domain.FriendRequestStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class FriendRequestRepository {
    private List<FriendRequest> requests = new ArrayList<>();
    private String url;
    private String username;
    private String password;

    public FriendRequestRepository(Properties props) {
        this.url = props.getProperty("jdbc.url");
        this.username = props.getProperty("jdbc.user");
        this.password = props.getProperty("jdbc.pass");
    }

    public void save(FriendRequest request) {
        String sql = "INSERT INTO friend_requests(sender_id, receiver_id, status) VALUES(?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, request.getFromId());
            ps.setString(2, request.getToId());
            ps.setString(3, request.getStatus().toString());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all pending friend requests sent to a user.
     * @param userId the ID of the user.
     * @return a list of FriendRequest objects.
     */
    public List<FriendRequest> getPendingForUser(String userId) {
        List<FriendRequest> pending = new ArrayList<>();
        String sql = "SELECT * FROM friend_requests WHERE receiver_id = ?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String fromId = rs.getString("sender_id");
                FriendRequestStatus status = FriendRequestStatus.valueOf(rs.getString("status"));
                if (status == FriendRequestStatus.PENDING)
                    pending.add(new FriendRequest(fromId, userId, status));
            }
            return pending;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the status of a friend request.
     * @param fromId the ID of the sender.
     * @param toId the ID of the receiver.
     * @param status the new status of the friend request.
     */
    public void setStatus(String fromId, String toId, FriendRequestStatus status) {
        String sql = "UPDATE friend_requests SET status = ? WHERE sender_id = ? AND receiver_id = ?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.toString());
            ps.setString(2, fromId);
            ps.setString(3, toId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
