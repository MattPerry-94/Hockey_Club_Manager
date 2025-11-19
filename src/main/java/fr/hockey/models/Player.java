package fr.hockey.models;

/**
 * Représente un joueur du club de hockey.
 *
 * <p>Chaque joueur possède :
 * <ul>
 *     <li>des informations d'identité (prénom, nom) ;</li>
 *     <li>une catégorie (U9, U11, U13, etc.) ;</li>
 *     <li>un rôle dans l'équipe (CAPITAINE, ASSISTANT, JOUEUR) ;</li>
 *     <li>un poste (GARDIEN, DEFENSEUR, ATTAQUANT) ;</li>
 *     <li>un numéro de maillot ;</li>
 *     <li>une licence (optionnelle), gérée via {@link fr.hockey.models.License}.</li>
 * </ul>
 *
 * <p>Cette classe est mappée à la table SQL {@code players} et est utilisée dans
 * l’ensemble des écrans du back-office (gestion des joueurs, licences, catégories, PDF, etc.).</p>
 */
public class Player {

    /** Identifiant unique du joueur. */
    private int id;

    /** Prénom du joueur. */
    private String firstName;

    /** Nom du joueur. */
    private String lastName;

    /** Catégorie sportive du joueur (U9, U11, U13, U15, U17, U20). */
    private String category;

    /** Rôle dans l'équipe (CAPITAINE, ASSISTANT, JOUEUR). */
    private String role;

    /** Poste du joueur (GARDIEN, DEFENSEUR, ATTAQUANT). */
    private String position;

    /** Numéro de maillot (1 à 99), ou 0 si non défini. */
    private int number;

    /** Licence associée au joueur, si elle existe. */
    private License license;

    /**
     * Constructeur par défaut.
     */
    public Player() {
    }

    /**
     * Constructeur minimal pour créer un joueur sans numéro ni licence.
     *
     * @param id identifiant unique du joueur
     * @param firstName prénom du joueur
     * @param lastName nom du joueur
     * @param category catégorie sportive
     * @param role rôle dans l’équipe
     * @param position poste du joueur
     */
    public Player(int id, String firstName, String lastName, String category, String role, String position) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.category = category;
        this.role = role;
        this.position = position;
    }

    /** @return identifiant unique du joueur */
    public int getId() {
        return id;
    }

    /** @param id identifiant unique du joueur */
    public void setId(int id) {
        this.id = id;
    }

    /** @return prénom du joueur */
    public String getFirstName() {
        return firstName;
    }

    /** @param firstName prénom du joueur */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** @return nom du joueur */
    public String getLastName() {
        return lastName;
    }

    /** @param lastName nom du joueur */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** @return catégorie du joueur */
    public String getCategory() {
        return category;
    }

    /** @param category catégorie du joueur */
    public void setCategory(String category) {
        this.category = category;
    }

    /** @return rôle du joueur */
    public String getRole() {
        return role;
    }

    /** @param role rôle du joueur */
    public void setRole(String role) {
        this.role = role;
    }

    /** @return poste du joueur */
    public String getPosition() {
        return position;
    }

    /** @param position poste du joueur */
    public void setPosition(String position) {
        this.position = position;
    }

    /** @return numéro de maillot */
    public int getNumber() {
        return number;
    }

    /** @param number numéro de maillot (1–99), ou 0 si non défini */
    public void setNumber(int number) {
        this.number = number;
    }

    /** @return licence associée au joueur (ou null si aucune) */
    public License getLicense() {
        return license;
    }

    /** @param license licence associée au joueur */
    public void setLicense(License license) {
        this.license = license;
    }

    /**
     * Retourne une version texte du joueur : "Prénom Nom (Catégorie - Poste)".
     *
     * @return représentation lisible du joueur
     */
    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + category + " - " + position + ")";
    }
}
