package com.adauction.group19.controller;

import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.MainMenu;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class MetricsScreenController {

    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private NumberAxis xAxis;


    @FXML
    private CheckBox chkImpressions,chkUniques, chkBounces, chkClicks, chkConversions, chkTotalCost, chkCTR, chkCPA, chkCPC, chkCPM, chkBounceRate;

    @FXML
    private Label lblImpressions, lblClicks, lblUniques, lblBounces, lblConversions, lblTotalCost,
        lblCTR, lblCPA, lblCPC, lblCPM, lblBounceRate;

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
            lblImpressions.setText("(" + campaignData.getTotalImpressions() + ")");
            lblClicks.setText("(" + campaignData.getTotalClicks() + ")");
            lblUniques.setText("(" + campaignData.getTotalUniques() + ")");
            lblBounces.setText("(" + campaignData.getTotalBounces() + ")");
            lblConversions.setText("(" + campaignData.getTotalConversions() + ")");
            lblTotalCost.setText("($" + campaignData.getTotalCost() + ")"); // Example: formatted as currency
            lblCTR.setText("(" + campaignData.getCTR() + "%)");
            lblCPA.setText("($" + campaignData.getCPA() + ")");
            lblCPC.setText("($" + campaignData.getCPC() + ")");
            lblCPM.setText("($" + campaignData.getCPM() + ")");
            lblBounceRate.setText("(" + campaignData.getBounceRate() + "%)");
        }
    }

    /**
     * Initializes event listeners after FXML is loaded.
     */
    @FXML
    public void initialize() {
        setupCheckboxListeners();
    }


    /**
     * Handles metric selection.
     */
    private int selectedDays = 14; // Default to 1 day

    private void setupCheckboxListeners() {
        chkImpressions.setOnAction(e -> updateGraphAndLabels(selectedDays));
        chkClicks.setOnAction(e -> updateGraphAndLabels(selectedDays));
        chkUniques.setOnAction(e -> updateGraphAndLabels(selectedDays));
        chkBounces.setOnAction(e -> updateGraphAndLabels(selectedDays));
        chkConversions.setOnAction(e -> updateGraphAndLabels(selectedDays));
        chkTotalCost.setOnAction(e -> updateGraphAndLabels(selectedDays));
        chkCTR.setOnAction(e -> updateGraphAndLabels(selectedDays));
        chkCPA.setOnAction(e -> updateGraphAndLabels(selectedDays));
        chkCPC.setOnAction(e -> updateGraphAndLabels(selectedDays));
        chkCPM.setOnAction(e -> updateGraphAndLabels(selectedDays));
        chkBounceRate.setOnAction(e -> updateGraphAndLabels(selectedDays));
    }

    /**
     * Adds a series to the graph if the metric is selected.
     * @param seriesName The name of the metric (e.g., "Impressions").
     * @param checkBox The checkbox associated with this metric.
     * @param label The label to update with the total.
     * @param maxRandomValue The maximum random value for placeholder data.
     * @param days The number of days selected.
     */
    private void addMetricSeries(String seriesName, CheckBox checkBox, Label label, int maxRandomValue, int days) {
        if (checkBox.isSelected()) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(seriesName);
            int total = 0;

            for (int i = 0; i < days * (days == 1 ? 24 : 1); i++) { // 24 points if 1 day, else normal
                int value = (int) (Math.random() * maxRandomValue);
                series.getData().add(new XYChart.Data<>(i, value));
                total += value;
            }

            lineChart.getData().add(series);

            // Update label with formatted value
            if (seriesName.equals("Total Cost") || seriesName.equals("CPA") || seriesName.equals("CPC") || seriesName.equals("CPM")) {
                label.setText("($" + total + ")");
            } else if (seriesName.equals("CTR") || seriesName.equals("Bounce Rate")) {
                label.setText("(" + total + "%)");
            } else {
                label.setText("(" + total + ")");
            }
        }
    }

    private void updateGraphAndLabels(int days) {
        lineChart.getData().clear();
        xAxis.setLabel(days == 1 ? "Time (HH:mm)" : "Days"); // Adjust X-axis label

        if (days == 1) {
            xAxis.setAutoRanging(false);  // Disable automatic scaling
            xAxis.setLowerBound(0);
            xAxis.setUpperBound(23);
            xAxis.setTickUnit(1); // Show each hour
        } else {
            xAxis.setAutoRanging(true);  // Let JavaFX adjust the axis for multiple days
        }

        // Add each metric using the helper function
        addMetricSeries("Impressions", chkImpressions, lblImpressions, 1000, days);
        addMetricSeries("Clicks", chkClicks, lblClicks, 100, days);
        addMetricSeries("Uniques", chkUniques, lblUniques, 100, days);
        addMetricSeries("Bounces", chkBounces, lblBounces, 100, days);
        addMetricSeries("Conversions", chkConversions, lblConversions, 10, days);
        addMetricSeries("Total Cost", chkTotalCost, lblTotalCost, 1000, days);
        addMetricSeries("CTR", chkCTR, lblCTR, 10, days);
        addMetricSeries("CPA", chkCPA, lblCPA, 100, days);
        addMetricSeries("CPC", chkCPC, lblCPC, 10, days);
        addMetricSeries("CPM", chkCPM, lblCPM, 10, days);
        addMetricSeries("Bounce Rate", chkBounceRate, lblBounceRate, 10, days);
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

    @FXML
    private Button btn1Day, btn5Days, btn1Week, btn2Weeks;

    /**
     * Handles time scale selection when a button is clicked.
     */
    @FXML
    private void handleTimeScale(ActionEvent event) {
        if (event.getSource() == btn1Day) {
            selectedDays = 1;
        } else if (event.getSource() == btn5Days) {
            selectedDays = 5;
        } else if (event.getSource() == btn1Week) {
            selectedDays = 7;
        } else if (event.getSource() == btn2Weeks) {
            selectedDays = 14;
        }

        updateGraphAndLabels(selectedDays);
    }


}
