package com.adauction.group19.tutorial;

import com.adauction.group19.service.UserSession;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

/**
 * A component-based tutorial system that highlights specific UI elements.
 * Uses popups positioned relative to specific components rather than a full-screen overlay.
 */
public class ComponentTutorial {
    // Tutorial steps for login screen
    public static final int LOGIN_WELCOME = 0;
    public static final int LOGIN_USERNAME = 1;
    public static final int LOGIN_PASSWORD = 2;
    public static final int LOGIN_BUTTON = 3;
    
    // Tutorial steps for register screen
    public static final int REGISTER_WELCOME = 4;
    public static final int REGISTER_USERNAME = 5;
    public static final int REGISTER_PASSWORD = 6;
    public static final int REGISTER_CONFIRM_PASSWORD = 7;
    public static final int REGISTER_BUTTON = 8;
    
    // Tutorial steps for main menu
    public static final int MENU_WELCOME = 9;
    public static final int MENU_CAMPAIGN_MANAGEMENT = 10;
    public static final int MENU_VIEW_METRICS = 11;
    public static final int MENU_USER_MANAGEMENT = 12;
    public static final int MENU_SETTINGS = 13;
    public static final int TUTORIAL_COMPLETE = 14;
    
    // Tutorial steps for input data screen
    public static final int INPUT_WELCOME = 15;
    public static final int INPUT_IMPRESSION_LOG = 16;
    public static final int INPUT_CLICK_LOG = 17;
    public static final int INPUT_SERVER_LOG = 18;
    public static final int INPUT_UPLOAD_BUTTON = 19;
    
    // Tutorial steps for metrics screen
    public static final int METRICS_WELCOME = 20;
    public static final int METRICS_KPI_SECTION = 21;
    public static final int METRICS_DATE_RANGE = 22;
    public static final int METRICS_CHARTS = 23;
    public static final int METRICS_EXPORT = 24;
    private static ComponentTutorial instance;
    
    private final Map<String, List<TutorialStep>> pageSteps = new HashMap<>();
    private final Map<Node, Object> originalEffects = new HashMap<>();
    private final List<Popup> activePopups = new ArrayList<>();
    
    private Stage currentStage;
    private Scene currentScene;
    private String currentPage;
    private int currentStepIndex = 0;
    private boolean tutorialActive = false;
    
    /**
     * Represents a single step in the tutorial.
     */
    public static class TutorialStep {
        private final String componentId;
        private final String title;
        private final String message;
        private final Pos position;
        private final boolean finalStep;
        
        public TutorialStep(String componentId, String title, String message, Pos position, boolean finalStep) {
            this.componentId = componentId;
            this.title = title;
            this.message = message;
            this.position = position;
            this.finalStep = finalStep;
        }
        
        public String getComponentId() {
            return componentId;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Pos getPosition() {
            return position;
        }
        
        public boolean isFinalStep() {
            return finalStep;
        }
    }
    
    /**
     * Private constructor to enforce singleton pattern.
     */
    private ComponentTutorial() {
        definePageTutorials();
    }
    
    /**
     * Gets the singleton instance.
     * 
     * @return The ComponentTutorial instance
     */
    public static synchronized ComponentTutorial getInstance() {
        if (instance == null) {
            instance = new ComponentTutorial();
        }
        return instance;
    }
    
