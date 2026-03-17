package com.ubb.repo;

import com.ubb.domain.Duck;
import com.ubb.domain.FlyingAndSwimmingDuck;
import com.ubb.domain.FlyingDuck;
import com.ubb.domain.SwimmingDuck;

import java.sql.*;
import java.util.*;

public class DatabaseCardRepository {
    private String url;
    private String username;
    private String password;

    public DatabaseCardRepository(Properties props) {
        this.url = props.getProperty("jdbc.url");
        this.username = props.getProperty("jdbc.user");
        this.password = props.getProperty("jdbc.pass");
    }

    /**
     * Checks if a flock with the specified ID exists.
     * @param cardId the ID of the flock.
     * @return true if a flock with the given ID exists, false otherwise.
     */
    private boolean existsCard(String cardId) {
        String sql = "SELECT * FROM card WHERE id = ?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cardId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next())
                return false;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * Returns the type of the user with the specified ID.
     * @param userId the ID of the user.
     * @return the type of the user with the specified ID, or null if the user is not associated
     */
    private String getUserType(String userId) {
        String sql = "SELECT type FROM users WHERE id = ?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next())
                return null;
            return rs.getString("type");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new flock of ducks.
     * @param cardId the ID of the flock.
     * @param name the name of the flock.
     * @param <T> the type of ducks in the flock.
     */
    public <T extends Duck> void createCard(String cardId, String name) {
        if (existsCard(cardId))
            throw new IllegalArgumentException("Flock with ID already exists: " + cardId);
        String sql = "INSERT INTO card (id, name) VALUES (?, ?);";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cardId);
            ps.setString(2, name);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the type of the ducks from the flock with the specified ID.
     * @param cardId the ID of the flock.
     * @return the type of the ducks from the flock with the specified ID, or null if the flock is
     * not associated with any ducks from the users' table.
     */
    public String getTypeOfTheFlock(String cardId) {
        String sql = "SELECT type FROM users WHERE card_id = ? LIMIT 1;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cardId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next())
                return null;
            return rs.getString("type");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
     * Adds a duck to a flock.
     * @param cardId the ID of the flock.
     * @param duckId the ID of the duck to be added.
     */
    public void addDuckToCard(String cardId, String duckId) {
        //verificare existenta card
        if (!existsCard(cardId))
            throw new IllegalArgumentException("Flock does not exist, id=" + cardId);

        //verificare existenta utilizator si ca este o rata
        if (!userExists(duckId)) {
            throw new IllegalArgumentException("User with id=" + duckId + " does not exist");
        }
        String typeOfGivenUser = getUserType(duckId);
        if (typeOfGivenUser.equals("PERSON")) {
            throw new IllegalArgumentException("User with id=" + duckId + " is not a duck");
        }

        //verificare daca tipul cardului este identic cu tipul ratei
        String typeOfCard = getTypeOfTheFlock(cardId);
        if (typeOfCard != null && !typeOfGivenUser.equals(typeOfCard)) {
            throw new IllegalArgumentException("Duck with id=" + duckId + " is not a " + typeOfCard);
        }

        addDuckToCardInternal(cardId, duckId);
    }

    /**
     * Internal method for adding a duck to a flock.
     * @param cardId the ID of the flock to which the duck is to be added.
     * @param duckId the ID of the duck to be added.
     */
    private void addDuckToCardInternal(String cardId, String duckId) {
        String sql = "UPDATE users SET card_id = ? WHERE id = ?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cardId);
            ps.setString(2, duckId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a duck from a flock.
     * @param cardId the ID of the flock.
     * @param duckId the ID of the duck to be removed.
     */
    public void removeDuckFromCard(String cardId, String duckId) {
        //verific existenta cardului
        if (!existsCard(cardId))
            throw new IllegalArgumentException("Flock does not exist, id=" + cardId);

        //verificare existenta utilizator si ca este o rata
        if (!userExists(duckId)) {
            throw new IllegalArgumentException("User with id=" + duckId + " does not exist");
        }
        String typeOfGivenUser = getUserType(duckId);
        if (typeOfGivenUser.equals("PERSON")) {
            throw new IllegalArgumentException("User with id=" + duckId + " is not a duck");
        }

        //verificare daca mai avem alte rate in card
        String typeOfCard = getTypeOfTheFlock(cardId);
        if (typeOfCard == null) {
            throw new IllegalArgumentException("Flock with id=" + cardId + " is already empty, cannot remove duck with id=" + duckId);
        }

        //verificare daca tipul cardului este identic cu tipul ratei
        if (!typeOfGivenUser.equals(typeOfCard)) {
            throw new IllegalArgumentException("Duck with id=" + duckId + " is not a " + typeOfCard);
        }

        removeDuckFromCardInternal(cardId, duckId);
    }

    /**
     * Internal method for removing a duck from a flock.
     * @param cardId the ID of the flock from which the duck is to be removed.
     * @param duckId the ID of the duck to be removed.
     */
    private void removeDuckFromCardInternal(String cardId, String duckId) {
        String sql = "UPDATE users SET card_id = ? WHERE id = ?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, null);
            ps.setString(2, duckId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns all flocks.
     * @return a map where keys are flock IDs and values are flock objects.
     */
    public Map<String, List<Duck>> getAllCards() {
        String sql = "SELECT id FROM card;";
        Map<String, List<Duck>> cards = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String cardId = rs.getString("id");
                List<Duck> ducks = new ArrayList<>();
                String sql2 = "SELECT * FROM users WHERE card_id = ?;";
                try (Connection conn2 = DriverManager.getConnection(url, username, password);
                     PreparedStatement ps2 = conn2.prepareStatement(sql2)) {
                    ps2.setString(1, cardId);
                    ResultSet rs2 = ps2.executeQuery();
                    while (rs2.next()) {
                        String type = rs2.getString("type");
                        if (type.equals("SWIMMING_DUCK")) {
                            String id = rs2.getString("id");
                            String userName = rs2.getString("username");
                            double speed = rs2.getDouble("speed");
                            double endurance = rs2.getDouble("endurance");
                            ducks.add(new SwimmingDuck(id, userName, speed, endurance));
                        }
                        else if (type.equals("FLYING_DUCK")) {
                            String id = rs2.getString("id");
                            String userName = rs2.getString("username");
                            double speed = rs2.getDouble("speed");
                            double endurance = rs2.getDouble("endurance");
                            ducks.add(new FlyingDuck(id, userName, speed, endurance));
                        }
                        else if (type.equals("FLYING_AND_SWIMMING_DUCK")) {
                            String id = rs2.getString("id");
                            String userName = rs2.getString("username");
                            double speed = rs2.getDouble("speed");
                            double endurance = rs2.getDouble("endurance");
                            ducks.add(new FlyingAndSwimmingDuck(id, userName, speed, endurance));
                        }
                    }
                }
                cards.put(cardId, ducks);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cards;
    }
}
