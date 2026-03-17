package com.ubb.repo;

import com.ubb.domain.*;
import com.ubb.exceptions.DuplicateEntityException;
import com.ubb.exceptions.EntityNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class DatabaseUserRepository implements UserRepository {
    private String url;
    private String username;
    private String password;
    public DatabaseUserRepository(Properties props) {
        this.url = props.getProperty("jdbc.url");
        this.username = props.getProperty("jdbc.user");
        this.password = props.getProperty("jdbc.pass");
    }

    /**
     * Adds a new user to the repository. Throws DuplicateEntityException
     * @param user The user to be added
     */
    @Override
    public void addUser(User user) {
        if (userExists(user.getId()))
            throw new DuplicateEntityException("User with ID already exists: " + user.getId());
        String sql = "INSERT INTO users (id, username, type, first_name, last_name, occupation, speed, endurance) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getId());
            ps.setString(2, user.getUserName());
            if (user.getType().equals(UserType.PERSON)) {
                Person p = (Person) user;
                ps.setString(3, "PERSON");
                ps.setString(4, p.getFirstName());
                ps.setString(5, p.getLastName());
                ps.setString(6, p.getOccupation());
                ps.setObject(7, null);
                ps.setObject(8, null);
            }
            else if (user.getType().equals(UserType.SWIMMING)) {
                SwimmingDuck sd = (SwimmingDuck) user;
                ps.setString(3, "SWIMMING_DUCK");
                ps.setObject(4, null);
                ps.setObject(5, null);
                ps.setObject(6, null);
                ps.setDouble(7, sd.getSpeed());
                ps.setDouble(8, sd.getEndurance());
            }
            else if (user.getType().equals(UserType.FLYING)) {
                FlyingDuck fd = (FlyingDuck) user;
                ps.setString(3, "FLYING_DUCK");
                ps.setObject(4, null);
                ps.setObject(5, null);
                ps.setObject(6, null);
                ps.setDouble(7, fd.getSpeed());
                ps.setDouble(8, fd.getEndurance());
            }
            else if (user.getType().equals(UserType.FLYING_AND_SWIMMING)) {
                FlyingAndSwimmingDuck fsd = (FlyingAndSwimmingDuck) user;
                ps.setString(3, "FLYING_AND_SWIMMING_DUCK");
                ps.setObject(4, null);
                ps.setObject(5, null);
                ps.setObject(6, null);
                ps.setDouble(7, fsd.getSpeed());
                ps.setDouble(8, fsd.getEndurance());
            }
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a user by their ID. Throws EntityNotFoundException if the user does not exist.
     * @param id The ID of the user to be removed
     */
    @Override
    public void removeUser(String id) {
        if (!userExists(id))
            throw new EntityNotFoundException("User with ID does not exist: " + id);
        String sql = "DELETE FROM users WHERE id=?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a user by their ID. Throws EntityNotFoundException if the user does not exist.
     * @param id The ID of the user to be retrieved
     * @return a User object
     */
    @Override
    public User getUser(String id) {
        if (!userExists(id))
            throw new EntityNotFoundException("User with ID does not exist: " + id);
        String sql = "SELECT * FROM users WHERE id=?;";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return null;
            }

            String type = rs.getString("type");

            if (type.equals("PERSON")) {
                String userName = rs.getString("username");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String occupation = rs.getString("occupation");
                return new Person(id, userName, firstName, lastName, occupation);
            }
            else if (type.equals("SWIMMING_DUCK")) {
                String userName = rs.getString("username");
                double speed = rs.getDouble("speed");
                double endurance = rs.getDouble("endurance");
                return new SwimmingDuck(id, userName, speed, endurance);
            }
            else if (type.equals("FLYING_DUCK")) {
                String userName = rs.getString("username");
                double speed = rs.getDouble("speed");
                double endurance = rs.getDouble("endurance");
                return new FlyingDuck(id, userName, speed, endurance);
            }
            else if (type.equals("FLYING_AND_SWIMMING_DUCK")) {
                String userName = rs.getString("username");
                double speed = rs.getDouble("speed");
                double endurance = rs.getDouble("endurance");
                return new FlyingAndSwimmingDuck(id, userName, speed, endurance);
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
    @Override
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

    @Override
    public int countUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getByUsername(String userName) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            String id = rs.getString("id");
            String userNme = rs.getString("username");
            String type = rs.getString("type");
            if (type.equals("PERSON")) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String occupation = rs.getString("occupation");
                return new Person(id, userNme, firstName, lastName, occupation);
            }
            else if (type.equals("SWIMMING_DUCK")) {
                double speed = rs.getDouble("speed");
                double endurance = rs.getDouble("endurance");
                return new SwimmingDuck(id, userNme, speed, endurance);
            }
            else if (type.equals("FLYING_DUCK")) {
                double speed = rs.getDouble("speed");
                double endurance = rs.getDouble("endurance");
                return new FlyingDuck(id, userNme, speed, endurance);
            }
            else if (type.equals("FLYING_AND_SWIMMING_DUCK")) {
                double speed = rs.getDouble("speed");
                double endurance = rs.getDouble("endurance");
                return new FlyingAndSwimmingDuck(id, userNme, speed, endurance);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public String getPasswordHash(String id) {
        String sql = "SELECT password_hash FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("password_hash");
            } else {
                throw new RuntimeException("User with ID " + id + " not found.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error in getPasswordHash: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getUsersPage(int pageNumber, int pageSize) {
        String sql = "SELECT * FROM users ORDER BY id LIMIT ? OFFSET ?;";
        int offset = (pageNumber - 1) * pageSize;
        List<User> users = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
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

    /**
     * Retrieves all users from the repository.
     * @return a list of all users present in the repository
     */
    @Override
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

    /**
     * Get all ducks from the repository.
     * @return A list of all ducks present in the repository.
     */
    @Override
    public List<Duck> getAllDucks() {
        String sql = "SELECT id, username, type, speed, endurance FROM users WHERE type = 'SWIMMING_DUCK' OR type = 'FLYING_DUCK' OR type = 'FLYING_AND_SWIMMING_DUCK' ORDER BY id;";
        List<Duck> ducks = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                String userName = rs.getString("username");
                double speed = rs.getDouble("speed");
                double endurance = rs.getDouble("endurance");
                String type = rs.getString("type");
                if (type.equals("SWIMMING_DUCK")) {
                    ducks.add(new SwimmingDuck(id, userName, speed, endurance));
                }
                else if (type.equals("FLYING_DUCK")) {
                    ducks.add(new FlyingDuck(id, userName, speed, endurance));
                }
                else if (type.equals("FLYING_AND_SWIMMING_DUCK")) {
                    ducks.add(new FlyingAndSwimmingDuck(id, userName, speed, endurance));
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ducks;
    }
}
