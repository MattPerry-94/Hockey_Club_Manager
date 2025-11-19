package fr.hockey.utils;

import javafx.scene.Parent;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Utilitaire chargé de la gestion des thèmes colorimétriques de l'application.
 *
 * <p>Cette classe permet :
 * <ul>
 *     <li>de lister les couleurs disponibles dans l’interface,</li>
 *     <li>de convertir un nom de couleur en code hexadécimal,</li>
 *     <li>d'appliquer dynamiquement un thème complet à une scène JavaFX,</li>
 *     <li>d'estimer si une couleur est claire ou foncée pour ajuster la lisibilité du texte.</li>
 * </ul>
 *
 * <p>Les valeurs CSS sont injectées sous forme de variables personnalisées (<code>-app-*</code>).</p>
 */
public class ThemeManager {

    /**
     * Retourne la liste des couleurs disponibles pour le thème.
     *
     * @return liste de noms de couleurs en français
     */
    public static List<String> getAvailableColors() {
        return Arrays.asList(
                "Rouge", "Bleu", "Vert", "Jaune", "Noir",
                "Blanc", "Gris", "Orange", "Violet", "Rose",
                "Marron / Brun", "Beige"
        );
    }

    /**
     * Convertit un nom de couleur en son code héxadécimal correspondant.
     *
     * @param colorName nom de la couleur (en français)
     * @return code hex (#RRGGBB) ou bleu par défaut si inconnu
     */
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

    /**
     * Détermine si une couleur donnée est claire ou sombre à partir de son code hexadécimal.
     *
     * <p>Utilise une approximation de la formule YIQ pour estimer la luminosité.</p>
     *
     * @param hex code couleur (#RRGGBB)
     * @return true si la couleur est claire, false sinon
     */
    private static boolean isLight(String hex) {
        int r = Integer.parseInt(hex.substring(1, 3), 16);
        int g = Integer.parseInt(hex.substring(3, 5), 16);
        int b = Integer.parseInt(hex.substring(5, 7), 16);
        double yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000.0;
        return yiq >= 180;
    }

    /**
     * Applique dynamiquement le thème choisi aux styles de la scène JavaFX.
     *
     * <p>Définit diverses variables CSS personnalisées :
     * <ul>
     *     <li><code>-app-accent</code> : couleur principale choisie</li>
     *     <li><code>-app-button-bg</code> : couleur des boutons</li>
     *     <li><code>-app-on-accent</code> : couleur du texte sur fond accent</li>
     *     <li><code>-app-header-bg</code> : couleur de l’en-tête</li>
     *     <li><code>-app-on-header</code> : couleur du texte dans l’en-tête</li>
     *     <li><code>-app-sidebar-bg</code> : couleur de la barre latérale</li>
     * </ul>
     *
     * @param root      élément racine de la scène JavaFX à styliser
     * @param colorName nom de la couleur choisie
     */
    public static void applyTheme(Parent root, String colorName) {
        String accent = colorToHex(colorName);

        // Définition forcée d’un fond de boutons neutre (lisibilité)
        String buttonBg = "#7f8c8d";
        String onButton = isLight(buttonBg) ? "#000000" : "#ffffff";

        // Header & Sidebar utilisant l'accent choisi
        String headerBg = accent;
        String onHeader = isLight(headerBg) ? "#000000" : "#ffffff";
        String sidebarBg = accent;

        String styleVars = String.join(" ",
                "-app-accent: " + accent + ";",
                "-app-button-bg: " + buttonBg + ";",
                "-app-on-accent: " + onButton + ";",
                "-app-header-bg: " + headerBg + ";",
                "-app-on-header: " + onHeader + ";",
                "-app-sidebar-bg: " + sidebarBg + ";"
        );

        root.setStyle(styleVars);
    }
}
