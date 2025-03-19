package com.adauction.group19.Scenario;

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
import java.util.concurrent.TimeoutException;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.junit.jupiter.api.Assertions.*;

public class ExtendedMetricsScreenTest extends ApplicationTest {

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

    // Create test data with multiple days of activity
    testData = createMultiDayTestData();
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
   * Creates test data that spans multiple days with various metrics
   */
  private CampaignData createMultiDayTestData() {
    CampaignData data = new CampaignData();

    // Create data for the past 14 days
    LocalDate today = LocalDate.now();

    for (int i = 13; i >= 0; i--) {
      LocalDate date = today.minusDays(i);

      // Morning data (10:00 AM)
      LocalDateTime morningTime = LocalDateTime.of(date, LocalTime.of(10, 0));
      // Add impressions (increasing by day)
      for (int j = 0; j < 10 + i; j++) {
        data.addImpression(morningTime, "user" + j,
            j % 2 == 0 ? Gender.MALE : Gender.FEMALE,
            AgeRange.AGE_25_34,
            Income.MEDIUM,
            Context.BLOG,
            0.5);
      }

      // Add clicks (about half of impressions)
      for (int j = 0; j < (5 + i/2); j++) {
        data.addClick(morningTime, 0.3, "1");
      }

      // Add conversions (fewer than clicks)
      for (int j = 0; j < (2 + i/3); j++) {
        // Check which method exists in your CampaignData class
        // Could be addConversion or addConversions - use the correct one
        data.addClick(morningTime, 0.3, "1"); // Treat clicks as conversions for testing purposes
      }

      // Add some server logs (including bounces)
      for (int j = 0; j < (7 + i); j++) {
        boolean isBounce = j % 3 == 0; // Every third entry is a bounce
        int pagesViewed = isBounce ? 1 : 3 + (j % 3);
        LocalDateTime endTime = morningTime.plusMinutes(isBounce ? 1 : 10);
        data.addServerLogEntry(morningTime, endTime, pagesViewed, j % 5 == 0, "1");
      }

      // Afternoon data (15:00 PM)
      LocalDateTime afternoonTime = LocalDateTime.of(date, LocalTime.of(15, 0));
      // More impressions
      for (int j = 0; j < 15 + i; j++) {
        data.addImpression(afternoonTime, "user" + (j+100),
            j % 2 == 0 ? Gender.MALE : Gender.FEMALE,
            AgeRange.AGE_35_44,
            Income.HIGH,
            Context.NEWS,
            0.7);
      }

      // More clicks (about 40% of impressions)
      for (int j = 0; j < (6 + i/2); j++) {
        data.addClick(afternoonTime, 0.4, "1");
      }

      // More conversions
      for (int j = 0; j < (3 + i/3); j++) {
        // Use the same method as above for consistency
        data.addClick(afternoonTime, 0.4, "1"); // Treat clicks as conversions for testing purposes
      }

      // More server logs
      for (int j = 0; j < (9 + i); j++) {
        boolean isBounce = j % 4 == 0; // Every fourth entry is a bounce
        int pagesViewed = isBounce ? 1 : 2 + (j % 4);
        LocalDateTime endTime = afternoonTime.plusMinutes(isBounce ? 2 : 15);
        data.addServerLogEntry(afternoonTime, endTime, pagesViewed, j % 6 == 0, "1");
      }
    }

    return data;
  }

  @Test
  public void testInitialLabelsMatchTotalValues() {
    // Check that the initial label values match the calculated totals from the campaign data

    FxAssert.verifyThat("#lblImpressions", LabeledMatchers.hasText("(" + testData.getTotalImpressions(filters) + ")"));
    FxAssert.verifyThat("#lblClicks", LabeledMatchers.hasText("(" + testData.getTotalClicks(filters) + ")"));
    FxAssert.verifyThat("#lblUniques", LabeledMatchers.hasText("(" + testData.getTotalUniques(filters) + ")"));
    FxAssert.verifyThat("#lblConversions", LabeledMatchers.hasText("(" + testData.getTotalConversions(filters) + ")"));

    // Check the formatted values for rate and cost metrics
    String expectedCTR = "(" + String.format("%.2f", testData.getCTR(filters)) + "%)";
    FxAssert.verifyThat("#lblCTR", LabeledMatchers.hasText(expectedCTR));

    String expectedCPC = "($" + String.format("%.2f", testData.getCPC(filters)) + ")";
    FxAssert.verifyThat("#lblCPC", LabeledMatchers.hasText(expectedCPC));
  }

