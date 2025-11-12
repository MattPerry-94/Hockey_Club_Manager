package fr.hockey.dao;

import fr.hockey.models.Coach;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoachDAO {

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
                        // Charger les équipes
                        coach.setTeams(loadTeamsForCoach(conn, coach.getId()));
                        return coach;
                    }
                }
            }
        }
        return null;
    }

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

    public boolean deleteById(int coachId) throws SQLException {
        String sql = "DELETE FROM coaches WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean addTeam(int coachId, String category) throws SQLException {
        String sql = "INSERT INTO coach_teams (coach_id, category) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            ps.setString(2, category);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteTeams(int coachId) throws SQLException {
        String sql = "DELETE FROM coach_teams WHERE coach_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            ps.executeUpdate();
            return true;
        }
    }

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
                    // Charger les équipes pour information
                    coach.setTeams(loadTeamsForCoach(conn, coach.getId()));
                    coaches.add(coach);
                }
            }
        }
        return coaches;
    }
}