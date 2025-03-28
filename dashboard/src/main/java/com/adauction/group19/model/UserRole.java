package com.adauction.group19.model;

/**
 * Defines the possible roles for users in the system.
 * - ADMIN: Has full access to all features, including user management
 * - USER: Standard user with access to data analysis features
 * - VIEWER: Can only view data but not modify settings
 */
public enum UserRole {
  ADMIN, USER, VIEWER;

  /**
   * Returns a display name for the role.
   *
   * @return A user-friendly display name for the role
   */
  public String getDisplayName() {
    switch (this) {
      case ADMIN:
        return "Administrator";
      case USER:
        return "Regular User";
      case VIEWER:
        return "Viewer";
      default:
        return name();
    }
  }
}