    /**
     * Defines all the tutorial steps for each page.
     */
    private void definePageTutorials() {
        // Login page tutorial
        List<TutorialStep> loginSteps = new ArrayList<>();
        loginSteps.add(new TutorialStep(null, "Welcome to AdVantage Dashboard!", 
                                       "This tutorial will guide you through the basic features of the application. Let's start with logging in.", 
                                       Pos.CENTER, false));
        loginSteps.add(new TutorialStep("usernameField", "Enter Your Username", 
                                      "Type your username in this field. If you don't have an account yet, you can register by clicking the Register button.", 
                                      Pos.BOTTOM_RIGHT, false));
        loginSteps.add(new TutorialStep("passwordField", "Enter Your Password", 
                                      "Type your password in this field. Make sure your password is at least 8 characters long for security.", 
                                      Pos.BOTTOM_RIGHT, false));
        loginSteps.add(new TutorialStep("loginButton", "Login to Your Account", 
                                      "Click the Login button to access your dashboard. If you've forgotten your password, contact your system administrator.", 
                                      Pos.TOP_RIGHT, true));
        pageSteps.put("login", loginSteps);
        
        // Register page tutorial
        List<TutorialStep> registerSteps = new ArrayList<>();
        registerSteps.add(new TutorialStep(null, "Register a New Account", 
                                         "Create a new account to access the AdVantage Dashboard. Fill in the required information to register.", 
                                         Pos.CENTER, false));
        registerSteps.add(new TutorialStep("usernameField", "Choose a Username", 
                                         "Enter a unique username for your account. This will be used to log in to the system.", 
                                         Pos.BOTTOM_RIGHT, false));
        registerSteps.add(new TutorialStep("passwordField", "Create a Password", 
                                         "Choose a secure password with at least 8 characters. Use a combination of letters, numbers, and symbols for better security.", 
                                         Pos.BOTTOM_RIGHT, false));
        registerSteps.add(new TutorialStep("confirmPasswordField", "Confirm Your Password", 
                                         "Re-enter your password to confirm it. Make sure both passwords match.", 
                                         Pos.BOTTOM_RIGHT, false));
        registerSteps.add(new TutorialStep("registerButton", "Complete Registration", 
                                         "Click the Register button to create your account. If successful, you'll be redirected to the login screen.", 
                                         Pos.TOP_RIGHT, true));
        pageSteps.put("register", registerSteps);
        
        // Main menu tutorial
        List<TutorialStep> mainMenuSteps = new ArrayList<>();
        mainMenuSteps.add(new TutorialStep(null, "Main Menu", 
                                          "Welcome to the main menu! From here, you can access all features of the AdVantage Dashboard.", 
                                          Pos.CENTER, false));
        // Note: Adjust these component IDs to match the actual IDs in your FXML
        mainMenuSteps.add(new TutorialStep("mainMenuButtons", "Campaign Management", 
                                          "Upload and manage your advertising campaigns. You can input campaign data, view saved campaigns, and organize your work.", 
                                          Pos.CENTER_RIGHT, false));
        mainMenuSteps.add(new TutorialStep("viewMetricsButton", "View Metrics", 
                                          "Analyze your campaign performance with various metrics and visualizations. Track impressions, clicks, conversions, and more.", 
                                          Pos.CENTER_RIGHT, false));
        mainMenuSteps.add(new TutorialStep("userManagementButton", "User Management", 
                                          "Administrators can manage user accounts, reset passwords, and set permissions. This option is only available to admin users.", 
                                          Pos.CENTER_RIGHT, false));
        mainMenuSteps.add(new TutorialStep("toggleThemeButton", "Theme Settings", 
                                          "Configure your application preferences, including theme options, data display settings, and user interface customization.", 
                                          Pos.CENTER_RIGHT, true));
        pageSteps.put("mainMenu", mainMenuSteps);
        
        // Input Data screen tutorial
        List<TutorialStep> inputDataSteps = new ArrayList<>();
        inputDataSteps.add(new TutorialStep(null, "Upload Campaign Data", 
                                         "This screen allows you to upload your campaign data files. You'll need to provide click logs, impression logs, and server logs.", 
                                         Pos.CENTER_LEFT, false));
        inputDataSteps.add(new TutorialStep("impressionFilePath", "Impression Log", 
                                         "Select your impression log file here. This file should contain information about ad impressions including demographics and context.", 
                                         Pos.CENTER_RIGHT, false));
        inputDataSteps.add(new TutorialStep("clickFilePath", "Click Log", 
                                         "Select your click log file here. This file should contain information about clicks, including cost and timestamp.", 
                                         Pos.CENTER_RIGHT, false));
        inputDataSteps.add(new TutorialStep("serverFilePath", "Server Log", 
                                         "Select your server log file here. This file should contain information about user sessions and page views.", 
                                         Pos.CENTER_RIGHT, false));
        inputDataSteps.add(new TutorialStep("uploadButton", "Upload Files", 
                                         "Click this button to upload and process your files. Once uploaded, you'll be able to analyze the campaign performance.", 
                                         Pos.BOTTOM_RIGHT, true));
        pageSteps.put("inputData", inputDataSteps);
        
        // Metrics screen tutorial
        List<TutorialStep> metricsSteps = new ArrayList<>();
        metricsSteps.add(new TutorialStep(null, "Campaign Metrics", 
                                        "This screen shows you the performance metrics for your campaign. You can analyze various aspects of your campaign here.", 
                                        Pos.CENTER_LEFT, false));
        metricsSteps.add(new TutorialStep("chkImpressions", "Key Performance Indicators", 
                                        "These checkboxes control which metrics are displayed. You can select metrics like impressions, clicks, CTR, and cost information.", 
                                        Pos.CENTER_LEFT, false));
        metricsSteps.add(new TutorialStep("lineChart", "Performance Charts", 
                                        "This chart visualizes your campaign performance over time. It updates based on the metrics you select.", 
                                        Pos.CENTER_RIGHT, false));
        metricsSteps.add(new TutorialStep("graphSettingsBtn", "Graph Settings", 
                                        "Click here to adjust your graph settings like colors, labels, and axis scaling.", 
                                        Pos.BOTTOM_CENTER, false));
        metricsSteps.add(new TutorialStep("exportCSVButton", "Export Data", 
                                        "These buttons let you export your metrics data in various formats including CSV, JSON, PDF, and image files.", 
                                        Pos.TOP_RIGHT, true));
        pageSteps.put("metrics", metricsSteps);
    }
    
