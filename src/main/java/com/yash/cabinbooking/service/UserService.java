package com.yash.cabinbooking.service;

import com.yash.cabinbooking.model.User;
import java.util.List;

/**
 * USER SERVICE INTERFACE - SINGLE COMPANY VERSION WITH SECURE AUTHENTICATION
 *
 * Enhanced Features:
 * - BCrypt password hashing support
 * - Secure authentication methods
 * - Single company operations for Yash Technology
 * - AdminController support methods
 */
public interface UserService {

    // ✅ ENHANCED: Authentication services with BCrypt support
    User authenticateUser(String email, String password);
    User authenticateUserWithHashedPassword(String email, String password); // ✅ NEW METHOD
    boolean registerUser(User user);
    boolean isEmailAvailable(String email);

    // User management
    User getUserById(int userId);
    User getUserByEmail(String email);
    List<User> getAllUsers();

    // ✅ SINGLE COMPANY: Simplified user retrieval
    List<User> getActiveUsers();

    boolean updateUserProfile(User user);
    boolean changePassword(int userId, String oldPassword, String newPassword);
    boolean changePasswordWithHash(int userId, String oldPassword, String newPassword); // ✅ NEW METHOD

    // User privileges and status
    boolean promoteToVIP(int userId);
    boolean promoteToAdmin(int userId);
    boolean updateUserType(int userId, User.UserType newType);
    boolean activateUser(int userId);
    boolean deactivateUser(int userId);

    // Business analytics
    int getUserBookingCount(int userId);
    List<User> getVIPUsers();
    List<User> getAdminUsers();
    boolean hasAdminPrivileges(User user);
    boolean canAccessVIPCabins(User user);

    // ✅ AdminController support methods
    int getTotalUserCount();
    int getActiveUserCount();
    List<User> getAllUsersForAdmin();
    boolean isUserActive(int userId);

    // ✅ Enhanced user type management
    List<User> getUsersByType(User.UserType userType);
    boolean validateUserPermissions(User user, String permission);
    boolean demoteUser(int userId);
    int getBookingCountByUserId(int userId);
}
