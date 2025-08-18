package com.yash.cabinbooking.util;

public class DatabaseTester {

    public static void main(String[] args) {
        System.out.println("🚀 Starting Database Connection Test...");
        System.out.println("==========================================");

        // Test connection
        boolean success = DbUtil.testConnection();

        if (success) {
            System.out.println("\n🎉 SUCCESS! Database setup is working!");
            System.out.println("✅ You can now proceed with model creation");
        } else {
            System.out.println("\n❌ FAILED! Please check:");
            System.out.println("1. MySQL server is running");
            System.out.println("2. Database 'cabin_booking' exists");
            System.out.println("3. Username/password in DbUtil.java are correct");
        }

        System.out.println("==========================================");
    }
}
