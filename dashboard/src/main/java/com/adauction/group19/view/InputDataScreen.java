package com.adauction.group19.view;

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
            return new Scene(root, 600, 400);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
