package com.ubb.domain;

public class Lane {
    private int id;
    private double distance;

    public Lane(int id, double distance) {
        this.id = id;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public double getDistance() {
        return distance;
    }
}