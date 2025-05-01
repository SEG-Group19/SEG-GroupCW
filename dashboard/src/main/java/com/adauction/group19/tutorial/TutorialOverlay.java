package com.adauction.group19.tutorial;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.net.URL;

/**
 * Visual overlay component for the tutorial system.
 * Creates a semi-transparent overlay with tooltips and highlighted areas.
 */
public class TutorialOverlay {
    private Stage parentStage;
    private Stage overlayStage;
    private TutorialManager tutorialManager;
    private VBox tooltipBox;
    
    /**
     * Constructs a new tutorial overlay.
     * 
     * @param parentStage The parent stage to overlay
     * @param tutorialManager The tutorial manager
     */
    public TutorialOverlay(Stage parentStage, TutorialManager tutorialManager) {
        this.parentStage = parentStage;
        this.tutorialManager = tutorialManager;
        createOverlayStage();
    }
    
    /**
     * Creates the overlay stage.
     */
    private void createOverlayStage() {
        overlayStage = new Stage();
        overlayStage.initOwner(parentStage);
        overlayStage.initStyle(StageStyle.TRANSPARENT);
        overlayStage.initModality(Modality.NONE);
        
        // Position and size the overlay to match the parent stage
        overlayStage.setX(parentStage.getX());
        overlayStage.setY(parentStage.getY());
        overlayStage.setWidth(parentStage.getWidth());
        overlayStage.setHeight(parentStage.getHeight());
        
        // Make the overlay follow the parent stage if it moves
        parentStage.xProperty().addListener((obs, oldVal, newVal) -> overlayStage.setX(newVal.doubleValue()));
        parentStage.yProperty().addListener((obs, oldVal, newVal) -> overlayStage.setY(newVal.doubleValue()));
        parentStage.widthProperty().addListener((obs, oldVal, newVal) -> overlayStage.setWidth(newVal.doubleValue()));
        parentStage.heightProperty().addListener((obs, oldVal, newVal) -> overlayStage.setHeight(newVal.doubleValue()));
    }
    
    /**
     * Shows the tutorial step with the specified ID.
     * 
     * @param stepId The ID of the step to show
     */
    public void showStep(int stepId) {
        // Create the base overlay pane
        StackPane overlayPane = new StackPane();
        overlayPane.getStyleClass().add("tutorial-overlay");
        
        // Create tooltip content based on step ID
        createTooltipForStep(stepId, overlayPane);
        
        // Create the scene and show the overlay
        Scene overlayScene = new Scene(overlayPane);
        overlayScene.setFill(Color.TRANSPARENT);
        
        // Apply CSS styling
        URL cssUrl = getClass().getResource("/css/tutorial.css");
        if (cssUrl != null) {
            overlayScene.getStylesheets().add(cssUrl.toExternalForm());
        }
        
        overlayStage.setScene(overlayScene);
        overlayStage.show();
        
        // Bring parent stage to front to ensure interaction works
        parentStage.toFront();
        // Then bring overlay back to front
        overlayStage.toFront();
    }
    
