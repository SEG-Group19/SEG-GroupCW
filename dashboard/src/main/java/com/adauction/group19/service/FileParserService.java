package com.adauction.group19.service;

import com.adauction.group19.model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * This class provides services for parsing files containing campaign data.
 */
public class FileParserService {

    /**
     * Parse the campaign data from the given files.
     * @param impressionFile The file containing impression data.
     * @param clickFile The file containing click data.
     * @param serverFile The file containing server log data.
     * @return The parsed campaign data.
     */
    public CampaignData parseCampaignData(File impressionFile, File clickFile, File serverFile) {
        CampaignData campaignData = new CampaignData();

        parseImpressions(impressionFile, campaignData);
        parseClicks(clickFile, campaignData);
        parseServerLogs(serverFile, campaignData);

        System.out.println("Parsing complete.");
        return campaignData;
    }

    /**
     * Parse the impression data from the given file.
     * @param file The file containing impression data.
     * @param campaignData The campaign data to add the impressions to.
     */
    private void parseImpressions(File file, CampaignData campaignData) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip the header line
                if (line.startsWith("Date")) {
                    continue;
                }
                // Process each line (CSV format)
                String[] data = line.split(",");
                // Parse date
                LocalDateTime dateTime = parseDateTime(data[0]);
                // Parse gender
                Gender gender = data[2].equals("Male") ? Gender.MALE : Gender.FEMALE;

                // Parse age range
                AgeRange ageRange;
                switch (data[3]) {
                    case "<25":
                        ageRange = AgeRange.AGE_25_MINUS;
                        break;
                    case "25-34":
                        ageRange = AgeRange.AGE_25_34;
                        break;
                    case "35-44":
                        ageRange = AgeRange.AGE_35_44;
                        break;
                    case "45-54":
                        ageRange = AgeRange.AGE_45_54;
                        break;
                    case ">54":
                        ageRange = AgeRange.AGE_55_PLUS;
                        break;
                    default:
                        ageRange = AgeRange.UNKNOWN;
                        break;
                }

                // Parse income
                Income income = Income.valueOf(data[4].toUpperCase());

                // Parse context
                Context context;
                switch (data[5]) {
                    case "Blog":
                        context = Context.BLOG;
                        break;
                    case "News":
                        context = Context.NEWS;
                        break;
                    case "Shopping":
                        context = Context.SHOPPING;
                        break;
                    case "Social Media":
                        context = Context.SOCIAL_MEDIA;
                        break;
                    default:
                        context = Context.UNKNOWN;
                        break;
                }

                // Parse impression cost
                double impressionCost = Double.parseDouble(data[6]);

                campaignData.addImpression(dateTime, gender, ageRange, income, context, impressionCost);
            }
        } catch (IOException e) {
            System.err.println("Error reading impression log: " + e.getMessage());
        }

        System.out.println("Impression log parsed successfully");
    }

    /**
     * Parse the click data from the given file.
     * @param file The file containing click data.
     * @param campaignData The campaign data to add the clicks to.
     */
    private void parseClicks(File file, CampaignData campaignData) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip header line
                if (line.startsWith("Date")) {
                    continue;
                }

                // Process each line (CSV format)
                String[] data = line.split(",");
                // Parse date
                LocalDateTime dateTime = parseDateTime(data[0]);

                // Parse click cost
                double clickCost = Double.parseDouble(data[2]);

                campaignData.addClick(dateTime, clickCost);
            }
        } catch (IOException e) {
            System.err.println("Error reading click log: " + e.getMessage());
        }

        System.out.println("Click log parsed successfully");
    }

    /**
     * Parse the server log data from the given file.
     * @param file The file containing server log data.
     * @param campaignData The campaign data to add the server log entries to.
     */
    private void parseServerLogs(File file, CampaignData campaignData) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip header line
                if (line.startsWith("Entry Date")) {
                    continue;
                }

                // Process each line (CSV format)
                String[] data = line.split(",");
                // Parse entry date and time
                LocalDateTime entryDateTime = parseDateTime(data[0]);

                // Parse exit date and time
                LocalDateTime exitDateTime = parseDateTime(data[2]);

                // Parse pages viewed
                int pagesViewed = Integer.parseInt(data[3]);

                // Parse conversion
                boolean conversion = data[4].equals("Yes");

                campaignData.addServerLogEntry(entryDateTime, exitDateTime, pagesViewed, conversion);
            }
        } catch (IOException e) {
            System.err.println("Error reading server log: " + e.getMessage());
        }

        System.out.println("Server log parsed successfully");
    }

    /**
     * Parse a date and time string in the format "yyyy-MM-dd HH:mm:ss".
     * @param dateTime The date and time string to parse.
     * @return The parsed date and time.
     */
    private LocalDateTime parseDateTime(String dateTime) {
        if (dateTime.equals("n/a")) {
            return null;
        }
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
