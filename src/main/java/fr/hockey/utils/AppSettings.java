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
 * Persistance simple des réglages applicatifs (chemin du logo, etc.)
 */
public class AppSettings {

    private static final Path CONFIG_DIR = Paths.get(System.getProperty("user.home"), ".hockeyclubmanager");
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("config.properties");
    private static final String KEY_LOGO_PATH = "logoPath";
    private static final String KEY_THEME_COLOR = "themeColor";
    // Informations légales
    private static final String KEY_LEGAL_NAME = "legal.name";
    private static final String KEY_LEGAL_ADDRESS = "legal.address";
    private static final String KEY_LEGAL_REGNO = "legal.regno";
    private static final String KEY_LEGAL_PUBLISHER = "legal.publisher";
    private static final String KEY_LEGAL_HOSTING = "legal.hosting";
    private static final String KEY_LEGAL_CONTACT = "legal.contact";
    private static final String KEY_LEGAL_PRIVACY = "legal.privacy";

    private static void ensureConfigDir() throws IOException {
        if (!Files.exists(CONFIG_DIR)) {
            Files.createDirectories(CONFIG_DIR);
        }
    }

    private static Properties load() throws IOException {
        Properties props = new Properties();
        if (Files.exists(CONFIG_FILE)) {
            try (FileInputStream fis = new FileInputStream(CONFIG_FILE.toFile())) {
                props.load(fis);
            }
        }
        return props;
    }

    private static void save(Properties props) throws IOException {
        ensureConfigDir();
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE.toFile())) {
            props.store(fos, "Hockey Club Manager Settings");
        }
    }

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

    public static void setLogoPath(String path) {
        try {
            Properties props = load();
            props.setProperty(KEY_LOGO_PATH, path == null ? "" : path);
            save(props);
        } catch (IOException ignored) {}
    }

    /**
     * Copie le fichier du logo dans le dossier de config et renvoie le chemin de destination.
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

    public static void setThemeColor(String colorName) {
        try {
            Properties props = load();
            props.setProperty(KEY_THEME_COLOR, colorName == null ? "" : colorName);
            save(props);
        } catch (IOException ignored) {}
    }

    // --- Informations légales (getters/setters) ---
    public static String getLegalName() { return getProp(KEY_LEGAL_NAME); }
    public static void setLegalName(String v) { setProp(KEY_LEGAL_NAME, v); }

    public static String getLegalAddress() { return getProp(KEY_LEGAL_ADDRESS); }
    public static void setLegalAddress(String v) { setProp(KEY_LEGAL_ADDRESS, v); }

    public static String getLegalRegNo() { return getProp(KEY_LEGAL_REGNO); }
    public static void setLegalRegNo(String v) { setProp(KEY_LEGAL_REGNO, v); }

    public static String getLegalPublisher() { return getProp(KEY_LEGAL_PUBLISHER); }
    public static void setLegalPublisher(String v) { setProp(KEY_LEGAL_PUBLISHER, v); }

    public static String getLegalHosting() { return getProp(KEY_LEGAL_HOSTING); }
    public static void setLegalHosting(String v) { setProp(KEY_LEGAL_HOSTING, v); }

    public static String getLegalContact() { return getProp(KEY_LEGAL_CONTACT); }
    public static void setLegalContact(String v) { setProp(KEY_LEGAL_CONTACT, v); }

    public static String getLegalPrivacy() { return getProp(KEY_LEGAL_PRIVACY); }
    public static void setLegalPrivacy(String v) { setProp(KEY_LEGAL_PRIVACY, v); }

    private static String getProp(String key) {
        try {
            Properties props = load();
            String val = props.getProperty(key);
            if (val != null && !val.isBlank()) return val;
        } catch (IOException ignored) {}
        return "";
    }

    private static void setProp(String key, String value) {
        try {
            Properties props = load();
            props.setProperty(key, value == null ? "" : value);
            save(props);
        } catch (IOException ignored) {}
    }
}