package com.yash.cabinbooking.service;

import com.yash.cabinbooking.model.User;
import java.util.List;

/**
 * USER SERVICE INTERFACE
 *
 * EVALUATION EXPLANATION:
 * - Business logic layer for user management
 * - Authentication and authorization services
 * - User registration and profile management
 * - VIP and admin privilege handling
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
    List<User> getUsersByCompany(int companyId);
    boolean updateUserProfile(User user);
    boolean changePassword(int userId, String oldPassword, String newPassword);

    // User privileges and status
    boolean promoteToVIP(int userId);
    boolean promoteToAdmin(int userId);
    boolean activateUser(int userId);
    boolean deactivateUser(int userId);

    // Business analytics
    int getUserBookingCount(int userId);
    List<User> getVIPUsers();
    boolean hasAdminPrivileges(User user);
    boolean canAccessVIPCabins(User user);
}
