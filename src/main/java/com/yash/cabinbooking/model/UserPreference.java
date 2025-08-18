package com.yash.cabinbooking.model;

import java.sql.Timestamp;


public class UserPreference {

    private int preferenceId;
    private int userId;
    private int preferredCabinCapacity;
    private String preferredTimeSlot;
    private String frequentlyBookedAmenities;
    private int bookingFrequency;
    private Timestamp lastUpdated;

    // Default Constructor
    public UserPreference() {
        this.bookingFrequency = 0;
        System.out.println("🤖 UserPreference object created for AI learning");
    }

    // Constructor for AI usage
    public UserPreference(int userId, int preferredCapacity, String preferredTimeSlot) {
        this();
        this.userId = userId;
        this.preferredCabinCapacity = preferredCapacity;
        this.preferredTimeSlot = preferredTimeSlot;
    }

    // Getters and Setters
    public int getPreferenceId() { return preferenceId; }
    public void setPreferenceId(int preferenceId) { this.preferenceId = preferenceId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getPreferredCabinCapacity() { return preferredCabinCapacity; }
    public void setPreferredCabinCapacity(int preferredCabinCapacity) {
        this.preferredCabinCapacity = preferredCabinCapacity;
    }

    public String getPreferredTimeSlot() { return preferredTimeSlot; }
    public void setPreferredTimeSlot(String preferredTimeSlot) {
        this.preferredTimeSlot = preferredTimeSlot;
    }

    public String getFrequentlyBookedAmenities() { return frequentlyBookedAmenities; }
    public void setFrequentlyBookedAmenities(String frequentlyBookedAmenities) {
        this.frequentlyBookedAmenities = frequentlyBookedAmenities;
    }

    public int getBookingFrequency() { return bookingFrequency; }
    public void setBookingFrequency(int bookingFrequency) {
        this.bookingFrequency = bookingFrequency;
    }

    public Timestamp getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Utility Methods for AI
    public boolean hasPreferences() {
        return preferredCabinCapacity > 0 ||
                (preferredTimeSlot != null && !preferredTimeSlot.isEmpty());
    }

    public void incrementBookingFrequency() {
        this.bookingFrequency++;
        System.out.println("📈 Booking frequency updated for user: " + userId);
    }

    @Override
    public String toString() {
        return "UserPreference{" +
                "userId=" + userId +
                ", preferredCapacity=" + preferredCabinCapacity +
                ", preferredTimeSlot='" + preferredTimeSlot + '\'' +
                ", bookingFrequency=" + bookingFrequency +
                '}';
    }
}
