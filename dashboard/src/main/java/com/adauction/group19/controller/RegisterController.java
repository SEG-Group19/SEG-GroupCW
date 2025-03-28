package com.adauction.group19.controller;

import com.adauction.group19.model.User;
import com.adauction.group19.model.UserRole;
import com.adauction.group19.service.DatabaseManager;
import com.adauction.group19.service.UserSession;
import com.adauction.group19.view.LoginScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.regex.Pattern;

/**
 * Controller for the registration screen.
 */
public class RegisterController {

  @FXML private TextField usernameField;
  @FXML private PasswordField passwordField;
  @FXML private PasswordField confirmPasswordField;
  @FXML private ComboBox<UserRole> roleComboBox;
  @FXML private Button registerButton;
  @FXML private Button backButton;

  private Stage stage;

  // Email validation pattern removed

  /**
   * Initializes the controller after FXML is loaded.
   */
  @FXML
  public void initialize() {
    // Initialize role combo box
    roleComboBox.getItems().addAll(UserRole.values());
    roleComboBox.setValue(UserRole.USER); // Default role

    // Initially hide the role selector as it should only be visible to admins
    boolean isAdmin = UserSession.getInstance().isAdmin();
    roleComboBox.setVisible(isAdmin);
    roleComboBox.setManaged(isAdmin);
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
   * Handles the register button click.
   *
   * @param event The action event.
   */
  @FXML
  private void handleRegister(ActionEvent event) {
    String username = usernameField.getText().trim();
    String password = passwordField.getText();
    String confirmPassword = confirmPasswordField.getText();
    UserRole role = roleComboBox.getValue();

    // Default to USER role if not an admin
    if (!UserSession.getInstance().isAdmin()) {
      role = UserRole.USER;
    }

    // Validate input
    if (!validateInput(username, password, confirmPassword)) {
      return;
    }

    // Check if username already exists
    if (DatabaseManager.getInstance().usernameExists(username)) {
      showAlert(Alert.AlertType.ERROR, "Registration Error",
          "Username already exists. Please choose a different username.");
      return;
    }

    // Email check removed

    // Register the user
    User newUser = DatabaseManager.getInstance().registerUser(username, password, role);

    if (newUser != null) {
      showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
          "User has been registered successfully.");

      // Navigate back to login screen
      if (stage != null) {
        Scene loginScene = LoginScreen.getScene(stage);
        stage.setScene(loginScene);
      }
    } else {
      showAlert(Alert.AlertType.ERROR, "Registration Error",
          "Failed to register user. Please try again.");
    }
  }

  /**
   * Handles the back button click.
   *
   * @param event The action event.
   */
  @FXML
  private void handleBack(ActionEvent event) {
    if (stage != null) {
      Scene loginScene = LoginScreen.getScene(stage);
      stage.setScene(loginScene);
    }
  }

  /**
   * Validates the registration input.
   *
   * @param username        The username.
   * @param password        The password.
   * @param confirmPassword The confirmed password.
   * @return true if input is valid, false otherwise.
   */
  private boolean validateInput(String username, String password, String confirmPassword) {
    // Check if any field is empty
    if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
      showAlert(Alert.AlertType.ERROR, "Registration Error",
          "All fields are required.");
      return false;
    }

    // Check username length
    if (username.length() < 4) {
      showAlert(Alert.AlertType.ERROR, "Registration Error",
          "Username must be at least 4 characters long.");
      return false;
    }

    // Check password length
    if (password.length() < 6) {
      showAlert(Alert.AlertType.ERROR, "Registration Error",
          "Password must be at least 6 characters long.");
      return false;
    }

    // Check if passwords match
    if (!password.equals(confirmPassword)) {
      showAlert(Alert.AlertType.ERROR, "Registration Error",
          "Passwords do not match.");
      return false;
    }

    // Email validation removed

    return true;
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