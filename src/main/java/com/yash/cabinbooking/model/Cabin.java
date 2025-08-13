package com.yash.cabinbooking.model;

import java.sql.Timestamp;

/**
 * CABIN MODEL CLASS - SINGLE COMPANY VERSION
 *
 * EVALUATION EXPLANATION:
 * - Represents meeting rooms/cabins for Yash Technology
 * - VIP-only flag for exclusive cabins
 * - Status enum for maintenance tracking
 * - Capacity and amenities for AI recommendations
 * - Optimized for single company usage
 */
public class Cabin {

    public enum Status {
        ACTIVE, MAINTENANCE, INACTIVE
    }

    // Static constant for single company
    private static final int DEFAULT_COMPANY_ID = 1;

    private int cabinId;
    private int companyId;
    private String name;
    private int capacity;
    private String amenities;
    private boolean isVipOnly;
    private String location;
    private Status status;
    private Timestamp createdAt;

    // Default Constructor
    public Cabin() {
        this.companyId = DEFAULT_COMPANY_ID; // ✅ Default to company ID 1
        this.status = Status.ACTIVE;
        this.isVipOnly = false;
        System.out.println("🏠 New Cabin object created for Yash Technology");
    }

    // ✅ UPDATED: Constructor without companyId (single company)
    public Cabin(String name, int capacity, String amenities,
                 boolean isVipOnly, String location) {
        this();
        this.name = name;
        this.capacity = capacity;
        this.amenities = amenities;
        this.isVipOnly = isVipOnly;
        this.location = location;
        System.out.println("🏠 Cabin created: " + name + " (Capacity: " + capacity + ")");
    }

    // Legacy constructor (for backward compatibility)
    public Cabin(int companyId, String name, int capacity, String amenities,
                 boolean isVipOnly, String location) {
        this();
        this.companyId = DEFAULT_COMPANY_ID; // ✅ Always use default company ID
        this.name = name;
        this.capacity = capacity;
        this.amenities = amenities;
        this.isVipOnly = isVipOnly;
        this.location = location;
        System.out.println("🏠 Cabin created: " + name + " (Capacity: " + capacity + ")");
    }

    // Full Constructor (from database)
    public Cabin(int cabinId, int companyId, String name, int capacity, String amenities,
                 boolean isVipOnly, String location, Status status, Timestamp createdAt) {
        this.cabinId = cabinId;
        this.companyId = companyId; // Keep original from database
        this.name = name;
        this.capacity = capacity;
        this.amenities = amenities;
        this.isVipOnly = isVipOnly;
        this.location = location;
        this.status = status;
        this.createdAt = createdAt;
        System.out.println("💾 Cabin loaded from database: " + name);
    }

    // Getters and Setters
    public int getCabinId() {
        return cabinId;
    }

    public void setCabinId(int cabinId) {
        this.cabinId = cabinId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public boolean isVipOnly() {
        return isVipOnly;
    }

    public void setVipOnly(boolean vipOnly) {
        isVipOnly = vipOnly;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    // Utility Methods for AI and Business Logic
    public boolean isAvailable() {
        return this.status == Status.ACTIVE;
    }

    public boolean isAccessibleForUser(User user) {
        if (!isAvailable()) {
            return false;
        }

        // VIP-only cabins are only accessible to VIP users and admins
        if (isVipOnly) {
            return user.isVIP() || user.isAdmin();
        }

        return true;
    }

    public String getCapacityRange() {
        if (capacity <= 4) return "Small";
        else if (capacity <= 8) return "Medium";
        else return "Large";
    }

    public String getVipStatusDisplay() {
        return isVipOnly ? "VIP Only" : "All Users";
    }

    // ✅ NEW: Utility methods for single company
    public static int getDefaultCompanyId() {
        return DEFAULT_COMPANY_ID;
    }

    public boolean belongsToDefaultCompany() {
        return this.companyId == DEFAULT_COMPANY_ID;
    }

    public String getDisplayName() {
        return this.name + " (" + getCapacityRange() + " - " + getVipStatusDisplay() + ")";
    }

    public String getStatusDisplay() {
        switch (this.status) {
            case ACTIVE:
                return "🟢 Active";
            case MAINTENANCE:
                return "🟡 Under Maintenance";
            case INACTIVE:
                return "🔴 Inactive";
            default:
                return "❓ Unknown";
        }
    }

    @Override
    public String toString() {
        return "Cabin{" +
                "cabinId=" + cabinId +
                ", companyId=" + companyId +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", amenities='" + amenities + '\'' +
                ", isVipOnly=" + isVipOnly +
                ", location='" + location + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
