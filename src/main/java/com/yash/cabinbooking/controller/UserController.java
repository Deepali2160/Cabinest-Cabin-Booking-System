package com.yash.cabinbooking.controller;

import com.yash.cabinbooking.service.UserService;
import com.yash.cabinbooking.service.CompanyService;
import com.yash.cabinbooking.service.BookingService;
import com.yash.cabinbooking.serviceimpl.*;
import com.yash.cabinbooking.model.*;
import com.yash.cabinbooking.dao.CabinDao;
import com.yash.cabinbooking.daoimpl.CabinDaoImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * USER CONTROLLER - SINGLE COMPANY VERSION
 *
 * Modified for Yash Technology single company usage
 * - Removed AI service dependencies (causing errors)
 * - Simplified for single organization operations
 * - Enhanced dashboard with booking statistics
 */
@WebServlet(name = "UserController", urlPatterns = {"/dashboard", "/profile", "/company/*"})
public class UserController extends HttpServlet {

    private UserService userService;
    private CompanyService companyService;
    private BookingService bookingService;
    // ✅ REMOVED: AIRecommendationService (causing errors)
    private CabinDao cabinDAO;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
        this.companyService = new CompanyServiceImpl();
        this.bookingService = new BookingServiceImpl();
        // ✅ REMOVED: this.aiService = new AIRecommendationServiceImpl();
        this.cabinDAO = new CabinDaoImpl();
        System.out.println("🔧 UserController initialized for Yash Technology (Single Company)");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            System.out.println("🔒 Unauthorized access attempt, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = getActionFromRequest(request);
        System.out.println("🌐 GET Request: " + action + " by user: " + currentUser.getName());

        switch (action) {
            case "dashboard":
                showDashboard(request, response, currentUser);
                break;
            case "profile":
                showProfile(request, response, currentUser);
                break;
            case "company":
                handleCompanyBrowsing(request, response, currentUser);
                break;
            default:
                showDashboard(request, response, currentUser);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = getActionFromRequest(request);
        System.out.println("📝 POST Request: " + action + " by user: " + currentUser.getName());

        switch (action) {
            case "profile":
                updateProfile(request, response, currentUser);
                break;
            default:
                showDashboard(request, response, currentUser);
                break;
        }
    }

    // ✅ UPDATED: Single company dashboard
    private void showDashboard(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("🏠 Loading dashboard for user: " + user.getName());

        try {
            // ✅ SINGLE COMPANY: Get company configuration instead of by ID
            Company userCompany = companyService.getCompanyConfig();
            if (userCompany == null) {
                System.err.println("❌ Company configuration not found, creating default");
                userCompany = createDefaultCompany();
            }

            // ✅ SINGLE COMPANY: Get accessible cabins without company parameter
            List<Cabin> accessibleCabins = cabinDAO.getAccessibleCabins(user);

            // ✅ SIMPLIFIED: Get recommended cabins (VIP cabins for normal users)
            List<Cabin> recommendedCabins = getRecommendedCabins(user);

            // Get user's recent bookings
            List<Booking> userBookings = bookingService.getUserBookings(user.getUserId());
            List<Booking> recentBookings = userBookings.stream()
                    .limit(5)
                    .collect(Collectors.toList());

            // ✅ SIMPLIFIED: Get popular time slots without AI
            List<String> popularTimeSlots = bookingService.getPopularTimeSlots();

            // Calculate basic user statistics
            long pendingBookings = userBookings.stream()
                    .filter(booking -> booking.getStatus() == Booking.Status.PENDING)
                    .count();

            long approvedBookings = userBookings.stream()
                    .filter(booking -> booking.getStatus() == Booking.Status.APPROVED)
                    .count();

            // ✅ SIMPLIFIED: Basic booking score calculation
            double bookingScore = calculateBasicBookingScore(userBookings);

            // Set attributes for JSP
            request.setAttribute("user", user);
            request.setAttribute("company", userCompany);
            request.setAttribute("cabins", accessibleCabins);
            request.setAttribute("recommendedCabins", recommendedCabins);
            request.setAttribute("recentBookings", recentBookings);
            request.setAttribute("popularTimeSlots", popularTimeSlots);
            request.setAttribute("totalBookings", userBookings.size());
            request.setAttribute("pendingBookings", (int) pendingBookings);
            request.setAttribute("approvedBookings", (int) approvedBookings);
            request.setAttribute("bookingScore", Math.round(bookingScore));

            System.out.println("✅ Dashboard data loaded successfully");
            System.out.println("   - Accessible cabins: " + accessibleCabins.size());
            System.out.println("   - Recommended cabins: " + recommendedCabins.size());
            System.out.println("   - Recent bookings: " + recentBookings.size());
            System.out.println("   - User booking score: " + Math.round(bookingScore));

            request.getRequestDispatcher("/user/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading dashboard. Please try again.");
            request.getRequestDispatcher("/common/error.jsp").forward(request, response);
        }
    }

    // ✅ UPDATED: Single company profile
    private void showProfile(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("👤 Loading profile for user: " + user.getName());

        try {
            // ✅ SINGLE COMPANY: Get company configuration
            Company currentCompany = companyService.getCompanyConfig();
            if (currentCompany == null) {
                currentCompany = createDefaultCompany();
            }

            // Get user statistics
            int totalBookings = userService.getUserBookingCount(user.getUserId());

            // Get user's booking history for better profile display
            List<Booking> userBookings = bookingService.getUserBookings(user.getUserId());

            long approvedBookings = userBookings.stream()
                    .filter(booking -> booking.getStatus() == Booking.Status.APPROVED)
                    .count();

            // Set attributes
            request.setAttribute("user", user);
            request.setAttribute("currentCompany", currentCompany);
            request.setAttribute("totalBookings", totalBookings);
            request.setAttribute("approvedBookings", (int) approvedBookings);

            System.out.println("✅ Profile data loaded for user: " + user.getName());

            request.getRequestDispatcher("/user/profile.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error loading profile: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading profile. Please try again.");
            request.getRequestDispatcher("/common/error.jsp").forward(request, response);
        }
    }

    // ✅ UPDATED: Single company profile update
    private void updateProfile(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("✏️ Updating profile for user: " + user.getName());

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            boolean updateSuccess = true;
            String errorMessage = null;

            // Update basic profile information
            if (name != null && !name.trim().isEmpty() && !name.equals(user.getName())) {
                user.setName(name.trim());
                System.out.println("📝 Name updated to: " + name);
            }

            // Update email if changed
            if (email != null && !email.trim().isEmpty() && !email.equals(user.getEmail())) {
                if (userService.isEmailAvailable(email.trim())) {
                    user.setEmail(email.trim().toLowerCase());
                    System.out.println("📧 Email updated to: " + email);
                } else {
                    errorMessage = "Email address is already in use";
                    updateSuccess = false;
                }
            }

            // ✅ SINGLE COMPANY: Ensure user belongs to default company
            user.setDefaultCompanyId(1); // Always set to Yash Technology

            // Update password if provided
            if (currentPassword != null && !currentPassword.isEmpty() &&
                    newPassword != null && !newPassword.isEmpty()) {

                if (newPassword.equals(confirmPassword)) {
                    if (newPassword.length() >= 6) {
                        boolean passwordChanged = userService.changePassword(
                                user.getUserId(), currentPassword, newPassword);

                        if (passwordChanged) {
                            System.out.println("🔑 Password updated successfully");
                        } else {
                            errorMessage = "Current password is incorrect";
                            updateSuccess = false;
                        }
                    } else {
                        errorMessage = "New password must be at least 6 characters";
                        updateSuccess = false;
                    }
                } else {
                    errorMessage = "New passwords do not match";
                    updateSuccess = false;
                }
            }

            // Save profile updates
            if (updateSuccess && errorMessage == null) {
                updateSuccess = userService.updateUserProfile(user);
                if (updateSuccess) {
                    // Update session with new user data
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    session.setAttribute("userName", user.getName());
                    session.setAttribute("userEmail", user.getEmail());

                    System.out.println("✅ Profile updated successfully for: " + user.getName());
                    request.setAttribute("successMessage", "Profile updated successfully!");
                } else {
                    errorMessage = "Failed to update profile. Please try again.";
                }
            }

            if (errorMessage != null) {
                System.err.println("❌ Profile update failed: " + errorMessage);
                request.setAttribute("error", errorMessage);
            }

            // Redirect back to profile page
            showProfile(request, response, user);

        } catch (Exception e) {
            System.err.println("❌ Error updating profile: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error updating profile. Please try again.");
            showProfile(request, response, user);
        }
    }

