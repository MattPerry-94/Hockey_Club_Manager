package fr.hockey.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Journalisation des modifications de données (audit) au niveau BD.
 *
 * Écrit dans le fichier `audit.log` sous le dossier utilisateur
 * `~/.hockeyclubmanager/` (comme le login.log).
 */
public final class AuditLogger {

    private static final String LOG_DIR = System.getProperty("user.home") + 
            java.io.File.separator + ".hockeyclubmanager";
    private static final String LOG_PATH = LOG_DIR + java.io.File.separator + "audit.log";

    private static Logger logger;

    private AuditLogger() {}

    private static synchronized Logger getLogger() {
        if (logger != null) return logger;

        logger = Logger.getLogger("DataAudit");
        logger.setUseParentHandlers(false);
        try {
            Path dir = Paths.get(LOG_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            FileHandler fh = new FileHandler(LOG_PATH, true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            // Si l'init échoue, on loggue sur la console
            logger.log(Level.WARNING, "Impossible d'initialiser AuditLogger", e);
        }
        return logger;
    }

    /**
     * Retourne l'acteur courant (admin ou coach) via SessionManager.
     */
    private static String currentActor() {
        SessionManager sm = SessionManager.getInstance();
        if (sm.isAdmin() && sm.getCurrentAdmin() != null) {
            return "ADMIN:" + sm.getCurrentAdmin().getUsername();
        }
        if (sm.isCoach() && sm.getCurrentCoach() != null) {
            return "COACH:" + sm.getCurrentCoach().getUsername();
        }
        return "SYSTEM";
    }

    /**
     * Loggue une modification de données.
     *
     * @param table      nom de la table
     * @param operation  type d'opération (INSERT/UPDATE/DELETE/...)
     * @param identifier identifiant clé (id, player_id, etc.)
     * @param details    résumé des champs impactés (sans données sensibles)
     */
    public static void logChange(String table, String operation, String identifier, String details) {
        String actor = currentActor();
        String message = String.format("actor=%s | table=%s | op=%s | id=%s | %s",
                actor, table, operation, identifier, details == null ? "" : details);
        getLogger().info(message);
    }
}

