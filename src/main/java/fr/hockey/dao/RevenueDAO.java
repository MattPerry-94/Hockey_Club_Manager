package fr.hockey.dao;

import fr.hockey.models.RevenueItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RevenueDAO {
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

    public double getTotalPaid() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) AS total FROM licenses WHERE paid = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("total");
        }
        return 0.0;
    }

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