package fr.hockey;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import fr.hockey.utils.ThemeManager;
import fr.hockey.utils.AppSettings;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            String saved = AppSettings.getThemeColor();
            if (saved == null || saved.isBlank()) {
                saved = "Gris";
            }
            ThemeManager.applyTheme(root, saved);
            primaryStage.setTitle("Club de Hockey - Gestionnaire de Licences");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}