package com.adauction.group19.controller;

import com.adauction.group19.model.CampaignData;
import com.adauction.group19.model.User;
import com.adauction.group19.service.*;
import com.adauction.group19.view.MainMenuScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * This class represents the controller for the Input Data screen.
 * Used to handle the file uploads from the input data screen.
 */
public class ManageSavedCampaignsController {

    public static ManageSavedCampaignsController instance;

    /**
     * The stage for the screen.
     */
    private Stage stage;

    @FXML
    public void initialize() {
        // Store a reference for testing purposes.
        instance = this;

        User user = UserSession.getInstance().getCurrentUser();
        System.out.println(CampaignDataManager.getInstance().getUserCampaigns(user.getId(), user.getRole()));
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
            Scene mainMenuScene = MainMenuScreen.getScene(stage);
            stage.setScene(mainMenuScene);
        } else {
            System.out.println("Stage is not set.");
        }
    }
}
