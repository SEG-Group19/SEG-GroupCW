package com.adauction.group19.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;
import javafx.stage.Stage;

public class GraphSettingsController {

    public Button cancelButton;
    @FXML private TextField startDateField, endDateField;
    @FXML private Spinner<Integer> bounceSpinner;
    @FXML private CheckComboBox<String> ageCheckCombo, genderCheckCombo, contextCheckCombo, incomeCheckCombo;
    @FXML private Button saveButton;

    private Stage stage; // The popup stage if you want to close it

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
    private void handleTimeInterval() {
        // Each time-interval button calls this
        System.out.println("Time interval button clicked!");
        // You might record which button was clicked, etc.
    }

    @FXML
    private void handleSave() {
        // Access selected items
        System.out.println("Selected Ages: " + ageCheckCombo.getCheckModel().getCheckedItems());
        System.out.println("Bounce seconds: " + bounceSpinner.getValue());

        // Potentially store these values or pass to main scene
        if (stage != null) {
            stage.close();
        }
    }

    public void handleCancel(ActionEvent actionEvent) {
        // Potentially store these values or pass to main scene
        if (stage != null) {
            stage.close();
        }
    }
}
