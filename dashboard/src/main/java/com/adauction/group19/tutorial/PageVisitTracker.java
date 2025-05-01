package com.adauction.group19.tutorial;

import com.adauction.group19.service.DatabaseManager;
import com.adauction.group19.service.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Tracks which pages a user has visited in order to show tutorials only on first visit.
 * Implements the singleton pattern.
 */
public class PageVisitTracker {
    private static PageVisitTracker instance;
    
    private final Set<String> visitedPages = new HashSet<>();
    private boolean loaded = false;
    
    /**
     * Private constructor to enforce singleton pattern.
     */
    private PageVisitTracker() {
        // Create the database table if it doesn't exist
        initDatabase();
    }
    
    /**
     * Gets the singleton instance.
     * 
     * @return The PageVisitTracker instance
     */
    public static synchronized PageVisitTracker getInstance() {
        if (instance == null) {
            instance = new PageVisitTracker();
        }
        return instance;
    }
    
    /**
     * Initializes the database table for storing page visits.
     */
    private void initDatabase() {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS page_visits (" +
                         "id INT AUTO_INCREMENT PRIMARY KEY, " +
                         "user_id INT NOT NULL, " +
                         "page_name VARCHAR(50) NOT NULL, " +
                         "UNIQUE (user_id, page_name), " +
                         "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";
            
            conn.createStatement().execute(sql);
        } catch (SQLException e) {
            System.err.println("Error initializing page_visits table: " + e.getMessage());
        }
    }
    
    /**
     * Loads the user's visited pages from the database.
     */
    public void loadUserProgress() {
        if (UserSession.getInstance().getCurrentUser() == null) {
            return;
        }
        
        visitedPages.clear();
        loaded = false;
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "SELECT page_name FROM page_visits WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                visitedPages.add(rs.getString("page_name"));
            }
            
            loaded = true;
            System.out.println("Loaded visited pages: " + visitedPages);
        } catch (SQLException e) {
            System.err.println("Error loading page visits: " + e.getMessage());
        }
    }
    
    /**
     * Checks if the user has visited a page before.
     * 
     * @param pageName The name of the page to check
     * @return true if the page has been visited, false otherwise
     */
    public boolean hasVisitedPage(String pageName) {
        // Make sure the user's visited pages are loaded
        if (!loaded && UserSession.getInstance().getCurrentUser() != null) {
            loadUserProgress();
        }
        
        return visitedPages.contains(pageName);
    }
    
    /**
     * Marks a page as visited by the current user.
     * 
     * @param pageName The name of the page to mark as visited
     */
    public void markPageVisited(String pageName) {
        if (UserSession.getInstance().getCurrentUser() == null) {
            return;
        }
        
        visitedPages.add(pageName);
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "INSERT INTO page_visits (user_id, page_name) VALUES (?, ?) " +
                         "ON DUPLICATE KEY UPDATE page_name = page_name";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());
            stmt.setString(2, pageName);
            
            stmt.executeUpdate();
            System.out.println("Marked page as visited: " + pageName);
        } catch (SQLException e) {
            System.err.println("Error marking page as visited: " + e.getMessage());
        }
    }
    
    /**
     * Resets the tutorial progress for the current user.
     */
    public void resetProgress() {
        if (UserSession.getInstance().getCurrentUser() == null) {
            return;
        }
        
        visitedPages.clear();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "DELETE FROM page_visits WHERE user_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());
            
            stmt.executeUpdate();
            System.out.println("Reset tutorial progress for user");
        } catch (SQLException e) {
            System.err.println("Error resetting tutorial progress: " + e.getMessage());
        }
    }
}
