package com.adauction.group19.view;

import com.adauction.group19.controller.MetricsScreenController;
import com.adauction.group19.tutorial.ComponentTutorial;
import com.adauction.group19.tutorial.PageVisitTracker;
import com.adauction.group19.utils.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * This class represents the View Metrics screen.
 * Primary purpose is to load the FXML file and connect it to the controller.
 * Serves as a bridge between the application and the FXML-based UI.
 */
public class ViewMetricsScreen {
        private static final String FXML_PATH = "/fxml/ViewMetricsScreen.fxml";

        /**
         * Returns the View Metrics screen.
         * @param stage The stage to set the scene on.
         * @return The View Metrics screen scene.
         */
        public static Scene getScene(Stage stage) {
                try {
                        FXMLLoader loader = new FXMLLoader(ViewMetricsScreen.class.getResource(FXML_PATH));
                        Parent root = loader.load();

                        MetricsScreenController controller = loader.getController();
                        controller.setStage(stage); 
                        controller.loadCampaignData();

                        Scene scene = new Scene(root, 1200, 720);
                        scene.setUserData(controller);
                        ThemeManager.applyTheme(scene);
                        
                        // Check if this is the first visit to this page and show tutorial if needed
                        if (!PageVisitTracker.getInstance().hasVisitedPage("metrics")) {
                            // Delay the tutorial slightly to ensure everything is loaded
                            javafx.application.Platform.runLater(() -> {
                                ComponentTutorial.getInstance().startTutorial(scene, stage, "metrics");
                            });
                        }
                        
                        scene.setUserData(controller);

                        return scene;
                } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                }
        }
}
