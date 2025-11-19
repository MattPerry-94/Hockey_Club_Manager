package fr.hockey.controllers;

import fr.hockey.dao.PlayerDAO;
import fr.hockey.models.Player;
import fr.hockey.utils.MatchSheetPdfGenerator;
import fr.hockey.utils.AppSettings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class MatchFormController implements Initializable {

    @FXML private ComboBox<String> categoryCombo;
    @FXML private DatePicker matchDatePicker;
    @FXML private TextField opponentField;
    @FXML private Label statusLabel;

    private final PlayerDAO playerDAO = new PlayerDAO();
    private static final List<String> CATEGORIES = Arrays.asList("U9", "U11", "U13", "U15", "U17", "U20");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryCombo.getItems().setAll(CATEGORIES);
        if (matchDatePicker != null) matchDatePicker.setValue(LocalDate.now());
    }

    public void setInitialCategory(String category) {
        if (category != null) {
            categoryCombo.getSelectionModel().select(category);
        }
    }

    @FXML
    private void handleSubmit() {
        String category = categoryCombo.getValue();
        if (category == null || category.trim().isEmpty()) {
            showWarn("Veuillez sélectionner une catégorie.");
            return;
        }
        LocalDate matchDate = matchDatePicker.getValue() != null ? matchDatePicker.getValue() : LocalDate.now();
        String opponent = safe(opponentField.getText());

        try {
            List<Player> list = playerDAO.findByCategory(category);
            list.sort(Comparator
                    .comparing((Player p) -> positionOrder(p.getPosition()))
                    .thenComparing(p -> safe(p.getLastName()), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(p -> safe(p.getFirstName()), String.CASE_INSENSITIVE_ORDER));

            FileChooser fc = new FileChooser();
            fc.setTitle("Enregistrer la feuille de match");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
            String dateStr = matchDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String opponentSlug = opponent.isEmpty() ? "" : ("_" + opponent.replaceAll("[^A-Za-z0-9]+", "-").replaceAll("-+", "-").replaceAll("^-|-$", ""));
            fc.setInitialFileName("Feuille-de-match_" + category + "_" + dateStr + opponentSlug + ".pdf");

            Stage stage = (Stage) categoryCombo.getScene().getWindow();
            File file = fc.showSaveDialog(stage);
            if (file == null) return;

            // Récupérer automatiquement le logo du club depuis les réglages
            File logoFile = null;
            String logoPath = AppSettings.getLogoPath();
            if (logoPath != null && !logoPath.isBlank()) {
                File lf = new File(logoPath);
                if (lf.exists()) logoFile = lf;
            }

            // Génération avec logo auto si disponible
            MatchSheetPdfGenerator.generate(category, list, file, matchDate, opponent, logoFile);

            showInfo("Feuille de match générée:\n" + file.getAbsolutePath());
            stage.close();
        } catch (SQLException ex) {
            showError("Erreur lors du chargement des joueurs: " + ex.getMessage());
        } catch (Exception ex) {
            showError("Erreur lors de la génération: " + ex.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) categoryCombo.getScene().getWindow();
        stage.close();
    }

    private int positionOrder(String pos) {
        if (pos == null) return 99;
        switch (pos.toUpperCase()) {
            case "GARDIEN": return 0;
            case "DEFENSEUR": return 1;
            case "ATTAQUANT": return 2;
            default: return 98;
        }
    }

    private String safe(String s) { return s == null ? "" : s.trim(); }

    private void showWarn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
    private void showInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
    private void showError(String msg) { new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait(); }
}