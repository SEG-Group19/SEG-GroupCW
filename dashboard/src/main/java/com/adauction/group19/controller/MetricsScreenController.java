package com.adauction.group19.controller;

import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.MainMenu;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class MetricsScreenController {
    @FXML
    private Button btnBack;

    @FXML
    private Label totalImpressionsLabel, totalClicksLabel, totalConversionsLabel, lblTimeScale;

    @FXML
    private Slider timeSlider;

    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private CheckBox chkImpressions, chkClicks, chkConversions, chkTotalCost, chkCTR, chkCPA, chkCPC, chkCPM, chkBounceRate;

    /**
     * The stage for the screen.
     */
    private Stage stage;

    /**
     * Loads campaign data into the UI.
     */
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
     * Initializes event listeners after FXML is loaded.
     */
    @FXML
    public void initialize() {
        setupSlider();
        setupCheckboxListeners();
    }

    /**
     * Handles slider value change.
     */
    private void setupSlider() {
        timeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblTimeScale.setText("Day: " + newVal.intValue());
            updateGraph(newVal.intValue());
        });
    }

    /**
     * Handles metric selection.
     */
    private void setupCheckboxListeners() {
        chkImpressions.setOnAction(e -> updateGraph((int) timeSlider.getValue()));
        chkClicks.setOnAction(e -> updateGraph((int) timeSlider.getValue()));
        chkConversions.setOnAction(e -> updateGraph((int) timeSlider.getValue()));
    }

    /**
     * Updates the graph when filters change.
     * @param days Number of days selected from the slider.
     */
    private void updateGraph(int days) {
        lineChart.getData().clear();

        if (chkImpressions.isSelected()) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Impressions");
            for (int i = 1; i <= days; i++) {
                series.getData().add(new XYChart.Data<>(i, Math.random() * 1000)); // Placeholder data
            }
            lineChart.getData().add(series);
        }

        if (chkClicks.isSelected()) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Clicks");
            for (int i = 1; i <= days; i++) {
                series.getData().add(new XYChart.Data<>(i, Math.random() * 100)); // Placeholder data
            }
            lineChart.getData().add(series);
        }

        if (chkConversions.isSelected()) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Conversions");
            for (int i = 1; i <= days; i++) {
                series.getData().add(new XYChart.Data<>(i, Math.random() * 10)); // Placeholder data
            }
            lineChart.getData().add(series);
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
