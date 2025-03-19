package com.adauction.group19.Unit;

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
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import static org.junit.jupiter.api.Assertions.*;


public class MetricsScreenUnitTest extends ApplicationTest {

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
    testData = createTestCampaignData();
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
  private CampaignData createTestCampaignData() {
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

      // Add conversions
      for (int j = 0; j < (2 + i/3); j++) {
        data.addClick(morningTime, 0.3, "1");
      }

      // Add some server logs (including bounces)
      for (int j = 0; j < (7 + i); j++) {
        boolean isBounce = j % 3 == 0;
        int pagesViewed = isBounce ? 1 : 3 + (j % 3);
        LocalDateTime endTime = morningTime.plusMinutes(isBounce ? 1 : 10);
        data.addServerLogEntry(morningTime, endTime, pagesViewed, j % 5 == 0, "1");
      }
    }

    return data;
  }

  @Test
  public void testInitialLabelsMatchTotalValues() {
    // Check that the initial label values match the calculated totals from the campaign data
    FxAssert.verifyThat("#lblImpressions",
        LabeledMatchers.hasText("(" + testData.getTotalImpressions(filters) + ")"));
    FxAssert.verifyThat("#lblClicks",
        LabeledMatchers.hasText("(" + testData.getTotalClicks(filters) + ")"));
    FxAssert.verifyThat("#lblUniques",
        LabeledMatchers.hasText("(" + testData.getTotalUniques(filters) + ")"));
    FxAssert.verifyThat("#lblConversions",
        LabeledMatchers.hasText("(" + testData.getTotalConversions(filters) + ")"));

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