package com.adauction.group19.view;

import com.adauction.group19.controller.UserManagementController;
import com.adauction.group19.utils.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class represents the User Management screen for administrators.
 */
public class UserManagementScreen {

  /**
   * Returns the user management screen scene.
   *
   * @param stage The stage to set the scene on.
   * @return The user management screen scene.
   */
  public static Scene getScene(Stage stage) {
    try {
      FXMLLoader loader = new FXMLLoader(UserManagementScreen.class.getResource("/fxml/UserManagementScreen.fxml"));
      Parent root = loader.load();

      UserManagementController controller = loader.getController();
      controller.setStage(stage);
      controller.loadUsers(); // Load users when the screen is created

      Scene scene = new Scene(root, 800, 600);
      ThemeManager.applyTheme(scene);

      return scene;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}