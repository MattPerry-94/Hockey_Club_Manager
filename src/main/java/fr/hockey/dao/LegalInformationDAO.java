package fr.hockey.dao;

import fr.hockey.models.LegalInformation;

import java.sql.*;

/**
 * DAO responsable de la gestion des informations légales du club.
 * Cette classe prend en charge :
 * <ul>
 *   <li>La détection automatique du nom réel de la colonne "reg_no" dans la base</li>
 *   <li>La récupération de l’enregistrement légal courant</li>
 *   <li>L’insertion et la mise à jour (update)</li>
 *   <li>La logique d’upsert (insert si absent, update sinon)</li>
 *   <li>Le mapping ResultSet → LegalInformation</li>
 * </ul>
 */
public class LegalInformationDAO {

    /** Nom détecté dynamiquement de la colonne “numéro d’enregistrement” (reg_no). */
    private String regNoColumn;

    /**
     * Détecte automatiquement dans la base le nom réel de la colonne représentant
     * le numéro d’enregistrement (reg_no, registration_no, etc.).
     *
     * @param conn connexion SQL active
     * @return nom de la colonne valide
     * @throws SQLException en cas d’erreur SQL
     */
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
        regNoColumn = "reg_no";
        return regNoColumn;
    }

    /**
     * Récupère l’enregistrement d’informations légales actuellement stocké.
     *
     * @return la LegalInformation courante ou null si aucune
     * @throws SQLException en cas d’erreur SQL
     */
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

    /**
     * Insère un nouvel enregistrement d’informations légales.
     *
     * @param info objet LegalInformation à insérer
     * @return ID généré, ou 0 en cas d’échec
     * @throws SQLException en cas d’erreur SQL
     */
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

    /**
     * Met à jour l’enregistrement légal existant correspondant à un ID donné.
     *
     * @param id identifiant dans la table
     * @param info données à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d’erreur SQL
     */
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

    /**
     * Insère ou met à jour selon qu’une donnée existe déjà.
     *
     * @param info infos légales à sauvegarder
     * @return true si opération réussie
     * @throws SQLException en cas d’erreur SQL
     */
    public boolean upsert(LegalInformation info) throws SQLException {
        LegalInformation current = findCurrent();
        if (current == null) {
            int newId = insert(info);
            return newId > 0;
        } else {
            return update(current.getId(), info);
        }
    }

    /**
     * Convertit un ResultSet JDBC en objet LegalInformation.
     *
     * @param rs ligne courante du ResultSet
     * @return instance LegalInformation
     * @throws SQLException en cas d’erreur d’extraction
     */
    private LegalInformation map(ResultSet rs) throws SQLException {
        LegalInformation li = new LegalInformation();
        li.setId(rs.getInt("id"));
        li.setName(rs.getString("name"));
        li.setAddress(rs.getString("address"));
        li.setRegNo(rs.getString("reg_no"));
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
