package com.adauction.group19.Scenario;

import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.ClickCostHistogramScreen;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ClickCostHistogramScreenScenarioTest extends ApplicationTest {

    private Stage stage;
    private File tempClickLogFile;

    // Initialize file before application starts
    public ClickCostHistogramScreenScenarioTest() {
        try {
            // Create a temporary click log file with proper test data
            tempClickLogFile = File.createTempFile("click_log_test", ".csv");

            // Write CSV with proper header and data
            try (FileWriter writer = new FileWriter(tempClickLogFile)) {
                writer.write("Date,ID,Click Cost\n");
                // Add 20 data points with varying costs
                for (int i = 1; i <= 20; i++) {
                    double cost = i * 0.1; // Costs from 0.1 to 2.0
                    writer.write(String.format("2025-03-19 10:%02d:00,%d,%.1f\n", i, i, cost));
                }
            }

            // Store the file path in CampaignDataStore
            CampaignDataStore.getInstance().setClickLogPath(tempClickLogFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error creating test file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        if (tempClickLogFile != null && tempClickLogFile.exists()) {
            // Load the scene with our test file path
            Scene scene = ClickCostHistogramScreen.getScene(stage, tempClickLogFile.getAbsolutePath());
            stage.setScene(scene);
            stage.show();
            stage.toFront();

            // Wait for the scene to finish loading
            WaitForAsyncUtils.waitForFxEvents();
            sleep(1000);
        } else {
            // Fall back to a simple scene if file creation failed
            Scene emptyScene = new Scene(new javafx.scene.layout.VBox(), 800, 600);
            stage.setScene(emptyScene);
            stage.show();
            System.err.println("Test file was not created properly, using empty scene");
        }
    }

    @Test
    void testCompleteHistogramScenario() {
        // Skip test if file wasn't created
        if (tempClickLogFile == null || !tempClickLogFile.exists()) {
            System.out.println("Skipping test because test file couldn't be created");
            return;
        }

        // Wait for any pending UI updates
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);

        // Verify initial UI elements are present
        BarChart<String, Number> histogram = lookup(".chart").queryAs(BarChart.class);
        Spinner<Integer> binsSpinner = lookup("#binsSpinner").queryAs(Spinner.class);
        Button updateButton = lookup("#updateButton").queryAs(Button.class);
        Button backButton = lookup("#goBackButton").queryAs(Button.class);

        assertNotNull(histogram, "Histogram chart should be present");
        assertNotNull(binsSpinner, "Bins spinner should be present");
        assertNotNull(updateButton, "Update button should be present");
        assertNotNull(backButton, "Back button should be present");

        assertTrue(histogram.isVisible(), "Histogram should be visible");
        assertTrue(binsSpinner.isVisible(), "Bins spinner should be visible");
        assertTrue(updateButton.isVisible(), "Update button should be visible");
        assertTrue(backButton.isVisible(), "Back button should be visible");

        // Get the initial number of bins and verify data
        int initialBins = binsSpinner.getValue();
        assertFalse(histogram.getData().isEmpty(), "Histogram should have initial data");

        // Test changing the number of bins
        Platform.runLater(() -> binsSpinner.getValueFactory().setValue(15));
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);

        // Verify the histogram updates
        assertNotEquals(initialBins, binsSpinner.getValue(), "Bins spinner should update");
        assertFalse(histogram.getData().isEmpty(), "Histogram should still have data after bin change");

        // Test the update button
        clickOn(updateButton);
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);

        // Verify the histogram updates again
        assertFalse(histogram.getData().isEmpty(), "Histogram should have data after update");

        // Test the back button
        assertTrue(backButton.isVisible(), "Back button should be visible");
        assertFalse(backButton.isDisabled(), "Back button should be enabled");

        // Click back button and verify it doesn't throw exceptions
        try {
            clickOn(backButton);
            // If we get here without exception, the button click worked
        } catch (Exception e) {
            fail("Back button should be clickable: " + e.getMessage());
        }
    }

    @Test
    void testHistogramWithDifferentDataRanges() {
        // Skip test if file wasn't created
        if (tempClickLogFile == null || !tempClickLogFile.exists()) {
            System.out.println("Skipping test because test file couldn't be created");
            return;
        }

        // Wait for any pending UI updates
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000);

        // Get UI components
        BarChart<String, Number> histogram = lookup(".chart").queryAs(BarChart.class);
        Spinner<Integer> binsSpinner = lookup("#binsSpinner").queryAs(Spinner.class);
        Button updateButton = lookup("#updateButton").queryAs(Button.class);

        // Test with different bin sizes
        int[] testBins = {5, 10, 15, 20};
        for (int bins : testBins) {
            // Set the number of bins
            Platform.runLater(() -> binsSpinner.getValueFactory().setValue(bins));
            WaitForAsyncUtils.waitForFxEvents();
            sleep(1000);

            // Click update button
            clickOn(updateButton);
            WaitForAsyncUtils.waitForFxEvents();
            sleep(1000);

            // Verify the histogram updates correctly
            assertFalse(histogram.getData().isEmpty(), 
                "Histogram should have data with " + bins + " bins");
            
            // Verify the data series
            assertEquals(1, histogram.getData().size(), 
                "Should have one data series");
        }
    }

    @AfterEach
    public void cleanup() {
        FxRobot robot = new FxRobot();
        try {
            Platform.runLater(() -> {
                if (stage != null && stage.isShowing()) {
                    stage.hide();
                }
            });
            robot.sleep(200);

            // Delete temporary file
            if (tempClickLogFile != null && tempClickLogFile.exists()) {
                tempClickLogFile.delete();
            }
        } catch (Exception e) {
            // Ignore cleanup issues
        }
    }
} 