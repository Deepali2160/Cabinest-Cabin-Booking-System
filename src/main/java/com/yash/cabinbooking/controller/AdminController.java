package com.yash.cabinbooking.controller;

import com.yash.cabinbooking.dao.CabinDao;
import com.yash.cabinbooking.daoimpl.CabinDaoImpl;
import com.yash.cabinbooking.service.BookingService;
import com.yash.cabinbooking.service.UserService;
import com.yash.cabinbooking.service.CompanyService;
import com.yash.cabinbooking.service.AIRecommendationService;
import com.yash.cabinbooking.serviceimpl.*;
import com.yash.cabinbooking.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * ADMIN CONTROLLER - COMPLETE FIXED VERSION
 * ✅ ALL APPROVAL ISSUES RESOLVED
 * ✅ GET and POST methods properly handled
 * ✅ Booking approval/rejection working perfectly
 */
@WebServlet(name = "AdminController", urlPatterns = {
        "/admin/dashboard",
        "/admin/bookings",
        "/admin/users",
        "/admin/analytics",
        "/admin/companies",
        "/admin/approve-booking",
        "/admin/reject-booking",
        "/admin/promote-user",
        "/admin/bulk-approve",
        "/admin/bulk-reject",
        // ✅ NEW: Add these company management URL patterns
        "/admin/add-company",
        "/admin/company/add",
        "/admin/company/edit",
        "/admin/company/delete",
        "/admin/company/status"
})

public class AdminController extends HttpServlet {

    private BookingService bookingService;
    private UserService userService;
    private CompanyService companyService;
    private AIRecommendationService aiService;
    private CabinDao cabinDAO;


