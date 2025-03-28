package com.adauction.group19.utils;

import org.h2.tools.Server;
import java.sql.SQLException;

/**
 * Utility class for starting and stopping the H2 Database Console.
 */
public class DatabaseConsole {
  private static Server server;

  /**
   * Starts the H2 Database Console.
   *
   * @return The URL to access the console
   * @throws SQLException If the server cannot be started
   */
  public static String startConsole() throws SQLException {
    // Start the H2 web server with default settings
    server = Server.createWebServer("-webAllowOthers", "-webPort", "8082").start();
    return server.getURL();
  }

  /**
   * Stops the H2 Database Console.
   */
  public static void stopConsole() {
    if (server != null) {
      server.stop();
      server = null;
    }
  }

  /**
   * Checks if the console is running.
   *
   * @return true if the console is running, false otherwise
   */
  public static boolean isRunning() {
    return server != null && server.isRunning(true);
  }

  /**
   * Gets the database connection URL for display.
   *
   * @return The database connection URL
   */
  public static String getConnectionURL() {
    return "jdbc:h2:./adauction";
  }
}