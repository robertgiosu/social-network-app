package com.ubb.repo;

import com.ubb.domain.Duck;
import com.ubb.domain.User;
import com.ubb.exceptions.DuplicateEntityException;
import com.ubb.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserRepository {

    /**
     * Add a user to the repository.
     * @param user The user to be added.
     * @throws DuplicateEntityException if the user already exists.
     */
    void addUser(User user) throws DuplicateEntityException;

    /**
     * Remove a user from the repository.
     * @param id The ID of the user to be removed.
     * @throws EntityNotFoundException if the user does not exist.
     */
    void removeUser(String id) throws EntityNotFoundException;

    /**
     * Get a user from the repository.
     * @param id The ID of the user to be retrieved.
     * @return The user with the given ID.
     * @throws EntityNotFoundException if the user does not exist.
     */
    User getUser(String id) throws EntityNotFoundException;

    /**
     * Check if a user with the specified ID exists in the repository.
     * @param id The ID of the user to be checked.
     * @return true if a user with the given ID exists, false otherwise.
     */
    boolean userExists(String id);

    /**
     * Retrieves all users from the repository.
     * @return A list of all users present in the repository.
     */
    List<User> getAllUsers();

    /**
     * Get all ducks from the repository.
     * @return A list of all ducks present in the repository.
     */
    List<Duck> getAllDucks();

    /**
     * Gets a list of users from a given page.
     * @param pageNumber the page number to be retrieved.
     * @param pageSize the number of users per page.
     * @return A list of users from the given page.
     */
    List<User> getUsersPage(int pageNumber, int pageSize);

    /**
     * Counts the number of users in the repository.
     * @return The number of users in the repository.
     */
    int countUsers();

    User getByUsername(String username);

    String getPasswordHash(String id);
}
