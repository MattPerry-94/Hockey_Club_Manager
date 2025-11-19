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

public class DashboardController implements Initializable {

    @FXML
    private Label userLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private Button changeLogoButton;

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

    @FXML
    private Button legalInfoButton;

    // Bouton Utilisateurs supprimé (pas d'interface utilisateurs)

    @FXML
    private StackPane contentArea;

    @FXML
    private ImageView logoImageView;

    @FXML
    private ImageView sidebarLogoImageView;

    @FXML
    private ComboBox<String> themeColorCombo;

    @FXML
    private BorderPane rootPane;

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
        if (legalInfoButton != null) {
            legalInfoButton.setVisible(true); // visible pour tous
        }
        if (createAdminButton != null) {
            createAdminButton.setVisible(isAdmin);
        }
        if (changeLogoButton != null) {
            changeLogoButton.setVisible(isAdmin);
        }

        if (themeColorCombo != null) {
            themeColorCombo.getItems().setAll(ThemeManager.getAvailableColors());
            String saved = AppSettings.getThemeColor();
            if (saved == null || saved.isBlank()) {
                saved = "Gris"; // défaut gris
            }
            themeColorCombo.getSelectionModel().select(saved);
            themeColorCombo.setVisible(isAdmin);
        }

        if (rootPane != null) {
            String initial = themeColorCombo != null && themeColorCombo.getValue() != null
                    ? themeColorCombo.getValue()
                    : (AppSettings.getThemeColor() != null ? AppSettings.getThemeColor() : "Gris");
            ThemeManager.applyTheme(rootPane, initial);
        }

        // Charger le logo s'il existe (header et barre latérale)
        String logoPath = AppSettings.getLogoPath();
        if (logoPath != null) {
            File file = new File(logoPath);
            if (file.exists()) {
                Image img = new Image(file.toURI().toString());
                if (logoImageView != null) {
                    logoImageView.setImage(img);
                }
                if (sidebarLogoImageView != null) {
                    sidebarLogoImageView.setImage(img);
                }
            }
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Effacer la session
            SessionManager.getInstance().clearSession();
            
            // Rediriger vers la page de connexion
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            // Réappliquer le thème pour garantir le style des boutons (gris)
            String saved = AppSettings.getThemeColor();
            if (saved == null || saved.isBlank()) {
                saved = "Gris";
            }
            ThemeManager.applyTheme(loginRoot, saved);
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

    @FXML
    private void showLegalInfo(ActionEvent event) {
        loadView("/fxml/legal_info.fxml");
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
                if (logoImageView != null) {
                    logoImageView.setImage(img);
                }
                if (sidebarLogoImageView != null) {
                    sidebarLogoImageView.setImage(img);
                }
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

    @FXML
    private void handleThemeChanged(ActionEvent event) {
        if (themeColorCombo != null && rootPane != null) {
            String selected = themeColorCombo.getValue();
            ThemeManager.applyTheme(rootPane, selected);
            AppSettings.setThemeColor(selected);
        }
    }
}