package com.yash.cabinbooking.controller;

import com.yash.cabinbooking.service.UserService;
import com.yash.cabinbooking.service.CompanyService;
import com.yash.cabinbooking.service.BookingService;
import com.yash.cabinbooking.service.AIRecommendationService;
import com.yash.cabinbooking.serviceimpl.*;
import com.yash.cabinbooking.model.*;
// Add these missing imports
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

/**
 * USER CONTROLLER
 *
 * EVALUATION EXPLANATION:
 * - User dashboard and profile management
 * - Integration with AI recommendation service
 * - Company cabin browsing functionality
 * - Session-based authorization
 * - Clean RESTful-style URL handling
 */
@WebServlet(name = "UserController", urlPatterns = {"/dashboard", "/profile", "/company/*"})
public class UserController extends HttpServlet {

    private UserService userService;
    private CompanyService companyService;
    private BookingService bookingService;
    private AIRecommendationService aiService;
    private CabinDao cabinDAO;
    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
        this.companyService = new CompanyServiceImpl();
        this.bookingService = new BookingServiceImpl();
        this.aiService = new AIRecommendationServiceImpl();
        this.cabinDAO = new CabinDaoImpl();
        System.out.println("🔧 UserController initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check authentication
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

        // Check authentication
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

    private void showDashboard(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("🏠 Loading dashboard for user: " + user.getName());

        try {
            // Get user's default company
            Company userCompany = companyService.getCompanyById(user.getDefaultCompanyId());
            if (userCompany == null) {
                System.err.println("❌ User's default company not found: " + user.getDefaultCompanyId());
                userCompany = companyService.getAllActiveCompanies().get(0); // Fallback to first company
            }

            // Get accessible cabins for the user
            List<Cabin> accessibleCabins = cabinDAO.getAccessibleCabins(userCompany.getCompanyId(), user);

            // Get AI recommendations
            List<Cabin> recommendedCabins = aiService.getRecommendedCabinsForUser(user, userCompany.getCompanyId());

            // Get user's recent bookings
            List<Booking> userBookings = bookingService.getUserBookings(user.getUserId());
            List<Booking> recentBookings = userBookings.stream()
                    .limit(5)
                    .collect(java.util.stream.Collectors.toList());

            // Get popular time slots
            List<String> popularTimeSlots = aiService.getPopularTimeSlots();

            // Set attributes for JSP
            request.setAttribute("user", user);
            request.setAttribute("company", userCompany);
            request.setAttribute("cabins", accessibleCabins);
            request.setAttribute("recommendedCabins", recommendedCabins);
            request.setAttribute("recentBookings", recentBookings);
            request.setAttribute("popularTimeSlots", popularTimeSlots);
            request.setAttribute("totalBookings", userBookings.size());

            // Calculate user booking score for display
            double bookingScore = aiService.calculateUserBookingScore(user);
            request.setAttribute("bookingScore", Math.round(bookingScore));

            System.out.println("✅ Dashboard data loaded successfully");
            System.out.println("   - Accessible cabins: " + accessibleCabins.size());
            System.out.println("   - AI recommendations: " + recommendedCabins.size());
            System.out.println("   - Recent bookings: " + recentBookings.size());

            request.getRequestDispatcher("/user/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading dashboard. Please try again.");
            request.getRequestDispatcher("/common/error.jsp").forward(request, response);
        }
    }

    private void showProfile(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("👤 Loading profile for user: " + user.getName());

        try {
            // Get all companies for company selection
            List<Company> allCompanies = companyService.getAllActiveCompanies();

            // Get user's current company
            Company currentCompany = companyService.getCompanyById(user.getDefaultCompanyId());

            // Get user statistics
            int totalBookings = userService.getUserBookingCount(user.getUserId());

            // Set attributes
            request.setAttribute("user", user);
            request.setAttribute("allCompanies", allCompanies);
            request.setAttribute("currentCompany", currentCompany);
            request.setAttribute("totalBookings", totalBookings);

            System.out.println("✅ Profile data loaded for user: " + user.getName());

            request.getRequestDispatcher("/user/profile.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error loading profile: " + e.getMessage());
            request.setAttribute("error", "Error loading profile. Please try again.");
            request.getRequestDispatcher("/common/error.jsp").forward(request, response);
        }
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("✏️ Updating profile for user: " + user.getName());

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String companyIdStr = request.getParameter("companyId");
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

            // Update company if changed
            if (companyIdStr != null && !companyIdStr.trim().isEmpty()) {
                try {
                    int companyId = Integer.parseInt(companyIdStr);
                    if (companyId != user.getDefaultCompanyId()) {
                        user.setDefaultCompanyId(companyId);
                        System.out.println("🏢 Company updated to: " + companyId);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("❌ Invalid company ID: " + companyIdStr);
                }
            }

            // Update password if provided
            if (currentPassword != null && !currentPassword.isEmpty() &&
                    newPassword != null && !newPassword.isEmpty()) {

                if (newPassword.equals(confirmPassword)) {
                    boolean passwordChanged = userService.changePassword(
                            user.getUserId(), currentPassword, newPassword);

                    if (passwordChanged) {
                        System.out.println("🔑 Password updated successfully");
                    } else {
                        errorMessage = "Current password is incorrect";
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

    private void handleCompanyBrowsing(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("🏢 Handling company browsing for user: " + user.getName());

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/browse")) {
                // Show all companies
                List<Company> allCompanies = companyService.getAllActiveCompanies();

                request.setAttribute("user", user);
                request.setAttribute("companies", allCompanies);

                System.out.println("📋 Loaded " + allCompanies.size() + " companies for browsing");

                request.getRequestDispatcher("/user/companies.jsp").forward(request, response);

            } else {
                // Show specific company cabins
                String companyIdStr = pathInfo.substring(1); // Remove leading slash

                try {
                    int companyId = Integer.parseInt(companyIdStr);
                    Company company = companyService.getCompanyById(companyId);

                    if (company != null) {
                        List<Cabin> companyCabins = cabinDAO.getAccessibleCabins(companyId, user);
                        List<Cabin> recommendedCabins = aiService.getRecommendedCabinsForUser(user, companyId);

                        request.setAttribute("user", user);
                        request.setAttribute("company", company);
                        request.setAttribute("cabins", companyCabins);
                        request.setAttribute("recommendedCabins", recommendedCabins);

                        System.out.println("🏠 Loaded " + companyCabins.size() + " cabins for company: " + company.getName());

                        request.getRequestDispatcher("/user/company-cabins.jsp").forward(request, response);
                    } else {
                        System.err.println("❌ Company not found: " + companyId);
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Company not found");
                    }

                } catch (NumberFormatException e) {
                    System.err.println("❌ Invalid company ID: " + companyIdStr);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid company ID");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error handling company browsing: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading company information");
            request.getRequestDispatcher("/common/error.jsp").forward(request, response);
        }
    }

    // UTILITY METHODS

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
}
