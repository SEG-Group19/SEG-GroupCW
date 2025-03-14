package com.adauction.group19.model;

import java.time.Duration;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    private HashMap<String, Object[]> userMap = new HashMap<>();
    private BounceCriteria bounceCriteria = new BounceCriteria(); // Initialize with default criteria

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
        // Now, index 0: dateTime, index 1: id, index 2: impressionCost.
        impressions.add(new Object[]{dateTime, id, impressionCost});
        userMap.put(id, new Object[]{gender, ageRange, income, context});
    }

    public void setBounceCriteria(BounceCriteria bounceCriteria) {
        this.bounceCriteria = bounceCriteria;
    }

    /**
     * Returns the bounce criteria for the campaign data.
     * @return the bounce criteria
     */
    public BounceCriteria getBounceCriteria() {
        return bounceCriteria;
    }

    /**
     * Determines if a server log entry represents a bounce based on the current bounce criteria.
     * @param serverLog The server log entry to check
     * @return true if the entry is considered a bounce, false otherwise
     */
    private boolean isBounce(Object[] serverLog) {
        LocalDateTime entryDateTime = (LocalDateTime) serverLog[0];
        LocalDateTime exitDateTime = (LocalDateTime) serverLog[1];
        int pagesViewed = (int) serverLog[2];
        long timeOnPageSeconds = 0;

        // Calculate time on page if both timestamps are present
        if (entryDateTime != null && exitDateTime != null) {
            timeOnPageSeconds = Duration.between(entryDateTime, exitDateTime).getSeconds();
        }

        // Check if it's a bounce based on pages viewed
        boolean bounceByPages = bounceCriteria.isConsiderPagesViewed() &&
            pagesViewed < bounceCriteria.getMinPagesViewed();

        // Check if it's a bounce based on time on site
        boolean bounceByTime = bounceCriteria.isConsiderTimeOnSite() &&
            entryDateTime != null && exitDateTime != null &&
            timeOnPageSeconds < bounceCriteria.getMinTimeOnSiteSeconds();

        // If considering both criteria, consider it a bounce if either is true
        if (bounceCriteria.isConsiderPagesViewed() && bounceCriteria.isConsiderTimeOnSite()) {
            return bounceByPages || bounceByTime;
        }
        // Otherwise, only consider the criteria that are enabled
        else if (bounceCriteria.isConsiderPagesViewed()) {
            return bounceByPages;
        }
        else if (bounceCriteria.isConsiderTimeOnSite()) {
            return bounceByTime;
        }

        // If no criteria are enabled, default to no bounces
        return false;
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
        return userMap.size();
    }


    /**
     * Returns the total number of bounces based on the current bounce criteria.
     * @return the total number of bounces
     */
    public int getTotalBounces() {
        int totalBounces = 0;
        for (Object[] serverLog : serverLogs) {
            if (isBounce(serverLog)) {
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
            totalCost += (double) impression[2];
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

    /**
     * Returns the Click-Through Rate (CTR
     * @return the CTR percentage.
     */
    public SimpleDateFormat getDate() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Returns the Click-Through Rate (CTR) = (Total Clicks / Total Impressions) * 100.
     * @return the CTR percentage.
     */
    public List<LocalDateTime> getImpressionDates() {
        List<LocalDateTime> impressionDates = new ArrayList<>();
        for (Object[] impression : impressions) {
            impressionDates.add((LocalDateTime) impression[0]);
        }
        return impressionDates;
    }

    /**
     * Returns the Click-Through Rate (CTR) = (Total Clicks / Total Impressions) * 100.
     * @param date the date to get the impressions for.
     * @return the number of impressions for the given date.
     */
    public int getImpressionsForDate(LocalDateTime date) {
        long count = impressions.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()))
            .count();
        return (int) count;
    }

    /**
     * Returns the number of clicks for the given date.
     * @param date the date to get the clicks for.
     * @return the number of clicks for the given date.
     */
    public int getClicksForDate(LocalDateTime date) {
        return (int) clicks.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()))
            .count();
    }

    /**
     * Returns the number of unique users for the given date.
     * @param date the date to get the uniques for.
     * @return the number of unique users for the given date.
     */
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

    /**
     * Returns the number of bounces for the given date based on the current bounce criteria.
     * @param date the date to get the bounces for
     * @return the number of bounces for the given date
     */
    public int getBouncesForDate(LocalDateTime date) {
        return (int) serverLogs.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()))
            .filter(this::isBounce)
            .count();
    }

    /**
     * Returns the number of conversions for the given date.
     * @param date the date to get the conversions for.
     * @return the number of conversions for the given date.
     */
    public int getConversionsForDate(LocalDateTime date) {
        return (int) serverLogs.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()) && (boolean) entry[3])
            .count();
    }

    /**
     * Returns the total cost for the given date.
     * @param date the date to get the total cost for.
     * @return the total cost for the given date.
     */
    public double getTotalCostForDate(LocalDateTime date) {
        double total = impressions.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()))
            .mapToDouble(entry -> {
                if (entry[2] instanceof Double) { // Correct index for Impression Cost
                    return (double) entry[2];
                } else {
                    System.err.println("Unexpected type in impressions cost: " + entry[2].getClass().getName());
                    return 0.0; // Ignore invalid values
                }
            })
            .sum();

        total += clicks.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()))
            .mapToDouble(entry -> {
                if (entry[1] instanceof Double) { // Click Cost should be at index 1
                    return (double) entry[1];
                } else {
                    System.err.println("Unexpected type in clicks cost: " + entry[1].getClass().getName());
                    return 0.0;
                }
            })
            .sum();

        return total;
    }

    /**
     * Returns the Click-Through Rate (CTR) = (Total Clicks / Total Impressions) * 100.
     * @param date the date to get the CTR for.
     * @return the CTR percentage for the given date.
     */
    public double getCTRForDate(LocalDateTime date) {
        int impressions = getImpressionsForDate(date);
        if (impressions == 0) return 0;
        return ((double) getClicksForDate(date) / impressions) * 100;
    }

    /**
     * Returns the Cost Per Acquisition (CPA) = Total Cost / Total Conversions.
     * @param date the date to get the CPA for.
     * @return the CPA value for the given date.
     */
    public double getCPAForDate(LocalDateTime date) {
        int conversions = getConversionsForDate(date);
        if (conversions == 0) return 0;
        return getTotalCostForDate(date) / conversions;
    }

    /**
     * Returns the Cost Per Click (CPC) = Total Cost / Total Clicks.
     * @param date the date to get the CPC for.
     * @return the CPC value for the given date.
     */
    public double getCPCForDate(LocalDateTime date) {
        int clicks = getClicksForDate(date);
        if (clicks == 0) return 0;
        return getTotalCostForDate(date) / clicks;
    }

    /**
     * Returns the Cost Per Thousand Impressions (CPM) = (Total Cost / Total Impressions) * 1000.
     * @param date the date to get the CPM for.
     * @return the CPM value for the given date.
     */
    public double getCPMForDate(LocalDateTime date) {
        int impressions = getImpressionsForDate(date);
        if (impressions == 0) return 0;
        return (getTotalCostForDate(date) / impressions) * 1000;
    }

    /**
     * Returns the Bounce Rate = (Total Bounces / Total Clicks) * 100.
     * @param date the date to get the bounce rate for.
     * @return the bounce rate percentage for the given date.
     */
    public double getBounceRateForDate(LocalDateTime date) {
        int clicks = getClicksForDate(date);
        int bounces = getBouncesForDate(date);

        if (clicks == 0) return 0; // Prevent division by zero

        double bounceRate = ((double) bounces / clicks) * 100; // Correctly compute percentage
        return Math.min(bounceRate, 100); // Ensure the value does not exceed 100%
    }

    /**
     * Returns the earliest date in the campaign data.
     * @return the earliest date in the campaign data.
     */
    public LocalDate getFirstDate() {
        return impressions.stream()
            .map(entry -> ((LocalDateTime) entry[0]).toLocalDate()) // Extract dates
            .min(LocalDate::compareTo) // Get the earliest date
            .orElse(LocalDate.now()); // Default to today if no data exists
    }

    /**
     * Gets the hourly impressions for a given date
     * @param dateTime the date to get the hourly impressions for.
     * @return the number of hourly impressions for the given date.
     */
    public int getHourlyImpressions(LocalDateTime dateTime) {
        return (int) impressions.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .count();
    }

    /**
     * Gets the hourly clicks for a given date.
     * @param dateTime the date to get the hourly clicks for.
     * @return the number of hourly clicks for the given date.
     */
    public int getHourlyClicks(LocalDateTime dateTime) {
        return (int) clicks.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .count();
    }

    /**
     * Gets the hourly conversions for a given date.
     * @param dateTime the date to get the hourly conversions for.
     * @return the number of hourly conversions for the given date.
     */
    public int getHourlyConversions(LocalDateTime dateTime) {
        return (int) serverLogs.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .filter(entry -> (boolean) entry[3])
            .count();
    }

    /**
     * Gets the hourly total cost for a given date.
     * @param dateTime the date to get the hourly total cost for.
     * @return the hourly total cost for the given date.
     */
    public double getHourlyTotalCost(LocalDateTime dateTime) {
        double total = impressions.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .mapToDouble(entry -> {
                if (entry[2] instanceof Double) { // Ensure correct index
                    return (double) entry[2];
                } else {
                    System.err.println("Unexpected type in hourly impressions cost: " + entry[2].getClass().getName());
                    return 0.0; // Ignore invalid values
                }
            })
            .sum();

        total += clicks.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .mapToDouble(entry -> {
                if (entry[1] instanceof Double) {
                    return (double) entry[1];
                } else {
                    System.err.println("Unexpected type in hourly clicks cost: " + entry[1].getClass().getName());
                    return 0.0;
                }
            })
            .sum();

        return total;
    }


    /**
     * Returns the Click-Through Rate (CTR) = (Total Clicks / Total Impressions) * 100.
     * @param dateTime the date and time to get the hourly CTR for.
     * @return the hourly CTR percentage for the given date and time.
     */
    public double getHourlyCTR(LocalDateTime dateTime) {
        int impressions = getHourlyImpressions(dateTime);
        if (impressions == 0) return 0;
        return ((double) getHourlyClicks(dateTime) / impressions) * 100;
    }

    /**
     * Returns the Cost Per Acquisition (CPA) = Total Cost / Total Conversions.
     * @param dateTime the date and time to get the hourly CPA for.
     * @return the hourly CPA value for the given date and time.
     */
    public double getHourlyCPA(LocalDateTime dateTime) {
        int conversions = getHourlyConversions(dateTime);
        if (conversions == 0) return 0;
        return getHourlyTotalCost(dateTime) / conversions;
    }

    /**
     * Returns the Cost Per Click (CPC) = Total Cost / Total Clicks.
     * @param dateTime the date and time to get the hourly CPC for.
     * @return the hourly CPC value for the given date and time.
     */
    public double getHourlyCPC(LocalDateTime dateTime) {
        int clicks = getHourlyClicks(dateTime);
        if (clicks == 0) return 0;
        return getHourlyTotalCost(dateTime) / clicks;
    }

    /**
     * Returns the Cost Per Thousand Impressions (CPM) = (Total Cost / Total Impressions) * 1000.
     * @param dateTime the date and time to get the hourly CPM for.
     * @return the hourly CPM value for the given date and time.
     */
    public double getHourlyCPM(LocalDateTime dateTime) {
        int impressions = getHourlyImpressions(dateTime);
        if (impressions == 0) return 0;
        return (getHourlyTotalCost(dateTime) / impressions) * 1000;
    }

    /**
     * Returns the Bounce Rate = (Total Bounces / Total Clicks) * 100 for the given hour.
     * @param dateTime the date and time to get the hourly bounce rate for
     * @return the hourly bounce rate percentage for the given date and time
     */
    public double getHourlyBounceRate(LocalDateTime dateTime) {
        int clicks = getHourlyClicks(dateTime);
        int bounces = (int) serverLogs.stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .filter(this::isBounce)
            .count();
        if (clicks == 0) return 0;
        double bounceRate = ((double) bounces / clicks) * 100;
        return Math.min(bounceRate, 100);
    }





}
