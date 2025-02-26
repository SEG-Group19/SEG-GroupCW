package com.adauction.group19.view;

import com.adauction.group19.controller.MetricsScreenController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * This class represents the View Metrics screen.
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

                        return new Scene(root, 1000, 600);
                } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                }
        }
}