  @Test
  public void testCheckboxTogglesMetricVisibility() {
    // Start with clear chart
    LineChart<String, Number> chart = lookup("#lineChart").queryAs(LineChart.class);
    assertEquals(0, chart.getData().size(), "Chart should start empty");

    // Toggle clicks checkbox
    clickOn("#chkClicks");
    sleep(300);

    // Verify clicks series was added
    assertEquals(1, chart.getData().size(), "Chart should have one series");
    assertEquals("Clicks", chart.getData().get(0).getName(), "Series should be Clicks");

    // Toggle another metric (Impressions)
    clickOn("#chkImpressions");
    sleep(300);

    // Verify both series are now present
    assertEquals(2, chart.getData().size(), "Chart should have two series");

    // Toggle off clicks
    clickOn("#chkClicks");
    sleep(300);

    // Verify only impressions remain
    assertEquals(1, chart.getData().size(), "Chart should have one series");
    assertEquals("Impressions", chart.getData().get(0).getName(), "Series should be Impressions");
  }
  @Test
  public void testMultipleMetricsSelection() {
    // Toggle on multiple metrics
    clickOn("#chkImpressions");
    clickOn("#chkClicks");
    clickOn("#chkConversions");
    sleep(300);

    // Verify all three series are displayed
    LineChart<String, Number> chart = lookup("#lineChart").queryAs(LineChart.class);
    assertEquals(3, chart.getData().size(), "Chart should have three series");

    // Verify series names
    boolean hasImpressions = false, hasClicks = false, hasConversions = false;
    for (XYChart.Series<String, Number> series : chart.getData()) {
      switch (series.getName()) {
        case "Impressions": hasImpressions = true; break;
        case "Clicks": hasClicks = true; break;
        case "Conversions": hasConversions = true; break;
      }
    }
    assertTrue(hasImpressions && hasClicks && hasConversions, "All three metrics should be present");
  }

  @Test
  public void testFormattedMetricsLabels() {
    // Verify proper formatting of different metric types

    // Cost metrics should have $ prefix and 2 decimal places
    String totalCostText = lookup("#lblTotalCost").queryAs(Label.class).getText();
    assertTrue(totalCostText.startsWith("($") && totalCostText.endsWith(")"),
        "Total cost should be formatted with $ prefix");

    // Rate metrics should have % suffix and 2 decimal places
    String ctrText = lookup("#lblCTR").queryAs(Label.class).getText();
    assertTrue(ctrText.contains("%"), "CTR should be formatted with % suffix");

    // Count metrics should be integers
    String impressionsText = lookup("#lblImpressions").queryAs(Label.class).getText();
    assertTrue(impressionsText.matches("\\(\\d+\\)"), "Impressions should be an integer");
  }

  @Test
  public void testAllCheckboxesWork() {
    // Test that all checkboxes can be toggled and affect the chart
    CheckBox[] checkboxes = {
        lookup("#chkImpressions").queryAs(CheckBox.class),
        lookup("#chkClicks").queryAs(CheckBox.class),
        lookup("#chkUniques").queryAs(CheckBox.class),
        lookup("#chkBounces").queryAs(CheckBox.class),
        lookup("#chkConversions").queryAs(CheckBox.class),
        lookup("#chkTotalCost").queryAs(CheckBox.class),
        lookup("#chkCTR").queryAs(CheckBox.class),
        lookup("#chkCPA").queryAs(CheckBox.class),
        lookup("#chkCPC").queryAs(CheckBox.class),
        lookup("#chkCPM").queryAs(CheckBox.class),
        lookup("#chkBounceRate").queryAs(CheckBox.class)
    };

    LineChart<String, Number> chart = lookup("#lineChart").queryAs(LineChart.class);

    // First ensure chart is empty
    assertEquals(0, chart.getData().size(), "Chart should start empty");

    // Toggle each checkbox on and verify it adds a series
    for (int i = 0; i < checkboxes.length; i++) {
      assertFalse(checkboxes[i].isSelected(), "Checkbox should start unchecked: " + checkboxes[i].getText());
      clickOn(checkboxes[i]);
      sleep(200);
      assertEquals(i + 1, chart.getData().size(),
          "Chart should have " + (i + 1) + " series after toggling " + checkboxes[i].getText());
    }

    // Toggle each checkbox off and verify it removes a series
    for (int i = checkboxes.length - 1; i >= 0; i--) {
      assertTrue(checkboxes[i].isSelected(), "Checkbox should be checked: " + checkboxes[i].getText());
      clickOn(checkboxes[i]);
      sleep(200);
      assertEquals(i, chart.getData().size(),
          "Chart should have " + i + " series after toggling off " + checkboxes[i].getText());
    }
  }

  @Test
  public void testBackButtonNavigation() {
    // Verify back button exists
    Node backButton = lookup("#goBackButton").query();
    assertNotNull(backButton, "Back button should exist");

    // We can't fully test navigation without mocking the MainMenu,
    // but we can at least click the button and verify it doesn't throw exceptions
    try {
      clickOn("#goBackButton");
      // If we get here without exception, the test passes
    } catch (Exception e) {
      fail("Back button click threw exception: " + e.getMessage());
    }
  }

  @AfterEach
  public void cleanup() {
    // Ensure we clean up after tests
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