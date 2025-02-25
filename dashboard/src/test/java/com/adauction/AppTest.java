package com.adauction;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
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
        // Click the Input Data button
        clickOn("#btnInputData");

        // Verify that the Input Data screen is displayed
        FxAssert.verifyThat("#titleLabel", hasText("Input Data"));

        // Click the Back button
        clickOn("#btnBack");

        // Verify that the Main Menu screen is displayed
        FxAssert.verifyThat("#btnInputData", hasText("Input Data"));
        FxAssert.verifyThat("#btnViewMetrics", hasText("View Metrics"));
    }

    @Test
    void testViewMetricsScreen() {
        // Click the View Metrics button
        clickOn("#btnViewMetrics");

        // Verify that the View Metrics screen is displayed
        FxAssert.verifyThat("#titleLabel", hasText("View Metrics"));

        // Click the Back button
        clickOn("#btnBack");

        // Verify that the Main Menu screen is displayed
        FxAssert.verifyThat("#btnInputData", hasText("Input Data"));
        FxAssert.verifyThat("#btnViewMetrics", hasText("View Metrics"));
    }

    @AfterAll
    static void tearDown() {
        // Force JavaFX to exit after the tests
        Platform.exit();
    }
}
