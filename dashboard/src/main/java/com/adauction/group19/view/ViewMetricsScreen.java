package com.adauction.group19.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
        BorderPane layout = new BorderPane();
        layout.setPrefSize(600, 400);

        // Title of page
        Label title = new Label("View Metrics");
        title.setId("titleLabel");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        // A Back button to return to the Main Menu.
        Button btnBack = new Button("Back to Main Menu");
        btnBack.setId("btnBack");
        btnBack.setOnAction(e -> stage.setScene(MainMenu.getScene(stage)));

        layout.setBottom(btnBack);
        layout.setTop(title);
        layout.setPadding(new Insets(20, 20, 20, 20));
        BorderPane.setAlignment(btnBack, Pos.CENTER);

        return new Scene(layout, 600, 400);
    }
}
