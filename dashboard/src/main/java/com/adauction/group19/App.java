package com.adauction.group19;

import com.adauction.group19.view.MainMenuScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
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

        Font.loadFont(getClass().getResource("/fonts/Roboto-Light.ttf").toExternalForm(), 12);
        Font.loadFont(getClass().getResource("/fonts/Roboto-Bold.ttf").toExternalForm(), 12);
        Font.loadFont(getClass().getResource("/fonts/Roboto-Regular.ttf").toExternalForm(), 12);

        // Open the Main Menu by default
        Scene mainMenuScene = MainMenuScreen.getScene(primaryStage);
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
