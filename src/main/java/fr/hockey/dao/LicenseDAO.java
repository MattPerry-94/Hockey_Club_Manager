package fr.hockey.dao;

import fr.hockey.models.License;
import fr.hockey.utils.AuditLogger;

import java.sql.*;
import java.time.LocalDate;

/**
 * DAO responsable de la gestion des licences des joueurs.
 * Permet de :
 * <ul>
 *     <li>Récupérer la licence d’un joueur</li>
 *     <li>Créer une licence</li>
 *     <li>Mettre à jour une licence</li>
 *     <li>Changer l'état payé / non payé</li>
 *     <li>Supprimer la licence d’un joueur</li>
 *     <li>Obtenir le tarif d’une catégorie</li>
 * </ul>
 */
public class LicenseDAO {

    /**
     * Récupère la licence associée à un joueur donné.
     *
     * @param playerId identifiant du joueur
     * @return License ou null si aucune licence trouvée
     * @throws SQLException en cas d’erreur SQL
     */
    public License findByPlayerId(int playerId) throws SQLException {
        String sql = "SELECT id, player_id, paid, expiration_date, amount FROM licenses WHERE player_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    License lic = new License();
                    lic.setId(rs.getInt("id"));
                    lic.setPlayerId(rs.getInt("player_id"));
                    lic.setPaid(rs.getBoolean("paid"));
                    Date exp = rs.getDate("expiration_date");
                    if (exp != null) lic.setExpirationDate(exp.toLocalDate());
                    lic.setAmount(rs.getDouble("amount"));
                    return lic;
                }
            }
        }
        return null;
    }

    /**
     * Crée une licence pour un joueur.
     *
     * @param playerId identifiant du joueur
     * @param amount montant de la licence
     * @param expirationDate date d’expiration
     * @param paid état payé ou non payé
     * @return true si l’insertion a réussi
     * @throws SQLException en cas d’erreur SQL
     */
    public boolean createForPlayer(int playerId, double amount, LocalDate expirationDate, boolean paid) throws SQLException {
        String sql = "INSERT INTO licenses (player_id, paid, expiration_date, amount) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setBoolean(2, paid);
            ps.setDate(3, Date.valueOf(expirationDate));
            ps.setDouble(4, amount);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                AuditLogger.logChange("licenses", "INSERT", "player_id=" + playerId,
                        String.format("paid=%s,expiration_date=%s,amount=%.2f", paid, expirationDate, amount));
            }
            return ok;
        }
    }

    /**
     * Met à jour les informations d’une licence existante.
     *
     * @param license licence contenant les nouvelles données
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d’erreur SQL
     */
    public boolean update(License license) throws SQLException {
        String sql = "UPDATE licenses SET paid = ?, expiration_date = ?, amount = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, license.isPaid());
            ps.setDate(2, Date.valueOf(license.getExpirationDate()));
            ps.setDouble(3, license.getAmount());
            ps.setInt(4, license.getId());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                AuditLogger.logChange("licenses", "UPDATE", String.valueOf(license.getId()),
                        String.format("paid=%s,expiration_date=%s,amount=%.2f", license.isPaid(), license.getExpirationDate(), license.getAmount()));
            }
            return ok;
        }
    }

    /**
     * Change uniquement l'état payé / non payé d’une licence.
     *
     * @param licenseId identifiant de la licence
     * @param paid nouveau statut
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d’erreur SQL
     */
    public boolean setPaid(int licenseId, boolean paid) throws SQLException {
        String sql = "UPDATE licenses SET paid = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, paid);
            ps.setInt(2, licenseId);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                AuditLogger.logChange("licenses", "UPDATE_PAID", String.valueOf(licenseId), "paid=" + paid);
            }
            return ok;
        }
    }

    /**
     * Supprime la licence associée à un joueur.
     *
     * @param playerId identifiant du joueur
     * @return true si suppression réussie
     * @throws SQLException en cas d’erreur SQL
     */
    public boolean deleteByPlayer(int playerId) throws SQLException {
        String sql = "DELETE FROM licenses WHERE player_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                AuditLogger.logChange("licenses", "DELETE_BY_PLAYER", String.valueOf(playerId), "");
            }
            return ok;
        }
    }

    /**
     * Retourne le tarif correspondant à une catégorie de joueur.
     *
     * @param category catégorie (U9, U11, U13…)
     * @return montant de la licence ou 0 si aucune entrée correspondante
     * @throws SQLException en cas d’erreur SQL
     */
    public double getFeeForCategory(String category) throws SQLException {
        String sql = "SELECT fee FROM category_fees WHERE category = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("fee");
            }
        }
        return 0.0;
    }
}
