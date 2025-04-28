package com.adauction.group19.Defect;

import com.adauction.group19.App;
import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.MainMenuScreen;
import com.adauction.group19.Util.TestUtils;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

public class MainMenuScreenDefectTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        // Setup authentication
        TestUtils.setupAuthenticatedSession();

        // Load main menu directly
        Scene mainMenuScene = MainMenuScreen.getScene(stage);
        stage.setScene(mainMenuScene);
        stage.show();
        stage.toFront();
        
        // Wait for UI to stabilize
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
    }

    // test if alerts work when there is no data
    @Test
    public void testViewMetricsScreenBtnWithoutData() {
        // Ensure no data is present
        CampaignDataStore.getInstance().clearCampaignData();
        
        // Find button by text instead of ID
        Button viewMetricsBtn = lookup(".primary-button").lookup((Node node) -> 
            node instanceof Button && ((Button) node).getText().contains("View Metrics")).queryButton();
        clickOn(viewMetricsBtn);
        
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
        
        verifyThat(".alert", NodeMatchers.isVisible());
    }

    @Test
    public void testViewClickCostWithoutData() {
        // Ensure no data is present
        CampaignDataStore.getInstance().clearCampaignData();
        
        // Find button by text instead of ID
        Button clickCostBtn = lookup(".primary-button").lookup((Node node) -> 
            node instanceof Button && ((Button) node).getText().contains("Click Cost")).queryButton();
        clickOn(clickCostBtn);
        
        WaitForAsyncUtils.waitForFxEvents();
        sleep(500);
        
        verifyThat(".alert", NodeMatchers.isVisible());
    }

    @AfterAll
    static void tearDown() {
        // Force JavaFX to exit after the tests
        Platform.exit();
    }
}