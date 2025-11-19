package fr.hockey.controllers;

import fr.hockey.dao.CoachDAO;
import fr.hockey.dao.PlayerDAO;
import fr.hockey.models.Coach;
import fr.hockey.models.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur JavaFX responsable de l'affichage des joueurs et coachs
 * selon une catégorie sélectionnée dans l'interface.
 */
public class CategoriesController implements Initializable {

    @FXML private ComboBox<String> categoryCombo;

    // Table joueurs
    @FXML private TableView<Player> playersTable;
    @FXML private TableColumn<Player, String> pFirstNameColumn;
    @FXML private TableColumn<Player, String> pLastNameColumn;
    @FXML private TableColumn<Player, String> pRoleColumn;
    @FXML private TableColumn<Player, String> pPositionColumn;

    // Table coachs
    @FXML private TableView<Coach> coachesTable;
    @FXML private TableColumn<Coach, String> cFirstNameColumn;
    @FXML private TableColumn<Coach, String> cLastNameColumn;
    @FXML private TableColumn<Coach, String> cEmailColumn;

    @FXML private Label statusLabel;

    private final PlayerDAO playerDAO = new PlayerDAO();
    private final CoachDAO coachDAO = new CoachDAO();
    private final ObservableList<Player> players = FXCollections.observableArrayList();
    private final ObservableList<Coach> coaches = FXCollections.observableArrayList();

    private static final List<String> CATEGORIES = Arrays.asList("U9", "U11", "U13", "U15", "U17", "U20");

    /**
     * Méthode d'initialisation du contrôleur appelée automatiquement par JavaFX.
     * Configure les tables et le filtre de catégories.
     *
     * @param url non utilisé.
     * @param resourceBundle ressources localisées, non utilisées ici.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTables();
        setupCategoryFilter();
    }

    /**
     * Configure les colonnes des tables (joueurs et coachs) en associant
     * chaque colonne à la propriété correspondante des modèles Player et Coach.
     * Initialise les listes observables utilisées par les TableView.
     */
    private void setupTables() {
        // Players
        pFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        pLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        pRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        pPositionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        playersTable.setItems(players);

        // Coaches
        cFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        cLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        cEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        coachesTable.setItems(coaches);
    }

    /**
     * Initialise le ComboBox des catégories :
     * - Remplit les valeurs disponibles
     * - Définit le texte par défaut
     * - Ajoute un listener déclenchant le chargement des joueurs/coach
     *   lorsqu'une catégorie est sélectionnée.
     */
    private void setupCategoryFilter() {
        categoryCombo.getItems().setAll(CATEGORIES);
        categoryCombo.setPromptText("Sélectionnez une catégorie");
        categoryCombo.setOnAction(e -> {
            String cat = categoryCombo.getValue();
            if (cat != null && !cat.trim().isEmpty()) {
                loadByCategory(cat);
            }
        });
    }

    /**
     * Charge les joueurs et les coachs correspondant à une catégorie donnée,
     * en utilisant les DAO PlayerDAO et CoachDAO.
     * Met également à jour le message de statut affiché.
     *
     * @param category catégorie sélectionnée (ex : "U15").
     */
    private void loadByCategory(String category) {
        try {
            players.setAll(playerDAO.findByCategory(category));
        } catch (SQLException ex) {
            setStatus("Erreur joueurs: " + ex.getMessage(), false);
        }

        try {
            coaches.setAll(coachDAO.findByCategory(category));
        } catch (SQLException ex) {
            setStatus("Erreur coachs: " + ex.getMessage(), false);
        }

        setStatus("Catégorie: " + category + " — " + players.size() + " joueurs, " + coaches.size() + " coachs", true);
    }

    /**
     * Met à jour le label de statut avec un message et une couleur distincte
     * selon que l'opération est réussie ou non.
     *
     * @param msg message à afficher.
     * @param ok  true si l'opération a réussi, false en cas d'erreur.
     */
    private void setStatus(String msg, boolean ok) {
        if (statusLabel != null) {
            statusLabel.setText(msg);
            statusLabel.setStyle(ok ? "-fx-text-fill: #2c3e50;" : "-fx-text-fill: #c0392b;");
        }
    }

    /**
     * Ouvre la fenêtre permettant de générer une feuille de match au format PDF.
     * Charge le fichier FXML correspondant, instancie son contrôleur puis,
     * si disponible, précharge la catégorie actuellement sélectionnée.
     * Affiche ensuite la fenêtre en modal.
     *
     * En cas d'erreur (chargement FXML, instanciation, etc.),
     * une alerte est affichée à l'utilisateur.
     */
    @FXML
    private void handleGeneratePdf() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/match_form.fxml"));
            javafx.scene.Parent root = loader.load();
            MatchFormController ctrl = loader.getController();

            String current = categoryCombo != null ? categoryCombo.getValue() : null;
            if (current != null) ctrl.setInitialCategory(current);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Formulaire de feuille de match");
            stage.initOwner(playersTable.getScene().getWindow());
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

        } catch (Exception ex) {
            Alert a = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
            a.setHeaderText("Impossible d'ouvrir le formulaire");
            a.showAndWait();
        }
    }

    /**
     * Retourne un ordre numérique permettant de trier les joueurs
     * en fonction de leur position : Gardien (0), Défenseur (1),
     * Attaquant (2), puis valeurs inconnues (98) et nulles (99).
     *
     * @param pos position textuelle du joueur.
     * @return un entier représentant l'ordre de tri.
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
     * Renvoie une version sécurisée d'une chaîne de caractères.
     * Si la valeur est null → chaîne vide, sinon → chaîne trimée.
     *
     * @param s chaîne potentiellement nulle.
     * @return chaîne nettoyée et non nulle.
     */
    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
