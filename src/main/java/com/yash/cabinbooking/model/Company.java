package com.yash.cabinbooking.model;

import java.sql.Timestamp;

/**
 * COMPANY MODEL CLASS
 *
 * EVALUATION EXPLANATION:
 * - Represents companies that own cabins
 * - Simple POJO with business logic methods
 * - Status enum for company state management
 */
public class Company {

    public enum Status {
        ACTIVE, INACTIVE
    }

    private int companyId;
    private String name;
    private String location;
    private String contactInfo;
    private Status status;
    private Timestamp createdAt;

    // Default Constructor
    public Company() {
        this.status = Status.ACTIVE;
        System.out.println("🏢 New Company object created");
    }

    // Constructor for creating new company
    public Company(String name, String location, String contactInfo) {
        this();
        this.name = name;
        this.location = location;
        this.contactInfo = contactInfo;
        System.out.println("🏢 Company created: " + name);
    }

    // Full Constructor (from database)
    public Company(int companyId, String name, String location, String contactInfo,
                   Status status, Timestamp createdAt) {
        this.companyId = companyId;
        this.name = name;
        this.location = location;
        this.contactInfo = contactInfo;
        this.status = status;
        this.createdAt = createdAt;
        System.out.println("💾 Company loaded from database: " + name);
    }

    // Getters and Setters
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
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
    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyId=" + companyId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}

