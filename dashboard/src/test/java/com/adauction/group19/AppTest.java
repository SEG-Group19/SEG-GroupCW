package com.adauction.group19;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;

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
        FxAssert.verifyThat("#btnInputData", hasText("Input Data"));
        FxAssert.verifyThat("#btnViewMetrics", hasText("View Metrics"));
    }

    @Test
    void testInputDataScreen() {
        // TODO: Implement this test when the Input Data screen is implemented
    }

    @Test
    void testViewMetricsScreen() {
        // TODO: Implement this test when the View Metrics screen is implemented
    }

    @AfterAll
    static void tearDown() {
        // Force JavaFX to exit after the tests
        Platform.exit();
    }
}
