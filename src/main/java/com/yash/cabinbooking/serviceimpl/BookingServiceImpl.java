package com.yash.cabinbooking.serviceimpl;

import com.yash.cabinbooking.service.BookingService;
import com.yash.cabinbooking.service.UserService; // ✅ ADD THIS IMPORT
import com.yash.cabinbooking.serviceimpl.UserServiceImpl; // ✅ ADD THIS IMPORT
import com.yash.cabinbooking.dao.BookingDao;
import com.yash.cabinbooking.dao.CabinDao;
import com.yash.cabinbooking.daoimpl.BookingDaoImpl;
import com.yash.cabinbooking.daoimpl.CabinDaoImpl;
import com.yash.cabinbooking.model.Booking;
import com.yash.cabinbooking.model.User;
import com.yash.cabinbooking.model.Cabin;
import com.yash.cabinbooking.util.DbUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * BOOKING SERVICE IMPLEMENTATION - ENHANCED WITH VIP OVERRIDE & ADMIN FEATURES
 *
 * FEATURES IMPLEMENTED:
 * 1. ⭐ VIP Override: VIP users can force book any cabin, normal users auto-reallocated
 * 2. 👨💼 Admin Cabin Reallocation: Admins can move users to different cabins
 * 3. 🎯 Admin Manual Assignment: Admins can assign specific cabins instead of user choice
 * 4. 🔧 Flexible duration booking system (15 min to 8+ hours)
 * 5. 🚀 Smart conflict detection and resolution
 */
public class BookingServiceImpl implements BookingService {

    private BookingDao bookingDAO;
    private CabinDao cabinDAO;
    private UserService userService; // ✅ ADD THIS LINE

    // ✅ Flexible duration constants
    private static final String TIME_SLOT_PATTERN = "^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]-([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$";
    private static final int MIN_DURATION_MINUTES = 15;
    private static final int MAX_DURATION_MINUTES = 480; // 8 hours
    private static final int BUSINESS_START_HOUR = 9;
    private static final int BUSINESS_END_HOUR = 18;

    public BookingServiceImpl() {
        this.bookingDAO = new BookingDaoImpl();
        this.cabinDAO = new CabinDaoImpl();
        this.userService = new UserServiceImpl(); // ✅ ADD THIS LINE
        System.out.println("🔧 BookingService initialized with enhanced VIP & Admin features");
    }

    @Override
    public boolean createBooking(Booking booking, User user) {
        System.out.println("📅 Creating booking for user: " + user.getName() + " (ID: " + user.getUserId() + ")");

        // ✅ ENHANCED: Input validation with flexible duration support
        if (!isValidBookingData(booking) || user == null) {
            System.err.println("❌ Invalid booking data or user");
            return false;
        }

        // Check if cabin exists and is accessible to user
        Cabin cabin = cabinDAO.getCabinById(booking.getCabinId());
        if (cabin == null) {
            System.err.println("❌ Cabin not found: " + booking.getCabinId());
            return false;
        }

        if (!cabin.isAccessibleForUser(user)) {
            System.err.println("❌ Cabin not accessible to user: " + user.getUserTypeDisplay());
            return false;
        }

        // Set user ID and apply VIP priority
        booking.setUserId(user.getUserId());
        applyVIPPriority(booking, user);

        // ✅ ENHANCED: VIP Override Logic
        if (user.isVIP() && !isSlotAvailable(booking.getCabinId(), booking.getBookingDate(), booking.getTimeSlot())) {
            System.out.println("⭐ VIP user requesting occupied slot - attempting force booking");
            return forceBookingForVIP(booking, user);
        }

        // ✅ ENHANCED: Smart slot availability check
        if (!isSlotAvailable(booking.getCabinId(), booking.getBookingDate(), booking.getTimeSlot())) {
            System.err.println("❌ Time slot not available: " + booking.getTimeSlot());
            return false;
        }

        boolean success = bookingDAO.createBooking(booking);

        if (success) {
            System.out.println("✅ Flexible duration booking created successfully: ID " + booking.getBookingId());
            // Update user preferences for AI learning
            updateUserPreferencesFromBooking(booking);
        } else {
            System.err.println("❌ Booking creation failed");
        }

        return success;
    }

