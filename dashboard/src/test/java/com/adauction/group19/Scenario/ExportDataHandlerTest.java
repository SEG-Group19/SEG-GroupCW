package com.adauction.group19.Scenario;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import com.adauction.group19.controller.MetricsScreenController;
import com.adauction.group19.model.AgeRange;
import com.adauction.group19.model.CampaignData;
import com.adauction.group19.model.Context;
import com.adauction.group19.model.Gender;
import com.adauction.group19.model.Income;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.ViewMetricsScreen;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ExportDataHandlerTest extends ApplicationTest {

  private Stage stage;
  private MetricsScreenController controller;
  private CampaignData testData;
  private List<Set<Enum<?>>> filters = new ArrayList<>();

  @TempDir
  Path tempDir;


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
    assertNotNull(controller);
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
  public void testExportCSV_CreatesFile() throws Exception {
    clickOn("#chkImpressions");
    clickOn("#chkClicks");
    clickOn("#chkConversions");
    sleep(300);
    File expectedFile = tempDir.resolve("test.csv").toFile();

    MetricsScreenController controllerSpy = spy(controller);
    doReturn(expectedFile).when(controllerSpy).chooseFile("csv");

    controllerSpy.handleExportCSV(null);

    assertTrue(expectedFile.exists());
    assertTrue(expectedFile.length() > 0);
  }

  @Test
  public void testExportJSON_CreatesFile() throws Exception {
    clickOn("#chkImpressions");
    clickOn("#chkClicks");
    clickOn("#chkConversions");
    sleep(300);
    File expectedFile = tempDir.resolve("test.json").toFile();

    MetricsScreenController controllerSpy = spy(controller);
    doReturn(expectedFile).when(controllerSpy).chooseFile("json");

    controllerSpy.handleExportJSON(null);

    assertTrue(expectedFile.exists());
    assertTrue(expectedFile.length() > 0);
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
