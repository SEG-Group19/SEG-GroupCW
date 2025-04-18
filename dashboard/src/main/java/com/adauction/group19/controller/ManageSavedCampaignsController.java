package com.adauction.group19.controller;

import com.adauction.group19.model.Campaign;
import com.adauction.group19.model.CampaignData;
import com.adauction.group19.model.User;
import com.adauction.group19.model.UserRole;
import com.adauction.group19.service.*;
import com.adauction.group19.view.MainMenuScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class represents the controller for the Input Data screen.
 * Used to handle the file uploads from the input data screen.
 */
public class ManageSavedCampaignsController {

    public static ManageSavedCampaignsController instance;
    List<Campaign> campaigns;

    @FXML private VBox campaignListContainer;
    private User user;

    /**
     * The stage for the screen.
     */
    private Stage stage;

    @FXML
    public void initialize() {
        // Store a reference for testing purposes.
        instance = this;

        user = UserSession.getInstance().getCurrentUser();

        User user = UserSession.getInstance().getCurrentUser();
        campaigns = CampaignDataManager.getInstance().getUserCampaigns(user.getId(), user.getRole());

        for (Campaign campaign : campaigns) {
            if (user.getRole() != UserRole.VIEWER)
                campaignListContainer.getChildren().add(createCampaignRow(campaign));
            else
                campaignListContainer.getChildren().add(createViewerCampaignRow(campaign));
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
            Scene mainMenuScene = MainMenuScreen.getScene(stage);
            stage.setScene(mainMenuScene);
        } else {
            System.out.println("Stage is not set.");
        }
    }

    private HBox createCampaignRow(Campaign c) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER);

        Label name = new Label(c.getCampaignName());
        name.getStyleClass().add("label-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnRename = new Button("Change Name");
        btnRename.getStyleClass().add("secondary-button");
        btnRename.setOnAction(e -> onRename(c));

        Button btnDelete = new Button("Delete");
        btnDelete.getStyleClass().add("red-button");
        btnDelete.setOnAction(e -> onDelete(c));

        Button btnLoad = new Button("Load");
        btnLoad.getStyleClass().addAll("primary-button", "load-button");
        btnLoad.setOnAction(e -> onLoad(c));

        HBox buttonBox = new HBox(10, btnRename, btnDelete, btnLoad);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(name, spacer, buttonBox);
        return row;
    }

    private HBox createViewerCampaignRow(Campaign c) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER);

        Label name = new Label(c.getCampaignName());
        name.getStyleClass().add("label-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLoad = new Button("Load");
        btnLoad.getStyleClass().addAll("primary-button", "load-button");
        btnLoad.setOnAction(e -> onLoad(c));

        HBox buttonBox = new HBox(10, btnLoad);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(name, spacer, buttonBox);
        return row;
    }

    private void onRename(Campaign c) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChangeName.fxml"));
            Parent root = loader.load();

            // Create a stage
            Stage popupStage = new Stage();
            popupStage.setTitle("Change Name");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);

            ChangeNameController controller = loader.getController();
            controller.setManageSavedCampaignsController(this);
            controller.setCampaignId(c.getId());
            controller.setStage(popupStage);

            popupStage.showAndWait();  // Wait until user closes
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void refreshList() {
        campaignListContainer.getChildren().clear();
        campaigns = CampaignDataManager.getInstance().getUserCampaigns(user.getId(), user.getRole());

        for (Campaign campaign : campaigns) {
            if (user.getRole() != UserRole.VIEWER)
                campaignListContainer.getChildren().add(createCampaignRow(campaign));
            else
                campaignListContainer.getChildren().add(createViewerCampaignRow(campaign));
        }
    }

    private void onDelete(Campaign c) {
        // delete 'c'
    }

    private void onLoad(Campaign c) {
        // load 'c' into your app
    }
}
