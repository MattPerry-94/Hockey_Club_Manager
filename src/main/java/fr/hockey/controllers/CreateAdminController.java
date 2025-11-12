package fr.hockey.controllers;

import fr.hockey.dao.AdminDAO;
import fr.hockey.models.Admin;
import fr.hockey.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class CreateAdminController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private Button createButton;
    @FXML private Label statusLabel;

    private final AdminDAO adminDAO = new AdminDAO();

    @FXML
    private void initialize() {
        boolean isAdmin = SessionManager.getInstance().isAdmin();
        createButton.setDisable(!isAdmin);
        if (!isAdmin) {
            statusLabel.setText("Accès réservé aux administrateurs.");
        }
    }

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

    private String safeTrim(String s) { return s == null ? "" : s.trim(); }

    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
    }
}