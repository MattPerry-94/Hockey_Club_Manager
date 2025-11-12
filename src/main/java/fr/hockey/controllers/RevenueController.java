package fr.hockey.controllers;

import fr.hockey.dao.RevenueDAO;
import fr.hockey.models.RevenueItem;
import fr.hockey.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class RevenueController {

    @FXML private TableView<RevenueItem> revenueTable;
    @FXML private TableColumn<RevenueItem, String> categoryColumn;
    @FXML private TableColumn<RevenueItem, Integer> paidCountColumn;
    @FXML private TableColumn<RevenueItem, Double> paidTotalColumn;
    @FXML private TableColumn<RevenueItem, Integer> unpaidCountColumn;
    @FXML private TableColumn<RevenueItem, Double> unpaidTotalColumn;
    @FXML private TableColumn<RevenueItem, Double> expectedTotalColumn;
    @FXML private Label totalsLabel;
    @FXML private Label roleInfoLabel;

    private final RevenueDAO revenueDAO = new RevenueDAO();
    private final ObservableList<RevenueItem> items = FXCollections.observableArrayList();

    private boolean isAdmin;

    @FXML
    public void initialize() {
        isAdmin = SessionManager.getInstance().isAdmin();
        roleInfoLabel.setText(isAdmin ? "Rôle: Administrateur" : "Rôle: Coach (lecture seule)");

        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        paidCountColumn.setCellValueFactory(new PropertyValueFactory<>("paidCount"));
        paidTotalColumn.setCellValueFactory(new PropertyValueFactory<>("paidTotal"));
        unpaidCountColumn.setCellValueFactory(new PropertyValueFactory<>("unpaidCount"));
        unpaidTotalColumn.setCellValueFactory(new PropertyValueFactory<>("unpaidTotal"));
        expectedTotalColumn.setCellValueFactory(new PropertyValueFactory<>("expectedTotal"));

        reload();
    }

    

    private void reload() {
        try {
            List<RevenueItem> list = revenueDAO.getRevenueByCategory();
            items.setAll(list);
            revenueTable.setItems(items);

            double totalPaid = revenueDAO.getTotalPaid();
            double totalExpected = revenueDAO.getTotalExpected();
            totalsLabel.setText(String.format("Total payé: %.2f € | Total attendu: %.2f €", totalPaid, totalExpected));
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