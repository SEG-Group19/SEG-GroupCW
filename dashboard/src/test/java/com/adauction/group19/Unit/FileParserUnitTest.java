package com.adauction.group19.Unit;

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileParserUnitTest extends ApplicationTest {

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
    public void testFileParsing() {
        File impressionFile = getTestFile("impression_log.csv");
        File clickFile = getTestFile("click_log.csv");
        File serverFile = getTestFile("server_log.csv");
        CampaignData campaignData;
        try {
            campaignData = fileParserService.parseCampaignData(impressionFile, clickFile, serverFile);
        } catch (Exception e) {
            e.printStackTrace();
            campaignData = null;
        }

        assertNotNull(campaignData, "Campaign data is null");
        assertEquals(486104, campaignData.getTotalImpressions(filters));
        assertEquals(23923, campaignData.getTotalClicks(filters));
        assertEquals(439832, campaignData.getTotalUniques(filters));
        assertEquals(8665, campaignData.getTotalBounces(filters));
        assertEquals(2026, campaignData.getTotalConversions(filters));
        assertEquals(4.92, roundToTwoDecimalPlaces(campaignData.getCTR(filters)));
        assertEquals(58.29, roundToTwoDecimalPlaces(campaignData.getCPA(filters)));
        assertEquals(242.95, roundToTwoDecimalPlaces(campaignData.getCPM(filters)));
        assertEquals(4.94, roundToTwoDecimalPlaces(campaignData.getCPC(filters)));
        assertEquals(36.22, roundToTwoDecimalPlaces(campaignData.getBounceRate(filters)));
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
