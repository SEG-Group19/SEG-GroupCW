package com.adauction.group19.controller;

import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.utils.ThemeManager;
import com.adauction.group19.view.InputDataScreen;
import com.adauction.group19.view.ViewMetricsScreen;
import com.adauction.group19.view.ClickCostHistogramScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
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
