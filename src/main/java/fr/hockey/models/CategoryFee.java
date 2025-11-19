package fr.hockey.models;

/**
 * Représente un tarif de licence associé à une catégorie (ex : U9, U11, etc.).
 *
 * <p>Chaque catégorie de joueurs possède un montant de licence distinct.
 * Cette classe est utilisée par {@code LicenseDAO} et {@code RevenueDAO}
 * pour calculer les montants payés, impayés et attendus.</p>
 */
public class CategoryFee {

    /** Identifiant unique du tarif. */
    private int id;

    /** Nom de la catégorie (ex: "U13"). */
    private String category;

    /** Montant de la licence associé à cette catégorie. */
    private double fee;

    /**
     * Constructeur par défaut.
     */
    public CategoryFee() {
    }

    /**
     * Constructeur complet.
     *
     * @param id identifiant unique
     * @param category nom de la catégorie
     * @param fee montant du tarif pour la catégorie
     */
    public CategoryFee(int id, String category, double fee) {
        this.id = id;
        this.category = category;
        this.fee = fee;
    }

    /**
     * Retourne l'identifiant du tarif.
     *
     * @return id du tarif
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'identifiant du tarif.
     *
     * @param id identifiant unique
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retourne la catégorie associée.
     *
     * @return nom de la catégorie
     */
    public String getCategory() {
        return category;
    }

    /**
     * Définit la catégorie associée.
     *
     * @param category nom de la catégorie
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Retourne le montant de la licence.
     *
     * @return montant de la licence
     */
    public double getFee() {
        return fee;
    }

    /**
     * Définit le montant de la licence.
     *
     * @param fee montant de la licence
     */
    public void setFee(double fee) {
        this.fee = fee;
    }
}
