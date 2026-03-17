package com.ubb.ui;

public class UserTableRow {

    private final String id;
    private final String username;

    // Person fields
    private final String firstName;
    private final String lastName;
    private final String occupation;

    // Duck fields
    private final String type;
    private final String speed;
    private final String endurance;

    public UserTableRow(String id, String username,
                        String firstName, String lastName, String occupation,
                        String type, String speed, String endurance) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.occupation = occupation;
        this.type = type;
        this.speed = speed;
        this.endurance = endurance;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getType() {
        return type;
    }

    public String getSpeed() {
        return speed;
    }

    public String getEndurance() {
        return endurance;
    }
}
