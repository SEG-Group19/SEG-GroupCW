package com.adauction.group19.model;

/**
 * Represents a user in the system with authentication and authorization information.
 */
public class User {
  private int id;
  private String username;
  private String passwordHash;
  private UserRole role;
  private boolean active;

  /**
   * Default constructor for User.
   */
  public User() {
    this.active = true;
  }

  /**
   * Constructor with basic user information.
   *
   * @param username     The username
   * @param passwordHash The hashed password
   * @param role         The user role
   */
  public User(String username, String passwordHash, UserRole role) {
    this.username = username;
    this.passwordHash = passwordHash;
    this.role = role;
    this.active = true;
  }

  // Getters and setters

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * Checks if the user has admin privileges.
   *
   * @return true if the user is an admin, false otherwise
   */
  public boolean isAdmin() {
    return role == UserRole.ADMIN;
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", role=" + role +
        ", active=" + active +
        '}';
  }
}