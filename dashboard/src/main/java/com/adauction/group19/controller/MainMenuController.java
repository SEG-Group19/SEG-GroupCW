package com.adauction.group19.controller;

import com.adauction.group19.utils.ThemeManager;
import com.adauction.group19.view.ExportScreen;
import com.adauction.group19.view.InputDataScreen;
import com.adauction.group19.view.ViewMetricsScreen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainMenuController {

    @FXML
    private Button toggleThemeButton;

    /**
     * The stage for the screen.
     */
    private Stage stage;

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
        if (stage != null) {
            Scene inputDataScene = ViewMetricsScreen.getScene(stage);
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
    public void handleExportButton(ActionEvent actionEvent) {
        if (stage != null) {
            Scene inputDataScene = ExportScreen.getScene(stage);
            stage.setScene(inputDataScene);
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
