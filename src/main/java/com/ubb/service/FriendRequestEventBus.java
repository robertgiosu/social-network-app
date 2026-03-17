package com.ubb.service;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestEventBus {
    private static final List<FriendRequestObserver> observers = new ArrayList<>();

    public static void subscribe(FriendRequestObserver o) {
        observers.add(o);
    }

    public static void unsubscribe(FriendRequestObserver o) {
        observers.remove(o);
    }

    public static void notifyAllObservers() {
        for (FriendRequestObserver o : observers) {
            o.onFriendRequestChanged();
        }
    }
}
