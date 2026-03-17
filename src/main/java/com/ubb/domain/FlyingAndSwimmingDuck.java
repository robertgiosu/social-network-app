package com.ubb.domain;

public class FlyingAndSwimmingDuck extends Duck implements FlyingAndSwimming{
    public FlyingAndSwimmingDuck(String id, String userName, double speed, double endurance) {
        super(id, userName, speed, endurance);
    }

    @Override
    public void flyAndSwim() {
        System.out.println(super.userName + " (FlyingAndSwimmingDuck) is flying and swimming with speed " + getSpeed());
    }

    @Override
    public void performAction() {
        flyAndSwim();
    }

    @Override
    public UserType getType() {
        return UserType.FLYING_AND_SWIMMING;
    }

    @Override
    public String toString() {
        return String.format("FlyingAndSwimmingDuck{id='%s', username='%s', type=%s, speed=%.2f, end=%.2f}",
                getId(), super.userName, "flying and swimming", getSpeed(), getEndurance());
    }
}
