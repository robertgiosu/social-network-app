package com.ubb.repo;

import com.ubb.domain.Message;
import com.ubb.domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseMessageRepository {
    private String url;
    private String username;
    private String password;
    private DatabaseUserRepository userRepo;

    public DatabaseMessageRepository(Properties props, DatabaseUserRepository userRepo) {
        this.url = props.getProperty("jdbc.url");
        this.username = props.getProperty("jdbc.user");
        this.password = props.getProperty("jdbc.pass");
        this.userRepo = userRepo;
    }

    /**
     * Saves a message in the database.
     * @param m Message to be saved.
     * @return the ID of the saved message.
     */
    public Long saveMessage(Message m) {
        String sql = "INSERT INTO messages (from_id, message, date, reply_to) VALUES (?, ?, ?, ?) RETURNING id;";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getFrom().getId());
            ps.setString(2, m.getMessage());
            ps.setTimestamp(3, Timestamp.valueOf(m.getDate()));
            if (m.getReplyTo() != null)
                ps.setLong(4, m.getReplyTo().getId());
            else
                ps.setNull(4, Types.BIGINT);
            ResultSet rs = ps.executeQuery();
            rs.next();
            Long msgId = rs.getLong(1);

            for (User receiver : m.getTo()) {
                String sql2 = "INSERT INTO message_recipients (message_id, recipient_id) VALUES (?, ?);";
                try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                    ps2.setLong(1, msgId);
                    ps2.setString(2, receiver.getId());
                    ps2.executeUpdate();
                }
            }
            return msgId;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a message from the database.
     * @param id the ID of the message to be retrieved.
     * @return the retrieved message, or null if no message with the given ID exists.
     */
    public Message getMessage(Long id) {
        String sql = "SELECT * FROM messages WHERE id = ?;";

        try (Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            String fromId = rs.getString("from_id");
            String text = rs.getString("message");
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            Long replyId = rs.getLong("reply_to");
            Message replyTo = (replyId != 0) ? getMessage(replyId) : null;

            User fromUser = userRepo.getUser(fromId);

            String sql2 = "SELECT recipient_id FROM message_recipients WHERE message_id=?;";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setLong(1, id);

            ResultSet rs2 = ps2.executeQuery();
            List<User> recipients = new ArrayList<>();

            while (rs2.next()) {
                recipients.add(userRepo.getUser(rs2.getString(1)));
            }


            return new Message(id, fromUser, recipients, text, date, replyTo);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a list of messages between two users.
     * @param userA the ID of the first user.
     * @param userB the ID of the second user.
     * @return a list of messages between the two users, ordered by date.
     */
    public List<Message> getConversation(String userA, String userB) {
        String sql = "SELECT m.* FROM messages m JOIN message_recipients mr ON m.id = mr.message_id WHERE (m.from_id = ? AND mr.recipient_id = ?) OR (m.from_id = ? AND mr.recipient_id = ?) ORDER BY date;";
        List<Message> list = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userA);
            ps.setString(2, userB);
            ps.setString(3, userB);
            ps.setString(4, userA);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(getMessage(rs.getLong("id")));
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