    /**
     * Creates the appropriate tooltip for the current tutorial step.
     * 
     * @param stepId The step ID
     * @param overlayPane The overlay pane to add the tooltip to
     */
    private void createTooltipForStep(int stepId, StackPane overlayPane) {
        // Create tooltip box
        tooltipBox = new VBox(10);
        tooltipBox.setAlignment(Pos.CENTER);
        tooltipBox.setPadding(new Insets(20));
        tooltipBox.setMaxWidth(400);
        tooltipBox.getStyleClass().add("tutorial-tooltip");
        
        Label titleLabel = new Label();
        titleLabel.getStyleClass().add("tutorial-title");
        
        Label descriptionLabel = new Label();
        descriptionLabel.getStyleClass().add("tutorial-description");
        
        // Create button box with appropriate buttons based on step
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getStyleClass().add("button-box");
        
        // For the final step of any screen, show a "Close Tutorial" button
        if (stepId == TutorialManager.LOGIN_BUTTON || 
            stepId == TutorialManager.REGISTER_BUTTON || 
            stepId == TutorialManager.MENU_SETTINGS || 
            stepId == TutorialManager.TUTORIAL_COMPLETE) {
            
            Button closeButton = new Button("Close Tutorial");
            closeButton.getStyleClass().add("tutorial-finish-button");
            closeButton.setOnAction(e -> tutorialManager.skipTutorial());
            buttonBox.getChildren().add(closeButton);
            
        } else {
            // For normal steps, show Next and Skip buttons
            Button nextButton = new Button("Next");
            nextButton.getStyleClass().add("tutorial-next-button");
            nextButton.setOnAction(e -> tutorialManager.nextStep());
            
            Button skipButton = new Button("Skip Tutorial");
            skipButton.getStyleClass().add("tutorial-skip-button");
            skipButton.setOnAction(e -> tutorialManager.skipTutorial());
            
            buttonBox.getChildren().addAll(nextButton, skipButton);
        }
        
        tooltipBox.getChildren().addAll(titleLabel, descriptionLabel, buttonBox);
        
        // Add drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.5));
        tooltipBox.setEffect(dropShadow);
        
        // Set content based on step
        switch (stepId) {
            case TutorialManager.LOGIN_WELCOME:
                titleLabel.setText("Welcome to AdVantage Dashboard!");
                descriptionLabel.setText("This tutorial will guide you through the basic features of the application. Let's start with logging in.");
                setTooltipPosition(overlayPane, tooltipBox, Pos.CENTER_LEFT);
                break;
                
            case TutorialManager.LOGIN_USERNAME:
                titleLabel.setText("Enter Your Username");
                descriptionLabel.setText("Type your username in this field. If you don't have an account yet, you can register by clicking the Register button.");
                // Highlight username field - in a real implementation, you would locate this element
                setTooltipPosition(overlayPane, tooltipBox, Pos.CENTER_RIGHT);
                addHighlightCircle(overlayPane, 300, 250, 50); // Example coordinates
                break;
                
            case TutorialManager.LOGIN_PASSWORD:
                titleLabel.setText("Enter Your Password");
                descriptionLabel.setText("Type your password in this field. Make sure your password is at least 8 characters long for security.");
                setTooltipPosition(overlayPane, tooltipBox, Pos.CENTER_RIGHT);
                addHighlightCircle(overlayPane, 300, 300, 50); // Example coordinates
                break;
                
            case TutorialManager.LOGIN_BUTTON:
                titleLabel.setText("Login to Your Account");
                descriptionLabel.setText("Click the Login button to access your dashboard. If you've forgotten your password, contact your system administrator.");
                setTooltipPosition(overlayPane, tooltipBox, Pos.CENTER_RIGHT);
                addHighlightCircle(overlayPane, 300, 350, 50); // Example coordinates
                break;
            
            // Register screen tutorial steps
            case TutorialManager.REGISTER_WELCOME:
                titleLabel.setText("Register a New Account");
                descriptionLabel.setText("Create a new account to access the AdVantage Dashboard. Fill in the required information to register.");
                setTooltipPosition(overlayPane, tooltipBox, Pos.CENTER_LEFT);
                break;
                
            case TutorialManager.REGISTER_USERNAME:
                titleLabel.setText("Choose a Username");
                descriptionLabel.setText("Enter a unique username for your account. This will be used to log in to the system.");
                setTooltipPosition(overlayPane, tooltipBox, Pos.CENTER_RIGHT);
                addHighlightCircle(overlayPane, 300, 250, 50); // Example coordinates
                break;
                
            case TutorialManager.REGISTER_PASSWORD:
                titleLabel.setText("Create a Password");
                descriptionLabel.setText("Choose a secure password with at least 8 characters. Use a combination of letters, numbers, and symbols for better security.");
                setTooltipPosition(overlayPane, tooltipBox, Pos.CENTER_RIGHT);
                addHighlightCircle(overlayPane, 300, 300, 50); // Example coordinates
                break;
                
            case TutorialManager.REGISTER_CONFIRM_PASSWORD:
                titleLabel.setText("Confirm Your Password");
                descriptionLabel.setText("Re-enter your password to confirm it. Make sure both passwords match.");
                setTooltipPosition(overlayPane, tooltipBox, Pos.CENTER_RIGHT);
                addHighlightCircle(overlayPane, 300, 350, 50); // Example coordinates
                break;
                
            case TutorialManager.REGISTER_BUTTON:
                titleLabel.setText("Complete Registration");
                descriptionLabel.setText("Click the Register button to create your account. If successful, you'll be redirected to the login screen.");
                setTooltipPosition(overlayPane, tooltipBox, Pos.BOTTOM_CENTER);
                addHighlightCircle(overlayPane, 300, 400, 50); // Example coordinates
                break;
                
            // Main menu tutorial steps
            case TutorialManager.MENU_WELCOME:
                titleLabel.setText("Main Menu");
                descriptionLabel.setText("Welcome to the main menu! From here, you can access all features of the AdVantage Dashboard.");
                setTooltipPosition(overlayPane, tooltipBox, Pos.CENTER_LEFT);
                break;
                
            case TutorialManager.MENU_CAMPAIGN_MANAGEMENT:
                titleLabel.setText("Campaign Management");
                descriptionLabel.setText("Upload and manage your advertising campaigns. You can input campaign data, view saved campaigns, and organize your work.");
                setTooltipPosition(overlayPane, tooltipBox, Pos.CENTER_RIGHT);
                addHighlightCircle(overlayPane, 300, 200, 50); // Example coordinates
                break;
                
            case TutorialManager.MENU_VIEW_METRICS:
                titleLabel.setText("View Metrics");
                descriptionLabel.setText("Analyze your campaign performance with various metrics and visualizations. Track impressions, clicks, conversions, and more.");
                setTooltipPosition(overlayPane, tooltipBox, Pos.CENTER_RIGHT);
                addHighlightCircle(overlayPane, 300, 260, 50); // Example coordinates
                break;
                
            case TutorialManager.MENU_USER_MANAGEMENT:
                titleLabel.setText("User Management");
                descriptionLabel.setText("Administrators can manage user accounts, reset passwords, and set permissions. This option is only available to admin users.");
                setTooltipPosition(overlayPane, tooltipBox, Pos.CENTER_RIGHT);
                addHighlightCircle(overlayPane, 300, 320, 50); // Example coordinates
                break;
                
            case TutorialManager.MENU_SETTINGS:
                titleLabel.setText("Settings");
                descriptionLabel.setText("Configure your application preferences, including theme options, data display settings, and user interface customization.");
                setTooltipPosition(overlayPane, tooltipBox, Pos.CENTER_RIGHT);
                addHighlightCircle(overlayPane, 300, 380, 50); // Example coordinates
                break;
                
            case TutorialManager.TUTORIAL_COMPLETE:
                titleLabel.setText("Tutorial Complete!");
                descriptionLabel.setText("Great job! You've completed the tutorial. Feel free to explore the application on your own, or check the help menu for more guidance.");
                setTooltipPosition(overlayPane, tooltipBox, Pos.CENTER_LEFT);
                break;
        }
    }
    
