package fr.hockey.controllers;

import fr.hockey.dao.CoachDAO;
import fr.hockey.models.Coach;
import fr.hockey.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur JavaFX gérant l’affichage, la création, la modification
 * et la suppression des coachs dans l’application.
 * Le comportement des actions dépend du rôle de l’utilisateur connecté (admin ou non).
 */
public class CoachesController implements Initializable {

    @FXML private TableView<Coach> coachesTable;
    @FXML private TableColumn<Coach, String> firstNameColumn;
    @FXML private TableColumn<Coach, String> lastNameColumn;
    @FXML private TableColumn<Coach, String> teamsColumn;
    @FXML private TableColumn<Coach, Void> actionsColumn;
    @FXML private Label roleInfoLabel;

    // Champs du formulaire de création/édition de coach
    @FXML private TextField coachUsernameField;
    @FXML private PasswordField coachPasswordField;
    @FXML private TextField coachFirstNameField;
    @FXML private TextField coachLastNameField;
    @FXML private TextField coachEmailField;
    @FXML private ComboBox<String> coachCategoryCombo;
    @FXML private Button createCoachButton;
    @FXML private Label createStatusLabel;
    @FXML private Button cancelCreateCoachButton;
    @FXML private Button addCoachButton;
    @FXML private VBox coachFormContainer;

    private final CoachDAO coachDAO = new CoachDAO();
    private final ObservableList<Coach> coaches = FXCollections.observableArrayList();
    private boolean isAdmin;
    private final List<String> CATEGORIES = java.util.Arrays.asList("U9", "U11", "U13", "U15", "U17", "U20");
    private boolean isEditMode = false;
    private Coach editingCoach;

