package fr.hockey.dao;

import fr.hockey.models.Admin;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    public Admin authenticate(String username, String password) throws SQLException {
        String query = "SELECT * FROM admins WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                
                // Vérifier le mot de passe
                if (BCrypt.checkpw(password, hashedPassword)) {
                    return extractAdminFromResultSet(rs);
                }
            }
        }
        
        return null; // Authentification échouée
    }
    
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
    
    public boolean save(Admin admin) throws SQLException {
        if (admin.getId() > 0) {
            return update(admin);
        } else {
            return insert(admin);
        }
    }

    public class AdminManager {

        public static void add_admin(String username, String first_name, String last_name, String password) throws SQLException {

            Connection connection = DatabaseConnection.getConnection();
            String hachedpwd = BCrypt.hashpw(password, BCrypt.gensalt(12));
            String sql_request = "INSERT INTO admins (username, first_name, last_name, password, email, role) VALUES(?, ?, ?, ?, '', 'ADMIN')";
            try{
                PreparedStatement pstmt = connection.prepareStatement(sql_request);
                pstmt.setString(1,username);
                pstmt.setString(2,first_name);
                pstmt.setString(3,last_name);
                pstmt.setString(4,hachedpwd);
                pstmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

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
    
    public boolean delete(int id) throws SQLException {
        String deleteAdmin = "DELETE FROM admins WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement adminStmt = conn.prepareStatement(deleteAdmin)) {
            adminStmt.setInt(1, id);
            int affectedRows = adminStmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
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

            // Les admins ne créent pas d'entrées dans coaches ici
            return true;
        }
    }
}