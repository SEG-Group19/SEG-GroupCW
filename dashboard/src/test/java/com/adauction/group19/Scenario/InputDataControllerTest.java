package com.adauction.group19.Scenario;

import com.adauction.group19.controller.InputDataController;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.InputDataScreen;
import com.adauction.group19.view.ViewMetricsScreen;
import com.adauction.group19.Util.TestUtils;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testfx.util.NodeQueryUtils.hasText;

public class InputDataControllerTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        
        // Set up authenticated session
        TestUtils.setupAuthenticatedSession();
        
        // Load input data screen directly
        Scene scene = InputDataScreen.getScene(stage);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }

    @Test
    public void testFileUploadAndDataParsing() {
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
        sleep(3000);

        // If there's no error, then the data was parsed successfully
    }

    private File getTestFile(String filename) {
        URL resource = getClass().getResource("/" + filename);
        assertNotNull(resource, "Resource not found: " + filename);
        return Paths.get(resource.getPath()).toFile();
    }
}
