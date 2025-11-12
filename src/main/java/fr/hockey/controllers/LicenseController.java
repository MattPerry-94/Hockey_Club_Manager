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

    @FXML
    public void initialize() {
        isAdmin = SessionManager.getInstance().isAdmin();
        roleInfoLabel.setText(isAdmin ? "Rôle: Administrateur" : "Rôle: Coach (lecture seule)");

        playerNameColumn.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getFirstName() + " " + cell.getValue().getLastName()
            )
        );
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        licensePaidColumn.setCellValueFactory(cell -> {
            License lic = cell.getValue().getLicense();
            String text = lic == null ? "Aucune" : (lic.isPaid() ? "Payée" : "Non payée");
            return new javafx.beans.property.SimpleStringProperty(text);
        });

        expirationColumn.setCellValueFactory(cell -> {
            License lic = cell.getValue().getLicense();
            String text = lic == null || lic.getExpirationDate() == null ? "-" : lic.getExpirationDate().toString();
            return new javafx.beans.property.SimpleStringProperty(text);
        });

        amountColumn.setCellValueFactory(cell -> {
            License lic = cell.getValue().getLicense();
            String text = lic == null ? "-" : String.format("%.2f", lic.getAmount());
            return new javafx.beans.property.SimpleStringProperty(text);
        });

        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button createBtn = new Button("Créer/MAJ");
            private final Button payBtn = new Button("Marquer Payée");
            private final Button unpayBtn = new Button("Marquer Non Payée");
            private final HBox box = new HBox(6, createBtn, payBtn, unpayBtn);

            {
                createBtn.setOnAction(this::handleCreateOrUpdate);
                payBtn.setOnAction(this::handleMarkPaid);
                unpayBtn.setOnAction(this::handleMarkUnpaid);
                if (!isAdmin) {
                    createBtn.setDisable(true);
                    payBtn.setDisable(true);
                    unpayBtn.setDisable(true);
                }
            }

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
                    createBtn.setDisable(!isAdmin); // création/MAJ par admin
                    payBtn.setDisable(!isAdmin);
                    unpayBtn.setDisable(!isAdmin);
                    setGraphic(box);
                }
            }
        });

        reload();
    }

    

    private void reload() {
        try {
            players.setAll(playerDAO.findAll());
            playersTable.setItems(players);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        Alert a = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
        a.setHeaderText("Erreur");
        a.showAndWait();
    }
}