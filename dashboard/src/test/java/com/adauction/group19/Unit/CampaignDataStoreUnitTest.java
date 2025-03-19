package com.adauction.group19.Unit;

import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.ViewMetricsScreen;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static com.adauction.group19.Util.CampaignDataUtil.createTestCampaignData;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CampaignDataStoreUnitTest extends ApplicationTest {

    private Stage stage;
    private CampaignData testData;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        // Create test data with multiple days of activity
        testData = createTestCampaignData(14);
        CampaignDataStore.getInstance().setCampaignData(testData);
    }

    @Test
    public void testCampaignDataStore() {
        CampaignData data = CampaignDataStore.getInstance().getCampaignData();
        assertEquals(testData, data);
    }

    @Test
    public void testClickLogPath() {
        CampaignDataStore.getInstance().setClickLogPath("/test/testPath");
        assertEquals("/test/testPath", CampaignDataStore.getInstance().getClickLogPath());
    }

}
