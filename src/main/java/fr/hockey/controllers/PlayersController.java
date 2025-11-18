package fr.hockey.controllers;

import fr.hockey.dao.PlayerDAO;
import fr.hockey.models.Player;
import fr.hockey.utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class PlayersController implements Initializable {

    @FXML
    private TableView<Player> playersTable;
    @FXML
    private TableColumn<Player, Integer> idColumn;
    @FXML
    private TableColumn<Player, String> numberColumn;
    @FXML
    private TableColumn<Player, String> firstNameColumn;
    @FXML
    private TableColumn<Player, String> lastNameColumn;
    @FXML
    private TableColumn<Player, String> categoryColumn;
    @FXML
    private TableColumn<Player, String> roleColumn;
    @FXML
    private TableColumn<Player, String> positionColumn;
    @FXML
    private TableColumn<Player, String> licenseStatusColumn;
    @FXML
    private TableColumn<Player, Void> actionsColumn;
    @FXML
    private ComboBox<String> categoryFilter;
    @FXML
    private ComboBox<String> positionFilter;
    @FXML
    private VBox formContainer;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private ComboBox<String> categoryCombo;
    @FXML
    private ComboBox<String> roleCombo;
    @FXML
    private ComboBox<String> positionCombo;
    @FXML
    private ComboBox<Integer> numberCombo;
    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;
    

    private final PlayerDAO playerDAO = new PlayerDAO();
    private Player currentPlayer;
    private boolean isEditMode = false;

    private final List<String> CATEGORIES = Arrays.asList("U9", "U11", "U13", "U15", "U17", "U20");
    private final List<String> ROLES = Arrays.asList("CAPITAINE", "ASSISTANT", "JOUEUR");
    private final List<String> POSITIONS = Arrays.asList("GARDIEN", "DEFENSEUR", "ATTAQUANT");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTable();
        setupFilters();
        setupForm();
        loadPlayers();
        boolean isAdmin = SessionManager.getInstance().isAdmin();
        boolean isCoach = SessionManager.getInstance().isCoach();
        if (!isAdmin && isCoach) {
            if (addButton != null) addButton.setDisable(true);
            // Le bouton Enregistrer reste utilisable en mode édition pour les coachs
            showForm(false);
        } else if (!isAdmin) {
            // Pour tout autre rôle non admin (sécurité par défaut)
            if (addButton != null) addButton.setDisable(true);
            if (saveButton != null) saveButton.setDisable(true);
            showForm(false);
        }
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        numberColumn.setCellValueFactory(cellData -> {
            int n = cellData.getValue().getNumber();
            return new SimpleStringProperty(n > 0 ? String.valueOf(n) : "");
        });
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

        licenseStatusColumn.setCellValueFactory(cellData -> {
            Player player = cellData.getValue();
            if (player.getLicense() == null) {
                return new SimpleStringProperty("Non licencié");
            } else {
                return new SimpleStringProperty(player.getLicense().isPaid() ? "Payée" : "Non payée");
            }
        });

        setupActionsColumn();
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Player, Void>, TableCell<Player, Void>> cellFactory = param -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");

            {
                editBtn.setOnAction(event -> {
                    Player player = getTableView().getItems().get(getIndex());
                    editPlayer(player);
                });
                deleteBtn.setOnAction(event -> {
                    Player player = getTableView().getItems().get(getIndex());
                    deletePlayer(player);
                });
                boolean isAdmin = SessionManager.getInstance().isAdmin();
                boolean isCoach = SessionManager.getInstance().isCoach();
                if (!isAdmin) {
                    // Autoriser l'édition pour coach, mais jamais la suppression
                    editBtn.setDisable(!isCoach);
                    deleteBtn.setDisable(true);
                }
                // Styles de thème
                editBtn.setStyle("-fx-background-color: -app-button-bg; -fx-text-fill: -app-on-accent;");
                deleteBtn.setStyle("-fx-background-color: -app-button-bg; -fx-text-fill: -app-on-accent;");
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        };
        actionsColumn.setCellFactory(cellFactory);
    }

    private void setupFilters() {
        categoryFilter.getItems().add("Toutes");
        categoryFilter.getItems().addAll(CATEGORIES);
        categoryFilter.setValue("Toutes");
        categoryFilter.setOnAction(e -> loadPlayers());

        positionFilter.getItems().add("Tous");
        positionFilter.getItems().addAll(POSITIONS);
        positionFilter.setValue("Tous");
        positionFilter.setOnAction(e -> loadPlayers());
    }

    private void setupForm() {
        categoryCombo.getItems().addAll(CATEGORIES);
        roleCombo.getItems().addAll(ROLES);
        positionCombo.getItems().addAll(POSITIONS);
        // Numéros de 1 à 99
        ObservableList<Integer> numbers = FXCollections.observableArrayList();
        for (int i = 1; i <= 99; i++) {
            numbers.add(i);
        }
        numberCombo.setItems(numbers);
    }

    private void loadPlayers() {
        try {
            List<Player> players;
            String categoryValue = categoryFilter.getValue();
            String positionValue = positionFilter.getValue();

            if ("Toutes".equals(categoryValue) && "Tous".equals(positionValue)) {
                players = playerDAO.findAll();
            } else if ("Toutes".equals(categoryValue)) {
                players = playerDAO.findByPosition(positionValue);
            } else if ("Tous".equals(positionValue)) {
                players = playerDAO.findByCategory(categoryValue);
            } else {
                players = playerDAO.findByCategoryAndPosition(categoryValue, positionValue);
            }

            ObservableList<Player> data = FXCollections.observableArrayList(players);
            playersTable.setItems(data);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des joueurs", e.getMessage());
        }
    }

    

    @FXML
    private void handleAddPlayer() {
        if (!SessionManager.getInstance().isAdmin()) {
            showAlert(Alert.AlertType.INFORMATION, "Lecture seule", "Action non autorisée", "Seul un administrateur peut ajouter des joueurs.");
            return;
        }
        isEditMode = false;
        currentPlayer = new Player();
        clearForm();
        showForm(true);
    }

    private void editPlayer(Player player) {
        boolean isAdmin = SessionManager.getInstance().isAdmin();
        boolean isCoach = SessionManager.getInstance().isCoach();
        if (!(isAdmin || isCoach)) {
            showAlert(Alert.AlertType.INFORMATION, "Lecture seule", "Action non autorisée", "Vous n'avez pas les droits pour modifier.");
            return;
        }
        isEditMode = true;
        currentPlayer = player;

        firstNameField.setText(player.getFirstName());
        lastNameField.setText(player.getLastName());
        categoryCombo.setValue(player.getCategory());
        roleCombo.setValue(player.getRole());
        positionCombo.setValue(player.getPosition());
        if (player.getNumber() > 0) {
            numberCombo.setValue(player.getNumber());
        } else {
            numberCombo.setValue(null);
        }
        // En mode coach: seul le rôle est modifiable
        if (isCoach && !isAdmin) {
            firstNameField.setDisable(true);
            lastNameField.setDisable(true);
            categoryCombo.setDisable(true);
            positionCombo.setDisable(true);
            numberCombo.setDisable(true);
            roleCombo.setDisable(false);
            saveButton.setDisable(false);
        } else {
            // Admin: tous les champs modifiables
            firstNameField.setDisable(false);
            lastNameField.setDisable(false);
            categoryCombo.setDisable(false);
            positionCombo.setDisable(false);
            numberCombo.setDisable(false);
            roleCombo.setDisable(false);
        }
        showForm(true);
    }

    private void deletePlayer(Player player) {
        if (!SessionManager.getInstance().isAdmin()) {
            showAlert(Alert.AlertType.INFORMATION, "Lecture seule", "Action non autorisée", "Seul un administrateur peut supprimer des joueurs.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le joueur");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + player.getFirstName() + " " + player.getLastName() + " ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    playerDAO.delete(player.getId());
                    loadPlayers();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression", e.getMessage());
                }
            }
        });
    }


    @FXML
    private void handleSave() {
        boolean isAdmin = SessionManager.getInstance().isAdmin();
        boolean isCoach = SessionManager.getInstance().isCoach();
        if (!(isAdmin || isCoach)) {
            showAlert(Alert.AlertType.INFORMATION, "Lecture seule", "Action non autorisée", "Vous n'avez pas les droits pour enregistrer des modifications.");
            return;
        }
        if (!validateForm()) {
            return;
        }

        currentPlayer.setFirstName(firstNameField.getText());
        currentPlayer.setLastName(lastNameField.getText());
        currentPlayer.setCategory(categoryCombo.getValue());
        currentPlayer.setRole(roleCombo.getValue());
        currentPlayer.setPosition(positionCombo.getValue());
        Integer numVal = numberCombo.getValue();
        currentPlayer.setNumber(numVal != null ? numVal : 0);

        try {
            playerDAO.save(currentPlayer);
            showForm(false);
            loadPlayers();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        showForm(false);
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        boolean isAdmin = SessionManager.getInstance().isAdmin();
        boolean isCoach = SessionManager.getInstance().isCoach();

        if (firstNameField.getText().trim().isEmpty()) errors.append("- Le prénom est requis\n");
        if (lastNameField.getText().trim().isEmpty()) errors.append("- Le nom est requis\n");
        if (categoryCombo.getValue() == null) errors.append("- La catégorie est requise\n");
        if (roleCombo.getValue() == null) errors.append("- Le rôle est requis\n");
        if (positionCombo.getValue() == null) errors.append("- Le poste est requis\n");
        // Exiger le numéro uniquement lors de la création (admin) ou édition admin
        if (!isEditMode && numberCombo.getValue() == null) {
            errors.append("- Le numéro est requis\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation", "Veuillez corriger les erreurs suivantes:", errors.toString());
            return false;
        }
        return true;
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        categoryCombo.setValue(null);
        roleCombo.setValue(null);
        positionCombo.setValue(null);
        numberCombo.setValue(null);
    }

    private void showForm(boolean show) {
        formContainer.setVisible(show);
        formContainer.setManaged(show);
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}