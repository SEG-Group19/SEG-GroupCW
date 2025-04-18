package com.adauction.group19.model;

/**
 * Represents the assignment of viewers to campaigns.
 * This enables role-based access control for viewers to only see specific campaigns.
 */
public class CampaignViewerAssignment {
    private int id;
    private int campaignId;
    private int viewerId;

    public CampaignViewerAssignment() {}

    public CampaignViewerAssignment(int campaignId, int viewerId) {
        this.campaignId = campaignId;
        this.viewerId = viewerId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public int getViewerId() {
        return viewerId;
    }

    public void setViewerId(int viewerId) {
        this.viewerId = viewerId;
    }
}