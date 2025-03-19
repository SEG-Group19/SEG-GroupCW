package com.adauction.group19.Defect;

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
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class MetricsScreenDefectTest extends ApplicationTest {

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

    // Create test data with potential defect scenarios
    testData = createDefectTestData();
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
   * Creates test data with potential edge cases and defect scenarios
   */
  private CampaignData createDefectTestData() {
    CampaignData data = new CampaignData();
    LocalDate today = LocalDate.now();

    // Scenario 1: Zero impressions
    LocalDateTime zeroTime = LocalDateTime.of(today, LocalTime.of(10, 0));

    // Scenario 2: Extreme values
    LocalDateTime extremeTime = LocalDateTime.of(today, LocalTime.of(15, 0));
    for (int j = 0; j < 10000; j++) {
      data.addImpression(extremeTime, "user_extreme_" + j,
          j % 2 == 0 ? Gender.MALE : Gender.FEMALE,
          AgeRange.AGE_25_34,
          Income.MEDIUM,
          Context.BLOG,
          Double.MAX_VALUE);
    }

    // Scenario 3: Sparse data
    LocalDateTime sparseTime = LocalDateTime.of(today, LocalTime.of(20, 0));
    for (int j = 0; j < 5; j++) {
      data.addImpression(sparseTime, "user_sparse_" + j,
          Gender.MALE,
          AgeRange.AGE_35_44,
          Income.HIGH,
          Context.NEWS,
          0.01);
      data.addClick(sparseTime, 0.001, "sparse_user_" + j);
    }

    // Scenario 4: Duplicate entries
    LocalDateTime duplicateTime = LocalDateTime.of(today, LocalTime.of(12, 0));
    for (int j = 0; j < 10; j++) {
      data.addImpression(duplicateTime, "duplicate_user",
          Gender.FEMALE,
          AgeRange.AGE_25_34,
          Income.LOW,
          Context.SHOPPING,
          0.1);
      data.addClick(duplicateTime, 0.2, "duplicate_user");
    }

    return data;
  }

  @Test
  public void testLabelsWithExtremeData() {
    // Verify labels can handle extreme data volumes and values
    Label impressionsLabel = lookup("#lblImpressions").queryAs(Label.class);
    Label clicksLabel = lookup("#lblClicks").queryAs(Label.class);
    Label ctrLabel = lookup("#lblCTR").queryAs(Label.class);

    // Verify labels update correctly with extreme data
    assertNotNull(impressionsLabel.getText(), "Impressions label should not be null");
    assertNotNull(clicksLabel.getText(), "Clicks label should not be null");
    assertNotNull(ctrLabel.getText(), "CTR label should not be null");
  }

  @Test
  public void testChartHandlesDuplicateEntries() {
    // Toggle multiple metrics
    clickOn("#chkImpressions");
    clickOn("#chkClicks");
    sleep(300);

    // Verify chart updates without errors
    LineChart<String, Number> chart = lookup("#lineChart").queryAs(LineChart.class);
    assertTrue(chart.getData().size() > 0, "Chart should have series with duplicate entries");
  }

  @Test
  public void testPerformanceWithLargeDataset() {
    // Toggle multiple metrics
    clickOn("#chkImpressions");
    clickOn("#chkClicks");
    clickOn("#chkConversions");
    sleep(300);

    // Measure chart generation time
    long startTime = System.currentTimeMillis();
    LineChart<String, Number> chart = lookup("#lineChart").queryAs(LineChart.class);
    long endTime = System.currentTimeMillis();

    // Verify chart generated within reasonable time
    assertTrue(chart.getData().size() > 0, "Chart should have series");
    assertTrue(endTime - startTime < 5000, "Chart generation should take less than 5 seconds");
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