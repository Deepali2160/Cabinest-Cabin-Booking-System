package com.yash.cabinbooking.daoimpl;

import com.yash.cabinbooking.dao.CabinDao;
import com.yash.cabinbooking.model.Cabin;
import com.yash.cabinbooking.model.User;
import com.yash.cabinbooking.util.DbUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CabinDaoImpl implements CabinDao {

    // ✅ KEEP ALL YOUR EXISTING METHODS AS THEY ARE

    @Override
    public boolean createCabin(Cabin cabin) {
        String sql = "INSERT INTO cabins (company_id, name, capacity, amenities, is_vip_only, location, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, 1); // ✅ Default company_id = 1 for single company
            pstmt.setString(2, cabin.getName());
            pstmt.setInt(3, cabin.getCapacity());
            pstmt.setString(4, cabin.getAmenities());
            pstmt.setBoolean(5, cabin.isVipOnly());
            pstmt.setString(6, cabin.getLocation());
            pstmt.setString(7, cabin.getStatus().name());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    cabin.setCabinId(generatedKeys.getInt(1));
                    cabin.setCompanyId(1); // Set company_id in cabin object
                }
                System.out.println("✅ Cabin created successfully: " + cabin.getName() + " (ID: " + cabin.getCabinId() + ")");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL Error in createCabin: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    // ✅ NEW: Add cabin method for AdminController
    public boolean addCabin(Cabin cabin) {
        System.out.println("➕ Adding new cabin: " + cabin.getName());
        return createCabin(cabin); // Use existing createCabin method
    }

    @Override
    public Cabin getCabinById(int cabinId) {
        String sql = "SELECT cabin_id, company_id, name, capacity, amenities, is_vip_only, location, status, created_at FROM cabins WHERE cabin_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return null;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cabinId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Cabin cabin = mapResultSetToCabin(rs);
                System.out.println("✅ Cabin found: " + cabin.getName() + " (ID: " + cabinId + ")");
                return cabin;
            } else {
                System.out.println("❌ Cabin not found with ID: " + cabinId);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting cabin by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return null;
    }

    @Override
    public List<Cabin> getAllCabins() {
        String sql = "SELECT cabin_id, company_id, name, capacity, amenities, is_vip_only, location, status, created_at FROM cabins ORDER BY name";

        List<Cabin> cabins = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return cabins;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cabins.add(mapResultSetToCabin(rs));
            }

            System.out.println("✅ Retrieved " + cabins.size() + " cabins from database");

        } catch (SQLException e) {
            System.err.println("❌ Error getting all cabins: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return cabins;
    }

    // ✅ ADDED: Get all active cabins (fixes CompanyServiceImpl error)
    @Override
    public List<Cabin> getAllActiveCabins() {
        String sql = "SELECT cabin_id, company_id, name, capacity, amenities, is_vip_only, location, status, created_at FROM cabins WHERE status = 'ACTIVE' ORDER BY name";

        List<Cabin> cabins = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return cabins;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cabins.add(mapResultSetToCabin(rs));
            }

            System.out.println("✅ Retrieved " + cabins.size() + " active cabins");

        } catch (SQLException e) {
            System.err.println("❌ Error getting active cabins: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return cabins;
    }

    @Override
    public boolean updateCabin(Cabin cabin) {
        String sql = "UPDATE cabins SET company_id = ?, name = ?, capacity = ?, amenities = ?, is_vip_only = ?, location = ?, status = ? WHERE cabin_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, 1); // ✅ Always set company_id = 1
            pstmt.setString(2, cabin.getName());
            pstmt.setInt(3, cabin.getCapacity());
            pstmt.setString(4, cabin.getAmenities());
            pstmt.setBoolean(5, cabin.isVipOnly());
            pstmt.setString(6, cabin.getLocation());
            pstmt.setString(7, cabin.getStatus().name());
            pstmt.setInt(8, cabin.getCabinId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Cabin updated successfully: " + cabin.getName());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating cabin: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    @Override
    public boolean deleteCabin(int cabinId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            // First, check if cabin has any active bookings
            String checkSql = "SELECT COUNT(*) FROM bookings WHERE cabin_id = ? AND status IN ('PENDING', 'APPROVED')";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, cabinId);
            rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.err.println("❌ Cannot delete cabin " + cabinId + " - has " + rs.getInt(1) + " active bookings");
                return false;
            }

            // Close first statement and result set
            DbUtil.closeAllResources(null, pstmt, rs);
            pstmt = null;
            rs = null;

            // If no active bookings, proceed with hard delete
            String deleteSql = "DELETE FROM cabins WHERE cabin_id = ?";
            pstmt = conn.prepareStatement(deleteSql);
            pstmt.setInt(1, cabinId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Cabin deleted permanently from database: " + cabinId);
                return true;
            } else {
                System.err.println("❌ No cabin found with ID: " + cabinId);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error deleting cabin: " + e.getMessage());
            e.printStackTrace();

            if (e.getMessage().contains("foreign key constraint") ||
                    e.getMessage().contains("Cannot delete or update a parent row")) {
                System.err.println("⚠️ Foreign key constraint violation - cabin has related records");
            }
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return false;
    }

    // ✅ UPDATED: Available cabins without company ID parameter
    @Override
    public List<Cabin> getAvailableCabins() {
        String sql = "SELECT cabin_id, company_id, name, capacity, amenities, is_vip_only, location, status, created_at FROM cabins WHERE status = 'ACTIVE' ORDER BY name";

        List<Cabin> cabins = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return cabins;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cabins.add(mapResultSetToCabin(rs));
            }

            System.out.println("✅ Retrieved " + cabins.size() + " available cabins");

        } catch (SQLException e) {
            System.err.println("❌ Error getting available cabins: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return cabins;
    }

    // ✅ UPDATED: Accessible cabins without company ID parameter
    @Override
    public List<Cabin> getAccessibleCabins(User user) {
        String sql;

        if (user.isVIP() || user.isAdmin()) {
            // VIP and Admin users can access all cabins
            sql = "SELECT cabin_id, company_id, name, capacity, amenities, is_vip_only, location, status, created_at FROM cabins WHERE status = 'ACTIVE' ORDER BY is_vip_only DESC, name";
        } else {
            // Normal users can only access non-VIP cabins
            sql = "SELECT cabin_id, company_id, name, capacity, amenities, is_vip_only, location, status, created_at FROM cabins WHERE status = 'ACTIVE' AND is_vip_only = FALSE ORDER BY name";
        }

        List<Cabin> cabins = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return cabins;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cabins.add(mapResultSetToCabin(rs));
            }

            System.out.println("✅ Retrieved " + cabins.size() + " accessible cabins for " + user.getUserTypeDisplay() + ": " + user.getName());

        } catch (SQLException e) {
            System.err.println("❌ Error getting accessible cabins: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return cabins;
    }

    // ✅ UPDATED: VIP cabins without company ID parameter (fixes CompanyServiceImpl error)
    @Override
    public List<Cabin> getVIPOnlyCabins() {
        String sql = "SELECT cabin_id, company_id, name, capacity, amenities, is_vip_only, location, status, created_at FROM cabins WHERE is_vip_only = TRUE AND status = 'ACTIVE' ORDER BY name";

        List<Cabin> cabins = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return cabins;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cabins.add(mapResultSetToCabin(rs));
            }

            System.out.println("✅ Retrieved " + cabins.size() + " VIP-only cabins");

        } catch (SQLException e) {
            System.err.println("❌ Error getting VIP cabins: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return cabins;
    }

    @Override
    public List<Cabin> getCabinsByCapacity(int minCapacity, int maxCapacity) {
        String sql = "SELECT cabin_id, company_id, name, capacity, amenities, is_vip_only, location, status, created_at FROM cabins WHERE capacity BETWEEN ? AND ? AND status = 'ACTIVE' ORDER BY capacity, name";

        List<Cabin> cabins = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return cabins;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, minCapacity);
            pstmt.setInt(2, maxCapacity);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cabins.add(mapResultSetToCabin(rs));
            }

            System.out.println("✅ Retrieved " + cabins.size() + " cabins with capacity " + minCapacity + "-" + maxCapacity);

        } catch (SQLException e) {
            System.err.println("❌ Error getting cabins by capacity: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return cabins;
    }

    @Override
    public List<Cabin> getCabinsWithAmenities(String amenities) {
        String sql = "SELECT cabin_id, company_id, name, capacity, amenities, is_vip_only, location, status, created_at FROM cabins WHERE amenities LIKE ? AND status = 'ACTIVE' ORDER BY name";

        List<Cabin> cabins = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return cabins;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + amenities + "%");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cabins.add(mapResultSetToCabin(rs));
            }

            System.out.println("🔍 Retrieved " + cabins.size() + " cabins with amenity: " + amenities);

        } catch (SQLException e) {
            System.err.println("❌ Error getting cabins with amenities: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return cabins;
    }

    // ✅ UPDATED: Popular cabins without company ID parameter
    @Override
    public List<Cabin> getPopularCabins() {
        String sql = "SELECT c.cabin_id, c.company_id, c.name, c.capacity, c.amenities, c.is_vip_only, c.location, c.status, c.created_at, COUNT(b.booking_id) as booking_count " +
                "FROM cabins c LEFT JOIN bookings b ON c.cabin_id = b.cabin_id " +
                "WHERE c.status = 'ACTIVE' " +
                "GROUP BY c.cabin_id " +
                "ORDER BY booking_count DESC, c.name " +
                "LIMIT 5";

        List<Cabin> cabins = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return cabins;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Cabin cabin = mapResultSetToCabin(rs);
                cabins.add(cabin);
            }

            System.out.println("🌟 Retrieved " + cabins.size() + " popular cabins");

        } catch (SQLException e) {
            System.err.println("❌ Error getting popular cabins: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return cabins;
    }

    @Override
    public List<Cabin> getSimilarCabins(int cabinId) {
        String sql = "SELECT c2.cabin_id, c2.company_id, c2.name, c2.capacity, c2.amenities, c2.is_vip_only, c2.location, c2.status, c2.created_at " +
                "FROM cabins c1, cabins c2 " +
                "WHERE c1.cabin_id = ? AND c2.cabin_id != ? " +
                "AND c2.status = 'ACTIVE' " +
                "AND (c2.capacity BETWEEN c1.capacity - 2 AND c1.capacity + 2 " +
                "     OR c2.is_vip_only = c1.is_vip_only) " +
                "ORDER BY c2.name " +
                "LIMIT 3";

        List<Cabin> cabins = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return cabins;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cabinId);
            pstmt.setInt(2, cabinId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cabins.add(mapResultSetToCabin(rs));
            }

            System.out.println("🔗 Retrieved " + cabins.size() + " similar cabins for cabin: " + cabinId);

        } catch (SQLException e) {
            System.err.println("❌ Error getting similar cabins: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return cabins;
    }

    // ✅ NEW: Update cabin status method for AdminController
    @Override
    public boolean updateCabinStatus(int cabinId, Cabin.Status status) {
        String sql = "UPDATE cabins SET status = ? WHERE cabin_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status.name());
            pstmt.setInt(2, cabinId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Cabin status updated successfully: " + cabinId + " to " + status);
                return true;
            } else {
                System.err.println("❌ No cabin found with ID: " + cabinId);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating cabin status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    // ✅ NEW: Soft delete method (optional - for business logic)
    @Override
    public boolean deactivateCabin(int cabinId) {
        return updateCabinStatus(cabinId, Cabin.Status.INACTIVE);
    }

    // ✅ NEW METHODS - FOR AI RECOMMENDATION SERVICE COMPATIBILITY

    /**
     * Get cabins by company ID - AI Service compatibility
     * Always returns all cabins since we have single company
     */
    @Override
    public List<Cabin> getCabinsByCompany(int companyId) {
        System.out.println("🤖 AI Service: Getting cabins for company: " + companyId + " (Single Company Mode)");
        // For single company, return all active cabins regardless of companyId
        return getAllActiveCabins();
    }

    /**
     * Get accessible cabins with company ID - AI Service compatibility
     * Company ID is ignored in single company mode
     */
    @Override
    public List<Cabin> getAccessibleCabins(int companyId, User user) {
        System.out.println("🤖 AI Service: Getting accessible cabins for company: " + companyId + ", user: " + user.getName());
        // Delegate to existing method - company ID is ignored
        return getAccessibleCabins(user);
    }

    /**
     * Get VIP cabins with company ID - AI Service compatibility
     * Company ID is ignored in single company mode
     */
    @Override
    public List<Cabin> getVIPOnlyCabins(int companyId) {
        System.out.println("🤖 AI Service: Getting VIP cabins for company: " + companyId + " (Single Company Mode)");
        // Delegate to existing method - company ID is ignored
        return getVIPOnlyCabins();
    }

    // PRIVATE UTILITY METHODS

    private Cabin mapResultSetToCabin(ResultSet rs) throws SQLException {
        Cabin cabin = new Cabin();
        cabin.setCabinId(rs.getInt("cabin_id"));
        cabin.setCompanyId(rs.getInt("company_id"));
        cabin.setName(rs.getString("name"));
        cabin.setCapacity(rs.getInt("capacity"));
        cabin.setAmenities(rs.getString("amenities"));
        cabin.setVipOnly(rs.getBoolean("is_vip_only"));
        cabin.setLocation(rs.getString("location"));
        cabin.setStatus(Cabin.Status.valueOf(rs.getString("status")));
        cabin.setCreatedAt(rs.getTimestamp("created_at"));
        return cabin;
    }
}
