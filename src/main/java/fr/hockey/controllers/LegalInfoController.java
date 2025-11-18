package fr.hockey.controllers;

import fr.hockey.dao.LegalInformationDAO;
import fr.hockey.models.LegalInformation;
import fr.hockey.utils.AppSettings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LegalInfoController implements Initializable {

    // Champs édition (formulaire)
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField regNoField;
    @FXML private TextField publisherField;
    @FXML private TextField hostingField;
    @FXML private TextField contactField;
    @FXML private TextArea privacyArea;
    // Vue lecture seule
    @FXML private Label nameLabel;
    @FXML private Label addressLabel;
    @FXML private Label regNoLabel;
    @FXML private Label publisherLabel;
    @FXML private Label hostingLabel;
    @FXML private Label contactLabel;
    @FXML private Label privacyLabel;
    // Panneaux pour bascule
    @FXML private VBox viewPane;
    @FXML private VBox editPane;
    // Statut et actions
    @FXML private Label statusLabel;

    private final LegalInformationDAO dao = new LegalInformationDAO();
    private LegalInformation currentInfo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Essayer de charger depuis la base de données, sinon fallback AppSettings
        try {
            LegalInformation current = dao.findCurrent();
            if (current != null) {
                currentInfo = current;
                populateEditFields(currentInfo);
                populateView(currentInfo);
                statusLabel.setText("Informations chargées depuis la base de données.");
            } else {
                // Pré-remplir avec AppSettings pour aider la première insertion
                currentInfo = new LegalInformation();
                currentInfo.setName(AppSettings.getLegalName());
                currentInfo.setAddress(AppSettings.getLegalAddress());
                currentInfo.setRegNo(AppSettings.getLegalRegNo());
                currentInfo.setPublisher(AppSettings.getLegalPublisher());
                currentInfo.setHosting(AppSettings.getLegalHosting());
                currentInfo.setContact(AppSettings.getLegalContact());
                currentInfo.setPrivacy(AppSettings.getLegalPrivacy());
                populateEditFields(currentInfo);
                populateView(currentInfo);
                statusLabel.setText("Aucune donnée en base, valeurs locales chargées.");
            }
            setViewMode(true);
        } catch (SQLException ex) {
            statusLabel.setText("Erreur de chargement: " + ex.getMessage());
            // Assurer un état UI cohérent
            setViewMode(true);
        }
    }

    @FXML
    private void handleSave() {
        LegalInformation info = new LegalInformation();
        info.setName(safe(nameField.getText()));
        info.setAddress(safe(addressField.getText()));
        info.setRegNo(safe(regNoField.getText()));
        info.setPublisher(safe(publisherField.getText()));
        info.setHosting(safe(hostingField.getText()));
        info.setContact(safe(contactField.getText()));
        info.setPrivacy(safe(privacyArea.getText()));

        try {
            boolean ok = dao.upsert(info);
            if (ok) {
                // Synchroniser aussi avec AppSettings pour cohérence locale
                AppSettings.setLegalName(info.getName());
                AppSettings.setLegalAddress(info.getAddress());
                AppSettings.setLegalRegNo(info.getRegNo());
                AppSettings.setLegalPublisher(info.getPublisher());
                AppSettings.setLegalHosting(info.getHosting());
                AppSettings.setLegalContact(info.getContact());
                AppSettings.setLegalPrivacy(info.getPrivacy());

                currentInfo = info;
                populateView(currentInfo);
                setViewMode(true);
                statusLabel.setText("Informations enregistrées en base.");
                Alert okAlert = new Alert(Alert.AlertType.INFORMATION, "Les informations légales ont été enregistrées en base de données.");
                okAlert.setHeaderText(null);
                okAlert.showAndWait();
            } else {
                throw new SQLException("Échec de l'enregistrement.");
            }
        } catch (SQLException ex) {
            statusLabel.setText("Erreur d'enregistrement: " + ex.getMessage());
            Alert err = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            err.setHeaderText("Erreur lors de l'enregistrement");
            err.showAndWait();
        }
    }

    @FXML
    private void handleEdit() {
        // Passer en mode édition et pré-remplir les champs si nécessaire
        populateEditFields(currentInfo != null ? currentInfo : new LegalInformation());
        setViewMode(false);
    }

    @FXML
    private void handleCancel() {
        // Revenir à la vue lecture seule sans enregistrer
        populateView(currentInfo != null ? currentInfo : new LegalInformation());
        setViewMode(true);
    }

    private void setViewMode(boolean viewMode) {
        if (viewPane != null && editPane != null) {
            viewPane.setVisible(viewMode);
            viewPane.setManaged(viewMode);
            editPane.setVisible(!viewMode);
            editPane.setManaged(!viewMode);
        }
    }

    private void populateEditFields(LegalInformation info) {
        if (info == null) return;
        nameField.setText(nvl(info.getName()));
        addressField.setText(nvl(info.getAddress()));
        regNoField.setText(nvl(info.getRegNo()));
        publisherField.setText(nvl(info.getPublisher()));
        hostingField.setText(nvl(info.getHosting()));
        contactField.setText(nvl(info.getContact()));
        privacyArea.setText(nvl(info.getPrivacy()));
    }

    private void populateView(LegalInformation info) {
        if (info == null) return;
        if (nameLabel != null) nameLabel.setText(nvl(info.getName()));
        if (addressLabel != null) addressLabel.setText(nvl(info.getAddress()));
        if (regNoLabel != null) regNoLabel.setText(nvl(info.getRegNo()));
        if (publisherLabel != null) publisherLabel.setText(nvl(info.getPublisher()));
        if (hostingLabel != null) hostingLabel.setText(nvl(info.getHosting()));
        if (contactLabel != null) contactLabel.setText(nvl(info.getContact()));
        if (privacyLabel != null) privacyLabel.setText(nvl(info.getPrivacy()));
    }

    private String safe(String s) { return s == null ? "" : s.trim(); }
    private String nvl(String s) { return s == null ? "" : s; }
}