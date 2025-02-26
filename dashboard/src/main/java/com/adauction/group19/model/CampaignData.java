package com.adauction.group19.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the data for a campaign.
 */
public class CampaignData {
    /**
     * The impressions, clicks, and server logs for the campaign.
     */
    private List<Object[]> impressions = new ArrayList<>();
    private List<Object[]> clicks = new ArrayList<>();
    private List<Object[]> serverLogs = new ArrayList<>();

    /**
     * Adds an impression to the campaign data.
     * @param dateTime date and time of the impression
     * @param gender gender of the user
     * @param ageRange age range of the user
     * @param income income of the user
     * @param context context of the impression
     * @param impressionCost cost of the impression
     */
    public void addImpression(LocalDateTime dateTime, Gender gender, AgeRange ageRange, Income income, Context context, double impressionCost) {
        impressions.add(new Object[]{dateTime, gender, ageRange, income, context, impressionCost});
    }

    /**
     * Adds a click to the campaign data.
     * @param dateTime date and time of the click
     * @param clickCost cost of the click
     */
    public void addClick(LocalDateTime dateTime, double clickCost) {
        clicks.add(new Object[]{dateTime, clickCost});
    }

    /**
     * Adds a server log entry to the campaign data.
     * @param entryDateTime date and time of the entry
     * @param exitDateTime date and time of the exit
     * @param pagesViewed number of pages viewed
     * @param conversion whether the user converted
     */
    public void addServerLogEntry(LocalDateTime entryDateTime, LocalDateTime exitDateTime, int pagesViewed, boolean conversion) {
        serverLogs.add(new Object[]{entryDateTime, exitDateTime, pagesViewed, conversion});
    }

    /**
     * Returns all the impression data for the campaign.
     * @return The impressions data for the campaign.
     */
    public List<Object[]> getImpressions() { return impressions; }

    /**
     * Returns all the click data for the campaign.
     * @return The click data for the campaign.
     */
    public List<Object[]> getClicks() { return clicks; }

    /**
     * Returns all the server logs for the campaign.
     * @return The server logs for the campaign.
     */
    public List<Object[]> getServerLogs() { return serverLogs; }

    // Extra methods for calculating important metrics

    /**
     * Returns the total number of impressions.
     * @return the total number of impressions.
     */
    public int getTotalImpressions() {
        return impressions.size();
    }

    /**
     * Returns the total number of clicks.
     * @return the total number of clicks.
     */
    public int getTotalClicks() {
        return clicks.size();
    }

    /**
     * Returns the total number of conversions.
     * @return the total number of conversions.
     */
    public int getTotalConversions() {
        int totalConversions = 0;
        for (Object[] serverLog : serverLogs) {
            if ((boolean) serverLog[3]) {
                totalConversions++;
            }
        }
        return totalConversions;
    }
    /**
     * Returns the total number of unique users (based on unique IP or session tracking).
     * @return the total number of unique users.
     */
    public int getTotalUniques() {
        return impressions.size(); // Placeholder, should be modified if unique user tracking is available
    }

    /**
     * Returns the total number of bounces (users who viewed only 1 page).
     * @return the total number of bounces.
     */
    public int getTotalBounces() {
        int totalBounces = 0;
        for (Object[] serverLog : serverLogs) {
            int pagesViewed = (int) serverLog[2];
            if (pagesViewed == 1) {
                totalBounces++;
            }
        }
        return totalBounces;
    }

    /**
     * Returns the total cost of impressions and clicks combined.
     * @return the total cost of the campaign.
     */
    public double getTotalCost() {
        double totalCost = 0;
        for (Object[] impression : impressions) {
            totalCost += (double) impression[5];
        }
        for (Object[] click : clicks) {
            totalCost += (double) click[1];
        }
        return totalCost;
    }

    /**
     * Returns the Click-Through Rate (CTR) = (Total Clicks / Total Impressions) * 100.
     * @return the CTR percentage.
     */
    public double getCTR() {
        if (getTotalImpressions() == 0) return 0;
        return ((double) getTotalClicks() / getTotalImpressions()) * 100;
    }

    /**
     * Returns the Cost Per Acquisition (CPA) = Total Cost / Total Conversions.
     * @return the CPA value.
     */
    public double getCPA() {
        if (getTotalConversions() == 0) return 0;
        return getTotalCost() / getTotalConversions();
    }

    /**
     * Returns the Cost Per Click (CPC) = Total Cost / Total Clicks.
     * @return the CPC value.
     */
    public double getCPC() {
        if (getTotalClicks() == 0) return 0;
        return getTotalCost() / getTotalClicks();
    }

    /**
     * Returns the Cost Per Thousand Impressions (CPM) = (Total Cost / Total Impressions) * 1000.
     * @return the CPM value.
     */
    public double getCPM() {
        if (getTotalImpressions() == 0) return 0;
        return (getTotalCost() / getTotalImpressions()) * 1000;
    }

    /**
     * Returns the Bounce Rate = (Total Bounces / Total Clicks) * 100.
     * @return the bounce rate percentage.
     */
    public double getBounceRate() {
        if (getTotalClicks() == 0) return 0;
        return ((double) getTotalBounces() / getTotalClicks()) * 100;
    }

}
