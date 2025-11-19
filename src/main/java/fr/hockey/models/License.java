package fr.hockey.models;

import java.time.LocalDate;

/**
 * Représente une licence d'un joueur du club.
 *
 * <p>Une licence contient :
 * <ul>
 *     <li>un identifiant unique ;</li>
 *     <li>l'identifiant du joueur associé ;</li>
 *     <li>un statut payé / non payé ;</li>
 *     <li>une date d'expiration ;</li>
 *     <li>le montant correspondant à la catégorie du joueur.</li>
 * </ul>
 *
 * <p>La gestion des licences est assurée via {@code LicenseDAO}.</p>
 */
public class License {

    /** Identifiant unique de la licence. */
    private int id;

    /** Identifiant du joueur à qui la licence est associée. */
    private int playerId;

    /** Indique si la licence a été payée. */
    private boolean paid;

    /** Date d'expiration de la licence. */
    private LocalDate expirationDate;

    /** Montant de la licence. */
    private double amount;

    /**
     * Constructeur par défaut.
     */
    public License() {
    }

    /**
     * Constructeur complet.
     *
     * @param id identifiant unique
     * @param playerId identifiant du joueur associé
     * @param paid statut de paiement
     * @param expirationDate date d'expiration
     * @param amount montant de la licence
     */
    public License(int id, int playerId, boolean paid, LocalDate expirationDate, double amount) {
        this.id = id;
        this.playerId = playerId;
        this.paid = paid;
        this.expirationDate = expirationDate;
        this.amount = amount;
    }

    /** @return identifiant unique */
    public int getId() {
        return id;
    }

    /** @param id identifiant unique */
    public void setId(int id) {
        this.id = id;
    }

    /** @return identifiant du joueur associé */
    public int getPlayerId() {
        return playerId;
    }

    /** @param playerId identifiant du joueur associé */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /** @return {@code true} si la licence est payée, sinon {@code false} */
    public boolean isPaid() {
        return paid;
    }

    /** @param paid statut payé/non payé */
    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    /** @return date d'expiration de la licence */
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    /** @param expirationDate date d'expiration */
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /** @return montant de la licence */
    public double getAmount() {
        return amount;
    }

    /** @param amount montant de la licence */
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
