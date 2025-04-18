package com.adauction.group19.controller;

import com.adauction.group19.model.BounceCriteria;
import com.adauction.group19.model.CampaignData;
import com.adauction.group19.model.User;
import com.adauction.group19.model.UserRole;
import com.adauction.group19.service.CampaignDataManager;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.service.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.CheckComboBox;
import javafx.stage.Stage;

import java.util.List;

public class AssignViewerController {

    public static AssignViewerController instance;
    public static int campaignId;
    @FXML private CheckComboBox userCheckCombo;

    @FXML private Button saveButton, cancelButton;

    private Stage stage; // The popup stage if you want to close it
    private ManageSavedCampaignsController manageSavedCampaignsController;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        instance = this;

        // Get the list of users from the CampaignDataManager
        List<User> users = DatabaseManager.getInstance().getAllUsers();

        // Create a CheckComboBox for the users
        for (User user : users) {
            if (user.getRole() == UserRole.VIEWER) {
                userCheckCombo.getItems().add(user.getUsername());
            }
        }
    }

    @FXML
    public void handleSave() {
        List<String> checkedUsers = userCheckCombo.getCheckModel().getCheckedItems();

        for (String checkedUser : checkedUsers) {
            // Get the user ID from the username
            User user = DatabaseManager.getInstance().getUserByUsername(checkedUser);
            if (user != null) {
                CampaignDataManager.getInstance().assignCampaignToViewer(campaignId, user.getId());
            }
        }

        // remove the rest
        List<User> assignedUsers = CampaignDataManager.getInstance().getAllViewersAssignedToCampaign(campaignId);
        for (User user : assignedUsers) {
            if (!checkedUsers.contains(user.getUsername())) {
                CampaignDataManager.getInstance().removeCampaignViewerAssignment(campaignId, user.getId());
            }
        }

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

        // Set the selected items to the current campaign's viewers
        List<User> assignedUsers = CampaignDataManager.getInstance().getAllViewersAssignedToCampaign(campaignId);
        for (User user : assignedUsers) {
            userCheckCombo.getCheckModel().check(user.getUsername());
        }
    }
}
