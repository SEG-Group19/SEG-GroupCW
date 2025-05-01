package com.adauction.group19.view;

import com.adauction.group19.controller.InputDataController;
import com.adauction.group19.tutorial.ComponentTutorial;
import com.adauction.group19.tutorial.PageVisitTracker;
import com.adauction.group19.utils.ThemeManager;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

/**
 * This class represents the Input Data screen.
 */
public class InputDataScreen {

    /**
     * Returns the Input Data screen.
     * @param stage The stage to set the scene on.
     * @return The Input Data screen.
     */
    public static Scene getScene(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(InputDataScreen.class.getResource("/fxml/InputDataScreen.fxml"));
            Parent root = loader.load();

            InputDataController controller = loader.getController();
            controller.setStage(stage);

            Scene scene = new Scene(root, 1200, 720);
            ThemeManager.applyTheme(scene);
            
            // Check if this is the first visit to this page and show tutorial if needed
            if (!PageVisitTracker.getInstance().hasVisitedPage("inputData")) {
                // Delay the tutorial slightly to ensure everything is loaded
                javafx.application.Platform.runLater(() -> {
                    ComponentTutorial.getInstance().startTutorial(scene, stage, "inputData");
                });
            }

            return scene;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
