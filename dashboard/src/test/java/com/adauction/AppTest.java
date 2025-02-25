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
    void shouldUpdateLabelWhenButtonClicked() {
        // Click the button
        clickOn("#myButton");

        // Verify that the label's text changes
        FxAssert.verifyThat("#lblMessage", hasText("Hello from button!"));
    }

    @AfterAll
    static void tearDown() {
        // Force JavaFX to exit after the tests
        Platform.exit();
    }
}