    /**
     * Starts the tutorial for a specific page.
     * 
     * @param scene The scene containing the components
     * @param stage The stage where the application is displayed
     * @param pageName The name of the page to show the tutorial for
     */
    public void startTutorial(Scene scene, Stage stage, String pageName) {
        if (!pageSteps.containsKey(pageName)) {
            System.err.println("No tutorial defined for page: " + pageName);
            return;
        }
        
        this.currentScene = scene;
        this.currentStage = stage;
        this.currentPage = pageName;
        this.currentStepIndex = 0;
        this.tutorialActive = true;
        
        // Mark the page as visited so the tutorial doesn't show again
        PageVisitTracker.getInstance().markPageVisited(pageName);
        
        // Show the first step
        showCurrentStep();
    }
    
    /**
     * Checks if the tutorial is currently active.
     * 
     * @return true if the tutorial is active, false otherwise
     */
    public boolean isTutorialActive() {
        return tutorialActive;
    }
    
    /**
     * Shows the current tutorial step.
     */
    private void showCurrentStep() {
        if (!tutorialActive || currentScene == null || currentStage == null) {
            return;
        }
        
        // Clean up any existing popups
        clearPopups();
        
        // Get the current step
        List<TutorialStep> steps = pageSteps.get(currentPage);
        if (currentStepIndex >= steps.size()) {
            endTutorial();
            return;
        }
        
        TutorialStep step = steps.get(currentStepIndex);
        
        // Find the target component (if any)
        Node targetComponent = null;
        if (step.getComponentId() != null) {
            targetComponent = findComponentById(currentScene.getRoot(), step.getComponentId());
        }
        
        // Create the tooltip content
        VBox tooltipContent = createTooltipContent(step);
        
        // Show the tooltip in the right position
        Popup tooltip = new Popup();
        tooltip.getContent().add(tooltipContent);
        tooltip.setAutoHide(false);
        tooltip.setHideOnEscape(false);
        
        // Place the tooltip based on the target component and position
        final Node finalTargetComponent = targetComponent;
        final VBox finalTooltipContent = tooltipContent;
        final TutorialStep finalStep = step;
        
        Platform.runLater(() -> {
            if (finalTargetComponent != null) {
                positionTooltipNearComponent(tooltip, finalTargetComponent, finalStep.getPosition());
                highlightComponent(finalTargetComponent);
            } else {
                // Center the tooltip if no target component
                tooltip.show(currentStage);
                tooltip.setX(currentStage.getX() + (currentStage.getWidth() - finalTooltipContent.getWidth()) / 2);
                tooltip.setY(currentStage.getY() + (currentStage.getHeight() - finalTooltipContent.getHeight()) / 2);
            }
            
            // Add to active popups
            activePopups.add(tooltip);
        });
    }
    
