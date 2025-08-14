package com.yash.cabinbooking.serviceimpl;

import com.yash.cabinbooking.service.UserService;
import com.yash.cabinbooking.dao.UserDao;
import com.yash.cabinbooking.daoimpl.UserDaoImpl;
import com.yash.cabinbooking.model.User;
import com.yash.cabinbooking.util.DbUtil;
import com.yash.cabinbooking.util.PasswordUtil; // ✅ ADDED: Import PasswordUtil

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * USER SERVICE IMPLEMENTATION - SINGLE COMPANY VERSION WITH SECURE PASSWORD HASHING
 *
 * Enhanced Features:
 * - BCrypt password hashing and verification
 * - Secure user authentication
 * - Single company focus for Yash Technology
 * - Enhanced business logic with security
 * - AdminController integration support
 */
public class UserServiceImpl implements UserService {

    private UserDao userDAO;

    // ✅ SINGLE COMPANY: Static constants
    private static final int DEFAULT_COMPANY_ID = 1;
    private static final String COMPANY_NAME = "Yash Technology";
    private static final int MIN_PASSWORD_LENGTH = 8; // ✅ ENHANCED: Increased to 8 for better security

    public UserServiceImpl() {
        this.userDAO = new UserDaoImpl();
        System.out.println("🔧 UserService initialized for " + COMPANY_NAME + " (Single Company) with BCrypt Security");
    }

    // ✅ LEGACY: Keep old method for backward compatibility (will be deprecated)
    @Override
    public User authenticateUser(String email, String password) {
        System.out.println("🔐 Legacy authentication for user: " + email + " (Consider using BCrypt method)");

        // Input validation
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.err.println("❌ Invalid login credentials provided");
            return null;
        }

        // Authenticate through DAO
        User user = userDAO.authenticateUser(email.trim().toLowerCase(), password);

