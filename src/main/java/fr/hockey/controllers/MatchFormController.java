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

/**
 * Contrôleur gérant le formulaire permettant de générer une feuille de match PDF.
 * L’utilisateur sélectionne :
 *  - une catégorie,
 *  - une date de match,
 *  - un adversaire.
 *
 * Le contrôleur charge automatiquement les joueurs de la catégorie,
 * les trie selon la position puis génère un PDF grâce à MatchSheetPdfGenerator.
 */
public class MatchFormController implements Initializable {

    @FXML private ComboBox<String> categoryCombo;
    @FXML private DatePicker matchDatePicker;
    @FXML private TextField opponentField;
    @FXML private Label statusLabel;

    private final PlayerDAO playerDAO = new PlayerDAO();
    private static final List<String> CATEGORIES = Arrays.asList("U9", "U11", "U13", "U15", "U17", "U20");

    /**
     * Initialise le formulaire :
     *  - charge la liste des catégories dans le ComboBox
     *  - définit la date du match sur aujourd’hui
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryCombo.getItems().setAll(CATEGORIES);
        if (matchDatePicker != null) matchDatePicker.setValue(LocalDate.now());
    }

    /**
     * Définit automatiquement la catégorie sélectionnée dans le ComboBox.
     * Cette méthode est utilisée lorsque le formulaire est ouvert depuis une autre vue.
     *
     * @param category catégorie à sélectionner
     */
    public void setInitialCategory(String category) {
        if (category != null) {
            categoryCombo.getSelectionModel().select(category);
        }
    }

    /**
     * Gère la soumission du formulaire :
     *  - vérification des champs
     *  - récupération des joueurs de la catégorie
     *  - tri selon la position puis nom/prénom
     *  - choix d’un emplacement de sauvegarde PDF
     *  - génération de la feuille de match
     *
     * En cas d’erreur, un message adapté est affiché.
     */
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

            // Tri des joueurs (position > nom > prénom)
            list.sort(Comparator
                    .comparing((Player p) -> positionOrder(p.getPosition()))
                    .thenComparing(p -> safe(p.getLastName()), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(p -> safe(p.getFirstName()), String.CASE_INSENSITIVE_ORDER));

            // Fenêtre de sauvegarde PDF
            FileChooser fc = new FileChooser();
            fc.setTitle("Enregistrer la feuille de match");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

            String dateStr = matchDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String opponentSlug = opponent.isEmpty()
                    ? ""
                    : ("_" + opponent.replaceAll("[^A-Za-z0-9]+", "-")
                    .replaceAll("-+", "-")
                    .replaceAll("^-|-$", ""));

            fc.setInitialFileName("Feuille-de-match_" + category + "_" + dateStr + opponentSlug + ".pdf");

            Stage stage = (Stage) categoryCombo.getScene().getWindow();
            File file = fc.showSaveDialog(stage);
            if (file == null) return;

            // Logo automatique si disponible
            File logoFile = null;
            String logoPath = AppSettings.getLogoPath();
            if (logoPath != null && !logoPath.isBlank()) {
                File lf = new File(logoPath);
                if (lf.exists()) logoFile = lf;
            }

            // Génération du PDF
            MatchSheetPdfGenerator.generate(category, list, file, matchDate, opponent, logoFile);

            showInfo("Feuille de match générée:\n" + file.getAbsolutePath());
            stage.close();

        } catch (SQLException ex) {
            showError("Erreur lors du chargement des joueurs: " + ex.getMessage());
        } catch (Exception ex) {
            showError("Erreur lors de la génération: " + ex.getMessage());
        }
    }

    /**
     * Ferme la fenêtre du formulaire sans action.
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) categoryCombo.getScene().getWindow();
        stage.close();
    }

    /**
     * Détermine un ordre numérique stable pour les positions :
     * 0 = Gardien, 1 = Défenseur, 2 = Attaquant, 98 = inconnu, 99 = null.
     *
     * @param pos position textuelle du joueur
     * @return indice utilisé pour le tri
     */
    private int positionOrder(String pos) {
        if (pos == null) return 99;
        switch (pos.toUpperCase()) {
            case "GARDIEN": return 0;
            case "DEFENSEUR": return 1;
            case "ATTAQUANT": return 2;
            default: return 98;
        }
    }

    /**
     * Sécurise une chaîne :
     * - jamais null,
     * - trimée.
     */
    private String safe(String s) { return s == null ? "" : s.trim(); }

    /** Affiche un avertissement. */
    private void showWarn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }

    /** Affiche une information. */
    private void showInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }

    /** Affiche une erreur. */
    private void showError(String msg) { new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait(); }
}