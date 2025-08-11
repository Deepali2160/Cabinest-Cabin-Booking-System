package com.yash.cabinbooking.dao;

import com.yash.cabinbooking.model.User;
import java.util.List;

/**
 * USER DAO INTERFACE
 *
 * EVALUATION EXPLANATION:
 * - Interface segregation principle follow kiya
 * - Contract define kiya for all user operations
 * - Implementation flexibility provide karta hai
 * - Testing ke liye mock objects easily create kar sakte hain
 *
 * INTERVIEW TALKING POINTS:
 * - "DAO pattern with interface segregation implement kiya"
 * - "Contract-based programming approach use kiya"
 * - "Future mein different implementations add kar sakte hain"
 * - "Unit testing ke liye mockable design banaya"
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
    List<User> getUsersByCompany(int companyId);
    boolean updateUser(User user);
    boolean deleteUser(int userId);

    // Business specific operations
    boolean updateUserPassword(int userId, String newPassword);
    boolean updateUserCompany(int userId, int newCompanyId);
    boolean activateUser(int userId);
    boolean deactivateUser(int userId);

    // AI related operations
    List<User> getVIPUsers();
    List<User> getActiveUsersByType(User.UserType userType);
    int getUserBookingCount(int userId);
}
