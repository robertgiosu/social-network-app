package com.ubb.domain;

public abstract class Duck extends User{
    private final double speed;
    private final double endurance;

    public Duck(String id, String userName, double speed, double endurance) {
        super(id, userName);
        this.speed = speed;
        this.endurance = endurance;
    }

    public double getSpeed() {
        return speed;
    }

    public double getEndurance() {
        return endurance;
    }

    public double getTimeForDistance(double distance) {
        return 2.0 * distance / speed;
    }

    public abstract void performAction();

    public abstract UserType getType();

    @Override
    public String toString() {
        return "Duck{" +
                "id='" + id + '\'' +
                ", speed=" + speed +
                ", endurance=" + endurance +
                '}';
    }
}
