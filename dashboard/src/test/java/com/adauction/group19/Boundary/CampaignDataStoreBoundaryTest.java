package com.adauction.group19.Boundary;

import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.ViewMetricsScreen;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static com.adauction.group19.Util.CampaignDataUtil.createTestCampaignData;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CampaignDataStoreBoundaryTest extends ApplicationTest {

    private Stage stage;
    private CampaignData testData;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
    }

    @Test
    public void testUpperBoundaryCampaignDataStore() {
        // Test data with 1000 days of activity
        testData = createTestCampaignData(1000);
        CampaignDataStore.getInstance().setCampaignData(testData);

        CampaignData data = CampaignDataStore.getInstance().getCampaignData();
        assertEquals(testData, data);
    }

    @Test
    public void testLowerBoundaryCampaignDataStore() {
        testData = createTestCampaignData(0);
        CampaignDataStore.getInstance().setCampaignData(testData);

        CampaignData data = CampaignDataStore.getInstance().getCampaignData();
        assertEquals(testData, data);
    }
}
