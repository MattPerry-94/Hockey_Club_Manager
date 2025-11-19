package fr.hockey.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * Gestion centralisée et persistance des réglages applicatifs.
 *
 * <p>Cette classe statique stocke et relit les paramètres persos de l'application,
 * tels que :</p>
 * <ul>
 *     <li>le chemin vers le logo du club ;</li>
 *     <li>la couleur du thème ;</li>
 *     <li>les informations légales affichées dans le tableau de bord ;</li>
 *     <li>tout autre paramètre persistant via un fichier properties.</li>
 * </ul>
 *
 * <p>Les réglages sont sauvegardés dans :</p>
 *
 * <pre>
 *   ~/.hockeyclubmanager/config.properties
 * </pre>
 *
 * <p>Le fichier est créé automatiquement si nécessaire.</p>
 */
public class AppSettings {

    /** Dossier de configuration dans le home utilisateur. */
    private static final Path CONFIG_DIR = Paths.get(System.getProperty("user.home"), ".hockeyclubmanager");

    /** Fichier properties contenant les réglages. */
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("config.properties");

    // --- Clés des propriétés générales ---
    private static final String KEY_LOGO_PATH = "logoPath";
    private static final String KEY_THEME_COLOR = "themeColor";

    // --- Clés des propriétés liées aux informations légales ---
    private static final String KEY_LEGAL_NAME = "legal.name";
    private static final String KEY_LEGAL_ADDRESS = "legal.address";
    private static final String KEY_LEGAL_REGNO = "legal.regno";
    private static final String KEY_LEGAL_PUBLISHER = "legal.publisher";
    private static final String KEY_LEGAL_HOSTING = "legal.hosting";
    private static final String KEY_LEGAL_CONTACT = "legal.contact";
    private static final String KEY_LEGAL_PRIVACY = "legal.privacy";

    /**
     * S'assure que le répertoire de configuration existe.
     *
     * @throws IOException si la création du dossier échoue
     */
    private static void ensureConfigDir() throws IOException {
        if (!Files.exists(CONFIG_DIR)) {
            Files.createDirectories(CONFIG_DIR);
        }
    }

    /**
     * Charge le fichier de configuration s'il existe.
     *
     * @return un objet {@link Properties} contenant toutes les clés
     * @throws IOException si la lecture échoue
     */
    private static Properties load() throws IOException {
        Properties props = new Properties();
        if (Files.exists(CONFIG_FILE)) {
            try (FileInputStream fis = new FileInputStream(CONFIG_FILE.toFile())) {
                props.load(fis);
            }
        }
        return props;
    }

    /**
     * Sauvegarde les propriétés dans le fichier de configuration.
     *
     * @param props propriétés à enregistrer
     * @throws IOException si l'écriture échoue
     */
    private static void save(Properties props) throws IOException {
        ensureConfigDir();
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE.toFile())) {
            props.store(fos, "Hockey Club Manager Settings");
        }
    }

    // -------------------------------------------------------------------------
    //  LOGO
    // -------------------------------------------------------------------------

    /**
     * Récupère le chemin actuel du logo du club, ou {@code null} s'il n'est pas défini.
     *
     * @return chemin absolu du logo ou null si absent
     */
    public static String getLogoPath() {
        try {
            Properties props = load();
            String val = props.getProperty(KEY_LOGO_PATH);
            if (val != null && !val.isBlank()) {
                return val;
            }
        } catch (IOException ignored) {}
        return null;
    }

    /**
     * Définit le chemin du logo du club.
     *
     * @param path chemin absolu du fichier image
     */
    public static void setLogoPath(String path) {
        try {
            Properties props = load();
            props.setProperty(KEY_LOGO_PATH, path == null ? "" : path);
            save(props);
        } catch (IOException ignored) {}
    }

    /**
     * Copie un fichier image dans le dossier de configuration et renvoie son chemin.
     *
     * @param sourceFile fichier image original
     * @return chemin du fichier copié dans ~/.hockeyclubmanager/
     * @throws IOException en cas d'erreur de copie
     */
    public static Path copyLogoToConfigDir(File sourceFile) throws IOException {
        ensureConfigDir();
        String fileName = sourceFile.getName();
        String ext = "";
        int dot = fileName.lastIndexOf('.');
        if (dot >= 0) {
            ext = fileName.substring(dot);
        }
        Path dest = CONFIG_DIR.resolve("club-logo" + ext);
        Files.copy(sourceFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
        return dest;
    }

    // -------------------------------------------------------------------------
    //  THÈME COULEUR
    // -------------------------------------------------------------------------

    /**
     * Lit la couleur de thème actuellement enregistrée.
     *
     * @return nom de la couleur, ou null si aucune couleur enregistrée
     */
    public static String getThemeColor() {
        try {
            Properties props = load();
            String val = props.getProperty(KEY_THEME_COLOR);
            if (val != null && !val.isBlank()) {
                return val;
            }
        } catch (IOException ignored) {}
        return null;
    }

    /**
     * Sauvegarde la couleur du thème.
     *
     * @param colorName nom interne de la couleur choisie
     */
    public static void setThemeColor(String colorName) {
        try {
            Properties props = load();
            props.setProperty(KEY_THEME_COLOR, colorName == null ? "" : colorName);
            save(props);
        } catch (IOException ignored) {}
    }

    // -------------------------------------------------------------------------
    //  INFORMATIONS LÉGALES
    // -------------------------------------------------------------------------

    /** @return valeur de la propriété demandée, ou chaîne vide si non trouvée */
    private static String getProp(String key) {
        try {
            Properties props = load();
            String val = props.getProperty(key);
            if (val != null && !val.isBlank()) return val;
        } catch (IOException ignored) {}
        return "";
    }

    /** Enregistre la valeur spécifiée dans les propriétés. */
    private static void setProp(String key, String value) {
        try {
            Properties props = load();
            props.setProperty(key, value == null ? "" : value);
            save(props);
        } catch (IOException ignored) {}
    }

    // Getters/Setters légalement exposés
    public static String getLegalName()     { return getProp(KEY_LEGAL_NAME); }
    public static void setLegalName(String v) { setProp(KEY_LEGAL_NAME, v); }

    public static String getLegalAddress()  { return getProp(KEY_LEGAL_ADDRESS); }
    public static void setLegalAddress(String v) { setProp(KEY_LEGAL_ADDRESS, v); }

    public static String getLegalRegNo()    { return getProp(KEY_LEGAL_REGNO); }
    public static void setLegalRegNo(String v) { setProp(KEY_LEGAL_REGNO, v); }

    public static String getLegalPublisher()  { return getProp(KEY_LEGAL_PUBLISHER); }
    public static void setLegalPublisher(String v) { setProp(KEY_LEGAL_PUBLISHER, v); }

    public static String getLegalHosting()   { return getProp(KEY_LEGAL_HOSTING); }
    public static void setLegalHosting(String v) { setProp(KEY_LEGAL_HOSTING, v); }

    public static String getLegalContact()   { return getProp(KEY_LEGAL_CONTACT); }
    public static void setLegalContact(String v) { setProp(KEY_LEGAL_CONTACT, v); }

    public static String getLegalPrivacy()   { return getProp(KEY_LEGAL_PRIVACY); }
    public static void setLegalPrivacy(String v) { setProp(KEY_LEGAL_PRIVACY, v); }
}
