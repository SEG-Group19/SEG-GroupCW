package com.adauction.group19.Unit;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;
import org.controlsfx.tools.Utils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class BounceRegistrationControllerTest extends ApplicationTest {

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
    public void testBounceRegistrationSave() {
        clickOn("#bounceRegistrationBtn");

        Spinner<Integer> spinnerPages = lookup("#minPagesViewed").queryAs(Spinner.class);
        Spinner<Integer> spinnerTime = lookup("#minTime").queryAs(Spinner.class);

        interact(() -> {
            spinnerPages.getValueFactory().setValue(30);
            spinnerTime.getValueFactory().setValue(20);
        });

        CheckBox considerPagesViewed = lookup("#considerPagesViewed").queryAs(CheckBox.class);
        CheckBox considerTime = lookup("#considerTime").queryAs(CheckBox.class);

        interact(() -> {
            considerPagesViewed.setSelected(true);
            considerTime.setSelected(false);
        });

        clickOn("#saveButton");
        BounceCriteria updatedCriteria = data.getBounceCriteria();

        assertEquals(30, updatedCriteria.getMinPagesViewed());
        assertEquals(20, updatedCriteria.getMinTimeOnSiteSeconds());
        assertEquals(true, updatedCriteria.isConsiderPagesViewed());
        assertEquals(false, updatedCriteria.isConsiderTimeOnSite());
    }

    @Test
    public void testBounceRegistrationCancel() {
        clickOn("#bounceRegistrationBtn");

        Spinner<Integer> spinnerPages = lookup("#minPagesViewed").queryAs(Spinner.class);
        Spinner<Integer> spinnerTime = lookup("#minTime").queryAs(Spinner.class);

        interact(() -> {
            spinnerPages.getValueFactory().setValue(30);
            spinnerTime.getValueFactory().setValue(20);
        });

        CheckBox considerPagesViewed = lookup("#considerPagesViewed").queryAs(CheckBox.class);
        CheckBox considerTime = lookup("#considerTime").queryAs(CheckBox.class);

        interact(() -> {
            considerPagesViewed.setSelected(true);
            considerTime.setSelected(false);
        });

        clickOn("#cancelButton");
        BounceCriteria updatedCriteria = data.getBounceCriteria();

        // should still be default values
        assertEquals(2, updatedCriteria.getMinPagesViewed());
        assertEquals(4, updatedCriteria.getMinTimeOnSiteSeconds());
        assertEquals(true, updatedCriteria.isConsiderPagesViewed());
        assertEquals(true, updatedCriteria.isConsiderTimeOnSite());
    }

    @AfterAll
    static void tearDown() {
        Platform.exit();
    }
}
