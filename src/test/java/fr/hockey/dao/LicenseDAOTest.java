package fr.hockey.dao;

import fr.hockey.models.License;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LicenseDAOTest {
    private Connection conn;

    @BeforeEach
    void setUp() throws Exception {
        conn = DriverManager.getConnection("jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1");
        DatabaseConnection.setConnection(conn);
        try (Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS licenses");
            st.execute("DROP TABLE IF EXISTS players");
            st.execute("DROP TABLE IF EXISTS category_fees");
            st.execute("CREATE TABLE players (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "first_name VARCHAR(50) NOT NULL," +
                    "last_name VARCHAR(50) NOT NULL," +
                    "category VARCHAR(10) NOT NULL," +
                    "role VARCHAR(20) NOT NULL," +
                    "position VARCHAR(20) NOT NULL," +
                    "number INT NULL" +
                    ")");
            st.execute("CREATE TABLE licenses (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "player_id INT NOT NULL," +
                    "paid BOOLEAN NOT NULL DEFAULT FALSE," +
                    "expiration_date DATE NOT NULL," +
                    "amount DECIMAL(10,2) NOT NULL," +
                    "FOREIGN KEY (player_id) REFERENCES players(id)" +
                    ")");
            st.execute("CREATE TABLE category_fees (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "category VARCHAR(10) NOT NULL UNIQUE," +
                    "fee DECIMAL(10,2) NOT NULL" +
                    ")");
        }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO players (first_name,last_name,category,role,position,number) VALUES (?,?,?,?,?,?)")) {
            ps.setString(1, "Jean");
            ps.setString(2, "Dupont");
            ps.setString(3, "U13");
            ps.setString(4, "JOUEUR");
            ps.setString(5, "ATTAQUANT");
            ps.setInt(6, 9);
            ps.executeUpdate();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn != null) conn.close();
        DatabaseConnection.setConnection(null);
    }

    @Test
    void testCreateLicenseAndFeeLookup() throws Exception {
        int playerId;
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM players LIMIT 1"); ResultSet rs = ps.executeQuery()) {
            assertTrue(rs.next());
            playerId = rs.getInt(1);
        }

        LicenseDAO dao = new LicenseDAO();
        boolean ok = dao.createForPlayer(playerId, 150.00, LocalDate.now().plusDays(30), false);
        assertTrue(ok);
        System.out.println("LICENSE_CREATE_OK=" + ok);

        conn = DriverManager.getConnection("jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1");
        DatabaseConnection.setConnection(conn);

        int count;
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM licenses"); ResultSet rs = ps.executeQuery()) {
            assertTrue(rs.next());
            count = rs.getInt(1);
        }
        System.out.println("LICENSE_COUNT=" + count);

        License lic = dao.findByPlayerId(playerId);
        assertNotNull(lic);
        System.out.println("LICENSE_PLAYER_ID=" + lic.getPlayerId());
        System.out.println("LICENSE_AMOUNT=" + lic.getAmount());
        System.out.println("LICENSE_PAID=" + lic.isPaid());

        conn = DriverManager.getConnection("jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1");
        DatabaseConnection.setConnection(conn);

        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO category_fees (category, fee) VALUES (?,?)")) {
            ps.setString(1, "U13");
            ps.setDouble(2, 220.00);
            ps.executeUpdate();
        }
        double fee = dao.getFeeForCategory("U13");
        assertEquals(220.00, fee, 0.001);
        System.out.println("FEE_U13=" + fee);

        System.out.println("TEST_RESULT=PASS");
    }

    @Test
    void testCreateLicenseForNonExistentPlayer() {
        LicenseDAO dao = new LicenseDAO();
        int fakePlayerId = 99999; // ID qui n'existe pas

        // On s'attend à une exception car la contrainte de clé étrangère (FOREIGN KEY) sera violée
        assertThrows(SQLException.class, () -> {
            dao.createForPlayer(fakePlayerId, 100.00, LocalDate.now(), false);
        }, "Créer une licence pour un joueur inexistant doit échouer (FK constraint)");

        System.out.println("TEST_INVALID_LICENSE=PASS (Exception SQL levée comme prévu)");
    }
}
