package com.adauction.group19;

import com.adauction.group19.model.BounceCriteria;
import com.adauction.group19.model.CampaignData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BounceCriteriaTest {

  private CampaignData campaignData;
  private LocalDateTime baseDateTime;

  private List<Set<Enum<?>>> filters = new ArrayList<>();

  @BeforeEach
  public void setUp() {
    campaignData = new CampaignData();
    baseDateTime = LocalDateTime.of(2025, 3, 1, 12, 0, 0);

    for (int i = 0; i < 4; i++) {
      filters.add(new HashSet<>());
    }
  }

  @Test
  public void testDefaultBounceCriteria() {
    BounceCriteria criteria = new BounceCriteria();
    assertEquals(2, criteria.getMinPagesViewed());
    assertEquals(4, criteria.getMinTimeOnSiteSeconds());
    assertTrue(criteria.isConsiderPagesViewed());
    assertTrue(criteria.isConsiderTimeOnSite());
  }

  @Test
  public void testBounceDetectionWithDifferentCriteria() {
    // Add a server log entry with 1 page viewed and 3 seconds duration
    LocalDateTime entryTime = baseDateTime;
    LocalDateTime exitTime = baseDateTime.plusSeconds(3);
    campaignData.addServerLogEntry(entryTime, exitTime, 1, false, "1");

    // Default criteria (< 2 pages OR < 4 seconds) should count this as a bounce
    assertEquals(1, campaignData.getTotalBounces(filters));

    // Only consider pages viewed, not time
    BounceCriteria pagesOnlyCriteria = new BounceCriteria(2, 4, true, false);
    campaignData.setBounceCriteria(pagesOnlyCriteria);
    assertEquals(1, campaignData.getTotalBounces(filters)); // Still a bounce (1 page < 2)

    // Only consider time, not pages viewed
    BounceCriteria timeOnlyCriteria = new BounceCriteria(2, 4, false, true);
    campaignData.setBounceCriteria(timeOnlyCriteria);
    assertEquals(1, campaignData.getTotalBounces(filters)); // Still a bounce (3 seconds < 4)

    // Change time threshold to make it not a bounce
    BounceCriteria relaxedTimeCriteria = new BounceCriteria(2, 3, false, true);
    campaignData.setBounceCriteria(relaxedTimeCriteria);
    assertEquals(0, campaignData.getTotalBounces(filters)); // Not a bounce (3 seconds >= 3)

    // Require both criteria to be met to be a bounce (AND instead of OR)
    // Custom implementation would be needed for this test
  }

  @Test
  public void testBounceCalculationForDateRanges() {
    // Add server log entries for two different dates
    LocalDateTime day1 = baseDateTime;
    LocalDateTime day2 = baseDateTime.plusDays(1);

    // Day 1: 1 page, 3 seconds (bounce by default criteria)
    campaignData.addServerLogEntry(day1, day1.plusSeconds(3), 1, false, "1");

    // Day 2: 2 pages, 3 seconds (not a bounce by default page criteria)
    campaignData.addServerLogEntry(day2, day2.plusSeconds(5), 2, false, "1");

    // Default criteria should find 1 bounce
    assertEquals(1, campaignData.getTotalBounces(filters));
    assertEquals(1, campaignData.getBouncesForDate(day1, filters));
    assertEquals(0, campaignData.getBouncesForDate(day2, filters));

    // Change criteria to consider pages only with higher threshold
    BounceCriteria strictPagesCriteria = new BounceCriteria(3, 4, true, false);
    campaignData.setBounceCriteria(strictPagesCriteria);

    // Now both entries should be bounces
    assertEquals(2, campaignData.getTotalBounces(filters));
    assertEquals(1, campaignData.getBouncesForDate(day1, filters));
    assertEquals(1, campaignData.getBouncesForDate(day2, filters));
  }

  @Test
  public void testUpdatingCriteria() {
    // Add a borderline server log entry: 1 page, 5 seconds
    campaignData.addServerLogEntry(baseDateTime, baseDateTime.plusSeconds(5), 1, false, "1");

    // Default criteria: bounce by pages (1 < 2), but not by time (5 >= 4)
    assertEquals(1, campaignData.getTotalBounces(filters));

    // Update criteria to require 6 seconds minimum
    BounceCriteria newCriteria = new BounceCriteria(2, 6, true, true);
    campaignData.setBounceCriteria(newCriteria);

    // Now it's a bounce by both criteria
    assertEquals(1, campaignData.getTotalBounces(filters));

    // Update criteria to be more lenient
    BounceCriteria lenientCriteria = new BounceCriteria(1, 4, true, true);
    campaignData.setBounceCriteria(lenientCriteria);

    // Now it's not a bounce by either criteria
    assertEquals(0, campaignData.getTotalBounces(filters));
  }

  @Test
  public void testEdgeCases() {
    // Add entry with null exit time (user still on site)
    campaignData.addServerLogEntry(baseDateTime, null, 1, false, "1");

    // Default criteria with time consideration - should not crash
    assertEquals(1, campaignData.getTotalBounces(filters)); // Should only consider the page count

    // Disable time consideration
    BounceCriteria pagesOnlyCriteria = new BounceCriteria(2, 4, true, false);
    campaignData.setBounceCriteria(pagesOnlyCriteria);
    assertEquals(1, campaignData.getTotalBounces(filters)); // Only considers pages

    // Disable both criteria
    BounceCriteria noCriteria = new BounceCriteria(2, 4, false, false);
    campaignData.setBounceCriteria(noCriteria);
    assertEquals(0, campaignData.getTotalBounces(filters)); // No enabled criteria = no bounces
  }

}