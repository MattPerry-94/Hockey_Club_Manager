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

import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTables();
        setupCategoryFilter();
    }

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

    private void setStatus(String msg, boolean ok) {
        if (statusLabel != null) {
            statusLabel.setText(msg);
            statusLabel.setStyle(ok ? "-fx-text-fill: #2c3e50;" : "-fx-text-fill: #c0392b;");
        }
    }
}