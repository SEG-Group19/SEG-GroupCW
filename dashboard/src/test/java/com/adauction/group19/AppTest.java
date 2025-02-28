package com.adauction.group19;

import com.adauction.group19.controller.InputDataController;
import com.adauction.group19.controller.MetricsScreenController;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import com.adauction.group19.model.CampaignData;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class AppTest extends ApplicationTest {

    private MetricsScreenController metricsScreenController;
    private CampaignData campaignData;

    @Override
    public void start(Stage stage) {
        // Launch the application in this test environment
        new App().start(stage);
        stage.toFront();
        stage.requestFocus();
    }

    /**
     * Set up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        campaignData = new CampaignData();
        metricsScreenController = new MetricsScreenController();
    }

    /**
     * Test the computation of metrics based on the campaign data.
     */
    @Test
    void testMetricComputation() {
        campaignData.addImpression(null, null, null, null, null, 1.5);
        campaignData.addClick(null, 0.5);
        campaignData.addServerLogEntry(null, null, 1, true);

        assertEquals(1, campaignData.getTotalImpressions());
        assertEquals(1, campaignData.getTotalClicks());
        assertEquals(1, campaignData.getTotalConversions());
        assertEquals(2.0, campaignData.getTotalCost(), 0.01);
        assertEquals(100.0, campaignData.getCTR(), 0.01);
        assertEquals(2.0, campaignData.getCPA(), 0.01);
        assertEquals(2.0, campaignData.getCPC(), 0.01);
    }

    /**
     * Test the graph updates based on metric selection.
     */
    @Test
    void testGraphUpdatesOnMetricSelection() {
        clickOn("#chkImpressions");
        WaitForAsyncUtils.waitForFxEvents();
        FxAssert.verifyThat("#lblImpressions", hasText("(0)"));

        clickOn("#chkClicks");
        WaitForAsyncUtils.waitForFxEvents();
        FxAssert.verifyThat("#lblClicks", hasText("(0)"));
    }

    @Test
    void testTimeScalingUpdatesGraph() {
        clickOn("#btn1Day");
        WaitForAsyncUtils.waitForFxEvents();
        FxAssert.verifyThat("#xAxis", hasText("Time (HH:mm)"));

        clickOn("#btn1Week");
        WaitForAsyncUtils.waitForFxEvents();
        FxAssert.verifyThat("#xAxis", hasText("Date & Time"));
    }
    /**
     * Test the computation of metrics based on the campaign data.
     */
    @Test
    void testEmptyDatasetHandling() {
        campaignData = new CampaignData();
        assertEquals(0, campaignData.getTotalImpressions());
        assertEquals(0, campaignData.getTotalClicks());
        assertEquals(0, campaignData.getTotalConversions());
    }
    /**
     * Test the computation of metrics based on the campaign data.
     */
    @Test
    void testLargeDatasetHandling() {
        for (int i = 0; i < 100000; i++) {
            campaignData.addImpression(null, null, null, null, null, 1.0);
        }
        assertEquals(100000, campaignData.getTotalImpressions());
    }

    @Test
    void testMainMenu() {
        // Verify that the Main Menu scene is displayed with both buttons
        FxAssert.verifyThat("#btnInputData", hasText("Input Data"));
        FxAssert.verifyThat("#btnViewMetrics", hasText("View Metrics"));
    }

    @Test
    void testInputDataScreen() {
        // Click the Input Data button
        clickOn("#btnInputData");

        // Verify we're on the Input Data screen
        FxAssert.verifyThat("#titleLabel", hasText("Upload Campaign Data"));

        // Go back to the Main Menu
        clickOn("#goBackButton");
        FxAssert.verifyThat("#btnInputData", hasText("Input Data"));
        FxAssert.verifyThat("#btnViewMetrics", hasText("View Metrics"));
    }

    @Test
    void testViewMetricsScreen() {
        // Click the Input Data button
        clickOn("#btnViewMetrics");

        // Verify we're on the Input Data screen
        FxAssert.verifyThat("#titleLabel", hasText("View Metrics"));

        // Go back to the Main Menu
        clickOn("#goBackButton");
        FxAssert.verifyThat("#btnInputData", hasText("Input Data"));
        FxAssert.verifyThat("#btnViewMetrics", hasText("View Metrics"));
    }

    @Test
    void testFileUploadAndDataParsing() {
        clickOn("#btnInputData");
        WaitForAsyncUtils.waitForFxEvents();

        File impressionFile = getTestFile("impression_log.csv");
        File clickFile = getTestFile("click_log.csv");
        File serverFile = getTestFile("server_log.csv");

        InputDataController controller = InputDataController.instance;

        Platform.runLater(() -> {
            controller.setImpressionFile(impressionFile);
            controller.setClickFile(clickFile);
            controller.setServerFile(serverFile);
        });
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#uploadButton");
        sleep(2000);

        clickOn("#goBackButton");
        clickOn("#btnViewMetrics");

        FxAssert.verifyThat("#totalImpressionsLabel", hasText("486104"));
        FxAssert.verifyThat("#totalClicksLabel", hasText("23923"));
        FxAssert.verifyThat("#totalConversionsLabel", hasText("2026"));
    }
    /*
    private File getTestFile(String filename) {
        URL resource = getClass().getClassLoader().getResource(filename);
        return resource != null ? Paths.get(resource.getPath()).toFile() : new File(filename);
    }
    */
    private File getTestFile(String filename) {
        File file = new File("src/test/resources/" + filename);
        if (!file.exists()) {
            throw new IllegalArgumentException("Test file not found: " + filename);
        }
        return file;
    }


    @AfterAll
    static void tearDown() {
        // Force JavaFX to exit after the tests
        Platform.exit();
    }
}
