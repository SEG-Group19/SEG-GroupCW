package com.adauction.group19.view;

import com.adauction.group19.controller.LoginController;
import com.adauction.group19.utils.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class represents the Login screen.
 */
public class LoginScreen {

  /**
   * Returns the login screen scene.
   *
   * @param stage The stage to set the scene on.
   * @return The login screen scene.
   */
  public static Scene getScene(Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader(LoginScreen.class.getResource("/fxml/LoginScreen.fxml"));
      Parent root = loader.load();

      LoginController controller = loader.getController();
      controller.setStage(stage);

      Scene scene = new Scene(root, 400, 300);
      ThemeManager.applyTheme(scene);

      return scene;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}