    @Override
    public Booking getBookingById(int bookingId) {
        System.out.println("🔍 Fetching booking by ID: " + bookingId);

        if (bookingId <= 0) {
            System.err.println("❌ Invalid booking ID: " + bookingId);
            return null;
        }

        return bookingDAO.getBookingById(bookingId);
    }

    @Override
    public List<Booking> getUserBookings(int userId) {
        System.out.println("📋 Fetching bookings for user: " + userId);

        if (userId <= 0) {
            System.err.println("❌ Invalid user ID: " + userId);
            return new ArrayList<>();
        }

        return bookingDAO.getBookingsByUser(userId);
    }

    @Override
    public List<Booking> getCabinBookings(int cabinId) {
        System.out.println("🏠 Fetching bookings for cabin: " + cabinId);

        if (cabinId <= 0) {
            System.err.println("❌ Invalid cabin ID: " + cabinId);
            return new ArrayList<>();
        }

        return bookingDAO.getBookingsByCabin(cabinId);
    }

    @Override
    public boolean updateBooking(Booking booking) {
        System.out.println("✏️ Updating booking: " + booking.getBookingId());

        if (!isValidBookingData(booking) || booking.getBookingId() <= 0) {
            System.err.println("❌ Invalid booking data for update");
            return false;
        }

        return bookingDAO.updateBooking(booking);
    }

    @Override
    public boolean cancelBooking(int bookingId, User user) {
        System.out.println("🚫 Cancelling booking: " + bookingId + " by user: " + user.getName());

        // Get booking to verify ownership or admin privileges
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) {
            System.err.println("❌ Booking not found: " + bookingId);
            return false;
        }

        // Check if user can cancel this booking
        if (booking.getUserId() != user.getUserId() && !user.isAdmin()) {
            System.err.println("❌ User not authorized to cancel booking: " + bookingId);
            return false;
        }

        boolean success = bookingDAO.deleteBooking(bookingId);

        if (success) {
            System.out.println("✅ Booking cancelled successfully: " + bookingId);
        } else {
            System.err.println("❌ Booking cancellation failed: " + bookingId);
        }

