package com.yash.cabinbooking.dao;

import com.yash.cabinbooking.model.User;
import java.util.List;

/**
 * USER DAO INTERFACE - SINGLE COMPANY VERSION
 *
 * Modified for Yash Technology single company usage
 * - Removed multi-company complexity
 * - Simplified for single organization operations
 * - Enhanced for single company user management
 */
public interface UserDao {

    // Authentication operations
    User authenticateUser(String email, String password);
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

    // Business specific operations
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

    // ✅ NEW: Single company utility methods
    List<User> getAllUsersForAdmin();
    boolean updateUserType(int userId, User.UserType newType);
    boolean isUserActive(int userId);
}
