package com.adauction.group19.service;

import com.adauction.group19.model.User;

/**
 * Manages the current user session.
 * Singleton class to track the currently logged-in user across the application.
 */
public class UserSession {
  private static UserSession instance;
  private User currentUser;

  /**
   * Private constructor to enforce singleton pattern.
   */
  private UserSession() {
  }

  /**
   * Gets the singleton instance of the UserSession.
   *
   * @return The UserSession instance
   */
  public static synchronized UserSession getInstance() {
    if (instance == null) {
      instance = new UserSession();
    }
    return instance;
  }

  /**
   * Gets the currently logged-in user.
   *
   * @return The current User, or null if no user is logged in
   */
  public User getCurrentUser() {
    return currentUser;
  }

  /**
   * Sets the currently logged-in user.
   *
   * @param user The user to set as current
   */
  public void setCurrentUser(User user) {
    this.currentUser = user;
  }

  /**
   * Checks if a user is currently logged in.
   *
   * @return true if a user is logged in, false otherwise
   */
  public boolean isLoggedIn() {
    return currentUser != null;
  }

  /**
   * Logs out the current user.
   */
  public void logout() {
    this.currentUser = null;
  }

  /**
   * Checks if the current user has admin privileges.
   *
   * @return true if the current user is an admin, false otherwise
   */
  public boolean isAdmin() {
    return isLoggedIn() && currentUser.isAdmin();
  }
}