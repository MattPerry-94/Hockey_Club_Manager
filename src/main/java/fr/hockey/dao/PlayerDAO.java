package fr.hockey.dao;

import fr.hockey.models.License;
import fr.hockey.models.Player;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {

    public List<Player> findAll() throws SQLException {
        String sql = "SELECT p.id, p.first_name, p.last_name, p.category, p.role, p.position, " +
                "l.id AS license_id, l.paid AS license_paid, l.expiration_date AS license_expiration_date, l.amount AS license_amount " +
                "FROM players p LEFT JOIN licenses l ON l.player_id = p.id " +
                "ORDER BY p.last_name, p.first_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapPlayers(rs);
        }
    }

    public List<Player> findByCategory(String category) throws SQLException {
        String sql = "SELECT p.id, p.first_name, p.last_name, p.category, p.role, p.position, " +
                "l.id AS license_id, l.paid AS license_paid, l.expiration_date AS license_expiration_date, l.amount AS license_amount " +
                "FROM players p LEFT JOIN licenses l ON l.player_id = p.id " +
                "WHERE p.category = ? ORDER BY p.last_name, p.first_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                return mapPlayers(rs);
            }
        }
    }

    public List<Player> findByPosition(String position) throws SQLException {
        String sql = "SELECT p.id, p.first_name, p.last_name, p.category, p.role, p.position, " +
                "l.id AS license_id, l.paid AS license_paid, l.expiration_date AS license_expiration_date, l.amount AS license_amount " +
                "FROM players p LEFT JOIN licenses l ON l.player_id = p.id " +
                "WHERE p.position = ? ORDER BY p.last_name, p.first_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, position);
            try (ResultSet rs = ps.executeQuery()) {
                return mapPlayers(rs);
            }
        }
    }

    public List<Player> findByCategoryAndPosition(String category, String position) throws SQLException {
        String sql = "SELECT p.id, p.first_name, p.last_name, p.category, p.role, p.position, " +
                "l.id AS license_id, l.paid AS license_paid, l.expiration_date AS license_expiration_date, l.amount AS license_amount " +
                "FROM players p LEFT JOIN licenses l ON l.player_id = p.id " +
                "WHERE p.category = ? AND p.position = ? ORDER BY p.last_name, p.first_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            ps.setString(2, position);
            try (ResultSet rs = ps.executeQuery()) {
                return mapPlayers(rs);
            }
        }
    }

    public boolean save(Player player) throws SQLException {
        if (player.getId() > 0) {
            return update(player);
        } else {
            return insert(player);
        }
    }

    private boolean insert(Player player) throws SQLException {
        String sql = "INSERT INTO players (first_name, last_name, category, role, position) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, player.getFirstName());
            ps.setString(2, player.getLastName());
            ps.setString(3, player.getCategory());
            ps.setString(4, player.getRole());
            ps.setString(5, player.getPosition());
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    player.setId(keys.getInt(1));
                }
            }
            return true;
        }
    }

    private boolean update(Player player) throws SQLException {
        String sql = "UPDATE players SET first_name = ?, last_name = ?, category = ?, role = ?, position = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getFirstName());
            ps.setString(2, player.getLastName());
            ps.setString(3, player.getCategory());
            ps.setString(4, player.getRole());
            ps.setString(5, player.getPosition());
            ps.setInt(6, player.getId());
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    public boolean delete(int playerId) throws SQLException {
        String sql = "DELETE FROM players WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    private List<Player> mapPlayers(ResultSet rs) throws SQLException {
        List<Player> list = new ArrayList<>();
        while (rs.next()) {
            Player p = new Player();
            p.setId(rs.getInt("id"));
            p.setFirstName(rs.getString("first_name"));
            p.setLastName(rs.getString("last_name"));
            p.setCategory(rs.getString("category"));
            p.setRole(rs.getString("role"));
            p.setPosition(rs.getString("position"));

            int licenseId = rs.getInt("license_id");
            if (!rs.wasNull()) {
                License lic = new License();
                lic.setId(licenseId);
                lic.setPlayerId(p.getId());
                lic.setPaid(rs.getBoolean("license_paid"));
                Date expDate = rs.getDate("license_expiration_date");
                if (expDate != null) {
                    lic.setExpirationDate(expDate.toLocalDate());
                } else {
                    lic.setExpirationDate(LocalDate.now());
                }
                lic.setAmount(rs.getDouble("license_amount"));
                p.setLicense(lic);
            }

            list.add(p);
        }
        return list;
    }
}