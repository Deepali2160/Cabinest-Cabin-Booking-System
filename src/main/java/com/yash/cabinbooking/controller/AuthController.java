package com.yash.cabinbooking.controller;

import com.yash.cabinbooking.service.UserService;
import com.yash.cabinbooking.service.CompanyService;
import com.yash.cabinbooking.serviceimpl.UserServiceImpl;
import com.yash.cabinbooking.serviceimpl.CompanyServiceImpl;
import com.yash.cabinbooking.model.User;
import com.yash.cabinbooking.model.Company;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * AUTHENTICATION CONTROLLER - FIXED VERSION
 *
 * FIXES APPLIED:
 * - Added "admin" session attribute for JSP compatibility
 * - Enhanced role-based redirect logic for SUPER_ADMIN
 * - Improved session management for all user types
 * - Added proper admin session attributes in registration
 */
@WebServlet(name = "AuthController", urlPatterns = {"/auth", "/login", "/logout", "/register"})
public class AuthController extends HttpServlet {

    private UserService userService;
    private CompanyService companyService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
        this.companyService = new CompanyServiceImpl();
        System.out.println("🔧 AuthController initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getActionFromRequest(request);
        System.out.println("🌐 GET Request received: " + action);

        switch (action) {
            case "login":
                showLoginPage(request, response);
                break;
            case "register":
                showRegistrationPage(request, response);
                break;
            case "logout":
                handleLogout(request, response);
                break;
            default:
                showLoginPage(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getActionFromRequest(request);
        System.out.println("📝 POST Request received: " + action);

        switch (action) {
            case "login":
                handleLogin(request, response);
                break;
            case "register":
                handleRegistration(request, response);
                break;
            default:
                showLoginPage(request, response);
                break;
        }
    }

    private void showLoginPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("🔐 Showing login page");

        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            System.out.println("✅ User already logged in: " + user.getName() + ", redirecting to appropriate dashboard");

            // Redirect to appropriate dashboard based on user type
            String redirectUrl = determineRedirectUrl(user);
            response.sendRedirect(request.getContextPath() + redirectUrl);
            return;
        }

        request.getRequestDispatcher("/common/login.jsp").forward(request, response);
    }

    private void showRegistrationPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("📝 Showing registration page");

        // Get companies for registration form
        List<Company> companies = companyService.getAllActiveCompanies();
        request.setAttribute("companies", companies);

        request.getRequestDispatcher("/common/register.jsp").forward(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("🔐 Processing login request");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        System.out.println("📧 Login attempt for email: " + email);

        // Input validation
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {

            System.err.println("❌ Invalid login credentials provided");
            request.setAttribute("error", "Email and password are required");
            showLoginPage(request, response);
            return;
        }

        // Authenticate user
        User user = userService.authenticateUser(email.trim(), password);

        if (user != null) {
            // Successful login
            System.out.println("✅ Login successful for: " + user.getName() + " (" + user.getUserTypeDisplay() + ")");

            // ✅ FIXED: Create comprehensive session with all required attributes
            HttpSession session = request.getSession(true);

            // Core user attributes
            session.setAttribute("user", user);
            session.setAttribute("admin", user);                    // ⭐ MAIN FIX: JSP expects "admin"
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userType", user.getUserType());
            session.setAttribute("userName", user.getName());

            // Additional attributes for better UX
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("isAdmin", user.isAdmin());
            session.setAttribute("isVip", user.isVIP());

            // Set session timeout (30 minutes)
            session.setMaxInactiveInterval(30 * 60);

            System.out.println("🎉 Complete session created for user: " + user.getName() + " (Type: " + user.getUserType() + ")");

            // ✅ FIXED: Enhanced redirect logic with better role separation
            String redirectUrl = determineRedirectUrl(user);
            System.out.println("🚀 Redirecting to: " + redirectUrl);
            response.sendRedirect(request.getContextPath() + redirectUrl);

        } else {
            // Login failed
            System.err.println("❌ Login failed for email: " + email);
            request.setAttribute("error", "Invalid email or password");
            request.setAttribute("email", email); // Keep email for user convenience
            showLoginPage(request, response);
        }
    }

