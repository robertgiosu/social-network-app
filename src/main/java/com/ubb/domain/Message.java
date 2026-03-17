package com.ubb.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message {
    private Long id;
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime date;
    private Message replyTo;

    public Message(Long id, User from, List<User> to, String message, LocalDateTime date, Message replyTo) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.replyTo = replyTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFrom() {
        return from;
    }

    public List<User> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Message getReplyTo() {
        return replyTo;
    }
}
