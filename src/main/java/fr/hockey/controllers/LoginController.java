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

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private AdminDAO adminDAO = new AdminDAO();
    private CoachDAO coachDAO = new CoachDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        try {
            Admin admin = adminDAO.authenticate(username, password);
            if (admin != null) {
                SessionManager.getInstance().setCurrentAdmin(admin);
                loadDashboard(event, admin.getRole());
                return;
            }

            // Essai d'authentification comme coach
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

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

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