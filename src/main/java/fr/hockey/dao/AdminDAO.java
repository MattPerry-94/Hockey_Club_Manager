package fr.hockey.dao;

import fr.hockey.models.Admin;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO permettant d'accéder et de gérer les administrateurs :
 * - authentification
 * - récupération par ID
 * - récupération de tous les admins
 * - insertion / mise à jour
 * - suppression
 *
 * Le hashing des mots de passe utilise BCrypt.
 */
public class AdminDAO {

    /**
     * Authentifie un administrateur à partir de son username et password.
     * Le mot de passe fourni est comparé au hash stocké dans la base.
     *
     * @param username nom d'utilisateur
     * @param password mot de passe en clair fourni par l'utilisateur
     * @return Admin authentifié ou null si échec
     * @throws SQLException en cas d’erreur SQL
     */
    public Admin authenticate(String username, String password) throws SQLException {
        String query = "SELECT * FROM admins WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                if (BCrypt.checkpw(password, hashedPassword)) {
                    return extractAdminFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Recherche un administrateur par son ID.
     *
     * @param id identifiant de l'admin
     * @return Admin ou null si non trouvé
     * @throws SQLException en cas d’erreur SQL
     */
    public Admin findById(int id) throws SQLException {
        String query = "SELECT * FROM admins WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractAdminFromResultSet(rs);
            }
        }
        return null;
    }

    /**
     * Retourne la liste de tous les administrateurs.
     *
     * @return liste des admins
     * @throws SQLException en cas d’erreur SQL
     */
    public List<Admin> findAll() throws SQLException {
        List<Admin> admins = new ArrayList<>();
        String query = "SELECT * FROM admins";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                admins.add(extractAdminFromResultSet(rs));
            }
        }
        return admins;
    }

    /**
     * Enregistre un administrateur :
     * - si l'ID existe → update
     * - sinon → insertion
     *
     * @param admin administrateur à sauvegarder
     * @return true si succès
     * @throws SQLException en cas d’erreur SQL
     */
    public boolean save(Admin admin) throws SQLException {
        if (admin.getId() > 0) {
            return update(admin);
        } else {
            return insert(admin);
        }
    }

    /**
     * Classe utilitaire interne permettant d’ajouter ou d’authentifier
     * des administrateurs de manière statique.
     * (Ancien code conservé pour compatibilité éventuelle.)
     */
    public class AdminManager {

        /**
         * Ajoute un administrateur avec hash du mot de passe.
         *
         * @param username nom d'utilisateur
         * @param first_name prénom
         * @param last_name nom
         * @param password mot de passe en clair
         * @throws SQLException en cas d’erreur SQL
         */
        public static void add_admin(String username, String first_name, String last_name, String password) throws SQLException {
            Connection connection = DatabaseConnection.getConnection();
            String hachedpwd = BCrypt.hashpw(password, BCrypt.gensalt(12));
            String sql_request = "INSERT INTO admins (username, first_name, last_name, password, email, role) VALUES(?, ?, ?, ?, '', 'ADMIN')";
            try {
                PreparedStatement pstmt = connection.prepareStatement(sql_request);
                pstmt.setString(1, username);
                pstmt.setString(2, first_name);
                pstmt.setString(3, last_name);
                pstmt.setString(4, hachedpwd);
                pstmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Authentifie un administrateur (version statique alternative).
         *
         * @param username nom d'utilisateur
         * @param password mot de passe en clair
         * @return true si authentification réussie
         * @throws SQLException en cas d’erreur SQL
         */
        public static boolean connexion(String username, String password) throws SQLException {
            Connection connection = DatabaseConnection.getConnection();
            String sql_request = "SELECT * FROM admins WHERE username = ?";
            try {
                PreparedStatement pstmt = connection.prepareStatement(sql_request);
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String pwd = rs.getString("password");
                    if (BCrypt.checkpw(password, pwd)) {
                        return true;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
    }

    /**
     * Met à jour les informations d’un administrateur existant.
     *
     * @param admin administrateur à modifier
     * @return true si la mise à jour a affecté au moins une ligne
     * @throws SQLException en cas d’erreur SQL
     */
    private boolean update(Admin admin) throws SQLException {
        String query = "UPDATE admins SET username = ?, first_name = ?, last_name = ?, email = ?, role = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, admin.getUsername());
            stmt.setString(2, admin.getFirstName());
            stmt.setString(3, admin.getLastName());
            stmt.setString(4, admin.getEmail());
            stmt.setString(5, admin.getRole());
            stmt.setInt(6, admin.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Met à jour le mot de passe d’un administrateur.
     * Le mot de passe est re-hashé avant insertion.
     *
     * @param adminId ID de l'admin
     * @param newPassword nouveau mot de passe en clair
     * @return true si succès
     * @throws SQLException en cas d’erreur SQL
     */
    public boolean updatePassword(int adminId, String newPassword) throws SQLException {
        String adminQuery = "UPDATE admins SET password = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement adminStmt = conn.prepareStatement(adminQuery)) {

            String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            adminStmt.setString(1, hashed);
            adminStmt.setInt(2, adminId);
            int affectedAdmins = adminStmt.executeUpdate();
            return affectedAdmins > 0;
        }
    }

    /**
     * Supprime un administrateur de la base.
     *
     * @param id identifiant de l’administrateur
     * @return true si une ligne a été supprimée
     * @throws SQLException en cas d’erreur SQL
     */
    public boolean delete(int id) throws SQLException {
        String deleteAdmin = "DELETE FROM admins WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement adminStmt = conn.prepareStatement(deleteAdmin)) {
            adminStmt.setInt(1, id);
            int affectedRows = adminStmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Construit un objet Admin à partir d’un ResultSet.
     *
     * @param rs ResultSet positionné sur un enregistrement admin
     * @return instance Admin
     * @throws SQLException en cas d’erreur d'accès aux colonnes
     */
    private Admin extractAdminFromResultSet(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getInt("id"));
        admin.setUsername(rs.getString("username"));
        admin.setPassword(rs.getString("password"));
        admin.setFirstName(rs.getString("first_name"));
        admin.setLastName(rs.getString("last_name"));
        admin.setEmail(rs.getString("email"));
        admin.setRole(rs.getString("role"));
        return admin;
    }

    /**
     * Insère un nouvel administrateur en base.
     * Le mot de passe est hashé avant insertion.
     *
     * @param admin administrateur à créer
     * @return true si l’insert a réussi
     * @throws SQLException en cas d’erreur SQL
     */
    private boolean insert(Admin admin) throws SQLException {
        String query = "INSERT INTO admins (username, password, first_name, last_name, email, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, admin.getUsername());
            String hashedPwd = BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt());
            stmt.setString(2, hashedPwd);
            stmt.setString(3, admin.getFirstName());
            stmt.setString(4, admin.getLastName());
            stmt.setString(5, admin.getEmail());
            stmt.setString(6, admin.getRole());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) return false;

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) admin.setId(keys.getInt(1));
            }

            return true;
        }
    }
}