    // ✅ UPDATED: Single company browsing (simplified)
    private void handleCompanyBrowsing(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("🏢 Handling company browsing for user: " + user.getName());

        try {
            // ✅ SINGLE COMPANY: Always show Yash Technology cabins
            Company company = companyService.getCompanyConfig();
            if (company == null) {
                company = createDefaultCompany();
            }

            List<Cabin> companyCabins = cabinDAO.getAccessibleCabins(user);
            List<Cabin> recommendedCabins = getRecommendedCabins(user);

            request.setAttribute("user", user);
            request.setAttribute("company", company);
            request.setAttribute("cabins", companyCabins);
            request.setAttribute("recommendedCabins", recommendedCabins);

            System.out.println("🏠 Loaded " + companyCabins.size() + " cabins for " + company.getName());

            request.getRequestDispatcher("/user/company-cabins.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error handling company browsing: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading company information");
            request.getRequestDispatcher("/common/error.jsp").forward(request, response);
        }
    }

    // ================================
    // UTILITY METHODS
    // ================================

    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }

    private String getActionFromRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String action = requestURI.substring(contextPath.length());

        if (action.startsWith("/")) {
            action = action.substring(1);
        }

        // Handle nested paths
        if (action.contains("/")) {
            return action.split("/")[0];
        }

        return action.isEmpty() ? "dashboard" : action;
    }

    // ✅ NEW: Create default company for fallback
    private Company createDefaultCompany() {
        Company defaultCompany = new Company();
        defaultCompany.setCompanyId(1);
        defaultCompany.setName("Yash Technology");
        defaultCompany.setLocation("Indore");
        defaultCompany.setContactInfo("contact@yashtech.com");
        return defaultCompany;
    }

    // ✅ NEW: Get recommended cabins without AI service
    private List<Cabin> getRecommendedCabins(User user) {
        try {
            if (user.isVIP() || user.isAdmin()) {
                // VIP and admin users get VIP cabins as recommendations
                return cabinDAO.getVIPOnlyCabins().stream()
                        .limit(3)
                        .collect(Collectors.toList());
            } else {
                // Normal users get high-capacity cabins as recommendations
                return cabinDAO.getAllActiveCabins().stream()
                        .filter(cabin -> cabin.getCapacity() >= 6)
                        .limit(3)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("❌ Error getting recommended cabins: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    // ✅ NEW: Calculate basic booking score without AI
    private double calculateBasicBookingScore(List<Booking> userBookings) {
        if (userBookings.isEmpty()) {
            return 0.0;
        }

        long approvedBookings = userBookings.stream()
                .filter(booking -> booking.getStatus() == Booking.Status.APPROVED)
                .count();

        double approvalRate = (double) approvedBookings / userBookings.size();
        return Math.min(100.0, approvalRate * 100.0);
    }
}
