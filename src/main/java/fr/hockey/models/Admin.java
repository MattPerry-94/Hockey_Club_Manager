package fr.hockey.models;

/**
 * Représente un administrateur du système.
 *
 * <p>Un administrateur possède un rôle spécifique ("ADMIN") et
 * des informations d'identification lui permettant d'accéder à
 * l'interface d'administration de l'application.</p>
 *
 * <p>Cette classe est utilisée par {@code AdminDAO} pour le chargement
 * et la gestion des administrateurs depuis la base de données.</p>
 */
public class Admin {

    /** Identifiant unique de l'administrateur. */
    private int id;

    /** Nom d'utilisateur utilisé pour la connexion. */
    private String username;

    /** Mot de passe (hashé dans la base de données). */
    private String password;

    /** Prénom de l'administrateur. */
    private String firstName;

    /** Nom de famille de l'administrateur. */
    private String lastName;

    /** Adresse e-mail associée au compte. */
    private String email;

    /** Rôle de l'utilisateur, généralement "ADMIN". */
    private String role;

    /**
     * Constructeur par défaut.
     */
    public Admin() {
    }

    /**
     * Constructeur complet.
     *
     * @param id identifiant
     * @param username nom d'utilisateur
     * @param password mot de passe (hashé)
     * @param firstName prénom
     * @param lastName nom de famille
     * @param email adresse e-mail
     * @param role rôle (normalement "ADMIN")
     */
    public Admin(int id, String username, String password, String firstName,
                 String lastName, String email, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

    /**
     * Méthode placeholder (non implémentée).
     * Elle est présente pour compatibilité mais n'est pas utilisée.
     *
     * @param username nom d'utilisateur
     * @param firstname prénom
     * @param lastname nom de famille
     * @param password mot de passe
     */
    public static void createAdmin(String username, String firstname, String lastname, String password) {
        // Méthode volontairement vide
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    /**
     * Retourne le nom complet de l'administrateur.
     *
     * @return prénom + nom
     */
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
