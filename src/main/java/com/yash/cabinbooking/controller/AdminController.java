package com.yash.cabinbooking.controller;

import com.yash.cabinbooking.model.User;
import com.yash.cabinbooking.model.Cabin;
import com.yash.cabinbooking.model.Booking;
import com.yash.cabinbooking.service.*;
import com.yash.cabinbooking.serviceimpl.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.*;

/**
 * ✅ COMPLETE ADMIN CONTROLLER - WITH ALL ENDPOINTS FIXED
 */
@WebServlet(urlPatterns = {
        "/admin/dashboard",
        "/admin/analytics",
        "/admin/users",
        "/admin/bookings",
        "/admin/promote-user",
        "/admin/demote-user",
        "/admin/approve-booking",    // ✅ ADDED
        "/admin/reject-booking",     // ✅ ADDED - MAIN FIX
        "/admin/bulk-approve",       // ✅ ADDED
        "/admin/bulk-reject",        // ✅ ADDED
        "/admin/force-vip-booking",  // ✅ ADDED
        "/admin/reallocate-cabin",   // ✅ ADDED
        "/admin/assign-cabin"        // ✅ ADDED
})
public class AdminController extends HttpServlet {

    private BookingService bookingService;
    private UserService userService;
    private CompanyService companyService;
    private CabinService cabinService;

