package com.adauction.group19.view;

import com.adauction.group19.controller.MetricsScreenController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class represents the View Metrics screen.
 */
public class ViewMetricsScreen {

    /**
     * Returns the View Metrics screen.
     * @param stage The stage to set the scene on.
     * @return The View Metrics screen.
     */
    public static Scene getScene(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(InputDataScreen.class.getResource("/fxml/ViewMetricsScreen.fxml"));
            Parent root = loader.load();

            // Get the campaign data using the controller
            MetricsScreenController controller = loader.getController();
            controller.loadCampaignData();
            controller.setStage(stage);

            return new Scene(root, 600, 400);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
