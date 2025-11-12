package fr.hockey.models;

import java.util.ArrayList;
import java.util.List;

public class Coach {
    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private List<String> teams;

    public Coach() {
        this.teams = new ArrayList<>();
    }

    public Coach(int id, String firstName, String lastName, String username, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.teams = new ArrayList<>();
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

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getTeams() {
        return teams;
    }

    public void setTeams(List<String> teams) {
        this.teams = teams;
    }
    
    public void addTeam(String team) {
        this.teams.add(team);
    }
    
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}