    private void handleRegistration(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("📝 Processing registration request");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String companyIdStr = request.getParameter("companyId");

        System.out.println("👤 Registration attempt for: " + name + " (" + email + ")");

        // Input validation
        String validationError = validateRegistrationInput(name, email, password, confirmPassword);
        if (validationError != null) {
            System.err.println("❌ Registration validation failed: " + validationError);
            request.setAttribute("error", validationError);
            request.setAttribute("name", name);
            request.setAttribute("email", email);
            showRegistrationPage(request, response);
            return;
        }

        // Parse company ID
        int companyId = 1; // Default company
        if (companyIdStr != null && !companyIdStr.trim().isEmpty()) {
            try {
                companyId = Integer.parseInt(companyIdStr);
            } catch (NumberFormatException e) {
                System.err.println("❌ Invalid company ID: " + companyIdStr);
            }
        }

        // Check if email already exists
        if (!userService.isEmailAvailable(email)) {
            System.err.println("❌ Email already exists: " + email);
            request.setAttribute("error", "Email address is already registered");
            request.setAttribute("name", name);
            showRegistrationPage(request, response);
            return;
        }

        // Create new user
        User newUser = new User(name.trim(), email.trim().toLowerCase(), password);
        newUser.setDefaultCompanyId(companyId);
        newUser.setUserType(User.UserType.NORMAL); // Default to normal user
        newUser.setStatus(User.Status.ACTIVE);

        boolean registrationSuccess = userService.registerUser(newUser);

        if (registrationSuccess) {
            System.out.println("✅ Registration successful for: " + newUser.getName() + " (ID: " + newUser.getUserId() + ")");

            // ✅ FIXED: Auto-login with complete session setup
            HttpSession session = request.getSession(true);

            // Core user attributes
            session.setAttribute("user", newUser);
            session.setAttribute("admin", newUser);              // ⭐ Add admin attribute for consistency
            session.setAttribute("userId", newUser.getUserId());
            session.setAttribute("userType", newUser.getUserType());
            session.setAttribute("userName", newUser.getName());

            // Additional attributes
            session.setAttribute("userEmail", newUser.getEmail());
            session.setAttribute("isAdmin", newUser.isAdmin());
            session.setAttribute("isVip", newUser.isVIP());

            session.setMaxInactiveInterval(30 * 60);

            System.out.println("🎉 Auto-login successful, redirecting to dashboard");

            // Set success message
            session.setAttribute("successMessage", "Welcome! Your account has been created successfully.");

            // Redirect to appropriate dashboard (though new users will be NORMAL)
            String redirectUrl = determineRedirectUrl(newUser);
            response.sendRedirect(request.getContextPath() + redirectUrl);

        } else {
            System.err.println("❌ Registration failed for: " + email);
            request.setAttribute("error", "Registration failed. Please try again.");
            request.setAttribute("name", name);
            request.setAttribute("email", email);
            showRegistrationPage(request, response);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("🚪 Processing logout request");

        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                System.out.println("👋 Logging out user: " + user.getName() + " (" + user.getUserTypeDisplay() + ")");
            }

            session.invalidate();
            System.out.println("🔒 Session invalidated successfully");
        }

        // Redirect to login page with logout message
        request.setAttribute("message", "You have been logged out successfully");
        showLoginPage(request, response);
    }

    // UTILITY METHODS

    private String getActionFromRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String action = requestURI.substring(contextPath.length());

        if (action.startsWith("/")) {
            action = action.substring(1);
        }

        return action.isEmpty() ? "login" : action;
    }

    /**
     * ✅ FIXED: Enhanced redirect logic with proper role separation
     */
    private String determineRedirectUrl(User user) {
        switch (user.getUserType()) {
            case SUPER_ADMIN:
                System.out.println("🔱 Super Admin detected: " + user.getName() + ", redirecting to admin dashboard");
                return "/admin/dashboard";  // Super Admin uses same admin dashboard but with more features

            case ADMIN:
                System.out.println("👨‍💼 Admin detected: " + user.getName() + ", redirecting to admin dashboard");
                return "/admin/dashboard";

            case VIP:
                System.out.println("⭐ VIP user detected: " + user.getName() + ", redirecting to VIP dashboard");
             return "/dashboard";     // Or use same dashboard with VIP features

            case NORMAL:
            default:
                System.out.println("👤 Normal user detected: " + user.getName() + ", redirecting to user dashboard");
                return "/dashboard";
        }
    }

    private String validateRegistrationInput(String name, String email, String password, String confirmPassword) {
        if (name == null || name.trim().isEmpty()) {
            return "Name is required";
        }

        if (name.length() > 100) {
            return "Name must be less than 100 characters";
        }

        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }

        if (!isValidEmail(email)) {
            return "Please enter a valid email address";
        }

        if (password == null || password.length() < 6) {
            return "Password must be at least 6 characters long";
        }

        if (confirmPassword == null || !password.equals(confirmPassword)) {
            return "Passwords do not match";
        }

        return null; // No validation errors
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }
}
