package com.adauction.group19.controller;

import com.adauction.group19.model.BounceCriteria;
import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataManager;
import com.adauction.group19.service.CampaignDataStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.CheckComboBox;
import javafx.stage.Stage;

public class ChangeNameController {

    public static ChangeNameController instance;
    public static int campaignId;
    @FXML private TextField campaignRename;

    @FXML private Button saveButton, cancelButton;

    private Stage stage; // The popup stage if you want to close it
    private ManageSavedCampaignsController manageSavedCampaignsController;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        instance = this;
    }

    @FXML
    public void handleSave() {
        String newName = campaignRename.getText();

        CampaignDataManager.getInstance().editCampaignName(campaignId, newName);

        manageSavedCampaignsController.refreshList();
        if (stage != null) {
            stage.close();
        }
    }

    public void handleCancel(ActionEvent actionEvent) {
        if (stage != null) {
            stage.close();
        }
    }

    public void setManageSavedCampaignsController (ManageSavedCampaignsController manageSavedCampaignsController) {
        this.manageSavedCampaignsController = manageSavedCampaignsController;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }
}
