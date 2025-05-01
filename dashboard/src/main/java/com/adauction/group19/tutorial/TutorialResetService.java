package com.adauction.group19.tutorial;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Service for resetting tutorial progress.
 */
public class TutorialResetService {
    
    /**
     * Resets the tutorial progress for the current user.
     * Shows a confirmation dialog before proceeding.
     * 
     * @return true if the reset was successful, false otherwise
     */
    public static boolean resetTutorials() {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Tutorials");
        alert.setHeaderText("Reset All Tutorials");
        alert.setContentText("This will reset all tutorials, and they will be shown again when you visit each page. Are you sure?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Reset the page visit tracker
                PageVisitTracker.getInstance().resetProgress();
                
                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Reset Complete");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Tutorials have been reset successfully. They will be shown again when you visit each page.");
                successAlert.showAndWait();
                
                return true;
            } catch (Exception e) {
                // Show error message
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Reset Failed");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Failed to reset tutorials: " + e.getMessage());
                errorAlert.showAndWait();
                
                return false;
            }
        }
        
        return false;
    }
}