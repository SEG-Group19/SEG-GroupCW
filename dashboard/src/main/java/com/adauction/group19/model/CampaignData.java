package com.adauction.group19.model;

import java.io.Serializable;
import java.time.Duration;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * This class represents the data for a campaign.
 */
public class CampaignData implements Serializable {
    private static final long serialVersionUID = 1L;

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
        // Now, index 0: dateTime, index 1: impressionCost, index 2: id,
        impressions.add(new Object[]{dateTime, impressionCost, id});
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
    public void addClick(LocalDateTime dateTime, double clickCost, String id) {
        clicks.add(new Object[]{dateTime, clickCost, id});
    }

    /**
     * Adds a server log entry to the campaign data.
     * @param entryDateTime date and time of the entry
     * @param exitDateTime date and time of the exit
     * @param pagesViewed number of pages viewed
     * @param conversion whether the user converted
     */
    public void addServerLogEntry(LocalDateTime entryDateTime, LocalDateTime exitDateTime, int pagesViewed, boolean conversion, String id) {
        serverLogs.add(new Object[]{entryDateTime, exitDateTime, pagesViewed, conversion, id});
    }

    // Filter is a list of sets of objects
    // Each set is a filter for a specific attribute
    // e.g. filter = [{ MALE, FEMALE }, { AGE_25_34, AGE_35_44 }, { LOW, MEDIUM }, { BLOG, NEWS }]
    private boolean filterMatches(List<Set<Enum<?>>> filter, Object[] data) {
        // Assume if data is null then it does match the filter
        if (data == null) {
            return true;
        }
        for (int i = 0; i < filter.size(); i++) {
            if (!filter.get(i).contains(data[i]) && !filter.get(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns all the impression data for the campaign.
     * @param filter the filter to apply to the impressions
     * @return The impressions data for the campaign.
     */
    public List<Object[]> getImpressions(List<Set<Enum<?>>> filter) {
        List<Object[]> filteredImpressions = new ArrayList<>();
        for (Object[] impression : impressions) {
            if (filterMatches(filter, userMap.get(impression[2]))) {
                filteredImpressions.add(impression);
            }
        }
        return filteredImpressions;
    }

    /**
     * Returns all the click data for the campaign.
     * @return The click data for the campaign.
     */
    public List<Object[]> getClicks(List<Set<Enum<?>>> filter) {
        List<Object[]> filteredClicks = new ArrayList<>();
        for (Object[] click : clicks) {
            if (filterMatches(filter, userMap.get(click[2]))) {
                filteredClicks.add(click);
            }
        }
        return filteredClicks;
    }

    /**
     * Returns all the server logs for the campaign.
     * @return The server logs for the campaign.
     */
    public List<Object[]> getServerLogs(List<Set<Enum<?>>> filter) {
        List<Object[]> filteredServerLogs = new ArrayList<>();
        for (Object[] serverLog : serverLogs) {
            if (filterMatches(filter, userMap.get(serverLog[4]))) {
                filteredServerLogs.add(serverLog);
            }
        }
        return filteredServerLogs;
    }

    // Extra methods for calculating important metrics

    /**
     * Returns the total number of impressions.
     * @return the total number of impressions.
     */
    public int getTotalImpressions(List<Set<Enum<?>>> filter) {
        return getImpressions(filter).size();
    }

    /**
     * Returns the total number of clicks.
     * @return the total number of clicks.
     */
    public int getTotalClicks(List<Set<Enum<?>>> filter) {
        return getClicks(filter).size();
    }

    /**
     * Returns the total number of conversions.
     * @return the total number of conversions.
     */
    public int getTotalConversions(List<Set<Enum<?>>> filter) {
        int totalConversions = 0;
        for (Object[] serverLog : serverLogs) {
            if ((boolean) serverLog[3] && filterMatches(filter, userMap.get(serverLog[4]))) {
                totalConversions++;
            }
        }
        return totalConversions;
    }
    /**
     * Returns the total number of unique users (based on unique IP or session tracking).
     * @return the total number of unique users.
     */
    public int getTotalUniques(List<Set<Enum<?>>> filter) {
        return userMap.size();
    }


    /**
     * Returns the total number of bounces based on the current bounce criteria.
     * @return the total number of bounces
     */
    public int getTotalBounces(List<Set<Enum<?>>> filter) {
        int totalBounces = 0;
        for (Object[] serverLog : serverLogs) {
            if (isBounce(serverLog) && filterMatches(filter, userMap.get(serverLog[4]))) {
                totalBounces++;
            }
        }
        return totalBounces;
    }



    /**
     * Returns the total cost of impressions and clicks combined.
     * @return the total cost of the campaign.
     */
    public double getTotalCost(List<Set<Enum<?>>> filter) {
        double totalCost = 0;
        for (Object[] impression : impressions) {
            if (filterMatches(filter, userMap.get(impression[2]))) {
                totalCost += (double) impression[1];
            }
        }
        for (Object[] click : clicks) {
            if (filterMatches(filter, userMap.get(click[2]))) {
                totalCost += (double) click[1];
            }
        }
        return totalCost;
    }

    /**
     * Returns the Click-Through Rate (CTR) = (Total Clicks / Total Impressions) * 100.
     * @return the CTR percentage.
     */
    public double getCTR(List<Set<Enum<?>>> filter) {
        if (getTotalImpressions(filter) == 0) return 0;
        return ((double) getTotalClicks(filter) / getTotalImpressions(filter)) * 100;
    }

    /**
     * Returns the Cost Per Acquisition (CPA) = Total Cost / Total Conversions.
     * @return the CPA value.
     */
    public double getCPA(List<Set<Enum<?>>> filter) {
        if (getTotalConversions(filter) == 0) return 0;
        return getTotalCost(filter) / getTotalConversions(filter);
    }

    /**
     * Returns the Cost Per Click (CPC) = Total Cost / Total Clicks.
     * @return the CPC value.
     */
    public double getCPC(List<Set<Enum<?>>> filter) {
        if (getTotalClicks(filter) == 0) return 0;
        return getTotalCost(filter) / getTotalClicks(filter);
    }

    /**
     * Returns the Cost Per Thousand Impressions (CPM) = (Total Cost / Total Impressions) * 1000.
     * @return the CPM value.
     */
    public double getCPM(List<Set<Enum<?>>> filter) {
        if (getTotalImpressions(filter) == 0) return 0;
        return (getTotalCost(filter) / getTotalImpressions(filter)) * 1000;
    }

    /**
     * Returns the Bounce Rate = (Total Bounces / Total Clicks) * 100.
     * @return the bounce rate percentage.
     */
    public double getBounceRate(List<Set<Enum<?>>> filter) {
        if (getTotalClicks(filter) == 0) return 0;
        return ((double) getTotalBounces(filter) / getTotalClicks(filter)) * 100;
    }

    /**
     * Returns the Click-Through Rate (CTR
     * @return the CTR percentage.
     */
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
     * Returns the Click-Through Rate (CTR) = (Total Clicks / Total Impressions) * 100.
     * @param date the date to get the impressions for.
     * @return the number of impressions for the given date.
     */
    public int getImpressionsForDate(LocalDateTime date, List<Set<Enum<?>>> filter) {
        return (int) getImpressions(filter).stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()))
            .count();
    }

    /**
     * Returns the number of clicks for the given date.
     * @param date the date to get the clicks for.
     * @return the number of clicks for the given date.
     */
    public int getClicksForDate(LocalDateTime date, List<Set<Enum<?>>> filter) {
        return (int) getClicks(filter).stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()))
            .count();
    }

