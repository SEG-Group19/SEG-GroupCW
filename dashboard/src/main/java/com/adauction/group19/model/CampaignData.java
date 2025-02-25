package com.adauction.group19.model;

public class CampaignData {
    private int impressions = 0;
    private int clicks = 0;
    private int serverLogs = 0;

    public void addImpression() { impressions++; }
    public void addClick() { clicks++; }
    public void addServerLogEntry() { serverLogs++; }

    public int getImpressions() { return impressions; }
    public int getClicks() { return clicks; }
    public int getServerLogs() { return serverLogs; }
}
