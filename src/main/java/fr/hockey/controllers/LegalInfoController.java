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

/**
 * Contrôleur responsable de la gestion des informations légales :
 * affichage en mode lecture seule, bascule en mode édition,
 * sauvegarde en base de données et synchronisation avec AppSettings.
 */
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

    // Panneaux pour bascule (lecture ↔ édition)
    @FXML private VBox viewPane;
    @FXML private VBox editPane;

    // Label de statut
    @FXML private Label statusLabel;

    private final LegalInformationDAO dao = new LegalInformationDAO();
    private LegalInformation currentInfo;

    /**
     * Initialise la vue en chargeant en priorité les informations légales depuis la base.
     * Si aucune donnée n'existe, charge les valeurs locales depuis AppSettings.
     * Configure ensuite l'interface en mode lecture seule.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            LegalInformation current = dao.findCurrent();

            if (current != null) {
                currentInfo = current;
                populateEditFields(currentInfo);
                populateView(currentInfo);
                statusLabel.setText("Informations chargées depuis la base de données.");
            } else {
                // Aucun enregistrement → charger valeurs locales
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
            setViewMode(true);
        }
    }

    /**
     * Enregistre les informations légales dans la base (INSERT ou UPDATE via upsert).
     * Met également à jour AppSettings pour que les valeurs locales restent synchronisées.
     * Repasse en mode lecture seule après sauvegarde.
     */
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
                // Synchroniser AppSettings
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

                Alert okAlert = new Alert(Alert.AlertType.INFORMATION,
                        "Les informations légales ont été enregistrées en base de données.");
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

    /**
     * Passe en mode édition et recharge les champs du formulaire avec les valeurs actuelles.
     */
    @FXML
    private void handleEdit() {
        populateEditFields(currentInfo != null ? currentInfo : new LegalInformation());
        setViewMode(false);
    }

    /**
     * Annule l'édition en cours et repasse en mode lecture seule
     * sans sauvegarder les modifications.
     */
    @FXML
    private void handleCancel() {
        populateView(currentInfo != null ? currentInfo : new LegalInformation());
        setViewMode(true);
    }

    /**
     * Bascule entre le mode lecture seule (viewPane) et le mode édition (editPane).
     *
     * @param viewMode true pour la vue lecture seule, false pour la vue édition
     */
    private void setViewMode(boolean viewMode) {
        if (viewPane != null && editPane != null) {
            viewPane.setVisible(viewMode);
            viewPane.setManaged(viewMode);
            editPane.setVisible(!viewMode);
            editPane.setManaged(!viewMode);
        }
    }

    /**
     * Remplit les champs du formulaire d'édition avec les valeurs de l'objet fourni.
     *
     * @param info instance LegalInformation dont les valeurs doivent être affichées
     */
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

    /**
     * Remplit la vue lecture seule avec les informations légales actuelles.
     *
     * @param info instance LegalInformation dont les données doivent être affichées
     */
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

    /**
     * Nettoie une chaîne : jamais null, toujours trimée.
     */
    private String safe(String s) { return s == null ? "" : s.trim(); }

    /**
     * Retourne une chaîne non nulle :
     * - si s est null → ""
     * - sinon → s
     */
    private String nvl(String s) { return s == null ? "" : s; }
}
