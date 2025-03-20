package com.adauction.group19.Unit;

import com.adauction.group19.App;
import com.adauction.group19.GUIStarter;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GUIStarterMainTest {

    @Test
    void testAppStartsSuccessfully() {
        GUIStarter guiStarter = new GUIStarter();
        assertNotNull(guiStarter);
    }

}
