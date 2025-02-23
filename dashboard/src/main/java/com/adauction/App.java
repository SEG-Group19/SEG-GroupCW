package com.adauction;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("HelloFX Test");

        // Layout
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        // A label to display messages
        Label label = new Label("Testing message");
        label.setId("lblMessage");

        // Example button
        Button btn = new Button("Click me!");
        btn.setId("myButton");
        btn.setOnAction(e -> label.setText("Hello from button!"));

        layout.getChildren().addAll(btn, label);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Example method
    public static int add(int a, int b) {
        return a + b;
    }
}
