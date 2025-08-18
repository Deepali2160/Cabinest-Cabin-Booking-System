package com.yash.cabinbooking.daoimpl;

import com.yash.cabinbooking.dao.CompanyDao;
import com.yash.cabinbooking.model.Company;
import com.yash.cabinbooking.util.DbUtil;
import java.sql.*;

public class CompanyDaoImpl implements CompanyDao {

    @Override
    public Company getCompanyConfig() {
        String sql = "SELECT company_name, company_location, company_contact, company_status, updated_at FROM company_config LIMIT 1";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) {
                System.err.println("❌ Database connection failed in getCompanyConfig");
                return Company.getDefaultCompany(); // Return default if DB fails
            }

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Company company = mapResultSetToCompany(rs);
                System.out.println("✅ Company config loaded: " + company.getCompanyName());
                return company;
            } else {
                System.out.println("⚠️ No company config found, returning default");
                return Company.getDefaultCompany();
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL Error in getCompanyConfig: " + e.getMessage());
            e.printStackTrace();
            return Company.getDefaultCompany(); // Return default on error
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }
    }

    @Override
    public boolean updateCompanyConfig(Company company) {
        String sql = "UPDATE company_config SET company_name = ?, company_location = ?, company_contact = ?, company_status = ?, updated_at = CURRENT_TIMESTAMP";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) {
                System.err.println("❌ Database connection failed in updateCompanyConfig");
                return false;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, company.getCompanyName());
            pstmt.setString(2, company.getCompanyLocation());
            pstmt.setString(3, company.getCompanyContact());
            pstmt.setString(4, company.getCompanyStatus().name());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Company config updated successfully: " + company.getCompanyName());
                return true;
            } else {
                System.out.println("⚠️ No company config record found to update");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL Error in updateCompanyConfig: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    @Override
    public boolean isCompanyActive() {
        String sql = "SELECT company_status FROM company_config LIMIT 1";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return true; // Default to active if DB fails

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("company_status");
                boolean isActive = "ACTIVE".equals(status);
                System.out.println("📊 Company status check: " + (isActive ? "ACTIVE" : "INACTIVE"));
                return isActive;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error checking company status: " + e.getMessage());
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return true; // Default to active
    }

    @Override
    public int getTotalCabinCount() {
        String sql = "SELECT COUNT(*) FROM cabins WHERE status = 'ACTIVE'";

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
                System.out.println("📊 Total active cabins: " + count);
                return count;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting total cabin count: " + e.getMessage());
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return 0;
    }

    @Override
    public boolean activateCompany() {
        return updateCompanyStatus("ACTIVE");
    }

    @Override
    public boolean deactivateCompany() {
        return updateCompanyStatus("INACTIVE");
    }

    // PRIVATE UTILITY METHODS

    private boolean updateCompanyStatus(String status) {
        String sql = "UPDATE company_config SET company_status = ?, updated_at = CURRENT_TIMESTAMP";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Company status updated to: " + status);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating company status: " + e.getMessage());
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        Company company = new Company();
        company.setCompanyName(rs.getString("company_name"));
        company.setCompanyLocation(rs.getString("company_location"));
        company.setCompanyContact(rs.getString("company_contact"));
        company.setCompanyStatus(Company.Status.valueOf(rs.getString("company_status")));
        company.setUpdatedAt(rs.getTimestamp("updated_at"));
        return company;
    }
}
