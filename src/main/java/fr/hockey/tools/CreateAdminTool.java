package fr.hockey.tools;

import fr.hockey.dao.AdminDAO;
import fr.hockey.models.Admin;

/**
 * Outil autonome permettant de créer rapidement un administrateur dans la base de données.
 *
 * <p>Ce programme est destiné à être exécuté manuellement par un développeur ou un
 * administrateur système lorsque l'application doit recevoir son premier compte ADMIN
 * (ou un compte admin d'urgence).</p>
 *
 * <p>Il crée un objet {@link Admin}, puis utilise {@link AdminDAO#save(Admin)} pour
 * insérer l'administrateur en base. Le mot de passe fourni en clair est automatiquement
 * haché via BCrypt dans le DAO.</p>
 *
 * <p>Usage : exécuter simplement la classe depuis votre IDE ou ligne de commande.</p>
 *
 * <pre>
 *   java fr.hockey.tools.CreateAdminTool
 * </pre>
 *
 * Le compte créé est affiché dans la console.
 */
public class CreateAdminTool {

    /**
     * Point d’entrée du programme.
     *
     * <p>Construit un administrateur avec les informations définies dans le code,
     * puis l’enregistre dans la base via {@link AdminDAO}. Si la création réussit,
     * l’identifiant généré est affiché.</p>
     *
     * @param args arguments non utilisés
     */
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
