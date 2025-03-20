package com.adauction.group19.controller;

import com.adauction.group19.model.BounceCriteria;
import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.CheckComboBox;
import javafx.stage.Stage;

public class BounceRegistrationController {

    public static BounceRegistrationController instance;
    @FXML public Spinner<Integer> minPagesViewed, minTime;
    @FXML private CheckBox considerPagesViewed, considerTime;

    @FXML private Button saveButton, cancelButton;

    private Stage stage; // The popup stage if you want to close it
    private CampaignData campaignData;
    private BounceCriteria bounceCriteria;
    private MetricsScreenController metricsScreenController;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        instance = this;

        campaignData = CampaignDataStore.getInstance().getCampaignData();
        bounceCriteria = campaignData.getBounceCriteria();

        minPagesViewed.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, bounceCriteria.getMinPagesViewed()));
        minTime.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, bounceCriteria.getMinTimeOnSiteSeconds()));
        considerPagesViewed.setSelected(bounceCriteria.isConsiderPagesViewed());
        considerTime.setSelected(bounceCriteria.isConsiderTimeOnSite());
    }

    @FXML
    public void handleSave() {
        bounceCriteria.setMinPagesViewed((int) minPagesViewed.getValue());
        bounceCriteria.setMinTimeOnSiteSeconds((int) minTime.getValue());
        bounceCriteria.setConsiderPagesViewed(considerPagesViewed.isSelected());
        bounceCriteria.setConsiderTimeOnSite(considerTime.isSelected());

        metricsScreenController.updateGraph();

        // Potentially store these values or pass to main scene
        if (stage != null) {
            stage.close();
        }
    }

    public void handleCancel(ActionEvent actionEvent) {
        if (stage != null) {
            stage.close();
        }
    }

    public void setMetricsScreenController(MetricsScreenController metricsScreenController) {
        this.metricsScreenController = metricsScreenController;
    }
}
