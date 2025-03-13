package com.adauction.group19.controller;

import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.MainMenuScreen;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

/**
 * This class represents the controller for the Metrics screen.
 * Responsible for displaying and updating campaign metrics.
 */
public class MetricsScreenController {

    @FXML public Button graphSettingsBtn;
    // UI components
    @FXML private LineChart<String, Number> lineChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private ToggleGroup timeGranularity;
    @FXML private RadioButton rbHourly, rbDaily, rbWeekly;
    @FXML private Button btnLastDay, btnLastWeek, btnLastMonth, btnAllData;

    // Metrics checkboxes
    @FXML private CheckBox chkImpressions, chkUniques, chkBounces, chkClicks, chkConversions;
    @FXML private CheckBox chkTotalCost, chkCTR, chkCPA, chkCPC, chkCPM, chkBounceRate;

    // Metrics labels
    @FXML private Label lblImpressions, lblClicks, lblUniques, lblBounces, lblConversions;
    @FXML private Label lblTotalCost, lblCTR, lblCPA, lblCPC, lblCPM, lblBounceRate;

    // Class variables
    private Stage stage;
    private CampaignData campaignData;
    private LocalDate startDate;
    private LocalDate endDate;
    private TimeGranularity currentGranularity = TimeGranularity.HOURLY;

    // Time granularity enum
    private enum TimeGranularity {
        HOURLY, DAILY, WEEKLY
    }

    /**
     * Initializes the controller after FXML is loaded.
     */
    @FXML
    public void initialize() {
        // Initialize date pickers
        // setupDatePickers();

        // Load campaign data
        campaignData = CampaignDataStore.getInstance().getCampaignData();

        if (campaignData == null) {
            System.err.println("Error: campaignData is null! Data may not have been loaded.");
            return;
        }

        // Set initial date range to all available data
        initializeDateRange();

        // Load the campaign data into the UI
        loadCampaignData();

        // Setup metric toggle listeners
        setupCheckboxListeners();

        // Update the graph with the initial data
        updateGraph();
    }

    @FXML
    private void handleGraphSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSettings.fxml"));
            Parent root = loader.load();