        return success;
    }

    @Override
    public List<Booking> getPendingBookingsForApproval() {
        System.out.println("⏳ Fetching pending bookings for admin approval");
        return bookingDAO.getPendingBookings();
    }

    @Override
    public boolean approveBooking(int bookingId, User admin) {
        System.out.println("✅ Admin approving booking: " + bookingId + " by " + admin.getName());

        if (!admin.isAdmin()) {
            System.err.println("❌ User does not have admin privileges: " + admin.getName());
            return false;
        }

        boolean success = bookingDAO.approveBooking(bookingId, admin.getUserId());

        if (success) {
            System.out.println("✅ Booking approved successfully by admin: " + admin.getName());
        } else {
            System.err.println("❌ Booking approval failed");
        }

        return success;
    }

    @Override
    public boolean rejectBooking(int bookingId, User admin) {
        System.out.println("❌ Admin rejecting booking: " + bookingId + " by " + admin.getName());

        if (!admin.isAdmin()) {
            System.err.println("❌ User does not have admin privileges: " + admin.getName());
            return false;
        }

        boolean success = bookingDAO.rejectBooking(bookingId, admin.getUserId());

        if (success) {
            System.out.println("❌ Booking rejected by admin: " + admin.getName());
        } else {
            System.err.println("❌ Booking rejection failed");
        }

        return success;
    }

    @Override
    public List<Booking> getBookingsForAdmin() {
        System.out.println("👨‍💼 Fetching all bookings for admin dashboard");
        return bookingDAO.getAllBookings();
    }

    @Override
    public List<Booking> getAllBookings() {
        System.out.println("📋 Fetching all bookings for admin");
        return bookingDAO.getAllBookings();
    }

    @Override
    public List<Booking> getRecentBookings(int limit) {
        System.out.println("📋 Fetching recent " + limit + " bookings");
        return bookingDAO.getRecentBookings(limit);
    }

    @Override
    public boolean isSlotAvailable(int cabinId, Date date, String timeSlot) {
        System.out.println("🔍 Checking slot availability: Cabin " + cabinId + ", Date " + date + ", Time " + timeSlot);

        if (cabinId <= 0 || date == null || timeSlot == null || timeSlot.trim().isEmpty()) {
            System.err.println("❌ Invalid parameters for slot availability check");
            return false;
        }

        // ✅ ENHANCED: Validate time slot format before checking availability
        if (!isValidTimeSlot(timeSlot)) {
            System.err.println("❌ Invalid time slot format: " + timeSlot);
            return false;
        }

        return bookingDAO.isSlotAvailable(cabinId, date, timeSlot);
    }

    @Override
    public List<Booking> getConflictingBookings(int cabinId, Date date, String timeSlot) {
        System.out.println("⚠️ Finding conflicting bookings for: Cabin " + cabinId + ", Date " + date + ", Time " + timeSlot);

        if (cabinId <= 0 || date == null || timeSlot == null) {
            System.err.println("❌ Invalid parameters for conflict check");
            return new ArrayList<>();
        }

        return bookingDAO.getConflictingBookings(cabinId, date, timeSlot);
    }

    // ✅ ENHANCED: Flexible duration time slot generation
    @Override
    public List<String> getAvailableTimeSlots(int cabinId, Date date) {
        System.out.println("🕐 Getting available flexible time slots for cabin: " + cabinId + " on " + date);

        if (cabinId <= 0 || date == null) {
            System.err.println("❌ Invalid parameters for time slots");
            return new ArrayList<>();
        }

        // Use enhanced DAO method for flexible duration slots
        if (bookingDAO instanceof BookingDaoImpl) {
            BookingDaoImpl enhancedDAO = (BookingDaoImpl) bookingDAO;
            return enhancedDAO.getAvailableTimeSlots(cabinId, date, 60); // Default 1 hour
        } else {
            // Fallback to basic fixed slots
            return getBasicAvailableTimeSlots(cabinId, date);
        }
    }

    @Override
    public List<String> getAlternativeTimeSlots(int cabinId, Date date, String requestedSlot) {
        System.out.println("🔄 Finding alternative time slots for: " + requestedSlot);

        // Use enhanced DAO method for smart alternatives
        if (bookingDAO instanceof BookingDaoImpl) {
            BookingDaoImpl enhancedDAO = (BookingDaoImpl) bookingDAO;
            return enhancedDAO.getAlternativeTimeSlots(cabinId, date, requestedSlot, 3);
        } else {
            // Fallback to basic alternatives
            List<String> availableSlots = getAvailableTimeSlots(cabinId, date);
            availableSlots.remove(requestedSlot);
            return availableSlots.size() > 3 ? availableSlots.subList(0, 3) : availableSlots;
        }
    }

    @Override
    public boolean applyVIPPriority(Booking booking, User user) {
        if (user.isVIP()) {
            booking.setPriorityLevel(Booking.PriorityLevel.VIP);
            System.out.println("⭐ VIP priority applied for user: " + user.getName());
            return true;
        } else if (user.isAdmin()) {
            booking.setPriorityLevel(Booking.PriorityLevel.HIGH);
            System.out.println("👨‍💼 Admin priority applied for user: " + user.getName());
            return true;
        } else {
            booking.setPriorityLevel(Booking.PriorityLevel.NORMAL);
            System.out.println("🟢 Normal priority set for user: " + user.getName());
            return false;
        }
    }

    @Override
    public List<Booking> getVIPPriorityBookings() {
        System.out.println("⭐ Fetching VIP priority bookings");

        List<Booking> allBookings = bookingDAO.getAllBookings();
        List<Booking> vipBookings = new ArrayList<>();

        for (Booking booking : allBookings) {
            if (booking.getPriorityLevel() == Booking.PriorityLevel.VIP) {
                vipBookings.add(booking);
            }
        }

        System.out.println("✅ Found " + vipBookings.size() + " VIP priority bookings");
        return vipBookings;
    }

    @Override
    public boolean canOverrideBooking(User user, Booking existingBooking) {
        System.out.println("🔐 Checking booking override permissions for user: " + user.getName());

        if (user.isAdmin()) {
            System.out.println("✅ Admin can override any booking");
            return true;
        }

        if (user.isVIP() && existingBooking.getPriorityLevel() == Booking.PriorityLevel.NORMAL) {
            System.out.println("⭐ VIP can override normal priority booking");
            return true;
        }

        System.out.println("❌ User cannot override this booking");
        return false;
    }

    @Override
    public List<Booking> getBookingsByDateRange(Date startDate, Date endDate) {
        System.out.println("📅 Fetching bookings from " + startDate + " to " + endDate);

        if (startDate == null || endDate == null) {
            System.err.println("❌ Invalid date range");
            return new ArrayList<>();
        }

        return bookingDAO.getBookingsByDateRange(startDate, endDate);
    }

    @Override
    public List<String> getPopularTimeSlots() {
        System.out.println("🌟 Fetching popular time slots");
        return bookingDAO.getPopularTimeSlots();
    }

    @Override
    public int getTotalBookingsCount() {
        System.out.println("📊 Getting total bookings count");
        List<Booking> allBookings = bookingDAO.getAllBookings();
        return allBookings.size();
    }

    @Override
    public int getPendingBookingsCount() {
        System.out.println("⏳ Getting pending bookings count");
        List<Booking> pendingBookings = bookingDAO.getPendingBookings();
        return pendingBookings.size();
    }

    @Override
    public List<Booking> getUserBookingHistory(int userId) {
        System.out.println("📊 Fetching booking history for user: " + userId);

        if (userId <= 0) {
            System.err.println("❌ Invalid user ID for history");
            return new ArrayList<>();
        }

        return bookingDAO.getUserBookingHistory(userId);
    }

    @Override
    public List<Booking> getBookingsByPurpose(String purpose) {
        System.out.println("🎯 Fetching bookings by purpose: " + purpose);

        if (purpose == null || purpose.trim().isEmpty()) {
            System.err.println("❌ Invalid purpose for search");
            return new ArrayList<>();
        }

        return bookingDAO.getBookingsByPurpose(purpose);
    }

    @Override
    public boolean updateUserPreferencesFromBooking(Booking booking) {
        System.out.println("🤖 Updating user preferences from booking for AI learning");

        if (booking == null) {
            System.err.println("❌ Invalid booking for preference update");
            return false;
        }

        try {
            // Get cabin details for preference learning
            Cabin cabin = cabinDAO.getCabinById(booking.getCabinId());
            if (cabin != null) {
                System.out.println("📈 Learning user preferences: Capacity " + cabin.getCapacity() +
                        ", Time " + booking.getTimeSlot() +
                        ", Purpose: " + booking.getPurpose());

                // This will be integrated with AI service later
                // For now, just log the learning activity
                return true;
            }

        } catch (Exception e) {
            System.err.println("❌ Error updating user preferences: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean approveBooking(int bookingId, int adminId) {
        try {
            return bookingDAO.approveBooking(bookingId, adminId);
        } catch (Exception e) {
            System.err.println("❌ Error approving booking: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean rejectBooking(int bookingId, int adminId) {
        try {
            return bookingDAO.rejectBooking(bookingId, adminId);
        } catch (Exception e) {
            System.err.println("❌ Error rejecting booking: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int getBookingCountByUserId(int userId) {
        try {
            String sql = "SELECT COUNT(*) FROM bookings WHERE user_id = ?";

            Connection connection = DbUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();
            int count = 0;

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }

            resultSet.close();
            statement.close();
            connection.close();

            return count;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ================================
    // 🎯 YOUR 3 REQUIREMENTS IMPLEMENTATION
    // ================================

    // ✅ UPDATED: Debug Method for Conflict Detection
    private void debugConflictingBookings(int cabinId, Date date, String timeSlot) {
        System.out.println("🔍 DEBUG: All conflicting bookings for cabin " + cabinId);

        List<Booking> allConflicts = bookingDAO.getConflictingBookings(cabinId, date, timeSlot);

        System.out.println("📊 Total conflicts found: " + allConflicts.size());

        for (Booking booking : allConflicts) {
            System.out.println("📋 Conflict Found:");
            System.out.println("   - Booking ID: " + booking.getBookingId());
            System.out.println("   - User ID: " + booking.getUserId());
            System.out.println("   - Status: " + booking.getStatus());
            System.out.println("   - Priority: " + booking.getPriorityLevel());
            System.out.println("   - Time Slot: " + booking.getTimeSlot());
            System.out.println("   - Date: " + booking.getBookingDate());
            System.out.println("   ----------------");
        }
    }

    // ⭐ REQUIREMENT 1: ENHANCED VIP Override Implementation
    @Override
    public boolean forceBookingForVIP(Booking vipBooking, User vipUser) {
        System.out.println("⭐ VIP Force Booking: " + vipUser.getName() + " for cabin " + vipBooking.getCabinId());

        try {
            // ✅ ADD DEBUG CALL:
            debugConflictingBookings(vipBooking.getCabinId(), vipBooking.getBookingDate(), vipBooking.getTimeSlot());

            // 1. Find conflicting normal bookings
            List<Booking> conflicts = findConflictingNormalBookings(
                    vipBooking.getCabinId(),
                    vipBooking.getBookingDate(),
                    vipBooking.getTimeSlot()
            );

            // ✅ ENHANCED: Process ALL conflicts, not just NORMAL priority
            if (conflicts.isEmpty()) {
                System.out.println("🔍 No NORMAL conflicts found, checking ALL conflicts...");

                // Get ALL conflicting bookings regardless of priority/status
                List<Booking> allConflicts = bookingDAO.getConflictingBookings(
                        vipBooking.getCabinId(),
                        vipBooking.getBookingDate(),
                        vipBooking.getTimeSlot()
                );

                // Process ANY non-VIP booking as conflicting
                for (Booking conflict : allConflicts) {
                    if (conflict.getPriorityLevel() != Booking.PriorityLevel.VIP) {
                        System.out.println("🎯 Processing non-VIP conflict: ID=" + conflict.getBookingId() +
                                ", Status=" + conflict.getStatus() +
                                ", Priority=" + conflict.getPriorityLevel());

                        // Get user for the conflicting booking
                        User normalUser = userService.getUserById(conflict.getUserId());

                        if (normalUser != null) {
                            // Simply reject the conflicting booking to make room for VIP
                            conflict.setStatus(Booking.Status.REJECTED);
                            bookingDAO.updateBooking(conflict);

                            System.out.println("✅ Conflicting booking rejected: " + normalUser.getName() +
                                    " (ID: " + conflict.getBookingId() + ")");
                        }
                    }
                }
            } else {
                // Original logic for NORMAL priority conflicts
                for (Booking conflict : conflicts) {
                    User normalUser = userService.getUserById(conflict.getUserId());
                    List<Cabin> alternatives = getAlternativeCabinsForUser(
                            normalUser,
                            vipBooking.getCabinId(),
                            vipBooking.getBookingDate(),
                            vipBooking.getTimeSlot()
                    );

                    if (!alternatives.isEmpty()) {
                        // Reallocate normal user to first available alternative
                        int newCabinId = alternatives.get(0).getCabinId();
                        conflict.setCabinId(newCabinId);
                        conflict.setStatus(Booking.Status.APPROVED);
                        bookingDAO.updateBooking(conflict);

                        System.out.println("✅ Normal user reallocated: " + normalUser.getName() + " to cabin " + newCabinId);
                    } else {
                        // No alternatives - reject normal booking
                        conflict.setStatus(Booking.Status.REJECTED);
                        bookingDAO.updateBooking(conflict);

                        System.out.println("❌ Normal booking rejected - no alternatives available");
                    }
                }
            }

            // 3. Create VIP booking with highest priority
            vipBooking.setPriorityLevel(Booking.PriorityLevel.VIP);
            vipBooking.setStatus(Booking.Status.APPROVED); // Auto-approve VIP
            vipBooking.setApprovedBy(vipUser.getUserId());
            vipBooking.setApprovedAt(new java.sql.Timestamp(System.currentTimeMillis()));

            boolean success = bookingDAO.createBooking(vipBooking);

            if (success) {
                System.out.println("⭐ VIP booking force-created successfully!");
                return true;
            }

        } catch (Exception e) {
            System.err.println("❌ VIP force booking failed: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<Booking> findConflictingNormalBookings(int cabinId, Date date, String timeSlot) {
        System.out.println("🔍 Finding conflicting normal bookings for cabin " + cabinId);

        List<Booking> allConflicts = bookingDAO.getConflictingBookings(cabinId, date, timeSlot);
        List<Booking> normalConflicts = new ArrayList<>();

        for (Booking booking : allConflicts) {
            // ✅ EXPANDED: Include APPROVED bookings and all non-VIP priorities
            if (booking.getPriorityLevel() != Booking.PriorityLevel.VIP &&
                    (booking.getStatus() == Booking.Status.PENDING ||
                            booking.getStatus() == Booking.Status.APPROVED)) {
                normalConflicts.add(booking);
                System.out.println("🎯 Found conflicting booking: ID=" + booking.getBookingId() +
                        ", Status=" + booking.getStatus() +
                        ", Priority=" + booking.getPriorityLevel());
            }
        }

        System.out.println("📋 Found " + normalConflicts.size() + " conflicting normal bookings");
        return normalConflicts;
    }

    @Override
    public boolean reallocateNormalUserToAlternative(int normalBookingId, User vipUser, int adminId) {
        System.out.println("🔄 Reallocating normal user booking: " + normalBookingId);

        try {
            // Log the reallocation activity
            Booking normalBooking = bookingDAO.getBookingById(normalBookingId);
            if (normalBooking != null) {
                // You can add audit logging here
                System.out.println("📝 Booking " + normalBookingId + " reallocated due to VIP override by " + vipUser.getName());
                return true;
            }
        } catch (Exception e) {
            System.err.println("❌ Reallocation failed: " + e.getMessage());
        }

        return false;
    }

    // 👨💼 REQUIREMENT 2: Admin Cabin Reallocation Implementation
    @Override
    public List<Cabin> getAlternativeCabinsForUser(User user, int excludeCabinId, Date date, String timeSlot) {
        System.out.println("🏠 Finding alternative cabins for user: " + user.getName());

        try {
            List<Cabin> allCabins = cabinDAO.getAccessibleCabins(user);
            List<Cabin> alternatives = new ArrayList<>();

            for (Cabin cabin : allCabins) {
                // Skip the excluded cabin
                if (cabin.getCabinId() == excludeCabinId) {
                    continue;
                }

                // Check if cabin is available for the time slot
                if (bookingDAO.isSlotAvailable(cabin.getCabinId(), date, timeSlot)) {
                    alternatives.add(cabin);
                }
            }

            System.out.println("✅ Found " + alternatives.size() + " alternative cabins");
            return alternatives;

        } catch (Exception e) {
            System.err.println("❌ Error finding alternatives: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean adminReallocateUserCabin(int bookingId, int newCabinId, int adminId, String reason) {
        System.out.println("👨💼 Admin reallocating booking " + bookingId + " to cabin " + newCabinId);

        try {
            Booking booking = bookingDAO.getBookingById(bookingId);
            if (booking == null) {
                System.err.println("❌ Booking not found: " + bookingId);
                return false;
            }

            // Verify new cabin is available
            if (!bookingDAO.isSlotAvailable(newCabinId, booking.getBookingDate(), booking.getTimeSlot())) {
                System.err.println("❌ New cabin not available: " + newCabinId);
                return false;
            }

            // Update booking with new cabin
            int oldCabinId = booking.getCabinId();
            booking.setCabinId(newCabinId);
            booking.setStatus(Booking.Status.APPROVED); // Auto-approve admin reallocation
            booking.setApprovedBy(adminId);
            booking.setApprovedAt(new java.sql.Timestamp(System.currentTimeMillis()));

            boolean success = bookingDAO.updateBooking(booking);

            if (success) {
                // Notify user of reallocation
                notifyUserOfReallocation(booking.getUserId(), oldCabinId, newCabinId, reason);
                System.out.println("✅ Admin reallocation successful");
                return true;
            }

        } catch (Exception e) {
            System.err.println("❌ Admin reallocation failed: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean notifyUserOfReallocation(int userId, int oldCabinId, int newCabinId, String reason) {
        System.out.println("📧 Notifying user " + userId + " of cabin reallocation");

        try {
            // Here you can implement email notification, system notification, etc.
            // For now, just log the notification
            User user = userService.getUserById(userId); // ✅ NOW WORKS
            Cabin oldCabin = cabinDAO.getCabinById(oldCabinId);
            Cabin newCabin = cabinDAO.getCabinById(newCabinId);

            String notification = String.format(
                    "Dear %s, your booking has been moved from %s to %s. Reason: %s",
                    user.getName(),
                    oldCabin.getName(),
                    newCabin.getName(),
                    reason
            );

            System.out.println("📨 Notification: " + notification);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Notification failed: " + e.getMessage());
            return false;
        }
    }

    // 🎯 REQUIREMENT 3: Admin Manual Assignment Implementation
    @Override
    public boolean adminAssignSpecificCabin(int userId, int requestedCabinId, int adminChosenCabinId,
                                            Date date, String timeSlot, String purpose) {
        System.out.println("🎯 Admin assigning specific cabin: " + adminChosenCabinId + " for user: " + userId);

        try {
            User user = userService.getUserById(userId); // ✅ NOW WORKS
            if (user == null) {
                System.err.println("❌ User not found: " + userId);
                return false;
            }

            // Check if admin chosen cabin is available
            if (!bookingDAO.isSlotAvailable(adminChosenCabinId, date, timeSlot)) {
                System.err.println("❌ Admin chosen cabin not available: " + adminChosenCabinId);
                return false;
            }

            // Create booking with admin's choice
            Booking adminChoiceBooking = new Booking();
            adminChoiceBooking.setUserId(userId);
            adminChoiceBooking.setCabinId(adminChosenCabinId);
            adminChoiceBooking.setBookingDate(date);
            adminChoiceBooking.setTimeSlot(timeSlot);
            adminChoiceBooking.setPurpose(purpose + " (Admin assigned alternative)");
            adminChoiceBooking.setStatus(Booking.Status.APPROVED); // Pre-approved by admin
            adminChoiceBooking.setPriorityLevel(user.isVIP() ? Booking.PriorityLevel.VIP : Booking.PriorityLevel.HIGH);

            boolean success = bookingDAO.createBooking(adminChoiceBooking);

            if (success) {
                System.out.println("✅ Admin manual assignment successful");
                return true;
            }

        } catch (Exception e) {
            System.err.println("❌ Admin manual assignment failed: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<Cabin> getSuitableAlternativeCabins(int originalCabinId, User user) {
        System.out.println("🔍 Finding suitable alternatives for cabin: " + originalCabinId);

        try {
            Cabin originalCabin = cabinDAO.getCabinById(originalCabinId);
            List<Cabin> allCabins = cabinDAO.getAccessibleCabins(user);
            List<Cabin> suitable = new ArrayList<>();

            for (Cabin cabin : allCabins) {
                if (cabin.getCabinId() != originalCabinId) {
                    // Prefer cabins with similar or larger capacity
                    if (originalCabin != null && cabin.getCapacity() >= originalCabin.getCapacity()) {
                        suitable.add(cabin);
                    }
                }
            }

            System.out.println("📋 Found " + suitable.size() + " suitable alternatives");
            return suitable;

        } catch (Exception e) {
            System.err.println("❌ Error finding suitable alternatives: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean createBookingWithAdminChoice(Booking originalRequest, int adminChosenCabinId, int adminId) {
        System.out.println("🎯 Creating booking with admin choice");

        try {
            // Update original request with admin's cabin choice
            originalRequest.setCabinId(adminChosenCabinId);
            originalRequest.setStatus(Booking.Status.APPROVED);
            originalRequest.setApprovedBy(adminId);
            originalRequest.setApprovedAt(new java.sql.Timestamp(System.currentTimeMillis()));

            return bookingDAO.createBooking(originalRequest);

        } catch (Exception e) {
            System.err.println("❌ Admin choice booking failed: " + e.getMessage());
            return false;
        }
    }

    // ================================
    // PRIVATE UTILITY METHODS
    // ================================

    private boolean isValidBookingData(Booking booking) {
        if (booking == null) {
            System.err.println("❌ Booking object is null");
            return false;
        }

        if (booking.getCabinId() <= 0) {
            System.err.println("❌ Invalid cabin ID: " + booking.getCabinId());
            return false;
        }

        if (booking.getBookingDate() == null) {
            System.err.println("❌ Booking date is required");
            return false;
        }

        if (booking.getTimeSlot() == null || booking.getTimeSlot().trim().isEmpty()) {
            System.err.println("❌ Time slot is required");
            return false;
        }

        if (booking.getPurpose() == null || booking.getPurpose().trim().isEmpty()) {
            System.err.println("❌ Booking purpose is required");
            return false;
        }

        // Check if booking date is not in the past
        Date today = new Date(System.currentTimeMillis());
        if (booking.getBookingDate().before(today)) {
            System.err.println("❌ Cannot book for past dates: " + booking.getBookingDate());
            return false;
        }

        // ✅ ENHANCED: Flexible time slot validation
        if (!isValidTimeSlot(booking.getTimeSlot())) {
            System.err.println("❌ Invalid time slot format: " + booking.getTimeSlot());
            return false;
        }

        return true;
    }

    // ✅ FIXED: Flexible time slot validation using regex pattern
    private boolean isValidTimeSlot(String timeSlot) {
        if (timeSlot == null || timeSlot.trim().isEmpty()) {
            return false;
        }

        // ✅ ENHANCED: Pattern-based validation for flexible duration
        if (!Pattern.matches(TIME_SLOT_PATTERN, timeSlot)) {
            System.err.println("❌ Time slot doesn't match pattern: " + timeSlot);
            return false;
        }

        try {
            String[] times = timeSlot.split("-");
            String startTime = times[0];
            String endTime = times[1];

            // Parse times
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");

            int startHour = Integer.parseInt(startParts[0]);
            int startMin = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMin = Integer.parseInt(endParts[1]);

            // Calculate duration
            int startTotalMin = startHour * 60 + startMin;
            int endTotalMin = endHour * 60 + endMin;
            int duration = endTotalMin - startTotalMin;

            // Validate business hours
            if (startHour < BUSINESS_START_HOUR || endHour > BUSINESS_END_HOUR) {
                System.err.println("❌ Time slot outside business hours (9 AM - 6 PM): " + timeSlot);
                return false;
            }

            // Validate duration
            if (duration < MIN_DURATION_MINUTES || duration > MAX_DURATION_MINUTES) {
                System.err.println("❌ Duration out of range (15 min - 8 hours): " + duration + " minutes");
                return false;
            }

            // Validate end time is after start time
            if (endTotalMin <= startTotalMin) {
                System.err.println("❌ End time must be after start time: " + timeSlot);
                return false;
            }

            System.out.println("✅ Valid flexible time slot: " + timeSlot + " (Duration: " + duration + " minutes)");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error parsing time slot: " + timeSlot + " - " + e.getMessage());
            return false;
        }
    }

    // ✅ NEW: Fallback method for basic time slots
    private List<String> getBasicAvailableTimeSlots(int cabinId, Date date) {
        List<String> allTimeSlots = getAllTimeSlots();
        List<String> availableSlots = new ArrayList<>();

        for (String timeSlot : allTimeSlots) {
            if (bookingDAO.isSlotAvailable(cabinId, date, timeSlot)) {
                availableSlots.add(timeSlot);
            }
        }

        System.out.println("✅ Found " + availableSlots.size() + " basic available time slots");
        return availableSlots;
    }

    // ✅ ENHANCED: Extended time slots including flexible durations
    private List<String> getAllTimeSlots() {
        List<String> timeSlots = new ArrayList<>();

        // Basic 1-hour slots
        timeSlots.add("09:00-10:00");
        timeSlots.add("10:00-11:00");
        timeSlots.add("11:00-12:00");
        timeSlots.add("12:00-13:00");
        timeSlots.add("14:00-15:00");
        timeSlots.add("15:00-16:00");
        timeSlots.add("16:00-17:00");
        timeSlots.add("17:00-18:00");

        // ✅ NEW: Add common flexible duration slots
        timeSlots.add("09:00-09:30");
        timeSlots.add("09:30-10:00");
        timeSlots.add("10:00-10:30");
        timeSlots.add("10:30-11:00");
        timeSlots.add("11:00-11:30");
        timeSlots.add("11:30-12:00");
        timeSlots.add("14:00-14:30");
        timeSlots.add("14:30-15:00");
        timeSlots.add("15:00-15:30");
        timeSlots.add("15:30-16:00");
        timeSlots.add("16:00-16:30");
        timeSlots.add("16:30-17:00");
        timeSlots.add("17:00-17:30");
        timeSlots.add("17:30-18:00");

        // 2-hour slots
        timeSlots.add("09:00-11:00");
        timeSlots.add("10:00-12:00");
        timeSlots.add("14:00-16:00");
        timeSlots.add("15:00-17:00");
        timeSlots.add("16:00-18:00");

        return timeSlots;
    }
}
