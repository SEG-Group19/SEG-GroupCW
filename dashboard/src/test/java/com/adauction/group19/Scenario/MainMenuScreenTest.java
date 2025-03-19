package com.adauction.group19.Scenario;

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

import java.util.List;

import static com.adauction.group19.Util.CampaignDataUtil.createTestCampaignData;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;


public class MainMenuScreenTest extends ApplicationTest {

    private Stage stage;
    private CampaignData testData = createTestCampaignData(14);

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        new App().start(stage);
        stage.toFront();
        stage.requestFocus();

        CampaignDataStore.getInstance().setCampaignData(testData);
    }

    @Test
    public void testViewInput() {
        clickOn("#btnInputData");
    }

    @Test
    public void testViewMetricsScreenBtn() {
        clickOn("#btnViewMetrics");
    }

    @Test
    public void testViewClickCost() {
        clickOn("#btnClickCost");
    }

    @Test
    public void testToggleTheme() {
        Parent root = stage.getScene().getRoot();

        List<String> sceneStylesheets = root.getStylesheets();

        System.out.println(sceneStylesheets);

        boolean containsStylesheet = sceneStylesheets.stream().anyMatch(s -> s.endsWith("main.css"))
                && sceneStylesheets.stream().anyMatch(s -> s.endsWith("dark.css"));

        assertTrue(containsStylesheet, "Scene should have both main.css and dark.css stylesheets applied");

        clickOn("#toggleThemeButton");

        containsStylesheet = sceneStylesheets.stream().anyMatch(s -> s.endsWith("main.css"))
                && sceneStylesheets.stream().anyMatch(s -> s.endsWith("light.css"));

        assertTrue(containsStylesheet, "Scene should have both main.css and light.css stylesheets applied");
    }

    @AfterAll
    static void tearDown() {
        // Force JavaFX to exit after the tests
        Platform.exit();
    }
}
