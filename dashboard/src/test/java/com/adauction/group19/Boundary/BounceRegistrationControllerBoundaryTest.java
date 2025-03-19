package com.adauction.group19.Boundary;

import com.adauction.group19.controller.BounceRegistrationController;
import com.adauction.group19.model.BounceCriteria;
import com.adauction.group19.model.CampaignData;
import com.adauction.group19.service.CampaignDataStore;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class BounceRegistrationControllerBoundaryTest extends ApplicationTest {

  private Stage stage;
  private BounceRegistrationController controller;
  private CampaignData campaignData;
  private BounceCriteria bounceCriteria;

  @BeforeEach
  public void setUp() {
    campaignData = new CampaignData();
    bounceCriteria = new BounceCriteria();
    campaignData.setBounceCriteria(bounceCriteria);
    CampaignDataStore.getInstance().setCampaignData(campaignData);
  }

  @Override
  public void start(Stage stage) throws Exception {
    this.stage = stage;

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BounceRegistration.fxml"));
    Parent root = loader.load();

    controller = loader.getController();
    controller.setStage(stage);

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    stage.toFront();
  }

  @Test
  public void testSpinnerBoundaries() {
    // Get spinner controls
    Spinner<Integer> minPagesSpinner = lookup("#minPagesViewed").queryAs(Spinner.class);
    Spinner<Integer> minTimeSpinner = lookup("#minTime").queryAs(Spinner.class);

    // Test minimum boundaries
    minPagesSpinner.getValueFactory().setValue(1);
    minTimeSpinner.getValueFactory().setValue(1);

    clickOn("#saveButton");
    sleep(300);

    assertEquals(1, bounceCriteria.getMinPagesViewed(), "Min pages viewed should be updated to 1");
    assertEquals(1, bounceCriteria.getMinTimeOnSiteSeconds(), "Min time should be updated to 1");

    // Test maximum boundaries
    minPagesSpinner.getValueFactory().setValue(100);
    minTimeSpinner.getValueFactory().setValue(100);

    clickOn("#saveButton");
    sleep(300);

    assertEquals(100, bounceCriteria.getMinPagesViewed(), "Min pages viewed should be updated to 100");
    assertEquals(100, bounceCriteria.getMinTimeOnSiteSeconds(), "Min time should be updated to 100");
  }

  @Test
  public void testCheckboxEdgeCases() {
    // Get checkbox controls
    CheckBox considerPagesCheckbox = lookup("#considerPagesViewed").queryAs(CheckBox.class);
    CheckBox considerTimeCheckbox = lookup("#considerTime").queryAs(CheckBox.class);

    // Test with both checkboxes unchecked (should still save but bounce criteria won't apply)
    considerPagesCheckbox.setSelected(false);
    considerTimeCheckbox.setSelected(false);

    clickOn("#saveButton");
    sleep(300);

    assertFalse(bounceCriteria.isConsiderPagesViewed(), "Consider pages should be unchecked");
    assertFalse(bounceCriteria.isConsiderTimeOnSite(), "Consider time should be unchecked");

    // Test with only one checked
    considerPagesCheckbox.setSelected(true);
    considerTimeCheckbox.setSelected(false);

    clickOn("#saveButton");
    sleep(300);

    assertTrue(bounceCriteria.isConsiderPagesViewed(), "Consider pages should be checked");
    assertFalse(bounceCriteria.isConsiderTimeOnSite(), "Consider time should remain unchecked");
  }

  @AfterEach
  public void cleanup() {
    FxRobot robot = new FxRobot();
    try {
      Platform.runLater(() -> {
        if (stage != null) {
          stage.hide();
        }
      });
      // Wait for the UI update to complete
      robot.sleep(200);
    } catch (Exception e) {
      // Ignore cleanup issues
    }
  }
}