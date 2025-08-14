package com.yash.cabinbooking.controller;

import com.yash.cabinbooking.service.UserService;
import com.yash.cabinbooking.service.CompanyService;
import com.yash.cabinbooking.serviceimpl.UserServiceImpl;
import com.yash.cabinbooking.serviceimpl.CompanyServiceImpl;
import com.yash.cabinbooking.model.User;
import com.yash.cabinbooking.model.Company;
import com.yash.cabinbooking.util.PasswordUtil; // ✅ ADDED: Import PasswordUtil

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * AUTHENTICATION CONTROLLER - SINGLE COMPANY VERSION WITH SECURE PASSWORD HASHING
 *
 * Enhanced Features:
 * - BCrypt password hashing for maximum security
 * - Strong password validation
 * - Secure user authentication
 * - Protection against rainbow table attacks
 * - Enhanced session management for all user types
 * - Improved role-based redirect logic
 * - Single company registration process
 * - Fixed admin session attributes for JSP compatibility
 */
@WebServlet(name = "AuthController", urlPatterns = {"/auth", "/login", "/logout", "/register"})
public class AuthController extends HttpServlet {

    private UserService userService;
    private CompanyService companyService;

    // ✅ SINGLE COMPANY: Static constants
    private static final String COMPANY_NAME = "Yash Technology";
    private static final int DEFAULT_COMPANY_ID = 1;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
        this.companyService = new CompanyServiceImpl();
        System.out.println("🔧 AuthController initialized for " + COMPANY_NAME + " (Single Company) with BCrypt Security");
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
        System.out.println("🔐 Showing login page for " + COMPANY_NAME);

        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            System.out.println("✅ User already logged in: " + user.getName() + ", redirecting to appropriate dashboard");

