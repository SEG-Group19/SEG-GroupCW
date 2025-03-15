package com.adauction.group19.controller;

import com.adauction.group19.model.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphSettingsController {

    public Button cancelButton;

    @FXML public DatePicker startDatePicker, endDatePicker;

    @FXML private CheckComboBox<String> ageCheckCombo, genderCheckCombo, contextCheckCombo, incomeCheckCombo;
    @FXML private Button saveButton;

    private Stage stage; // The popup stage if you want to close it
    private CampaignData campaignData;
    private MetricsScreenController metricsScreenController;

    private String lastTimeInterval = "";

    private HashMap<String, Enum<?>> filterMap = new HashMap<>();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        ageCheckCombo.getItems().addAll("Under 25", "25-34", "35-44", "45-54", "Over 54");
        genderCheckCombo.getItems().addAll("Male", "Female");
        contextCheckCombo.getItems().addAll("News", "Shopping", "Social Media", "Blog");
        incomeCheckCombo.getItems().addAll("Low", "Medium", "High");

        filterMap.put("Under 25", AgeRange.AGE_25_MINUS);
        filterMap.put("25-34", AgeRange.AGE_25_34);
        filterMap.put("35-44", AgeRange.AGE_35_44);
        filterMap.put("45-54", AgeRange.AGE_45_54);
        filterMap.put("Over 54", AgeRange.AGE_55_PLUS);
        filterMap.put("Male", Gender.MALE);
        filterMap.put("Female", Gender.FEMALE);
        filterMap.put("News", Context.NEWS);
        filterMap.put("Shopping", Context.SHOPPING);
        filterMap.put("Social Media", Context.SOCIAL_MEDIA);
        filterMap.put("Blog", Context.BLOG);
        filterMap.put("Low", Income.LOW);
        filterMap.put("Medium", Income.MEDIUM);
        filterMap.put("High", Income.HIGH);

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
        // Apply dates
        handleApplyDateRange();

        // Apply time granularity option
        if (metricsScreenController != null && !lastTimeInterval.isEmpty()) {
            metricsScreenController.setTimeInterval(lastTimeInterval);
        }

        // Apply filters
        List<Set<Enum<?>>> filters = metricsScreenController.getFilters();
        for (int i = 0; i < filters.size(); i++) filters.get(i).clear();

        for (String filter : genderCheckCombo.getCheckModel().getCheckedItems()) {
            filters.get(0).add(filterMap.get(filter));
        }
        for (String filter : ageCheckCombo.getCheckModel().getCheckedItems()) {
            filters.get(1).add(filterMap.get(filter));
        }
        for (String filter : incomeCheckCombo.getCheckModel().getCheckedItems()) {
            filters.get(2).add(filterMap.get(filter));
        }
        for (String filter : contextCheckCombo.getCheckModel().getCheckedItems()) {
            filters.get(3).add(filterMap.get(filter));
        }

        metricsScreenController.setFilters(filters);

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
