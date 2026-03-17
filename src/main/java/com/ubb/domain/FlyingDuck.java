package com.ubb.domain;

public class FlyingDuck extends Duck implements Flying{
    public FlyingDuck(String id, String userName, double speed, double endurance) {
        super(id, userName, speed, endurance);
    }

    @Override
    public void fly() {
        System.out.println(super.userName + " (FlyingDuck) is flying with speed " + getSpeed());
    }

    @Override
    public void performAction() {
        fly();
    }

    @Override
    public UserType getType() {
        return UserType.FLYING;
    }

    @Override
    public String toString() {
        return String.format("FlyingDuck{id='%s', username='%s', type=%s, speed=%.2f, end=%.2f}",
                getId(), super.userName, "flying", getSpeed(), getEndurance());
    }
}
