package com.adauction.group19.service;

import com.adauction.group19.model.Campaign;
import com.adauction.group19.model.CampaignViewerAssignment;
import com.adauction.group19.model.User;
import com.adauction.group19.model.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Singleton class that manages campaign data operations.
 * Handles storing and retrieving campaign data, and managing viewer assignments.
 */
public class CampaignDataManager {
    private static CampaignDataManager instance;
    private final DatabaseManager dbManager;

    private CampaignDataManager() {
        dbManager = DatabaseManager.getInstance();
    }

    public static synchronized CampaignDataManager getInstance() {
        if (instance == null) {
            instance = new CampaignDataManager();
        }
        return instance;
    }

    /**
     * Adds a new campaign to the database.
     *
     * @param userId The ID of the user creating the campaign
     * @param campaignName The name of the campaign
     * @param data The campaign data as bytes
     * @return The created Campaign object if successful, null otherwise
     */
    public Campaign addCampaign(int userId, String campaignName, byte[] data) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO campaigns (user_id, campaign_name, data) VALUES (?, ?, ?)",
                 Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, campaignName);
            pstmt.setBytes(3, data);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 1) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    Campaign campaign = new Campaign(userId, campaignName, data);
                    campaign.setId(generatedKeys.getInt(1));
                    return campaign;
                }
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error adding campaign: " + e.getMessage());
            return null;
        }
    }

    public void deleteCampaign(int campaignId) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM campaigns WHERE id = ?")) {
            pstmt.setInt(1, campaignId);
            pstmt.executeUpdate();  // assignments will be auto‑removed
        } catch (SQLException e) {
            System.err.println("Error removing campaign: " + e.getMessage());
        }
    }


    /**
     * Retrieves all campaigns accessible to a user based on their role and assignments.
     *
     * @param userId The ID of the user
     * @param userRole The role of the user
     * @return List of accessible campaigns
     */
    public List<Campaign> getUserCampaigns(int userId, UserRole userRole) {
        String sql;
        if (userRole == UserRole.ADMIN) {
            sql = "SELECT * FROM campaigns";
        } else if (userRole == UserRole.USER) {
            sql = "SELECT * FROM campaigns WHERE user_id = ?";
        } else { // VIEWER
            sql = "SELECT c.* FROM campaigns c " +
                  "JOIN campaign_viewer_assignments cva ON c.id = cva.campaign_id " +
                  "WHERE cva.viewer_id = ?";
        }

        List<Campaign> campaigns = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (userRole != UserRole.ADMIN) {
                pstmt.setInt(1, userId);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Campaign campaign = new Campaign();
                campaign.setId(rs.getInt("id"));
                campaign.setUserId(rs.getInt("user_id"));
                campaign.setCampaignName(rs.getString("campaign_name"));
                campaign.setData(rs.getBytes("data"));
                campaigns.add(campaign);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving campaigns: " + e.getMessage());
        }
        return campaigns;
    }

    public void editCampaignName(int campaignId, String newName) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE campaigns SET campaign_name = ? WHERE id = ?")) {

            pstmt.setString(1, newName);
            pstmt.setInt(2, campaignId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating campaign name: " + e.getMessage());
        }
    }

    public List<User> getAllViewersAssignedToCampaign(int campaignId) {
        List<User> viewers = new ArrayList<>();
        String sql = "SELECT u.* FROM users u " +
                     "JOIN campaign_viewer_assignments cva ON u.id = cva.viewer_id " +
                     "WHERE cva.campaign_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, campaignId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User viewer = new User();
                viewer.setId(rs.getInt("id"));
                viewer.setUsername(rs.getString("username"));
                viewers.add(viewer);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving viewers assigned to campaign: " + e.getMessage());
        }
        return viewers;
    }

    public int getTotalCampaignCount() {
        String sql = "SELECT COUNT(*) FROM campaigns";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving total campaign count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Assigns a campaign to a viewer.
     *
     * @param campaignId The ID of the campaign
     * @param viewerId The ID of the viewer
     * @return true if assignment was successful, false otherwise
     */
    public boolean assignCampaignToViewer(int campaignId, int viewerId) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO campaign_viewer_assignments (campaign_id, viewer_id) VALUES (?, ?)")) {

            pstmt.setInt(1, campaignId);
            pstmt.setInt(2, viewerId);

            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error assigning campaign to viewer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Removes a campaign viewer assignment.
     *
     * @param campaignId The ID of the campaign
     * @param viewerId The ID of the viewer
     * @return true if removal was successful, false otherwise
     */
    public boolean removeCampaignViewerAssignment(int campaignId, int viewerId) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "DELETE FROM campaign_viewer_assignments WHERE campaign_id = ? AND viewer_id = ?")) {

            pstmt.setInt(1, campaignId);
            pstmt.setInt(2, viewerId);

            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error removing campaign viewer assignment: " + e.getMessage());
            return false;
        }
    }
}