package com.adauction.group19.view;

import com.adauction.group19.controller.MetricsScreenController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class represents the View Metrics screen.
 */
public class ViewMetricsScreen {
    private static final String FXML_PATH = "/fxml/ViewMetricsScreen.fxml";
    /**
     * Returns the View Metrics screen.
     * @param stage The stage to set the scene on.
     * @return The View Metrics screen.
     */
    public static Scene getScene(Stage stage) {
        //try {
            /*
            FXMLLoader loader = new FXMLLoader(ViewMetricsScreen.class.getResource(FXML_PATH));
            Parent root = loader.load();

            // Get the campaign data using the controller
            MetricsScreenController controller = loader.getController();
            controller.loadCampaignData();
            controller.setStage(stage);
             */
            /*
            Back Button
             */
            Button btnBack = new Button("Back");
            btnBack.setOnAction(e -> stage.setScene(MainMenu.getScene(stage)));

            /*
            Metrics CheckBoxes(left panel)
             */
            VBox metricsPanel = new VBox(10);
            metricsPanel.setPadding(new Insets(20));
            metricsPanel.setStyle("-fx-background-color: #F4F4F4;"); // Light gray background
            metricsPanel.setAlignment(Pos.TOP_LEFT);
            metricsPanel.setPrefWidth(250);

            Label lblMetrics = new Label("Select Metrics:");
            CheckBox chkImpressions = new CheckBox("Number of Impressions");
            CheckBox chkClicks = new CheckBox("Number of Clicks");
            CheckBox chkUniques = new CheckBox("Number of Uniques");
            CheckBox chkBounces = new CheckBox("Number of Bounces");
            CheckBox chkConversions = new CheckBox("Number of Conversions");
            CheckBox chkTotalCost = new CheckBox("Total Cost");
            CheckBox chkCTR = new CheckBox("CTR");
            CheckBox chkCPA = new CheckBox("CPA");
            CheckBox chkCPC = new CheckBox("CPC");
            CheckBox chkCPM = new CheckBox("CPM");
            CheckBox chkBounceRate = new CheckBox("Bounce Rate");

            //add checkboxes to the VBox
            metricsPanel.getChildren().addAll(lblMetrics, chkImpressions, chkClicks, chkUniques, chkBounces,
                chkConversions, chkTotalCost, chkCTR, chkCPA, chkCPC, chkCPM, chkBounceRate);


            /*
            Layout Management
             */
            BorderPane root = new BorderPane(); //to be removed
            root.setLeft(metricsPanel);
            root.setTop(btnBack);

            return new Scene(root, 1000, 600);
        /*
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }*/
    }
}
