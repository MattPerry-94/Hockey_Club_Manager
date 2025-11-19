package fr.hockey.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un coach du club de hockey.
 *
 * <p>Un coach possède des informations personnelles ainsi qu'une liste de catégories
 * (équipes) qu'il entraîne. La gestion des équipes est synchronisée avec la table
 * {@code coach_teams} via {@code CoachDAO}.</p>
 */
public class Coach {

    /** Identifiant unique du coach. */
    private int id;

    /** Prénom du coach. */
    private String firstName;

    /** Nom du coach. */
    private String lastName;

    /** Nom d'utilisateur utilisé pour la connexion. */
    private String username;

    /** Adresse email du coach. */
    private String email;

    /** Liste des catégories d'équipes entraînées par ce coach. */
    private List<String> teams;

    /**
     * Constructeur par défaut.
     * Initialise la liste des équipes.
     */
    public Coach() {
        this.teams = new ArrayList<>();
    }

    /**
     * Constructeur complet.
     *
     * @param id identifiant unique
     * @param firstName prénom
     * @param lastName nom
     * @param username identifiant de connexion
     * @param email adresse email
     */
    public Coach(int id, String firstName, String lastName, String username, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.teams = new ArrayList<>();
    }

    /** @return identifiant du coach */
    public int getId() {
        return id;
    }

    /** @param id identifiant du coach */
    public void setId(int id) {
        this.id = id;
    }

    /** @return prénom du coach */
    public String getFirstName() {
        return firstName;
    }

    /** @param firstName prénom du coach */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** @return nom du coach */
    public String getLastName() {
        return lastName;
    }

    /** @param lastName nom du coach */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** @return nom d'utilisateur du coach */
    public String getUsername() {
        return username;
    }

    /** @param username nom d'utilisateur du coach */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return adresse email du coach */
    public String getEmail() {
        return email;
    }

    /** @param email adresse email du coach */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retourne la liste des catégories d'équipes entraînées par ce coach.
     *
     * @return liste des catégories (U9, U11, U15...)
     */
    public List<String> getTeams() {
        return teams;
    }

    /**
     * Définit les catégories d'équipes du coach.
     *
     * @param teams liste des catégories
     */
    public void setTeams(List<String> teams) {
        this.teams = teams;
    }

    /**
     * Ajoute une équipe entraînée par ce coach.
     *
     * @param team catégorie à ajouter
     */
    public void addTeam(String team) {
        this.teams.add(team);
    }

    /**
     * Retourne le nom complet du coach.
     *
     * @return prénom + nom
     */
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
