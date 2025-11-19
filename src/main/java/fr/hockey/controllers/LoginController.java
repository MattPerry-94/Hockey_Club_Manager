package fr.hockey.controllers;

import fr.hockey.dao.AdminDAO;
import fr.hockey.dao.CoachDAO;
import fr.hockey.models.Admin;
import fr.hockey.models.Coach;
import fr.hockey.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Contrôleur responsable de la gestion de l'écran de connexion.
 * Il tente d'authentifier l'utilisateur comme administrateur ou comme coach
 * et initialise la session en conséquence. En cas de succès, la vue
 * du tableau de bord est chargée.
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private AdminDAO adminDAO = new AdminDAO();
    private CoachDAO coachDAO = new CoachDAO();

    /**
     * Gère le clic sur le bouton de connexion :
     * - vérifie que les champs ne sont pas vides
     * - tente une authentification comme administrateur
     * - sinon tente une authentification comme coach
     * - initialise la session et charge le tableau de bord si succès
     *
     * En cas d'échec, affiche un message d'erreur dans l'interface.
     *
     * @param event événement JavaFX du clic sur le bouton de connexion
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        try {
            // Authentification admin
            Admin admin = adminDAO.authenticate(username, password);
            if (admin != null) {
                SessionManager.getInstance().setCurrentAdmin(admin);
                loadDashboard(event, admin.getRole());
                return;
            }

            // Authentification coach
            Coach coach = coachDAO.authenticate(username, password);
            if (coach != null) {
                SessionManager.getInstance().setCurrentCoach(coach);
                loadDashboard(event, "COACH");
                return;
            }

            showError("Nom d'utilisateur ou mot de passe incorrect");

        } catch (Exception e) {
            showError("Erreur de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Affiche un message d’erreur dans le label prévu à cet effet.
     *
     * @param message texte à afficher
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * Charge la vue principale du tableau de bord après authentification.
     *
     * @param event événement d’origine (clic sur le bouton)
     * @param role rôle de l’utilisateur authentifié ("ADMIN" ou "COACH")
     *
     * @throws IOException si le fichier FXML ne peut pas être chargé
     */
    private void loadDashboard(ActionEvent event, String role) throws IOException {
        String fxmlFile = "/fxml/dashboard.fxml";

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent dashboardRoot = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Hockey Club Manager - Tableau de bord");
        stage.setScene(new Scene(dashboardRoot));
        stage.setMaximized(true);
        stage.show();
    }
}
