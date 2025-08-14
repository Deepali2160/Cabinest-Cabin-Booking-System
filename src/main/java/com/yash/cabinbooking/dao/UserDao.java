package com.yash.cabinbooking.dao;

import com.yash.cabinbooking.model.User;
import java.util.List;

/**
 * USER DAO INTERFACE - SINGLE COMPANY VERSION WITH SECURE PASSWORD SUPPORT
 *
 * Enhanced Features:
 * - BCrypt password support
 * - Secure authentication methods
 * - Single company operations for Yash Technology
 */
public interface UserDao {

    // ✅ ENHANCED: Authentication operations with BCrypt support
    User authenticateUser(String email, String password);
    User getUserByEmailForAuth(String email); // ✅ NEW: For BCrypt authentication
    boolean emailExists(String email);

    // CRUD operations
    boolean createUser(User user);
    User getUserById(int userId);
    User getUserByEmail(String email);
    List<User> getAllUsers();

    // ✅ SIMPLIFIED: Single company users (no company filter needed)
    List<User> getActiveUsers();

    boolean updateUser(User user);
    boolean deleteUser(int userId);

    // ✅ ENHANCED: Password operations
    boolean updateUserPassword(int userId, String newPassword);
    boolean activateUser(int userId);
    boolean deactivateUser(int userId);

    // ✅ SINGLE COMPANY: Enhanced user type operations
    List<User> getVIPUsers();
    List<User> getActiveUsersByType(User.UserType userType);
    List<User> getAdminUsers();

    // Analytics operations
    int getUserBookingCount(int userId);
    int getTotalUserCount();
    int getActiveUserCount();

    // ✅ Single company utility methods
    List<User> getAllUsersForAdmin();
    boolean updateUserType(int userId, User.UserType newType);
    boolean isUserActive(int userId);
}
