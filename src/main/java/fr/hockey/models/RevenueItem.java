package fr.hockey.models;

/**
 * Représente les données de revenus agrégées pour une catégorie donnée.
 *
 * <p>Un objet {@code RevenueItem} regroupe les valeurs calculées par
 * {@link fr.hockey.dao.RevenueDAO} concernant :
 * <ul>
 *     <li>le montant total payé pour une catégorie ;</li>
 *     <li>le montant total non payé ;</li>
 *     <li>le nombre de licences payées ;</li>
 *     <li>le nombre de licences non payées ;</li>
 *     <li>le total attendu (payé + non payé).</li>
 * </ul>
 *
 * <p>Il est utilisé dans l’interface de gestion des revenus du club pour afficher un
 * tableau synthétique par catégorie.</p>
 */
public class RevenueItem {

    /** Nom de la catégorie (U9, U11, U13, etc.). */
    private String category;

    /** Nombre de licences payées. */
    private int paidCount;

    /** Montant total payé pour la catégorie. */
    private double paidTotal;

    /** Nombre de licences non payées. */
    private int unpaidCount;

    /** Montant total non payé pour la catégorie. */
    private double unpaidTotal;

    /**
     * Construit un objet représentant les revenus agrégés pour une catégorie.
     *
     * @param category nom de la catégorie
     * @param paidCount nombre de licences payées
     * @param paidTotal total payé
     * @param unpaidCount nombre de licences non payées
     * @param unpaidTotal total non payé
     */
    public RevenueItem(String category, int paidCount, double paidTotal, int unpaidCount, double unpaidTotal) {
        this.category = category;
        this.paidCount = paidCount;
        this.paidTotal = paidTotal;
        this.unpaidCount = unpaidCount;
        this.unpaidTotal = unpaidTotal;
    }

    /** @return la catégorie sportive */
    public String getCategory() { return category; }

    /** @return le nombre de licences payées */
    public int getPaidCount() { return paidCount; }

    /** @return le total payé en euros */
    public double getPaidTotal() { return paidTotal; }

    /** @return le nombre de licences non payées */
    public int getUnpaidCount() { return unpaidCount; }

    /** @return le total non payé en euros */
    public double getUnpaidTotal() { return unpaidTotal; }

    /**
     * Calcule le total attendu pour la catégorie :
     * <p><b>payé + non payé</b></p>
     *
     * @return montant attendu total
     */
    public double getExpectedTotal() { return paidTotal + unpaidTotal; }
}
