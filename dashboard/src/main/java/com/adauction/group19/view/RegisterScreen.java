package com.adauction.group19.view;

import com.adauction.group19.controller.RegisterController;
import com.adauction.group19.tutorial.ComponentTutorial;
import com.adauction.group19.tutorial.PageVisitTracker;
import com.adauction.group19.utils.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class represents the Registration screen.
 */
public class RegisterScreen {

  /**
   * Returns the registration screen scene.
   *
   * @param stage The stage to set the scene on.
   * @return The registration screen scene.
   */
  public static Scene getScene(Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader(RegisterScreen.class.getResource("/fxml/RegisterScreen.fxml"));
      Parent root = loader.load();

      RegisterController controller = loader.getController();
      controller.setStage(stage);

      Scene scene = new Scene(root, 1200, 720);
      ThemeManager.applyTheme(scene);
      
      // Check if this is the first visit to this page and show tutorial if needed
      if (!PageVisitTracker.getInstance().hasVisitedPage("register")) {
        // Delay the tutorial slightly to ensure everything is loaded
        javafx.application.Platform.runLater(() -> {
          ComponentTutorial.getInstance().startTutorial(scene, stage, "register");
        });
      }

      return scene;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}