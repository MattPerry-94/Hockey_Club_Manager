package fr.hockey.utils;

import fr.hockey.models.Admin;
import fr.hockey.models.Coach;

/**
 * Gestion centralisée de la session utilisateur.
 *
 * <p>Ce singleton permet de stocker l'utilisateur actuellement connecté,
 * qu'il s'agisse d'un administrateur ou d'un coach. Les deux types de comptes
 * sont mutuellement exclusifs : lorsqu'un admin est défini, le coach est nul,
 * et inversement.</p>
 *
 * <p>Cette classe fournit également des méthodes utilitaires pour vérifier
 * le rôle de l'utilisateur courant.</p>
 */
public class SessionManager {

    /** Instance unique du singleton. */
    private static SessionManager instance;

    /** Administrateur actuellement connecté (ou null). */
    private Admin currentAdmin;

    /** Coach actuellement connecté (ou null). */
    private Coach currentCoach;

    /**
     * Constructeur privé pour empêcher l’instanciation directe.
     */
    private SessionManager() {
        // Constructeur privé pour le singleton
    }

    /**
     * Retourne l’instance unique du SessionManager (pattern Singleton).
     *
     * @return instance unique
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Retourne l'administrateur actuellement connecté.
     *
     * @return Admin ou null
     */
    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    /**
     * Définit l'administrateur actuellement connecté.
     * Ce choix réinitialise automatiquement tout coach connecté.
     *
     * @param admin administrateur connecté
     */
    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
        this.currentCoach = null; // assure exclusivité
    }

    /**
     * Retourne le coach actuellement connecté.
     *
     * @return Coach ou null
     */
    public Coach getCurrentCoach() {
        return currentCoach;
    }

    /**
     * Définit le coach actuellement connecté.
     * Ce choix réinitialise automatiquement tout administrateur connecté.
     *
     * @param coach coach connecté
     */
    public void setCurrentCoach(Coach coach) {
        this.currentCoach = coach;
        this.currentAdmin = null; // assure exclusivité
    }

    /**
     * Réinitialise complètement la session courante (coach + admin).
     */
    public void clearSession() {
        this.currentAdmin = null;
        this.currentCoach = null;
    }

    /**
     * Indique si l'utilisateur connecté est un administrateur.
     *
     * @return true si admin connecté, false sinon
     */
    public boolean isAdmin() {
        return currentAdmin != null && "ADMIN".equals(currentAdmin.getRole());
    }

    /**
     * Indique si l'utilisateur connecté est un coach.
     *
     * @return true si coach connecté, false sinon
     */
    public boolean isCoach() {
        return currentCoach != null;
    }
}
