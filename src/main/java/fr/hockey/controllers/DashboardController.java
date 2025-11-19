package fr.hockey.controllers;

import fr.hockey.models.Admin;
import fr.hockey.models.Coach;
import fr.hockey.utils.SessionManager;
import fr.hockey.utils.AppSettings;
import fr.hockey.utils.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur principal du tableau de bord après connexion.
 * Il gère :
 * - l’affichage du nom et du rôle de l’utilisateur
 * - la navigation entre les différentes vues (joueurs, coachs, catégories…)
 * - la gestion du thème
 * - le changement du logo du club
 * - la déconnexion
 * Les fonctionnalités affichées dépendent du rôle (admin ou coach).
 */
public class DashboardController implements Initializable {

    @FXML private Label userLabel;
    @FXML private Button logoutButton;
    @FXML private Button changeLogoButton;
    @FXML private Button playersButton;
    @FXML private Button coachesButton;
    @FXML private Button licensesButton;
    @FXML private Button categoriesButton;
    @FXML private Button revenueButton;
    @FXML private Button createAdminButton;
    @FXML private Button legalInfoButton;
    @FXML private StackPane contentArea;
    @FXML private ImageView logoImageView;
    @FXML private ImageView sidebarLogoImageView;
    @FXML private ComboBox<String> themeColorCombo;
    @FXML private BorderPane rootPane;

    /**
     * Initialise l’interface selon le rôle utilisateur :
     * - affiche le nom de l’utilisateur connecté
     * - active/désactive les boutons réservés à l’admin
     * - applique le thème sauvegardé
     * - charge le logo s’il existe
     */
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
        categoriesButton.setVisible(true);
        coachesButton.setVisible(isAdmin);
        revenueButton.setVisible(isAdmin);

        if (legalInfoButton != null) legalInfoButton.setVisible(true);
        if (createAdminButton != null) createAdminButton.setVisible(isAdmin);
        if (changeLogoButton != null) changeLogoButton.setVisible(isAdmin);

        // Thème
        if (themeColorCombo != null) {
            themeColorCombo.getItems().setAll(ThemeManager.getAvailableColors());
            String saved = AppSettings.getThemeColor();
            if (saved == null || saved.isBlank()) saved = "Gris";
            themeColorCombo.getSelectionModel().select(saved);
            themeColorCombo.setVisible(isAdmin);
        }

        if (rootPane != null) {
            String initial = themeColorCombo != null && themeColorCombo.getValue() != null
                    ? themeColorCombo.getValue()
                    : (AppSettings.getThemeColor() != null ? AppSettings.getThemeColor() : "Gris");
            ThemeManager.applyTheme(rootPane, initial);
        }

        // Logo
        String logoPath = AppSettings.getLogoPath();
        if (logoPath != null) {
            File file = new File(logoPath);
            if (file.exists()) {
                Image img = new Image(file.toURI().toString());
                if (logoImageView != null) logoImageView.setImage(img);
                if (sidebarLogoImageView != null) sidebarLogoImageView.setImage(img);
            }
        }
    }

    /**
     * Déconnecte l’utilisateur :
     * - efface la session
     * - recharge la vue de connexion
     * - applique le thème sauvegardé
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            SessionManager.getInstance().clearSession();

            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

            String saved = AppSettings.getThemeColor();
            if (saved == null || saved.isBlank()) saved = "Gris";
            ThemeManager.applyTheme(loginRoot, saved);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Club de Hockey - Connexion");
            stage.setScene(new Scene(loginRoot, 800, 600));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge la vue des joueurs.
     */
    @FXML
    private void showPlayers(ActionEvent event) {
        loadView("/fxml/players.fxml");
    }

    /**
     * Charge la vue des coachs (réservée aux admins).
     */
    @FXML
    private void showCoaches(ActionEvent event) {
        loadView("/fxml/coaches.fxml");
    }

    /**
     * Charge la vue des licences.
     */
    @FXML
    private void showLicenses(ActionEvent event) {
        loadView("/fxml/licenses.fxml");
    }

    /**
     * Charge la vue des catégories.
     */
    @FXML
    private void showCategories(ActionEvent event) {
        loadView("/fxml/categories.fxml");
    }

    /**
     * Charge la vue des revenus (réservée aux admins).
     */
    @FXML
    private void showRevenue(ActionEvent event) {
        loadView("/fxml/revenue.fxml");
    }

    /**
     * Affiche le formulaire de création d’administrateur (réservé admins).
     */
    @FXML
    private void showCreateAdmin(ActionEvent event) {
        loadView("/fxml/create_admin.fxml");
    }

    /**
     * Affiche la page des mentions légales (accessible à tous).
     */
    @FXML
    private void showLegalInfo(ActionEvent event) {
        loadView("/fxml/legal_info.fxml");
    }

    /**
     * Charge dynamiquement une vue dans la zone centrale du tableau de bord.
     *
     * @param fxmlPath chemin du fichier FXML à charger
     */
    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

        } catch (Exception e) {
            e.printStackTrace();

            StringBuilder sb = new StringBuilder();
            sb.append("Impossible de charger la vue: ").append(fxmlPath).append("\n")
                    .append(e).append("\n");

            Throwable cause = e.getCause();
            if (cause != null) {
                sb.append("Cause: ").append(cause).append("\n");
            }

            Alert a = new Alert(Alert.AlertType.ERROR, sb.toString(), ButtonType.OK);
            a.setHeaderText("Erreur d'affichage");
            a.showAndWait();
        }
    }

    /**
     * Permet à l’administrateur d’importer une image de logo :
     * - ouverture d’un FileChooser
     * - copie du fichier dans le dossier de configuration
     * - mise à jour immédiate du logo dans le tableau de bord
     * - enregistrement du chemin dans AppSettings
     */
    @FXML
    private void handleChangeLogo(ActionEvent event) {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Sélectionner un logo");
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            File selected = chooser.showOpenDialog(stage);

            if (selected != null) {
                Path dest = AppSettings.copyLogoToConfigDir(selected);
                AppSettings.setLogoPath(dest.toString());

                Image img = new Image(dest.toUri().toString());
                if (logoImageView != null) logoImageView.setImage(img);
                if (sidebarLogoImageView != null) sidebarLogoImageView.setImage(img);

                Alert ok = new Alert(Alert.AlertType.INFORMATION, "Logo mis à jour avec succès.");
                ok.setHeaderText(null);
                ok.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR, "Échec de la mise à jour du logo: " + e.getMessage(), ButtonType.OK);
            a.setHeaderText("Erreur import logo");
            a.showAndWait();
        }
    }

    /**
     * Gère le changement de thème :
     * - applique le thème sélectionné
     * - le sauvegarde dans AppSettings
     */
    @FXML
    private void handleThemeChanged(ActionEvent event) {
        if (themeColorCombo != null && rootPane != null) {
            String selected = themeColorCombo.getValue();
            ThemeManager.applyTheme(rootPane, selected);
            AppSettings.setThemeColor(selected);
        }
    }
}
