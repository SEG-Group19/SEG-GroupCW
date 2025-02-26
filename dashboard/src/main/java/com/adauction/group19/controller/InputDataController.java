package com.adauction.group19.controller;

import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.service.FileParserService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

/**
 * This class represents the controller for the Input Data screen.
 * Used to handle the file uploads from the input data screen.
 */
public class InputDataController {

    /**
     * The text fields for the file paths.
     */
    @FXML private TextField impressionFilePath;
    @FXML private TextField clickFilePath;
    @FXML private TextField serverFilePath;

    /**
     * The files for the uploaded data.
     */
    private File impressionFile;
    private File clickFile;
    private File serverFile;

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
    private void handleFileUpload() {
        // All 3 files must be present for upload
        if (impressionFile != null && clickFile != null && serverFile != null) {
            FileParserService fileParserService = new FileParserService();
            CampaignData campaignData = fileParserService.parseCampaignData(impressionFile, clickFile, serverFile);

            CampaignDataStore.getInstance().setCampaignData(campaignData);

            System.out.println("Files uploaded successfully!");
        } else {
            System.out.println("Please select all three files before uploading.");
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
}
