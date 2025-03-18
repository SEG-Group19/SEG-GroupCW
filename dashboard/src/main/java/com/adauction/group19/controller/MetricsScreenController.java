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

    private List<Set<Enum<?>>> filters = new ArrayList<>();

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

        // Default time interval 1 day
        setTimeInterval("1 day");

        // Update the graph with the initial data
        updateGraph();

        // setup filters
        for (int i = 0; i < 4; i++) {
            filters.add(new HashSet<>());
        }
    }

    protected void setFilters(List<Set<Enum<?>>> filters) {
        this.filters = filters;
        updateGraph();
    }

    protected List<Set<Enum<?>>> getFilters() {
        return filters;
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
        lblImpressions.setText("(" + campaignData.getTotalImpressions(filters) + ")");
        lblClicks.setText("(" + campaignData.getTotalClicks(filters) + ")");
        lblUniques.setText("(" + campaignData.getTotalUniques(filters) + ")");
        lblBounces.setText("(" + campaignData.getTotalBounces(filters) + ")");
        lblConversions.setText("(" + campaignData.getTotalConversions(filters) + ")");

        // Format to 2 decimal places for financial metrics
        lblTotalCost.setText("($" + formatDouble(campaignData.getTotalCost(filters)) + ")");
        lblCTR.setText("(" + formatDouble(campaignData.getCTR(filters)) + "%)");
        lblCPA.setText("($" + formatDouble(campaignData.getCPA(filters)) + ")");
        lblCPC.setText("($" + formatDouble(campaignData.getCPC(filters)) + ")");
        lblCPM.setText("($" + formatDouble(campaignData.getCPM(filters)) + ")");
        lblBounceRate.setText("(" + formatDouble(campaignData.getBounceRate(filters)) + "%)");
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
                label.setText("(" + campaignData.getTotalImpressions(filters) + ")");
                break;
            case "Clicks":
                label.setText("(" + campaignData.getTotalClicks(filters) + ")");
                break;
            case "Uniques":
                label.setText("(" + campaignData.getTotalUniques(filters) + ")");
                break;
            case "Bounces":
                label.setText("(" + campaignData.getTotalBounces(filters) + ")");
                break;
            case "Conversions":
                label.setText("(" + campaignData.getTotalConversions(filters) + ")");
                break;
            case "Total Cost":
                label.setText("($" + formatDouble(campaignData.getTotalCost(filters)) + ")");
                break;
            case "CTR":
                label.setText("(" + formatDouble(campaignData.getCTR(filters)) + "%)");
                break;
            case "CPA":
                label.setText("($" + formatDouble(campaignData.getCPA(filters)) + ")");
                break;
            case "CPC":
                label.setText("($" + formatDouble(campaignData.getCPC(filters)) + ")");
                break;
            case "CPM":
                label.setText("($" + formatDouble(campaignData.getCPM(filters)) + ")");
                break;
            case "Bounce Rate":
                label.setText("(" + formatDouble(campaignData.getBounceRate(filters)) + "%)");
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
                        ? campaignData.getUniquesForDate(dateTime, filters)
                        : campaignData.getBouncesForDate(dateTime, filters);
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
                        int uniquesValue = campaignData.getUniquesForDate(dateTime, filters);
                        series.getData().add(new XYChart.Data<>(xLabel, uniquesValue));

                        // Collect unique IDs for the period total
                        for (Object[] impression : campaignData.getImpressions(filters)) {
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
                        int bounceValue = campaignData.getBouncesForDate(dateTime, filters);
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
                    ? campaignData.getHourlyImpressions(dateTime, filters)
                    : campaignData.getImpressionsForDate(dateTime, filters);
            case "Clicks":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyClicks(dateTime, filters)
                    : campaignData.getClicksForDate(dateTime, filters);
            case "Conversions":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyConversions(dateTime, filters)
                    : campaignData.getConversionsForDate(dateTime, filters);
            case "Total Cost":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyTotalCost(dateTime, filters)
                    : campaignData.getTotalCostForDate(dateTime, filters);
            case "CTR":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyCTR(dateTime, filters)
                    : campaignData.getCTRForDate(dateTime, filters);
            case "CPA":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyCPA(dateTime, filters)
                    : campaignData.getCPAForDate(dateTime, filters);
            case "CPC":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyCPC(dateTime, filters)
                    : campaignData.getCPCForDate(dateTime, filters);
            case "CPM":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyCPM(dateTime, filters)
                    : campaignData.getCPMForDate(dateTime, filters);
            case "Bounce Rate":
                return currentGranularity == TimeGranularity.HOURLY
                    ? campaignData.getHourlyBounceRate(dateTime, filters)
                    : campaignData.getBounceRateForDate(dateTime, filters);
            case "Uniques":
                return campaignData.getUniquesForDate(dateTime, filters);
            case "Bounces":
                return campaignData.getBouncesForDate(dateTime, filters);
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
     * @param data The text data to export.
     */
    public void exportTextFile(String format, String data) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export as " + format);
        fileChooser.setInitialFileName("output");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(format + " file", "*." + format));
        File file = fileChooser.showSaveDialog(stage);
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
    private BufferedImage getLineChartImage() {
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export as Image");
        fileChooser.setInitialFileName("output");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image file", "*.png"));
        File file = fileChooser.showSaveDialog(stage);
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

    /**
     * Handles the export to PDF button.
     * @param actionEvent The action event.
     */
    @FXML
    public void handleExportPDF(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export as PDF");
        fileChooser.setInitialFileName("output");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF file", "*.pdf"));
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }
        try (PDDocument document = new PDDocument()) {
            BufferedImage bufferedImage = getLineChartImage();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            double imageWidth = bufferedImage.getWidth();
            double imageHeight = bufferedImage.getHeight();
            PDPage page = new PDPage(new PDRectangle((float)imageWidth, (float)imageHeight));
            document.addPage(page);
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageBytes, "image");
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pdImage, 0, 0);
            }
            document.save(file);
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