            // Create a stage
            Stage popupStage = new Stage();
            popupStage.setTitle("Graph Settings");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);

            GraphSettingsController controller = loader.getController();
            controller.setMetricsScreenController(this);
            controller.setDates(startDate, endDate);
            controller.setStage(popupStage);

            popupStage.showAndWait();  // Wait until user closes
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBounceRegistration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BounceRegistration.fxml"));
            Parent root = loader.load();

            // Create a stage
            Stage popupStage = new Stage();
            popupStage.setTitle("Define bounce registration");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);

            BounceRegistrationController controller = loader.getController();
            controller.setMetricsScreenController(this);
            controller.setStage(popupStage);

            popupStage.showAndWait();  // Wait until user closes
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the date range based on available campaign data.
     */
    private void initializeDateRange() {
        if (campaignData == null || campaignData.getImpressionDates().isEmpty()) {
            return;
        }

        // Find the earliest and latest dates in the campaign data
        List<LocalDateTime> impressionDates = campaignData.getImpressionDates();

        startDate = impressionDates.stream()
            .map(LocalDateTime::toLocalDate)
            .min(LocalDate::compareTo)
            .orElse(LocalDate.now());

        endDate = impressionDates.stream()
            .map(LocalDateTime::toLocalDate)
            .max(LocalDate::compareTo)
            .orElse(LocalDate.now());
    }

    protected void setDates(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

        updateGraph();
    }

    protected LocalDate getStartDate() {
        return startDate;
    }

    protected LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Loads campaign data into the UI.
     */
    @FXML
    public void loadCampaignData() {
        if (campaignData == null) {
            return;
        }

        // Set summary labels for totals
        lblImpressions.setText("(" + campaignData.getTotalImpressions() + ")");
        lblClicks.setText("(" + campaignData.getTotalClicks() + ")");
        lblUniques.setText("(" + campaignData.getTotalUniques() + ")");
        lblBounces.setText("(" + campaignData.getTotalBounces() + ")");
        lblConversions.setText("(" + campaignData.getTotalConversions() + ")");

        // Format to 2 decimal places for financial metrics
        lblTotalCost.setText("($" + formatDouble(campaignData.getTotalCost()) + ")");
        lblCTR.setText("(" + formatDouble(campaignData.getCTR()) + "%)");
        lblCPA.setText("($" + formatDouble(campaignData.getCPA()) + ")");
        lblCPC.setText("($" + formatDouble(campaignData.getCPC()) + ")");
        lblCPM.setText("($" + formatDouble(campaignData.getCPM()) + ")");
        lblBounceRate.setText("(" + formatDouble(campaignData.getBounceRate()) + "%)");
    }

    /**
     * Formats a double value to 2 decimal places.
     * @param value The value to format.
     * @return The formatted value as a string.
     */
    private String formatDouble(double value) {
        return String.format("%.2f", value);
    }

    /**
     * Sets up listeners for the metric checkboxes.
     */
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

    /**
     * Toggles a metric series on or off based on the checkbox state.
     * @param seriesName The name of the series.
     * @param checkBox The checkbox for the series.
     * @param label The label to update with the total value.
     */
    private void toggleMetric(String seriesName, CheckBox checkBox, Label label) {
        if (checkBox.isSelected()) {
            // Add metric series to the chart
            addMetricSeries(seriesName);
        } else {
            // Remove the specific series
            lineChart.getData().removeIf(series -> series.getName().equals(seriesName));

            // Restore the total label
            restoreDefaultLabelValue(seriesName, label);
        }
    }

    /**
     * Restores the default label values for the given series.
     * @param seriesName The name of the series.
     * @param label The label to update.
     */
    private void restoreDefaultLabelValue(String seriesName, Label label) {
        if (campaignData == null) {
            label.setText("(0)");
            return;
        }

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
                label.setText("(0)");
        }
    }

    /**
     * Updates the graph based on the selected date range and granularity.
     */
    protected void updateGraph() {
        if (campaignData == null) {
            return;
        }

        // Clear existing graph data
        lineChart.getData().clear();

        // Set up X-axis with appropriate labels based on granularity
        List<String> xLabels = getXAxisLabels();
        xAxis.setCategories(FXCollections.observableArrayList(xLabels));

        // Set appropriate x-axis label
        String xAxisLabel;
        switch (currentGranularity) {
            case HOURLY:
                xAxisLabel = "Hour";
                break;
            case WEEKLY:
                xAxisLabel = "Week";
                break;
            case DAILY:
            default:
                xAxisLabel = "Date";
        }
        xAxis.setLabel(xAxisLabel);

        // Update metric series for all selected checkboxes
        updateSelectedMetrics();
    }

    /**
     * Updates all selected metric series on the graph.
     */
    private void updateSelectedMetrics() {
        if (chkImpressions.isSelected()) addMetricSeries("Impressions");
        if (chkClicks.isSelected()) addMetricSeries("Clicks");
        if (chkUniques.isSelected()) addMetricSeries("Uniques");
        if (chkBounces.isSelected()) addMetricSeries("Bounces");
        if (chkConversions.isSelected()) addMetricSeries("Conversions");
        if (chkTotalCost.isSelected()) addMetricSeries("Total Cost");
        if (chkCTR.isSelected()) addMetricSeries("CTR");
        if (chkCPA.isSelected()) addMetricSeries("CPA");
        if (chkCPC.isSelected()) addMetricSeries("CPC");
        if (chkCPM.isSelected()) addMetricSeries("CPM");
        if (chkBounceRate.isSelected()) addMetricSeries("Bounce Rate");
    }

    /**
     * Adds a metric series to the line chart.
     * @param seriesName The name of the series.
     */
    private void addMetricSeries(String seriesName) {
        if (campaignData == null) {
            return;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(seriesName);

        // Get the x-axis labels
        List<String> xLabels = getXAxisLabels();

        // Track period total for the label
        double periodTotal = 0;

        // For metrics that need special handling (unique users, bounce rate)
        if (seriesName.equals("Uniques") || seriesName.equals("Bounces")) {
            if (currentGranularity == TimeGranularity.HOURLY) {
                // For hourly granularity, show daily values across all hours
                for (String xLabel : xLabels) {
                    LocalDateTime dateTime = parseXAxisLabel(xLabel);
                    double value = seriesName.equals("Uniques")
                        ? campaignData.getUniquesForDate(dateTime)
                        : campaignData.getBouncesForDate(dateTime);
                    series.getData().add(new XYChart.Data<>(xLabel, value));
                    periodTotal += value;
                }
            } else {
                // For daily/weekly granularity
                if (seriesName.equals("Uniques")) {
                    // For uniques, track unique IDs over the period
                    Set<Object> periodUniqueIds = new HashSet<>();

                    for (String xLabel : xLabels) {
                        LocalDateTime dateTime = parseXAxisLabel(xLabel);
                        int uniquesValue = campaignData.getUniquesForDate(dateTime);
                        series.getData().add(new XYChart.Data<>(xLabel, uniquesValue));

                        // Collect unique IDs for the period total
                        for (Object[] impression : campaignData.getImpressions()) {
                            LocalDateTime impressionTime = (LocalDateTime) impression[0];

                            if (isInTimeRange(impressionTime, dateTime)) {
                                periodUniqueIds.add(impression[1]); // ID at index 1
                            }
                        }
                    }
                    periodTotal = periodUniqueIds.size();
                } else {
                    // For bounces, sum values
                    for (String xLabel : xLabels) {
                        LocalDateTime dateTime = parseXAxisLabel(xLabel);
                        int bounceValue = campaignData.getBouncesForDate(dateTime);
                        series.getData().add(new XYChart.Data<>(xLabel, bounceValue));
                        periodTotal += bounceValue;
                    }
                }
            }
        } else {
            // For regular metrics, just get the value for each label
            for (String xLabel : xLabels) {
                LocalDateTime dateTime = parseXAxisLabel(xLabel);
                double value = getMetricValueForDateTime(seriesName, dateTime);
                series.getData().add(new XYChart.Data<>(xLabel, value));
                periodTotal += value;
            }
        }

        // Add the series to the chart
        lineChart.getData().add(series);

        // Update the label with the period total
        updateMetricLabel(seriesName, periodTotal);
    }

    /**
     * Checks if an impression time is within the time range of the x-axis label.
     * @param impressionTime The time of the impression.
     * @param labelTime The time represented by the x-axis label.
     * @return True if the impression time is within the time range.
     */
    private boolean isInTimeRange(LocalDateTime impressionTime, LocalDateTime labelTime) {
        switch (currentGranularity) {
            case HOURLY:
                return impressionTime.getHour() == labelTime.getHour() &&
                    impressionTime.toLocalDate().equals(labelTime.toLocalDate());
            case WEEKLY:
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                return impressionTime.get(weekFields.weekOfWeekBasedYear()) ==
                    labelTime.get(weekFields.weekOfWeekBasedYear()) &&
                    impressionTime.getYear() == labelTime.getYear();
            case DAILY:
            default:
                return impressionTime.toLocalDate().equals(labelTime.toLocalDate());
        }
    }

    /**
     * Updates a metric label with the period total.
     * @param seriesName The name of the series.
     * @param total The total value for the period.
     */
    private void updateMetricLabel(String seriesName, double total) {
        Label label = getLabelForSeries(seriesName);
        if (label == null) return;

        if (Arrays.asList("Total Cost", "CPA", "CPC", "CPM").contains(seriesName)) {
            label.setText("($" + formatDouble(total) + ")");
        } else if (Arrays.asList("CTR", "Bounce Rate").contains(seriesName)) {
            label.setText("(" + formatDouble(total) + "%)");
        } else {
            label.setText("(" + (int) total + ")");
        }
    }

    /**
     * Gets the label for a series.
     * @param seriesName The name of the series.
     * @return The label for the series.
     */
    private Label getLabelForSeries(String seriesName) {
        switch (seriesName) {
            case "Impressions": return lblImpressions;
            case "Clicks": return lblClicks;
            case "Uniques": return lblUniques;
            case "Bounces": return lblBounces;
            case "Conversions": return lblConversions;
            case "Total Cost": return lblTotalCost;
            case "CTR": return lblCTR;
            case "CPA": return lblCPA;
            case "CPC": return lblCPC;
            case "CPM": return lblCPM;
            case "Bounce Rate": return lblBounceRate;
            default: return null;
        }
    }

    /**
     * Gets the metric value for a specific date and time.
     * @param seriesName The name of the series.
     * @param dateTime The date and time to get the value for.
     * @return The metric value.
     */
    private double getMetricValueForDateTime(String seriesName, LocalDateTime dateTime) {
        switch (seriesName) {
            case "Impressions":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyImpressions(dateTime)
                    : campaignData.getImpressionsForDate(dateTime);
            case "Clicks":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyClicks(dateTime)
                    : campaignData.getClicksForDate(dateTime);
            case "Conversions":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyConversions(dateTime)
                    : campaignData.getConversionsForDate(dateTime);
            case "Total Cost":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyTotalCost(dateTime)
                    : campaignData.getTotalCostForDate(dateTime);
            case "CTR":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyCTR(dateTime)
                    : campaignData.getCTRForDate(dateTime);
            case "CPA":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyCPA(dateTime)
                    : campaignData.getCPAForDate(dateTime);
            case "CPC":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyCPC(dateTime)
                    : campaignData.getCPCForDate(dateTime);
            case "CPM":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyCPM(dateTime)
                    : campaignData.getCPMForDate(dateTime);
            case "Bounce Rate":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyBounceRate(dateTime)
                    : campaignData.getBounceRateForDate(dateTime);
            case "Uniques":
                return campaignData.getUniquesForDate(dateTime);
            case "Bounces":
                return campaignData.getBouncesForDate(dateTime);
            default:
                return 0.0;
        }
    }

    /**
     * Parses an X-axis label into a LocalDateTime object.
     * @param label The label to parse.
     * @return The LocalDateTime object.
     */
    private LocalDateTime parseXAxisLabel(String label) {
        try {
            switch (currentGranularity) {
                case HOURLY:
                    // Format: YYYY-MM-DD HH:mm
                    return LocalDateTime.parse(label, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                case WEEKLY:
                    // Format: YYYY-WW (Week of year)
                    String[] parts = label.split("-W");
                    int year = Integer.parseInt(parts[0]);
                    int weekOfYear = Integer.parseInt(parts[1]);
                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    return LocalDate.ofYearDay(year, 1)
                        .with(weekFields.weekOfWeekBasedYear(), weekOfYear)
                        .atStartOfDay();
                case DAILY:
                default:
                    // Format: YYYY-MM-DD
                    return LocalDate.parse(label).atStartOfDay();
            }
        } catch (Exception e) {
            System.err.println("Error parsing label: " + label + " - " + e.getMessage());
            return LocalDateTime.now();
        }
    }

    /**
     * Generates X-axis labels based on the current date range and granularity.
     * @return List of X-axis labels.
     */
    private List<String> getXAxisLabels() {
        if (campaignData == null || startDate == null || endDate == null) {
            return Collections.emptyList();
        }

        List<String> labels = new ArrayList<>();
        DateTimeFormatter formatter;

        switch (currentGranularity) {
            case HOURLY:
                // For hourly view, show each hour of a single day
                // If date range spans multiple days, use the start date
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDate dayToShow = startDate;

                for (int hour = 0; hour < 24; hour++) {
                    LocalDateTime hourTime = dayToShow.atTime(hour, 0);
                    labels.add(formatter.format(hourTime));
                }
                break;

            case WEEKLY:
                // For weekly view, show week numbers
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                LocalDate current = startDate;

                while (!current.isAfter(endDate)) {
                    int weekNumber = current.get(weekFields.weekOfWeekBasedYear());
                    int year = current.getYear();
                    labels.add(year + "-W" + weekNumber);

                    // Move to next week
                    current = current.plusWeeks(1);
                }
                break;

            case DAILY:
            default:
                // For daily view, show each date in the range
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                current = startDate;

                while (!current.isAfter(endDate)) {
                    labels.add(formatter.format(current));
                    current = current.plusDays(1);
                }
                break;
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
    @FXML
    public void handleBackButton(ActionEvent actionEvent) {
        if (stage != null) {
            Scene mainMenuScene = MainMenuScreen.getScene(stage);
            stage.setScene(mainMenuScene);
        } else {
            System.out.println("Stage is not set.");
        }
    }

    /**
     * Handles changes in time granularity.
     * @param actionEvent The action event.
     */
    @FXML
    public void handleGranularityChange(ActionEvent actionEvent) {
        if (rbHourly.isSelected()) {
            currentGranularity = TimeGranularity.HOURLY;
        } else if (rbWeekly.isSelected()) {
            currentGranularity = TimeGranularity.WEEKLY;
        } else {
            currentGranularity = TimeGranularity.DAILY;
        }

        updateGraph();
    }

    /**
     * Handles quick date range selection.
     * @param actionEvent The action event.
     */
    @FXML
    public void handleQuickDateRange(ActionEvent actionEvent) {
        if (campaignData == null || campaignData.getImpressionDates().isEmpty()) {
            return;
        }

        // Get campaign date range
        List<LocalDateTime> impressionDates = campaignData.getImpressionDates();
        LocalDate firstDate = impressionDates.stream()
            .map(LocalDateTime::toLocalDate)
            .min(LocalDate::compareTo)
            .orElse(LocalDate.now());

        LocalDate lastDate = impressionDates.stream()
            .map(LocalDateTime::toLocalDate)
            .max(LocalDate::compareTo)
            .orElse(LocalDate.now());

        // Set date range based on button clicked
        Button sourceButton = (Button) actionEvent.getSource();

        if (sourceButton == btnLastDay) {
            // Last 24 hours - use only the last available day
            startDate = lastDate;
            endDate = lastDate;
            currentGranularity = TimeGranularity.HOURLY;
            rbHourly.setSelected(true);
        } else if (sourceButton == btnLastWeek) {
            // Last week - up to 7 days back from the last date
            startDate = lastDate.minusDays(6);
            endDate = lastDate;
            currentGranularity = TimeGranularity.DAILY;
            rbDaily.setSelected(true);
        } else if (sourceButton == btnLastMonth) {
            // Last month - up to 30 days back from the last date
            startDate = lastDate.minusDays(29);
            endDate = lastDate;
            currentGranularity = TimeGranularity.DAILY;
            rbDaily.setSelected(true);
        } else if (sourceButton == btnAllData) {
            // All data - use entire date range
            startDate = firstDate;
            endDate = lastDate;
            currentGranularity = TimeGranularity.DAILY;
            rbDaily.setSelected(true);
        }

        // Update the graph
        updateGraph();
    }

    /**
     * Sets the time interval for the graph and updates it.
     * @param timeInterval The time interval to set (e.g. "1 hour", "4 hours", "1 day", "1 week").
     */
    public void setTimeInterval(String timeInterval) {
        if (timeInterval == null || timeInterval.isEmpty()) {
            return; // Don't change anything if timeInterval is empty
        }

        switch (timeInterval) {
            case "1 hour":
            case "4 hours":
                currentGranularity = TimeGranularity.HOURLY;
                rbHourly.setSelected(true);
                break;
            case "1 day":
                currentGranularity = TimeGranularity.DAILY;
                rbDaily.setSelected(true);
                break;
            case "1 week":
                currentGranularity = TimeGranularity.WEEKLY;
                rbWeekly.setSelected(true);
                break;
            default:
                System.err.println("Unknown time interval: " + timeInterval);
                return;
        }

        // Update the graph with the new time interval
        updateGraph();
    }
}