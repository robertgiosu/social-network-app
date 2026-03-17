package com.ubb.domain;

public class Event {
    protected final String id;
    protected final String title;

    public Event(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
