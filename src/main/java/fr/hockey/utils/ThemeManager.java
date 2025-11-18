package fr.hockey.utils;

import javafx.scene.Parent;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ThemeManager {

    public static List<String> getAvailableColors() {
        return Arrays.asList(
                "Rouge", "Bleu", "Vert", "Jaune", "Noir",
                "Blanc", "Gris", "Orange", "Violet", "Rose",
                "Marron / Brun", "Beige"
        );
    }

    public static String colorToHex(String colorName) {
        String c = colorName == null ? "" : colorName.toLowerCase(Locale.ROOT).trim();
        switch (c) {
            case "rouge": return "#e74c3c";
            case "bleu": return "#3498db";
            case "vert": return "#27ae60";
            case "jaune": return "#f1c40f";
            case "noir": return "#000000";
            case "blanc": return "#ffffff";
            case "gris": return "#7f8c8d";
            case "orange": return "#e67e22";
            case "violet": return "#9b59b6";
            case "rose": return "#ff69b4";
            case "marron / brun": return "#8B4513";
            case "beige": return "#f5f5dc";
            default: return "#3498db"; // Bleu par défaut
        }
    }

    private static boolean isLight(String hex) {
        // Estimation de luminosité (YIQ)
        int r = Integer.parseInt(hex.substring(1, 3), 16);
        int g = Integer.parseInt(hex.substring(3, 5), 16);
        int b = Integer.parseInt(hex.substring(5, 7), 16);
        double yiq = ((r*299)+(g*587)+(b*114))/1000.0;
        return yiq >= 180; // seuil simple
    }

    public static void applyTheme(Parent root, String colorName) {
        String accent = colorToHex(colorName);
        // Boutons forcés en gris pour visibilité
        String buttonBg = "#7f8c8d"; // Gris moyen
        String onButton = isLight(buttonBg) ? "#000000" : "#ffffff";
        // Pour header et sidebar, on réutilise l’accent choisi
        String headerBg = accent;
        String onHeader = isLight(headerBg) ? "#000000" : "#ffffff";
        String sidebarBg = accent;

        String styleVars = String.join(" ",
                "-app-accent: "+accent+";",
                "-app-button-bg: "+buttonBg+";",
                "-app-on-accent: "+onButton+";",
                "-app-header-bg: "+headerBg+";",
                "-app-on-header: "+onHeader+";",
                "-app-sidebar-bg: "+sidebarBg+";"
        );
        root.setStyle(styleVars);
    }
}