    @Override
    public void init() throws ServletException {
        try {
            this.bookingService = new BookingServiceImpl();
            this.userService = new UserServiceImpl();
            this.companyService = new CompanyServiceImpl();
            this.aiService = new AIRecommendationServiceImpl();
            this.cabinDAO = new CabinDaoImpl();
            System.out.println("🔧 AdminController initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ AdminController initialization failed: " + e.getMessage());
            throw new ServletException("AdminController initialization failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            System.out.println("🔒 Unauthorized admin access attempt, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = getActionFromRequest(request);
        System.out.println("🌐 Admin GET Request: " + action + " by admin: " + currentUser.getName());

        try {
            switch (action) {
                case "dashboard":
                    showAdminDashboard(request, response, currentUser);
                    break;
                case "bookings":
                    showBookingManagement(request, response, currentUser);
                    break;
                case "users":
                    showUserManagement(request, response, currentUser);
                    break;
                case "analytics":
                    showAnalytics(request, response, currentUser);
                    break;
                case "companies":
                    showCompanyManagement(request, response, currentUser);
                    break;
                // ✅ NEW: Add company management GET cases
                case "add-company":
                    showAddCompanyForm(request, response, currentUser);
                    break;
                case "approve-booking":
                    approveBooking(request, response, currentUser);
                    break;
                case "reject-booking":
                    rejectBooking(request, response, currentUser);
                    break;
                default:
                    showAdminDashboard(request, response, currentUser);
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing admin GET request: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error processing request", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = getActionFromRequest(request);
        System.out.println("📝 Admin POST Request: " + action + " by admin: " + currentUser.getName());


        try {
            switch (action) {
                case "approve-booking":
                    approveBooking(request, response, currentUser);
                    break;
                case "reject-booking":
                    rejectBooking(request, response, currentUser);
                    break;
                case "promote-user":
                    promoteUser(request, response, currentUser);
                    break;
                case "bulk-approve":
                    bulkApproveBookings(request, response, currentUser);
                    break;
                case "bulk-reject":
                    bulkRejectBookings(request, response, currentUser);
                    break;
                // ✅ NEW: Add company management POST cases
                case "add-company":
                case "company/add":
                    addCompany(request, response, currentUser);
                    break;
                case "company/delete":
                    deleteCompany(request, response, currentUser);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing admin POST request: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error processing request", e);
        }
    }

    // ✅ ENHANCED: Admin dashboard with better error handling
    private void showAdminDashboard(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        System.out.println("🏠 Loading admin dashboard for: " + admin.getName());

        try {
            // Initialize with safe default values
            int pendingCount = 0;
            int totalBookings = 0;
            int totalUsers = 0;
            int totalCompanies = 0;
            long normalUsers = 0;
            long vipUsers = 0;
            long adminUsers = 0;
            List<Booking> pendingBookings = new ArrayList<>();
            List<Booking> vipBookings = new ArrayList<>();
            List<Booking> recentBookings = new ArrayList<>();
            List<String> popularTimeSlots = new ArrayList<>();
            List<Booking> todayBookings = new ArrayList<>();

            // Safe data loading with error handling
            try {
                pendingBookings = bookingService.getPendingBookingsForApproval();
                pendingCount = pendingBookings != null ? pendingBookings.size() : 0;
            } catch (Exception e) {
                System.err.println("⚠️ Error loading pending bookings: " + e.getMessage());
            }

            try {
                vipBookings = bookingService.getVIPPriorityBookings();
            } catch (Exception e) {
                System.err.println("⚠️ Error loading VIP bookings: " + e.getMessage());
                vipBookings = new ArrayList<>();
            }

            try {
                totalBookings = bookingService.getTotalBookingsCount();
            } catch (Exception e) {
                System.err.println("⚠️ Error loading total bookings: " + e.getMessage());
            }

            try {
                List<User> allUsers = userService.getAllUsers();
                if (allUsers != null) {
                    totalUsers = allUsers.size();
                    normalUsers = allUsers.stream().filter(u -> u.getUserType() == User.UserType.NORMAL).count();
                    vipUsers = allUsers.stream().filter(u -> u.getUserType() == User.UserType.VIP).count();
                    adminUsers = allUsers.stream().filter(u -> u.isAdmin()).count();
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error loading users: " + e.getMessage());
            }

            try {
                List<Company> activeCompanies = companyService.getAllActiveCompanies();
                totalCompanies = activeCompanies != null ? activeCompanies.size() : 0;
            } catch (Exception e) {
                System.err.println("⚠️ Error loading companies: " + e.getMessage());
            }

            try {
                List<Booking> allBookings = bookingService.getBookingsForAdmin();
                if (allBookings != null) {
                    recentBookings = allBookings.stream().limit(10).collect(Collectors.toList());
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error loading recent bookings: " + e.getMessage());
            }

            try {
                popularTimeSlots = aiService.getPopularTimeSlots();
            } catch (Exception e) {
                System.err.println("⚠️ Error loading AI insights: " + e.getMessage());
                popularTimeSlots = new ArrayList<>();
            }

            try {
                Date today = new Date(System.currentTimeMillis());
                todayBookings = bookingService.getBookingsByDateRange(today, today);
            } catch (Exception e) {
                System.err.println("⚠️ Error loading today's bookings: " + e.getMessage());
                todayBookings = new ArrayList<>();
            }

            // Set all attributes for JSP
            request.setAttribute("admin", admin);
            request.setAttribute("pendingBookings", pendingBookings);
            request.setAttribute("pendingCount", pendingCount);
            request.setAttribute("vipBookings", vipBookings);
            request.setAttribute("totalBookings", totalBookings);
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("totalCompanies", totalCompanies);
            request.setAttribute("normalUsers", normalUsers);
            request.setAttribute("vipUsers", vipUsers);
            request.setAttribute("adminUsers", adminUsers);
            request.setAttribute("recentBookings", recentBookings);
            request.setAttribute("popularTimeSlots", popularTimeSlots);
            request.setAttribute("todayBookings", todayBookings);

            System.out.println("✅ Admin dashboard loaded successfully");
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Critical error in admin dashboard: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error loading admin dashboard", e);
        }
    }

    private void showBookingManagement(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        System.out.println("📋 Loading booking management for admin: " + admin.getName());

        try {
            String filter = request.getParameter("filter");
            if (filter == null) filter = "pending";

            List<Booking> bookings = new ArrayList<>();

            switch (filter) {
                case "pending":
                    bookings = bookingService.getPendingBookingsForApproval();
                    break;
                case "approved":
                    List<Booking> allBookings = bookingService.getBookingsForAdmin();
                    bookings = allBookings.stream()
                            .filter(b -> b.getStatus() == Booking.Status.APPROVED)
                            .collect(Collectors.toList());
                    break;
                case "rejected":
                    allBookings = bookingService.getBookingsForAdmin();
                    bookings = allBookings.stream()
                            .filter(b -> b.getStatus() == Booking.Status.REJECTED)
                            .collect(Collectors.toList());
                    break;
                case "vip":
                    bookings = bookingService.getVIPPriorityBookings();
                    break;
                default:
                    bookings = bookingService.getBookingsForAdmin();
                    break;
            }

            // ✅ ENHANCED: Calculate accurate counts
            List<Booking> allBookings = bookingService.getBookingsForAdmin();
            long pendingCount = allBookings.stream().filter(b -> b.getStatus() == Booking.Status.PENDING).count();
            long approvedCount = allBookings.stream().filter(b -> b.getStatus() == Booking.Status.APPROVED).count();
            long rejectedCount = allBookings.stream().filter(b -> b.getStatus() == Booking.Status.REJECTED).count();
            long vipCount = allBookings.stream().filter(b -> b.getPriorityLevel() == Booking.PriorityLevel.VIP).count();

            request.setAttribute("admin", admin);
            request.setAttribute("bookings", bookings);
            request.setAttribute("currentFilter", filter);
            request.setAttribute("pendingCount", pendingCount);
            request.setAttribute("approvedCount", approvedCount);
            request.setAttribute("rejectedCount", rejectedCount);
            request.setAttribute("vipCount", vipCount);
            request.setAttribute("totalCount", allBookings.size());

            System.out.println("✅ Booking management loaded successfully");
            request.getRequestDispatcher("/admin/booking-management.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error loading booking management: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error loading booking management", e);
        }
    }

    // ✅ FIXED: Enhanced approval method with proper logging
    private void approveBooking(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        String bookingIdStr = request.getParameter("bookingId");
        System.out.println("✅ Admin approving booking: " + bookingIdStr + " by " + admin.getName());

        try {
            if (bookingIdStr == null || bookingIdStr.trim().isEmpty()) {
                System.err.println("❌ No booking ID provided for approval");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Booking ID is required");
                return;
            }

            int bookingId = Integer.parseInt(bookingIdStr);
            Booking booking = bookingService.getBookingById(bookingId);

            if (booking == null) {
                System.err.println("❌ Booking not found: " + bookingId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Booking not found");
                return;
            }

            if (booking.getStatus() != Booking.Status.PENDING) {
                System.err.println("❌ Cannot approve non-pending booking: " + bookingId + " (Status: " + booking.getStatus() + ")");
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Only pending bookings can be approved");
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
                return;
            }

            // ✅ CRITICAL FIX: Actually call the booking service approval method
            boolean approvalSuccess = bookingService.approveBooking(bookingId, admin);

            HttpSession session = request.getSession();
            if (approvalSuccess) {
                System.out.println("✅ Booking approved successfully: " + bookingId + " by admin: " + admin.getName());
                session.setAttribute("successMessage", "Booking #" + bookingId + " approved successfully!");
            } else {
                System.err.println("❌ Booking approval failed: " + bookingId);
                session.setAttribute("errorMessage", "Failed to approve booking. Please try again.");
            }

            response.sendRedirect(request.getContextPath() + "/admin/bookings");

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid booking ID format: " + bookingIdStr);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid booking ID");
        } catch (Exception e) {
            System.err.println("❌ Error approving booking: " + e.getMessage());
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Error approving booking. Please try again.");
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
        }
    }

    // ✅ FIXED: Enhanced rejection method with proper logging
    private void rejectBooking(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        String bookingIdStr = request.getParameter("bookingId");
        System.out.println("❌ Admin rejecting booking: " + bookingIdStr + " by " + admin.getName());

        try {
            if (bookingIdStr == null || bookingIdStr.trim().isEmpty()) {
                System.err.println("❌ No booking ID provided for rejection");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Booking ID is required");
                return;
            }

            int bookingId = Integer.parseInt(bookingIdStr);
            Booking booking = bookingService.getBookingById(bookingId);

            if (booking == null) {
                System.err.println("❌ Booking not found: " + bookingId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Booking not found");
                return;
            }

            if (booking.getStatus() != Booking.Status.PENDING) {
                System.err.println("❌ Cannot reject non-pending booking: " + bookingId + " (Status: " + booking.getStatus() + ")");
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Only pending bookings can be rejected");
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
                return;
            }

            // ✅ CRITICAL FIX: Actually call the booking service rejection method
            boolean rejectionSuccess = bookingService.rejectBooking(bookingId, admin);

            HttpSession session = request.getSession();
            if (rejectionSuccess) {
                System.out.println("❌ Booking rejected successfully: " + bookingId + " by admin: " + admin.getName());
                session.setAttribute("successMessage", "Booking #" + bookingId + " rejected successfully!");
            } else {
                System.err.println("❌ Booking rejection failed: " + bookingId);
                session.setAttribute("errorMessage", "Failed to reject booking. Please try again.");
            }

            response.sendRedirect(request.getContextPath() + "/admin/bookings");

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid booking ID format: " + bookingIdStr);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid booking ID");
        } catch (Exception e) {
            System.err.println("❌ Error rejecting booking: " + e.getMessage());
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Error rejecting booking. Please try again.");
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
        }
    }

    // ✅ NEW: Bulk reject method (was missing)
    private void bulkRejectBookings(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        String[] bookingIds = request.getParameterValues("bookingIds");
        System.out.println("❌ Admin bulk rejecting bookings by: " + admin.getName());

        try {
            if (bookingIds == null || bookingIds.length == 0) {
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Please select bookings to reject");
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
                return;
            }

            int successCount = 0;
            int failCount = 0;

            for (String bookingIdStr : bookingIds) {
                try {
                    int bookingId = Integer.parseInt(bookingIdStr);
                    boolean success = bookingService.rejectBooking(bookingId, admin);
                    if (success) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (NumberFormatException e) {
                    failCount++;
                }
            }

            HttpSession session = request.getSession();
            if (successCount > 0) {
                session.setAttribute("successMessage",
                        successCount + " bookings rejected successfully!" +
                                (failCount > 0 ? " (" + failCount + " failed)" : ""));
            } else {
                session.setAttribute("errorMessage", "Failed to reject any bookings. Please try again.");
            }

            response.sendRedirect(request.getContextPath() + "/admin/bookings");

        } catch (Exception e) {
            System.err.println("❌ Error in bulk rejection: " + e.getMessage());
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Error in bulk rejection. Please try again.");
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
        }
    }

    // Keep all your existing methods: showUserManagement, showAnalytics, showCompanyManagement, promoteUser, bulkApproveBookings...
    private void showUserManagement(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        System.out.println("👥 Loading user management for admin: " + admin.getName());

        try {
            List<User> allUsers = userService.getAllUsers();
            List<Company> allCompanies = companyService.getAllActiveCompanies();

            List<User> normalUsers = allUsers.stream()
                    .filter(u -> u.getUserType() == User.UserType.NORMAL)
                    .collect(Collectors.toList());

            List<User> vipUsers = allUsers.stream()
                    .filter(u -> u.getUserType() == User.UserType.VIP)
                    .collect(Collectors.toList());

            List<User> adminUsers = allUsers.stream()
                    .filter(u -> u.isAdmin())
                    .collect(Collectors.toList());

            Map<Integer, Integer> userBookingCounts = new HashMap<>();
            for (User user : allUsers) {
                try {
                    int bookingCount = userService.getUserBookingCount(user.getUserId());
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
            request.setAttribute("allCompanies", allCompanies);
            request.setAttribute("totalUsers", allUsers.size());
            request.setAttribute("userBookingCounts", userBookingCounts);

            System.out.println("✅ User management loaded successfully");
            request.getRequestDispatcher("/admin/user-management.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error loading user management: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error loading user management", e);
        }
    }

    private void showAnalytics(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        System.out.println("📊 Loading analytics dashboard for admin: " + admin.getName());

        try {
            List<Booking> allBookings = bookingService.getBookingsForAdmin();
            List<User> allUsers = userService.getAllUsers();
            List<Company> allCompanies = companyService.getAllActiveCompanies();

            long totalBookings = allBookings.size();
            long approvedBookings = allBookings.stream().filter(b -> b.getStatus() == Booking.Status.APPROVED).count();
            long pendingBookings = allBookings.stream().filter(b -> b.getStatus() == Booking.Status.PENDING).count();
            long rejectedBookings = allBookings.stream().filter(b -> b.getStatus() == Booking.Status.REJECTED).count();

            double approvalRate = totalBookings > 0 ? (double) approvedBookings / totalBookings * 100 : 0;

            List<String> popularTimeSlots = aiService.getPopularTimeSlots();
            List<String> popularPurposes = new ArrayList<>(); // Add method to AI service if needed

            Company mostPopularCompany = null; // Add method to company service if needed
            List<Company> vipCompanies = new ArrayList<>(); // Add method to company service if needed

            long activeUsers = allUsers.stream().filter(u -> u.getStatus() == User.Status.ACTIVE).count();
            long vipUsers = allUsers.stream().filter(u -> u.getUserType() == User.UserType.VIP).count();

            List<Booking> vipBookings = bookingService.getVIPPriorityBookings();
            long vipBookingCount = vipBookings.size();

            Date today = new Date(System.currentTimeMillis());
            List<Booking> todayBookings = bookingService.getBookingsByDateRange(today, today);

            request.setAttribute("admin", admin);
            request.setAttribute("totalBookings", totalBookings);
            request.setAttribute("approvedBookings", approvedBookings);
            request.setAttribute("pendingBookings", pendingBookings);
            request.setAttribute("rejectedBookings", rejectedBookings);
            request.setAttribute("approvalRate", Math.round(approvalRate));
            request.setAttribute("popularTimeSlots", popularTimeSlots);
            request.setAttribute("popularPurposes", popularPurposes);
            request.setAttribute("mostPopularCompany", mostPopularCompany);
            request.setAttribute("vipCompanies", vipCompanies);
            request.setAttribute("activeUsers", activeUsers);
            request.setAttribute("vipUsers", vipUsers);
            request.setAttribute("vipBookingCount", vipBookingCount);
            request.setAttribute("todayBookings", todayBookings.size());
            request.setAttribute("totalCompanies", allCompanies.size());

            System.out.println("✅ Analytics dashboard loaded successfully");
            request.getRequestDispatcher("/admin/analytics.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error loading analytics: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error loading analytics", e);
        }
    }

    private void showCompanyManagement(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        System.out.println("🏢 Loading company management for admin: " + admin.getName());

        try {
            List<Company> allCompanies = companyService.getAllActiveCompanies();

            Map<Integer, Integer> companyCabinCounts = new HashMap<>();
            for (Company company : allCompanies) {
                try {
                    int cabinCount = companyService.getCompanyCabinCount(company.getCompanyId());
                    companyCabinCounts.put(company.getCompanyId(), cabinCount);
                } catch (Exception e) {
                    companyCabinCounts.put(company.getCompanyId(), 0);
                }
            }

            List<Company> vipCompanies = new ArrayList<>(); // Add method if needed

            request.setAttribute("admin", admin);
            request.setAttribute("allCompanies", allCompanies);
            request.setAttribute("vipCompanies", vipCompanies);
            request.setAttribute("totalCompanies", allCompanies.size());
            request.setAttribute("companyCabinCounts", companyCabinCounts);

            System.out.println("✅ Company management loaded successfully");
            request.getRequestDispatcher("/admin/company-management.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error loading company management: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error loading company management", e);
        }
    }

    private void promoteUser(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");
        String newType = request.getParameter("userType");

        try {
            if (userIdStr == null || newType == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID and type are required");
                return;
            }

            int userId = Integer.parseInt(userIdStr);
            boolean promotionSuccess = false;
            String successMessage = "";

            switch (newType.toUpperCase()) {
                case "VIP":
                    promotionSuccess = userService.promoteToVIP(userId);
                    successMessage = "User promoted to VIP successfully!";
                    break;
                case "ADMIN":
                    promotionSuccess = userService.promoteToAdmin(userId);
                    successMessage = "User promoted to Admin successfully!";
                    break;
                default:
                    System.err.println("❌ Invalid user type for promotion: " + newType);
                    break;
            }

            HttpSession session = request.getSession();
            if (promotionSuccess) {
                session.setAttribute("successMessage", successMessage);
            } else {
                session.setAttribute("errorMessage", "Failed to promote user. Please try again.");
            }

            response.sendRedirect(request.getContextPath() + "/admin/users");

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        } catch (Exception e) {
            System.err.println("❌ Error promoting user: " + e.getMessage());
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Error promoting user. Please try again.");
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    private void bulkApproveBookings(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        String[] bookingIds = request.getParameterValues("bookingIds");

        try {
            if (bookingIds == null || bookingIds.length == 0) {
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Please select bookings to approve");
                response.sendRedirect(request.getContextPath() + "/admin/bookings");
                return;
            }

            int successCount = 0;
            int failCount = 0;

            for (String bookingIdStr : bookingIds) {
                try {
                    int bookingId = Integer.parseInt(bookingIdStr);
                    boolean success = bookingService.approveBooking(bookingId, admin);
                    if (success) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (NumberFormatException e) {
                    failCount++;
                }
            }

            HttpSession session = request.getSession();
            if (successCount > 0) {
                session.setAttribute("successMessage",
                        successCount + " bookings approved successfully!" +
                                (failCount > 0 ? " (" + failCount + " failed)" : ""));
            } else {
                session.setAttribute("errorMessage", "Failed to approve any bookings. Please try again.");
            }

            response.sendRedirect(request.getContextPath() + "/admin/bookings");

        } catch (Exception e) {
            System.err.println("❌ Error in bulk approval: " + e.getMessage());
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Error in bulk approval. Please try again.");
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
        }
    }

    // UTILITY METHODS

    private User getCurrentUser(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                return (User) session.getAttribute("user");
            }
        } catch (Exception e) {
            System.err.println("❌ Error getting current user: " + e.getMessage());
        }
        return null;
    }

    private String getActionFromRequest(HttpServletRequest request) {
        try {
            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();
            String path = requestURI.substring(contextPath.length());

            // Prevent JSP files from being treated as actions
            if (path.endsWith(".jsp")) {
                return "dashboard";
            }

            if (path.startsWith("/admin/")) {
                path = path.substring(7);
            } else if (path.startsWith("/admin")) {
                path = path.substring(6);
            }

            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            String queryAction = request.getParameter("action");
            if (queryAction != null && !queryAction.trim().isEmpty()) {
                return queryAction.trim();
            }

            return path.isEmpty() ? "dashboard" : path;

        } catch (Exception e) {
            System.err.println("❌ Error extracting action: " + e.getMessage());
            return "dashboard";
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response,
                             String message, Exception e) throws ServletException, IOException {
        try {
            request.setAttribute("errorMessage", message);
            request.setAttribute("errorDetails", e.getMessage());

            try {
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            } catch (Exception ex) {
                response.setContentType("text/html");
                response.getWriter().println("<html><body>");
                response.getWriter().println("<h2>Admin Dashboard Error</h2>");
                response.getWriter().println("<p>" + message + "</p>");
                response.getWriter().println("<p><a href='" + request.getContextPath() + "/login'>Back to Login</a></p>");
                response.getWriter().println("</body></html>");
            }
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        }
    }
    // ===============================
// ✅ COMPANY MANAGEMENT METHODS
// ===============================

    // ✅ NEW: Show add company form
    private void showAddCompanyForm(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        System.out.println("➕ Loading add company form for admin: " + admin.getName());

        try {
            request.setAttribute("admin", admin);

            System.out.println("✅ Add company form loaded successfully");
            request.getRequestDispatcher("/admin/add-company.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error loading add company form: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error loading add company form", e);
        }
    }

    // ✅ NEW: Add company
    private void addCompany(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        System.out.println("➕ Admin adding new company by: " + admin.getName());

        try {
            String name = request.getParameter("name");
            String location = request.getParameter("location");
            String contactInfo = request.getParameter("contactInfo");
            String statusStr = request.getParameter("status");

            // Validate required fields
            if (name == null || name.trim().isEmpty() ||
                    location == null || location.trim().isEmpty() ||
                    contactInfo == null || contactInfo.trim().isEmpty()) {
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "All fields are required");
                response.sendRedirect(request.getContextPath() + "/admin/add-company");
                return;
            }

            // Check if company name already exists
            Company existingCompany = companyService.getCompanyByName(name.trim());
            if (existingCompany != null) {
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Company name '" + name + "' already exists. Please choose a different name.");
                response.sendRedirect(request.getContextPath() + "/admin/add-company");
                return;
            }

            // Create new company
            Company newCompany = new Company();
            newCompany.setName(name.trim());
            newCompany.setLocation(location.trim());
            newCompany.setContactInfo(contactInfo.trim());

            // Set status (default to ACTIVE)
            if (statusStr != null && !statusStr.isEmpty()) {
                newCompany.setStatus(Company.Status.valueOf(statusStr));
            } else {
                newCompany.setStatus(Company.Status.ACTIVE);
            }

            boolean addSuccess = companyService.createCompany(newCompany);

            HttpSession session = request.getSession();
            if (addSuccess) {
                System.out.println("✅ Company added successfully: " + name + " by admin: " + admin.getName());
                session.setAttribute("successMessage", "Company '" + name + "' added successfully!");
                response.sendRedirect(request.getContextPath() + "/admin/companies");
            } else {
                System.err.println("❌ Company addition failed: " + name);
                session.setAttribute("errorMessage", "Failed to add company. Please try again.");
                response.sendRedirect(request.getContextPath() + "/admin/add-company");
            }

        } catch (Exception e) {
            System.err.println("❌ Error adding company: " + e.getMessage());
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Error adding company: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/add-company");
        }
    }

    // ✅ NEW: Delete company
    private void deleteCompany(HttpServletRequest request, HttpServletResponse response, User admin)
            throws ServletException, IOException {
        String companyIdStr = request.getParameter("id");
        System.out.println("🗑️ Admin deleting company: " + companyIdStr + " by " + admin.getName());

        try {
            if (companyIdStr == null || companyIdStr.trim().isEmpty()) {
                System.err.println("❌ No company ID provided for deletion");
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Company ID is required for deletion");
                response.sendRedirect(request.getContextPath() + "/admin/companies");
                return;
            }

            int companyId = Integer.parseInt(companyIdStr);

            // Check if company exists
            Company company = companyService.getCompanyById(companyId);
            if (company == null) {
                System.err.println("❌ Company not found: " + companyId);
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Company not found");
                response.sendRedirect(request.getContextPath() + "/admin/companies");
                return;
            }

            // Check if company has cabins
            int cabinCount = companyService.getCompanyCabinCount(companyId);
            if (cabinCount > 0) {
                System.err.println("❌ Cannot delete company with cabins: " + companyId);
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage",
                        "Cannot delete company '" + company.getName() + "'. It has " + cabinCount +
                                " cabin(s). Please remove all cabins first.");
                response.sendRedirect(request.getContextPath() + "/admin/companies");
                return;
            }

            // ✅ SOFT DELETE: Set status to INACTIVE
            boolean deleteSuccess = companyService.deactivateCompany(companyId);

            HttpSession session = request.getSession();
            if (deleteSuccess) {
                System.out.println("✅ Company deactivated successfully: " + companyId + " (" + company.getName() + ") by admin: " + admin.getName());
                session.setAttribute("successMessage", "Company '" + company.getName() + "' deactivated successfully!");
            } else {
                System.err.println("❌ Company deletion failed: " + companyId);
                session.setAttribute("errorMessage", "Failed to delete company. Please try again.");
            }

            response.sendRedirect(request.getContextPath() + "/admin/companies");

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid company ID format: " + companyIdStr);
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Invalid company ID");
            response.sendRedirect(request.getContextPath() + "/admin/companies");
        } catch (Exception e) {
            System.err.println("❌ Error deleting company: " + e.getMessage());
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Error deleting company: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/companies");
        }
    }

}
