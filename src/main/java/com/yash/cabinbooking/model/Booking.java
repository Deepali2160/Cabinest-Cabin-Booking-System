package com.yash.cabinbooking.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * BOOKING MODEL CLASS - SINGLE COMPANY VERSION
 *
 * EVALUATION EXPLANATION:
 * - Enhanced for flexible duration booking (15 min to 8+ hours)
 * - Optimized for Yash Technology single company usage
 * - Smart time slot management with duration calculation
 * - Status workflow for admin approval process
 * - Priority levels for VIP handling
 * - Aligned with database table structure
 *
 * INTERVIEW TALKING POINTS:
 * - "Implemented flexible duration booking model supporting any time range"
 * - "Added smart time slot validation and overlap detection methods"
 * - "Enhanced enum system matching database constraints"
 * - "Built single-company focused model for scalability"
 */
public class Booking {

    // ✅ DATABASE ALIGNED: Booking types matching your table
    public enum BookingType {
        SINGLE_DAY("Single Day", "One-time booking for a single day"),
        MULTI_DAY("Multi-Day", "Booking spanning multiple consecutive days"),
        RECURRING("Recurring", "Weekly/monthly recurring booking"),
        EMERGENCY("Emergency", "Urgent booking with highest priority");

        private final String displayName;
        private final String description;

        BookingType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }

