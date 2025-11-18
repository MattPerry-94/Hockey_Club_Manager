package fr.hockey.dao;

import fr.hockey.models.LegalInformation;

import java.sql.*;

public class LegalInformationDAO {

    // Cache du nom de colonne pour le n° d'enregistrement
    private String regNoColumn;

    private String resolveRegNoColumn(Connection conn) throws SQLException {
        if (regNoColumn != null) return regNoColumn;
        String[] candidates = {"reg_no", "registration_no", "registration_number", "registration", "regno"};
        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'legal_informations'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            java.util.HashSet<String> set = new java.util.HashSet<>();
            while (rs.next()) set.add(rs.getString(1));
            for (String c : candidates) {
                if (set.contains(c)) {
                    regNoColumn = c;
                    return regNoColumn;
                }
            }
        }
        // Par défaut, tenter reg_no
        regNoColumn = "reg_no";
        return regNoColumn;
    }

    public LegalInformation findCurrent() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String regCol = resolveRegNoColumn(conn);
            String sql = "SELECT id, name, address, " + regCol + " AS reg_no, publisher, hosting, contact, privacy, created_at, updated_at " +
                    "FROM legal_informations ORDER BY id LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public int insert(LegalInformation info) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String regCol = resolveRegNoColumn(conn);
            String sql = "INSERT INTO legal_informations (name, address, " + regCol + ", publisher, hosting, contact, privacy, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, info.getName());
                ps.setString(2, info.getAddress());
                ps.setString(3, info.getRegNo());
                ps.setString(4, info.getPublisher());
                ps.setString(5, info.getHosting());
                ps.setString(6, info.getContact());
                ps.setString(7, info.getPrivacy());
                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            return keys.getInt(1);
                        }
                    }
                }
            }
        }
        return 0;
    }

    public boolean update(int id, LegalInformation info) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String regCol = resolveRegNoColumn(conn);
            String sql = "UPDATE legal_informations SET name = ?, address = ?, " + regCol + " = ?, publisher = ?, hosting = ?, contact = ?, privacy = ?, updated_at = NOW() " +
                    "WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, info.getName());
                ps.setString(2, info.getAddress());
                ps.setString(3, info.getRegNo());
                ps.setString(4, info.getPublisher());
                ps.setString(5, info.getHosting());
                ps.setString(6, info.getContact());
                ps.setString(7, info.getPrivacy());
                ps.setInt(8, id);
                return ps.executeUpdate() > 0;
            }
        }
        
        }

    public boolean upsert(LegalInformation info) throws SQLException {
        LegalInformation current = findCurrent();
        if (current == null) {
            int newId = insert(info);
            return newId > 0;
        } else {
            return update(current.getId(), info);
        }
    }

    private LegalInformation map(ResultSet rs) throws SQLException {
        LegalInformation li = new LegalInformation();
        li.setId(rs.getInt("id"));
        li.setName(rs.getString("name"));
        li.setAddress(rs.getString("address"));
        li.setRegNo(rs.getString("reg_no")); // aliasé dans la requête
        li.setPublisher(rs.getString("publisher"));
        li.setHosting(rs.getString("hosting"));
        li.setContact(rs.getString("contact"));
        li.setPrivacy(rs.getString("privacy"));
        Timestamp cAt = rs.getTimestamp("created_at");
        Timestamp uAt = rs.getTimestamp("updated_at");
        if (cAt != null) li.setCreatedAt(cAt.toLocalDateTime());
        if (uAt != null) li.setUpdatedAt(uAt.toLocalDateTime());
        return li;
    }
}