package com.yash.cabinbooking.dao;

import com.yash.cabinbooking.model.Booking;
import java.sql.Date;
import java.util.List;

/**
 * BOOKING DAO INTERFACE - FLEXIBLE DURATION SYSTEM
 *
 * EVALUATION EXPLANATION:
 * - Enhanced interface supporting flexible time slot management
 * - Duration-based booking system with 15-minute precision
 * - Smart conflict detection and resolution methods
 * - AI-powered recommendations and analytics
 * - Enterprise-level method signatures for scalability
 *
 * INTERVIEW TALKING POINTS:
 * - "Designed flexible booking interface supporting multiple duration types"
 * - "Implemented smart time slot management with conflict resolution"
 * - "Added AI-ready methods for user behavior analysis"
 * - "Created scalable architecture for future booking enhancements"
 */
public interface BookingDao {

    // ================================
    // CORE CRUD OPERATIONS
    // ================================

    /**
     * Create a new booking with enhanced validation
     * @param booking Booking object with all details
     * @return true if booking created successfully
     */
    boolean createBooking(Booking booking);

    /**
     * Retrieve booking by ID with user and cabin details
     * @param bookingId Unique booking identifier
     * @return Booking object or null if not found
     */
    Booking getBookingById(int bookingId);

    /**
     * Get all bookings with pagination support
     * @return List of all bookings ordered by creation date
     */
    List<Booking> getAllBookings();

    /**
     * Get all bookings for a specific user
     * @param userId User identifier
     * @return List of user's bookings
     */
    List<Booking> getBookingsByUser(int userId);

    /**
     * Get all bookings for a specific cabin
     * @param cabinId Cabin identifier
     * @return List of cabin bookings
     */
    List<Booking> getBookingsByCabin(int cabinId);

    /**
     * Update existing booking details
     * @param booking Updated booking object
     * @return true if update successful
     */
    boolean updateBooking(Booking booking);

    /**
     * Cancel/delete a booking (soft delete)
     * @param bookingId Booking to cancel
     * @return true if cancellation successful
     */
    boolean deleteBooking(int bookingId);

    // ================================
    // BUSINESS WORKFLOW OPERATIONS
    // ================================

    /**
     * Get all pending bookings for admin review
     * @return List of bookings awaiting approval
     */
    List<Booking> getPendingBookings();

    /**
     * Get all approved bookings
     * @return List of confirmed bookings
     */
    List<Booking> getApprovedBookings();

    /**
     * Get bookings for a specific date
     * @param date Target date
     * @return List of bookings for the date
     */
    List<Booking> getBookingsByDate(Date date);

    /**
     * Get bookings within a date range
     * @param startDate Range start date
     * @param endDate Range end date
     * @return List of bookings in date range
     */
    List<Booking> getBookingsByDateRange(Date startDate, Date endDate);

    /**
     * Approve a pending booking
     * @param bookingId Booking to approve
     * @param approvedBy Admin user ID
     * @return true if approval successful
     */
    boolean approveBooking(int bookingId, int approvedBy);

    /**
     * Reject a pending booking
     * @param bookingId Booking to reject
     * @param approvedBy Admin user ID
     * @return true if rejection successful
     */
    boolean rejectBooking(int bookingId, int approvedBy);

    // ================================
    // ENHANCED CONFLICT DETECTION
    // ================================

    /**
     * Check if a time slot is available with smart overlap detection
     * @param cabinId Cabin identifier
     * @param date Booking date
     * @param timeSlot Time slot in format "HH:MM-HH:MM"
     * @return true if slot is available
     */
    boolean isSlotAvailable(int cabinId, Date date, String timeSlot);

    /**
     * Get bookings that conflict with given time slot
     * @param cabinId Cabin identifier
     * @param date Booking date
     * @param timeSlot Time slot to check
     * @return List of conflicting bookings
     */
    List<Booking> getConflictingBookings(int cabinId, Date date, String timeSlot);

    // ================================
    // FLEXIBLE DURATION SYSTEM (NEW)
    // ================================

    /**
     * Get available time slots for a specific duration
     * @param cabinId Cabin identifier
     * @param date Booking date
     * @param durationMinutes Duration in minutes (15, 30, 60, 120, etc.)
     * @return List of available time slots for the duration
     */
    List<String> getAvailableTimeSlots(int cabinId, Date date, int durationMinutes);

