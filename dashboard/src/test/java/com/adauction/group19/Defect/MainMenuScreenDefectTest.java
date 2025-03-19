package com.adauction.group19.Defect;

import com.adauction.group19.App;
import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.MainMenuScreen;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import java.util.List;

import static com.adauction.group19.Util.CampaignDataUtil.createTestCampaignData;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;


public class MainMenuScreenDefectTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        new App().start(stage);
        stage.toFront();
        stage.requestFocus();
    }

    // test if alerts work when there is no data
    @Test
    public void testViewMetricsScreenBtnWithoutData() {
        clickOn("#btnViewMetrics");
        verifyThat(".alert", NodeMatchers.isVisible());
    }

    @Test
    public void testViewClickCostWithoutData() {
        clickOn("#btnClickCost");
        verifyThat(".alert", NodeMatchers.isVisible());
    }

    @AfterAll
    static void tearDown() {
        // Force JavaFX to exit after the tests
        Platform.exit();
    }
}
