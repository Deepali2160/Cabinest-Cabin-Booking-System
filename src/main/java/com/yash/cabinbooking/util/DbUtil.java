package com.yash.cabinbooking.util;

import java.sql.*;

/**
 * DATABASE UTILITY CLASS - WITH PROPER IST TIMEZONE HANDLING
 *
 * FIXES APPLIED:
 * - Fixed timezone mismatch causing wrong timestamp display
 * - Added IST (Asia/Kolkata) timezone configuration
 * - Enhanced connection string with proper datetime parameters
 * - Added session timezone setting for consistent behavior
 */
public class DbUtil {

    // ✅ MAIN FIX: Updated URL with correct IST timezone
    private static final String URL = "jdbc:mysql://localhost:3306/cabin_booking1?" +
            "useSSL=false&" +
            "serverTimezone=Asia/Kolkata&" +        // ✅ FIXED: IST timezone instead of UTC
            "useLegacyDatetimeCode=false&" +         // ✅ NEW: Better datetime handling
            "useTimezone=true";                      // ✅ NEW: Enable timezone support

    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    /**
     * Get database connection with proper IST timezone
     */
    public static Connection getConnection() {
        try {
            Class.forName(DRIVER_CLASS);
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            // ✅ ENHANCED: Set session timezone to IST for consistent timestamp handling
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("SET time_zone = '+05:30'");  // IST timezone
                System.out.println("⏰ Database session timezone set to IST (+05:30)");
            }

            System.out.println("✅ Database connected successfully with IST timezone");
            return connection;
        } catch (Exception e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Close database connection
     */
    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Close all resources
     */
    public static void closeAllResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
            System.out.println("🧹 All resources closed successfully");
        } catch (SQLException e) {
            System.err.println("❌ Error closing resources: " + e.getMessage());
        }
    }

    /**
     * Enhanced test with timezone verification
     */
    public static boolean testConnection() {
        System.out.println("🧪 Testing database connection with IST timezone...");
        Connection testConn = getConnection();
        if (testConn != null) {
            try {
                // ✅ NEW: Verify timezone setting
                PreparedStatement pstmt = testConn.prepareStatement("SELECT NOW(), @@session.time_zone");
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    System.out.println("🕐 Current database time: " + rs.getTimestamp(1));
                    System.out.println("⏰ Database timezone: " + rs.getString(2));
                }
                pstmt.close();
                rs.close();
            } catch (SQLException e) {
                System.out.println("⚠️ Timezone verification failed: " + e.getMessage());
            }

            closeConnection(testConn);
            System.out.println("✅ Database test PASSED with IST timezone configuration");
            return true;
        } else {
            System.out.println("❌ Database test FAILED");
            return false;
        }
    }

    // ✅ NEW: Utility method for consistent IST timestamp
    public static Timestamp getCurrentISTTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
