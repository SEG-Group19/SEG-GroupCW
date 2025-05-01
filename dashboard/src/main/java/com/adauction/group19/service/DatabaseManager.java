package com.adauction.group19.service;

import com.adauction.group19.model.User;
import com.adauction.group19.model.UserRole;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages database operations for the application.
 * Handles database initialization, connection, and user management.
 */
public class DatabaseManager {
  private static final String DB_URL = "jdbc:h2:./adauction";
  private static final String DB_USER = "sa";
  private static final String DB_PASSWORD = "";

  private static DatabaseManager instance;
  private Connection connection;

  /**
   * Private constructor to enforce singleton pattern.
   */
  private DatabaseManager() {
    try {
      // Initialize database
      initDatabase();
    } catch (SQLException e) {
      System.err.println("Error initializing database: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Gets the singleton instance of the DatabaseManager.
   *
   * @return The DatabaseManager instance
   */
  public static synchronized DatabaseManager getInstance() {
    if (instance == null) {
      instance = new DatabaseManager();
    }
    return instance;
  }

  /**
   * Initializes the database, creating tables if they don't exist.
   *
   * @throws SQLException If database initialization fails
   */
  private void initDatabase() throws SQLException {
    // Create tables if they don't exist
    try (Connection conn = getConnection();
        Statement stmt = conn.createStatement()) {

      // Create users table
      stmt.execute("""
CREATE TABLE IF NOT EXISTS users (
  id                 INT AUTO_INCREMENT PRIMARY KEY,
  username           VARCHAR(50)  NOT NULL UNIQUE,
  password_hash      VARCHAR(255) NOT NULL,
  role               VARCHAR(20)  NOT NULL,
  active             BOOLEAN      DEFAULT TRUE,
  tutorial_completed BOOLEAN      DEFAULT FALSE
)""");

      // Create campaigns table
      stmt.execute("""
CREATE TABLE IF NOT EXISTS campaigns (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  user_id       INT NOT NULL,
  campaign_name VARCHAR(100) NOT NULL,
  data          BLOB,
  FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
)""");

      // Create campaign_viewer_assignments table
      stmt.execute("""
CREATE TABLE IF NOT EXISTS campaign_viewer_assignments (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  campaign_id INT NOT NULL,
  viewer_id   INT NOT NULL,
  FOREIGN KEY (campaign_id)
    REFERENCES campaigns(id)
    ON DELETE CASCADE,
  FOREIGN KEY (viewer_id)
    REFERENCES users(id)
    ON DELETE CASCADE
)""");

      // Check if there is at least one admin user
      ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'ADMIN'");
      if (rs.next() && rs.getInt(1) == 0) {
        // Create default admin user if no admin exists
        createDefaultAdmin();
      }

    } catch (SQLException e) {
      System.err.println("Error initializing database tables: " + e.getMessage());
      throw e;
    }
  }

  /**
   * Creates a default admin user.
   *
   * @throws SQLException If creating the default admin fails
   */
  private void createDefaultAdmin() throws SQLException {
    String username = "admin";
    String password = "admin123"; // In real-world application, use a stronger password

    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(
            "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)")) {

      pstmt.setString(1, username);
      pstmt.setString(2, hashedPassword);
      pstmt.setString(3, UserRole.ADMIN.name());

      pstmt.executeUpdate();

      System.out.println("Created default admin user. Username: admin, Password: admin123");
    }
  }

  public User getUserByUsername(String username) {
    String sql = "SELECT * FROM users WHERE username = ?";
    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("active"));

        return user;
      }
    } catch (SQLException e) {
      System.err.println("Error getting user by username: " + e.getMessage());
    }

