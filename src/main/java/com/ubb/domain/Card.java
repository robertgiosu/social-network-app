package com.ubb.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Card<T extends Duck> {
    private final String id;
    private final String name;

    public Card(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the average performance of the flock.
     * @return the average performance of the flock.
     */
    public double getAveragePerformance(List<Duck> members) {
        if (members.isEmpty())
            return 0.0;
        double sumSpeed = 0.0;
        double sumEndurance = 0.0;
        for (var duck : members) {
            sumSpeed += duck.getSpeed();
            sumEndurance += duck.getEndurance();
        }
        double avgSpeed = sumSpeed / members.size();
        double avgEndurance = sumEndurance / members.size();
        return (avgSpeed + avgEndurance) / 2;
    }
}
