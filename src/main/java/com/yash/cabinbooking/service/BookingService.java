package com.yash.cabinbooking.service;

import com.yash.cabinbooking.model.Booking;
import com.yash.cabinbooking.model.User;
import java.sql.Date;
import java.util.List;

/**
 * BOOKING SERVICE INTERFACE
 *
 * EVALUATION EXPLANATION:
 * - Core business logic for cabin booking system
 * - Handles booking validation and conflicts
 * - VIP priority and admin approval workflow
 * - Integration with AI recommendation engine
 */
public interface BookingService {

    // Core booking operations
    boolean createBooking(Booking booking, User user);
    Booking getBookingById(int bookingId);
    List<Booking> getUserBookings(int userId);
    List<Booking> getCabinBookings(int cabinId);
    boolean updateBooking(Booking booking);
    boolean cancelBooking(int bookingId, User user);

    // Admin operations
    List<Booking> getPendingBookingsForApproval();
    boolean approveBooking(int bookingId, User admin);
    boolean rejectBooking(int bookingId, User admin);
    List<Booking> getBookingsForAdmin();

    // Availability and conflicts
    boolean isSlotAvailable(int cabinId, Date date, String timeSlot);
    List<Booking> getConflictingBookings(int cabinId, Date date, String timeSlot);
    List<String> getAvailableTimeSlots(int cabinId, Date date);
    List<String> getAlternativeTimeSlots(int cabinId, Date date, String requestedSlot);

    // VIP and priority handling
    boolean applyVIPPriority(Booking booking, User user);
    List<Booking> getVIPPriorityBookings();
    boolean canOverrideBooking(User user, Booking existingBooking);

    // Analytics and reporting
    List<Booking> getBookingsByDateRange(Date startDate, Date endDate);
    List<String> getPopularTimeSlots();
    int getTotalBookingsCount();
    int getPendingBookingsCount();

    // AI integration support
    List<Booking> getUserBookingHistory(int userId);
    List<Booking> getBookingsByPurpose(String purpose);
    boolean updateUserPreferencesFromBooking(Booking booking);
}
