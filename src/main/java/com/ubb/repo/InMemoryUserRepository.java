package com.ubb.repo;

import com.ubb.domain.Duck;
import com.ubb.domain.User;
import com.ubb.domain.UserType;
import com.ubb.exceptions.DuplicateEntityException;
import com.ubb.exceptions.EntityNotFoundException;

import java.util.*;

public class InMemoryUserRepository implements UserRepository{
    private Map<String, User> users = new HashMap<>();

    /**
     * Adds a new user to the repository. Throws DuplicateEntityException
     * @param user The user to be added
     */
    @Override
    public void addUser(User user) {
        if (users.containsKey(user.getId())) throw new DuplicateEntityException("User with ID already exists: " + user.getId());
        users.put(user.getId(), user);

    }

    /**
     * Removes a user by their ID. Throws EntityNotFoundException if the user does not exist.
     * @param id The ID of the user to be removed
     */
    @Override
    public void removeUser(String id) {
        if (!users.containsKey(id)) throw new EntityNotFoundException("User with ID does not exist: " + id);
        users.remove(id);
    }

    /**
     * Gets a user by their ID. Throws EntityNotFoundException if the user does not exist.
     * @param id The ID of the user to be retrieved
     * @return a User object
     */
    @Override
    public User getUser(String id) {
        if (!users.containsKey(id)) throw new EntityNotFoundException("User with ID does not exist: " + id);
        return users.get(id);
    }

    /**
     * Checks if a user with the specified ID exists in the repository.
     * @param id The ID of the user to be checked.
     * @return true if a user with the given ID exists, false otherwise.
     */
    @Override
    public boolean userExists(String id) {
        return users.containsKey(id);
    }

    /**
     * Retrieves all users from the repository.
     * @return a list of all users present in the repository
     */
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Get all ducks from the repository.
     * @return A list of all ducks present in the repository.
     */
    @Override
    public List<Duck> getAllDucks() {
        List<Duck> ducks = new ArrayList<>();
        for (User user : users.values()) {
            if (user.getType().equals(UserType.SWIMMING) || user.getType().equals(UserType.FLYING)
            || user.getType().equals(UserType.FLYING_AND_SWIMMING)) ducks.add((Duck) user);
        }
        return ducks;
    }

    @Override
    public List<User> getUsersPage(int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public int countUsers() {
        return users.size();
    }

    @Override
    public User getByUsername(String username) {
        return null;
    }

    @Override
    public String getPasswordHash(String id) {
        return "";
    }

}
