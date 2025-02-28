package com.adauction.group19.controller;

import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.MainMenu;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.chart.CategoryAxis;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;

public class MetricsScreenController {

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private CategoryAxis xAxis;


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
     * The campaign data.
     */
    private CampaignData campaignData;

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

            // Format to 2 decimal places
            lblTotalCost.setText("($" + formatDouble(campaignData.getTotalCost()) + ")");
            lblCTR.setText("(" + formatDouble(campaignData.getCTR()) + "%)");
            lblCPA.setText("($" + formatDouble(campaignData.getCPA()) + ")");
            lblCPC.setText("($" + formatDouble(campaignData.getCPC()) + ")");
            lblCPM.setText("($" + formatDouble(campaignData.getCPM()) + ")");
            lblBounceRate.setText("(" + formatDouble(campaignData.getBounceRate()) + "%)");
        }
    }
    private String formatDouble(double value) {
        return String.format("%.2f", value); // Rounds to 2 decimal places
    }
    /**
     * Restores the default label values for the given series.
     * @param seriesName The name of the series.
     * @param label The label to update.
     */
    private void restoreDefaultLabelValue(String seriesName, Label label) {
        if (campaignData != null) {
            switch (seriesName) {
                case "Impressions":
                    label.setText("(" + campaignData.getTotalImpressions() + ")");
                    break;
                case "Clicks":
                    label.setText("(" + campaignData.getTotalClicks() + ")");
                    break;
                case "Uniques":
                    label.setText("(" + campaignData.getTotalUniques() + ")");
                    break;
                case "Bounces":
                    label.setText("(" + campaignData.getTotalBounces() + ")");
                    break;
                case "Conversions":
                    label.setText("(" + campaignData.getTotalConversions() + ")");
                    break;
                case "Total Cost":
                    label.setText("($" + formatDouble(campaignData.getTotalCost()) + ")");
                    break;
                case "CTR":
                    label.setText("(" + formatDouble(campaignData.getCTR()) + "%)");
                    break;
                case "CPA":
                    label.setText("($" + formatDouble(campaignData.getCPA()) + ")");
                    break;
                case "CPC":
                    label.setText("($" + formatDouble(campaignData.getCPC()) + ")");
                    break;
                case "CPM":
                    label.setText("($" + formatDouble(campaignData.getCPM()) + ")");
                    break;
                case "Bounce Rate":
                    label.setText("(" + formatDouble(campaignData.getBounceRate()) + "%)");
                    break;
                default:
                    label.setText("(0)"); // Fallback case
            }
        }
    }




    /**
     * Initializes event listeners after FXML is loaded.
     */
    @FXML
    public void initialize() {
        // Initialize with default values first
        campaignData = CampaignDataStore.getInstance().getCampaignData();

        if (campaignData == null) {
            System.err.println("Error: campaignData is null! Data may not have been loaded.");
            // Create empty default labels on the chart
            updateGraphAndLabels(selectedDays);
        } else {
            // Load the campaign data into the UI
            loadCampaignData();

            // Update the graph with real data
            updateGraphAndLabels(selectedDays);
        }

        setupCheckboxListeners();
    }




    /**
     * Handles metric selection.
     */
    private int selectedDays = 14; // Default to 1 day

    private void setupCheckboxListeners() {
        chkImpressions.setOnAction(e -> toggleMetric("Impressions", chkImpressions, lblImpressions));
        chkClicks.setOnAction(e -> toggleMetric("Clicks", chkClicks, lblClicks));
        chkUniques.setOnAction(e -> toggleMetric("Uniques", chkUniques, lblUniques));
        chkBounces.setOnAction(e -> toggleMetric("Bounces", chkBounces, lblBounces));
        chkConversions.setOnAction(e -> toggleMetric("Conversions", chkConversions, lblConversions));
        chkTotalCost.setOnAction(e -> toggleMetric("Total Cost", chkTotalCost, lblTotalCost));
        chkCTR.setOnAction(e -> toggleMetric("CTR", chkCTR, lblCTR));
        chkCPA.setOnAction(e -> toggleMetric("CPA", chkCPA, lblCPA));
        chkCPC.setOnAction(e -> toggleMetric("CPC", chkCPC, lblCPC));
        chkCPM.setOnAction(e -> toggleMetric("CPM", chkCPM, lblCPM));
        chkBounceRate.setOnAction(e -> toggleMetric("Bounce Rate", chkBounceRate, lblBounceRate));
    }
    /*
      * Toggles a metric series on or off based on the checkbox state.
     */
    private void toggleMetric(String seriesName, CheckBox checkBox, Label label) {
        if (checkBox.isSelected()) {
            // Add metric series to the chart
            addMetricSeries(seriesName, checkBox, label, selectedDays, getXAxisLabels(selectedDays));
        } else {
            // Remove only the specific series
            lineChart.getData().removeIf(series -> series.getName().equals(seriesName));

            // Restore the 14-day total when unchecked
            restoreDefaultLabelValue(seriesName, label);
        }
    }




    /**
     * Adds a metric series to the line chart.
     * @param seriesName The name of the series (e.g., "Impressions").
     * @param checkBox The checkbox for the series.
     * @param label The label to update with the total value.
     * @param days The number of days to show.
     * @param xLabels The list of X-axis labels.
     */
    private void addMetricSeries(String seriesName, CheckBox checkBox, Label label, int days, List<String> xLabels) {
        if (checkBox.isSelected() && campaignData != null) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(seriesName);
            double total = 0;

            if (seriesName.equals("Uniques") || seriesName.equals("Bounces")) {
                if (days == 1) {
                    // Hourly view: use the daily aggregate for the single day across all hours.
                    LocalDateTime day = parseDateLabel(xLabels.get(0));
                    double dailyValue = seriesName.equals("Uniques")
                        ? campaignData.getUniquesForDate(day)
                        : campaignData.getBouncesForDate(day);
                    for (String xLabel : xLabels) {
                        series.getData().add(new XYChart.Data<>(xLabel, dailyValue));
                    }
                    total = dailyValue;
                } else {
                    // Multi-day view: for each day, get the day's value and also compute the overall period value.
                    if (seriesName.equals("Uniques")) {
                        // Build a union of unique IDs over the period.
                        Set<Object> periodUniqueIds = new HashSet<>();
                        for (String xLabel : xLabels) {
                            LocalDateTime day = parseDateLabel(xLabel);
                            int dayUnique = campaignData.getUniquesForDate(day);
                            series.getData().add(new XYChart.Data<>(xLabel, dayUnique));
                            // Instead of just summing counts, collect IDs from impressions on that day.
                            for (Object[] impression : campaignData.getImpressions()) {
                                LocalDate d = ((LocalDateTime) impression[0]).toLocalDate();
                                if (d.equals(day.toLocalDate())) {
                                    periodUniqueIds.add(impression[1]); // id is stored at index 1
                                }
                            }
                        }
                        total = periodUniqueIds.size();
                    } else {
                        // For Bounces, sum the bounce count over the period.
                        int periodBounces = 0;
                        for (String xLabel : xLabels) {
                            LocalDateTime day = parseDateLabel(xLabel);
                            int dayBounces = campaignData.getBouncesForDate(day);
                            series.getData().add(new XYChart.Data<>(xLabel, dayBounces));
                            periodBounces += dayBounces;
                        }
                        total = periodBounces;
                    }
                }
            } else {
                // For additive metrics, just compute each value and sum them.
                for (String xLabel : xLabels) {
                    double value = getMetricValueForDate(seriesName, xLabel);
                    series.getData().add(new XYChart.Data<>(xLabel, value));
                    total += value;
                }
            }

            lineChart.getData().add(series);

            // Set label text based on metric type.
            if (Arrays.asList("Total Cost", "CPA", "CPC", "CPM").contains(seriesName)) {
                label.setText("($" + String.format("%.2f", total) + ")");
            } else if (Arrays.asList("CTR", "Bounce Rate").contains(seriesName)) {
                label.setText("(" + String.format("%.2f", total) + "%)");
            } else {
                label.setText("(" + (int) total + ")");
            }
        } else {
            label.setText("(0)");
        }
    }


    /**
     * Retrieves the actual value of a metric for a given date/time.
     * @param seriesName The metric to fetch (e.g., "Impressions").
     * @param dateLabel The formatted date/time label.
     * @return The metric value for the given date.
     */
    private double getMetricValueForDate(String seriesName, String dateLabel) {
        if (campaignData == null) {
            return 0.0;
        }

        LocalDateTime date = parseDateLabel(dateLabel);
        boolean isOneDayView = dateLabel.length() > 10; // if label includes time (HH:mm)
        double value = 0.0;

        switch (seriesName) {
            case "Impressions":
                value = isOneDayView ? campaignData.getHourlyImpressions(date) : campaignData.getImpressionsForDate(date);
                break;
            case "Clicks":
                value = isOneDayView ? campaignData.getHourlyClicks(date) : campaignData.getClicksForDate(date);
                break;
            case "Conversions":
                value = isOneDayView ? campaignData.getHourlyConversions(date) : campaignData.getConversionsForDate(date);
                break;
            case "Total Cost":
                value = isOneDayView ? campaignData.getHourlyTotalCost(date) : campaignData.getTotalCostForDate(date);
                break;
            case "CTR":
                value = isOneDayView ? campaignData.getHourlyCTR(date) : campaignData.getCTRForDate(date);
                break;
            case "CPA":
                value = isOneDayView ? campaignData.getHourlyCPA(date) : campaignData.getCPAForDate(date);
                break;
            case "CPC":
                value = isOneDayView ? campaignData.getHourlyCPC(date) : campaignData.getCPCForDate(date);
                break;
            case "CPM":
                value = isOneDayView ? campaignData.getHourlyCPM(date) : campaignData.getCPMForDate(date);
                break;
            case "Bounce Rate":
                value = isOneDayView ? campaignData.getHourlyBounceRate(date) : campaignData.getBounceRateForDate(date);
                break;
            case "Uniques":
                // When not in hourly view, return the unique count for that day.
                value = campaignData.getUniquesForDate(date);
                break;
            case "Bounces":
                value = campaignData.getBouncesForDate(date);
                break;
            default:
                value = 0.0;
        }
        return value;
    }





    /**
     * Parses a date label string into a LocalDateTime object.
     * @param dateLabel The date label to parse.
     * @return The LocalDateTime object.
     */
    private LocalDateTime parseDateLabel(String dateLabel) {
        try {
            LocalDate firstDate = campaignData.getFirstDate(); // Get the earliest date

            if (dateLabel.length() == 10) { // If YYYY-MM-DD
                return LocalDate.parse(dateLabel, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
            } else if (dateLabel.length() == 16) { // If YYYY-MM-DD HH:mm
                return LocalDateTime.parse(dateLabel, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } else if (dateLabel.length() == 5) { // If HH:mm (hourly)
                return LocalTime.parse(dateLabel, DateTimeFormatter.ofPattern("HH:mm")).atDate(firstDate);
            } else {
                System.err.println("Unexpected date format: " + dateLabel);
                return LocalDateTime.now();
            }
        } catch (Exception e) {
            System.err.println("Error parsing date: " + dateLabel + " - " + e.getMessage());
            return LocalDateTime.now();
        }
    }

    private void updateGraphAndLabels(int days) {
        this.selectedDays = days; // Update selected time scale
        List<String> xLabels = getXAxisLabels(days); // Get new X-axis labels

        // Set up X-axis categories
        xAxis.setCategories(FXCollections.observableArrayList(xLabels));
        xAxis.setLabel(days == 1 ? "Time (HH:mm)" : "Date & Time");

        lineChart.setAnimated(false); // Prevent animation lag

        // Preserve only selected checkboxes instead of resetting everything
        updateMetricSeries("Impressions", chkImpressions, lblImpressions, days, xLabels);
        updateMetricSeries("Clicks", chkClicks, lblClicks, days, xLabels);
        updateMetricSeries("Uniques", chkUniques, lblUniques, days, xLabels);
        updateMetricSeries("Bounces", chkBounces, lblBounces, days, xLabels);
        updateMetricSeries("Conversions", chkConversions, lblConversions, days, xLabels);
        updateMetricSeries("Total Cost", chkTotalCost, lblTotalCost, days, xLabels);
        updateMetricSeries("CTR", chkCTR, lblCTR, days, xLabels);
        updateMetricSeries("CPA", chkCPA, lblCPA, days, xLabels);
        updateMetricSeries("CPC", chkCPC, lblCPC, days, xLabels);
        updateMetricSeries("CPM", chkCPM, lblCPM, days, xLabels);
        updateMetricSeries("Bounce Rate", chkBounceRate, lblBounceRate, days, xLabels);
    }

    private void updateMetricSeries(String seriesName, CheckBox checkBox, Label label, int days, List<String> xLabels) {
        if (checkBox.isSelected()) {
            // Remove old data for this series
            lineChart.getData().removeIf(series -> series.getName().equals(seriesName));

            // Re-add the updated data
            addMetricSeries(seriesName, checkBox, label, days, xLabels);
        }
    }


    /**
     * Handles 3 scenarios: campaign data null, no impression dates, and actual impression dates.
     * @param days The number of days selected.
     * @return The list of X-axis labels.
     */
    private List<String> getXAxisLabels(int days) {
        List<String> labels = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        CampaignData campaignData = CampaignDataStore.getInstance().getCampaignData();
        if (campaignData == null || campaignData.getImpressionDates().isEmpty()) {
            System.err.println("Campaign data or impression dates are missing.");
            return labels;
        }

        List<LocalDateTime> impressionDates = campaignData.getImpressionDates();
        LocalDate firstDate = impressionDates.stream()
            .map(LocalDateTime::toLocalDate)
            .min(LocalDate::compareTo)
            .orElse(LocalDate.now());

        if (days == 1) {
            // Generate hourly labels only for the first recorded date
            for (int i = 0; i < 24; i++) {
                labels.add(firstDate + " " + String.format("%02d:00", i)); // YYYY-MM-DD HH:00
            }
        } else {
            // Generate daily labels for multi-day selections
            for (int i = 0; i < days; i++) {
                LocalDate date = firstDate.plusDays(i);
                labels.add(date.toString()); // YYYY-MM-DD
            }
        }
        return labels;
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
