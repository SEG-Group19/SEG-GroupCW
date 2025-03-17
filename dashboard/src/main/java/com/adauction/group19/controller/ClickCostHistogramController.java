package com.adauction.group19.controller;

import com.adauction.group19.service.ClickDistributionService;
import com.adauction.group19.view.MainMenuScreen;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;

public class ClickCostHistogramController {

    @FXML private VBox chartContainer;
    @FXML private Spinner<Integer> binsSpinner;
    @FXML private Button goBackButton;
    @FXML private Button updateButton;

    private Stage stage;
    private final ClickDistributionService service;
    private String currentFilePath;
    private BarChart<String, Number> histogram;
    private boolean chartInitialized = false;

    public ClickCostHistogramController() {
        System.out.println("ClickCostHistogramController constructor called");
        this.service = new ClickDistributionService();
    }

    @FXML
    private void initialize() {
        try {
            System.out.println("Initializing ClickCostHistogramController...");
            
            // Check if FXML injection worked
            if (chartContainer == null) {
                throw new RuntimeException("chartContainer not injected by FXML loader");
            }
            
            if (binsSpinner == null) {
                throw new RuntimeException("binsSpinner not injected by FXML loader");
            }
            
            System.out.println("FXML injection successful");
            
            // Initialize the spinner
            if (binsSpinner.getValueFactory() == null) {
                System.out.println("Setting up spinner value factory");
                binsSpinner.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 10, 5)
                );
            }

            // Defer chart creation until the scene is ready
            Platform.runLater(this::initializeChart);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error during initialization: " + e.getMessage());
            showError("Initialization Error", "Failed to initialize the view", e.getMessage());
        }
    }

    private void initializeChart() {
        try {
            if (chartInitialized) {
                System.out.println("Chart already initialized, skipping");
                return;
            }

            System.out.println("Creating chart axes");
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Click Cost Range ($)");
            xAxis.setAnimated(false);
            
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Frequency");
            yAxis.setAnimated(false);
            yAxis.setAutoRanging(true);
            
            System.out.println("Creating bar chart");
            histogram = new BarChart<>(xAxis, yAxis);
            histogram.setTitle("Click Cost Distribution");
            histogram.setAnimated(false);
            histogram.setLegendVisible(false);
            histogram.setVerticalGridLinesVisible(true);
            histogram.setHorizontalGridLinesVisible(true);
            histogram.setStyle("-fx-background-color: transparent;");
            histogram.setCategoryGap(0);
            histogram.setBarGap(0);
            
            // Style the chart
            histogram.getStyleClass().add("chart");
            
            // Make the chart fill the available space
            System.out.println("Setting chart size bindings");
            histogram.prefWidthProperty().bind(chartContainer.widthProperty());
            histogram.prefHeightProperty().bind(chartContainer.heightProperty());
            
            // Add chart to container
            System.out.println("Adding chart to container");
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(histogram);
            
            chartInitialized = true;
            System.out.println("Chart initialization complete");

            // If we have data waiting to be displayed, show it now
            if (currentFilePath != null && !currentFilePath.isEmpty()) {
                updateHistogram();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error creating chart: " + e.getMessage());
            showError("Chart Creation Error", "Failed to create the histogram", e.getMessage());
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void loadData(String filePath) {
        try {
            System.out.println("Loading data from: " + filePath);
            this.currentFilePath = filePath;
            
            // If the chart isn't ready yet, data will be loaded when it is
            if (!chartInitialized) {
                System.out.println("Chart not yet initialized, deferring data load");
                return;
            }
            
            updateHistogram();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading data: " + e.getMessage());
            showError("Data Loading Error", "Failed to load click cost data", e.getMessage());
        }
    }

    @FXML
    private void handleBackButton() {
        try {
            if (stage != null) {
                stage.setScene(MainMenuScreen.getScene(stage));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation Error", "Failed to return to main menu", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (!chartInitialized) {
            System.out.println("Chart not initialized, attempting to initialize");
            initializeChart();
        }
        updateHistogram();
    }

    private void updateHistogram() {
        try {
            if (currentFilePath == null || currentFilePath.isEmpty()) {
                showError("Data Error", "No data file loaded", "Please load a click log file first.");
                return;
            }

            if (!chartInitialized || histogram == null) {
                System.err.println("Chart not properly initialized");
                showError("Chart Error", "Chart not initialized", "The histogram chart was not properly initialized.");
                return;
            }

            System.out.println("Updating histogram with bins: " + binsSpinner.getValue());
            int bins = binsSpinner.getValue();
            Map<Double, Integer> distribution = service.calculateClickCostDistribution(currentFilePath, bins);
            
            if (distribution.isEmpty()) {
                showError("Data Error", "No data to display", "The click log file contains no valid click cost data.");
                return;
            }

            System.out.println("Clearing existing data and adding new series");
            histogram.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            
            distribution.forEach((cost, frequency) -> {
                // Format the cost range for the category axis
                String costRange = String.format("%.2f", cost);
                series.getData().add(new XYChart.Data<>(costRange, frequency));
            });
            
            histogram.getData().add(series);
            System.out.println("Histogram updated successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error updating histogram: " + e.getMessage());
            showError("Update Error", "Failed to update histogram", e.getMessage());
        }
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 