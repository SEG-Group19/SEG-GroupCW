package com.adauction.group19.view;

import com.adauction.group19.controller.MainMenuController;
import com.adauction.group19.controller.MetricsScreenController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class represents the Main Menu screen.
 */
public class MainMenu {

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

            // Get the campaign data using the controller
            MainMenuController controller = loader.getController();
            controller.setStage(stage);

            return new Scene(root, 1000, 600);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
