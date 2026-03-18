package fr.hockey.utils;

import java.util.regex.Pattern;

/**
 * Utilitaire de validation des mots de passe.
 * Politique :
 * - Au moins 12 caractères
 * - Au moins une minuscule
 * - Au moins une majuscule
 * - Au moins un chiffre
 * - Au moins un caractère spécial
 */
public class PasswordValidator {

    private static final int MIN_LENGTH = 12;
    private static final Pattern LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL = Pattern.compile(".*[^a-zA-Z0-9].*");

    /**
     * Vérifie si le mot de passe respecte la politique de sécurité.
     *
     * @param password le mot de passe à tester
     * @return true si valide, false sinon
     */
    public static boolean isValid(String password) {
        if (password == null) return false;
        if (password.length() < MIN_LENGTH) return false;
        if (!LOWERCASE.matcher(password).matches()) return false;
        if (!UPPERCASE.matcher(password).matches()) return false;
        if (!DIGIT.matcher(password).matches()) return false;
        if (!SPECIAL.matcher(password).matches()) return false;
        return true;
    }

    /**
     * Retourne le message d'erreur décrivant la politique de mot de passe.
     *
     * @return le message d'erreur
     */
    public static String getErrorMessage() {
        return "Le mot de passe doit contenir au moins 12 caractères, " +
               "une majuscule, une minuscule, un chiffre et un caractère spécial.";
    }
}
