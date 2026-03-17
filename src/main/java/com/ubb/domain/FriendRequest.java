package com.ubb.domain;

public class FriendRequest {
    private final String fromId;
    private final String toId;
    private FriendRequestStatus status;

    public FriendRequest(String fromId, String toId, FriendRequestStatus status) {
        this.fromId = fromId;
        this.toId = toId;
        this.status = status;
    }

    public String getFromId() {
        return fromId;
    }

    public String getToId() {
        return toId;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status){
        this.status = status;
    }

    public String toString() {
        return "[Friend Request]: From: " + fromId + ", To: " + toId + ", Status: " + status;
    }
}
