package fr.hockey.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestion centralisée de la connexion à la base MySQL.
 * <p>
 * Ce composant implémente un singleton basique permettant
 * de réutiliser la même connexion JDBC tant qu'elle reste ouverte.
 * </p>
 *
 * <ul>
 *     <li>Chargement du driver MySQL</li>
 *     <li>Ouverture automatique de la connexion au premier appel</li>
 *     <li>Réutilisation de la connexion si elle est encore ouverte</li>
 *     <li>Fermeture manuelle possible via {@link #closeConnection()}</li>
 * </ul>
 */
public class DatabaseConnection {

    /** URL complète JDBC vers la base MySQL. */
    private static final String URL = "jdbc:mysql://localhost:3306/club_manager";

    /** Nom d'utilisateur MySQL. */
    private static final String USER = "root";

    /** Mot de passe MySQL. */
    private static final String PASSWORD = "";

    /** Connexion partagée (singleton). */
    private static Connection connection;

    /**
     * Constructeur privé empêchant l'instanciation.
     * Classe utilitaire basée sur des méthodes statiques.
     */
    private DatabaseConnection() {
        // Constructeur privé pour empêcher l'instanciation
    }

    /**
     * Récupère l'unique connexion JDBC à la base de données.
     * <p>
     * Si aucune connexion n'est ouverte, ou si la connexion précédente
     * a été fermée, une nouvelle connexion est créée.
     * </p>
     *
     * @return connexion JDBC active
     * @throws SQLException en cas d'impossibilité de se connecter ou si le driver est introuvable
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL introuvable", e);
            }
        }
        return connection;
    }

    public static void setConnection(Connection c) {
        connection = c;
    }

    /**
     * Ferme la connexion active si elle existe.
     * <p>
     * Cette méthode est optionnelle dans une application desktop,
     * mais utile pour garantir une libération propre des ressources.
     * </p>
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}