    /**
     * Sets the position of the tooltip within the overlay.
     * 
     * @param overlayPane The overlay pane
     * @param tooltipBox The tooltip box to position
     * @param position The desired position
     */
    private void setTooltipPosition(StackPane overlayPane, VBox tooltipBox, Pos position) {
        overlayPane.getChildren().add(tooltipBox);
        StackPane.setAlignment(tooltipBox, position);
        
        // Add margins based on position to ensure tooltips don't block UI elements
        if (position == Pos.CENTER_RIGHT) {
            StackPane.setMargin(tooltipBox, new Insets(0, 50, 0, 0));
        } else if (position == Pos.CENTER_LEFT) {
            StackPane.setMargin(tooltipBox, new Insets(0, 0, 0, 50));
        } else if (position == Pos.BOTTOM_CENTER) {
            StackPane.setMargin(tooltipBox, new Insets(0, 0, 50, 0));
        } else if (position == Pos.TOP_CENTER) {
            StackPane.setMargin(tooltipBox, new Insets(50, 0, 0, 0));
        }
    }
    
    /**
     * Adds a highlight circle to the overlay to focus attention on a UI element.
     * 
     * @param overlayPane The overlay pane
     * @param centerX The X coordinate of the circle center
     * @param centerY The Y coordinate of the circle center
     * @param radius The radius of the circle
     */
    private void addHighlightCircle(StackPane overlayPane, double centerX, double centerY, double radius) {
        // Create a rectangle that covers the entire overlay
        Rectangle darkRect = new Rectangle(0, 0, overlayStage.getWidth(), overlayStage.getHeight());
        darkRect.setFill(Color.color(0, 0, 0, 0.5));
        
        // Create a circle for the cutout
        Circle circle = new Circle(centerX, centerY, radius);
        
        // Create an animated spotlight circle for visual emphasis
        Circle spotlightCircle = new Circle(centerX, centerY, radius + 5);
        spotlightCircle.getStyleClass().add("spotlight-circle");
        
        // Add the spotlight circle to the overlay
        overlayPane.getChildren().add(spotlightCircle);
        
        // Use the circle as a clip on the rectangle
        // This creates a "spotlight" effect with the circle being transparent
        darkRect.setClip(circle);
        
        // Add the spotlight effect to the overlay
        overlayPane.getChildren().add(0, darkRect);
    }
    
    /**
     * Hides the overlay.
     */
    public void hide() {
        if (overlayStage != null) {
            overlayStage.hide();
        }
    }
}