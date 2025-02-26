package com.adauction.group19;

import com.adauction.group19.controller.InputDataController;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class AppTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        // Launch the application in this test environment
        new App().start(stage);
        stage.toFront();
        stage.requestFocus();
    }

    @Test
    void testMainMenu() {
        // Verify that the Main Menu scene is displayed with both buttons
        FxAssert.verifyThat("#btnInputDataLabel", hasText("\uD83D\uDCCB Upload data"));
        FxAssert.verifyThat("#btnViewMetricsLabel", hasText("\uD83D\uDCC8 View metrics"));
    }

    @Test
    void testInputDataScreen() {
        // Click the Input Data button
        clickOn("#btnInputData");

        // Verify we're on the Input Data screen
        FxAssert.verifyThat("#titleLabel", hasText("Upload Campaign Data"));

        // Go back to the Main Menu
        clickOn("#goBackButton");
        FxAssert.verifyThat("#btnInputDataLabel", hasText("\uD83D\uDCCB Upload data"));
        FxAssert.verifyThat("#btnViewMetricsLabel", hasText("\uD83D\uDCC8 View metrics"));
    }

    @Test
    void testViewMetricsScreen() {
        // Click the Input Data button
        clickOn("#btnViewMetrics");

        // Verify we're on the Input Data screen
        FxAssert.verifyThat("#titleLabel", hasText("View Metrics"));

        // Go back to the Main Menu
        clickOn("#goBackButton");
        FxAssert.verifyThat("#btnInputDataLabel", hasText("\uD83D\uDCCB Upload data"));
        FxAssert.verifyThat("#btnViewMetricsLabel", hasText("\uD83D\uDCC8 View metrics"));
    }

    @Test
    public void testFileUploadAndDataParsing() {
        // Navigate to input data screen
        clickOn("#btnInputData");
        WaitForAsyncUtils.waitForFxEvents();

        // Get test file paths
        File impressionFile = getTestFile("impression_log.csv");
        File clickFile = getTestFile("click_log.csv");
        File serverFile = getTestFile("server_log.csv");

        // Access the controller instance
        InputDataController controller = InputDataController.instance;

        // Inject test files into the controller
        Platform.runLater(() -> {
            controller.setImpressionFile(impressionFile);
            controller.setClickFile(clickFile);
            controller.setServerFile(serverFile);
        });
        WaitForAsyncUtils.waitForFxEvents();

        // Click the upload button
        clickOn("#uploadButton");

        // Wait for data to upload
        sleep(2000);

        // Verify that the data was parsed correctly
        clickOn("#goBackButton");
        clickOn("#btnViewMetrics");

        // Verify that the metrics are displayed correctly
        FxAssert.verifyThat("#totalImpressionsLabel", hasText("486104"));
        FxAssert.verifyThat("#totalClicksLabel", hasText("23923"));
        FxAssert.verifyThat("#totalConversionsLabel", hasText("2026"));
    }

    private File getTestFile(String filename) {
        URL resource = getClass().getClassLoader().getResource(filename);
        return Paths.get(resource.getPath()).toFile();
    }

    @AfterAll
    static void tearDown() {
        // Force JavaFX to exit after the tests
        Platform.exit();
    }
}
