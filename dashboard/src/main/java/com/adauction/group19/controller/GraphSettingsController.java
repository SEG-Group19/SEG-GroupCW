package com.adauction.group19.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;
import javafx.stage.Stage;

public class GraphSettingsController {

    public Button cancelButton;

    @FXML public DatePicker startDatePicker, endDatePicker;
    @FXML private Spinner<Integer> bounceSpinner;
    @FXML private CheckComboBox<String> ageCheckCombo, genderCheckCombo, contextCheckCombo, incomeCheckCombo;
    @FXML private Button saveButton;

    private Stage stage; // The popup stage if you want to close it

    private String lastTimeInterval = "";

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Add some dummy data to the multiple selection combos
        ageCheckCombo.getItems().addAll("Under 25", "25-34", "35-44", "45-54", "Over 54");
        genderCheckCombo.getItems().addAll("Male", "Female");
        contextCheckCombo.getItems().addAll("News", "Shopping", "Social Media", "Blog", "Hobbies", "Travel");
        incomeCheckCombo.getItems().addAll("Low", "Medium", "High");
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
        System.out.println("Bounce seconds: " + bounceSpinner.getValue());

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
}
