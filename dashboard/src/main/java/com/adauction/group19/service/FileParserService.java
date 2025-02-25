package com.adauction.group19.service;

import com.adauction.group19.model.CampaignData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileParserService {

    public CampaignData parseCampaignData(File impressionFile, File clickFile, File serverFile) {
        CampaignData campaignData = new CampaignData();

        parseImpressions(impressionFile, campaignData);
        parseClicks(clickFile, campaignData);
        parseServerLogs(serverFile, campaignData);

        System.out.println("Parsing complete.");
        return campaignData;
    }

    private void parseImpressions(File file, CampaignData campaignData) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Process each line (CSV format)
                String[] data = line.split(",");
                campaignData.addImpression();
            }
        } catch (IOException e) {
            System.err.println("Error reading impression log: " + e.getMessage());
        }
    }

    private void parseClicks(File file, CampaignData campaignData) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                campaignData.addClick();
            }
        } catch (IOException e) {
            System.err.println("Error reading click log: " + e.getMessage());
        }
    }

    private void parseServerLogs(File file, CampaignData campaignData) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                campaignData.addServerLogEntry();
            }
        } catch (IOException e) {
            System.err.println("Error reading server log: " + e.getMessage());
        }
    }
}
