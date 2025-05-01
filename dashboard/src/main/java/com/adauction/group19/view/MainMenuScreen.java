package com.adauction.group19.view;

import com.adauction.group19.controller.MainMenuController;
import com.adauction.group19.tutorial.ComponentTutorial;
import com.adauction.group19.tutorial.PageVisitTracker;
import com.adauction.group19.utils.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class represents the Main Menu screen.
 */
public class MainMenuScreen {

    /**
     * Returns the main menu scene. Contains 2 buttons: Input Data and View Metrics.
     *
     * @param stage The stage to set the scene on.
     * @return The main menu scene.
     */
    public static Scene getScene(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(InputDataScreen.class.getResource("/fxml/MainMenuScreen.fxml"));
            Parent root = loader.load();

            MainMenuController controller = loader.getController();
            controller.setStage(stage);

            Scene scene = new Scene(root, 1200, 720);
            ThemeManager.applyTheme(scene);
            
            // Check if this is the first visit to this page and show tutorial if needed
            if (!PageVisitTracker.getInstance().hasVisitedPage("mainMenu")) {
                // Delay the tutorial slightly to ensure everything is loaded
                javafx.application.Platform.runLater(() -> {
                    ComponentTutorial.getInstance().startTutorial(scene, stage, "mainMenu");
                });
            }

            return scene;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}