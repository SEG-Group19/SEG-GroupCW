package com.adauction.group19.controller;

import com.adauction.group19.model.Campaign;
import com.adauction.group19.model.CampaignData;
import com.adauction.group19.model.User;
import com.adauction.group19.service.*;
import com.adauction.group19.utils.SerializationUtil;
import com.adauction.group19.view.MainMenuScreen;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;

/**
 * This class represents the controller for the Input Data screen.
 * Used to handle the file uploads from the input data screen.
 */
public class InputDataController {

    public static InputDataController instance;

    /**
     * The text fields for the file paths.
     */
    @FXML
    private TextField impressionFilePath;
    @FXML
    private TextField clickFilePath;
    @FXML
    private TextField serverFilePath;

    /**
     * The files for the uploaded data.
     */
    private File impressionFile;
    private File clickFile;
    private File serverFile;

    /**
     * The stage for the screen.
     */
    private Stage stage;

    @FXML
    public void initialize() {
        // Store a reference for testing purposes.
        instance = this;
    }

    /**
     * Sets the impression file.
     * @param impressionFile The impression file to set.
     */
    public void setImpressionFile(File impressionFile) {
        this.impressionFile = impressionFile;
    }

    /**
     * Sets the click file.
     * @param clickFile The click file to set.
     */
    public void setClickFile(File clickFile) {
        this.clickFile = clickFile;
    }

    /**
     * Sets the server file.
     * @param serverFile The server file to set.
     */
    public void setServerFile(File serverFile) {
        this.serverFile = serverFile;
    }

    /**
     * Handles the selection of the impression file.
     */
    @FXML
    private void handleImpressionFileSelect() {
        impressionFile = chooseFile();
        if (impressionFile != null) {
            impressionFilePath.setText(impressionFile.getAbsolutePath());
        }
    }

    /**
     * Handles the selection of the click file.
     */
    @FXML
    private void handleClickFileSelect() {
        clickFile = chooseFile();
        if (clickFile != null) {
            clickFilePath.setText(clickFile.getAbsolutePath());
            // Store the click log path in CampaignDataStore
            CampaignDataStore.getInstance().setClickLogPath(clickFile.getAbsolutePath());
        }
    }

    /**
     * Handles the selection of the server file.
     */
    @FXML
    private void handleServerFileSelect() {
        serverFile = chooseFile();
        if (serverFile != null) {
            serverFilePath.setText(serverFile.getAbsolutePath());
        }
    }

    /**
     * Handles the upload of the files.
     */
    @FXML
    public void handleFileUpload() {
        // All 3 files must be present for upload
        if (impressionFile != null && clickFile != null && serverFile != null) {
            FileParserService fileParserService = new FileParserService();
            CampaignData campaignData;
            try {
                campaignData = fileParserService.parseCampaignData(impressionFile, clickFile, serverFile);
            } catch (Exception e) {
                e.printStackTrace();
                // throw error message to user
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error Parsing Campaign Data");
                alert.setHeaderText("Error Parsing Campaign Data");
                alert.setContentText("Error parsing campaign data: " + e.getMessage());
                alert.showAndWait();
                return;
            }

            CampaignDataStore.getInstance().setCampaignData(campaignData);

            // Create default campaign name
            User user = UserSession.getInstance().getCurrentUser();
            String campaignName = "Campaign " + (CampaignDataManager.getInstance().getTotalCampaignCount() + 1);

            byte[] data;
            try {
                data = SerializationUtil.serialise(campaignData);
            } catch (Exception e) {
                e.printStackTrace();

                // throw error message to user
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error Serializing Campaign Data");
                alert.setHeaderText("Error Serializing Campaign Data");
                alert.setContentText("Error serializing campaign data: " + e.getMessage());
                alert.showAndWait();
                return;
            }

            Campaign campaign = CampaignDataManager.getInstance().addCampaign(user.getId(),campaignName, data);

            System.out.println("Files uploaded successfully!");
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Please Select All Files");
            alert.setHeaderText("Please Select All Files");
            alert.setContentText("Please select all files before uploading.");
            alert.showAndWait();
        }
    }


    /**
     * Opens a file chooser dialog to select a file.
     * @return The selected file.
     */
    private File chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        return fileChooser.showOpenDialog(new Stage());
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
