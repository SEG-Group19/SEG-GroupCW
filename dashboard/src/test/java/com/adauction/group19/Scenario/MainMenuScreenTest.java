package com.adauction.group19.Scenario;

import com.adauction.group19.view.MainMenuScreen;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;

public class MainMenuScreenTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Scene scene = MainMenuScreen.getScene(stage);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }


}
