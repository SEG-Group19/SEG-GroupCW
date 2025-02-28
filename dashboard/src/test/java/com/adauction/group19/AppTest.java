package com.adauction.group19;

import com.adauction.group19.controller.InputDataController;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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

    @AfterAll
    static void tearDown() {
        Platform.exit(); // Properly shut down JavaFX
        try {
            Thread.sleep(500); // Small delay to let JavaFX shut down
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}