            String redirectUrl = determineRedirectUrl(user);
            response.sendRedirect(request.getContextPath() + redirectUrl);
            return;
        }

        // ✅ SINGLE COMPANY: Set company info for login page
        request.setAttribute("companyName", COMPANY_NAME);
        request.getRequestDispatcher("/common/login.jsp").forward(request, response);
    }

    private void showRegistrationPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("📝 Showing registration page for " + COMPANY_NAME);

        // ✅ SINGLE COMPANY: Get company configuration
        Company company = companyService.getCompanyConfig();
        if (company == null) {
            company = createDefaultCompany();
        }

        request.setAttribute("company", company);
        request.setAttribute("companyName", COMPANY_NAME);
        request.getRequestDispatcher("/common/register.jsp").forward(request, response);
    }

    // ✅ SECURITY ENHANCED: Login with BCrypt password verification
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("🔐 Processing secure login request for " + COMPANY_NAME);

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

        // ✅ SECURITY ENHANCEMENT: Authenticate with hashed password verification
        User user = userService.authenticateUserWithHashedPassword(email.trim(), password);

        if (user != null) {
            // Successful login
            System.out.println("✅ Secure login successful for: " + user.getName() +
                    " (" + user.getUserTypeDisplay() + ") at " + COMPANY_NAME);

            // ✅ ENHANCED: Create comprehensive session with all required attributes
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
            session.setAttribute("isSuperAdmin", user.isSuperAdmin());

            // ✅ SINGLE COMPANY: Company-specific session attributes
            session.setAttribute("companyName", COMPANY_NAME);
            session.setAttribute("companyId", DEFAULT_COMPANY_ID);

            // Set session timeout (30 minutes)
            session.setMaxInactiveInterval(30 * 60);

            System.out.println("🎉 Complete secure session created for user: " + user.getName() +
                    " (Type: " + user.getUserType() + ") - Company: " + COMPANY_NAME);

            // ✅ ENHANCED: Role-based redirect logic
            String redirectUrl = determineRedirectUrl(user);
            System.out.println("🚀 Redirecting to: " + redirectUrl);
            response.sendRedirect(request.getContextPath() + redirectUrl);

        } else {
            // Login failed
            System.err.println("❌ Secure login failed for email: " + email);
            request.setAttribute("error", "Invalid email or password");
            request.setAttribute("email", email); // Keep email for user convenience
            showLoginPage(request, response);
        }
    }

    // ✅ SECURITY ENHANCED: Registration with BCrypt password hashing
    private void handleRegistration(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("📝 Processing secure registration request for " + COMPANY_NAME);

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        System.out.println("👤 Registration attempt for: " + name + " (" + email + ") at " + COMPANY_NAME);

        // ✅ ENHANCED: Input validation with strong password requirements
        String validationError = validateRegistrationInput(name, email, password, confirmPassword);
        if (validationError != null) {
            System.err.println("❌ Registration validation failed: " + validationError);
            request.setAttribute("error", validationError);
            request.setAttribute("name", name);
            request.setAttribute("email", email);
            showRegistrationPage(request, response);
            return;
        }

        // Check if email already exists
        if (!userService.isEmailAvailable(email)) {
            System.err.println("❌ Email already exists: " + email);
            request.setAttribute("error", "Email address is already registered");
            request.setAttribute("name", name);
            showRegistrationPage(request, response);
            return;
        }

        // ✅ SECURITY ENHANCEMENT: Hash password before creating user
        String hashedPassword;
        try {
            hashedPassword = PasswordUtil.hashPassword(password);
            System.out.println("🔐 Password hashed successfully for user: " + name + " using BCrypt with salt");
        } catch (Exception e) {
            System.err.println("❌ Password hashing failed: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Registration failed due to security error. Please try again.");
            request.setAttribute("name", name);
            request.setAttribute("email", email);
            showRegistrationPage(request, response);
            return;
        }

        // ✅ SINGLE COMPANY: Create new user with securely hashed password
        User newUser = new User(name.trim(), email.trim().toLowerCase(), hashedPassword); // ✅ HASHED PASSWORD
        newUser.setDefaultCompanyId(DEFAULT_COMPANY_ID);
        newUser.setUserType(User.UserType.NORMAL); // Default to normal user
        newUser.setStatus(User.Status.ACTIVE);

        boolean registrationSuccess = userService.registerUser(newUser);

        if (registrationSuccess) {
            System.out.println("✅ Secure registration successful for: " + newUser.getName() +
                    " (ID: " + newUser.getUserId() + ") at " + COMPANY_NAME + " with BCrypt password protection");

            // ✅ ENHANCED: Auto-login with complete session setup
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
            session.setAttribute("isSuperAdmin", newUser.isSuperAdmin());

            // ✅ SINGLE COMPANY: Company-specific attributes
            session.setAttribute("companyName", COMPANY_NAME);
            session.setAttribute("companyId", DEFAULT_COMPANY_ID);

            session.setMaxInactiveInterval(30 * 60);

            System.out.println("🎉 Auto-login successful for " + COMPANY_NAME + ", redirecting to dashboard");

            // Set success message with security information
            session.setAttribute("successMessage",
                    "Welcome to " + COMPANY_NAME + "! Your account has been created successfully with enterprise-grade password security.");

            // Redirect to user dashboard (new users are always NORMAL)
            response.sendRedirect(request.getContextPath() + "/dashboard");

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
                System.out.println("👋 Logging out user: " + user.getName() +
                        " (" + user.getUserTypeDisplay() + ") from " + COMPANY_NAME);
            }

            session.invalidate();
            System.out.println("🔒 Session invalidated successfully");
        }

        // Redirect to login page with logout message
        request.setAttribute("message", "You have been logged out successfully from " + COMPANY_NAME);
        showLoginPage(request, response);
    }

    // ================================
    // UTILITY METHODS
    // ================================

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
     * ✅ ENHANCED: Role-based redirect logic for single company
     */
    private String determineRedirectUrl(User user) {
        switch (user.getUserType()) {
            case SUPER_ADMIN:
                System.out.println("🔱 Super Admin detected: " + user.getName() +
                        ", redirecting to admin dashboard with full privileges");
                return "/admin/dashboard";

            case ADMIN:
                System.out.println("👨‍💼 Admin detected: " + user.getName() +
                        ", redirecting to admin dashboard");
                return "/admin/dashboard";

            case VIP:
                System.out.println("⭐ VIP user detected: " + user.getName() +
                        ", redirecting to VIP-enhanced dashboard");
                return "/dashboard";

            case NORMAL:
            default:
                System.out.println("👤 Normal user detected: " + user.getName() +
                        ", redirecting to user dashboard");
                return "/dashboard";
        }
    }

    // ✅ SECURITY ENHANCED: Strong password validation
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

        // ✅ ENHANCED: Minimum 8 characters for better security
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters long";
        }

        // ✅ ENHANCED: Strong password validation using PasswordUtil
        if (!PasswordUtil.isStrongPassword(password)) {
            return "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (!@#$%^&*()_+-=[]{}|;:,.<>?)";
        }

        if (confirmPassword == null || !password.equals(confirmPassword)) {
            return "Passwords do not match";
        }

        // ✅ ADDITIONAL: Password strength feedback
        String strength = PasswordUtil.getPasswordStrength(password);
        if ("Very Weak".equals(strength) || "Weak".equals(strength)) {
            return "Password is too weak. Please choose a stronger password with mix of characters.";
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

    // ✅ NEW: Create default company for fallback
    private Company createDefaultCompany() {
        Company defaultCompany = new Company();
        defaultCompany.setCompanyId(DEFAULT_COMPANY_ID);
        defaultCompany.setName(COMPANY_NAME);
        defaultCompany.setLocation("Indore");
        defaultCompany.setContactInfo("contact@yashtech.com");
        return defaultCompany;
    }
}