    /**
     * Méthode appelée automatiquement par JavaFX au chargement du contrôleur.
     * Initialise l’interface, configure les tables, active/désactive
     * les fonctionnalités selon le rôle, et charge la liste des coachs.
     *
     * @param url non utilisé.
     * @param resourceBundle non utilisé.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        isAdmin = SessionManager.getInstance().isAdmin();
        roleInfoLabel.setText(isAdmin ? "Rôle: Administrateur" : "Rôle: Coach (lecture seule)");

        setupTable();
        setupActionsColumn();
        setupCreateForm();
        showForm(false);
        reload();
    }

    /**
     * Configure les colonnes du tableau affichant la liste des coachs :
     * prénom, nom et liste des catégories assignées.
     */
    private void setupTable() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        teamsColumn.setCellValueFactory(cell -> {
            Coach c = cell.getValue();
            String text = (c.getTeams() == null || c.getTeams().isEmpty()) ? "-" : String.join(", ", c.getTeams());
            return new javafx.beans.property.SimpleStringProperty(text);
        });
    }

    /**
     * Configure la colonne contenant les boutons d'action (modifier/supprimer).
     * Les actions sont désactivées pour les utilisateurs non administrateurs.
     */
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox box = new HBox(8, editBtn, deleteBtn);

            {
                editBtn.setOnAction(e -> {
                    Coach c = getTableView().getItems().get(getIndex());
                    editCoach(c);
                });

                deleteBtn.setOnAction(e -> {
                    Coach c = getTableView().getItems().get(getIndex());
                    deleteCoach(c);
                });

                editBtn.setStyle("-fx-background-color: -app-button-bg; -fx-text-fill: -app-on-accent;");
                deleteBtn.setStyle("-fx-background-color: -app-button-bg; -fx-text-fill: -app-on-accent;");

                if (!SessionManager.getInstance().isAdmin()) {
                    editBtn.setDisable(true);
                    deleteBtn.setDisable(true);
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    /**
     * Recharge la liste des coachs depuis la base de données et met à jour l’affichage.
     * Affiche un message d’erreur en cas de problème SQL.
     */
    private void reload() {
        try {
            List<Coach> all = coachDAO.findAll();
            coaches.setAll(all);
            coachesTable.setItems(coaches);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /**
     * Configure le formulaire de création en activant ou désactivant les champs
     * selon les droits administrateurs. Initialise la liste des catégories.
     */
    private void setupCreateForm() {
        boolean admin = SessionManager.getInstance().isAdmin();
        if (createCoachButton != null) createCoachButton.setDisable(!admin);
        if (cancelCreateCoachButton != null) cancelCreateCoachButton.setDisable(!admin);
        if (addCoachButton != null) addCoachButton.setDisable(!admin);
        if (coachCategoryCombo != null) {
            coachCategoryCombo.setItems(FXCollections.observableArrayList(CATEGORIES));
        }
        if (!admin && createStatusLabel != null) {
            createStatusLabel.setText("Accès à la création réservé aux administrateurs.");
        }
    }

    /**
     * Gère la création ou modification d’un coach selon que le mode édition est activé.
     * Vérifie les champs, appelle les DAO correspondants et met à jour l’état visuel.
     */
    @FXML
    private void handleCreateCoach() {
        if (!SessionManager.getInstance().isAdmin()) {
            if (createStatusLabel != null) {
                createStatusLabel.setText("Action réservée aux administrateurs.");
            }
            return;
        }

        String username = safeTrim(coachUsernameField.getText());
        String password = safeTrim(coachPasswordField.getText());
        String first = safeTrim(coachFirstNameField.getText());
        String last = safeTrim(coachLastNameField.getText());
        String email = safeTrim(coachEmailField.getText());
        String category = coachCategoryCombo == null ? "" : safeTrim(coachCategoryCombo.getValue());

        // Validation
        if (!isEditMode) {
            if (username.isEmpty() || password.isEmpty() || first.isEmpty() || last.isEmpty() || email.isEmpty() || category.isEmpty()) {
                setStatus("Tous les champs sont obligatoires, y compris la catégorie.", false);
                return;
            }
        } else {
            if (username.isEmpty() || first.isEmpty() || last.isEmpty() || email.isEmpty() || category.isEmpty()) {
                setStatus("Tous les champs (hors mot de passe) sont obligatoires.", false);
                return;
            }
        }

        // Création
        if (!isEditMode) {
            try {
                int coachId = coachDAO.createCoach(username, password, first, last, email);
                if (coachId > 0) {
                    coachDAO.addTeam(coachId, category);
                    setStatus("Coach créé: " + username + " (id=" + coachId + "), catégorie: " + category, true);
                    clearCreateForm();
                    showForm(false);
                    reload();
                } else {
                    setStatus("Échec de la création.", false);
                }
            } catch (SQLException ex) {
                String msg = ex.getMessage();
                if (msg != null && msg.toLowerCase().contains("duplicate")) {
                    setStatus("Le nom d'utilisateur existe déjà.", false);
                } else {
                    setStatus("Erreur SQL: " + ex.getMessage(), false);
                }
            }
        }
        // Modification
        else {
            try {
                boolean ok = coachDAO.updateCoach(editingCoach.getId(), username, first, last, email, password);
                if (ok) {
                    coachDAO.deleteTeams(editingCoach.getId());
                    coachDAO.addTeam(editingCoach.getId(), category);
                    setStatus("Coach mis à jour: " + username + ", catégorie: " + category, true);
                    isEditMode = false;
                    editingCoach = null;
                    clearCreateForm();
                    showForm(false);
                    reload();
                } else {
                    setStatus("Aucune mise à jour effectuée.", false);
                }
            } catch (SQLException ex) {
                setStatus("Erreur SQL: " + ex.getMessage(), false);
            }
        }
    }

    /**
     * Définit le message de statut du formulaire de création/modification.
     *
     * @param text    texte à afficher.
     * @param success true si l’opération a réussi, false sinon.
     */
    private void setStatus(String text, boolean success) {
        if (createStatusLabel != null) {
            createStatusLabel.setText(text);
            createStatusLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        }
    }

    /**
     * Retourne une chaîne trimée et jamais nulle.
     *
     * @param s chaîne potentiellement nulle.
     * @return chaîne trimée ou vide.
     */
    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    /**
     * Réinitialise tous les champs du formulaire de création/modification.
     */
    private void clearCreateForm() {
        if (coachUsernameField != null) coachUsernameField.clear();
        if (coachPasswordField != null) coachPasswordField.clear();
        if (coachFirstNameField != null) coachFirstNameField.clear();
        if (coachLastNameField != null) coachLastNameField.clear();
        if (coachEmailField != null) coachEmailField.clear();
        if (coachCategoryCombo != null) coachCategoryCombo.getSelectionModel().clearSelection();
        if (createCoachButton != null) createCoachButton.setText("Enregistrer");
    }

    /**
     * Gestion du clic sur le bouton "Annuler" lors de la création/édition.
     */
    @FXML
    private void handleCancelCreateCoach() {
        clearCreateForm();
        setStatus("", true);
        showForm(false);
    }

    /**
     * Affiche le formulaire vide pour ajouter un coach.
     */
    @FXML
    private void handleAddCoach() {
        setStatus("", true);
        showForm(true);
    }

    /**
     * Affiche ou cache le bloc de formulaire.
     *
     * @param show true pour afficher, false pour cacher.
     */
    private void showForm(boolean show) {
        if (coachFormContainer != null) {
            coachFormContainer.setVisible(show);
            coachFormContainer.setManaged(show);
        }
    }

    /**
     * Prépare le formulaire pour éditer un coach existant :
     * remplit les champs et active le mode édition.
     *
     * @param coach le coach à modifier.
     */
    private void editCoach(Coach coach) {
        if (!SessionManager.getInstance().isAdmin()) return;

        editingCoach = coach;
        isEditMode = true;

        if (coachUsernameField != null) coachUsernameField.setText(coach.getUsername());
        if (coachPasswordField != null) coachPasswordField.clear();
        if (coachFirstNameField != null) coachFirstNameField.setText(coach.getFirstName());
        if (coachLastNameField != null) coachLastNameField.setText(coach.getLastName());
        if (coachEmailField != null) coachEmailField.setText(coach.getEmail());

        if (coachCategoryCombo != null) {
            String cat = (coach.getTeams() == null || coach.getTeams().isEmpty()) ? null : coach.getTeams().get(0);
            coachCategoryCombo.getSelectionModel().select(cat);
        }

        if (createCoachButton != null) createCoachButton.setText("Mettre à jour");
        showForm(true);
    }

    /**
     * Supprime un coach après confirmation utilisateur,
     * puis recharge la liste des coachs.
     *
     * @param coach coach à supprimer.
     */
    private void deleteCoach(Coach coach) {
        if (!SessionManager.getInstance().isAdmin()) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le coach");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + coach.getFirstName() + " " + coach.getLastName() + " ?");

        alert.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    coachDAO.deleteById(coach.getId());
                    setStatus("Coach supprimé.", true);
                    reload();
                } catch (SQLException ex) {
                    setStatus("Erreur SQL: " + ex.getMessage(), false);
                }
            }
        });
    }

    /**
     * Affiche une alerte d'erreur générique, utilisée en cas de problème SQL.
     *
     * @param ex exception à afficher.
     */
    private void showError(Exception ex) {
        Alert a = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
        a.setHeaderText("Erreur");
        a.showAndWait();
    }
}
