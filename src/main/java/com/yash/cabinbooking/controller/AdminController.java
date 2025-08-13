package com.yash.cabinbooking.controller;

import com.yash.cabinbooking.model.User;
import com.yash.cabinbooking.model.Cabin;
import com.yash.cabinbooking.model.Company;
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
import java.sql.Date; // ✅ ADD THIS IMPORT
import java.util.*;

/**
 * ✅ PRODUCTION READY ADMIN CONTROLLER - WITH VIP OVERRIDE & ADMIN FEATURES
 */
@WebServlet(urlPatterns = {
        "/admin/dashboard",
        "/admin/analytics",
        "/admin/users",
        "/admin/bookings",
        "/admin/promote-user",
        "/admin/demote-user"
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
        } catch (Exception e) {
            throw new ServletException("AdminController initialization failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();

        // Extract action from servlet path
        String action = "dashboard";
        if (servletPath.startsWith("/admin/")) {
            action = servletPath.substring(7);
        }

        // Check authentication
        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

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
            handleError(request, response, "Error processing admin request", e);
        }
    }

    // ✅ CLEAN DASHBOARD METHOD
    private void showAdminDashboard(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        try {
            // Get all bookings for dashboard data
            List<Booking> allBookings = bookingService.getAllBookings();
            List<Booking> recentBookings = new ArrayList<>();
            List<Booking> pendingBookings = new ArrayList<>();
            List<Booking> vipBookings = new ArrayList<>();

            // Process all bookings
            for (Booking booking : allBookings) {
                // Collect pending bookings
                if (booking.getStatus() == Booking.Status.PENDING) {
                    pendingBookings.add(booking);
                }

                // Collect VIP bookings
                if (booking.getPriorityLevel() == Booking.PriorityLevel.VIP) {
                    vipBookings.add(booking);
                }

                // Get recent bookings (latest 5)
                if (recentBookings.size() < 5) {
                    recentBookings.add(booking);
                }
            }

            // Get user analytics
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

            // Set all required attributes for dashboard
            request.setAttribute("admin", admin);
            request.setAttribute("totalUsers", allUsers.size());
            request.setAttribute("totalCabins", cabinService.getTotalCabinCount());
            request.setAttribute("totalBookings", allBookings.size());
            request.setAttribute("recentBookings", recentBookings);
            request.setAttribute("pendingCount", pendingBookings.size());
            request.setAttribute("vipBookings", vipBookings);

            // User distribution for sidebar
            request.setAttribute("normalUsers", normalUsers);
            request.setAttribute("vipUsers", vipUsers);
            request.setAttribute("adminUsers", adminUsers);

            // Cabin stats for sidebar
            request.setAttribute("activeCabins", cabinService.getTotalCabinCount());
            request.setAttribute("vipCabins", 0);
            request.setAttribute("maintenanceCabins", 0);

            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            handleError(request, response, "Error loading dashboard", e);
        }
    }

    // ✅ CLEAN BOOKING MANAGEMENT METHOD
    private void showBookingManagement(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        try {
            // Get filter parameter
            String filter = request.getParameter("filter");
            if (filter == null || filter.isEmpty()) {
                filter = "pending"; // Default to pending bookings
            }

            // Get all bookings from service
            List<Booking> allBookings = bookingService.getAllBookings();
            List<Booking> filteredBookings = new ArrayList<>();

            // Initialize counters for filter tabs
            int pendingCount = 0;
            int approvedCount = 0;
            int rejectedCount = 0;
            int vipCount = 0;

            // Process all bookings for filtering and counting
            for (Booking booking : allBookings) {
                // Count bookings by status
                if (booking.getStatus() == Booking.Status.PENDING) {
                    pendingCount++;
                } else if (booking.getStatus() == Booking.Status.APPROVED) {
                    approvedCount++;
                } else if (booking.getStatus() == Booking.Status.REJECTED) {
                    rejectedCount++;
                }

                // Count VIP priority bookings
                if (booking.getPriorityLevel() == Booking.PriorityLevel.VIP) {
                    vipCount++;
                }

                // Apply selected filter
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

            // Set all required attributes
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

    // ✅ ENHANCED USER MANAGEMENT METHOD WITH PROPER DATA
    private void showUserManagement(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        try {
            // Get all users
            List<User> allUsers = userService.getAllUsers();

            // Separate users by type for easier JSP processing
            List<User> normalUsers = new ArrayList<>();
            List<User> vipUsers = new ArrayList<>();
            List<User> adminUsers = new ArrayList<>();

            // Get user booking counts (if method exists in service)
            Map<Integer, Integer> userBookingCounts = new HashMap<>();

            for (User user : allUsers) {
                // Categorize users
                if (user.isAdmin()) {
                    adminUsers.add(user);
                } else if (user.isVIP()) {
                    vipUsers.add(user);
                } else {
                    normalUsers.add(user);
                }

                // Get booking count for each user (you may need to implement this method)
                try {
                    int bookingCount = bookingService.getBookingCountByUserId(user.getUserId());
                    userBookingCounts.put(user.getUserId(), bookingCount);
                } catch (Exception e) {
                    userBookingCounts.put(user.getUserId(), 0);
                }
            }

            // Set all required attributes
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

    // ✅ CLEAN ANALYTICS METHOD
    // ✅ ENHANCED ANALYTICS METHOD WITH PROPER CALCULATIONS
    private void showAnalytics(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        try {
            // USER ANALYTICS
            List<User> allUsers = userService.getAllUsers();
            int totalUsers = allUsers.size();
            int normalUsers = 0;
            int vipUsers = 0;
            int adminUsers = 0;
            int activeUsers = 0; // ✅ ADDED: Active users count

            for (User user : allUsers) {
                // Count by user type
                if (user.isAdmin()) {
                    adminUsers++;
                } else if (user.isVIP()) {
                    vipUsers++;
                } else {
                    normalUsers++;
                }

                // ✅ ADDED: Count active users
                if (user.getStatus() == User.Status.ACTIVE) {
                    activeUsers++;
                }
            }

            // BOOKING ANALYTICS
            List<Booking> allBookings = bookingService.getAllBookings();
            int totalBookings = allBookings.size();
            int approvedBookings = 0;
            int pendingBookings = 0;
            int rejectedBookings = 0;
            int vipBookings = 0; // ✅ ADDED: VIP bookings count
            int todaysBookings = 0; // ✅ ADDED: Today's bookings

            // ✅ ADDED: Get today's date for filtering
            Date today = new Date(System.currentTimeMillis());

            for (Booking booking : allBookings) {
                // Count by status
                if (booking.getStatus() == Booking.Status.APPROVED) {
                    approvedBookings++;
                } else if (booking.getStatus() == Booking.Status.PENDING) {
                    pendingBookings++;
                } else if (booking.getStatus() == Booking.Status.REJECTED) {
                    rejectedBookings++;
                }

                // ✅ ADDED: Count VIP priority bookings
                if (booking.getPriorityLevel() == Booking.PriorityLevel.VIP) {
                    vipBookings++;
                }

                // ✅ ADDED: Count today's bookings
                if (booking.getBookingDate() != null && booking.getBookingDate().equals(today)) {
                    todaysBookings++;
                }
            }

            // ✅ ADDED: Calculate approval rate percentage
            double approvalRate = 0.0;
            if (totalBookings > 0) {
                approvalRate = (approvedBookings * 100.0) / totalBookings;
            }

            // ✅ ADDED: Calculate rejection rate percentage
            double rejectionRate = 0.0;
            if (totalBookings > 0) {
                rejectionRate = (rejectedBookings * 100.0) / totalBookings;
            }

            // ✅ ADDED: Calculate VIP booking percentage
            double vipBookingRate = 0.0;
            if (totalBookings > 0) {
                vipBookingRate = (vipBookings * 100.0) / totalBookings;
            }

            // CABIN ANALYTICS
            int totalCabins = cabinService.getTotalCabinCount();
            int activeCabins = cabinService.getActiveCabinCount(); // ✅ ADDED: Use proper method
            List<Cabin> vipCabinList = cabinService.getVIPCabins();
            int vipCabins = vipCabinList.size();

            // ✅ ADDED: Calculate maintenance cabins
            int maintenanceCabins = totalCabins - activeCabins;

            // ✅ ADDED: Calculate utilization rate
            double utilizationRate = 0.0;
            if (totalCabins > 0) {
                utilizationRate = (approvedBookings * 100.0) / (totalCabins * 30); // Assuming 30 days average
            }

            // ✅ ADDED: Popular time slots from service
            List<String> popularTimeSlots = bookingService.getPopularTimeSlots();
            if (popularTimeSlots.isEmpty()) {
                popularTimeSlots = Arrays.asList("09:00-10:00", "10:00-11:00", "11:00-12:00", "14:00-15:00", "15:00-16:00");
            }

            // ✅ ADDED: System health metrics
            Map<String, Object> systemMetrics = new HashMap<>();
            systemMetrics.put("averageBookingsPerUser", totalUsers > 0 ? (double) totalBookings / totalUsers : 0.0);
            systemMetrics.put("cabinUtilizationRate", utilizationRate);
            systemMetrics.put("vipUserPercentage", totalUsers > 0 ? (vipUsers * 100.0) / totalUsers : 0.0);
            systemMetrics.put("adminUserPercentage", totalUsers > 0 ? (adminUsers * 100.0) / totalUsers : 0.0);

            // SET ALL REQUEST ATTRIBUTES
            request.setAttribute("admin", admin);

            // User Analytics
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("normalUsers", normalUsers);
            request.setAttribute("vipUsers", vipUsers);
            request.setAttribute("adminUsers", adminUsers);
            request.setAttribute("activeUsers", activeUsers); // ✅ FIXED: Active users

            // Booking Analytics
            request.setAttribute("totalBookings", totalBookings);
            request.setAttribute("approvedBookings", approvedBookings);
            request.setAttribute("pendingBookings", pendingBookings);
            request.setAttribute("rejectedBookings", rejectedBookings);
            request.setAttribute("vipBookings", vipBookings); // ✅ FIXED: VIP bookings count
            request.setAttribute("todaysBookings", todaysBookings); // ✅ ADDED: Today's activity

            // ✅ FIXED: Percentage calculations
            request.setAttribute("approvalRate", Math.round(approvalRate * 100.0) / 100.0); // Round to 2 decimal places
            request.setAttribute("rejectionRate", Math.round(rejectionRate * 100.0) / 100.0);
            request.setAttribute("vipBookingRate", Math.round(vipBookingRate * 100.0) / 100.0);

            // Cabin Analytics
            request.setAttribute("totalCabins", totalCabins);
            request.setAttribute("activeCabins", activeCabins);
            request.setAttribute("vipCabins", vipCabins);
            request.setAttribute("maintenanceCabins", maintenanceCabins);

            // Enhanced Analytics
            request.setAttribute("popularTimeSlots", popularTimeSlots);
            request.setAttribute("systemMetrics", systemMetrics);
            request.setAttribute("utilizationRate", Math.round(utilizationRate * 100.0) / 100.0);

            System.out.println("📊 Analytics calculated:");
            System.out.println("   - Total Users: " + totalUsers + " (Active: " + activeUsers + ")");
            System.out.println("   - Total Bookings: " + totalBookings + " (Approved: " + approvedBookings + ")");
            System.out.println("   - Approval Rate: " + approvalRate + "%");
            System.out.println("   - VIP Bookings: " + vipBookings);
            System.out.println("   - Today's Bookings: " + todaysBookings);

            request.getRequestDispatcher("/admin/analytics.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error in analytics calculation: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error loading analytics", e);
        }
    }


    // ✅ CLEAN CABIN MANAGEMENT METHOD
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

    // ✅ NEW PROMOTE USER METHOD
    private void handlePromoteUser(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        String userId = request.getParameter("userId");
        String newRole = request.getParameter("role"); // vip या admin

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

        // Redirect back to user management
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }

    // ✅ NEW DEMOTE USER METHOD
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

    // ✅ ENHANCED POST METHOD WITH PROPER REDIRECTS
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        User admin = getCurrentUser(request);

        if (admin == null || !admin.isAdmin()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            if ("approve".equals(action)) {
                int bookingId = Integer.parseInt(request.getParameter("bookingId"));
                boolean success = bookingService.approveBooking(bookingId, admin.getUserId());

                if (success) {
                    request.getSession().setAttribute("successMessage",
                            "Booking #" + bookingId + " approved successfully!");
                } else {
                    request.getSession().setAttribute("errorMessage",
                            "Failed to approve booking #" + bookingId);
                }

                response.sendRedirect(request.getContextPath() + "/admin/bookings");
                return;

            } else if ("reject".equals(action)) {
                int bookingId = Integer.parseInt(request.getParameter("bookingId"));
                boolean success = bookingService.rejectBooking(bookingId, admin.getUserId());

                if (success) {
                    request.getSession().setAttribute("successMessage",
                            "Booking #" + bookingId + " rejected successfully!");
                } else {
                    request.getSession().setAttribute("errorMessage",
                            "Failed to reject booking #" + bookingId);
                }

                response.sendRedirect(request.getContextPath() + "/admin/bookings");
                return;

            } else if ("bulkApprove".equals(action)) {
                String[] bookingIds = request.getParameterValues("bookingIds");
                int successCount = 0;

                if (bookingIds != null) {
                    for (String idStr : bookingIds) {
                        try {
                            int bookingId = Integer.parseInt(idStr);
                            if (bookingService.approveBooking(bookingId, admin.getUserId())) {
                                successCount++;
                            }
                        } catch (NumberFormatException e) {
                            // Log invalid booking ID but continue processing
                        }
                    }
                }

                request.getSession().setAttribute("successMessage",
                        "Bulk approve completed: " + successCount + " bookings approved");
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
                return;

            } else if ("bulkReject".equals(action)) {
                String[] bookingIds = request.getParameterValues("bookingIds");
                int successCount = 0;

                if (bookingIds != null) {
                    for (String idStr : bookingIds) {
                        try {
                            int bookingId = Integer.parseInt(idStr);
                            if (bookingService.rejectBooking(bookingId, admin.getUserId())) {
                                successCount++;
                            }
                        } catch (NumberFormatException e) {
                            // Log invalid booking ID but continue processing
                        }
                    }
                }

                request.getSession().setAttribute("successMessage",
                        "Bulk reject completed: " + successCount + " bookings rejected");
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
                return;

            } else if ("forceVipBooking".equals(action)) {
                handleVipForceBooking(request, response, admin);
                return;

            } else if ("reallocateCabin".equals(action)) {
                handleCabinReallocation(request, response, admin);
                return;

            } else if ("assignSpecificCabin".equals(action)) {
                handleSpecificCabinAssignment(request, response, admin);
                return;

            } else {
                request.getSession().setAttribute("errorMessage", "Unknown action requested");
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
            }

        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error processing request: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
        }
    }

    // 🎯 NEW METHODS FOR YOUR 3 REQUIREMENTS:

    // ⭐ REQUIREMENT 1: VIP Force Booking Handler
    private void handleVipForceBooking(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {

        try {
            int vipUserId = Integer.parseInt(request.getParameter("vipUserId"));
            int cabinId = Integer.parseInt(request.getParameter("cabinId"));
            String dateStr = request.getParameter("bookingDate");
            String timeSlot = request.getParameter("timeSlot");
            String purpose = request.getParameter("purpose");

            Date bookingDate = Date.valueOf(dateStr);

            // Get VIP user
            User vipUser = userService.getUserById(vipUserId);
            if (vipUser == null || !vipUser.isVIP()) {
                request.getSession().setAttribute("errorMessage", "Invalid VIP user!");
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
                return;
            }

            // Create VIP booking
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

    // 👨💼 REQUIREMENT 2: Cabin Reallocation Handler
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

    // 🎯 REQUIREMENT 3: Specific Cabin Assignment Handler
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
        request.setAttribute("error", message);
        request.getRequestDispatcher("/admin/error.jsp").forward(request, response);
    }
}
