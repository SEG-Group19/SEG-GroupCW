package com.adauction.group19.controller;

import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.service.UserSession;
import com.adauction.group19.utils.ThemeManager;
import com.adauction.group19.view.InputDataScreen;
import com.adauction.group19.view.LoginScreen;
import com.adauction.group19.view.UserManagementScreen;
import com.adauction.group19.view.ViewMetricsScreen;
import com.adauction.group19.view.ClickCostHistogramScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainMenuController {

    @FXML private Button toggleThemeButton;
    @FXML private Button userManagementButton;
    @FXML private Button logoutButton;

    /**
     * The stage for the screen.
     */
    private Stage stage;

    /**
     * Initialize the controller.
     */
    @FXML
    public void initialize() {
        // Show/hide user management button based on admin status
        if (userManagementButton != null) {
            userManagementButton.setVisible(UserSession.getInstance().isAdmin());
            userManagementButton.setManaged(UserSession.getInstance().isAdmin());
        }
    }

    /**
     * Sets the stage for the screen.
     * @param stage The stage to set.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles the Input Data button. Switches scene to the Input Data screen.
     * @param actionEvent The action event.
     */
    @FXML
    public void handleInputDataButton(ActionEvent actionEvent) {
        if (stage != null) {
            Scene inputDataScene = InputDataScreen.getScene(stage);
            stage.setScene(inputDataScene);
        } else {
            System.out.println("Stage is not set.");
        }
    }

    /**
     * Handles the View Metrics button. Switches scene to the View Metrics screen.
     * @param actionEvent The action event.
     */
    @FXML
    public void handleViewMetricsButton(ActionEvent actionEvent) {
        if (CampaignDataStore.getInstance().getCampaignData() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campaign Data Not Found");
            alert.setHeaderText("Campaign Data Not Found");
            alert.setContentText("Please input data before viewing metrics.");
            alert.showAndWait();
            return;
        }

        if (stage != null) {
            Scene inputDataScene = ViewMetricsScreen.getScene(stage);
            stage.setScene(inputDataScene);
        } else {
            System.out.println("Stage is not set.");
        }
    }

    /**
     * Handles the Click Cost button. Switches scene to the Click Cost Histogram screen.
     * @param actionEvent The action event.
     */
    @FXML
    public void handleClickCostButton(ActionEvent actionEvent) {
        if (CampaignDataStore.getInstance().getCampaignData() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campaign Data Not Found");
            alert.setHeaderText("Campaign Data Not Found");
            alert.setContentText("Please input data before viewing click cost distribution.");
            alert.showAndWait();
            return;
        }

        if (stage != null) {
            // Get the file path from the campaign data store
            String filePath = CampaignDataStore.getInstance().getClickLogPath();
            Scene clickCostScene = ClickCostHistogramScreen.getScene(stage, filePath);
            stage.setScene(clickCostScene);
        } else {
            System.out.println("Stage is not set.");
        }
    }

    /**
     * Handles the User Management button. Switches scene to the User Management screen.
     * @param actionEvent The action event.
     */
    @FXML
    public void handleUserManagementButton(ActionEvent actionEvent) {
        if (!UserSession.getInstance().isAdmin()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Access Denied");
            alert.setHeaderText("Admin Access Required");
            alert.setContentText("You must be an administrator to access user management.");
            alert.showAndWait();
            return;
        }

        if (stage != null) {
            Scene userManagementScene = UserManagementScreen.getScene(stage);
            stage.setScene(userManagementScene);
        } else {
            System.out.println("Stage is not set.");
        }
    }

    /**
     * Handles the Logout button. Logs out the current user and returns to the login screen.
     * @param actionEvent The action event.
     */
    @FXML
    public void handleLogoutButton(ActionEvent actionEvent) {
        // Clear the current user session
        UserSession.getInstance().logout();

        if (stage != null) {
            Scene loginScene = LoginScreen.getScene(stage);
            stage.setScene(loginScene);
        } else {
            System.out.println("Stage is not set.");
        }
    }

    /**
     * Handles the toggle theme button. Toggles the theme of the scene.
     * @param actionEvent The action event.
     */
    @FXML
    private void handleToggleThemeButton(ActionEvent actionEvent) {
        Scene scene = toggleThemeButton.getScene();
        ThemeManager.toggleTheme(scene);

        // Toggle the text of the button
        toggleThemeButton.setText(ThemeManager.isDarkMode() ? "☀" : "🌙");
    }
}