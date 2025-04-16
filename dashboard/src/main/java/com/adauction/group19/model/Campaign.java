package com.adauction.group19.model;

import java.sql.Blob;

/**
 * Represents a campaign in the system.
 */
public class Campaign {
    private int id;
    private int userId;
    private String campaignName;
    private byte[] data;

    public Campaign() {}

    public Campaign(int userId, String campaignName, byte[] data) {
        this.userId = userId;
        this.campaignName = campaignName;
        this.data = data;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}