package com.adauction.group19.model;

import java.io.Serializable;

/**
 * This class encapsulates the criteria used to determine if a session should be
 * counted as a bounce.
 */
public class BounceCriteria implements Serializable {
  private static final long serialVersionUID = 1L;

  /** The minimum number of pages viewed to not be considered a bounce */
  private int minPagesViewed;

  /** The minimum time spent (in seconds) on the site to not be considered a bounce */
  private int minTimeOnSiteSeconds;

  /** Whether to consider the pages viewed criterion when determining bounces */
  private boolean considerPagesViewed;

  /** Whether to consider the time on site criterion when determining bounces */
  private boolean considerTimeOnSite;

  /**
   * Creates default bounce criteria (1 page, 4 seconds)
   */
  public BounceCriteria() {
    this.minPagesViewed = 2; // If user views < 2 pages (i.e., 1 page), it's a bounce
    this.minTimeOnSiteSeconds = 4; // If user spends < 4 seconds, it's a bounce
    this.considerPagesViewed = true;
    this.considerTimeOnSite = true;
  }

  /**
   * Creates custom bounce criteria
   * @param minPagesViewed The minimum number of pages viewed to not be considered a bounce
   * @param minTimeOnSiteSeconds The minimum time spent (in seconds) on the site to not be considered a bounce
   * @param considerPagesViewed Whether to consider the pages viewed criterion
   * @param considerTimeOnSite Whether to consider the time on site criterion
   */
  public BounceCriteria(int minPagesViewed, int minTimeOnSiteSeconds,
      boolean considerPagesViewed, boolean considerTimeOnSite) {
    this.minPagesViewed = minPagesViewed;
    this.minTimeOnSiteSeconds = minTimeOnSiteSeconds;
    this.considerPagesViewed = considerPagesViewed;
    this.considerTimeOnSite = considerTimeOnSite;
  }

  // Getters and setters
  public int getMinPagesViewed() {
    return minPagesViewed;
  }

  public void setMinPagesViewed(int minPagesViewed) {
    this.minPagesViewed = minPagesViewed;
  }

  public int getMinTimeOnSiteSeconds() {
    return minTimeOnSiteSeconds;
  }

  public void setMinTimeOnSiteSeconds(int minTimeOnSiteSeconds) {
    this.minTimeOnSiteSeconds = minTimeOnSiteSeconds;
  }

  public boolean isConsiderPagesViewed() {
    return considerPagesViewed;
  }

  public void setConsiderPagesViewed(boolean considerPagesViewed) {
    this.considerPagesViewed = considerPagesViewed;
  }

  public boolean isConsiderTimeOnSite() {
    return considerTimeOnSite;
  }

  public void setConsiderTimeOnSite(boolean considerTimeOnSite) {
    this.considerTimeOnSite = considerTimeOnSite;
  }
}