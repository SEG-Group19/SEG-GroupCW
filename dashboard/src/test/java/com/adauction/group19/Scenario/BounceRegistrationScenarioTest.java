package com.adauction.group19.Scenario;

import com.adauction.group19.controller.BounceRegistrationController;
import com.adauction.group19.controller.MetricsScreenController;
import com.adauction.group19.model.*;
import com.adauction.group19.Util.CampaignDataUtil;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.ViewMetricsScreen;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.controlsfx.tools.Utils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;


public class BounceRegistrationScenarioTest extends ApplicationTest {

    private MetricsScreenController controller;
    private CampaignData data;
    private List<Set<Enum<?>>> filters = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        data = CampaignDataUtil.createTestCampaignData(14);
        CampaignDataStore.getInstance().setCampaignData(data);

        for (int i = 0; i < 4; i++) {
            filters.add(new HashSet<>());
        }

        Scene scene = ViewMetricsScreen.getScene(stage);
        stage.setScene(scene);
        stage.show();
        stage.toFront();

        controller = (MetricsScreenController) scene.getUserData();
    }

    @Test
    public void testBounceRegistrationMinPages() {
        BounceRegistrationController bounceRegistrationController = new BounceRegistrationController();
        bounceRegistrationController.setMetricsScreenController(controller);

        BounceCriteria bounceCriteria = data.getBounceCriteria();
        bounceCriteria.setConsiderTimeOnSite(false);
        bounceCriteria.setMinPagesViewed(1);
        controller.updateGraph();

        // refresh page
        clickOn("#goBackButton");
        clickOn("#btnViewMetrics");

        FxAssert.verifyThat("#lblBounces", LabeledMatchers.hasText("(" + data.getTotalBounces(filters) + ")"));
    }

    @Test
    public void testBounceRegistrationTime() {
        BounceRegistrationController bounceRegistrationController = new BounceRegistrationController();
        bounceRegistrationController.setMetricsScreenController(controller);

        BounceCriteria bounceCriteria = data.getBounceCriteria();
        bounceCriteria.setConsiderPagesViewed(false);
        bounceCriteria.setMinTimeOnSiteSeconds(2);
        controller.updateGraph();

        // refresh page
        clickOn("#goBackButton");
        clickOn("#btnViewMetrics");

        FxAssert.verifyThat("#lblBounces", LabeledMatchers.hasText("(" + data.getTotalBounces(filters) + ")"));
    }

    @AfterAll
    static void tearDown() {
        Platform.exit();
    }
}
