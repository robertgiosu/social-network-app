package com.ubb.repo;

import com.ubb.exceptions.EntityNotFoundException;

import java.util.Map;
import java.util.Set;

public interface FriendshipRepository {
    /**
     * Establishes a bi-directional friendship between two users.
     * @param id1 The ID of the first user.
     * @param id2 The ID of the second user.
     * @throws EntityNotFoundException if either user does not exist.
     */
    void addFriendship(String id1, String id2) throws EntityNotFoundException;

    /**
     * Removes a bi-directional friendship between two users identified by their IDs.
     * @param id1 The ID of the first user.
     * @param id2 The ID of the second user.
     * @throws EntityNotFoundException if either user does not exist.
     */
    void removeFriendship(String id1, String id2) throws EntityNotFoundException;

    /**
     * Returns the IDs of all friends of a specified user.
     * @param id The ID of the user whose friends are to be retrieved.
     * @return A Set of IDs representing the friends of the specified user.
     * @throws EntityNotFoundException if the user does not exist.
     */
    Set<String> getFriendsIds(String id) throws EntityNotFoundException;

    /**
     * Returns the adjacency representation of the users and their relationships.
     * @return A Map where each key is a user's ID and the associated value is a Set of IDs representing all of the user's friends.
     */
    public Map<String, Set<String>> getAdjacency();
}
