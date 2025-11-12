package fr.hockey.models;

public class Player {
    private int id;
    private String firstName;
    private String lastName;
    private String category; // U9, U11, U13, U15, U17, U20
    private String role; // "CAPITAINE", "ASSISTANT", "JOUEUR"
    private String position; // "GARDIEN", "DEFENSEUR", "ATTAQUANT"
    private License license;

    public Player() {
    }

    public Player(int id, String firstName, String lastName, String category, String role, String position) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.category = category;
        this.role = role;
        this.position = position;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + category + " - " + position + ")";
    }
}