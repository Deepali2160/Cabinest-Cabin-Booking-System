package com.yash.cabinbooking.model;

import java.sql.Timestamp;

/**
 * COMPANY MODEL CLASS - SINGLE COMPANY VERSION
 */
public class Company {

    public enum Status {
        ACTIVE, INACTIVE
    }

    // Static constants for single company
    private static final String DEFAULT_COMPANY_NAME = "Yash Technology";
    private static final String DEFAULT_LOCATION = "Indore";
    private static final String DEFAULT_CONTACT = "contact@yashtech.com";

    private int companyId = 1; // ✅ ADD: Default company ID
    private String companyName;
    private String companyLocation;
    private String companyContact;
    private Status companyStatus;
    private Timestamp updatedAt;

    // Default Constructor - Single Company Setup
    public Company() {
        this.companyId = 1; // ✅ ADD: Set default ID
        this.companyName = DEFAULT_COMPANY_NAME;
        this.companyLocation = DEFAULT_LOCATION;
        this.companyContact = DEFAULT_CONTACT;
        this.companyStatus = Status.ACTIVE;
        System.out.println("🏢 Yash Technology Company object created");
    }

    // Constructor for company config updates
    public Company(String companyName, String companyLocation, String companyContact) {
        this.companyId = 1; // ✅ ADD: Set default ID
        this.companyName = companyName;
        this.companyLocation = companyLocation;
        this.companyContact = companyContact;
        this.companyStatus = Status.ACTIVE;
        System.out.println("🏢 Company config updated: " + companyName);
    }

    // Full Constructor (from company_config table)
    public Company(String companyName, String companyLocation, String companyContact,
                   Status companyStatus, Timestamp updatedAt) {
        this.companyId = 1; // ✅ ADD: Set default ID
        this.companyName = companyName;
        this.companyLocation = companyLocation;
        this.companyContact = companyContact;
        this.companyStatus = companyStatus;
        this.updatedAt = updatedAt;
        System.out.println("💾 Company loaded from config: " + companyName);
    }

    // Static method to get default company instance
    public static Company getDefaultCompany() {
        return new Company();
    }

    // ✅ ADD: Missing methods for CabinController compatibility
    public String getName() {
        return this.companyName;
    }

    public void setName(String name) {
        this.companyName = name;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getLocation() {
        return this.companyLocation;
    }

    public void setLocation(String location) {
        this.companyLocation = location;
    }

    public String getContactInfo() {
        return this.companyContact;
    }

    public void setContactInfo(String contactInfo) {
        this.companyContact = contactInfo;
    }

    // Existing getters and setters
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLocation() {
        return companyLocation;
    }

    public void setCompanyLocation(String companyLocation) {
        this.companyLocation = companyLocation;
    }

    public String getCompanyContact() {
        return companyContact;
    }

    public void setCompanyContact(String companyContact) {
        this.companyContact = companyContact;
    }

    public Status getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(Status companyStatus) {
        this.companyStatus = companyStatus;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility Methods
    public boolean isActive() {
        return this.companyStatus == Status.ACTIVE;
    }

    // Get company display name for UI
    public String getDisplayName() {
        return this.companyName + " (" + this.companyLocation + ")";
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyId=" + companyId +
                ", companyName='" + companyName + '\'' +
                ", companyLocation='" + companyLocation + '\'' +
                ", companyContact='" + companyContact + '\'' +
                ", companyStatus=" + companyStatus +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
