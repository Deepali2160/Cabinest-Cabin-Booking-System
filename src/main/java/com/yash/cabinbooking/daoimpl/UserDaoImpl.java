package com.yash.cabinbooking.daoimpl;

import com.yash.cabinbooking.dao.UserDao;
import com.yash.cabinbooking.model.User;
import com.yash.cabinbooking.util.DbUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * USER DAO IMPLEMENTATION - SINGLE COMPANY VERSION
 *
 * Modified for Yash Technology single company usage
 * - Enhanced error handling with safe enum conversion
 * - Single company focused operations
 * - Improved logging and resource management
 */
public class UserDaoImpl implements UserDao {

    @Override
    public User authenticateUser(String email, String password) {
        String sql = "SELECT user_id, name, email, password, user_type, default_company_id, status, created_at " +
                "FROM users WHERE email = ? AND password = ? AND status = 'ACTIVE'";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) {
                System.err.println("❌ Database connection failed in authenticateUser");
                return null;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                System.out.println("✅ User authenticated successfully: " + email + " (Type: " + user.getUserType() + ")");
                return user;
            } else {
                System.out.println("❌ Authentication failed for email: " + email);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL Error in authenticateUser: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("🔍 Email check for " + email + ": " + (count > 0 ? "EXISTS" : "AVAILABLE"));
                return count > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return false;
    }

    @Override
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (name, email, password, user_type, default_company_id, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) {
                System.err.println("❌ Database connection failed in createUser");
                return false;
            }

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getUserType().name());
            pstmt.setInt(5, user.getDefaultCompanyId());
            pstmt.setString(6, user.getStatus().name());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }

                System.out.println("✅ User created successfully: " + user.getEmail() +
                        " (ID: " + user.getUserId() + ", Type: " + user.getUserType() + ")");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL Error in createUser: " + e.getMessage());
            if (e.getErrorCode() == 1062) { // Duplicate key error
                System.err.println("🔄 Email already exists: " + user.getEmail());
            }
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT user_id, name, email, password, user_type, default_company_id, status, created_at " +
                "FROM users WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return null;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                System.out.println("✅ User found by ID: " + userId + " (" + user.getName() + ")");
                return user;
            } else {
                System.out.println("❌ User not found with ID: " + userId);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT user_id, name, email, password, user_type, default_company_id, status, created_at " +
                "FROM users WHERE email = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return null;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                System.out.println("✅ User found by email: " + email);
                return user;
            } else {
                System.out.println("❌ User not found with email: " + email);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting user by email: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return null;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT user_id, name, email, password, user_type, default_company_id, status, created_at " +
                "FROM users ORDER BY created_at DESC";

        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return users;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            System.out.println("✅ Retrieved " + users.size() + " users from database");

        } catch (SQLException e) {
            System.err.println("❌ Error getting all users: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return users;
    }

    // ✅ NEW: Get active users (single company)
    @Override
    public List<User> getActiveUsers() {
        String sql = "SELECT user_id, name, email, password, user_type, default_company_id, status, created_at " +
                "FROM users WHERE status = 'ACTIVE' ORDER BY name";

        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return users;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            System.out.println("✅ Retrieved " + users.size() + " active users");

        } catch (SQLException e) {
            System.err.println("❌ Error getting active users: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return users;
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, user_type = ?, default_company_id = ?, status = ? " +
                "WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getUserType().name());
            pstmt.setInt(4, user.getDefaultCompanyId());
            pstmt.setString(5, user.getStatus().name());
            pstmt.setInt(6, user.getUserId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ User updated successfully: " + user.getEmail() +
                        " (Type: " + user.getUserType() + ")");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    @Override
    public boolean deleteUser(int userId) {
        // Soft delete - mark as inactive
        String sql = "UPDATE users SET status = 'INACTIVE' WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ User deactivated successfully: " + userId);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error deleting user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    @Override
    public boolean updateUserPassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected > 0 ? "✅ Password updated for user: " + userId :
                    "❌ Password update failed for user: " + userId);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating password: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    @Override
    public boolean activateUser(int userId) {
        return updateUserStatus(userId, "ACTIVE");
    }

    @Override
    public boolean deactivateUser(int userId) {
        return updateUserStatus(userId, "INACTIVE");
    }

    @Override
    public List<User> getVIPUsers() {
        return getActiveUsersByType(User.UserType.VIP);
    }

    // ✅ NEW: Get admin users
    @Override
    public List<User> getAdminUsers() {
        String sql = "SELECT user_id, name, email, password, user_type, default_company_id, status, created_at " +
                "FROM users WHERE user_type IN ('ADMIN', 'SUPER_ADMIN') AND status = 'ACTIVE'";

        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return users;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            System.out.println("✅ Retrieved " + users.size() + " admin users");

        } catch (SQLException e) {
            System.err.println("❌ Error getting admin users: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return users;
    }

    @Override
    public List<User> getActiveUsersByType(User.UserType userType) {
        String sql = "SELECT user_id, name, email, password, user_type, default_company_id, status, created_at " +
                "FROM users WHERE user_type = ? AND status = 'ACTIVE'";

        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return users;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userType.name());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            System.out.println("✅ Retrieved " + users.size() + " " + userType + " users");

        } catch (SQLException e) {
            System.err.println("❌ Error getting users by type: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return users;
    }

    @Override
    public int getUserBookingCount(int userId) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return 0;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("📊 User " + userId + " has " + count + " bookings");
                return count;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting user booking count: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return 0;
    }

    // ✅ NEW: Get total user count
    @Override
    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) FROM users";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return 0;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("📊 Total users: " + count);
                return count;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting total user count: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return 0;
    }

    // ✅ NEW: Get active user count
    @Override
    public int getActiveUserCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE status = 'ACTIVE'";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return 0;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("📊 Active users: " + count);
                return count;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting active user count: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return 0;
    }

    // ✅ NEW: Get all users for admin dashboard
    @Override
    public List<User> getAllUsersForAdmin() {
        String sql = "SELECT user_id, name, email, password, user_type, default_company_id, status, created_at " +
                "FROM users ORDER BY user_type, created_at DESC";

        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return users;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            System.out.println("✅ Retrieved " + users.size() + " users for admin dashboard");

        } catch (SQLException e) {
            System.err.println("❌ Error getting users for admin: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return users;
    }

    // ✅ NEW: Update user type
    @Override
    public boolean updateUserType(int userId, User.UserType newType) {
        String sql = "UPDATE users SET user_type = ? WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newType.name());
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ User type updated: " + userId + " to " + newType);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating user type: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    // ✅ NEW: Check if user is active
    @Override
    public boolean isUserActive(int userId) {
        String sql = "SELECT status FROM users WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                return "ACTIVE".equals(status);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error checking user status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return false;
    }

    // ================================
    // PRIVATE UTILITY METHODS
    // ================================

    private boolean updateUserStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ User " + userId + " status updated to: " + status);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating user status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    // ✅ ENHANCED: Safe enum conversion with fallbacks
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));

        // ✅ SAFE: Enum conversion with fallback
        String userTypeStr = rs.getString("user_type");
        user.setUserType(User.userTypeFromString(userTypeStr));

        user.setDefaultCompanyId(rs.getInt("default_company_id"));

        // ✅ SAFE: Status conversion with fallback
        String statusStr = rs.getString("status");
        user.setStatus(User.statusFromString(statusStr));

        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }

}
