package com.adauction.group19.model;

import java.time.Duration;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public void addImpression(LocalDateTime dateTime, String id, Gender gender, AgeRange ageRange, Income income, Context context, double impressionCost) {
        // Now, index 0: dateTime, index 1: id, index 2: gender, index 3: ageRange,
        // index 4: income, index 5: context, index 6: impressionCost.
        impressions.add(new Object[]{dateTime, id, gender, ageRange, income, context, impressionCost});
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
        Set<Object> uniqueIds = new HashSet<>();
        for (Object[] impression : impressions) {
            uniqueIds.add(impression[1]); // assuming index 1 is the user ID
        }
        return uniqueIds.size();
    }


    /**
     * Returns the total number of bounces.
     * @return the total number of bounces.
     */
    public int getTotalBounces() {
        int totalBounces = 0;
        for (Object[] serverLog : serverLogs) {
            LocalDateTime entryDateTime = (LocalDateTime) serverLog[0];
            LocalDateTime exitDateTime = (LocalDateTime) serverLog[1];
            int pagesViewed = (int) serverLog[2];
            long timeOnPageSeconds = 0;

            // Only compute time on page if both timestamps are non-null
            if (entryDateTime != null && exitDateTime != null) {
                timeOnPageSeconds = Duration.between(entryDateTime, exitDateTime).getSeconds();
            }

            // Count as a bounce if the user viewed only one page
            // OR if both timestamps are present and the time on page is less than 4 seconds.
            if (pagesViewed == 1 || (entryDateTime != null && exitDateTime != null && timeOnPageSeconds < 4)) {
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
            totalCost += (double) impression[6];
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

    public SimpleDateFormat getDate() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    public List<LocalDateTime> getImpressionDates() {
        List<LocalDateTime> impressionDates = new ArrayList<>();
        for (Object[] impression : impressions) {
            impressionDates.add((LocalDateTime) impression[0]);
        }
        return impressionDates;
    }

    /**
     * Date-based metrics
     */
    public int getImpressionsForDate(LocalDateTime date) {
        long count = impressions.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()))
            .count();
        return (int) count;
    }


    public int getClicksForDate(LocalDateTime date) {
        return (int) clicks.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()))
            .count();
    }

    public int getUniquesForDate(LocalDateTime date) {
        Set<Object> uniqueIds = new HashSet<>();
        for (Object[] impression : impressions) {
            LocalDate impressionDate = ((LocalDateTime) impression[0]).toLocalDate();
            if (impressionDate.equals(date.toLocalDate())) {
                uniqueIds.add(impression[1]);
            }
        }
        return uniqueIds.size();
    }


    public int getBouncesForDate(LocalDateTime date) {
        return (int) serverLogs.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()))
            .filter(entry -> {
                LocalDateTime entryDT = (LocalDateTime) entry[0];
                LocalDateTime exitDT = (LocalDateTime) entry[1];
                int pagesViewed = (int) entry[2];
                long timeOnPageSeconds = 0;
                if (entryDT != null && exitDT != null) {
                    timeOnPageSeconds = Duration.between(entryDT, exitDT).getSeconds();
                }
                // Bounce if only one page or time on page is less than 4 seconds.
                return pagesViewed == 1 || (entryDT != null && exitDT != null && timeOnPageSeconds < 4);
            })
            .count();
    }


    public int getConversionsForDate(LocalDateTime date) {
        return (int) serverLogs.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()) && (boolean) entry[3])
            .count();
    }

    public double getTotalCostForDate(LocalDateTime date) {
        double total = impressions.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()))
            .mapToDouble(entry -> (double) entry[5])
            .sum();
        total += clicks.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()))
            .mapToDouble(entry -> (double) entry[1])
            .sum();
        return total;
    }

    public double getCTRForDate(LocalDateTime date) {
        int impressions = getImpressionsForDate(date);
        if (impressions == 0) return 0;
        return ((double) getClicksForDate(date) / impressions) * 100;
    }

    public double getCPAForDate(LocalDateTime date) {
        int conversions = getConversionsForDate(date);
        if (conversions == 0) return 0;
        return getTotalCostForDate(date) / conversions;
    }

    public double getCPCForDate(LocalDateTime date) {
        int clicks = getClicksForDate(date);
        if (clicks == 0) return 0;
        return getTotalCostForDate(date) / clicks;
    }

    public double getCPMForDate(LocalDateTime date) {
        int impressions = getImpressionsForDate(date);
        if (impressions == 0) return 0;
        return (getTotalCostForDate(date) / impressions) * 1000;
    }

    public double getBounceRateForDate(LocalDateTime date) {
        int clicks = getClicksForDate(date);
        int bounces = getBouncesForDate(date);

        if (clicks == 0) return 0; // Prevent division by zero

        double bounceRate = ((double) bounces / clicks) * 100; // Correctly compute percentage
        return Math.min(bounceRate, 100); // Ensure the value does not exceed 100%
    }

    /**
     * @return the earliest date in the campaign data.
     */
    public LocalDate getFirstDate() {
        return impressions.stream()
            .map(entry -> ((LocalDateTime) entry[0]).toLocalDate()) // Extract dates
            .min(LocalDate::compareTo) // Get the earliest date
            .orElse(LocalDate.now()); // Default to today if no data exists
    }

    public int getHourlyImpressions(LocalDateTime dateTime) {
        return (int) impressions.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .count();
    }

    public int getHourlyClicks(LocalDateTime dateTime) {
        return (int) clicks.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .count();
    }

    public int getHourlyConversions(LocalDateTime dateTime) {
        return (int) serverLogs.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .filter(entry -> (boolean) entry[3])
            .count();
    }

    public double getHourlyTotalCost(LocalDateTime dateTime) {
        double total = impressions.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .mapToDouble(entry -> (double) entry[5])
            .sum();
        total += clicks.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .mapToDouble(entry -> (double) entry[1])
            .sum();
        return total;
    }

    public double getHourlyCTR(LocalDateTime dateTime) {
        int impressions = getHourlyImpressions(dateTime);
        if (impressions == 0) return 0;
        return ((double) getHourlyClicks(dateTime) / impressions) * 100;
    }

    public double getHourlyCPA(LocalDateTime dateTime) {
        int conversions = getHourlyConversions(dateTime);
        if (conversions == 0) return 0;
        return getHourlyTotalCost(dateTime) / conversions;
    }

    public double getHourlyCPC(LocalDateTime dateTime) {
        int clicks = getHourlyClicks(dateTime);
        if (clicks == 0) return 0;
        return getHourlyTotalCost(dateTime) / clicks;
    }

    public double getHourlyCPM(LocalDateTime dateTime) {
        int impressions = getHourlyImpressions(dateTime);
        if (impressions == 0) return 0;
        return (getHourlyTotalCost(dateTime) / impressions) * 1000;
    }

    public double getHourlyBounceRate(LocalDateTime dateTime) {
        int clicks = getHourlyClicks(dateTime);
        int bounces = (int) serverLogs.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .filter(entry -> (int) entry[2] == 1)
            .count();
        if (clicks == 0) return 0;
        double bounceRate = ((double) bounces / clicks) * 100;
        return Math.min(bounceRate, 100);
    }





}
