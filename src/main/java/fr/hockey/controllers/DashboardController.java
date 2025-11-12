package fr.hockey.controllers;

import fr.hockey.models.Admin;
import fr.hockey.models.Coach;
import fr.hockey.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label userLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private Button playersButton;

    @FXML
    private Button coachesButton;

    @FXML
    private Button licensesButton;

    @FXML
    private Button categoriesButton;

    @FXML
    private Button revenueButton;

    @FXML
    private Button createAdminButton;

    // Bouton Utilisateurs supprimé (pas d'interface utilisateurs)

    @FXML
    private StackPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SessionManager session = SessionManager.getInstance();
        Admin currentAdmin = session.getCurrentAdmin();
        Coach currentCoach = session.getCurrentCoach();

        boolean isAdmin = session.isAdmin();
        boolean isCoach = session.isCoach();

        if (isAdmin && currentAdmin != null) {
            userLabel.setText("Administrateur: " + currentAdmin.getFirstName() + " " + currentAdmin.getLastName());
        } else if (isCoach && currentCoach != null) {
            userLabel.setText("Coach: " + currentCoach.getFirstName() + " " + currentCoach.getLastName());
        }

        // Visibilité selon rôle
        playersButton.setVisible(true);
        licensesButton.setVisible(true);
        categoriesButton.setVisible(true); // visible pour les deux
        coachesButton.setVisible(isAdmin); // réservé admin
        revenueButton.setVisible(isAdmin); // réservé admin
        if (createAdminButton != null) {
            createAdminButton.setVisible(isAdmin);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Effacer la session
            SessionManager.getInstance().clearSession();
            
            // Rediriger vers la page de connexion
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Club de Hockey - Connexion");
            stage.setScene(new Scene(loginRoot, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showPlayers(ActionEvent event) {
        loadView("/fxml/players.fxml");
    }

    @FXML
    private void showCoaches(ActionEvent event) {
        loadView("/fxml/coaches.fxml");
    }

    @FXML
    private void showLicenses(ActionEvent event) {
        loadView("/fxml/licenses.fxml");
    }

    @FXML
    private void showCategories(ActionEvent event) {
        loadView("/fxml/categories.fxml");
    }

    @FXML
    private void showRevenue(ActionEvent event) {
        loadView("/fxml/revenue.fxml");
    }

    @FXML
    private void showCreateAdmin(ActionEvent event) {
        // réservé à l’admin par visibilité; on charge la vue
        loadView("/fxml/create_admin.fxml");
    }

    // Méthode showUsers supprimée

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            e.printStackTrace();
            StringBuilder sb = new StringBuilder();
            sb.append("Impossible de charger la vue: ").append(fxmlPath).append("\n");
            sb.append(e.toString()).append("\n");
            Throwable cause = e.getCause();
            if (cause != null) {
                sb.append("Cause: ").append(cause.toString()).append("\n");
            }
            Alert a = new Alert(Alert.AlertType.ERROR, sb.toString(), ButtonType.OK);
            a.setHeaderText("Erreur d'affichage");
            a.showAndWait();
        }
    }
}