        // String conversion for database compatibility
        public static BookingType fromString(String type) {
            try {
                return BookingType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return BookingType.SINGLE_DAY; // Default fallback
            }
        }
    }

    // ✅ DATABASE ALIGNED: Status matching your table exactly
    public enum Status {
        PENDING("Pending", "⏳", "Awaiting admin approval"),
        APPROVED("Approved", "✅", "Confirmed and ready to use"),
        REJECTED("Rejected", "❌", "Request denied by admin"),
        CANCELLED("Cancelled", "🚫", "Cancelled by user or admin");

        private final String displayName;
        private final String icon;
        private final String description;

        Status(String displayName, String icon, String description) {
            this.displayName = displayName;
            this.icon = icon;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getIcon() { return icon; }
        public String getDescription() { return description; }

        // String conversion for database compatibility
        public static Status fromString(String status) {
            try {
                return Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Status.PENDING; // Default fallback
            }
        }
    }

    // ✅ DATABASE ALIGNED: Priority levels matching your table exactly
    public enum PriorityLevel {
        NORMAL("Normal", "🟢", 1, "Standard priority"),
        HIGH("High", "🔴", 2, "High priority - faster approval"),
        VIP("VIP", "🌟", 3, "VIP priority - immediate attention");

        private final String displayName;
        private final String icon;
        private final int level;
        private final String description;

        PriorityLevel(String displayName, String icon, int level, String description) {
            this.displayName = displayName;
            this.icon = icon;
            this.level = level;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getIcon() { return icon; }
        public int getLevel() { return level; }
        public String getDescription() { return description; }

        // String conversion for database compatibility
        public static PriorityLevel fromString(String priority) {
            try {
                return PriorityLevel.valueOf(priority.toUpperCase());
            } catch (IllegalArgumentException e) {
                return PriorityLevel.NORMAL; // Default fallback
            }
        }
    }

    // ✅ DATABASE FIELDS (exactly matching your bookings table)
    private int bookingId;          // booking_id - PK, auto_increment
    private int userId;             // user_id - NOT NULL, MUL
    private int cabinId;            // cabin_id - NOT NULL, MUL
    private Date bookingDate;       // booking_date - NOT NULL
    private String timeSlot;        // time_slot - NOT NULL, varchar(20)
    private String purpose;         // purpose - NOT NULL, text
    private BookingType bookingType; // booking_type - NOT NULL, enum, default SINGLE_DAY
    private Status status;          // status - enum, default PENDING
    private PriorityLevel priorityLevel; // priority_level - enum, default NORMAL
    private Timestamp createdAt;    // created_at - default CURRENT_TIMESTAMP
    private int approvedBy;         // approved_by - nullable int
    private Timestamp approvedAt;   // approved_at - nullable timestamp

    // ✅ ADD: NEW REJECTION FIELDS (matching database)
    private int rejectedBy;         // rejected_by - nullable int
    private Timestamp rejectedAt;   // rejected_at - nullable timestamp

    // ✅ CALCULATED FIELDS (not in database - for display/logic only)
    private int durationMinutes;    // Calculated from timeSlot
    private String startTime;       // Extracted from timeSlot (e.g., "09:00")
    private String endTime;         // Extracted from timeSlot (e.g., "10:30")

    // ✅ DISPLAY FIELDS (for UI - not in database)
    private String userName;        // From users table join
    private String cabinName;       // From cabins table join
    private String approverName;    // From users table join (admin name)
    private String rejecterName;    // ✅ ADD: From users table join (admin who rejected)

    // ✅ SINGLE COMPANY CONSTANTS
    private static final String COMPANY_NAME = "Yash Technology";

    // ================================
    // CONSTRUCTORS (UPDATED)
    // ================================

    // Default Constructor
    public Booking() {
        this.bookingType = BookingType.SINGLE_DAY;
        this.status = Status.PENDING;
        this.priorityLevel = PriorityLevel.NORMAL;
        this.durationMinutes = 0;
        System.out.println("📅 New Booking object created for " + COMPANY_NAME);
    }

    // Constructor for new booking creation
    public Booking(int userId, int cabinId, Date bookingDate, String timeSlot, String purpose) {
        this();
        this.userId = userId;
        this.cabinId = cabinId;
        this.bookingDate = bookingDate;
        this.timeSlot = timeSlot;
        this.purpose = purpose;
        calculateTimeFields();
        System.out.println("📅 New booking created - User: " + userId + ", Duration: " + durationMinutes + " min");
    }

    // Constructor with booking type and priority
    public Booking(int userId, int cabinId, Date bookingDate, String timeSlot, String purpose,
                   BookingType bookingType, PriorityLevel priorityLevel) {
        this();
        this.userId = userId;
        this.cabinId = cabinId;
        this.bookingDate = bookingDate;
        this.timeSlot = timeSlot;
        this.purpose = purpose;
        this.bookingType = bookingType;
        this.priorityLevel = priorityLevel;
        calculateTimeFields();
        System.out.println("📅 Enhanced booking created - Type: " + bookingType + ", Priority: " + priorityLevel);
    }

    // ✅ UPDATED: Full constructor (from database) - WITH REJECTION FIELDS
    public Booking(int bookingId, int userId, int cabinId, Date bookingDate, String timeSlot,
                   String purpose, BookingType bookingType, Status status, PriorityLevel priorityLevel,
                   Timestamp createdAt, int approvedBy, Timestamp approvedAt,
                   int rejectedBy, Timestamp rejectedAt) { // ✅ ADDED PARAMETERS
        this.bookingId = bookingId;
        this.userId = userId;
        this.cabinId = cabinId;
        this.bookingDate = bookingDate;
        this.timeSlot = timeSlot;
        this.purpose = purpose;
        this.bookingType = bookingType;
        this.status = status;
        this.priorityLevel = priorityLevel;
        this.createdAt = createdAt;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.rejectedBy = rejectedBy;       // ✅ ADDED
        this.rejectedAt = rejectedAt;       // ✅ ADDED
        calculateTimeFields();
        System.out.println("💾 Booking loaded from database: " + bookingId + " (" + durationMinutes + " min)");
    }

    // ================================
    // TIME CALCULATION METHODS (UNCHANGED)
    // ================================

    // Auto-calculate duration and time fields from timeSlot
    private void calculateTimeFields() {
        if (timeSlot != null && timeSlot.contains("-")) {
            try {
                String[] times = timeSlot.split("-");
                this.startTime = times[0].trim();
                this.endTime = times[1].trim();

                // Calculate duration in minutes
                LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));
                this.durationMinutes = (int) java.time.Duration.between(start, end).toMinutes();

            } catch (Exception e) {
                System.err.println("⚠️ Error parsing time slot: " + timeSlot);
                this.durationMinutes = 60; // Default to 1 hour
                this.startTime = "09:00";
                this.endTime = "10:00";
            }
        }
    }

    // ================================
    // VALIDATION METHODS (UNCHANGED)
    // ================================

    // Validate time slot format and duration
    public boolean isValidTimeSlot() {
        if (timeSlot == null || !timeSlot.contains("-")) {
            return false;
        }

        try {
            String[] times = timeSlot.split("-");
            LocalTime start = LocalTime.parse(times[0].trim(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime end = LocalTime.parse(times[1].trim(), DateTimeFormatter.ofPattern("HH:mm"));

            // Valid if end is after start and duration is reasonable (15 min to 8 hours)
            long duration = java.time.Duration.between(start, end).toMinutes();
            return duration >= 15 && duration <= 480;

        } catch (Exception e) {
            return false;
        }
    }

    // Check if booking is within business hours
    public boolean isWithinBusinessHours() {
        if (startTime == null || endTime == null) {
            return false;
        }

        try {
            LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime businessStart = LocalTime.of(9, 0);  // 9:00 AM
            LocalTime businessEnd = LocalTime.of(18, 0);   // 6:00 PM

            return !start.isBefore(businessStart) && !end.isAfter(businessEnd);

        } catch (Exception e) {
            return false;
        }
    }

    // Check if this booking overlaps with another booking
    public boolean overlapsWith(Booking other) {
        if (other == null || !this.bookingDate.equals(other.bookingDate)) {
            return false;
        }

        try {
            LocalTime thisStart = LocalTime.parse(this.startTime, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime thisEnd = LocalTime.parse(this.endTime, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime otherStart = LocalTime.parse(other.startTime, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime otherEnd = LocalTime.parse(other.endTime, DateTimeFormatter.ofPattern("HH:mm"));

            return thisStart.isBefore(otherEnd) && thisEnd.isAfter(otherStart);

        } catch (Exception e) {
            return false;
        }
    }

    // ================================
    // DISPLAY METHODS (UNCHANGED)
    // ================================

    // Get human-readable duration display
    public String getDurationDisplay() {
        if (durationMinutes < 60) {
            return durationMinutes + " minutes";
        } else if (durationMinutes == 60) {
            return "1 hour";
        } else if (durationMinutes < 120) {
            return "1 hour " + (durationMinutes - 60) + " minutes";
        } else {
            int hours = durationMinutes / 60;
            int minutes = durationMinutes % 60;
            return hours + " hours" + (minutes > 0 ? " " + minutes + " minutes" : "");
        }
    }

    // Get formatted time slot display
    public String getTimeSlotDisplay() {
        if (startTime != null && endTime != null) {
            return startTime + " - " + endTime + " (" + getDurationDisplay() + ")";
        }
        return timeSlot;
    }

    // Get status display with icon
    public String getStatusDisplay() {
        return status.getIcon() + " " + status.getDisplayName();
    }

    // Get priority display with icon
    public String getPriorityDisplay() {
        return priorityLevel.getIcon() + " " + priorityLevel.getDisplayName();
    }

    // Get booking type display
    public String getBookingTypeDisplay() {
        return bookingType.getDisplayName();
    }

    // ================================
    // BUSINESS LOGIC METHODS (UNCHANGED)
    // ================================

    // Status check methods
    public boolean isPending() { return this.status == Status.PENDING; }
    public boolean isApproved() { return this.status == Status.APPROVED; }
    public boolean isRejected() { return this.status == Status.REJECTED; }
    public boolean isCancelled() { return this.status == Status.CANCELLED; }

    // Priority check methods
    public boolean isVipPriority() { return this.priorityLevel == PriorityLevel.VIP; }
    public boolean isHighPriority() { return this.priorityLevel.getLevel() >= PriorityLevel.HIGH.getLevel(); }

    // Business rule methods
    public boolean canBeCancelled() {
        return status == Status.PENDING || status == Status.APPROVED;
    }

    public boolean requiresApproval() {
        return status == Status.PENDING;
    }

    public boolean isUpcoming() {
        if (bookingDate == null) return false;

        try {
            Date today = new Date(System.currentTimeMillis());
            return bookingDate.after(today) || bookingDate.equals(today);
        } catch (Exception e) {
            return false;
        }
    }

    // ================================
    // GETTERS AND SETTERS (UPDATED WITH NEW FIELDS)
    // ================================

    // Primary key
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    // Foreign keys
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCabinId() { return cabinId; }
    public void setCabinId(int cabinId) { this.cabinId = cabinId; }

    // Core booking fields
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
        calculateTimeFields(); // Auto-recalculate when timeSlot changes
    }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    // Enum fields
    public BookingType getBookingType() { return bookingType; }
    public void setBookingType(BookingType bookingType) { this.bookingType = bookingType; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public PriorityLevel getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(PriorityLevel priorityLevel) { this.priorityLevel = priorityLevel; }

    // Timestamp fields
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getApprovedBy() { return approvedBy; }
    public void setApprovedBy(int approvedBy) { this.approvedBy = approvedBy; }

    public Timestamp getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Timestamp approvedAt) { this.approvedAt = approvedAt; }

    // ✅ NEW: REJECTION FIELDS GETTERS & SETTERS
    public int getRejectedBy() { return rejectedBy; }
    public void setRejectedBy(int rejectedBy) { this.rejectedBy = rejectedBy; }

    public Timestamp getRejectedAt() { return rejectedAt; }
    public void setRejectedAt(Timestamp rejectedAt) { this.rejectedAt = rejectedAt; }

    // Calculated fields
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    // Display fields (for UI)
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getCabinName() { return cabinName; }
    public void setCabinName(String cabinName) { this.cabinName = cabinName; }

    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }

    // ✅ NEW: REJECTER NAME GETTER & SETTER
    public String getRejecterName() { return rejecterName; }
    public void setRejecterName(String rejecterName) { this.rejecterName = rejecterName; }

    // Company info (single company)
    public String getCompanyName() { return COMPANY_NAME; }

    // ================================
    // UTILITY METHODS (UPDATED)
    // ================================

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", cabinId=" + cabinId +
                ", bookingDate=" + bookingDate +
                ", timeSlot='" + timeSlot + '\'' +
                ", duration=" + getDurationDisplay() +
                ", purpose='" + purpose + '\'' +
                ", bookingType=" + bookingType +
                ", status=" + status +
                ", priorityLevel=" + priorityLevel +
                ", createdAt=" + createdAt +
                ", approvedAt=" + approvedAt +
                ", rejectedAt=" + rejectedAt +  // ✅ ADDED
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Booking booking = (Booking) obj;
        return bookingId == booking.bookingId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(bookingId);
    }
}
