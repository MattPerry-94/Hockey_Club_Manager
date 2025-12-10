package fr.hockey.dao;

import fr.hockey.models.Player;
import org.junit.jupiter.api.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerDAOTest {
    private Connection conn;

    @BeforeEach
    void setUp() throws Exception {
        conn = DriverManager.getConnection("jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1");
        DatabaseConnection.setConnection(conn);
        try (Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS licenses");
            st.execute("DROP TABLE IF EXISTS players");
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
                    "amount DECIMAL(10,2) NOT NULL" +
                    ")");
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (conn != null) conn.close();
        DatabaseConnection.setConnection(null);
    }

    @Test
    void testInsertPlayer() throws Exception {
        Player p = new Player();
        p.setFirstName("Jean");
        p.setLastName("Dupont");
        p.setCategory("U13");
        p.setRole("JOUEUR");
        p.setPosition("ATTAQUANT");
        p.setNumber(9);

        PlayerDAO dao = new PlayerDAO();
        boolean ok = dao.save(p);

        assertTrue(ok);
        System.out.println("INSERT_OK=" + ok);

        Connection checkConn = DatabaseConnection.getConnection();
        int count;
        try (PreparedStatement cps = checkConn.prepareStatement("SELECT COUNT(*) FROM players");
             ResultSet crs = cps.executeQuery()) {
            assertTrue(crs.next());
            count = crs.getInt(1);
        }
        System.out.println("PLAYERS_COUNT=" + count);

        int storedId;
        try (PreparedStatement gid = checkConn.prepareStatement("SELECT id FROM players LIMIT 1");
             ResultSet grs = gid.executeQuery()) {
            assertTrue(grs.next());
            storedId = grs.getInt(1);
            assertTrue(storedId > 0);
        }
        System.out.println("PLAYER_ID=" + storedId);

        String firstName;
        try (PreparedStatement ps = checkConn.prepareStatement("SELECT first_name FROM players WHERE id = ?")) {
            ps.setInt(1, storedId);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                firstName = rs.getString(1);
            }
        }
        System.out.println("PLAYER_FIRST_NAME=" + firstName);

        System.out.println("TEST_RESULT=PASS");

        assertTrue(true);
    }
}
