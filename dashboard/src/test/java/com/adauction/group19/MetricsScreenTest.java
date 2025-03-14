package com.adauction.group19;

import com.adauction.group19.model.AgeRange;
import com.adauction.group19.model.CampaignData;
import com.adauction.group19.model.Context;
import com.adauction.group19.model.Gender;
import com.adauction.group19.model.Income;
import com.adauction.group19.service.CampaignDataStore;
import com.adauction.group19.view.ViewMetricsScreen;
import java.time.LocalDateTime;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

public class MetricsScreenTest extends ApplicationTest {

  @Override
  public void start(Stage stage) {
    // Initially load the metrics screen with empty data.
    CampaignData data = new CampaignData();
    CampaignDataStore.getInstance().setCampaignData(data);
    Scene scene = ViewMetricsScreen.getScene(stage);
    stage.setScene(scene);
    stage.show();
    stage.toFront();
  }

  @Test
  public void testMetricsScreenData() {
    // Inject dummy campaign data and force the scene to reload.
    Platform.runLater(() -> {
      CampaignData data = new CampaignData();
      LocalDateTime now = LocalDateTime.now();
      // Two impressions with distinct IDs (uniques should be 2)
      data.addImpression(now, "user1", Gender.MALE, AgeRange.AGE_25_34, Income.HIGH, Context.BLOG, 0.5);
      data.addImpression(now, "user2", Gender.FEMALE, AgeRange.AGE_35_44, Income.MEDIUM, Context.NEWS, 0.7);
      // One click (so total clicks = 1)
      data.addClick(now, 0.3);
      // One server log entry that qualifies as a bounce (pagesViewed == 1, duration 3 sec)
      data.addServerLogEntry(now, now.plusSeconds(3), 1, true);
      // Update the store
      CampaignDataStore.getInstance().setCampaignData(data);

      // Force a reload of the metrics screen by getting a new scene.
      Stage stage = (Stage) lookup("#goBackButton").query().getScene().getWindow();
      Scene newScene = ViewMetricsScreen.getScene(stage);
      stage.setScene(newScene);
    });

    // Allow time for the UI update
    sleep(500);

    // Now verify that the metrics are updated correctly.
    FxAssert.verifyThat("#lblImpressions", LabeledMatchers.hasText("(2)"));
    FxAssert.verifyThat("#lblClicks", LabeledMatchers.hasText("(1)"));
  }

  @AfterAll
  static void tearDown() {
    Platform.exit();
  }
}