        if (user != null) {
            System.out.println("✅ Legacy user authenticated successfully: " + user.getName() +
                    " (" + user.getUserTypeDisplay() + ") - " + COMPANY_NAME);
            return user;
        } else {
            System.out.println("❌ Authentication failed for: " + email);
            return null;
        }
    }

    // ✅ NEW: Secure BCrypt authentication method
    @Override
    public User authenticateUserWithHashedPassword(String email, String password) {
        System.out.println("🔐 Secure BCrypt authentication for user: " + email);

        // Input validation
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.err.println("❌ Invalid login credentials provided");
            return null;
        }

        try {
            // Get user by email first
            User user = userDAO.getUserByEmail(email.trim().toLowerCase());

            if (user == null) {
                System.out.println("❌ User not found with email: " + email);
                return null;
            }

            // Verify password using BCrypt
            String storedHash = user.getPassword();

            // Check if stored password is already a BCrypt hash
            if (PasswordUtil.isBCryptHash(storedHash)) {
                // Verify using BCrypt
                boolean passwordMatches = PasswordUtil.verifyPassword(password, storedHash);

                if (passwordMatches) {
                    System.out.println("✅ BCrypt authentication successful for: " + user.getName() +
                            " (" + user.getUserTypeDisplay() + ") - " + COMPANY_NAME);
                    return user;
                } else {
                    System.out.println("❌ BCrypt authentication failed - password mismatch for: " + email);
                    return null;
                }
            } else {
                // Legacy plain text password - migrate to BCrypt
                if (storedHash.equals(password)) {
                    System.out.println("⚠️ Legacy password authentication successful - migrating to BCrypt for: " + user.getName());

                    // Automatically upgrade to BCrypt
                    String hashedPassword = PasswordUtil.hashPassword(password);
                    userDAO.updateUserPassword(user.getUserId(), hashedPassword);

                    System.out.println("✅ Password migrated to BCrypt for user: " + user.getName());
                    return user;
                } else {
                    System.out.println("❌ Legacy authentication failed for: " + email);
                    return null;
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error during BCrypt authentication: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean registerUser(User user) {
        System.out.println("📝 Registering new user with secure password: " + user.getEmail() + " for " + COMPANY_NAME);

        // Input validation
        if (!isValidUserData(user)) {
            System.err.println("❌ Invalid user data for registration");
            return false;
        }

        // Check if email already exists
        if (!isEmailAvailable(user.getEmail())) {
            System.err.println("❌ Email already exists: " + user.getEmail());
            return false;
        }

        // ✅ SECURITY ENHANCEMENT: Hash password if not already hashed
        String password = user.getPassword();
        if (!PasswordUtil.isBCryptHash(password)) {
            try {
                String hashedPassword = PasswordUtil.hashPassword(password);
                user.setPassword(hashedPassword);
                System.out.println("🔐 Password hashed successfully for registration: " + user.getName());
            } catch (Exception e) {
                System.err.println("❌ Password hashing failed during registration: " + e.getMessage());
                return false;
            }
        }

        // ✅ SINGLE COMPANY: Set default values for new user
        if (user.getUserType() == null) {
            user.setUserType(User.UserType.NORMAL);
        }

        if (user.getStatus() == null) {
            user.setStatus(User.Status.ACTIVE);
        }

        // ✅ SINGLE COMPANY: Always set to default company
        user.setDefaultCompanyId(DEFAULT_COMPANY_ID);

        // Process email to lowercase for consistency
        user.setEmail(user.getEmail().trim().toLowerCase());

        boolean success = userDAO.createUser(user);

        if (success) {
            System.out.println("✅ User registered successfully with secure password: " + user.getName() +
                    " (ID: " + user.getUserId() + ", Company: " + COMPANY_NAME + ")");
        } else {
            System.err.println("❌ User registration failed for: " + user.getEmail());
        }

        return success;
    }

    @Override
    public boolean isEmailAvailable(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        boolean exists = userDAO.emailExists(email.trim().toLowerCase());
        System.out.println("📧 Email availability check for " + email + ": " + (exists ? "TAKEN" : "AVAILABLE"));
        return !exists;
    }

    @Override
    public User getUserById(int userId) {
        System.out.println("🔍 Fetching user by ID: " + userId);

        if (userId <= 0) {
            System.err.println("❌ Invalid user ID provided: " + userId);
            return null;
        }

        User user = userDAO.getUserById(userId);

        if (user != null) {
            System.out.println("✅ User found: " + user.getName() + " (" + COMPANY_NAME + ")");
        } else {
            System.out.println("❌ User not found with ID: " + userId);
        }

        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        System.out.println("🔍 Fetching user by email: " + email);

        if (email == null || email.trim().isEmpty()) {
            System.err.println("❌ Invalid email provided");
            return null;
        }

        return userDAO.getUserByEmail(email.trim().toLowerCase());
    }

    @Override
    public List<User> getAllUsers() {
        System.out.println("📋 Fetching all users for " + COMPANY_NAME);
        return userDAO.getAllUsers();
    }

    @Override
    public List<User> getActiveUsers() {
        System.out.println("📋 Fetching active users for " + COMPANY_NAME);
        return userDAO.getActiveUsers();
    }

    @Override
    public boolean updateUserProfile(User user) {
        System.out.println("✏️ Updating user profile: " + user.getUserId());

        if (!isValidUserData(user) || user.getUserId() <= 0) {
            System.err.println("❌ Invalid user data for update");
            return false;
        }

        // ✅ SINGLE COMPANY: Ensure user belongs to default company
        if (user.getDefaultCompanyId() != DEFAULT_COMPANY_ID) {
            user.setDefaultCompanyId(DEFAULT_COMPANY_ID);
            System.out.println("⚠️ Company ID corrected to " + DEFAULT_COMPANY_ID + " for user: " + user.getName());
        }

        boolean success = userDAO.updateUser(user);

        if (success) {
            System.out.println("✅ User profile updated successfully: " + user.getName() + " (" + COMPANY_NAME + ")");
        } else {
            System.err.println("❌ User profile update failed for ID: " + user.getUserId());
        }

        return success;
    }

    // ✅ LEGACY: Keep old method for backward compatibility
    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        System.out.println("🔑 Legacy password change for user: " + userId + " (Consider using BCrypt method)");

        // Enhanced input validation
        if (userId <= 0 || oldPassword == null || newPassword == null ||
                newPassword.length() < MIN_PASSWORD_LENGTH) {
            System.err.println("❌ Invalid password change request - Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
            return false;
        }

        // Get current user
        User user = userDAO.getUserById(userId);
        if (user == null) {
            System.err.println("❌ User not found for password change: " + userId);
            return false;
        }

        // Verify old password
        if (!user.getPassword().equals(oldPassword)) {
            System.err.println("❌ Old password verification failed for user: " + userId);
            return false;
        }

        // Update password
        boolean success = userDAO.updateUserPassword(userId, newPassword);

        if (success) {
            System.out.println("✅ Password changed successfully for user: " + userId);
        } else {
            System.err.println("❌ Password change failed for user: " + userId);
        }

        return success;
    }

    // ✅ NEW: Secure password change with BCrypt
    @Override
    public boolean changePasswordWithHash(int userId, String oldPassword, String newPassword) {
        System.out.println("🔑 Secure password change with BCrypt for user: " + userId);

        // Enhanced input validation
        if (userId <= 0 || oldPassword == null || newPassword == null ||
                newPassword.length() < MIN_PASSWORD_LENGTH) {
            System.err.println("❌ Invalid password change request - Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
            return false;
        }

        // Enhanced password strength validation
        if (!PasswordUtil.isStrongPassword(newPassword)) {
            System.err.println("❌ New password does not meet strength requirements");
            return false;
        }

        try {
            // Get current user
            User user = userDAO.getUserById(userId);
            if (user == null) {
                System.err.println("❌ User not found for password change: " + userId);
                return false;
            }

            String currentHashedPassword = user.getPassword();

            // Verify old password
            boolean oldPasswordValid = false;
            if (PasswordUtil.isBCryptHash(currentHashedPassword)) {
                // Verify using BCrypt
                oldPasswordValid = PasswordUtil.verifyPassword(oldPassword, currentHashedPassword);
            } else {
                // Legacy plain text verification
                oldPasswordValid = currentHashedPassword.equals(oldPassword);
            }

            if (!oldPasswordValid) {
                System.err.println("❌ Old password verification failed for user: " + userId);
                return false;
            }

            // Hash new password
            String newHashedPassword = PasswordUtil.hashPassword(newPassword);

            // Update password in database
            boolean success = userDAO.updateUserPassword(userId, newHashedPassword);

            if (success) {
                System.out.println("✅ Secure password changed successfully for user: " + userId);
            } else {
                System.err.println("❌ Secure password change failed for user: " + userId);
            }

            return success;

        } catch (Exception e) {
            System.err.println("❌ Error during secure password change: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean promoteToVIP(int userId) {
        System.out.println("⭐ Promoting user to VIP: " + userId);
        return updateUserType(userId, User.UserType.VIP);
    }

    @Override
    public boolean promoteToAdmin(int userId) {
        System.out.println("👨‍💼 Promoting user to Admin: " + userId);
        return updateUserType(userId, User.UserType.ADMIN);
    }

    @Override
    public boolean updateUserType(int userId, User.UserType newType) {
        System.out.println("🔄 Updating user type to " + newType + " for user: " + userId);

        if (userId <= 0 || newType == null) {
            System.err.println("❌ Invalid parameters for user type update");
            return false;
        }

        boolean success = userDAO.updateUserType(userId, newType);

        if (success) {
            System.out.println("✅ User type updated successfully: " + userId + " to " + newType);
        } else {
            System.err.println("❌ User type update failed for user: " + userId);
        }

        return success;
    }

    @Override
    public boolean activateUser(int userId) {
        System.out.println("🔓 Activating user: " + userId);
        boolean success = userDAO.activateUser(userId);
        if (success) {
            System.out.println("✅ User activated successfully: " + userId);
        }
        return success;
    }

    @Override
    public boolean deactivateUser(int userId) {
        System.out.println("🔒 Deactivating user: " + userId);
        boolean success = userDAO.deactivateUser(userId);
        if (success) {
            System.out.println("✅ User deactivated successfully: " + userId);
        }
        return success;
    }

    @Override
    public int getUserBookingCount(int userId) {
        System.out.println("📊 Getting booking count for user: " + userId);
        return userDAO.getUserBookingCount(userId);
    }

    @Override
    public List<User> getVIPUsers() {
        System.out.println("⭐ Fetching all VIP users for " + COMPANY_NAME);
        return userDAO.getVIPUsers();
    }

    @Override
    public List<User> getAdminUsers() {
        System.out.println("👨‍💼 Fetching all admin users for " + COMPANY_NAME);
        return userDAO.getAdminUsers();
    }

    @Override
    public boolean hasAdminPrivileges(User user) {
        if (user == null) return false;

        boolean hasPrivileges = user.getUserType() == User.UserType.ADMIN ||
                user.getUserType() == User.UserType.SUPER_ADMIN;

        System.out.println("🔐 Admin privilege check for " + user.getName() + ": " + hasPrivileges);
        return hasPrivileges;
    }

    @Override
    public boolean canAccessVIPCabins(User user) {
        if (user == null) return false;

        boolean canAccess = user.getUserType() == User.UserType.VIP ||
                user.getUserType() == User.UserType.ADMIN ||
                user.getUserType() == User.UserType.SUPER_ADMIN;

        System.out.println("⭐ VIP cabin access check for " + user.getName() + ": " + canAccess);
        return canAccess;
    }

    @Override
    public int getTotalUserCount() {
        System.out.println("📊 Getting total user count for " + COMPANY_NAME);
        return userDAO.getTotalUserCount();
    }

    @Override
    public int getActiveUserCount() {
        System.out.println("📊 Getting active user count for " + COMPANY_NAME);
        return userDAO.getActiveUserCount();
    }

    @Override
    public List<User> getAllUsersForAdmin() {
        System.out.println("📋 Fetching all users for admin dashboard");
        return userDAO.getAllUsersForAdmin();
    }

    @Override
    public boolean isUserActive(int userId) {
        System.out.println("🔍 Checking if user is active: " + userId);
        return userDAO.isUserActive(userId);
    }

    @Override
    public List<User> getUsersByType(User.UserType userType) {
        System.out.println("📋 Fetching users by type: " + userType);

        if (userType == null) {
            System.err.println("❌ User type cannot be null");
            return new ArrayList<>();
        }

        return userDAO.getActiveUsersByType(userType);
    }

    @Override
    public boolean validateUserPermissions(User user, String permission) {
        if (user == null || permission == null) {
            return false;
        }

        System.out.println("🔐 Validating permission '" + permission + "' for user: " + user.getName());

        switch (permission.toLowerCase()) {
            case "admin":
                return hasAdminPrivileges(user);
            case "vip_cabins":
                return canAccessVIPCabins(user);
            case "booking":
                return user.isActive();
            case "manage_cabins":
                return user.isAdmin();
            case "approve_bookings":
                return user.isAdmin();
            default:
                System.err.println("❌ Unknown permission: " + permission);
                return false;
        }
    }

    @Override
    public boolean demoteUser(int userId) {
        try {
            System.out.println("📉 Demoting user to NORMAL: " + userId);

            // Update user type to NORMAL in database
            String sql = "UPDATE users SET user_type = 'NORMAL' WHERE user_id = ?";

            Connection connection = DbUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            int rowsAffected = statement.executeUpdate();

            statement.close();
            connection.close();

            if (rowsAffected > 0) {
                System.out.println("✅ User demoted successfully: " + userId);
                return true;
            } else {
                System.err.println("❌ User demotion failed: " + userId);
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ Error demoting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getBookingCountByUserId(int userId) {
        try {
            System.out.println("📊 Getting booking count for user: " + userId);

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

            System.out.println("📊 Booking count for user " + userId + ": " + count);
            return count;

        } catch (Exception e) {
            System.err.println("❌ Error getting booking count: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    // ================================
    // PRIVATE UTILITY METHODS
    // ================================

    private boolean isValidUserData(User user) {
        if (user == null) {
            System.err.println("❌ User object is null");
            return false;
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            System.err.println("❌ User name is required");
            return false;
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            System.err.println("❌ User email is required");
            return false;
        }

        if (!isValidEmail(user.getEmail())) {
            System.err.println("❌ Invalid email format: " + user.getEmail());
            return false;
        }

        if (user.getPassword() == null || user.getPassword().length() < MIN_PASSWORD_LENGTH) {
            System.err.println("❌ Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Enhanced email validation
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }
}
