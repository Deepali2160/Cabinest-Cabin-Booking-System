package com.yash.cabinbooking.dao;

import com.yash.cabinbooking.model.Booking;
import java.sql.Date;
import java.util.List;

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
     * Get all bookings ordered by creation date (for AdminController)
     * @return List of all bookings
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
     * Get recent bookings for admin dashboard (needed for AdminController)
     * @param limit Maximum number of recent bookings to return
     * @return List of recent bookings
     */
    List<Booking> getRecentBookings(int limit);

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
    // TIME SLOT MANAGEMENT
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
    // ANALYTICS & REPORTING
    // ================================

    /**
     * Get user's booking history for analysis
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
    // ADDITIONAL UTILITY METHODS
    // ================================

    /**
     * Get bookings by status for filtering
     * @param status Booking status (PENDING, APPROVED, REJECTED, CANCELLED)
     * @return List of bookings with specified status
     */
    List<Booking> getBookingsByStatus(String status);

    /**
     * Get VIP priority bookings for admin attention
     * @return List of VIP priority bookings
     */
    List<Booking> getVIPBookings();

    /**
     * Get bookings that require urgent attention
     * @return List of urgent bookings (pending VIP, conflicts, etc.)
     */
    List<Booking> getUrgentBookings();

    /**
     * Get booking count for a specific user
     * @param userId User identifier
     * @return Total number of bookings by user
     */
    int getBookingCountByUser(int userId);

    /**
     * Get booking count for a specific cabin
     * @param cabinId Cabin identifier
     * @return Total number of bookings for cabin
     */
    int getBookingCountByCabin(int cabinId);

    /**
     * Get total booking count
     * @return Total number of bookings in system
     */
    int getTotalBookingCount();
}
