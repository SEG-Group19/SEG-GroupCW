package com.adauction.group19.controller;

import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.MainMenu;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MetricsScreenController {
    @FXML
    private Label totalImpressionsLabel;

    @FXML
    private Label totalClicksLabel;

    @FXML
    private Label totalConversionsLabel;

    /**
     * The stage for the screen.
     */
    private Stage stage;

    @FXML
    public void loadCampaignData() {
        CampaignData campaignData = CampaignDataStore.getInstance().getCampaignData();

        if (campaignData != null) {
            totalImpressionsLabel.setText(String.valueOf(campaignData.getTotalImpressions()));
            totalClicksLabel.setText(String.valueOf(campaignData.getTotalClicks()));
            totalConversionsLabel.setText(String.valueOf(campaignData.getTotalConversions()));
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
     * Handles the go back button.
     * @param actionEvent The action event.
     */
    public void handleBackButton(ActionEvent actionEvent) {
        if (stage != null) {
            Scene mainMenuScene = MainMenu.getScene(stage);
            stage.setScene(mainMenuScene);
        } else {
            System.out.println("Stage is not set.");
        }
    }
}