    @Override
    public void init() throws ServletException {
        try {
            this.bookingService = new BookingServiceImpl();
            this.userService = new UserServiceImpl();
            this.companyService = new CompanyServiceImpl();
            this.cabinService = new CabinServiceImpl();
            System.out.println("🔧 AdminController initialized for Yash Technology - ALL ENDPOINTS LOADED");
        } catch (Exception e) {
            throw new ServletException("AdminController initialization failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        String action = "dashboard";
        if (servletPath.startsWith("/admin/")) {
            action = servletPath.substring(7);
        }

        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            System.out.println("🔒 Unauthorized admin access attempt");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        System.out.println("🌐 Admin GET Request: " + action + " by user: " + currentUser.getName());

        try {
            switch (action) {
                case "dashboard":
                case "":
                    showAdminDashboard(request, response, currentUser);
                    break;
                case "analytics":
                    showAnalytics(request, response, currentUser);
                    break;
                case "users":
                    showUserManagement(request, response, currentUser);
                    break;
                case "bookings":
                    showBookingManagement(request, response, currentUser);
                    break;
                case "cabins":
                    showCabinManagement(request, response, currentUser);
                    break;
                case "promote-user":
                    handlePromoteUser(request, response, currentUser);
                    break;
                case "demote-user":
                    handleDemoteUser(request, response, currentUser);
                    break;
                default:
                    showAdminDashboard(request, response, currentUser);
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ Error in admin GET: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error processing admin request", e);
        }
    }

    // ✅ COMPLETE UPDATED POST METHOD WITH PROPER ROUTING
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User admin = getCurrentUser(request);
        if (admin == null || !admin.isAdmin()) {
            System.err.println("❌ Unauthorized admin POST request");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"error\": \"Unauthorized access\"}");
            return;
        }

        // ✅ GET ACTION FROM URL PATH (NOT PARAMETER)
        String servletPath = request.getServletPath();
        String action = "";

        if (servletPath.startsWith("/admin/")) {
            action = servletPath.substring(7); // Remove "/admin/" prefix
        }

        System.out.println("📝 Admin POST Request: " + action + " by user: " + admin.getName());

        try {
            switch (action) {
                case "approve-booking":
                    handleApproveBooking(request, response, admin);
                    break;
                case "reject-booking":  // ✅ MAIN FIX - THIS WAS MISSING!
                    handleRejectBooking(request, response, admin);
                    break;
                case "bulk-approve":
                    handleBulkApprove(request, response, admin);
                    break;
                case "bulk-reject":
                    handleBulkReject(request, response, admin);
                    break;
                case "force-vip-booking":
                    handleVipForceBooking(request, response, admin);
                    break;
                case "reallocate-cabin":
                    handleCabinReallocation(request, response, admin);
                    break;
                case "assign-cabin":
                    handleSpecificCabinAssignment(request, response, admin);
                    break;
                default:
                    // ✅ FALLBACK: Try parameter-based action for backward compatibility
                    String paramAction = request.getParameter("action");
                    if (paramAction != null) {
                        handleParameterBasedAction(request, response, admin, paramAction);
                    } else {
                        System.err.println("❌ Unknown admin action: " + action);
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action not found: " + action);
                    }
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ Error in admin POST: " + e.getMessage());
            e.printStackTrace();

            // ✅ PROPER ERROR RESPONSE
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
            } else {
                request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
            }
        }
    }

    // ✅ APPROVE BOOKING HANDLER
    private void handleApproveBooking(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        System.out.println("✅ Processing booking approval by admin: " + admin.getName());

        try {
            String bookingIdStr = request.getParameter("bookingId");

            if (bookingIdStr == null || bookingIdStr.trim().isEmpty()) {
                System.err.println("❌ No booking ID provided for approval");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"error\": \"Booking ID required\"}");
                return;
            }

            int bookingId = Integer.parseInt(bookingIdStr);
            boolean success = bookingService.approveBooking(bookingId, admin.getUserId());

            if (success) {
                System.out.println("✅ Booking approved successfully: " + bookingId);

                // ✅ CHECK IF AJAX REQUEST
                if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": true, \"message\": \"Booking approved successfully!\"}");
                } else {
                    request.getSession().setAttribute("successMessage", "Booking #" + bookingId + " approved successfully!");
                    response.sendRedirect(request.getContextPath() + "/admin/bookings");
                }
            } else {
                System.err.println("❌ Booking approval failed: " + bookingId);

                if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": false, \"error\": \"Approval failed\"}");
                } else {
                    request.getSession().setAttribute("errorMessage", "Failed to approve booking #" + bookingId);
                    response.sendRedirect(request.getContextPath() + "/admin/bookings");
                }
            }

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid booking ID format: " + request.getParameter("bookingId"));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"error\": \"Invalid booking ID\"}");
        } catch (Exception e) {
            System.err.println("❌ Error approving booking: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"error\": \"Server error: " + e.getMessage() + "\"}");
        }
    }

    // ✅ REJECT BOOKING HANDLER - MAIN FIX!
    private void handleRejectBooking(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        System.out.println("❌ Processing booking rejection by admin: " + admin.getName());

        try {
            String bookingIdStr = request.getParameter("bookingId");
            String reason = request.getParameter("reason");

            if (bookingIdStr == null || bookingIdStr.trim().isEmpty()) {
                System.err.println("❌ No booking ID provided for rejection");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"error\": \"Booking ID required\"}");
                return;
            }

            int bookingId = Integer.parseInt(bookingIdStr);

            // ✅ ADD REASON IF PROVIDED
            if (reason == null || reason.trim().isEmpty()) {
                reason = "Rejected by admin";
            }

            System.out.println("❌ Rejecting booking " + bookingId + " with reason: " + reason);

            // ✅ USE PROPER SERVICE METHOD
            boolean success = bookingService.rejectBooking(bookingId, admin.getUserId());

            if (success) {
                System.out.println("❌ Booking rejected successfully: " + bookingId);

                // ✅ CHECK IF AJAX REQUEST
                if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": true, \"message\": \"Booking rejected successfully!\"}");
                } else {
                    request.getSession().setAttribute("successMessage", "Booking #" + bookingId + " rejected successfully!");
                    response.sendRedirect(request.getContextPath() + "/admin/bookings");
                }
            } else {
                System.err.println("❌ Booking rejection failed: " + bookingId);

                if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": false, \"error\": \"Rejection failed\"}");
                } else {
                    request.getSession().setAttribute("errorMessage", "Failed to reject booking #" + bookingId);
                    response.sendRedirect(request.getContextPath() + "/admin/bookings");
                }
            }

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid booking ID format: " + request.getParameter("bookingId"));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"error\": \"Invalid booking ID\"}");
        } catch (Exception e) {
            System.err.println("❌ Error rejecting booking: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"error\": \"Server error: " + e.getMessage() + "\"}");
        }
    }

    // ✅ BULK APPROVE HANDLER
    private void handleBulkApprove(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        System.out.println("✅ Processing bulk approval by admin: " + admin.getName());

        try {
            String[] bookingIds = request.getParameterValues("bookingIds");
            int successCount = 0;
            int totalCount = 0;

            if (bookingIds != null) {
                totalCount = bookingIds.length;
                for (String idStr : bookingIds) {
                    try {
                        int bookingId = Integer.parseInt(idStr);
                        if (bookingService.approveBooking(bookingId, admin.getUserId())) {
                            successCount++;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("❌ Invalid booking ID in bulk: " + idStr);
                    }
                }
            }

            String message = "Bulk approval completed: " + successCount + "/" + totalCount + " bookings approved";
            System.out.println("✅ " + message);

            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": true, \"message\": \"" + message + "\"}");
            } else {
                request.getSession().setAttribute("successMessage", message);
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
            }

        } catch (Exception e) {
            System.err.println("❌ Error in bulk approval: " + e.getMessage());

            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"error\": \"Bulk approval failed\"}");
            } else {
                request.getSession().setAttribute("errorMessage", "Bulk approval failed");
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
            }
        }
    }

    // ✅ BULK REJECT HANDLER
    private void handleBulkReject(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        System.out.println("❌ Processing bulk rejection by admin: " + admin.getName());

        try {
            String[] bookingIds = request.getParameterValues("bookingIds");
            int successCount = 0;
            int totalCount = 0;

            if (bookingIds != null) {
                totalCount = bookingIds.length;
                for (String idStr : bookingIds) {
                    try {
                        int bookingId = Integer.parseInt(idStr);
                        if (bookingService.rejectBooking(bookingId, admin.getUserId())) {
                            successCount++;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("❌ Invalid booking ID in bulk: " + idStr);
                    }
                }
            }

            String message = "Bulk rejection completed: " + successCount + "/" + totalCount + " bookings rejected";
            System.out.println("❌ " + message);

            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": true, \"message\": \"" + message + "\"}");
            } else {
                request.getSession().setAttribute("successMessage", message);
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
            }

        } catch (Exception e) {
            System.err.println("❌ Error in bulk rejection: " + e.getMessage());

            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"error\": \"Bulk rejection failed\"}");
            } else {
                request.getSession().setAttribute("errorMessage", "Bulk rejection failed");
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
            }
        }
    }

    // ✅ PARAMETER-BASED ACTION HANDLER (BACKWARD COMPATIBILITY)
    private void handleParameterBasedAction(HttpServletRequest request, HttpServletResponse response,
                                            User admin, String action) throws ServletException, IOException {
        System.out.println("🔄 Handling parameter-based action: " + action);

        if ("approve".equals(action)) {
            handleApproveBooking(request, response, admin);
        } else if ("reject".equals(action)) {
            handleRejectBooking(request, response, admin);
        } else if ("bulkApprove".equals(action)) {
            handleBulkApprove(request, response, admin);
        } else if ("bulkReject".equals(action)) {
            handleBulkReject(request, response, admin);
        } else if ("forceVipBooking".equals(action)) {
            handleVipForceBooking(request, response, admin);
        } else if ("reallocateCabin".equals(action)) {
            handleCabinReallocation(request, response, admin);
        } else if ("assignSpecificCabin".equals(action)) {
            handleSpecificCabinAssignment(request, response, admin);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action: " + action);
        }
    }

    // ✅ EXISTING METHODS (UNCHANGED) - DASHBOARD
    private void showAdminDashboard(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        try {
            List<Booking> allBookings = bookingService.getAllBookings();
            List<Booking> recentBookings = new ArrayList<>();
            List<Booking> pendingBookings = new ArrayList<>();
            List<Booking> vipBookings = new ArrayList<>();

            for (Booking booking : allBookings) {
                if (booking.getStatus() == Booking.Status.PENDING) {
                    pendingBookings.add(booking);
                }
                if (booking.getPriorityLevel() == Booking.PriorityLevel.VIP) {
                    vipBookings.add(booking);
                }
                if (recentBookings.size() < 5) {
                    recentBookings.add(booking);
                }
            }

            List<User> allUsers = userService.getAllUsers();
            int normalUsers = 0, vipUsers = 0, adminUsers = 0;

            for (User user : allUsers) {
                if (user.isAdmin()) {
                    adminUsers++;
                } else if (user.isVIP()) {
                    vipUsers++;
                } else {
                    normalUsers++;
                }
            }

            request.setAttribute("admin", admin);
            request.setAttribute("totalUsers", allUsers.size());
            request.setAttribute("totalCabins", cabinService.getTotalCabinCount());
            request.setAttribute("totalBookings", allBookings.size());
            request.setAttribute("recentBookings", recentBookings);
            request.setAttribute("pendingCount", pendingBookings.size());
            request.setAttribute("vipBookings", vipBookings);
            request.setAttribute("normalUsers", normalUsers);
            request.setAttribute("vipUsers", vipUsers);
            request.setAttribute("adminUsers", adminUsers);
            request.setAttribute("activeCabins", cabinService.getTotalCabinCount());
            request.setAttribute("vipCabins", 0);
            request.setAttribute("maintenanceCabins", 0);

            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            handleError(request, response, "Error loading dashboard", e);
        }
    }

    // ✅ EXISTING METHODS (UNCHANGED) - BOOKING MANAGEMENT
    private void showBookingManagement(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        try {
            String filter = request.getParameter("filter");
            if (filter == null || filter.isEmpty()) {
                filter = "pending";
            }

            List<Booking> allBookings = bookingService.getAllBookings();
            List<Booking> filteredBookings = new ArrayList<>();

            int pendingCount = 0;
            int approvedCount = 0;
            int rejectedCount = 0;
            int vipCount = 0;

            for (Booking booking : allBookings) {
                if (booking.getStatus() == Booking.Status.PENDING) {
                    pendingCount++;
                } else if (booking.getStatus() == Booking.Status.APPROVED) {
                    approvedCount++;
                } else if (booking.getStatus() == Booking.Status.REJECTED) {
                    rejectedCount++;
                }

                if (booking.getPriorityLevel() == Booking.PriorityLevel.VIP) {
                    vipCount++;
                }

                switch (filter) {
                    case "pending":
                        if (booking.getStatus() == Booking.Status.PENDING) {
                            filteredBookings.add(booking);
                        }
                        break;
                    case "approved":
                        if (booking.getStatus() == Booking.Status.APPROVED) {
                            filteredBookings.add(booking);
                        }
                        break;
                    case "rejected":
                        if (booking.getStatus() == Booking.Status.REJECTED) {
                            filteredBookings.add(booking);
                        }
                        break;
                    case "vip":
                        if (booking.getPriorityLevel() == Booking.PriorityLevel.VIP) {
                            filteredBookings.add(booking);
                        }
                        break;
                    case "all":
                    default:
                        filteredBookings.add(booking);
                        break;
                }
            }

            request.setAttribute("admin", admin);
            request.setAttribute("bookings", filteredBookings);
            request.setAttribute("pendingCount", pendingCount);
            request.setAttribute("approvedCount", approvedCount);
            request.setAttribute("rejectedCount", rejectedCount);
            request.setAttribute("vipCount", vipCount);
            request.setAttribute("totalCount", allBookings.size());
            request.setAttribute("currentFilter", filter);

            request.getRequestDispatcher("/admin/booking-management.jsp").forward(request, response);

        } catch (Exception e) {
            handleError(request, response, "Error loading booking management", e);
        }
    }

    // ✅ ALL OTHER EXISTING METHODS - KEEP AS THEY WERE

    private void showUserManagement(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        try {
            List<User> allUsers = userService.getAllUsers();
            List<User> normalUsers = new ArrayList<>();
            List<User> vipUsers = new ArrayList<>();
            List<User> adminUsers = new ArrayList<>();
            Map<Integer, Integer> userBookingCounts = new HashMap<>();

            for (User user : allUsers) {
                if (user.isAdmin()) {
                    adminUsers.add(user);
                } else if (user.isVIP()) {
                    vipUsers.add(user);
                } else {
                    normalUsers.add(user);
                }

                try {
                    int bookingCount = bookingService.getBookingCountByUserId(user.getUserId());
                    userBookingCounts.put(user.getUserId(), bookingCount);
                } catch (Exception e) {
                    userBookingCounts.put(user.getUserId(), 0);
                }
            }

            request.setAttribute("admin", admin);
            request.setAttribute("allUsers", allUsers);
            request.setAttribute("normalUsers", normalUsers);
            request.setAttribute("vipUsers", vipUsers);
            request.setAttribute("adminUsers", adminUsers);
            request.setAttribute("totalUsers", allUsers.size());
            request.setAttribute("userBookingCounts", userBookingCounts);

            request.getRequestDispatcher("/admin/user-management.jsp").forward(request, response);

        } catch (Exception e) {
            handleError(request, response, "Error loading user management", e);
        }
    }

    private void showAnalytics(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        try {
            List<User> allUsers = userService.getAllUsers();
            int totalUsers = allUsers.size();
            int normalUsers = 0;
            int vipUsers = 0;
            int adminUsers = 0;
            int activeUsers = 0;

            for (User user : allUsers) {
                if (user.isAdmin()) {
                    adminUsers++;
                } else if (user.isVIP()) {
                    vipUsers++;
                } else {
                    normalUsers++;
                }

                if (user.getStatus() == User.Status.ACTIVE) {
                    activeUsers++;
                }
            }

            List<Booking> allBookings = bookingService.getAllBookings();
            int totalBookings = allBookings.size();
            int approvedBookings = 0;
            int pendingBookings = 0;
            int rejectedBookings = 0;
            int vipBookings = 0;

            // ✅ MAIN FIX: Proper today's date calculation
            int todaysBookings = 0;
            int todayApprovals = 0;

            // ✅ Get today's date properly (date only, no time)
            java.time.LocalDate today = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Kolkata"));
            java.sql.Date todayDate = java.sql.Date.valueOf(today);

            System.out.println("📅 Today's date for analytics: " + todayDate);
            System.out.println("🌍 Current timezone: Asia/Kolkata");

            for (Booking booking : allBookings) {
                // Count by status
                if (booking.getStatus() == Booking.Status.APPROVED) {
                    approvedBookings++;
                } else if (booking.getStatus() == Booking.Status.PENDING) {
                    pendingBookings++;
                } else if (booking.getStatus() == Booking.Status.REJECTED) {
                    rejectedBookings++;
                }

                // Count VIP bookings
                if (booking.getPriorityLevel() == Booking.PriorityLevel.VIP) {
                    vipBookings++;
                }

                // ✅ FIXED: Today's bookings calculation
                if (booking.getBookingDate() != null) {
                    // Convert booking date to LocalDate for comparison
                    java.time.LocalDate bookingLocalDate = booking.getBookingDate().toLocalDate();

                    if (bookingLocalDate.equals(today)) {
                        todaysBookings++;
                        System.out.println("📅 Found today's booking: " + booking.getBookingId() +
                                " for date: " + booking.getBookingDate());
                    }
                }

                // ✅ FIXED: Today's approvals calculation
                if (booking.getApprovedAt() != null) {
                    java.time.LocalDate approvalDate = booking.getApprovedAt().toLocalDateTime().toLocalDate();
                    if (approvalDate.equals(today)) {
                        todayApprovals++;
                    }
                }
            }

            // Calculate rates
            double approvalRate = 0.0;
            if (totalBookings > 0) {
                approvalRate = (approvedBookings * 100.0) / totalBookings;
            }

            double rejectionRate = 0.0;
            if (totalBookings > 0) {
                rejectionRate = (rejectedBookings * 100.0) / totalBookings;
            }

            double vipBookingRate = 0.0;
            if (totalBookings > 0) {
                vipBookingRate = (vipBookings * 100.0) / totalBookings;
            }

            // Cabin statistics
            int totalCabins = cabinService.getTotalCabinCount();
            int activeCabins = cabinService.getActiveCabinCount();
            List<Cabin> vipCabinList = cabinService.getVIPCabins();
            int vipCabins = vipCabinList.size();
            int maintenanceCabins = totalCabins - activeCabins;

            double utilizationRate = 0.0;
            if (totalCabins > 0) {
                utilizationRate = (approvedBookings * 100.0) / (totalCabins * 30);
            }

            List<String> popularTimeSlots = bookingService.getPopularTimeSlots();
            if (popularTimeSlots.isEmpty()) {
                popularTimeSlots = Arrays.asList("09:00-10:00", "10:00-11:00", "11:00-12:00", "14:00-15:00", "15:00-16:00");
            }

            Map<String, Object> systemMetrics = new HashMap<>();
            systemMetrics.put("averageBookingsPerUser", totalUsers > 0 ? (double) totalBookings / totalUsers : 0.0);
            systemMetrics.put("cabinUtilizationRate", utilizationRate);
            systemMetrics.put("vipUserPercentage", totalUsers > 0 ? (vipUsers * 100.0) / totalUsers : 0.0);
            systemMetrics.put("adminUserPercentage", totalUsers > 0 ? (adminUsers * 100.0) / totalUsers : 0.0);

            // ✅ ENHANCED: Debug logging
            System.out.println("📊 Analytics Summary:");
            System.out.println("   - Total Bookings: " + totalBookings);
            System.out.println("   - Today's Date: " + todayDate);
            System.out.println("   - Today's Bookings: " + todaysBookings);
            System.out.println("   - Today's Approvals: " + todayApprovals);
            System.out.println("   - Pending Bookings: " + pendingBookings);

            // Set attributes
            request.setAttribute("admin", admin);
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("normalUsers", normalUsers);
            request.setAttribute("vipUsers", vipUsers);
            request.setAttribute("adminUsers", adminUsers);
            request.setAttribute("activeUsers", activeUsers);
            request.setAttribute("totalBookings", totalBookings);
            request.setAttribute("approvedBookings", approvedBookings);
            request.setAttribute("pendingBookings", pendingBookings);
            request.setAttribute("rejectedBookings", rejectedBookings);
            request.setAttribute("vipBookings", vipBookings);

            // ✅ MAIN FIX: Correct today's activity data
            request.setAttribute("todaysBookings", todaysBookings);  // ✅ Fixed
            request.setAttribute("todayBookings", todaysBookings);    // ✅ Alternative name
            request.setAttribute("todayApprovals", todayApprovals);   // ✅ New field

            request.setAttribute("approvalRate", Math.round(approvalRate * 100.0) / 100.0);
            request.setAttribute("rejectionRate", Math.round(rejectionRate * 100.0) / 100.0);
            request.setAttribute("vipBookingRate", Math.round(vipBookingRate * 100.0) / 100.0);
            request.setAttribute("totalCabins", totalCabins);
            request.setAttribute("activeCabins", activeCabins);
            request.setAttribute("vipCabins", vipCabins);
            request.setAttribute("maintenanceCabins", maintenanceCabins);
            request.setAttribute("popularTimeSlots", popularTimeSlots);
            request.setAttribute("systemMetrics", systemMetrics);
            request.setAttribute("utilizationRate", Math.round(utilizationRate * 100.0) / 100.0);

            // ✅ NEW: Add current timestamp for JSP
            request.setAttribute("now", new java.util.Date());
            request.setAttribute("currentDate", todayDate);

            request.getRequestDispatcher("/admin/analytics.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error in analytics calculation: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error loading analytics", e);
        }
    }


    private void showCabinManagement(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        try {
            List<Cabin> allCabins = cabinService.getAllCabins();
            request.setAttribute("admin", admin);
            request.setAttribute("allCabins", allCabins);

            request.getRequestDispatcher("/admin/manage-cabins.jsp").forward(request, response);

        } catch (Exception e) {
            handleError(request, response, "Error loading cabin management", e);
        }
    }

    private void handlePromoteUser(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        String userId = request.getParameter("userId");
        String newRole = request.getParameter("role");

        if (userId != null && !userId.isEmpty() && newRole != null) {
            try {
                int userIdInt = Integer.parseInt(userId);
                boolean success = false;

                if ("vip".equals(newRole)) {
                    success = userService.promoteToVIP(userIdInt);
                    if (success) {
                        request.getSession().setAttribute("successMessage",
                                "User promoted to VIP successfully!");
                    } else {
                        request.getSession().setAttribute("errorMessage",
                                "Failed to promote user to VIP!");
                    }
                } else if ("admin".equals(newRole) && "SUPER_ADMIN".equals(admin.getUserType().toString())) {
                    success = userService.promoteToAdmin(userIdInt);
                    if (success) {
                        request.getSession().setAttribute("successMessage",
                                "User promoted to Admin successfully!");
                    } else {
                        request.getSession().setAttribute("errorMessage",
                                "Failed to promote user to Admin!");
                    }
                } else {
                    request.getSession().setAttribute("errorMessage",
                            "Insufficient permissions or invalid role!");
                }

            } catch (NumberFormatException e) {
                request.getSession().setAttribute("errorMessage",
                        "Invalid user ID!");
            }
        } else {
            request.getSession().setAttribute("errorMessage",
                    "Missing required parameters!");
        }

        response.sendRedirect(request.getContextPath() + "/admin/users");
    }

    private void handleDemoteUser(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        String userId = request.getParameter("userId");

        if (userId != null && !userId.isEmpty()) {
            try {
                int userIdInt = Integer.parseInt(userId);
                boolean success = userService.demoteUser(userIdInt);

                if (success) {
                    request.getSession().setAttribute("successMessage",
                            "User demoted successfully!");
                } else {
                    request.getSession().setAttribute("errorMessage",
                            "Failed to demote user!");
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("errorMessage",
                        "Invalid user ID!");
            }
        } else {
            request.getSession().setAttribute("errorMessage",
                    "User ID is required!");
        }

        response.sendRedirect(request.getContextPath() + "/admin/users");
    }

    // ✅ VIP FORCE BOOKING HANDLER
    private void handleVipForceBooking(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        try {
            int vipUserId = Integer.parseInt(request.getParameter("vipUserId"));
            int cabinId = Integer.parseInt(request.getParameter("cabinId"));
            String dateStr = request.getParameter("bookingDate");
            String timeSlot = request.getParameter("timeSlot");
            String purpose = request.getParameter("purpose");

            Date bookingDate = Date.valueOf(dateStr);

            User vipUser = userService.getUserById(vipUserId);
            if (vipUser == null || !vipUser.isVIP()) {
                request.getSession().setAttribute("errorMessage", "Invalid VIP user!");
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
                return;
            }

            Booking vipBooking = new Booking();
            vipBooking.setUserId(vipUserId);
            vipBooking.setCabinId(cabinId);
            vipBooking.setBookingDate(bookingDate);
            vipBooking.setTimeSlot(timeSlot);
            vipBooking.setPurpose(purpose);

            boolean success = bookingService.forceBookingForVIP(vipBooking, vipUser);

            if (success) {
                request.getSession().setAttribute("successMessage",
                        "VIP booking created successfully! Normal users have been reallocated automatically.");
            } else {
                request.getSession().setAttribute("errorMessage",
                        "Failed to create VIP booking!");
            }

        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage",
                    "Error processing VIP booking: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/bookings");
    }

    // ✅ CABIN REALLOCATION HANDLER
    private void handleCabinReallocation(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            int newCabinId = Integer.parseInt(request.getParameter("newCabinId"));
            String reason = request.getParameter("reason");

            if (reason == null || reason.trim().isEmpty()) {
                reason = "Admin reallocation for better resource management";
            }

            boolean success = bookingService.adminReallocateUserCabin(bookingId, newCabinId, admin.getUserId(), reason);

            if (success) {
                request.getSession().setAttribute("successMessage",
                        "Booking reallocated successfully! User has been notified.");
            } else {
                request.getSession().setAttribute("errorMessage",
                        "Failed to reallocate booking!");
            }

        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage",
                    "Error reallocating booking: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/bookings");
    }

    // ✅ SPECIFIC CABIN ASSIGNMENT HANDLER
    private void handleSpecificCabinAssignment(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            int requestedCabinId = Integer.parseInt(request.getParameter("requestedCabinId"));
            int adminChosenCabinId = Integer.parseInt(request.getParameter("adminChosenCabinId"));
            String dateStr = request.getParameter("bookingDate");
            String timeSlot = request.getParameter("timeSlot");
            String purpose = request.getParameter("purpose");

            Date bookingDate = Date.valueOf(dateStr);

            boolean success = bookingService.adminAssignSpecificCabin(
                    userId, requestedCabinId, adminChosenCabinId, bookingDate, timeSlot, purpose);

            if (success) {
                request.getSession().setAttribute("successMessage",
                        "Cabin assigned successfully! Booking created with admin's choice.");
            } else {
                request.getSession().setAttribute("errorMessage",
                        "Failed to assign specific cabin!");
            }

        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage",
                    "Error assigning cabin: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/bookings");
    }

    // ✅ UTILITY METHODS
    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response,
                             String message, Exception e) throws ServletException, IOException {
        System.err.println("❌ AdminController Error: " + message);
        e.printStackTrace();
        request.setAttribute("error", message);
        request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
    }
}
