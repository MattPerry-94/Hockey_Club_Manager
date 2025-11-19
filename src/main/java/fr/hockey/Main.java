package fr.hockey;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import fr.hockey.utils.ThemeManager;
import fr.hockey.utils.AppSettings;

import java.io.IOException;

/**
 * Point d'entrée principal de l'application Hockey Club Manager.
 *
 * <p>
 * Cette classe initialise l'interface graphique JavaFX, charge l'écran
 * de connexion et applique le thème choisi par l'utilisateur.
 * </p>
 *
 * <p>
 * Le thème est récupéré via {@link AppSettings#getThemeColor()} et appliqué
 * globalement grâce à {@link ThemeManager#applyTheme(Parent, String)}.
 * </p>
 *
 * <p>
 * La fenêtre principale impose une taille minimale de 800×600 afin de
 * garantir une lisibilité correcte.
 * </p>
 */
public class Main extends Application {

    /**
     * Méthode appelée automatiquement au lancement de l'application JavaFX.
     *
     * @param primaryStage la fenêtre principale fournie par la plateforme JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Chargement de la vue FXML de connexion
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

            // Chargement du thème sauvegardé
            String saved = AppSettings.getThemeColor();
            if (saved == null || saved.isBlank()) {
                saved = "Gris"; // thème par défaut
            }
            ThemeManager.applyTheme(root, saved);

            // Création de la fenêtre principale
            primaryStage.setTitle("Club de Hockey - Gestionnaire de Licences");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lance l'application JavaFX.
     *
     * @param args arguments en ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
