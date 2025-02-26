package com.adauction.group19.view;

import com.adauction.group19.controller.MetricsScreenController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
            Graph area
             */
            NumberAxis xAxis = new NumberAxis();
            NumberAxis yAxis = new NumberAxis();
            LineChart <Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Selected Metrics Graph");

            //sample data, placeholder
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Sample Data");
            series.getData().add(new XYChart.Data<>(1, 23));
            series.getData().add(new XYChart.Data<>(2, 14));
            series.getData().add(new XYChart.Data<>(3, 15));
            series.getData().add(new XYChart.Data<>(4, 24));

            lineChart.getData().add(series);

            VBox graphPanel = new VBox(10, lineChart);
            graphPanel.setPadding(new Insets(20));
            graphPanel.setAlignment(Pos.CENTER);

            /*
            time scale slider
             */
            HBox sliderBox = new HBox(10);
            sliderBox.setAlignment(Pos.CENTER);
            sliderBox.setPadding(new Insets(10));

            Label lblTimeScale = new Label("Day: 1"); //label to display selected day

            Slider timeSlider = new Slider(1, 30, 1); // Min = 1, Max = 30, Default = 1
            timeSlider.setShowTickLabels(true);
            timeSlider.setShowTickMarks(true);
            timeSlider.setMajorTickUnit(5);
            timeSlider.setMinorTickCount(4);
            timeSlider.setSnapToTicks(true);

            timeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                    lblTimeScale.setText("Day: " + newVal.intValue()); // Ensure integer display
            });
            sliderBox.getChildren().addAll(new Label("Time Scale:"), timeSlider, lblTimeScale);

            VBox bottomControls = new VBox(10, sliderBox);
            bottomControls.setAlignment(Pos.CENTER);

            /*
            Layout Management
             */
            BorderPane root = new BorderPane(); //to be removed
            root.setLeft(metricsPanel);
            root.setCenter(graphPanel);
            root.setTop(btnBack);
            root.setBottom(bottomControls);

            return new Scene(root, 1000, 600);
        /*
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }*/
    }
}
