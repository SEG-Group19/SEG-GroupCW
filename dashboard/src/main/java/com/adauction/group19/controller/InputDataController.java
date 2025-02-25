package com.adauction.group19.controller;

import com.adauction.group19.service.FileParserService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class InputDataController {

    @FXML private TextField impressionFilePath;
    @FXML private TextField clickFilePath;
    @FXML private TextField serverFilePath;

    private File impressionFile;
    private File clickFile;
    private File serverFile;

    @FXML
    private void handleImpressionFileSelect() {
        impressionFile = chooseFile();
        if (impressionFile != null) {
            impressionFilePath.setText(impressionFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleClickFileSelect() {
        clickFile = chooseFile();
        if (clickFile != null) {
            clickFilePath.setText(clickFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleServerFileSelect() {
        serverFile = chooseFile();
        if (serverFile != null) {
            serverFilePath.setText(serverFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleFileUpload() {
        if (impressionFile != null && clickFile != null && serverFile != null) {
            FileParserService fileParserService = new FileParserService();
            fileParserService.parseCampaignData(impressionFile, clickFile, serverFile);
            System.out.println("Files uploaded successfully!");
        } else {
            System.out.println("Please select all three files before uploading.");
        }
    }

    private File chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        return fileChooser.showOpenDialog(new Stage());
    }
}
