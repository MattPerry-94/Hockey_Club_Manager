package fr.hockey.utils;

import fr.hockey.models.Admin;
import fr.hockey.models.Coach;

public class SessionManager {
    private static SessionManager instance;
    private Admin currentAdmin;
    private Coach currentCoach;
    
    private SessionManager() {
        // Constructeur privé pour le singleton
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public Admin getCurrentAdmin() {
        return currentAdmin;
    }
    
    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
        this.currentCoach = null; // assure exclusivité
    }
    
    public Coach getCurrentCoach() {
        return currentCoach;
    }

    public void setCurrentCoach(Coach coach) {
        this.currentCoach = coach;
        this.currentAdmin = null; // assure exclusivité
    }

    public void clearSession() {
        this.currentAdmin = null;
        this.currentCoach = null;
    }
    
    public boolean isAdmin() {
        return currentAdmin != null && "ADMIN".equals(currentAdmin.getRole());
    }
    
    public boolean isCoach() {
        return currentCoach != null;
    }
}