    return null; // User not found
  }

  /**
   * Gets a database connection.
   *
   * @return A Connection to the database
   * @throws SQLException If getting the connection fails
   */
  public Connection getConnection() throws SQLException {
    if (connection == null || connection.isClosed()) {
      try {
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
      } catch (ClassNotFoundException e) {
        System.err.println("H2 JDBC driver not found: " + e.getMessage());
        throw new SQLException("H2 JDBC driver not found", e);
      }
    }
    return connection;
  }

  /**
   * Closes the database connection.
   */
  public void closeConnection() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (SQLException e) {
      System.err.println("Error closing database connection: " + e.getMessage());
    }
  }

  /**
   * Authenticates a user with the given username and password.
   *
   * @param username The username
   * @param password The password (plain text)
   * @return The authenticated User object if successful, null otherwise
   */
  public User authenticateUser(String username, String password) {
    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(
            "SELECT * FROM users WHERE username = ? AND active = TRUE")) {

      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        String storedHash = rs.getString("password_hash");

        // Verify password
        if (BCrypt.checkpw(password, storedHash)) {
          User user = new User();
          user.setId(rs.getInt("id"));
          user.setUsername(rs.getString("username"));
          user.setRole(UserRole.valueOf(rs.getString("role")));
          user.setActive(rs.getBoolean("active"));

          return user;
        }
      }

      return null; // Authentication failed
    } catch (SQLException e) {
      System.err.println("Error authenticating user: " + e.getMessage());
      return null;
    }
  }

  /**
   * Registers a new user in the database.
   *
   * @param username The username
   * @param password The password (plain text, will be hashed)
   * @param role     The user role
   * @return The newly created User object if successful, null otherwise
   */
  public User registerUser(String username, String password, UserRole role) {
    // Hash the password
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(
            "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS)) {

      pstmt.setString(1, username);
      pstmt.setString(2, hashedPassword);
      pstmt.setString(3, "VIEWER");

      int affectedRows = pstmt.executeUpdate();

      if (affectedRows == 1) {
        ResultSet generatedKeys = pstmt.getGeneratedKeys();
        if (generatedKeys.next()) {
          User user = new User(username, hashedPassword, role);
          user.setId(generatedKeys.getInt(1));
          return user;
        }
      }

      return null; // Registration failed
    } catch (SQLException e) {
      System.err.println("Error registering user: " + e.getMessage());
      return null;
    }
  }

  /**
   * Gets a list of all users in the system.
   *
   * @return A list of all users
   */
  public List<User> getAllUsers() {
    List<User> users = new ArrayList<>();

    try (Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

      while (rs.next()) {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("active"));

        users.add(user);
      }
    } catch (SQLException e) {
      System.err.println("Error getting all users: " + e.getMessage());
    }

    return users;
  }

  /**
   * Gets a user by their ID.
   *
   * @param userId The user ID
   * @return The User object if found, null otherwise
   */
  public User getUserById(int userId) {
    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {

      pstmt.setInt(1, userId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("active"));

        return user;
      }

      return null; // User not found
    } catch (SQLException e) {
      System.err.println("Error getting user by ID: " + e.getMessage());
      return null;
    }
  }

  /**
   * Updates an existing user's information.
   *
   * @param user The user to update
   * @return true if update was successful, false otherwise
   */
  public boolean updateUser(User user) {
    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(
            "UPDATE users SET username = ?, role = ?, active = ? WHERE id = ?")) {

      pstmt.setString(1, user.getUsername());
      pstmt.setString(2, user.getRole().name());
      pstmt.setBoolean(3, user.isActive());
      pstmt.setInt(4, user.getId());

      int affectedRows = pstmt.executeUpdate();
      return affectedRows == 1;
    } catch (SQLException e) {
      System.err.println("Error updating user: " + e.getMessage());
      return false;
    }
  }

  /**
   * Updates a user's password.
   *
   * @param userId      The user ID
   * @param newPassword The new password (plain text, will be hashed)
   * @return true if update was successful, false otherwise
   */
  public boolean updatePassword(int userId, String newPassword) {
    // Hash the new password
    String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(
            "UPDATE users SET password_hash = ? WHERE id = ?")) {

      pstmt.setString(1, hashedPassword);
      pstmt.setInt(2, userId);

      int affectedRows = pstmt.executeUpdate();
      return affectedRows == 1;
    } catch (SQLException e) {
      System.err.println("Error updating password: " + e.getMessage());
      return false;
    }
  }

  /**
   * Deletes a user from the system.
   *
   * @param userId The ID of the user to delete
   * @return true if deletion was successful, false otherwise
   */
  public boolean deleteUser(int userId) {
    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {

      pstmt.setInt(1, userId);

      int affectedRows = pstmt.executeUpdate();
      return affectedRows == 1;
    } catch (SQLException e) {
      System.err.println("Error deleting user: " + e.getMessage());
      return false;
    }
  }

  /**
   * Checks if a username already exists in the database.
   *
   * @param username The username to check
   * @return true if the username exists, false otherwise
   */
  public boolean usernameExists(String username) {
    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(
            "SELECT COUNT(*) FROM users WHERE username = ?")) {

      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return rs.getInt(1) > 0;
      }

      return false;
    } catch (SQLException e) {
      System.err.println("Error checking if username exists: " + e.getMessage());
      return false;
    }
  }
  
  /**
   * Saves the tutorial completion status for a user.
   *
   * @param username The username
   * @param completed Whether the tutorial has been completed
   * @return true if the update was successful, false otherwise
   */
  public boolean saveTutorialStatus(String username, boolean completed) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(
             "UPDATE users SET tutorial_completed = ? WHERE username = ?")) {

      pstmt.setBoolean(1, completed);
      pstmt.setString(2, username);

      int affectedRows = pstmt.executeUpdate();
      return affectedRows == 1;
    } catch (SQLException e) {
      System.err.println("Error saving tutorial status: " + e.getMessage());
      return false;
    }
  }

  /**
   * Checks if a user has completed the tutorial.
   *
   * @param username The username
   * @return true if the tutorial has been completed, false otherwise
   */
  public boolean hasTutorialCompleted(String username) {
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(
             "SELECT tutorial_completed FROM users WHERE username = ?")) {

      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return rs.getBoolean("tutorial_completed");
      }

      return false;
    } catch (SQLException e) {
      System.err.println("Error checking tutorial status: " + e.getMessage());
      return false;
    }
  }
}