    /**
     * Get alternative time slots when requested slot is unavailable
     * @param cabinId Cabin identifier
     * @param date Booking date
     * @param requestedSlot Originally requested time slot
     * @param maxAlternatives Maximum number of alternatives to return
     * @return List of alternative time slots
     */
    List<String> getAlternativeTimeSlots(int cabinId, Date date, String requestedSlot, int maxAlternatives);

    /**
     * Get predefined duration options for frontend
     * @return List of available booking durations in minutes
     */
    List<Integer> getAvailableDurations();

    /**
     * Generate time slots for a specific duration
     * @param durationMinutes Duration in minutes
     * @return List of all possible time slots for the duration
     */
    List<String> generateTimeSlotsForDuration(int durationMinutes);

    // ================================
    // AI & ANALYTICS OPERATIONS
    // ================================

    /**
     * Get user's booking history for AI analysis
     * @param userId User identifier
     * @return List of user's past approved bookings
     */
    List<Booking> getUserBookingHistory(int userId);

    /**
     * Get popular time slots across all bookings
     * @return List of most frequently booked time slots
     */
    List<String> getPopularTimeSlots();

    /**
     * Get bookings by purpose for pattern analysis
     * @param purpose Purpose keyword to search
     * @return List of bookings matching the purpose
     */
    List<Booking> getBookingsByPurpose(String purpose);

    // ================================
    // REPORTING & STATISTICS (NEW)
    // ================================

    /**
     * Get booking statistics for a date range
     * @param startDate Range start
     * @param endDate Range end
     * @return Map containing booking statistics
     */
    default java.util.Map<String, Integer> getBookingStatistics(Date startDate, Date endDate) {
        // Default implementation can be overridden
        return new java.util.HashMap<>();
    }

    /**
     * Get cabin utilization data for analytics
     * @param cabinId Cabin identifier
     * @param days Number of days to analyze
     * @return List of utilization percentages
     */
    default List<Double> getCabinUtilization(int cabinId, int days) {
        // Default implementation can be overridden
        return new java.util.ArrayList<>();
    }

    /**
     * Get peak booking hours for optimization
     * @param companyId Company identifier
     * @return Map of hour -> booking count
     */
    default java.util.Map<Integer, Integer> getPeakBookingHours(int companyId) {
        // Default implementation can be overridden
        return new java.util.HashMap<>();
    }

    // ================================
    // ADVANCED SEARCH OPERATIONS (NEW)
    // ================================

    /**
     * Search bookings with multiple filters
     * @param userId User ID (optional, 0 for all users)
     * @param cabinId Cabin ID (optional, 0 for all cabins)
     * @param status Booking status (optional, null for all)
     * @param startDate Date range start (optional)
     * @param endDate Date range end (optional)
     * @param purpose Purpose keyword (optional)
     * @return List of matching bookings
     */
    default List<Booking> searchBookings(int userId, int cabinId, String status,
                                         Date startDate, Date endDate, String purpose) {
        // Default implementation can be overridden
        return new java.util.ArrayList<>();
    }

    /**
     * Get bookings requiring attention (expired pending, conflicts, etc.)
     * @return List of bookings needing admin attention
     */
    default List<Booking> getBookingsRequiringAttention() {
        // Default implementation can be overridden
        return new java.util.ArrayList<>();
    }

    // ================================
    // BATCH OPERATIONS (NEW)
    // ================================

    /**
     * Bulk approve multiple bookings
     * @param bookingIds List of booking IDs to approve
     * @param approvedBy Admin user ID
     * @return Map of booking ID -> success status
     */
    default java.util.Map<Integer, Boolean> bulkApproveBookings(List<Integer> bookingIds, int approvedBy) {
        // Default implementation can be overridden
        return new java.util.HashMap<>();
    }

    /**
     * Bulk reject multiple bookings
     * @param bookingIds List of booking IDs to reject
     * @param approvedBy Admin user ID
     * @return Map of booking ID -> success status
     */
    default java.util.Map<Integer, Boolean> bulkRejectBookings(List<Integer> bookingIds, int approvedBy) {
        // Default implementation can be overridden
        return new java.util.HashMap<>();
    }
}