    /**
     * Creates the tooltip content for a tutorial step.
     * 
     * @param step The tutorial step
     * @return A VBox containing the tooltip content
     */
    private VBox createTooltipContent(TutorialStep step) {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.setMaxWidth(350);
        content.setBackground(new Background(new BackgroundFill(
                Color.web("#3498db"), new CornerRadii(5), Insets.EMPTY)));
        
        // Title
        Label titleLabel = new Label(step.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setWrapText(true);
        
        // Message
        Label messageLabel = new Label(step.getMessage());
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        messageLabel.setWrapText(true);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        // For the final step, show a "Close Tutorial" button
        if (step.isFinalStep()) {
            Button closeButton = new Button("Close Tutorial");
            closeButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
            closeButton.setOnAction(e -> endTutorial());
            buttonBox.getChildren().add(closeButton);
        } else {
            // Otherwise, show Next and Skip buttons
            Button nextButton = new Button("Next");
            nextButton.setStyle("-fx-background-color: white; -fx-text-fill: #3498db; -fx-font-weight: bold;");
            nextButton.setOnAction(e -> nextStep());
            
            Button skipButton = new Button("Skip Tutorial");
            skipButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
            skipButton.setOnAction(e -> endTutorial());
            
            buttonBox.getChildren().addAll(nextButton, skipButton);
        }
        
        content.getChildren().addAll(titleLabel, messageLabel, buttonBox);
        
        // Add a drop shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setRadius(10.0);
        shadow.setColor(Color.color(0, 0, 0, 0.5));
        content.setEffect(shadow);
        
        return content;
    }
    
    /**
     * Positions a tooltip near a component.
     * 
     * @param tooltip The tooltip popup
     * @param component The target component
     * @param position The desired position
     */
    private void positionTooltipNearComponent(Popup tooltip, Node component, Pos position) {
        Bounds bounds = component.localToScreen(component.getBoundsInLocal());
        
        // Show the popup (required to get its dimensions)
        tooltip.show(currentStage);
        
        // Get the tooltip dimensions
        double tooltipWidth = tooltip.getWidth();
        double tooltipHeight = tooltip.getHeight();
        
        // Calculate position based on alignment
        double x = bounds.getMinX();
        double y = bounds.getMinY();
        
        switch (position) {
            case TOP_LEFT:
                y = bounds.getMinY() - tooltipHeight - 10;
                break;
            case TOP_CENTER:
                x = bounds.getMinX() + (bounds.getWidth() - tooltipWidth) / 2;
                y = bounds.getMinY() - tooltipHeight - 10;
                break;
            case TOP_RIGHT:
                x = bounds.getMaxX() - tooltipWidth;
                y = bounds.getMinY() - tooltipHeight - 10;
                break;
            case CENTER_LEFT:
                x = bounds.getMinX() - tooltipWidth - 10;
                y = bounds.getMinY() + (bounds.getHeight() - tooltipHeight) / 2;
                break;
            case CENTER:
                x = bounds.getMinX() + (bounds.getWidth() - tooltipWidth) / 2;
                y = bounds.getMinY() + (bounds.getHeight() - tooltipHeight) / 2;
                break;
            case CENTER_RIGHT:
                x = bounds.getMaxX() + 10;
                y = bounds.getMinY() + (bounds.getHeight() - tooltipHeight) / 2;
                break;
            case BOTTOM_LEFT:
                y = bounds.getMaxY() + 10;
                break;
            case BOTTOM_CENTER:
                x = bounds.getMinX() + (bounds.getWidth() - tooltipWidth) / 2;
                y = bounds.getMaxY() + 10;
                break;
            case BOTTOM_RIGHT:
                x = bounds.getMaxX() - tooltipWidth;
                y = bounds.getMaxY() + 10;
                break;
            default:
                // Default to CENTER_RIGHT if no position specified
                x = bounds.getMaxX() + 10;
                y = bounds.getMinY() + (bounds.getHeight() - tooltipHeight) / 2;
                break;
        }
        
        // Ensure the tooltip stays within the screen bounds
        x = Math.max(0, Math.min(x, currentStage.getX() + currentStage.getWidth() - tooltipWidth));
        y = Math.max(0, Math.min(y, currentStage.getY() + currentStage.getHeight() - tooltipHeight));
        
        // Update the tooltip position
        tooltip.setX(x);
        tooltip.setY(y);
    }
    
    /**
     * Highlights a component by adding a glow effect.
     * 
     * @param component The component to highlight
     */
    private void highlightComponent(Node component) {
        // Store the original effect
        originalEffects.put(component, component.getEffect());
        
        // Create a glow effect
        Glow glow = new Glow(0.8);
        DropShadow borderGlow = new DropShadow();
        borderGlow.setColor(Color.DODGERBLUE);
        borderGlow.setWidth(30);
        borderGlow.setHeight(30);
        borderGlow.setInput(glow);
        
        // Apply the effect
        component.setEffect(borderGlow);
        
        // Create a pulsing animation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> borderGlow.setSpread(0.3)),
                new KeyFrame(Duration.seconds(0.5), e -> borderGlow.setSpread(0.6)),
                new KeyFrame(Duration.seconds(1.0), e -> borderGlow.setSpread(0.3))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    /**
     * Restores the original effects of highlighted components.
     */
    private void restoreComponentEffects() {
        for (Map.Entry<Node, Object> entry : originalEffects.entrySet()) {
            entry.getKey().setEffect((javafx.scene.effect.Effect) entry.getValue());
        }
        originalEffects.clear();
    }
    
    /**
     * Clears all active popups.
     */
    private void clearPopups() {
        for (Popup popup : activePopups) {
            popup.hide();
        }
        activePopups.clear();
        
        // Restore original component effects
        restoreComponentEffects();
    }
    
    /**
     * Advances to the next tutorial step.
     */
    public void nextStep() {
        currentStepIndex++;
        showCurrentStep();
    }
    
    /**
     * Ends the tutorial, clearing all popups and effects.
     */
    public void endTutorial() {
        tutorialActive = false;
        clearPopups();
    }
    
    /**
     * Finds a component by its ID in the scene graph.
     * 
     * @param root The root node to search from
     * @param id The ID to search for
     * @return The component with the specified ID, or null if not found
     */
    private Node findComponentById(Node root, String id) {
        if (id == null) {
            return null;
        }
        
        // Check if this node has the ID we're looking for
        if (id.equals(root.getId())) {
            return root;
        }
        
        // If this is a parent node, search its children
        if (root instanceof Parent) {
            for (Node child : ((Parent) root).getChildrenUnmodifiable()) {
                Node result = findComponentById(child, id);
                if (result != null) {
                    return result;
                }
            }
        }
        
        // Not found
        return null;
    }
}
