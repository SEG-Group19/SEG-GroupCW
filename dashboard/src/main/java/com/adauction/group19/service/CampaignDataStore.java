package com.adauction.group19.service;

import com.adauction.group19.model.CampaignData;

/**
 * This class represents the data store for the campaign data.
 * It is used to store the campaign data and can be used to retrieve the data.
 */
public class CampaignDataStore {
    /**
     * The singleton instance of the CampaignDataStore.
     */
    private static CampaignDataStore instance;

    /**
     * The campaign data to store.
     */
    private CampaignData campaignData;

    /**
     * Private constructor to prevent direct instantiation.
     */
    private CampaignDataStore() {
    }

    /**
     * Returns the singleton instance of the CampaignDataStore.
     * @return the singleton instance of the CampaignDataStore
     */
    public static CampaignDataStore getInstance() {
        if (instance == null) {
            instance = new CampaignDataStore();
        }
        return instance;
    }

    /**
     * Sets the campaign data.
     * @param campaignData the campaign data to set
     */
    public void setCampaignData(CampaignData campaignData) {
        this.campaignData = campaignData;
    }

    /**
     * Returns the campaign data.
     * @return the campaign data
     */
    public CampaignData getCampaignData() {
        return campaignData;
    }
}
