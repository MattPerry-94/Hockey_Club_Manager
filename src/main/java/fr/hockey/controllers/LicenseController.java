package fr.hockey.controllers;

import fr.hockey.dao.LicenseDAO;
import fr.hockey.dao.PlayerDAO;
import fr.hockey.models.License;
import fr.hockey.models.Player;
import fr.hockey.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Contrôleur gérant les licences des joueurs :
 * - affichage des joueurs et de leur statut de licence,
 * - création ou mise à jour de la licence,
 * - marquage payé / non payé.
 * Les actions de modification sont réservées aux administrateurs.
 */
public class LicenseController {

    @FXML private TableView<Player> playersTable;
    @FXML private TableColumn<Player, String> playerNameColumn;
    @FXML private TableColumn<Player, String> categoryColumn;
    @FXML private TableColumn<Player, String> licensePaidColumn;
    @FXML private TableColumn<Player, String> expirationColumn;
    @FXML private TableColumn<Player, String> amountColumn;
    @FXML private TableColumn<Player, Void> actionsColumn;
    @FXML private Label roleInfoLabel;

    private final PlayerDAO playerDAO = new PlayerDAO();
    private final LicenseDAO licenseDAO = new LicenseDAO();
    private final ObservableList<Player> players = FXCollections.observableArrayList();

    private boolean isAdmin;

    /**
     * Initialise l’interface :
     * - configure les colonnes du tableau,
     * - active / désactive les actions selon le rôle,
     * - installe la colonne d’actions,
     * - charge la liste des joueurs.
     */
    @FXML
    public void initialize() {
        isAdmin = SessionManager.getInstance().isAdmin();
        roleInfoLabel.setText(isAdmin ? "Rôle: Administrateur" : "Rôle: Coach (lecture seule)");

        // Nom complet
        playerNameColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getFirstName() + " " + cell.getValue().getLastName()
                )
        );

        // Catégorie
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        // Licence payée ou non
        licensePaidColumn.setCellValueFactory(cell -> {
            License lic = cell.getValue().getLicense();
            String text = lic == null ? "Aucune" : (lic.isPaid() ? "Payée" : "Non payée");
            return new javafx.beans.property.SimpleStringProperty(text);
        });

        // Date d'expiration
        expirationColumn.setCellValueFactory(cell -> {
            License lic = cell.getValue().getLicense();
            String text = lic == null || lic.getExpirationDate() == null ? "-" : lic.getExpirationDate().toString();
            return new javafx.beans.property.SimpleStringProperty(text);
        });

        // Montant
        amountColumn.setCellValueFactory(cell -> {
            License lic = cell.getValue().getLicense();
            String text = lic == null ? "-" : String.format("%.2f", lic.getAmount());
            return new javafx.beans.property.SimpleStringProperty(text);
        });

        // Colonne d’actions
        actionsColumn.setCellFactory(col -> new TableCell<>() {

            private final Button createBtn = new Button("Créer/MAJ");
            private final Button payBtn = new Button("Marquer Payée");
            private final Button unpayBtn = new Button("Marquer Non Payée");
            private final HBox box = new HBox(6, createBtn, payBtn, unpayBtn);

            {
                createBtn.setOnAction(this::handleCreateOrUpdate);
                payBtn.setOnAction(this::handleMarkPaid);
                unpayBtn.setOnAction(this::handleMarkUnpaid);

                // Thème
                createBtn.setStyle("-fx-background-color: -app-button-bg; -fx-text-fill: -app-on-accent;");
                payBtn.setStyle("-fx-background-color: -app-button-bg; -fx-text-fill: -app-on-accent;");
                unpayBtn.setStyle("-fx-background-color: -app-button-bg; -fx-text-fill: -app-on-accent;");

                // Restrictions pour les coaches
                if (!isAdmin) {
                    createBtn.setDisable(true);
                    payBtn.setDisable(true);
                    unpayBtn.setDisable(true);
                }
            }

            /**
             * Crée une nouvelle licence ou met à jour la licence existante :
             * - calcule le montant selon catégorie,
             * - fixe l’expiration à un an,
             * - insert/update dans la base.
             */
            private void handleCreateOrUpdate(ActionEvent e) {
                Player p = getTableView().getItems().get(getIndex());
                try {
                    License lic = licenseDAO.findByPlayerId(p.getId());
                    double fee = licenseDAO.getFeeForCategory(p.getCategory());
                    LocalDate exp = LocalDate.now().plusYears(1);

                    if (lic == null) {
                        licenseDAO.createForPlayer(p.getId(), fee, exp, false);
                    } else {
                        lic.setAmount(fee);
                        lic.setExpirationDate(exp);
                        licenseDAO.update(lic);
                    }
                    reload();
                } catch (SQLException ex) {
                    showError(ex);
                }
            }

            /**
             * Marque la licence du joueur comme payée.
             */
            private void handleMarkPaid(ActionEvent e) {
                if (!isAdmin) return;
                Player p = getTableView().getItems().get(getIndex());
                try {
                    License lic = licenseDAO.findByPlayerId(p.getId());
                    if (lic != null) {
                        licenseDAO.setPaid(lic.getId(), true);
                        reload();
                    }
                } catch (SQLException ex) {
                    showError(ex);
                }
            }

            /**
             * Marque la licence du joueur comme non payée.
             */
            private void handleMarkUnpaid(ActionEvent e) {
                if (!isAdmin) return;
                Player p = getTableView().getItems().get(getIndex());
                try {
                    License lic = licenseDAO.findByPlayerId(p.getId());
                    if (lic != null) {
                        licenseDAO.setPaid(lic.getId(), false);
                        reload();
                    }
                } catch (SQLException ex) {
                    showError(ex);
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    createBtn.setDisable(!isAdmin);
                    payBtn.setDisable(!isAdmin);
                    unpayBtn.setDisable(!isAdmin);
                    setGraphic(box);
                }
            }
        });

        reload();
    }

    /**
     * Recharge la liste des joueurs depuis la base,
     * ainsi que leurs licences associées.
     */
    private void reload() {
        try {
            players.setAll(playerDAO.findAll());
            playersTable.setItems(players);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /**
     * Affiche une fenêtre d’erreur générique avec le message de l’exception.
     *
     * @param ex exception SQL ou autre problème
     */
    private void showError(Exception ex) {
        Alert a = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
        a.setHeaderText("Erreur");
        a.showAndWait();
    }
}
