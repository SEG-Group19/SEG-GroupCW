package com.adauction.group19.Scenario;

import com.adauction.group19.controller.GraphSettingsController;
import com.adauction.group19.model.*;
import com.adauction.group19.service.CampaignDataStore;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class GraphSettingsControllerTest extends ApplicationTest {

  private Stage stage;
  private GraphSettingsController controller;
  private CampaignData campaignData;

  @BeforeEach
  public void setUp() {
    // Create test campaign data
    campaignData = new CampaignData();
    LocalDateTime now = LocalDateTime.now();

    // Add some test impressions with diverse demographics
    for (int i = 0; i < 10; i++) {
      Gender gender = i % 2 == 0 ? Gender.MALE : Gender.FEMALE;
      AgeRange age = i % 3 == 0 ? AgeRange.AGE_25_34 : AgeRange.AGE_35_44;
      Income income = i % 2 == 0 ? Income.HIGH : Income.MEDIUM;
      Context context = i % 2 == 0 ? Context.BLOG : Context.NEWS;

      campaignData.addImpression(now, "user" + i, gender, age, income, context, 0.5);
    }

    CampaignDataStore.getInstance().setCampaignData(campaignData);
  }

  @Override
  public void start(Stage stage) throws Exception {
    this.stage = stage;

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphSettings.fxml"));
    Parent root = loader.load();

    controller = loader.getController();
    controller.setStage(stage);

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    stage.toFront();
  }

  @Test
  public void testDatePickersExist() {
    // Simply verify the date pickers exist and are functional
    DatePicker startDatePicker = lookup("#startDatePicker").queryAs(DatePicker.class);
    DatePicker endDatePicker = lookup("#endDatePicker").queryAs(DatePicker.class);

    assertNotNull(startDatePicker, "Start date picker should exist");
    assertNotNull(endDatePicker, "End date picker should exist");

    // Test that we can set values
    LocalDate testDate = LocalDate.now();
    Platform.runLater(() -> {
      startDatePicker.setValue(testDate);
      endDatePicker.setValue(testDate);
    });

    WaitForAsyncUtils.waitForFxEvents();

    assertEquals(testDate, startDatePicker.getValue(), "Start date should be set correctly");
    assertEquals(testDate, endDatePicker.getValue(), "End date should be set correctly");
  }

  @Test
  public void testFilterCheckComboBoxes() {
    // Verify filter combo boxes exist
    CheckComboBox<String> genderCombo = lookup("#genderCheckCombo").queryAs(CheckComboBox.class);
    CheckComboBox<String> ageCombo = lookup("#ageCheckCombo").queryAs(CheckComboBox.class);
    CheckComboBox<String> contextCombo = lookup("#contextCheckCombo").queryAs(CheckComboBox.class);
    CheckComboBox<String> incomeCombo = lookup("#incomeCheckCombo").queryAs(CheckComboBox.class);

    assertNotNull(genderCombo, "Gender filter combo should exist");
    assertNotNull(ageCombo, "Age filter combo should exist");
    assertNotNull(contextCombo, "Context filter combo should exist");
    assertNotNull(incomeCombo, "Income filter combo should exist");

    // Check that the combo boxes have items
    assertFalse(genderCombo.getItems().isEmpty(), "Gender combo should have items");
    assertFalse(ageCombo.getItems().isEmpty(), "Age combo should have items");
    assertFalse(contextCombo.getItems().isEmpty(), "Context combo should have items");
    assertFalse(incomeCombo.getItems().isEmpty(), "Income combo should have items");
  }

  @Test
  public void testCancelButton() {
    // Test that cancel button exists and closes the window
    Stage originalStage = stage;
    assertTrue(originalStage.isShowing(), "Stage should be showing before cancel");

    // Click cancel button
    clickOn("#cancelButton");

    // Wait for UI updates
    WaitForAsyncUtils.waitForFxEvents();
    sleep(300);

    // Check that stage is closed
    assertFalse(originalStage.isShowing(), "Stage should be closed after cancel");
  }


  @AfterEach
  public void cleanup() {
    FxRobot robot = new FxRobot();
    try {
      Platform.runLater(() -> {
        if (stage != null && stage.isShowing()) {
          stage.hide();
        }
      });
      robot.sleep(200);
    } catch (Exception e) {
      // Ignore cleanup issues
    }
  }
}