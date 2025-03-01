package com.adauction.group19.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * This class manages the colour theme of the application.
 * It can toggle between dark and light mode and set the theme for a screen.
 */
public class ThemeManager {
    /** The current theme of the application. */
    private static boolean isDarkMode = true;

    /**
     * Returns the current theme of the application.
     * @return The current theme of the application.
     */
    public static boolean isDarkMode() {
        return isDarkMode;
    }

    /**
     * Toggles the theme of the application.
     * @param scene The scene to toggle the theme for.
     */
    public static void toggleTheme(Scene scene) {
        Parent root = scene.getRoot();
        root.getStylesheets().clear();

        isDarkMode = !isDarkMode;

        root.getStylesheets().add(ThemeManager.class.getResource("/css/main.css").toExternalForm());
        if (isDarkMode) {
            root.getStylesheets().add(ThemeManager.class.getResource("/css/dark.css").toExternalForm());
        } else {
            root.getStylesheets().add(ThemeManager.class.getResource("/css/light.css").toExternalForm());
        }

        root.applyCss();
        root.requestLayout();
    }

    /**
     * Applies the theme to the scene.
     * @param scene The scene to apply the theme to.
     */
    public static void applyTheme(Scene scene) {
        Parent root = scene.getRoot();
        root.getStylesheets().clear();

        root.getStylesheets().add(ThemeManager.class.getResource("/css/main.css").toExternalForm());
        if (isDarkMode) {
            root.getStylesheets().add(ThemeManager.class.getResource("/css/dark.css").toExternalForm());
        } else {
            root.getStylesheets().add(ThemeManager.class.getResource("/css/light.css").toExternalForm());
        }
    }
}
