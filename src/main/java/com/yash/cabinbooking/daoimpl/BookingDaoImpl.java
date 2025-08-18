package com.yash.cabinbooking.daoimpl;

import com.yash.cabinbooking.dao.BookingDao;
import com.yash.cabinbooking.model.Booking;
import com.yash.cabinbooking.util.DbUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDaoImpl implements BookingDao {

    // ✅ FIXED: Updated base SQL with rejection fields
    private static final String BASE_SELECT_SQL =
            "SELECT b.booking_id, b.user_id, b.cabin_id, b.booking_date, b.time_slot, b.purpose, " +
                    "b.booking_type, b.status, b.priority_level, b.created_at, b.approved_by, b.approved_at, " +
                    "b.rejected_by, b.rejected_at, " + // ✅ ADDED REJECTION FIELDS
                    "u.name as user_name, c.name as cabin_name " +
                    "FROM bookings b " +
                    "LEFT JOIN users u ON b.user_id = u.user_id " +
                    "LEFT JOIN cabins c ON b.cabin_id = c.cabin_id ";

    @Override
    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, cabin_id, booking_date, time_slot, purpose, booking_type, status, priority_level) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) {
                System.err.println("❌ Database connection failed in createBooking");
                return false;
            }

            if (!isValidTimeSlotFormat(booking.getTimeSlot())) {
                System.err.println("❌ Invalid time slot format: " + booking.getTimeSlot());
                return false;
            }

            if (!isSlotAvailable(booking.getCabinId(), booking.getBookingDate(), booking.getTimeSlot())) {
                System.err.println("❌ Time slot not available: " + booking.getTimeSlot() + " on " + booking.getBookingDate());
                return false;
            }

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, booking.getUserId());
            pstmt.setInt(2, booking.getCabinId());
            pstmt.setDate(3, booking.getBookingDate());
            pstmt.setString(4, booking.getTimeSlot());
            pstmt.setString(5, booking.getPurpose());
            pstmt.setString(6, booking.getBookingType().name());
            pstmt.setString(7, booking.getStatus().name());
            pstmt.setString(8, booking.getPriorityLevel().name());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    booking.setBookingId(generatedKeys.getInt(1));
                }
                System.out.println("✅ Booking created successfully: ID " + booking.getBookingId() + " for user " + booking.getUserId());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL Error in createBooking: " + e.getMessage());
            if (e.getErrorCode() == 1062) {
                System.err.println("🔄 Time slot already booked: " + booking.getTimeSlot());
            }
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    @Override
    public Booking getBookingById(int bookingId) {
        String sql = BASE_SELECT_SQL + "WHERE b.booking_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return null;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookingId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Booking booking = mapResultSetToBooking(rs);
                System.out.println("✅ Booking found: ID " + bookingId);
                return booking;
            } else {
                System.out.println("❌ Booking not found with ID: " + bookingId);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting booking by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return null;
    }

    // ✅ FIXED: getAllBookings with rejection fields
    @Override
    public List<Booking> getAllBookings() {
        String sql = BASE_SELECT_SQL + "ORDER BY b.created_at DESC";

        List<Booking> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) {
                System.err.println("❌ Database connection failed in getAllBookings");
                return bookings;
            }

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                try {
                    Booking booking = mapResultSetToBooking(rs);
                    bookings.add(booking);
                } catch (SQLException e) {
                    System.err.println("⚠️ Error mapping booking row: " + e.getMessage());
                    // Continue with next row
                }
            }

            System.out.println("✅ Retrieved " + bookings.size() + " bookings from database");

        } catch (SQLException e) {
            System.err.println("❌ Error getting all bookings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return bookings;
    }

    @Override
    public List<Booking> getRecentBookings(int limit) {
        String sql = BASE_SELECT_SQL + "ORDER BY b.created_at DESC LIMIT ?";

        List<Booking> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return bookings;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            System.out.println("✅ Retrieved " + bookings.size() + " recent bookings");

        } catch (SQLException e) {
            System.err.println("❌ Error getting recent bookings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return bookings;
    }

    @Override
    public List<Booking> getBookingsByUser(int userId) {
        String sql = BASE_SELECT_SQL + "WHERE b.user_id = ? ORDER BY b.booking_date DESC, b.time_slot";

        List<Booking> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return bookings;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            System.out.println("✅ Retrieved " + bookings.size() + " bookings for user: " + userId);

        } catch (SQLException e) {
            System.err.println("❌ Error getting bookings by user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return bookings;
    }

    @Override
    public List<Booking> getBookingsByCabin(int cabinId) {
        String sql = BASE_SELECT_SQL + "WHERE b.cabin_id = ? ORDER BY b.booking_date DESC, b.time_slot";

        List<Booking> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return bookings;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cabinId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            System.out.println("✅ Retrieved " + bookings.size() + " bookings for cabin: " + cabinId);

        } catch (SQLException e) {
            System.err.println("❌ Error getting bookings by cabin: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return bookings;
    }

    // ✅ FIXED: Enhanced updateBooking method
    @Override
    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE bookings SET user_id = ?, cabin_id = ?, booking_date = ?, time_slot = ?, " +
                "purpose = ?, booking_type = ?, status = ?, priority_level = ?, " +
                "approved_by = ?, approved_at = ?, rejected_by = ?, rejected_at = ? " +
                "WHERE booking_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, booking.getUserId());
            pstmt.setInt(2, booking.getCabinId());
            pstmt.setDate(3, booking.getBookingDate());
            pstmt.setString(4, booking.getTimeSlot());
            pstmt.setString(5, booking.getPurpose());
            pstmt.setString(6, booking.getBookingType().name());
            pstmt.setString(7, booking.getStatus().name());
            pstmt.setString(8, booking.getPriorityLevel().name());

            // ✅ Handle nullable approved fields
            if (booking.getApprovedBy() > 0) {
                pstmt.setInt(9, booking.getApprovedBy());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }

            if (booking.getApprovedAt() != null) {
                pstmt.setTimestamp(10, booking.getApprovedAt());
            } else {
                pstmt.setNull(10, Types.TIMESTAMP);
            }

            // ✅ Handle nullable rejected fields
            if (booking.getRejectedBy() > 0) {
                pstmt.setInt(11, booking.getRejectedBy());
            } else {
                pstmt.setNull(11, Types.INTEGER);
            }

            if (booking.getRejectedAt() != null) {
                pstmt.setTimestamp(12, booking.getRejectedAt());
            } else {
                pstmt.setNull(12, Types.TIMESTAMP);
            }

            pstmt.setInt(13, booking.getBookingId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Booking updated successfully: " + booking.getBookingId());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating booking: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    @Override
    public boolean deleteBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookingId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Booking cancelled successfully: " + bookingId);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error deleting booking: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    @Override
    public List<Booking> getPendingBookings() {
        String sql = BASE_SELECT_SQL +
                "WHERE b.status = 'PENDING' " +
                "ORDER BY b.priority_level DESC, b.created_at ASC";

        List<Booking> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return bookings;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            System.out.println("⏳ Retrieved " + bookings.size() + " pending bookings for admin review");

        } catch (SQLException e) {
            System.err.println("❌ Error getting pending bookings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return bookings;
    }

    @Override
    public List<Booking> getApprovedBookings() {
        String sql = BASE_SELECT_SQL +
                "WHERE b.status = 'APPROVED' " +
                "ORDER BY b.booking_date DESC, b.time_slot";

        List<Booking> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return bookings;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            System.out.println("✅ Retrieved " + bookings.size() + " approved bookings");

        } catch (SQLException e) {
            System.err.println("❌ Error getting approved bookings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return bookings;
    }

    @Override
    public List<Booking> getBookingsByDate(Date date) {
        String sql = BASE_SELECT_SQL +
                "WHERE b.booking_date = ? " +
                "ORDER BY b.time_slot, c.name";

        List<Booking> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return bookings;

            pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, date);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            System.out.println("📅 Retrieved " + bookings.size() + " bookings for date: " + date);

        } catch (SQLException e) {
            System.err.println("❌ Error getting bookings by date: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return bookings;
    }

    @Override
    public List<Booking> getBookingsByDateRange(Date startDate, Date endDate) {
        String sql = BASE_SELECT_SQL +
                "WHERE b.booking_date BETWEEN ? AND ? " +
                "ORDER BY b.booking_date, b.time_slot";

        List<Booking> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return bookings;

            pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            System.out.println("📅 Retrieved " + bookings.size() + " bookings from " + startDate + " to " + endDate);

        } catch (SQLException e) {
            System.err.println("❌ Error getting bookings by date range: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return bookings;
    }

    @Override
    public boolean approveBooking(int bookingId, int approvedBy) {
        String sql = "UPDATE bookings SET status = 'APPROVED', approved_by = ?, approved_at = CURRENT_TIMESTAMP WHERE booking_id = ? AND status = 'PENDING'";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, approvedBy);
            pstmt.setInt(2, bookingId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Booking approved successfully: " + bookingId + " by admin: " + approvedBy);
                return true;
            } else {
                System.out.println("❌ Booking approval failed - may already be processed: " + bookingId);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error approving booking: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    // ✅ MAIN FIX: Corrected rejectBooking method
    @Override
    public boolean rejectBooking(int bookingId, int rejectedBy) { // ✅ FIXED: Parameter name
        String sql = "UPDATE bookings SET status = 'REJECTED', rejected_by = ?, rejected_at = CURRENT_TIMESTAMP WHERE booking_id = ? AND status = 'PENDING'";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, rejectedBy);  // ✅ FIXED: Use rejected_by instead of approved_by
            pstmt.setInt(2, bookingId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("❌ Booking rejected successfully: " + bookingId + " by admin: " + rejectedBy);
                return true;
            } else {
                System.out.println("❌ Booking rejection failed - may already be processed: " + bookingId);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error rejecting booking: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    @Override
    public boolean isSlotAvailable(int cabinId, Date date, String timeSlot) {
        if (timeSlot == null || timeSlot.trim().isEmpty()) {
            System.err.println("❌ Empty time slot provided for availability check");
            return false;
        }

        if (!isValidTimeSlotFormat(timeSlot)) {
            System.err.println("❌ Invalid time slot format: " + timeSlot);
            return false;
        }

        String sql = "SELECT COUNT(*) FROM bookings WHERE cabin_id = ? AND booking_date = ? AND status IN ('PENDING', 'APPROVED') AND " +
                "((SUBSTRING(time_slot, 1, 5) < SUBSTRING(?, 7, 5)) AND (SUBSTRING(time_slot, 7, 5) > SUBSTRING(?, 1, 5)))";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) {
                System.err.println("❌ Database connection failed in isSlotAvailable");
                return false;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cabinId);
            pstmt.setDate(2, date);
            pstmt.setString(3, timeSlot);
            pstmt.setString(4, timeSlot);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                boolean available = (count == 0);
                System.out.println("🔍 Enhanced availability check - Cabin: " + cabinId + ", Date: " + date + ", Time: " + timeSlot + " -> " + (available ? "✅ AVAILABLE" : "❌ CONFLICT"));
                return available;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error checking slot availability: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return false;
    }

    @Override
    public List<Booking> getConflictingBookings(int cabinId, Date date, String timeSlot) {
        String sql = BASE_SELECT_SQL +
                "WHERE b.cabin_id = ? AND b.booking_date = ? AND b.status IN ('PENDING', 'APPROVED') AND " +
                "((SUBSTRING(b.time_slot, 1, 5) < SUBSTRING(?, 7, 5)) AND (SUBSTRING(b.time_slot, 7, 5) > SUBSTRING(?, 1, 5)))";

        List<Booking> conflicts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return conflicts;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cabinId);
            pstmt.setDate(2, date);
            pstmt.setString(3, timeSlot);
            pstmt.setString(4, timeSlot);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                conflicts.add(mapResultSetToBooking(rs));
            }

            System.out.println("⚠️ Found " + conflicts.size() + " conflicting bookings");

        } catch (SQLException e) {
            System.err.println("❌ Error getting conflicting bookings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return conflicts;
    }

    // ✅ Continue with all other methods from your original implementation...
    // (getAvailableTimeSlots, getAlternativeTimeSlots, etc. - keeping them as they are)

    @Override
    public List<String> getAvailableTimeSlots(int cabinId, Date date, int durationMinutes) {
        List<String> availableSlots = new ArrayList<>();

        int startHour = 9;
        int endHour = 18;
        int interval = 15;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return availableSlots;

            String sql = "SELECT time_slot FROM bookings WHERE cabin_id = ? AND booking_date = ? AND status IN ('PENDING', 'APPROVED')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cabinId);
            pstmt.setDate(2, date);
            rs = pstmt.executeQuery();

            List<String> bookedSlots = new ArrayList<>();
            while (rs.next()) {
                bookedSlots.add(rs.getString("time_slot"));
            }

            for (int hour = startHour; hour < endHour; hour++) {
                for (int minute = 0; minute < 60; minute += interval) {
                    String startTime = String.format("%02d:%02d", hour, minute);

                    int totalMinutes = hour * 60 + minute + durationMinutes;
                    int endHourCalc = totalMinutes / 60;
                    int endMinute = totalMinutes % 60;

                    if (endHourCalc <= endHour && (endHourCalc < endHour || endMinute == 0)) {
                        String endTime = String.format("%02d:%02d", endHourCalc, endMinute);
                        String timeSlot = startTime + "-" + endTime;

                        if (!hasTimeConflict(timeSlot, bookedSlots)) {
                            availableSlots.add(timeSlot);
                        }
                    }
                }
            }

            System.out.println("🕐 Generated " + availableSlots.size() + " available slots for " + durationMinutes + " minutes duration");

        } catch (SQLException e) {
            System.err.println("❌ Error getting available time slots: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return availableSlots;
    }

    @Override
    public List<String> getAlternativeTimeSlots(int cabinId, Date date, String requestedSlot, int maxAlternatives) {
        List<String> alternatives = new ArrayList<>();

        if (requestedSlot == null || requestedSlot.trim().isEmpty()) {
            return alternatives;
        }

        try {
            int duration = calculateSlotDuration(requestedSlot);
            if (duration <= 0) {
                duration = 60;
            }

            List<String> availableSlots = getAvailableTimeSlots(cabinId, date, duration);

            for (int i = 0; i < Math.min(maxAlternatives, availableSlots.size()); i++) {
                alternatives.add(availableSlots.get(i));
            }

            System.out.println("🔄 Found " + alternatives.size() + " alternative time slots for " + duration + " minutes");

        } catch (Exception e) {
            System.err.println("❌ Error getting alternative time slots: " + e.getMessage());
            e.printStackTrace();
        }

        return alternatives;
    }

    @Override
    public List<Integer> getAvailableDurations() {
        List<Integer> durations = new ArrayList<>();
        durations.add(15);
        durations.add(30);
        durations.add(45);
        durations.add(60);
        durations.add(90);
        durations.add(120);
        durations.add(180);
        durations.add(240);
        return durations;
    }

    @Override
    public List<String> generateTimeSlotsForDuration(int durationMinutes) {
        List<String> timeSlots = new ArrayList<>();

        int startHour = 9;
        int endHour = 18;
        int interval = 15;

        for (int hour = startHour; hour < endHour; hour++) {
            for (int minute = 0; minute < 60; minute += interval) {
                String startTime = String.format("%02d:%02d", hour, minute);

                int totalMinutes = hour * 60 + minute + durationMinutes;
                int endHourCalc = totalMinutes / 60;
                int endMinute = totalMinutes % 60;

                if (endHourCalc <= endHour && (endHourCalc < endHour || endMinute == 0)) {
                    String endTime = String.format("%02d:%02d", endHourCalc, endMinute);
                    timeSlots.add(startTime + "-" + endTime);
                }
            }
        }

        return timeSlots;
    }

    @Override
    public List<Booking> getUserBookingHistory(int userId) {
        String sql = BASE_SELECT_SQL +
                "WHERE b.user_id = ? AND b.status = 'APPROVED' " +
                "ORDER BY b.booking_date DESC LIMIT 20";

        List<Booking> history = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return history;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                history.add(mapResultSetToBooking(rs));
            }

            System.out.println("📊 Retrieved " + history.size() + " booking history records for user: " + userId);

        } catch (SQLException e) {
            System.err.println("❌ Error getting user booking history: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return history;
    }

    @Override
    public List<String> getPopularTimeSlots() {
        String sql = "SELECT time_slot, COUNT(*) as booking_count " +
                "FROM bookings " +
                "WHERE status = 'APPROVED' " +
                "GROUP BY time_slot " +
                "ORDER BY booking_count DESC " +
                "LIMIT 5";

        List<String> timeSlots = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return timeSlots;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String timeSlot = rs.getString("time_slot");
                int count = rs.getInt("booking_count");
                timeSlots.add(timeSlot);
                System.out.println("⭐ Popular time slot: " + timeSlot + " (" + count + " bookings)");
            }

            System.out.println("🕐 Retrieved " + timeSlots.size() + " popular time slots");

        } catch (SQLException e) {
            System.err.println("❌ Error getting popular time slots: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return timeSlots;
    }

    @Override
    public List<Booking> getBookingsByPurpose(String purpose) {
        String sql = BASE_SELECT_SQL +
                "WHERE b.purpose LIKE ? AND b.status = 'APPROVED' " +
                "ORDER BY b.created_at DESC LIMIT 10";

        List<Booking> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return bookings;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + purpose + "%");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            System.out.println("🎯 Retrieved " + bookings.size() + " bookings with purpose: " + purpose);

        } catch (SQLException e) {
            System.err.println("❌ Error getting bookings by purpose: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return bookings;
    }

    @Override
    public List<Booking> getBookingsByStatus(String status) {
        String sql = BASE_SELECT_SQL +
                "WHERE b.status = ? " +
                "ORDER BY b.created_at DESC";

        List<Booking> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return bookings;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            System.out.println("📊 Retrieved " + bookings.size() + " bookings with status: " + status);

        } catch (SQLException e) {
            System.err.println("❌ Error getting bookings by status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return bookings;
    }

    @Override
    public List<Booking> getVIPBookings() {
        String sql = BASE_SELECT_SQL +
                "WHERE b.priority_level = 'VIP' " +
                "ORDER BY b.created_at DESC";

        List<Booking> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return bookings;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            System.out.println("⭐ Retrieved " + bookings.size() + " VIP bookings");

        } catch (SQLException e) {
            System.err.println("❌ Error getting VIP bookings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return bookings;
    }

    @Override
    public List<Booking> getUrgentBookings() {
        String sql = BASE_SELECT_SQL +
                "WHERE (b.priority_level IN ('VIP', 'HIGH') AND b.status = 'PENDING') " +
                "OR (b.booking_type = 'EMERGENCY') " +
                "ORDER BY b.priority_level DESC, b.created_at ASC";

        List<Booking> bookings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return bookings;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            System.out.println("🚨 Retrieved " + bookings.size() + " urgent bookings requiring attention");

        } catch (SQLException e) {
            System.err.println("❌ Error getting urgent bookings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return bookings;
    }

    @Override
    public int getBookingCountByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return 0;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting booking count by user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return 0;
    }

    @Override
    public int getBookingCountByCabin(int cabinId) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE cabin_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return 0;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cabinId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting booking count by cabin: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return 0;
    }

    @Override
    public int getTotalBookingCount() {
        String sql = "SELECT COUNT(*) FROM bookings";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return 0;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting total booking count: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return 0;
    }

    // ================================
    // PRIVATE UTILITY METHODS
    // ================================

    private boolean hasTimeConflict(String newSlot, List<String> existingSlots) {
        try {
            String[] newTimes = newSlot.split("-");
            if (newTimes.length != 2) return true;

            String newStart = newTimes[0];
            String newEnd = newTimes[1];

            for (String existingSlot : existingSlots) {
                String[] existingTimes = existingSlot.split("-");
                if (existingTimes.length != 2) continue;

                String existingStart = existingTimes[0];
                String existingEnd = existingTimes[1];

                if (timeOverlaps(newStart, newEnd, existingStart, existingEnd)) {
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            System.err.println("❌ Error checking time conflict: " + e.getMessage());
            return true;
        }
    }

    private boolean timeOverlaps(String start1, String end1, String start2, String end2) {
        try {
            int start1Minutes = timeToMinutes(start1);
            int end1Minutes = timeToMinutes(end1);
            int start2Minutes = timeToMinutes(start2);
            int end2Minutes = timeToMinutes(end2);

            return (start1Minutes < end2Minutes && end1Minutes > start2Minutes);

        } catch (Exception e) {
            return true;
        }
    }

    private int timeToMinutes(String time) {
        try {
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours * 60 + minutes;
        } catch (Exception e) {
            return 0;
        }
    }

    private int calculateSlotDuration(String timeSlot) {
        try {
            String[] times = timeSlot.split("-");
            if (times.length != 2) return 60;

            int startMinutes = timeToMinutes(times[0]);
            int endMinutes = timeToMinutes(times[1]);
            return endMinutes - startMinutes;
        } catch (Exception e) {
            return 60;
        }
    }

    private boolean isValidTimeSlotFormat(String timeSlot) {
        if (timeSlot == null || timeSlot.trim().isEmpty()) {
            return false;
        }

        String pattern = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]-([0-1]?[0-9]|2[0-3]):[0-5][0-9]$";

        if (!timeSlot.matches(pattern)) {
            return false;
        }

        try {
            String[] times = timeSlot.split("-");
            int startMinutes = timeToMinutes(times[0]);
            int endMinutes = timeToMinutes(times[1]);

            if (endMinutes <= startMinutes) {
                return false;
            }

            if ((endMinutes - startMinutes) > 480) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // ✅ MAIN FIX: Enhanced mapResultSetToBooking with rejection fields
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setCabinId(rs.getInt("cabin_id"));
        booking.setBookingDate(rs.getDate("booking_date"));
        booking.setTimeSlot(rs.getString("time_slot"));
        booking.setPurpose(rs.getString("purpose"));

        // Handle booking type safely
        String bookingTypeStr = rs.getString("booking_type");
        if (bookingTypeStr != null && !bookingTypeStr.trim().isEmpty()) {
            try {
                booking.setBookingType(Booking.BookingType.valueOf(bookingTypeStr));
            } catch (IllegalArgumentException e) {
                System.err.println("⚠️ Invalid booking type: " + bookingTypeStr + ", defaulting to SINGLE_DAY");
                booking.setBookingType(Booking.BookingType.SINGLE_DAY);
            }
        } else {
            booking.setBookingType(Booking.BookingType.SINGLE_DAY);
        }

        // Handle status safely with fromString method
        String statusStr = rs.getString("status");
        booking.setStatus(Booking.Status.fromString(statusStr));

        // Handle priority level safely with fromString method
        String priorityStr = rs.getString("priority_level");
        booking.setPriorityLevel(Booking.PriorityLevel.fromString(priorityStr));

        booking.setCreatedAt(rs.getTimestamp("created_at"));

        // Handle nullable approved fields safely
        int approvedBy = rs.getInt("approved_by");
        if (!rs.wasNull()) {
            booking.setApprovedBy(approvedBy);
        }

        Timestamp approvedAt = rs.getTimestamp("approved_at");
        if (approvedAt != null) {
            booking.setApprovedAt(approvedAt);
        }

        // ✅ MAIN FIX: Handle nullable rejected fields
        try {
            int rejectedBy = rs.getInt("rejected_by");
            if (!rs.wasNull()) {
                booking.setRejectedBy(rejectedBy);
            }

            Timestamp rejectedAt = rs.getTimestamp("rejected_at");
            if (rejectedAt != null) {
                booking.setRejectedAt(rejectedAt);
            }
        } catch (SQLException e) {
            // Columns might not exist in some queries - handle gracefully
            System.out.println("⚠️ Rejected fields not available in this query (this is normal for some operations)");
        }

        // Set display fields if available
        booking.setUserName(rs.getString("user_name"));
        booking.setCabinName(rs.getString("cabin_name"));

        return booking;
    }
}
