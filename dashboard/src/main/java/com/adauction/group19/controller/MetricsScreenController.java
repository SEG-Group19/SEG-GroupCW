package com.adauction.group19.controller;

import com.adauction.group19.model.CampaignData;
import com.adauction.group19.model.ExportData;
import com.adauction.group19.model.Gender;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.utils.ThemeManager;
import com.adauction.group19.view.MainMenuScreen;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

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

    private Map<String, List<Set<Enum<?>>>> filterMap = new HashMap<>();
    private List<String> metricKeys = List.of(
            "Impressions",
            "Clicks",
            "Uniques",
            "Bounces",
            "Conversions",
            "Cost",
            "CTR",
            "CPA",
            "CPC",
            "CPM",
            "Bounce Rate"
    );

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

        for (String key : metricKeys) {
            List<Set<Enum<?>>> emptyList = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                emptyList.add(new HashSet<>());
            }
            filterMap.put(key, emptyList);
        }

        // Set initial date range to all available data
        initializeDateRange();

        // Load the campaign data into the UI
        loadCampaignData();

        // Setup metric toggle listeners
        setupCheckboxListeners();

        // Default time interval 1 day
        setTimeInterval("1 day");

        // Update the graph with the initial data
        updateGraph();
    }

    public void setFilterMap(Map<String, List<Set<Enum<?>>>> filterMap) {
        this.filterMap = filterMap;
        updateGraph();
    }

    public Map<String, List<Set<Enum<?>>>> getFilterMap() {
        return filterMap;
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

    public void setDates(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

        updateGraph();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
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
        lblImpressions.setText("(" + campaignData.getTotalImpressions(filterMap.get("Impressions")) + ")");
        lblClicks.setText("(" + campaignData.getTotalClicks(filterMap.get("Clicks")) + ")");
        lblUniques.setText("(" + campaignData.getTotalUniques(filterMap.get("Uniques")) + ")");
        lblBounces.setText("(" + campaignData.getTotalBounces(filterMap.get("Bounces")) + ")");
        lblConversions.setText("(" + campaignData.getTotalConversions(filterMap.get("Conversions")) + ")");

        // Format to 2 decimal places for financial metrics
        lblTotalCost.setText("($" + formatDouble(campaignData.getTotalCost(filterMap.get("Cost"))) + ")");
        lblCTR.setText("(" + formatDouble(campaignData.getCTR(filterMap.get("CTR"))) + "%)");
        lblCPA.setText("($" + formatDouble(campaignData.getCPA(filterMap.get("CPA"))) + ")");
        lblCPC.setText("($" + formatDouble(campaignData.getCPC(filterMap.get("CPC"))) + ")");
        lblCPM.setText("($" + formatDouble(campaignData.getCPM(filterMap.get("CPM"))) + ")");
        lblBounceRate.setText("(" + formatDouble(campaignData.getBounceRate(filterMap.get("Bounce Rate"))) + "%)");
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
                label.setText("(" + campaignData.getTotalImpressions(filterMap.get("Impressions")) + ")");
                break;
            case "Clicks":
                label.setText("(" + campaignData.getTotalClicks(filterMap.get("Clicks")) + ")");
                break;
            case "Uniques":
                label.setText("(" + campaignData.getTotalUniques(filterMap.get("Uniques")) + ")");
                break;
            case "Bounces":
                label.setText("(" + campaignData.getTotalBounces(filterMap.get("Bounces")) + ")");
                break;
            case "Conversions":
                label.setText("(" + campaignData.getTotalConversions(filterMap.get("Conversions")) + ")");
                break;
            case "Total Cost":
                label.setText("($" + formatDouble(campaignData.getTotalCost(filterMap.get("Cost"))) + ")");
                break;
            case "CTR":
                label.setText("(" + formatDouble(campaignData.getCTR(filterMap.get("CTR"))) + "%)");
                break;
            case "CPA":
                label.setText("($" + formatDouble(campaignData.getCPA(filterMap.get("CPA"))) + ")");
                break;
            case "CPC":
                label.setText("($" + formatDouble(campaignData.getCPC(filterMap.get("CPC"))) + ")");
                break;
            case "CPM":
                label.setText("($" + formatDouble(campaignData.getCPM(filterMap.get("CPM"))) + ")");
                break;
            case "Bounce Rate":
                label.setText("(" + formatDouble(campaignData.getBounceRate(filterMap.get("Bounce Rate"))) + "%)");
                break;
            default:
                label.setText("(0)");
        }
    }

    /**
     * Updates the graph based on the selected date range and granularity.
     */
    public void updateGraph() {
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
                        ? campaignData.getUniquesForDate(dateTime, filterMap.get("Uniques"))
                        : campaignData.getBouncesForDate(dateTime, filterMap.get("Bounce Rate"));
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
                        int uniquesValue = campaignData.getUniquesForDate(dateTime, filterMap.get("Uniques"));
                        series.getData().add(new XYChart.Data<>(xLabel, uniquesValue));

                        // Collect unique IDs for the period total
                        for (Object[] impression : campaignData.getImpressions(filterMap.get("Impressions"))) {
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
                        int bounceValue = campaignData.getBouncesForDate(dateTime, filterMap.get("Bounce Rate"));
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
                    ? campaignData.getHourlyImpressions(dateTime, filterMap.get("Impressions"))
                    : campaignData.getImpressionsForDate(dateTime, filterMap.get("Impressions"));
            case "Clicks":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyClicks(dateTime, filterMap.get("Clicks"))
                    : campaignData.getClicksForDate(dateTime, filterMap.get("Clicks"));
            case "Conversions":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyConversions(dateTime, filterMap.get("Conversions"))
                    : campaignData.getConversionsForDate(dateTime, filterMap.get("Conversions"));
            case "Total Cost":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyTotalCost(dateTime, filterMap.get("Cost"))
                    : campaignData.getTotalCostForDate(dateTime, filterMap.get("Cost"));
            case "CTR":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyCTR(dateTime, filterMap.get("CTR"))
                    : campaignData.getCTRForDate(dateTime, filterMap.get("CTR"));
            case "CPA":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyCPA(dateTime, filterMap.get("CPA"))
                    : campaignData.getCPAForDate(dateTime, filterMap.get("CPA"));
            case "CPC":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyCPC(dateTime, filterMap.get("CPC"))
                    : campaignData.getCPCForDate(dateTime, filterMap.get("CPC"));
            case "CPM":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyCPM(dateTime, filterMap.get("CPM"))
                    : campaignData.getCPMForDate(dateTime, filterMap.get("CPM"));
            case "Bounce Rate":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyBounceRate(dateTime, filterMap.get("Bounce Rate"))
                    : campaignData.getBounceRateForDate(dateTime, filterMap.get("Bounce Rate"));
            case "Uniques":
                return campaignData.getUniquesForDate(dateTime, filterMap.get("Uniques"));
            case "Bounces":
                return campaignData.getBouncesForDate(dateTime, filterMap.get("Bounce Rate"));
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
     * Gets the export data.
     * @return The export data.
     */
    public ExportData getExportData() {
        List<XYChart.Series<String, Number>> series = lineChart.getData();
        if (series.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No data to export");
            alert.setContentText("Please select a date range and add some data to the graph.");
            alert.showAndWait();
            return null;
        }

        List<String> header = series.stream()
            .map(XYChart.Series::getName)
            .toList();

        Set<String> allDates = series.stream()
            .flatMap(s -> s.getData().stream().map(XYChart.Data::getXValue))
            .collect(Collectors.toCollection(TreeSet::new));
        TreeMap<String, List<Number>> dateToValue = new TreeMap<>();
        for (String date : allDates) {
            dateToValue.put(date, new ArrayList<>());
        }

        for (XYChart.Series<String, Number> s : series) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                dateToValue.get(d.getXValue()).add(d.getYValue());
            }
        }

        return new ExportData(header, dateToValue);
    }

    /**
     * Chooses a file to export the data to.
     * @param format The format to export the data to.
     * @return The file.
     */
    public File chooseFile(String format) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export as " + format);
        fileChooser.setInitialFileName("output");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(format + " file", "*." + format)
        );
        return fileChooser.showSaveDialog(stage);
    }

    /**
     * Chooses a file to export the data to.    
     * @param format The format to export the data to.
     * @param data The text data to export.
     */
    public void exportTextFile(String format, String data) {
        File file = chooseFile(format);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(data);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error exporting file");
                alert.setContentText("Error exporting file: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    /**
     * Get the Image of the LineChart.
     * @return The Image of the LineChart.
     */
    public BufferedImage getLineChartImage() {
        boolean wasDarkMode = ThemeManager.isDarkMode();
        if (wasDarkMode) {
            ThemeManager.toggleTheme(lineChart.getScene());
        }
        WritableImage image = lineChart.snapshot(null, null);
        if (wasDarkMode) {
            ThemeManager.toggleTheme(lineChart.getScene());
        }
        return SwingFXUtils.fromFXImage(image, null);
    }

    /**
     * Handles the export to image button.
     * @param actionEvent The action event.
     */
    @FXML
    public void handleExportImage(ActionEvent actionEvent) {
        File file = chooseFile("png");
        if (file == null) {
            return;
        }
        try {
            BufferedImage image = getLineChartImage();
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error exporting file");
            alert.setContentText("Error exporting file: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public String getPerformanceAnalysisText() {
        StringBuilder analysis = new StringBuilder();
        analysis.append("#Ad Performance Analysis\n\n");
        
        // Hourly analysis
        Map<Integer, Double> hourlyMetrics = getHourlyPerformance();
        Map.Entry<Integer, Double> bestHourEntry = hourlyMetrics.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);
        
        if (bestHourEntry != null && bestHourEntry.getValue() > 0) {
            analysis.append(String.format("• Peak Hour: %02d:00 (%d impressions)\n", 
                bestHourEntry.getKey(), bestHourEntry.getValue().intValue()));
        } else {
            analysis.append("• No hourly data available\n");
        }
        
        // Daily analysis
        Map<DayOfWeek, Double> dailyMetrics = getDailyPerformance();
        Map.Entry<DayOfWeek, Double> bestDayEntry = dailyMetrics.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);
        
        if (bestDayEntry != null && bestDayEntry.getValue() > 0) {
            analysis.append(String.format("• Best Day: %s (%d impressions)\n",
                bestDayEntry.getKey(), bestDayEntry.getValue().intValue()));
        } else {
            analysis.append("• No daily data available\n");
        }
        
        // Total metrics
        if (campaignData != null) {
            analysis.append("\n#Summary Metrics:\n\n");
            analysis.append(String.format("• Total Impressions: %d\n", campaignData.getTotalImpressions(filterMap.get("Impressions"))));
            analysis.append(String.format("• Total Clicks: %d\n", campaignData.getTotalClicks(filterMap.get("Clicks"))));
            analysis.append(String.format("• CTR: %.2f%%\n", campaignData.getCTR(filterMap.get("CTR"))));
        }
        
        return analysis.toString();
    }

    /**
     * Helper to get hourly performance metrics based on actual campaign data
     */
    private Map<Integer, Double> getHourlyPerformance() {
        Map<Integer, Double> hourlyMetrics = new HashMap<>();
        
        // Initialize all hours with 0
        for (int hour = 0; hour < 24; hour++) {
            hourlyMetrics.put(hour, 0.0);
        }
        
        if (campaignData == null || campaignData.getImpressions(filterMap.get("Impressions")).isEmpty()) {
            return hourlyMetrics;
        }

        // Count impressions by hour
        for (Object[] impression : campaignData.getImpressions(filterMap.get("Impressions"))) {
            LocalDateTime impressionTime = (LocalDateTime) impression[0];
            int hour = impressionTime.getHour();
            hourlyMetrics.put(hour, hourlyMetrics.get(hour) + 1);
        }

        return hourlyMetrics;
    }

    /**
     * Helper to get daily performance metrics based on actual campaign data
     */
    private Map<DayOfWeek, Double> getDailyPerformance() {
        Map<DayOfWeek, Double> dailyMetrics = new EnumMap<>(DayOfWeek.class);
        
        // Initialize all days with 0
        for (DayOfWeek day : DayOfWeek.values()) {
            dailyMetrics.put(day, 0.0);
        }
        
        if (campaignData == null || campaignData.getImpressions(filterMap.get("Impressions")).isEmpty()) {
            return dailyMetrics;
        }

        // Count impressions by day of week
        for (Object[] impression : campaignData.getImpressions(filterMap.get("Impressions"))) {
            LocalDateTime impressionTime = (LocalDateTime) impression[0];
            DayOfWeek day = impressionTime.getDayOfWeek();
            dailyMetrics.put(day, dailyMetrics.get(day) + 1);
        }

        return dailyMetrics;
    }

    @FXML
    public void handleExportPDF(ActionEvent actionEvent) {
        File file = chooseFile("pdf");
        if (file == null) {
            return;
        }
    
        try (PDDocument document = new PDDocument()) {
            // Create first page
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            // Get the chart image
            BufferedImage chartImage = getLineChartImage();
            ByteArrayOutputStream chartBaos = new ByteArrayOutputStream();
            ImageIO.write(chartImage, "png", chartBaos);
            byte[] chartImageBytes = chartBaos.toByteArray();
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, chartImageBytes, "chart");
    
            // Calculate dimensions with margins
            float margin = 30; // 30 points (~10mm) margin on all sides
            float pageWidth = PDRectangle.A4.getWidth() - 2 * margin;
            float pageHeight = PDRectangle.A4.getHeight() - 2 * margin;
            
            // Scale image to fit page width while maintaining aspect ratio
            float scale = Math.min(pageWidth / pdImage.getWidth(), (pageHeight * 0.6f) / pdImage.getHeight());
            float scaledWidth = pdImage.getWidth() * scale;
            float scaledHeight = pdImage.getHeight() * scale;
    
            // First content stream for the first page
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            try {
                // Add title
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.newLineAtOffset(margin, PDRectangle.A4.getHeight() - margin - 20);
                contentStream.showText("Campaign Performance Report");
                contentStream.endText();
    
                // Add chart image (centered horizontally)
                float imageX = margin + (pageWidth - scaledWidth) / 2;
                float imageY = PDRectangle.A4.getHeight() - margin - scaledHeight - 30;
                contentStream.drawImage(pdImage, imageX, imageY, scaledWidth, scaledHeight);
    
                // Add the analysis text
                String analysisText = getPerformanceAnalysisText();
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 14);
                contentStream.newLineAtOffset(margin, imageY-20);
                
                // Split text into lines and add to PDF
                for (String line : analysisText.split("\n")) {
                    String text = line.trim();
                    if (text.startsWith("#")) {
                        text = text.substring(1);
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    } else {
                        contentStream.setFont(PDType1Font.HELVETICA, 12);
                    }
                    contentStream.showText(text);
                    contentStream.newLineAtOffset(0, -15); // Move to next line
                }
                contentStream.endText();
    
                // Add footer to last page
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 8);
                contentStream.newLineAtOffset(margin, margin - 10);
                contentStream.showText("Generated on: " + LocalDate.now().format(DateTimeFormatter.ISO_DATE));
                contentStream.endText();
            } finally {
                contentStream.close();
            }
    
            document.save(file);
            System.out.println("PDF generated at: " + file.getAbsolutePath());
            System.out.println("File size: " + file.length() + " bytes");
            System.out.println("Content written:\n" + getPerformanceAnalysisText());
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error exporting file");
            alert.setContentText("Error exporting file: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Handles the export to CSV button.
     * @param actionEvent The action event.
     */
    @FXML
    public void handleExportCSV(ActionEvent actionEvent) {
        ExportData exportData = getExportData();
        if (exportData == null) {
            return;
        }
        String csv = exportData.toCSV();
        exportTextFile("csv", csv);
    }


    /**
     * Handles the export to JSON button.
     * @param actionEvent The action event.
     */
    @FXML
    public void handleExportJSON(ActionEvent actionEvent) {
        ExportData exportData = getExportData();
        if (exportData == null) {
            return;
        }
        String json = exportData.toJSON();
        exportTextFile("json", json);
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
        } else if (sourceButton == btnLastWeek) {
            // Last week - up to 7 days back from the last date
            startDate = lastDate.minusDays(6);
            endDate = lastDate;
            currentGranularity = TimeGranularity.DAILY;
        } else if (sourceButton == btnLastMonth) {
            // Last month - up to 30 days back from the last date
            startDate = lastDate.minusDays(29);
            endDate = lastDate;
            currentGranularity = TimeGranularity.DAILY;
        } else if (sourceButton == btnAllData) {
            // All data - use entire date range
            startDate = firstDate;
            endDate = lastDate;
            currentGranularity = TimeGranularity.DAILY;
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
                currentGranularity = TimeGranularity.HOURLY;
                break;
            case "1 day":
                currentGranularity = TimeGranularity.DAILY;
                break;
            case "1 week":
                currentGranularity = TimeGranularity.WEEKLY;
                break;
            default:
                System.err.println("Unknown time interval: " + timeInterval);
                return;
        }

        // Update the graph with the new time interval
        updateGraph();
    }
}
