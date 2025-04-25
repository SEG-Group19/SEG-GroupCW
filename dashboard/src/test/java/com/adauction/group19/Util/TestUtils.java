package com.adauction.group19.Util;

import com.adauction.group19.model.User;
import com.adauction.group19.model.UserRole;
import com.adauction.group19.service.UserSession;

public class TestUtils {
  /**
   * Sets up an authenticated session with an admin user.
   * This bypasses the login screen for tests.
   */
  public static void setupAuthenticatedSession() {
    User adminUser = new User("testAdmin", "hashedPassword", UserRole.ADMIN);
    adminUser.setId(1);
    adminUser.setActive(true);
    UserSession.getInstance().setCurrentUser(adminUser);
  }

  /**
   * Logs out the current user session.
   */
  public static void clearAuthenticatedSession() {
    UserSession.getInstance().logout();
  }
}