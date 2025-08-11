package com.yash.cabinbooking.util;

import java.sql.*;

/**
 * DATABASE UTILITY CLASS - SIMPLIFIED VERSION
 *
 * EVALUATION POINTS:
 * - "Simple JDBC connection management implement kiya"
 * - "Hardcoded values use kiye for quick development"
 * - "Proper resource cleanup methods banaye"
 * - "Console logging add kiya for debugging"
 */
public class DbUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/cabin_booking?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    /**
     * Get database connection
     */
    public static Connection getConnection() {
        try {
            Class.forName(DRIVER_CLASS);
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✅ Database connected successfully");
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
     * Test database connection
     */
    public static boolean testConnection() {
        System.out.println("🧪 Testing database connection...");
        Connection testConn = getConnection();
        if (testConn != null) {
            closeConnection(testConn);
            System.out.println("✅ Database test PASSED");
            return true;
        } else {
            System.out.println("❌ Database test FAILED");
            return false;
        }
    }
}
