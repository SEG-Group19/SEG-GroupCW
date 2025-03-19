package com.adauction.group19.Util;

import com.adauction.group19.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CampaignDataUtil {

    /**
     * Creates test data that spans multiple days with various metrics
     */
    public static CampaignData createTestCampaignData(Integer days) {
        com.adauction.group19.model.CampaignData data = new com.adauction.group19.model.CampaignData();

        // Create data for the past 14 days
        LocalDate today = LocalDate.now();

        for (int i = days; i >= 0; i--) {
            LocalDate date = today.minusDays(i);

            // Morning data (10:00 AM)
            LocalDateTime morningTime = LocalDateTime.of(date, LocalTime.of(10, 0));
            // Add impressions (increasing by day)
            for (int j = 0; j < 10 + i; j++) {
                data.addImpression(morningTime, "user" + j,
                        j % 2 == 0 ? Gender.MALE : Gender.FEMALE,
                        AgeRange.AGE_25_34,
                        Income.MEDIUM,
                        Context.BLOG,
                        0.5);
            }

            // Add clicks (about half of impressions)
            for (int j = 0; j < (5 + i/2); j++) {
                data.addClick(morningTime, 0.3, "1");
            }

            // Add conversions
            for (int j = 0; j < (2 + i/3); j++) {
                data.addClick(morningTime, 0.3, "1");
            }

            // Add some server logs (including bounces)
            for (int j = 0; j < (7 + i); j++) {
                boolean isBounce = j % 3 == 0;
                int pagesViewed = isBounce ? 1 : 3 + (j % 3);
                LocalDateTime endTime = morningTime.plusMinutes(isBounce ? 1 : 10);
                data.addServerLogEntry(morningTime, endTime, pagesViewed, j % 5 == 0, "1");
            }
        }

        return data;
    }

}
