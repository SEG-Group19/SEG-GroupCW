package com.adauction.group19.controller;

import com.adauction.group19.model.User;
import com.adauction.group19.service.DatabaseManager;
import com.adauction.group19.service.UserSession;
import com.adauction.group19.view.MainMenuScreen;
import com.adauction.group19.view.RegisterScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the login screen.
 */
public class LoginController {

  @FXML private TextField usernameField;
  @FXML private PasswordField passwordField;
  @FXML private Button loginButton;
  @FXML private Button registerButton;

  private Stage stage;

  /**
   * Initializes the controller after FXML is loaded.
   */
  @FXML
  public void initialize() {
    // Add action listeners, if needed
  }

  /**
   * Sets the stage for the screen.
   *
   * @param stage The stage to set.
   */
  public void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * Handles the login button click.
   *
   * @param event The action event.
   */
  @FXML
  private void handleLogin(ActionEvent event) {
    String username = usernameField.getText().trim();
    String password = passwordField.getText();

    if (username.isEmpty() || password.isEmpty()) {
      showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password.");
      return;
    }

    // Attempt to authenticate user
    User user = DatabaseManager.getInstance().authenticateUser(username, password);

    if (user != null) {
      // Set the current user in the session
      UserSession.getInstance().setCurrentUser(user);

      // Navigate to main menu
      if (stage != null) {
        Scene mainMenuScene = MainMenuScreen.getScene(stage);
        stage.setScene(mainMenuScene);
        stage.setTitle("AdVantage - Online Advertising Dashboard");
      }
    } else {
      showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
    }
  }

  /**
   * Handles the register button click.
   *
   * @param event The action event.
   */
  @FXML
  private void handleRegister(ActionEvent event) {
    if (stage != null) {
      Scene registerScene = RegisterScreen.getScene(stage);
      stage.setScene(registerScene);
    }
  }

  /**
   * Shows an alert dialog.
   *
   * @param type    The alert type.
   * @param title   The alert title.
   * @param content The alert content.
   */
  private void showAlert(Alert.AlertType type, String title, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }
}