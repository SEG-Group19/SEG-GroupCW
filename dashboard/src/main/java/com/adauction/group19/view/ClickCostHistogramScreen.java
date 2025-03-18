package com.adauction.group19.view;

import com.adauction.group19.controller.ClickCostHistogramController;
import com.adauction.group19.utils.ThemeManager;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class ClickCostHistogramScreen {
    private static final String FXML_PATH = "/fxml/ClickCostHistogram.fxml";

    public static Scene getScene(Stage stage, String filePath) {
        try {
            System.out.println("Loading ClickCostHistogram FXML...");
            
            // Get the resource URL
            URL fxmlUrl = ClickCostHistogramScreen.class.getResource(FXML_PATH);
            if (fxmlUrl == null) {
                throw new IOException("Could not find FXML file: " + FXML_PATH);
            }
            System.out.println("Found FXML at: " + fxmlUrl);
            
            // Create the loader
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            System.out.println("Created FXML loader");
            
            // Load the root node
            Parent root = loader.load();
            System.out.println("Loaded FXML root node");
            
            // Get and initialize the controller
            ClickCostHistogramController controller = loader.getController();
            if (controller == null) {
                throw new IOException("Could not load controller");
            }
            System.out.println("Got controller instance");
            
            // Create the scene first
            Scene scene = new Scene(root, 1200, 720);
            System.out.println("Created scene");
            
            // Apply theme
            ThemeManager.applyTheme(scene);
            System.out.println("Applied theme");
            
            // Set stage and load data after scene is created
            controller.setStage(stage);
            System.out.println("Set stage on controller");
            
            // Load data after a short delay to ensure chart initialization is complete
            Platform.runLater(() -> {
                try {
                    System.out.println("Loading data in Platform.runLater");
                    controller.loadData(filePath);
                } catch (Exception e) {
                    System.err.println("Error loading data in Platform.runLater: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            return scene;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error creating histogram view: " + e.getMessage());
            
            // Show error dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Loading View");
            alert.setHeaderText("Could not load the histogram view");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
            
            return null;
        }
    }
} 