    /**
     * Returns the number of unique users for the given date.
     * @param date the date to get the uniques for.
     * @return the number of unique users for the given date.
     */
    public int getUniquesForDate(LocalDateTime date, List<Set<Enum<?>>> filter) {
        Set<Object> uniqueIds = new HashSet<>();
        for (Object[] impression : getImpressions(filter)) {
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
    public int getBouncesForDate(LocalDateTime date, List<Set<Enum<?>>> filter) {
        return (int) getServerLogs(filter).stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()))
            .filter(this::isBounce)
            .count();
    }

    /**
     * Returns the number of conversions for the given date.
     * @param date the date to get the conversions for.
     * @return the number of conversions for the given date.
     */
    public int getConversionsForDate(LocalDateTime date, List<Set<Enum<?>>> filter) {
        return (int) getServerLogs(filter).stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()) && (boolean) entry[3])
            .count();
    }

    /**
     * Returns the total cost for the given date.
     * @param date the date to get the total cost for.
     * @return the total cost for the given date.
     */
    public double getTotalCostForDate(LocalDateTime date, List<Set<Enum<?>>> filter) {
        double total = getImpressions(filter).stream()
            .filter(entry -> ((LocalDateTime) entry[0]).toLocalDate().equals(date.toLocalDate()))
            .mapToDouble(entry -> {
                if (entry[1] instanceof Double) { // Correct index for Impression Cost
                    return (double) entry[1];
                } else {
                    System.err.println("Unexpected type in impressions cost: " + entry[1].getClass().getName());
                    return 0.0; // Ignore invalid values
                }
            })
            .sum();

        total += getClicks(filter).stream()
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
    public double getCTRForDate(LocalDateTime date, List<Set<Enum<?>>> filter) {
        int impressions = getImpressionsForDate(date, filter);
        if (impressions == 0) return 0;
        return ((double) getClicksForDate(date, filter) / impressions) * 100;
    }

    /**
     * Returns the Cost Per Acquisition (CPA) = Total Cost / Total Conversions.
     * @param date the date to get the CPA for.
     * @return the CPA value for the given date.
     */
    public double getCPAForDate(LocalDateTime date, List<Set<Enum<?>>> filter) {
        int conversions = getConversionsForDate(date, filter);
        if (conversions == 0) return 0;
        return getTotalCostForDate(date, filter) / conversions;
    }

    /**
     * Returns the Cost Per Click (CPC) = Total Cost / Total Clicks.
     * @param date the date to get the CPC for.
     * @return the CPC value for the given date.
     */
    public double getCPCForDate(LocalDateTime date, List<Set<Enum<?>>> filter) {
        int clicks = getClicksForDate(date, filter);
        if (clicks == 0) return 0;
        return getTotalCostForDate(date, filter) / clicks;
    }

    /**
     * Returns the Cost Per Thousand Impressions (CPM) = (Total Cost / Total Impressions) * 1000.
     * @param date the date to get the CPM for.
     * @return the CPM value for the given date.
     */
    public double getCPMForDate(LocalDateTime date, List<Set<Enum<?>>> filter) {
        int impressions = getImpressionsForDate(date, filter);
        if (impressions == 0) return 0;
        return (getTotalCostForDate(date, filter) / impressions) * 1000;
    }

    /**
     * Returns the Bounce Rate = (Total Bounces / Total Clicks) * 100.
     * @param date the date to get the bounce rate for.
     * @return the bounce rate percentage for the given date.
     */
    public double getBounceRateForDate(LocalDateTime date, List<Set<Enum<?>>> filter) {
        int clicks = getClicksForDate(date, filter);
        int bounces = getBouncesForDate(date, filter);

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
    public int getHourlyImpressions(LocalDateTime dateTime, List<Set<Enum<?>>> filter) {
        return (int) getImpressions(filter).stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .count();
    }

    /**
     * Gets the hourly clicks for a given date.
     * @param dateTime the date to get the hourly clicks for.
     * @return the number of hourly clicks for the given date.
     */
    public int getHourlyClicks(LocalDateTime dateTime, List<Set<Enum<?>>> filter) {
        return (int) getClicks(filter).stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .count();
    }

    /**
     * Gets the hourly conversions for a given date.
     * @param dateTime the date to get the hourly conversions for.
     * @return the number of hourly conversions for the given date.
     */
    public int getHourlyConversions(LocalDateTime dateTime, List<Set<Enum<?>>> filter) {
        return (int) getServerLogs(filter).stream()
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
    public double getHourlyTotalCost(LocalDateTime dateTime, List<Set<Enum<?>>> filter) {
        double total = getImpressions(filter).stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .mapToDouble(entry -> {
                if (entry[1] instanceof Double) { // Ensure correct index
                    return (double) entry[1];
                } else {
                    System.err.println("Unexpected type in hourly impressions cost: " + entry[1].getClass().getName());
                    return 0.0; // Ignore invalid values
                }
            })
            .sum();

        total += getClicks(filter).stream()
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
    public double getHourlyCTR(LocalDateTime dateTime, List<Set<Enum<?>>> filter) {
        int impressions = getHourlyImpressions(dateTime, filter);
        if (impressions == 0) return 0;
        return ((double) getHourlyClicks(dateTime, filter) / impressions) * 100;
    }

    /**
     * Returns the Cost Per Acquisition (CPA) = Total Cost / Total Conversions.
     * @param dateTime the date and time to get the hourly CPA for.
     * @return the hourly CPA value for the given date and time.
     */
    public double getHourlyCPA(LocalDateTime dateTime, List<Set<Enum<?>>> filter) {
        int conversions = getHourlyConversions(dateTime, filter);
        if (conversions == 0) return 0;
        return getHourlyTotalCost(dateTime, filter) / conversions;
    }

    /**
     * Returns the Cost Per Click (CPC) = Total Cost / Total Clicks.
     * @param dateTime the date and time to get the hourly CPC for.
     * @return the hourly CPC value for the given date and time.
     */
    public double getHourlyCPC(LocalDateTime dateTime, List<Set<Enum<?>>> filter) {
        int clicks = getHourlyClicks(dateTime, filter);
        if (clicks == 0) return 0;
        return getHourlyTotalCost(dateTime, filter) / clicks;
    }

    /**
     * Returns the Cost Per Thousand Impressions (CPM) = (Total Cost / Total Impressions) * 1000.
     * @param dateTime the date and time to get the hourly CPM for.
     * @return the hourly CPM value for the given date and time.
     */
    public double getHourlyCPM(LocalDateTime dateTime, List<Set<Enum<?>>> filter) {
        int impressions = getHourlyImpressions(dateTime, filter);
        if (impressions == 0) return 0;
        return (getHourlyTotalCost(dateTime, filter) / impressions) * 1000;
    }

    /**
     * Returns the Bounce Rate = (Total Bounces / Total Clicks) * 100 for the given hour.
     * @param dateTime the date and time to get the hourly bounce rate for
     * @return the hourly bounce rate percentage for the given date and time
     */
    public double getHourlyBounceRate(LocalDateTime dateTime, List<Set<Enum<?>>> filter) {
        int clicks = getHourlyClicks(dateTime, filter);
        int bounces = (int) getServerLogs(filter).stream()
            .filter(entry -> ((LocalDateTime) entry[0]).getHour() == dateTime.getHour()
                && ((LocalDateTime) entry[0]).toLocalDate().equals(dateTime.toLocalDate()))
            .filter(this::isBounce)
            .count();
        if (clicks == 0) return 0;
        double bounceRate = ((double) bounces / clicks) * 100;
        return Math.min(bounceRate, 100);
    }





}
