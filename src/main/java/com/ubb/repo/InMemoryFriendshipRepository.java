package com.ubb.repo;

import com.ubb.exceptions.EntityNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InMemoryFriendshipRepository implements FriendshipRepository{
    private Map<String, Set<String>> adjacency = new HashMap<>();

    /**
     * Establishes a bi-directional friendship between two users.
     * If either user does not exist, an EntityNotFoundException is thrown.
     * @param id1 The ID of the first user.
     * @param id2 The ID of the second user.
     * @throws EntityNotFoundException if either user does not exist.
     */
    @Override
    public void addFriendship(String id1, String id2) {
        if (id1.equals(id2)) return;
        adjacency.get(id1).add(id2);
        adjacency.get(id2).add(id1);
    }

    /**
     * Removes a bi-directional friendship between two users identified by their IDs.
     * @param id1 The ID of the first user.
     * @param id2 The ID of the second user.
     * @throws EntityNotFoundException if any of the specified users do not exist.
     */
    @Override
    public void removeFriendship(String id1, String id2) {
        adjacency.get(id1).remove(id2);
        adjacency.get(id2).remove(id1);
    }

    /**
     * Returns the IDs of all friends of a specified user.
     * @param id The ID of the user whose friends are to be retrieved.
     * @return a Set of IDs representing the friends of the specified user.
     * @throws EntityNotFoundException if the user does not exist.
     */
    @Override
    public Set<String> getFriendsIds(String id) {
        return adjacency.get(id);
    }

    /**
     * Retrieves the adjacency representation of the users and their relationships in the repository.
     * @return a Map where each key is a user's ID and the associated value is a Set of IDs
     *         representing all of the user's friends.
     */
    @Override
    public Map<String, Set<String>> getAdjacency() {
        return adjacency;
    }
}
