package fr.hockey.dao;

import fr.hockey.models.Coach;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO dédié à la gestion des coachs.
 * Permet :
 *  - authentification
 *  - récupération de tous les coachs
 *  - création / modification / suppression
 *  - gestion des équipes assignées à un coach
 *  - recherche par catégorie
 */
public class CoachDAO {

    /**
     * Authentifie un coach via son username et password.
     * Compare le mot de passe en clair avec le hash stocké (BCrypt).
     *
     * @param username nom d'utilisateur
     * @param password mot de passe en clair saisi par l'utilisateur
     * @return Coach authentifié ou null si échec
     * @throws SQLException en cas d'erreur SQL
     */
    public Coach authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id, first_name, last_name, username, email, password FROM coaches WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashed = rs.getString("password");
                    if (hashed != null && BCrypt.checkpw(password, hashed)) {
                        Coach coach = new Coach();
                        coach.setId(rs.getInt("id"));
                        coach.setFirstName(rs.getString("first_name"));
                        coach.setLastName(rs.getString("last_name"));
                        coach.setUsername(rs.getString("username"));
                        coach.setEmail(rs.getString("email"));
                        coach.setTeams(loadTeamsForCoach(conn, coach.getId()));
                        return coach;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Retourne la liste de tous les coachs triés par nom et prénom.
     *
     * @return liste des coachs
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Coach> findAll() throws SQLException {
        String sql = "SELECT id, first_name, last_name, username, email FROM coaches ORDER BY last_name, first_name";
        List<Coach> coaches = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Coach coach = new Coach();
                coach.setId(rs.getInt("id"));
                coach.setFirstName(rs.getString("first_name"));
                coach.setLastName(rs.getString("last_name"));
                coach.setUsername(rs.getString("username"));
                coach.setEmail(rs.getString("email"));
                coach.setTeams(loadTeamsForCoach(conn, coach.getId()));
                coaches.add(coach);
            }
        }
        return coaches;
    }

    /**
     * Charge la liste des catégories (équipes) assignées à un coach.
     *
     * @param conn connexion SQL existante
     * @param coachId identifiant du coach
     * @return liste des catégories entraînées par ce coach
     * @throws SQLException en cas d'erreur SQL
     */
    private List<String> loadTeamsForCoach(Connection conn, int coachId) throws SQLException {
        String sql = "SELECT category FROM coach_teams WHERE coach_id = ? ORDER BY category";
        List<String> teams = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    teams.add(rs.getString("category"));
                }
            }
        }
        return teams;
    }

    /**
     * Crée un coach dans la base avec hash du mot de passe.
     *
     * @param username nom d'utilisateur
     * @param rawPassword mot de passe en clair
     * @param firstName prénom
     * @param lastName nom
     * @param email email du coach
     * @return ID généré ou -1 en cas d'échec
     * @throws SQLException en cas d'erreur SQL
     */
    public int createCoach(String username, String rawPassword, String firstName, String lastName, String email) throws SQLException {
        String sql = "INSERT INTO coaches (first_name, last_name, username, email, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, username);
            ps.setString(4, email);
            ps.setString(5, BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /**
     * Met à jour un coach avec ou sans mot de passe.
     *
     * @param coachId identifiant du coach
     * @param username nouveau username
     * @param firstName prénom
     * @param lastName nom
     * @param email email
     * @param newPasswordOrNull nouveau mot de passe (si null → on ne change pas)
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean updateCoach(int coachId, String username, String firstName, String lastName, String email, String newPasswordOrNull) throws SQLException {
        boolean updatePassword = newPasswordOrNull != null && !newPasswordOrNull.trim().isEmpty();
        String sql = updatePassword
                ? "UPDATE coaches SET first_name = ?, last_name = ?, username = ?, email = ?, password = ? WHERE id = ?"
                : "UPDATE coaches SET first_name = ?, last_name = ?, username = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, username);
            ps.setString(4, email);
            if (updatePassword) {
                ps.setString(5, BCrypt.hashpw(newPasswordOrNull, BCrypt.gensalt()));
                ps.setInt(6, coachId);
            } else {
                ps.setInt(5, coachId);
            }
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Supprime un coach via son ID.
     *
     * @param coachId identifiant
     * @return true si suppression réussie
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean deleteById(int coachId) throws SQLException {
        String sql = "DELETE FROM coaches WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Ajoute une équipe (catégorie) à un coach.
     *
     * @param coachId identifiant du coach
     * @param category catégorie (U9, U11, etc.)
     * @return true si l'insertion a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean addTeam(int coachId, String category) throws SQLException {
        String sql = "INSERT INTO coach_teams (coach_id, category) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            ps.setString(2, category);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Supprime toutes les équipes associées à un coach.
     *
     * @param coachId identifiant du coach
     * @return true toujours
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean deleteTeams(int coachId) throws SQLException {
        String sql = "DELETE FROM coach_teams WHERE coach_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            ps.executeUpdate();
            return true;
        }
    }

    /**
     * Recherche tous les coachs associés à une catégorie donnée.
     *
     * @param category catégorie recherchée
     * @return liste des coachs de cette catégorie
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Coach> findByCategory(String category) throws SQLException {
        String sql = "SELECT c.id, c.first_name, c.last_name, c.username, c.email " +
                "FROM coaches c INNER JOIN coach_teams ct ON ct.coach_id = c.id " +
                "WHERE ct.category = ? ORDER BY c.last_name, c.first_name";
        List<Coach> coaches = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Coach coach = new Coach();
                    coach.setId(rs.getInt("id"));
                    coach.setFirstName(rs.getString("first_name"));
                    coach.setLastName(rs.getString("last_name"));
                    coach.setUsername(rs.getString("username"));
                    coach.setEmail(rs.getString("email"));
                    coach.setTeams(loadTeamsForCoach(conn, coach.getId()));
                    coaches.add(coach);
                }
            }
        }
        return coaches;
    }
}
