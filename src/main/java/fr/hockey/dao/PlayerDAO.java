package fr.hockey.dao;

import fr.hockey.models.License;
import fr.hockey.models.Player;
import fr.hockey.utils.AuditLogger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsable de la gestion des joueurs.
 *
 * <p>Fonctionnalités prises en charge :</p>
 * <ul>
 *     <li>Récupération de tous les joueurs (avec leur licence)</li>
 *     <li>Recherche par catégorie</li>
 *     <li>Recherche par poste</li>
 *     <li>Recherche combinée catégorie + poste</li>
 *     <li>Insertion, modification et suppression d'un joueur</li>
 *     <li>Mappage complet ResultSet → Player + License</li>
 * </ul>
 */
public class PlayerDAO {

    /**
     * Retourne la liste complète des joueurs, triée par nom et prénom.
     * Les licences sont chargées via une jointure LEFT JOIN.
     *
     * @return liste des joueurs (avec licence éventuelle)
     * @throws SQLException en cas d’erreur SQL
     */
    public List<Player> findAll() throws SQLException {
        String sql = "SELECT p.id, p.first_name, p.last_name, p.category, p.role, p.position, p.number, " +
                "l.id AS license_id, l.paid AS license_paid, l.expiration_date AS license_expiration_date, l.amount AS license_amount " +
                "FROM players p LEFT JOIN licenses l ON l.player_id = p.id " +
                "ORDER BY p.last_name, p.first_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapPlayers(rs);
        }
    }

    /**
     * Recherche tous les joueurs d’une catégorie donnée.
     *
     * @param category catégorie recherchée (U9, U11…)
     * @return liste des joueurs
     * @throws SQLException en cas d’erreur SQL
     */
    public List<Player> findByCategory(String category) throws SQLException {
        String sql = "SELECT p.id, p.first_name, p.last_name, p.category, p.role, p.position, p.number, " +
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

    /**
     * Recherche les joueurs à partir d’un poste donné.
     *
     * @param position poste (GARDIEN, DÉFENSEUR, ATTAQUANT)
     * @return liste des joueurs
     * @throws SQLException en cas d’erreur SQL
     */
    public List<Player> findByPosition(String position) throws SQLException {
        String sql = "SELECT p.id, p.first_name, p.last_name, p.category, p.role, p.position, p.number, " +
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

    /**
     * Recherche les joueurs selon catégorie + poste.
     *
     * @param category catégorie U9…U20
     * @param position poste (GARDIEN/DÉFENSEUR/ATTAQUANT)
     * @return liste filtrée
     * @throws SQLException en cas d’erreur SQL
     */
    public List<Player> findByCategoryAndPosition(String category, String position) throws SQLException {
        String sql = "SELECT p.id, p.first_name, p.last_name, p.category, p.role, p.position, p.number, " +
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

    public Player findById(int id) throws SQLException {
        String sql = "SELECT p.id, p.first_name, p.last_name, p.category, p.role, p.position, p.number, " +
                "l.id AS license_id, l.paid AS license_paid, l.expiration_date AS license_expiration_date, l.amount AS license_amount " +
                "FROM players p LEFT JOIN licenses l ON l.player_id = p.id WHERE p.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                List<Player> list = mapPlayers(rs);
                return list.isEmpty() ? null : list.get(0);
            }
        }
    }

    /**
     * Enregistre un joueur.
     * <p>
     * Si le joueur a un ID > 0, une mise à jour est effectuée.
     * Sinon, un nouvel enregistrement est créé.
     * </p>
     *
     * @param player joueur à sauvegarder
     * @return true si succès
     * @throws SQLException en cas d’erreur SQL
     */
    public boolean save(Player player) throws SQLException {
        if (player.getId() > 0) {
            return update(player);
        } else {
            return insert(player);
        }
    }

    /**
     * Crée un nouveau joueur dans la base.
     *
     * @param player instance Player à insérer
     * @return true si insertion réussie
     * @throws SQLException en cas d’erreur SQL
     */
    private boolean insert(Player player) throws SQLException {
        String sql = "INSERT INTO players (first_name, last_name, category, role, position, number) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, player.getFirstName());
            ps.setString(2, player.getLastName());
            ps.setString(3, player.getCategory());
            ps.setString(4, player.getRole());
            ps.setString(5, player.getPosition());
            if (player.getNumber() > 0) {
                ps.setInt(6, player.getNumber());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            int affected = ps.executeUpdate();
            if (affected == 0) return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    player.setId(keys.getInt(1));
                }
            }
            AuditLogger.logChange(
                    "players",
                    "INSERT",
                    String.valueOf(player.getId()),
                    String.format("first_name=%s,last_name=%s,category=%s,role=%s,position=%s,number=%s",
                            player.getFirstName(), player.getLastName(), player.getCategory(), player.getRole(), player.getPosition(),
                            player.getNumber() > 0 ? player.getNumber() : "NULL")
            );
            return true;
        }
    }

    /**
     * Met à jour un joueur existant.
     *
     * @param player joueur contenant les valeurs mises à jour
     * @return true si modification réussie
     * @throws SQLException en cas d’erreur SQL
     */
    private boolean update(Player player) throws SQLException {
        String sql = "UPDATE players SET first_name = ?, last_name = ?, category = ?, role = ?, position = ?, number = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getFirstName());
            ps.setString(2, player.getLastName());
            ps.setString(3, player.getCategory());
            ps.setString(4, player.getRole());
            ps.setString(5, player.getPosition());
            if (player.getNumber() > 0) {
                ps.setInt(6, player.getNumber());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, player.getId());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                AuditLogger.logChange(
                        "players",
                        "UPDATE",
                        String.valueOf(player.getId()),
                        String.format("first_name=%s,last_name=%s,category=%s,role=%s,position=%s,number=%s",
                                player.getFirstName(), player.getLastName(), player.getCategory(), player.getRole(), player.getPosition(),
                                player.getNumber() > 0 ? player.getNumber() : "NULL")
                );
                return true;
            }
            return false;
        }
    }

    /**
     * Supprime un joueur de la base.
     *
     * @param playerId ID du joueur
     * @return true si suppression réussie
     * @throws SQLException en cas d’erreur SQL
     */
    public boolean delete(int playerId) throws SQLException {
        String sql = "DELETE FROM players WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                AuditLogger.logChange("players", "DELETE", String.valueOf(playerId), "");
                return true;
            }
            return false;
        }
    }

    /**
     * Convertit un ResultSet en liste de joueurs avec leur licence éventuelle.
     *
     * @param rs ResultSet contenant les colonnes de player + license
     * @return liste des joueurs
     * @throws SQLException en cas d’erreur de lecture
     */
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

            int number = rs.getInt("number");
            p.setNumber(!rs.wasNull() ? number : 0);

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
