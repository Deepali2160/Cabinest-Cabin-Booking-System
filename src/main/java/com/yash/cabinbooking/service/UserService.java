package com.yash.cabinbooking.service;

import com.yash.cabinbooking.model.User;
import java.util.List;

/**
 * USER SERVICE INTERFACE - SINGLE COMPANY VERSION
 *
 * Modified for Yash Technology single company usage
 * - Removed multi-company complexity
 * - Enhanced for single organization operations
 * - Added AdminController support methods
 */
public interface UserService {

    // Authentication services
    User authenticateUser(String email, String password);
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

    // ✅ NEW: AdminController support methods
    int getTotalUserCount();
    int getActiveUserCount();
    List<User> getAllUsersForAdmin();
    boolean isUserActive(int userId);

    // ✅ NEW: Enhanced user type management
    List<User> getUsersByType(User.UserType userType);
    boolean validateUserPermissions(User user, String permission);
    // ✅ ADD THESE NEW METHODS

    boolean demoteUser(int userId);
    int getBookingCountByUserId(int userId); // Optional - for user booking counts
}
