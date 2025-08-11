package com.yash.cabinbooking.model;

import java.sql.Timestamp;

/**
 * USER MODEL CLASS
 *
 * EVALUATION EXPLANATION:
 * - Simple POJO with proper encapsulation
 * - Enum for user types provides type safety
 * - Timestamp fields for audit trail
 * - toString method for debugging
 *
 * INTERVIEW TALKING POINTS:
 * - "POJO pattern follow kiya hai with private fields"
 * - "Enum use kiya user types ke liye better type safety"
 * - "Constructor overloading for different use cases"
 * - "toString method debugging ke liye helpful hai"
 */
public class User {

    // User Types Enum for type safety
    public enum UserType {
        NORMAL, VIP, ADMIN, SUPER_ADMIN
    }

    public enum Status {
        ACTIVE, INACTIVE
    }

    // Private fields - Proper encapsulation
    private int userId;
    private String name;
    private String email;
    private String password;
    private UserType userType;
    private int defaultCompanyId;
    private Status status;
    private Timestamp createdAt;

    // Default Constructor
    public User() {
        this.userType = UserType.NORMAL;
        this.status = Status.ACTIVE;
        System.out.println("🆕 New User object created");
    }

    // Constructor for Registration
    public User(String name, String email, String password) {
        this();
        this.name = name;
        this.email = email;
        this.password = password;
        System.out.println("👤 User created for registration: " + email);
    }

    // Constructor for Login
    public User(String email, String password) {
        this();
        this.email = email;
        this.password = password;
        System.out.println("🔐 User created for login: " + email);
    }

    // Full Constructor (from database)
    public User(int userId, String name, String email, String password,
                UserType userType, int defaultCompanyId, Status status, Timestamp createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.defaultCompanyId = defaultCompanyId;
        this.status = status;
        this.createdAt = createdAt;
        System.out.println("💾 User loaded from database: " + email);
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public int getDefaultCompanyId() {
        return defaultCompanyId;
    }

    public void setDefaultCompanyId(int defaultCompanyId) {
        this.defaultCompanyId = defaultCompanyId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // Utility Methods
    public boolean isVIP() {
        return this.userType == UserType.VIP;
    }

    public boolean isAdmin() {
        return this.userType == UserType.ADMIN || this.userType == UserType.SUPER_ADMIN;
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    public String getUserTypeDisplay() {
        switch (userType) {
            case VIP: return "VIP User";
            case ADMIN: return "Administrator";
            case SUPER_ADMIN: return "Super Administrator";
            default: return "Normal User";
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + userType +
                ", defaultCompanyId=" + defaultCompanyId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId == user.userId && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return userId + email.hashCode();
    }
}
