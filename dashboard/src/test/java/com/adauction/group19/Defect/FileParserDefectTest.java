package com.adauction.group19.Defect;

import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.ViewMetricsScreen;
import com.adauction.group19.service.FileParserService;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.adauction.group19.Util.CampaignDataUtil.createTestCampaignData;
import static org.junit.jupiter.api.Assertions.*;

public class FileParserDefectTest extends ApplicationTest {

    private Stage stage;
    private CampaignData testData;
    private FileParserService fileParserService;
    private List<Set<Enum<?>>> filters = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        // Create test data with multiple days of activity
        testData = createTestCampaignData(14);

        fileParserService = new FileParserService();
    }

    @Test
    public void testParseDateTime() {
        // Test parsing of date and time
        String dateTimeString = "2021-01-01 12:00:00";
        LocalDateTime expectedDateTime = LocalDateTime.of(2021, 1, 1, 12, 0, 0);
        LocalDateTime actualDateTime = FileParserService.parseDateTime(dateTimeString);
        assertEquals(expectedDateTime, actualDateTime);
    }

    @Test
    public void testIncorrectImpressionFile() {
        File impressionFile = getTestFile("click_log.csv");
        File clickFile = getTestFile("click_log.csv");
        File serverFile = getTestFile("server_log.csv");

        Exception exception = assertThrows(Exception.class, () -> {
            fileParserService.parseCampaignData(impressionFile, clickFile, serverFile);
        });
    }

    @Test
    public void testIncorrectClickFile() {
        File impressionFile = getTestFile("impression_log.csv");
        File clickFile = getTestFile("impression_log.csv");
        File serverFile = getTestFile("server_log.csv");

        Exception exception = assertThrows(Exception.class, () -> {
            fileParserService.parseCampaignData(impressionFile, clickFile, serverFile);
        });
    }

    @Test
    public void testIncorrectServerFile() {
        File impressionFile = getTestFile("impression_log.csv");
        File clickFile = getTestFile("click_log.csv");
        File serverFile = getTestFile("impression_log.csv");

        Exception exception = assertThrows(Exception.class, () -> {
            fileParserService.parseCampaignData(impressionFile, clickFile, serverFile);
        });
    }

    private File getTestFile(String filename) {
        URL resource = getClass().getResource("/" + filename);
        assertNotNull(resource, "Resource not found: " + filename);
        return Paths.get(resource.getPath()).toFile();
    }

    private double roundToTwoDecimalPlaces(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
