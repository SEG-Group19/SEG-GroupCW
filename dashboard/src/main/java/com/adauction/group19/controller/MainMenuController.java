package com.adauction.group19.controller;

import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.InputDataScreen;
import com.adauction.group19.view.MainMenu;
import com.adauction.group19.view.ViewMetricsScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MainMenuController {

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

}
