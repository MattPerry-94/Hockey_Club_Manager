package fr.hockey.controllers;

import fr.hockey.dao.AdminDAO;
import fr.hockey.models.Admin;
import fr.hockey.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

/**
 * Contrôleur responsable de la création d'un nouvel administrateur.
 * Cette interface est réservée exclusivement aux utilisateurs possédant
 * les droits administrateurs.
 */
public class CreateAdminController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private Button createButton;
    @FXML private Label statusLabel;

    private final AdminDAO adminDAO = new AdminDAO();

    /**
     * Méthode appelée automatiquement lors du chargement du FXML.
     * Active ou désactive le bouton de création en fonction du rôle
     * de l'utilisateur courant (admin ou non).
     */
    @FXML
    private void initialize() {
        boolean isAdmin = SessionManager.getInstance().isAdmin();
        createButton.setDisable(!isAdmin);
        if (!isAdmin) {
            statusLabel.setText("Accès réservé aux administrateurs.");
        }
    }

    /**
     * Gère la création d'un nouvel administrateur.
     * Vérifie les champs, construit un objet Admin puis appelle
     * le DAO pour l'enregistrer dans la base de données.
     * Affiche un message de succès ou d'erreur selon le résultat.
     */
    @FXML
    private void handleCreate() {
        statusLabel.setText("");

        String username = safeTrim(usernameField.getText());
        String password = safeTrim(passwordField.getText());
        String first = safeTrim(firstNameField.getText());
        String last = safeTrim(lastNameField.getText());
        String email = safeTrim(emailField.getText());
        String role = "ADMIN";

        if (username.isEmpty() || password.isEmpty() || first.isEmpty() || last.isEmpty() || email.isEmpty()) {
            statusLabel.setText("Tous les champs sont obligatoires.");
            return;
        }

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password); // Le DAO hash le mot de passe
        admin.setFirstName(first);
        admin.setLastName(last);
        admin.setEmail(email);
        admin.setRole(role);

        try {
            boolean ok = adminDAO.save(admin);
            if (ok) {
                statusLabel.setStyle("-fx-text-fill: green;");
                statusLabel.setText("Admin créé: " + username + " (id=" + admin.getId() + ")");
                clearForm();
            } else {
                statusLabel.setText("Échec de la création.");
            }
        } catch (SQLException ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.toLowerCase().contains("duplicate")) {
                statusLabel.setText("Le nom d'utilisateur existe déjà.");
            } else {
                statusLabel.setText("Erreur SQL: " + ex.getMessage());
            }
        }
    }

    /**
     * Nettoie une chaîne : jamais null, trimée.
     *
     * @param s chaîne potentiellement nulle
     * @return chaîne nettoyée ou vide
     */
    private String safeTrim(String s) { return s == null ? "" : s.trim(); }

    /**
     * Réinitialise tous les champs du formulaire après une création réussie.
     */
    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
    }
}
