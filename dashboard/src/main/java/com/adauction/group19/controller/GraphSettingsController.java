package com.adauction.group19.controller;

import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GraphSettingsController {

    public Button cancelButton;

    @FXML public DatePicker startDatePicker, endDatePicker;

    @FXML private CheckComboBox<String> ageCheckCombo, genderCheckCombo, contextCheckCombo, incomeCheckCombo;
    @FXML private Button saveButton;

    private Stage stage; // The popup stage if you want to close it
    private CampaignData campaignData;
    private MetricsScreenController metricsScreenController;

    private String lastTimeInterval = "";


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        ageCheckCombo.getItems().addAll("Under 25", "25-34", "35-44", "45-54", "Over 54");
        genderCheckCombo.getItems().addAll("Male", "Female");
        contextCheckCombo.getItems().addAll("News", "Shopping", "Social Media", "Blog", "Hobbies", "Travel");
        incomeCheckCombo.getItems().addAll("Low", "Medium", "High");

        campaignData = CampaignDataStore.getInstance().getCampaignData();

        setupDatePickers();
    }

    protected void setDates(LocalDate startDate, LocalDate endDate) {
        startDatePicker.setValue(startDate);
        endDatePicker.setValue(endDate);
    }

    /**
     * Sets up the date pickers with proper formatting and listeners.
     */
    private void setupDatePickers() {
        // Set date formatters
        StringConverter<LocalDate> dateConverter = new StringConverter<>() {
            private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                return (date != null) ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty())
                        ? LocalDate.parse(string, dateFormatter) : null;
            }
        };

        startDatePicker.setConverter(dateConverter);
        endDatePicker.setConverter(dateConverter);

        // Ensure end date can't be before start date
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && endDatePicker.getValue() != null &&
                    newValue.isAfter(endDatePicker.getValue())) {
                endDatePicker.setValue(newValue);
            }
        });

        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && startDatePicker.getValue() != null &&
                    newValue.isBefore(startDatePicker.getValue())) {
                startDatePicker.setValue(newValue);
            }
        });
    }

    /**
     * Handles date range application.
     */
    @FXML
    public void handleApplyDateRange() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate != null && endDate != null) {
            metricsScreenController.setDates(startDate, endDate);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Date Range");
            alert.setHeaderText("Please select both start and end dates");
            alert.setContentText("Both date fields must be filled to apply a custom date range.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleTimeInterval(ActionEvent event) {
        // Get the source button of the event
        Button clickedButton = (Button) event.getSource();

        // Save the button text as the selected time interval
        lastTimeInterval = clickedButton.getText();
    }

    @FXML
    private void handleSave() {
        // Access selected items
        System.out.println("Selected Dates: " + startDatePicker.getValue() + " to " + endDatePicker.getValue());
        System.out.println("Selected Time Interval: " + lastTimeInterval);
        System.out.println("Selected Ages: " + ageCheckCombo.getCheckModel().getCheckedItems());
        System.out.println("Selected Gender/s " + genderCheckCombo.getCheckModel().getCheckedItems());
        System.out.println("Selected Contexts: " + contextCheckCombo.getCheckModel().getCheckedItems());
        System.out.println("Selected Incomes: " + incomeCheckCombo.getCheckModel().getCheckedItems());

        // Apply dates
        handleApplyDateRange();

        // Apply time granularity option
        if (metricsScreenController != null && !lastTimeInterval.isEmpty()) {
            metricsScreenController.setTimeInterval(lastTimeInterval);
        }

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
