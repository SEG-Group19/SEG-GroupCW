package com.adauction.group19;

import com.adauction.group19.view.MainMenu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main class of the application. Launches the application.
 */
public class App extends Application {

    /**
     * Starts the application.
     * @param primaryStage The primary stage of the application.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Online Advertising Dashboard");

        // Open the Main Menu by default
        Scene mainMenuScene = MainMenu.getScene(primaryStage);
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    /**
     * Launches the application.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
