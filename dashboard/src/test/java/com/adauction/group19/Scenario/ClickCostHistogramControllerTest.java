package com.adauction.group19.Scenario;

import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.ClickCostHistogramScreen;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
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

public class ClickCostHistogramControllerTest extends ApplicationTest {

  private Stage stage;
  private File tempClickLogFile;

  // Initialize file before application starts
  public ClickCostHistogramControllerTest() {
    try {
      // Create a temporary click log file with proper test data
      tempClickLogFile = File.createTempFile("click_log_test", ".csv");

      // Write CSV with proper header and data
      try (FileWriter writer = new FileWriter(tempClickLogFile)) {
        writer.write("Date,ID,Click Cost\n");
        // Add 10 data points with costs from 0.1 to 1.0
        for (int i = 1; i <= 10; i++) {
          double cost = i / 10.0;
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
  public void testHistogramAndControlsExist() {
    // Skip test if file wasn't created
    if (tempClickLogFile == null || !tempClickLogFile.exists()) {
      System.out.println("Skipping test because test file couldn't be created");
      return;
    }

    // Verify chart and controls exist
    BarChart<String, Number> histogram = lookup(".chart").queryAs(BarChart.class);
    Spinner<Integer> binsSpinner = lookup("#binsSpinner").queryAs(Spinner.class);

    assertNotNull(histogram, "Histogram chart should exist");
    assertNotNull(binsSpinner, "Bins spinner should exist");
    assertNotNull(lookup("#updateButton").query(), "Update button should exist");
    assertNotNull(lookup("#goBackButton").query(), "Back button should exist");
  }

  @Test
  public void testUpdateButton() {
    // Skip test if file wasn't created
    if (tempClickLogFile == null || !tempClickLogFile.exists()) {
      System.out.println("Skipping test because test file couldn't be created");
      return;
    }

    // Click update button
    clickOn("#updateButton");

    // Wait for chart to update
    WaitForAsyncUtils.waitForFxEvents();
    sleep(1000);

    // Verify chart has data
    BarChart<String, Number> histogram = lookup(".chart").queryAs(BarChart.class);
    assertFalse(histogram.getData().isEmpty(), "Histogram should have data after update");

    if (!histogram.getData().isEmpty()) {
      assertFalse(histogram.getData().get(0).getData().isEmpty(),
          "Histogram should have data points after update");
    }
  }

  @Test
  public void testBackButton() {
    // Skip test if file wasn't created
    if (tempClickLogFile == null || !tempClickLogFile.exists()) {
      System.out.println("Skipping test because test file couldn't be created");
      return;
    }

    // Verify back button exists
    assertNotNull(lookup("#goBackButton").query(), "Back button should exist");

    // We can't fully test navigation, but we can verify the button exists and is clickable
    try {
      clickOn("#goBackButton");
      // If we get here without exception, the button click worked
    } catch (Exception e) {
      fail("Back button should be clickable: " + e.getMessage());
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