package com.adauction.group19.view;

import com.adauction.group19.controller.RegisterController;
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

      return scene;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}