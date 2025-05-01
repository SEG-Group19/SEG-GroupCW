package com.adauction.group19.tutorial;

import com.adauction.group19.service.DatabaseManager;
import com.adauction.group19.service.UserSession;
import javafx.stage.Stage;

/**
 * Manages the application tutorial state and flow.
 * Implements the singleton pattern to ensure only one tutorial instance exists.
 */
public class TutorialManager {
    private static TutorialManager instance;
    
    private boolean tutorialActive = false;
    private boolean tutorialCompleted = false;
    private int currentStep = 0;
    private Stage currentStage;
    private TutorialOverlay currentOverlay;
    private String currentScreen = "login"; // login, register, mainMenu
    
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
    
    /**
     * Private constructor to prevent instantiation.
     */
    private TutorialManager() {
        // Check if the tutorial has been completed before
        checkTutorialStatus();
    }
    
    /**
     * Gets the singleton instance of the TutorialManager.
     * 
     * @return The TutorialManager instance
     */
    public static TutorialManager getInstance() {
        if (instance == null) {
            instance = new TutorialManager();
        }
        return instance;
    }
    
    /**
     * Gets the current screen being displayed in the tutorial.
     * 
     * @return The current screen name ("login", "register", or "mainMenu")
     */
    public String getCurrentScreen() {
        return currentScreen;
    }
    
    /**
     * Sets the current screen and updates the tutorial step accordingly.
     * 
     * @param screen The new screen name ("login", "register", or "mainMenu")
     */
    public void setCurrentScreen(String screen) {
        if (currentScreen.equals(screen)) {
            // If we're already on this screen, just make sure tutorial is visible
            showCurrentStep();
            return;
        }
        
        System.out.println("Changing tutorial screen from " + currentScreen + " to " + screen);
        
        // Change the screen and reset steps
        this.currentScreen = screen;
        resetStepForScreen();
        showCurrentStep();
    }
    
    /**
     * Resets the current step based on the current screen.
     */
    private void resetStepForScreen() {
        // Reset step counter based on current screen
        if ("login".equals(currentScreen)) {
            currentStep = LOGIN_WELCOME;
        } else if ("register".equals(currentScreen)) {
            currentStep = REGISTER_WELCOME;
        } else if ("mainMenu".equals(currentScreen)) {
            currentStep = MENU_WELCOME;
        }
        
        System.out.println("Reset to step: " + currentStep + " for screen: " + currentScreen);
    }
    
    /**
     * Checks if the current step belongs to the given screen.
     * 
     * @param screen The screen to check
     * @return true if the current step is in the specified screen, false otherwise
     */
    public boolean isStepInScreen(String screen) {
        if ("login".equals(screen)) {
            return currentStep >= LOGIN_WELCOME && currentStep <= LOGIN_BUTTON;
        } else if ("register".equals(screen)) {
            return currentStep >= REGISTER_WELCOME && currentStep <= REGISTER_BUTTON;
        } else if ("mainMenu".equals(screen)) {
            return currentStep >= MENU_WELCOME && currentStep <= MENU_SETTINGS;
        }
        return false;
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
     * Checks if the tutorial has been completed.
     * 
     * @return true if the tutorial has been completed, false otherwise
     */
    public boolean isTutorialCompleted() {
        return tutorialCompleted;
    }
    
    /**
     * Gets the current tutorial step.
     * 
     * @return The current step number
     */
    public int getCurrentStep() {
        return currentStep;
    }
    
    /**
     * Sets the current stage where the tutorial is being displayed.
     * 
     * @param stage The current JavaFX stage
     */
    public void setCurrentStage(Stage stage) {
        this.currentStage = stage;
        
        // If tutorial is active, show current step after stage change
        if (tutorialActive) {
            showCurrentStep();
        }
    }
    
    /**
     * Forces immediate update of the tutorial overlay.
     * This can be called when screens transition to force the tutorial to appear.
     */
    public void forceUpdateTutorial() {
        if (!tutorialActive) {
            System.out.println("Cannot force update tutorial - tutorial is not active");
            return;
        }
        
        System.out.println("Forcing tutorial update for screen: " + currentScreen);
        showCurrentStep();
    }
    
    /**
     * Starts the tutorial from the first step.
     */
    public void startTutorial() {
        tutorialActive = true;
        currentStep = LOGIN_WELCOME;
        currentScreen = "login";
        showCurrentStep();
    }
    
    /**
     * Advances to the next tutorial step.
     */
    public void nextStep() {
        currentStep++;
        
        // Check if we've reached the end of a screen's steps
        if ("login".equals(currentScreen) && currentStep > LOGIN_BUTTON) {
            // Do not automatically advance to register or main menu
            // Just stay at the last login step
            currentStep = LOGIN_BUTTON;
            return;
        } else if ("register".equals(currentScreen) && currentStep > REGISTER_BUTTON) {
            // Do not automatically advance to main menu
            // Just stay at the last register step
            currentStep = REGISTER_BUTTON;
            return;
        } else if (currentStep > TUTORIAL_COMPLETE) {
            endTutorial();
            return;
        }
        
        showCurrentStep();
    }
    
    /**
     * Shows the tutorial overlay for the current step.
     */
    private void showCurrentStep() {
        if (currentStage == null) {
            System.out.println("Cannot show tutorial - stage is null");
            return;
        }
        
        if (!tutorialActive) {
            System.out.println("Tutorial is not active");
            return;
        }
        
        // Remove existing overlay if it exists
        if (currentOverlay != null) {
            currentOverlay.hide();
        }
        
        // Create and show new overlay
        currentOverlay = new TutorialOverlay(currentStage, this);
        currentOverlay.showStep(currentStep);
        
        System.out.println("Showing tutorial step: " + currentStep + " (Screen: " + currentScreen + ")");
    }
    
    /**
     * Skips the tutorial and marks it as completed.
     */
    public void skipTutorial() {
        endTutorial();
    }
    
    /**
     * Ends the tutorial, cleans up, and saves the completed status.
     */
    public void endTutorial() {
        tutorialActive = false;
        tutorialCompleted = true;
        
        // Clean up overlay
        if (currentOverlay != null) {
            currentOverlay.hide();
            currentOverlay = null;
        }
        
        // Save tutorial completed status
        saveTutorialStatus();
    }
    
    /**
     * Checks if the tutorial has been completed by the current user.
     */
    private void checkTutorialStatus() {
        // This would normally check the database, but for simplicity,
        // we'll assume the tutorial hasn't been completed
        tutorialCompleted = false;
    }
    
    /**
     * Saves the tutorial completion status for the current user.
     */
    private void saveTutorialStatus() {
        // Only save if a user is logged in
        if (UserSession.getInstance().getCurrentUser() != null) {
            String username = UserSession.getInstance().getCurrentUser().getUsername();
            DatabaseManager.getInstance().saveTutorialStatus(username, true);
        }
    }
    
    /**
     * Resets the tutorial to start from the beginning.
     */
    public void resetTutorial() {
        tutorialActive = false;
        tutorialCompleted = false;
        currentStep = 0;
        
        // Clean up overlay
        if (currentOverlay != null) {
            currentOverlay.hide();
            currentOverlay = null;
        }
    }
}