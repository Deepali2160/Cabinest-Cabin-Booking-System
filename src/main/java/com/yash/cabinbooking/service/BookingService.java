package com.yash.cabinbooking.service;

import com.yash.cabinbooking.model.Booking;
import com.yash.cabinbooking.model.Cabin;
import com.yash.cabinbooking.model.User;
import java.sql.Date;
import java.util.List;

public interface BookingService {

    // Core booking operations
    boolean createBooking(Booking booking, User user);
    Booking getBookingById(int bookingId);
    List<Booking> getUserBookings(int userId);
    List<Booking> getCabinBookings(int cabinId);
    boolean updateBooking(Booking booking);
    boolean cancelBooking(int bookingId, User user);

    // ✅ ADDED: Methods needed for AdminController
    List<Booking> getAllBookings();
    List<Booking> getRecentBookings(int limit);

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

    // ✅ ADD THESE TWO METHODS:
    boolean approveBooking(int bookingId, int adminId);
    boolean rejectBooking(int bookingId, int adminId);
    int getBookingCountByUserId(int userId);
    // ⭐ REQUIREMENT 1: VIP Override Methods
    boolean forceBookingForVIP(Booking vipBooking, User vipUser);
    boolean reallocateNormalUserToAlternative(int normalBookingId, User vipUser, int adminId);
    List<Booking> findConflictingNormalBookings(int cabinId, Date date, String timeSlot);

    // 👨💼 REQUIREMENT 2: Admin Cabin Reallocation Methods
    List<Cabin> getAlternativeCabinsForUser(User user, int excludeCabinId, Date date, String timeSlot);
    boolean adminReallocateUserCabin(int bookingId, int newCabinId, int adminId, String reason);
    boolean notifyUserOfReallocation(int userId, int oldCabinId, int newCabinId, String reason);

    // 🎯 REQUIREMENT 3: Admin Manual Assignment Methods
    boolean adminAssignSpecificCabin(int userId, int requestedCabinId, int adminChosenCabinId, Date date, String timeSlot, String purpose);
    List<Cabin> getSuitableAlternativeCabins(int originalCabinId, User user);
    boolean createBookingWithAdminChoice(Booking originalRequest, int adminChosenCabinId, int adminId);

}
