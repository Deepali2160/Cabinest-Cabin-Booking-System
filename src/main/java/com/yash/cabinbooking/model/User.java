package com.yash.cabinbooking.model;

import java.sql.Timestamp;

/**
 * USER MODEL CLASS - SINGLE COMPANY VERSION
 * Modified for Yash Technology single company usage
 */
public class User {

    // User Types Enum for type safety
    public enum UserType {
        NORMAL, VIP, ADMIN, SUPER_ADMIN
    }

    public enum Status {
        ACTIVE, INACTIVE
    }

    // ✅ SINGLE COMPANY: Static constants
    private static final int DEFAULT_COMPANY_ID = 1;
    private static final String COMPANY_NAME = "Yash Technology";

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
        this.defaultCompanyId = DEFAULT_COMPANY_ID; // ✅ SINGLE COMPANY: Auto-set
        System.out.println("🆕 New User object created for " + COMPANY_NAME);
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
        this.defaultCompanyId = defaultCompanyId != 0 ? defaultCompanyId : DEFAULT_COMPANY_ID; // ✅ SINGLE COMPANY: Fallback
        this.status = status;
        this.createdAt = createdAt;
        System.out.println("💾 User loaded from database: " + email);
    }

    // ✅ NEW: Constructor with UserType (for admin creation)
    public User(String name, String email, String password, UserType userType) {
        this();
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
        System.out.println("👤 User created with type " + userType + ": " + email);
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
        return defaultCompanyId != 0 ? defaultCompanyId : DEFAULT_COMPANY_ID; // ✅ SINGLE COMPANY: Always return valid ID
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

    // ✅ SINGLE COMPANY: Enhanced utility methods
    public boolean isVIP() {
        return this.userType == UserType.VIP;
    }

    public boolean isAdmin() {
        return this.userType == UserType.ADMIN || this.userType == UserType.SUPER_ADMIN;
    }

    public boolean isSuperAdmin() {
        return this.userType == UserType.SUPER_ADMIN;
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    public boolean isNormalUser() {
        return this.userType == UserType.NORMAL;
    }

    public String getUserTypeDisplay() {
        switch (userType) {
            case VIP: return "VIP User";
            case ADMIN: return "Administrator";
            case SUPER_ADMIN: return "Super Administrator";
            default: return "Normal User";
        }
    }

    // ✅ NEW: Single company utility methods
    public String getCompanyName() {
        return COMPANY_NAME;
    }

    public boolean belongsToCompany() {
        return this.defaultCompanyId == DEFAULT_COMPANY_ID;
    }

    // ✅ NEW: Permission checking methods
    public boolean canAccessCabin(boolean isVipCabin) {
        if (isVipCabin) {
            return this.isVIP() || this.isAdmin();
        }
        return true; // All users can access normal cabins
    }

    public boolean canManageCabins() {
        return this.isAdmin();
    }

    public boolean canApproveBookings() {
        return this.isAdmin();
    }

    // ✅ NEW: Enum conversion methods for database compatibility
    public static UserType userTypeFromString(String userType) {
        try {
            return UserType.valueOf(userType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UserType.NORMAL; // Default fallback
        }
    }

    public static Status statusFromString(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Status.ACTIVE; // Default fallback
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + userType +
                ", company='" + COMPANY_NAME + '\'' +
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
