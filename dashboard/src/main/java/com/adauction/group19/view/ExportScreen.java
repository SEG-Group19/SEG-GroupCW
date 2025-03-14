package com.adauction.group19.view;

import com.adauction.group19.controller.ExportScreenController;
import com.adauction.group19.utils.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * This class represents the Export screen. Primary purpose is to load the FXML
 * file and connect it to the controller. Serves as a bridge between the
 * application and the FXML-based UI.
 */
public class ExportScreen {

    private static final String FXML_PATH = "/fxml/ExportScreen.fxml";

    /**
     * Returns the Export screen.
     *
     * @param stage The stage to set the scene on.
     * @return The Export screen scene.
     */
    public static Scene getScene(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(ExportScreen.class.getResource(FXML_PATH));
            Parent root = loader.load();

            ExportScreenController controller = loader.getController();
            controller.setStage(stage);
            controller.loadCampaignData();

            Scene scene = new Scene(root, 1200, 720);
            ThemeManager.applyTheme(scene);

            return scene;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
