package com.adauction.group19.controller;

import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.MainMenu;
import java.time.LocalDateTime;
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
        // Initialize with default values first
        campaignData = CampaignDataStore.getInstance().getCampaignData();

        if (campaignData == null) {
            System.err.println("Error: campaignData is null! Data may not have been loaded.");
            // Create empty default labels on the chart
            updateGraphAndLabels(selectedDays);
        } else {
            System.out.println("Campaign data loaded successfully.");
            System.out.println("Total Impressions: " + campaignData.getTotalImpressions());
            System.out.println("Total Clicks: " + campaignData.getTotalClicks());

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
            int total = 0;

            for (String xLabel : xLabels) {
                int value = getMetricValueForDate(seriesName, xLabel); // Get real value from data
                series.getData().add(new XYChart.Data<>(xLabel, value));
                total += value;
            }

            lineChart.getData().add(series);

            // Update label with formatted value
            if (Arrays.asList("Total Cost", "CPA", "CPC", "CPM").contains(seriesName)) {
                label.setText("($" + total + ")");
            } else if (Arrays.asList("CTR", "Bounce Rate").contains(seriesName)) {
                label.setText("(" + total + "%)");
            } else {
                label.setText("(" + total + ")");
            }
        }
    }

    /**
     * Retrieves the actual value of a metric for a given date/time.
     * @param seriesName The metric to fetch (e.g., "Impressions").
     * @param dateLabel The formatted date/time label.
     * @return The metric value for the given date.
     */
    private int getMetricValueForDate(String seriesName, String dateLabel) {
        if (campaignData == null) {
            return 0; // No data available
        }

        // Convert dateLabel back to LocalDateTime if needed
        LocalDateTime date = parseDateLabel(dateLabel);

        switch (seriesName) {
            case "Impressions":
                return campaignData.getImpressionsForDate(date);
            case "Clicks":
                return campaignData.getClicksForDate(date);
            case "Uniques":
                return campaignData.getUniquesForDate(date);
            case "Bounces":
                return campaignData.getBouncesForDate(date);
            case "Conversions":
                return campaignData.getConversionsForDate(date);
            case "Total Cost":
                return (int) campaignData.getTotalCostForDate(date);
            case "CTR":
                return (int) campaignData.getCTRForDate(date);
            case "CPA":
                return (int) campaignData.getCPAForDate(date);
            case "CPC":
                return (int) campaignData.getCPCForDate(date);
            case "CPM":
                return (int) campaignData.getCPMForDate(date);
            case "Bounce Rate":
                return (int) campaignData.getBounceRateForDate(date);
            default:
                return 0;
        }
    }

    /**
     * Parses a date label string into a LocalDateTime object.
     * @param dateLabel The date label to parse.
     * @return The LocalDateTime object.
     */
    private LocalDateTime parseDateLabel(String dateLabel) {
        try {
            if (dateLabel.length() == 10) { // If only YYYY-MM-DD, parse as LocalDate and assume start of the day
                return LocalDate.parse(dateLabel, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
            } else if (dateLabel.length() == 16) { // If YYYY-MM-DD HH:mm, parse as LocalDateTime
                return LocalDateTime.parse(dateLabel, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } else {
                System.err.println("Unexpected date format: " + dateLabel);
                return LocalDateTime.now(); // Default to prevent crashes
            }
        } catch (Exception e) {
            System.err.println("Error parsing date: " + dateLabel + " - " + e.getMessage());
            return LocalDateTime.now(); // Default value if parsing fails
        }
    }







    private void updateGraphAndLabels(int days) {
        lineChart.getData().clear();

        List<String> xLabels = getXAxisLabels(days); // Get date/time labels

        // Set up X-axis as a CategoryAxis with custom labels
        xAxis.setCategories(FXCollections.observableArrayList(xLabels));
        xAxis.setLabel(days == 1 ? "Time (HH:mm)" : "Date & Time");

        lineChart.setAnimated(false);

        // Add each metric using the helper function
        addMetricSeries("Impressions", chkImpressions, lblImpressions, days, xLabels);
        addMetricSeries("Clicks", chkClicks, lblClicks, days, xLabels);
        addMetricSeries("Uniques", chkUniques, lblUniques, days, xLabels);
        addMetricSeries("Bounces", chkBounces, lblBounces, days, xLabels);
        addMetricSeries("Conversions", chkConversions, lblConversions, days, xLabels);
        addMetricSeries("Total Cost", chkTotalCost, lblTotalCost, days, xLabels);
        addMetricSeries("CTR", chkCTR, lblCTR, days, xLabels);
        addMetricSeries("CPA", chkCPA, lblCPA, days, xLabels);
        addMetricSeries("CPC", chkCPC, lblCPC, days, xLabels);
        addMetricSeries("CPM", chkCPM, lblCPM, days, xLabels);
        addMetricSeries("Bounce Rate", chkBounceRate, lblBounceRate, days, xLabels);

    }

    /**
     * Handles 3 scenarios: campaign data null, no impression dates, and actual impression dates.
     * @param days The number of days selected.
     * @return The list of X-axis labels.
     */
    private List<String> getXAxisLabels(int days) {
        List<String> labels = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");

        // Get actual impression timestamps from CampaignData
        CampaignData campaignData = CampaignDataStore.getInstance().getCampaignData();

        if (campaignData == null) {
            System.err.println("Campaign data is null in getXAxisLabels. Generating default labels.");

            // Generate default labels based on current date
            Calendar cal = Calendar.getInstance();
            Date today = cal.getTime();

            if (days == 1) {
                // If 1 day selected, show hours (0-23)
                for (int i = 0; i < 24; i++) {
                    labels.add(String.format("%02d:00", i));
                }
            } else if (days == 5 || days == 7) {
                // If 5 or 7 days selected, generate daily labels with time intervals
                for (int i = days - 1; i >= 0; i--) {
                    cal.setTime(today);
                    cal.add(Calendar.DAY_OF_MONTH, -i);
                    String dateStr = dateFormat.format(cal.getTime());

                    // Add 4 time points for each day (00:00, 06:00, 12:00, 18:00)
                    labels.add(dateStr + " 00:00");
                    labels.add(dateStr + " 06:00");
                    labels.add(dateStr + " 12:00");
                    labels.add(dateStr + " 18:00");
                }
            } else {
                // If 14 days selected, just show dates
                for (int i = days - 1; i >= 0; i--) {
                    cal.setTime(today);
                    cal.add(Calendar.DAY_OF_MONTH, -i);
                    labels.add(dateFormat.format(cal.getTime()));
                }
            }

            return labels;
        }

        // If campaign data exists, use actual impression dates
        List<LocalDateTime> impressionDates = campaignData.getImpressionDates();

        if (impressionDates == null || impressionDates.isEmpty()) {
            System.err.println("Impression dates are empty. Generating default labels.");
            // Reuse the same default label generation as above
            Calendar cal = Calendar.getInstance();
            Date today = cal.getTime();

            if (days == 1) {
                for (int i = 0; i < 24; i++) {
                    labels.add(String.format("%02d:00", i));
                }
            } else if (days == 5 || days == 7) {
                for (int i = days - 1; i >= 0; i--) {
                    cal.setTime(today);
                    cal.add(Calendar.DAY_OF_MONTH, -i);
                    String dateStr = dateFormat.format(cal.getTime());
                    labels.add(dateStr + " 00:00");
                    labels.add(dateStr + " 06:00");
                    labels.add(dateStr + " 12:00");
                    labels.add(dateStr + " 18:00");
                }
            } else {
                for (int i = days - 1; i >= 0; i--) {
                    cal.setTime(today);
                    cal.add(Calendar.DAY_OF_MONTH, -i);
                    labels.add(dateFormat.format(cal.getTime()));
                }
            }

            return labels;
        }

        // Extract unique dates from timestamps
        Set<String> uniqueDates = impressionDates.stream()
            .map(date -> date.toLocalDate().toString())
            .collect(Collectors.toCollection(TreeSet::new));

        if (days == 1) {
            // If 1 day selected, show hours (0-23)
            for (int i = 0; i < 24; i++) {
                labels.add(String.format("%02d:00", i));
            }
        } else if (days == 5 || days == 7) {
            // If 5 or 7 days selected, show date + hours (every 6 hours)
            for (String date : uniqueDates.stream().limit(days).collect(Collectors.toList())) {
                labels.add(date + " 00:00");
                labels.add(date + " 06:00");
                labels.add(date + " 12:00");
                labels.add(date + " 18:00");
            }
        } else {
            // If 14 days selected, just show dates
            labels.addAll(uniqueDates.stream().limit(days).collect(Collectors.toList()));
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
