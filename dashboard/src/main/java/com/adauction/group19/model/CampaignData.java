package com.adauction.group19.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CampaignData {
    private List<Object[]> impressions = new ArrayList<>();
    private List<Object[]> clicks = new ArrayList<>();
    private List<Object[]> serverLogs = new ArrayList<>();

    public void addImpression(LocalDateTime dateTime, Gender gender, AgeRange ageRange, Income income, Context context, double impressionCost) {
        impressions.add(new Object[]{dateTime, gender, ageRange, income, context, impressionCost});
    }
    public void addClick(LocalDateTime dateTime, double clickCost) {
        clicks.add(new Object[]{dateTime, clickCost});
    }
    public void addServerLogEntry(LocalDateTime entryDateTime, LocalDateTime exitDateTime, int pagesViewed, boolean conversion) {
        serverLogs.add(new Object[]{entryDateTime, exitDateTime, pagesViewed, conversion});
    }

    public List<Object[]> getImpressions() { return impressions; }
    public List<Object[]> getClicks() { return clicks; }
    public List<Object[]> getServerLogs() { return serverLogs; }
}
