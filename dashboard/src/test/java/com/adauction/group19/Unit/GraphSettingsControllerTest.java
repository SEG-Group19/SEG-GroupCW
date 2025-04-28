package com.adauction.group19.Unit;

import com.adauction.group19.controller.BounceRegistrationController;
import com.adauction.group19.controller.GraphSettingsController;
import com.adauction.group19.controller.MetricsScreenController;
import com.adauction.group19.model.*;
import com.adauction.group19.Util.TestUtils;
import com.adauction.group19.Util.CampaignDataUtil;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.ViewMetricsScreen;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.tools.Utils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.junit.jupiter.api.Assertions.*;


public class GraphSettingsControllerTest extends ApplicationTest {

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
    public void testGraphSettingsSave() {
        clickOn("#graphSettingsBtn");

        DatePicker startDatePicker = lookup("#startDatePicker").queryAs(DatePicker.class);
        DatePicker endDatePicker = lookup("#endDatePicker").queryAs(DatePicker.class);

        interact(() -> {
            startDatePicker.setValue(LocalDateTime.now().toLocalDate());
            endDatePicker.setValue(LocalDateTime.now().minusDays(7).toLocalDate());
        });

        CheckComboBox<String> ageCheckCombo = lookup("#ageCheckCombo").queryAs(CheckComboBox.class);
        CheckComboBox<String> genderCheckCombo = lookup("#genderCheckCombo").queryAs(CheckComboBox.class);
        CheckComboBox<String> contextCheckCombo = lookup("#contextCheckCombo").queryAs(CheckComboBox.class);
        CheckComboBox<String> incomeCheckCombo = lookup("#incomeCheckCombo").queryAs(CheckComboBox.class);
        CheckComboBox<String> metricCheckCombo = lookup("#metricCheckCombo").queryAs(CheckComboBox.class);

        interact(() -> {
            // Select at least one metric first - this is crucial!
            metricCheckCombo.getCheckModel().check("Impressions");

            // Then select the filters
            genderCheckCombo.getCheckModel().check("Male");
            ageCheckCombo.getCheckModel().check("Under 25");
            contextCheckCombo.getCheckModel().check("News");
            incomeCheckCombo.getCheckModel().check("Medium");
            incomeCheckCombo.getCheckModel().check("Low");
        });

        interact(() -> {
            GraphSettingsController.instance.handleSave();
        });

        // Get the filterMap from the controller
        Map<String, List<Set<Enum<?>>>> filterMap = controller.getFilterMap();

        // Check filters for the "Impressions" metric
        List<Set<Enum<?>>> impressionsFilters = filterMap.get("Impressions");

        // Check that the filters are set correctly
        assertTrue(impressionsFilters.get(0).contains(Gender.MALE));
        assertFalse(impressionsFilters.get(0).contains(Gender.FEMALE));

        assertTrue(impressionsFilters.get(1).contains(AgeRange.AGE_25_MINUS));

        assertTrue(impressionsFilters.get(2).contains(Income.MEDIUM));
        assertTrue(impressionsFilters.get(2).contains(Income.LOW));
        assertFalse(impressionsFilters.get(2).contains(Income.HIGH));

        assertTrue(impressionsFilters.get(3).contains(Context.NEWS));
    }

    @Test
    public void testGraphSettingsCancel() {
        clickOn("#graphSettingsBtn");

        DatePicker startDatePicker = lookup("#startDatePicker").queryAs(DatePicker.class);
        DatePicker endDatePicker = lookup("#endDatePicker").queryAs(DatePicker.class);

        interact(() -> {
            startDatePicker.setValue(LocalDateTime.now().toLocalDate());
            endDatePicker.setValue(LocalDateTime.now().minusDays(7).toLocalDate());
        });

        CheckComboBox<String> ageCheckCombo = lookup("#ageCheckCombo").queryAs(CheckComboBox.class);
        CheckComboBox<String> genderCheckCombo = lookup("#genderCheckCombo").queryAs(CheckComboBox.class);
        CheckComboBox<String> contextCheckCombo = lookup("#contextCheckCombo").queryAs(CheckComboBox.class);
        CheckComboBox<String> incomeCheckCombo = lookup("#incomeCheckCombo").queryAs(CheckComboBox.class);

        interact(() -> {
            genderCheckCombo.getCheckModel().check("Male");
            ageCheckCombo.getCheckModel().check("Under 25");
            contextCheckCombo.getCheckModel().check("News");
            incomeCheckCombo.getCheckModel().check("Medium");
            incomeCheckCombo.getCheckModel().check("Low");
        });

        interact(() -> {
            GraphSettingsController.instance.handleCancel(null);
        });
        //List<Set<Enum<?>>> filters = controller.getFilters();

        assertTrue(filters.get(0).isEmpty());
        assertTrue(filters.get(1).isEmpty());
        assertTrue(filters.get(2).isEmpty());
        assertTrue(filters.get(3).isEmpty());
    }

    @AfterAll
    static void tearDown() {
        Platform.exit();
    }
}
