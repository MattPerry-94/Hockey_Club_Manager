package fr.hockey.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * Contrôleur de la vue des logs d'audit.
 * Lit le fichier d'audit et fournit un rafraîchissement manuel.
 */
public class LogsController implements Initializable {

    @FXML
    private TextArea logTextArea;

    private Path auditLogPath() {
        return Paths.get(System.getProperty("user.home"), ".hockeyclubmanager", "audit.log");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadLogs();
    }

    @FXML
    private void handleRefresh() {
        loadLogs();
    }

    @FXML
    private void handleOpenFolder() {
        try {
            Path path = auditLogPath().getParent();
            if (path != null && Files.exists(path)) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(path.toFile());
                } else {
                    showInfo("Dossier des logs : " + path.toString());
                }
            } else {
                showInfo("Le dossier des logs n'existe pas encore.");
            }
        } catch (Exception e) {
            showError("Impossible d'ouvrir le dossier des logs: " + e.getMessage());
        }
    }

    private void loadLogs() {
        try {
            Path path = auditLogPath();
            if (Files.exists(path)) {
                // Lire le contenu entier; pour gros fichiers, on pourrait limiter.
                byte[] bytes = Files.readAllBytes(path);
                String content = new String(bytes, StandardCharsets.UTF_8);
                logTextArea.setText(content);
                logTextArea.positionCaret(content.length());
            } else {
                logTextArea.setText("Aucun log d'audit trouvé. Les événements seront affichés ici.");
            }
        } catch (IOException e) {
            showError("Erreur de lecture des logs: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        a.setHeaderText("Erreur");
        a.showAndWait();
    }

    private void showInfo(String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}

