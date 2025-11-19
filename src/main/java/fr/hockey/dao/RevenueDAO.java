package fr.hockey.dao;

import fr.hockey.models.RevenueItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO dédié aux statistiques de revenus liées aux licences.
 *
 * <p>Fonctionnalités principales :</p>
 * <ul>
 *     <li>Calcul des revenus par catégorie (payé / non payé)</li>
 *     <li>Total global payé</li>
 *     <li>Total global attendu</li>
 * </ul>
 *
 * <p>Utilisé dans le tableau de bord des revenus.</p>
 */
public class RevenueDAO {

    /**
     * Récupère les revenus regroupés par catégorie de joueurs.
     *
     * <p>Pour chaque catégorie, cette méthode calcule :</p>
     * <ul>
     *     <li>Le nombre de licences payées</li>
     *     <li>Le montant total déjà payé</li>
     *     <li>Le nombre de licences non payées</li>
     *     <li>Le montant total dû mais non encore payé</li>
     * </ul>
     *
     * @return liste d'objets {@link RevenueItem} contenant les statistiques
     * @throws SQLException en cas d’erreur SQL
     */
    public List<RevenueItem> getRevenueByCategory() throws SQLException {
        String sql = """
                SELECT p.category,
                       SUM(CASE WHEN l.paid = TRUE THEN 1 ELSE 0 END) AS paid_count,
                       COALESCE(SUM(CASE WHEN l.paid = TRUE THEN l.amount END), 0) AS paid_total,
                       SUM(CASE WHEN l.paid = FALSE THEN 1 ELSE 0 END) AS unpaid_count,
                       COALESCE(SUM(CASE WHEN l.paid = FALSE THEN l.amount END), 0) AS unpaid_total
                FROM players p
                LEFT JOIN licenses l ON l.player_id = p.id
                GROUP BY p.category
                ORDER BY p.category
                """;

        List<RevenueItem> items = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(new RevenueItem(
                        rs.getString("category"),
                        rs.getInt("paid_count"),
                        rs.getDouble("paid_total"),
                        rs.getInt("unpaid_count"),
                        rs.getDouble("unpaid_total")
                ));
            }
        }
        return items;
    }

    /**
     * Retourne le montant total déjà payé par l’ensemble des licenciés.
     *
     * @return total des paiements perçus
     * @throws SQLException en cas d’erreur SQL
     */
    public double getTotalPaid() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) AS total FROM licenses WHERE paid = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble("total");
        }
        return 0.0;
    }

    /**
     * Retourne le montant total attendu, qu’il soit payé ou non.
     *
     * <p>Il s’agit du montant cumulé des licences enregistrées.</p>
     *
     * @return total attendu
     * @throws SQLException en cas d’erreur SQL
     */
    public double getTotalExpected() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) AS total FROM licenses";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble("total");
        }
        return 0.0;
    }
}
