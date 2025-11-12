package fr.hockey.tools;

import fr.hockey.dao.AdminDAO;
import fr.hockey.models.Admin;


public class CreateAdminTool {

    public static void main(String[] args) {
        try {
            Admin admin = new Admin(
                    0,               // id auto-inc
                    "MattP",         // username
                    "mdp123",        // password (sera haché)
                    "Matt",          // firstName
                    "Perry",         // lastName
                    "matt.perry@club.fr", // email (facultatif)
                    "ADMIN"          // role
            );

            AdminDAO dao = new AdminDAO();
            dao.save(admin); // insertion + hash BCrypt

            System.out.println("✅ Admin créé : " + admin.getUsername() + " (id=" + admin.getId() + ")");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création : " + e.getMessage());
            e.printStackTrace();
        }
    }
}