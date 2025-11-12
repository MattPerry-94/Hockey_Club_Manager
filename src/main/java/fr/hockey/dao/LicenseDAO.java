package fr.hockey.dao;

import fr.hockey.models.License;

import java.sql.*;
import java.time.LocalDate;

public class LicenseDAO {

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

    public boolean createForPlayer(int playerId, double amount, LocalDate expirationDate, boolean paid) throws SQLException {
        String sql = "INSERT INTO licenses (player_id, paid, expiration_date, amount) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setBoolean(2, paid);
            ps.setDate(3, Date.valueOf(expirationDate));
            ps.setDouble(4, amount);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(License license) throws SQLException {
        String sql = "UPDATE licenses SET paid = ?, expiration_date = ?, amount = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, license.isPaid());
            ps.setDate(2, Date.valueOf(license.getExpirationDate()));
            ps.setDouble(3, license.getAmount());
            ps.setInt(4, license.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean setPaid(int licenseId, boolean paid) throws SQLException {
        String sql = "UPDATE licenses SET paid = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, paid);
            ps.setInt(2, licenseId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteByPlayer(int playerId) throws SQLException {
        String sql = "DELETE FROM licenses WHERE player_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            return ps.executeUpdate() > 0;
        }
    }

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