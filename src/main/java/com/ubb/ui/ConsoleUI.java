package com.ubb.ui;

import com.ubb.domain.*;
import com.ubb.exceptions.DuplicateEntityException;
import com.ubb.exceptions.EntityNotFoundException;
import com.ubb.exceptions.ValidationException;
import com.ubb.service.SocialNetworkService;
import com.ubb.validation.DuckValidator;
import com.ubb.validation.PersonValidator;
import com.ubb.validation.Validator;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConsoleUI {
    private final SocialNetworkService service;
    private Scanner sc = new Scanner(System.in);
    private Validator<Person> personValidator = new PersonValidator();
    private Validator<Duck> duckValidator = new DuckValidator();

    public ConsoleUI(SocialNetworkService service) {
        this.service = service;
    }

    /**
     * Runs the console UI.
     */
    public void run() {
        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> addPerson();
                    case "2" -> addDuck();
                    case "3" -> removeUser();
                    case "4" -> addFriendship();
                    case "5" -> removeFriendship();
                    case "6" -> numberOfCommunities();
                    case "7" -> showMostSociableCommunity();
                    case "8" -> showAllUsers();
                    case "9" -> showAllFriendships();
                    case "10" -> manageDuckFlocks();
                    case "11" -> manageEvents();
                    case "0" -> { return; }
                    default -> System.out.println("Invalid choice");
                }
            }   catch (DuplicateEntityException | EntityNotFoundException | ValidationException e) {
                System.out.println("ERROR: " + e.getMessage() + "\n");
            }
        }
    }

    /**
     * Adds default users and ducks to the social network.
     */
    private void addDefault() {
        Person p1 = new Person("1", "user1", "John", "Doe", "Software Engineer");
        Person p2 = new Person("2", "user2", "Maria", "Pop", "Cashier");
        Person p3 = new Person("3", "user3", "Jane", "Smith", "Doctor");
        Duck d1 = new FlyingDuck("4", "user4", 10.0, 10.0);
        Duck d2 = new SwimmingDuck("5", "user5", 4.0, 2.0);
        Duck d3 = new FlyingAndSwimmingDuck("6", "user6", 12.0, 8.0);
        Duck d4 = new FlyingAndSwimmingDuck("7", "user7", 18.0, 12.0);
        Duck d5 = new SwimmingDuck("8", "user8", 5.0, 2.0);
        Duck d6 = new SwimmingDuck("9", "user9", 5.0, 5.0);
        Duck d7 = new SwimmingDuck("10", "user10", 3.0, 5.0);
        Duck d8 = new SwimmingDuck("11", "user11", 2.0, 7.0);
        service.addUser(p1);
        service.addUser(p2);
        service.addUser(p3);
        service.addUser(d1);
        service.addUser(d2);
        service.addUser(d3);
        service.addUser(d4);
        service.addUser(d5);
        service.addUser(d6);
        service.addUser(d7);
        service.addUser(d8);
        service.addFriendship(p1.getId(), p2.getId());
        service.addFriendship(p2.getId(), p3.getId());
        service.addFriendship(d2.getId(), d3.getId());
    }

    /**
     * Prints the main menu.
     */
    private void printMenu() {
        System.out.println("\n=== DuckSocialNetwork ===");
        System.out.println("1. Add Person");
        System.out.println("2. Add Duck");
        System.out.println("3. Delete User (by id)");
        System.out.println("4. Add Friendship");
        System.out.println("5. Delete Friendship");
        System.out.println("6. Show number of communities");
        System.out.println("7. Show the most sociable communities (ids)");
        System.out.println("8. Show all users");
        System.out.println("9. Show all friendships");
        System.out.println("10. Manage duck flocks");
        System.out.println("11. Manage events");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }

    /**
     * Adds a new person to the social network.
     */
    private void addPerson() {
        String id = UUID.randomUUID().toString();
        System.out.println("Username: ");
        String userName = sc.nextLine().trim();
        System.out.println("First name: ");
        String firstName = sc.nextLine().trim();
        System.out.println("Last name: ");
        String lastName = sc.nextLine().trim();
        System.out.println("Occupation: ");
        String occupation = sc.nextLine().trim();

        Person p = new Person(id, userName, firstName, lastName, occupation);
        personValidator.validate(p);
        service.addUser(p);
        System.out.println("Person added successfully");
    }

    /**
     * Adds a new duck to the social network.
     */
    private void addDuck() {
        String id = UUID.randomUUID().toString();
        System.out.println("Username: ");
        String userName = sc.nextLine().trim();
        System.out.println("Type (FLYING, SWIMMING, FLYING_AND_SWIMMING): ");
        String t = sc.nextLine().trim();
        if (!t.equals("FLYING") && !t.equals("SWIMMING") && !t.equals("FLYING_AND_SWIMMING")) {
            System.out.println("Invalid type");
            return;
        }
        DuckType type = DuckType.valueOf(t);
        System.out.println("Speed: ");
        double speed = Double.parseDouble(sc.nextLine().trim());
        System.out.println("Endurance: ");
        double endurance = Double.parseDouble(sc.nextLine().trim());

        if (type == DuckType.FLYING) {
            Duck d = new FlyingDuck(id, userName, speed, endurance);
            duckValidator.validate(d);
            service.addUser(d);
            System.out.println("Flying duck added successfully");
        }
        else if (type == DuckType.SWIMMING) {
            Duck d = new SwimmingDuck(id, userName, speed, endurance);
            duckValidator.validate(d);
            service.addUser(d);
            System.out.println("Swimming duck added successfully");
        }
        else if (type == DuckType.FLYING_AND_SWIMMING) {
            Duck d = new FlyingAndSwimmingDuck(id, userName, speed, endurance);
            duckValidator.validate(d);
            service.addUser(d);
            System.out.println("Flying and swimming duck added successfully");
        }
    }

    /**
     * Removes a user from the social network.
     */
    private void removeUser() {
        System.out.println("ID: ");
        String id = sc.nextLine().trim();
        service.removeUser(id);
        System.out.println("User removed successfully");
    }

    /**
     * Adds a friendship between two users.
     */
    private void addFriendship() {
        System.out.println("ID 1: ");
        String id1 = sc.nextLine().trim();
        System.out.println("ID 2: ");
        String id2 = sc.nextLine().trim();
        service.addFriendship(id1, id2);
        System.out.println("Friendship added successfully");
    }

    /**
     * Removes a friendship between two users.
     */
    private void removeFriendship() {
        System.out.println("ID 1: ");
        String id1 = sc.nextLine().trim();
        System.out.println("ID 2: ");
        String id2 = sc.nextLine().trim();
        service.removeFriendship(id1, id2);
        System.out.println("Friendship removed successfully");
    }

    /**
     * Prints the number of communities in the social network.
     */
    private void numberOfCommunities() {
        System.out.println("Number of communities: " + service.numberOfCommunities());
    }

    /**
     * Shows the most sociable communities in the social network.
     */
    private void showMostSociableCommunity() {
        List<String> comp = service.computeMostSociableFromAdj();
        if (comp == null || comp.isEmpty()) {
            System.out.println("No communities");
        } else {
            System.out.println("Most sociable community (ids):");
            System.out.println(comp.stream().collect(Collectors.joining(", ")));
        }
    }

    /**
     * Shows all users in the social network.
     */
    private void showAllUsers() {
        service.getAllUsers().forEach(System.out::println);
    }

    /**
     * Shows all friendships in the social network.
     */
    private void showAllFriendships() {
        service.getAdjacency().forEach((k, v) -> {
            System.out.println("User " + k + ":");
            v.forEach(System.out::println);
        });
    }

    /**
     * Manages duck flocks.
     */
    private void manageDuckFlocks () {
        while (true) {
            System.out.println("\n--- Duck Flock Management ---");
            System.out.println("1. Create a flock");
            System.out.println("2. Add a duck to a flock");
            System.out.println("3. Delete a duck from a flock");
            System.out.println("4. Show all flocks");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String opt = sc.nextLine().trim();
            try {
                switch (opt) {
                    case "1" -> createFlock();
                    case "2" -> addDuckToFlock();
                    case "3" -> removeDuckFromFlock();
                    case "4" -> listFlocks();
                    case "0" -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Creates a new flock.
     */
    private void createFlock() {
        System.out.print("name of the flock: ");
        String name = sc.nextLine().trim();
        String id = UUID.randomUUID().toString();
        service.createCard(id, name);
        System.out.println("Flock created with id=" + id);
    }

    /**
     * Adds a duck to a flock.
     */
    private void addDuckToFlock() {
        System.out.print("flock id: ");
        String cardId = sc.nextLine().trim();
        System.out.print("duck id: ");
        String duckId = sc.nextLine().trim();
        service.addDuckToCard(cardId, duckId);
        System.out.println("Duck added to flock.");
    }

    /**
     * Removes a duck from a flock.
     */
    private void removeDuckFromFlock() {
        System.out.print("flock id: ");
        String cardId = sc.nextLine().trim();
        System.out.print("duck id: ");
        String duckId = sc.nextLine().trim();
        service.removeDuckFromCard(cardId, duckId);
        System.out.println("Duck removed from flock.");
    }

    /**
     * Lists all flocks.
     */
    private void listFlocks() {
        Map<String, List<Duck>> cards = service.getAllCards();
        System.out.println("\n=== Flocks ===");
        for (String id : cards.keySet()) {
            System.out.println("Flock id: " + id);
            List<Duck> l = cards.get(id);
            if (!l.isEmpty()) {
                for (var ducks : l)
                    System.out.println(ducks);
            }
            else {
                System.out.println("No ducks");
            }
            System.out.println();
        }
    }

    /**
     * Manages events.
     */
    private void manageEvents() {
        while (true) {
            System.out.println("\n--- Event Management ---");
            System.out.println("1. Create Event");
            System.out.println("2. Create RaceEvent");
            System.out.println("3. Subscribe user to Event");
            System.out.println("4. Unsubscribe user from Event");
            System.out.println("5. Run RaceEvent");
            System.out.println("6. Show all events");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            String opt = sc.nextLine().trim();
            try {
                switch (opt) {
                    case "1" -> createEvent();
                    case "2" -> createRaceEvent();
                    case "3" -> subscribeEvent();
                    case "4" -> unsubscribeEvent();
                    case "5" -> runRaceEvent();
                    case "6" -> showAllEvents();
                    case "0" -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Creates a new event.
     */
    private void createEvent() {
        System.out.print("event title: ");
        String title = sc.nextLine().trim();
        String id = UUID.randomUUID().toString();
        service.createEvent(id, title);
        System.out.println("Event created with id=" + id);
    }

    /**
     * Creates a new race event.
     */
    private void createRaceEvent() {
        System.out.print("event title: ");
        String title = sc.nextLine().trim();
        String id = UUID.randomUUID().toString();
        service.createRaceEvent(id, title);
        System.out.println("RaceEvent created with id=" + id);
    }

    /**
     * Subscribes a user to an event.
     */
    private void subscribeEvent() {
        System.out.print("id event: ");
        String eventId = sc.nextLine().trim();
        System.out.print("id user: ");
        String userId = sc.nextLine().trim();
        service.subscribeToEvent(eventId, userId);
        System.out.println("User subscribed to event.");
    }

    /**
     * Unsubscribes a user from an event.
     */
    private void unsubscribeEvent() {
        System.out.print("id event: ");
        String eventId = sc.nextLine().trim();
        System.out.print("id user: ");
        String userId = sc.nextLine().trim();
        service.unsubscribeFromEvent(eventId, userId);
        System.out.println("User unsubscribed from event.");
    }

    /**
     * Runs a race event.
     */
    private void runRaceEvent() {
        System.out.print("id event: ");
        String eventId = sc.nextLine().trim();
        System.out.println(service.runRaceEvent(eventId));
    }

    /**
     * Shows all events.
     */
    private void showAllEvents() {
        Map<String, Event> events = service.getAllEvents();
        System.out.println("\n=== Events ===");
        for (String id : events.keySet()) {
            System.out.println("Event id: " + id);
            Event e = events.get(id);
            System.out.println("Title: " + e.getTitle());
            //System.out.println("Subscribers: " + e.getSubscribers().stream().map(User::getId).collect(Collectors.joining(", ")));
        }
    }
}
