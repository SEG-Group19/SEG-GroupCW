package com.adauction.group19.controller;

import com.adauction.group19.model.User;
import com.adauction.group19.model.UserRole;
import com.adauction.group19.service.DatabaseManager;
import com.adauction.group19.service.UserSession;
import com.adauction.group19.view.MainMenuScreen;
import com.adauction.group19.view.RegisterScreen;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.util.Optional;

/**
 * Controller for the user management screen.
 */
public class UserManagementController {

  @FXML private TableView<User> userTable;
  @FXML private TableColumn<User, Integer> idColumn;
  @FXML private TableColumn<User, String> usernameColumn;
  @FXML private TableColumn<User, String> emailColumn;
  @FXML private TableColumn<User, String> roleColumn;
  @FXML private TableColumn<User, String> statusColumn;

  @FXML private Button addUserButton;
  @FXML private Button editUserButton;
  @FXML private Button changePasswordButton;
  @FXML private Button toggleActiveButton;
  @FXML private Button deleteUserButton;
  @FXML private Button backButton;

  private Stage stage;
  private ObservableList<User> users = FXCollections.observableArrayList();

  /**
   * Initializes the controller after FXML is loaded.
   */
  @FXML
  public void initialize() {
    // Set up table columns
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
    emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

    roleColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getRole().getDisplayName()));

    statusColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().isActive() ? "Active" : "Inactive"));

    // Bind the table to the observable list
    userTable.setItems(users);

    // Add selection listener to enable/disable buttons based on selection
    userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
      boolean hasSelection = newSelection != null;
      boolean isCurrentUser = hasSelection &&
          UserSession.getInstance().getCurrentUser().getId() == newSelection.getId();

      editUserButton.setDisable(!hasSelection);
      changePasswordButton.setDisable(!hasSelection);
      toggleActiveButton.setDisable(!hasSelection || isCurrentUser);
      deleteUserButton.setDisable(!hasSelection || isCurrentUser);
    });
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
   * Loads all users from the database into the table.
   */
  public void loadUsers() {
    users.clear();
    users.addAll(DatabaseManager.getInstance().getAllUsers());
  }

  /**
   * Handles the add user button click.
   *
   * @param event The action event.
   */
  @FXML
  private void handleAddUser(ActionEvent event) {
    if (stage != null) {
      Scene registerScene = RegisterScreen.getScene(stage);
      stage.setScene(registerScene);
    }
  }

  /**
   * Handles the edit user button click.
   *
   * @param event The action event.
   */
  @FXML
  private void handleEditUser(ActionEvent event) {
    User selectedUser = userTable.getSelectionModel().getSelectedItem();
    if (selectedUser == null) {
      return;
    }

    // Create a dialog for editing user details
    Dialog<User> dialog = new Dialog<>();
    dialog.setTitle("Edit User");
    dialog.setHeaderText("Edit user details for: " + selectedUser.getUsername());

    // Set the button types
    ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

    // Create the username, email, and role fields
    TextField usernameField = new TextField(selectedUser.getUsername());
    TextField emailField = new TextField(selectedUser.getEmail());
    ComboBox<UserRole> roleComboBox = new ComboBox<>();

    roleComboBox.getItems().addAll(UserRole.values());
    roleComboBox.setValue(selectedUser.getRole());

    // Create the layout
    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

    grid.add(new Label("Username:"), 0, 0);
    grid.add(usernameField, 1, 0);
    grid.add(new Label("Email:"), 0, 1);
    grid.add(emailField, 1, 1);
    grid.add(new Label("Role:"), 0, 2);
    grid.add(roleComboBox, 1, 2);

    dialog.getDialogPane().setContent(grid);

    // Request focus on the username field by default
    usernameField.requestFocus();

    // Convert the result to a user when the save button is clicked
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == saveButtonType) {
        // Update user with new values
        selectedUser.setUsername(usernameField.getText());
        selectedUser.setEmail(emailField.getText());
        selectedUser.setRole(roleComboBox.getValue());
        return selectedUser;
      }
      return null;
    });

    Optional<User> result = dialog.showAndWait();

    result.ifPresent(user -> {
      // Update user in database
      if (DatabaseManager.getInstance().updateUser(user)) {
        showAlert(Alert.AlertType.INFORMATION, "User Updated",
            "User details updated successfully.");
        loadUsers(); // Refresh the table
      } else {
        showAlert(Alert.AlertType.ERROR, "Update Error",
            "Failed to update user details.");
      }
    });
  }

  /**
   * Handles the change password button click.
   *
   * @param event The action event.
   */
  @FXML
  private void handleChangePassword(ActionEvent event) {
    User selectedUser = userTable.getSelectionModel().getSelectedItem();
    if (selectedUser == null) {
      return;
    }

    // Create a dialog for changing password
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Change Password");
    dialog.setHeaderText("Change password for: " + selectedUser.getUsername());

    // Set the button types
    ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

    // Create the password fields
    PasswordField passwordField = new PasswordField();
    PasswordField confirmPasswordField = new PasswordField();

    // Create the layout
    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

    grid.add(new Label("New Password:"), 0, 0);
    grid.add(passwordField, 1, 0);
    grid.add(new Label("Confirm Password:"), 0, 1);
    grid.add(confirmPasswordField, 1, 1);

    dialog.getDialogPane().setContent(grid);

    // Request focus on the password field by default
    passwordField.requestFocus();

    // Disable the save button if passwords don't match
    Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
    saveButton.setDisable(true);

    // Add listeners to enable/disable save button based on password match
    passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
      saveButton.setDisable(newValue.isEmpty() || !newValue.equals(confirmPasswordField.getText()));
    });

    confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
      saveButton.setDisable(newValue.isEmpty() || !newValue.equals(passwordField.getText()));
    });

    // Convert the result to a password when the save button is clicked
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == saveButtonType) {
        return passwordField.getText();
      }
      return null;
    });

    Optional<String> result = dialog.showAndWait();

    result.ifPresent(password -> {
      // Update password in database
      if (DatabaseManager.getInstance().updatePassword(selectedUser.getId(), password)) {
        showAlert(Alert.AlertType.INFORMATION, "Password Updated",
            "Password updated successfully.");
      } else {
        showAlert(Alert.AlertType.ERROR, "Update Error",
            "Failed to update password.");
      }
    });
  }

  /**
   * Handles the toggle active button click.
   *
   * @param event The action event.
   */
  @FXML
  private void handleToggleActive(ActionEvent event) {
    User selectedUser = userTable.getSelectionModel().getSelectedItem();
    if (selectedUser == null) {
      return;
    }

    // Cannot deactivate current user
    if (UserSession.getInstance().getCurrentUser().getId() == selectedUser.getId()) {
      showAlert(Alert.AlertType.WARNING, "Action Not Allowed",
          "You cannot deactivate your own account.");
      return;
    }

    // Toggle active status
    selectedUser.setActive(!selectedUser.isActive());

    // Update user in database
    if (DatabaseManager.getInstance().updateUser(selectedUser)) {
      String status = selectedUser.isActive() ? "activated" : "deactivated";
      showAlert(Alert.AlertType.INFORMATION, "User Status Updated",
          "User account has been " + status + " successfully.");
      loadUsers(); // Refresh the table
    } else {
      showAlert(Alert.AlertType.ERROR, "Update Error",
          "Failed to update user status.");
    }
  }

  /**
   * Handles the delete user button click.
   *
   * @param event The action event.
   */
  @FXML
  private void handleDeleteUser(ActionEvent event) {
    User selectedUser = userTable.getSelectionModel().getSelectedItem();
    if (selectedUser == null) {
      return;
    }

    // Cannot delete current user
    if (UserSession.getInstance().getCurrentUser().getId() == selectedUser.getId()) {
      showAlert(Alert.AlertType.WARNING, "Action Not Allowed",
          "You cannot delete your own account.");
      return;
    }

    // Confirm deletion
    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmAlert.setTitle("Confirm Deletion");
    confirmAlert.setHeaderText("Are you sure you want to delete this user?");
    confirmAlert.setContentText("Username: " + selectedUser.getUsername() +
        "\nThis action cannot be undone.");

    Optional<ButtonType> result = confirmAlert.showAndWait();

    if (result.isPresent() && result.get() == ButtonType.OK) {
      // Delete user from database
      if (DatabaseManager.getInstance().deleteUser(selectedUser.getId())) {
        showAlert(Alert.AlertType.INFORMATION, "User Deleted",
            "User has been deleted successfully.");
        loadUsers(); // Refresh the table
      } else {
        showAlert(Alert.AlertType.ERROR, "Deletion Error",
            "Failed to delete user.");
      }
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
      Scene mainMenuScene = MainMenuScreen.getScene(stage);
      stage.setScene(mainMenuScene);
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