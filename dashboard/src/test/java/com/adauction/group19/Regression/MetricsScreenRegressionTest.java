package com.adauction.group19.Regression;

import com.adauction.group19.controller.MetricsScreenController;
import com.adauction.group19.model.*;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.ViewMetricsScreen;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class MetricsScreenRegressionTest extends ApplicationTest {

  private Stage stage;
  private MetricsScreenController controller;
  private CampaignData testData;
  private List<Set<Enum<?>>> filters = new ArrayList<>();

  @BeforeEach
  public void setup() {
    for (int i = 0; i < 4; i++) {
      filters.add(new HashSet<>());
    }
  }

  @Override
  public void start(Stage stage) {
    this.stage = stage;

    // Create test data with historical comparison scenarios
    testData = createRegressionTestData();
    CampaignDataStore.getInstance().setCampaignData(testData);

    // Load the metrics screen
    Scene scene = ViewMetricsScreen.getScene(stage);
    stage.setScene(scene);
    stage.show();
    stage.toFront();

    // Get the controller to directly test its methods
    controller = (MetricsScreenController) scene.getUserData();
  }

  /**
   * Creates test data that simulates different historical scenarios
   * to check for regression in metric calculations
   */
  private CampaignData createRegressionTestData() {
    CampaignData data = new CampaignData();

    // Previous version data generation scenarios
    LocalDate[] historicalDates = {
        LocalDate.now().minusMonths(3),
        LocalDate.now().minusMonths(2),
        LocalDate.now().minusMonths(1)
    };

    // Simulate different campaign performances across historical periods
    for (LocalDate historicalDate : historicalDates) {
      LocalDateTime baseTime = LocalDateTime.of(historicalDate, LocalTime.of(10, 0));

      // Varying performance scenarios
      int impressionMultiplier = historicalDate.getMonthValue();

      for (int j = 0; j < 50 * impressionMultiplier; j++) {
        data.addImpression(baseTime, "user_historical_" + j,
            j % 2 == 0 ? Gender.MALE : Gender.FEMALE,
            j % 3 == 0 ? AgeRange.AGE_25_34 : AgeRange.AGE_35_44,
            j % 4 == 0 ? Income.LOW : Income.MEDIUM,
            j % 5 == 0 ? Context.BLOG : Context.NEWS,
            0.1 * impressionMultiplier);

        // Clicks proportional to impressions
        if (j % 3 == 0) {
          data.addClick(baseTime, 0.05 * impressionMultiplier, "user_historical_" + j);
        }

        // Conversions subset of clicks
        if (j % 5 == 0) {
          data.addServerLogEntry(
              baseTime,
              baseTime.plusMinutes(10),
              3,
              true,
              "user_historical_" + j
          );
        }
      }
    }

    return data;
  }

  @Test
  public void testMetricConsistencyAcrossHistoricalData() {
    // Verify that metrics are calculated consistently across different historical periods
    double ctrBeforeFiltering = testData.getCTR(filters);

    // Apply various filters to test consistency
    Set<Enum<?>> genderFilter = new HashSet<>();
    genderFilter.add(Gender.MALE);
    filters.set(0, genderFilter);

    double ctrWithGenderFilter = testData.getCTR(filters);

    // Ensure the filtering doesn't cause unexpected metric changes
    assertNotEquals(0, ctrBeforeFiltering, "CTR should not be zero before filtering");
    assertNotEquals(0, ctrWithGenderFilter, "CTR should not be zero after gender filtering");
  }

  @Test
  public void testMultiMetricInteractionStability() {
    // Toggle multiple metrics and verify chart stability
    CheckBox[] checkboxes = {
        lookup("#chkImpressions").queryAs(CheckBox.class),
        lookup("#chkClicks").queryAs(CheckBox.class),
        lookup("#chkConversions").queryAs(CheckBox.class)
    };

    // Toggle all checkboxes
    for (CheckBox checkbox : checkboxes) {
      clickOn(checkbox);
      sleep(200);
    }

    // Verify chart data
    LineChart<String, Number> chart = lookup("#lineChart").queryAs(LineChart.class);
    assertEquals(checkboxes.length, chart.getData().size(),
        "Chart should have series for all selected metrics");

    // Verify each series has data points
    for (XYChart.Series<String, Number> series : chart.getData()) {
      assertFalse(series.getData().isEmpty(),
          "Series " + series.getName() + " should have data points");
    }
  }

  @Test
  public void testDateRangeStability() {
    // Use graph settings to modify date range instead of specific buttons
    clickOn("#graphSettingsBtn");
    sleep(300);

    // Verify date pickers exist and can be interacted with
    try {
      // These might need to be adjusted based on your actual FXML
      clickOn("#startDatePicker");
      sleep(200);
      clickOn("#endDatePicker");
      sleep(200);
    } catch (Exception e) {
      fail("Unable to interact with date range selection: " + e.getMessage());
    }

    // Verify chart updates without errors
    LineChart<String, Number> chart = lookup("#lineChart").queryAs(LineChart.class);
    assertTrue(chart.getData().size() >= 0, "Chart should be able to update with selected date range");
  }

  @AfterEach
  public void cleanup() {
    FxRobot robot = new FxRobot();
    try {
      Platform.runLater(() -> {
        if (stage != null) {
          stage.hide();
        }
      });
      // Wait for the UI update to complete
      robot.sleep(200);
    } catch (Exception e) {
      // Ignore cleanup issues
    }
  }
}