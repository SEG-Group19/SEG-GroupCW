package com.adauction.view;

import com.adauction.view.InputDataScreen;
import com.adauction.view.ViewMetricsScreen;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        Button btnInputData = new Button("Input Data");
        Button btnViewMetrics = new Button("View Metrics");

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(btnInputData, btnViewMetrics);

        Scene scene = new Scene(layout, 600, 400);

        // Choose which screen to go to based on the button clicked
        btnInputData.setOnAction(e -> stage.setScene(InputDataScreen.getScene(stage)));
        btnViewMetrics.setOnAction(e -> stage.setScene(ViewMetricsScreen.getScene(stage)));

        return scene;
    }
}
