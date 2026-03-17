package com.ubb.domain;

public class SwimmingDuck extends Duck implements Swimming{
    public SwimmingDuck(String id, String userName, double speed, double endurance) {
        super(id, userName, speed, endurance);
    }

    @Override
    public void swim() {
        System.out.println(super.userName + " (SwimmingDuck) is swimming with speed " + getSpeed());
    }

    @Override
    public void performAction() {
        swim();
    }

    @Override
    public UserType getType() {
        return UserType.SWIMMING;
    };

    @Override
    public String toString() {
        return String.format("SwimmingDuck{id='%s', username='%s', type=%s, speed=%.2f, end=%.2f}",
                getId(), super.userName, "swimming", getSpeed(), getEndurance());
    }
}
