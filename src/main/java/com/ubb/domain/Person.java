package com.ubb.domain;

public class Person extends User {
    private final String firstName;
    private final String lastName;
    private final String occupation;

    public Person(String id, String userName, String firstName, String lastName, String occupation) {
        super(id, userName);
        this.firstName = firstName;
        this.lastName = lastName;
        this.occupation = occupation;
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

    @Override
    public UserType getType() {
        return UserType.PERSON;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", occupation='" + occupation + '\'' +
                '}';
    }
}
