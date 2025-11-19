package fr.hockey.models;

import java.time.LocalDateTime;

/**
 * Représente les informations légales (mentions légales) du club.
 *
 * <p>Ce modèle sert à stocker dans la base de données les éléments nécessaires
 * pour la page des mentions légales : nom de la structure, adresse, numéro
 * d'enregistrement, hébergeur, contact, politique de confidentialité, etc.</p>
 *
 * <p>Les champs {@code createdAt} et {@code updatedAt} sont gérés par la base
 * de données via {@code NOW()} dans le DAO.</p>
 */
public class LegalInformation {

    /** Identifiant unique de l'entrée. */
    private int id;

    /** Nom légal de l'entité (association, club, entreprise…). */
    private String name;

    /** Adresse complète. */
    private String address;

    /** Numéro d'enregistrement (SIRET, RNA, etc.). */
    private String regNo;

    /** Nom de l'éditeur / responsable de la publication. */
    private String publisher;

    /** Hébergeur du site ou de l'application. */
    private String hosting;

    /** Email ou informations de contact. */
    private String contact;

    /** Politique de confidentialité / texte RGPD. */
    private String privacy;

    /** Date et heure de création du registre. */
    private LocalDateTime createdAt;

    /** Date et heure de dernière mise à jour. */
    private LocalDateTime updatedAt;

    /** @return identifiant unique */
    public int getId() { return id; }

    /** @param id identifiant unique */
    public void setId(int id) { this.id = id; }

    /** @return nom légal */
    public String getName() { return name; }

    /** @param name nom légal */
    public void setName(String name) { this.name = name; }

    /** @return adresse légale */
    public String getAddress() { return address; }

    /** @param address adresse légale */
    public void setAddress(String address) { this.address = address; }

    /** @return numéro d'enregistrement légal */
    public String getRegNo() { return regNo; }

    /** @param regNo numéro d'enregistrement légal */
    public void setRegNo(String regNo) { this.regNo = regNo; }

    /** @return responsable de la publication */
    public String getPublisher() { return publisher; }

    /** @param publisher responsable de la publication */
    public void setPublisher(String publisher) { this.publisher = publisher; }

    /** @return hébergeur */
    public String getHosting() { return hosting; }

    /** @param hosting hébergeur */
    public void setHosting(String hosting) { this.hosting = hosting; }

    /** @return contact (email ou autre) */
    public String getContact() { return contact; }

    /** @param contact contact */
    public void setContact(String contact) { this.contact = contact; }

    /** @return politique de confidentialité */
    public String getPrivacy() { return privacy; }

    /** @param privacy politique de confidentialité */
    public void setPrivacy(String privacy) { this.privacy = privacy; }

    /** @return date de création */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /** @param createdAt date de création */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /** @return date de dernière mise à jour */
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    /** @param updatedAt date de dernière mise à jour */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
