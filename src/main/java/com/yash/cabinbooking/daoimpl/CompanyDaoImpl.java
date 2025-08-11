package com.yash.cabinbooking.daoimpl;

import com.yash.cabinbooking.dao.CompanyDao;
import com.yash.cabinbooking.model.Company;
import com.yash.cabinbooking.util.DbUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * COMPANY DAO IMPLEMENTATION
 *
 * EVALUATION EXPLANATION:
 * - Company management ke liye complete CRUD operations
 * - Multi-company support for cabin booking system
 * - Status-based filtering for active companies
 * - Business metrics like cabin count tracking
 */
public class CompanyDaoImpl implements CompanyDao {

    @Override
    public boolean createCompany(Company company) {
        String sql = "INSERT INTO companies (name, location, contact_info, status) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) {
                System.err.println("❌ Database connection failed in createCompany");
                return false;
            }

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, company.getName());
            pstmt.setString(2, company.getLocation());
            pstmt.setString(3, company.getContactInfo());
            pstmt.setString(4, company.getStatus().name());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    company.setCompanyId(generatedKeys.getInt(1));
                }
                System.out.println("✅ Company created successfully: " + company.getName() + " (ID: " + company.getCompanyId() + ")");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL Error in createCompany: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    @Override
    public Company getCompanyById(int companyId) {
        String sql = "SELECT company_id, name, location, contact_info, status, created_at FROM companies WHERE company_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return null;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, companyId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Company company = mapResultSetToCompany(rs);
                System.out.println("✅ Company found: " + company.getName() + " (ID: " + companyId + ")");
                return company;
            } else {
                System.out.println("❌ Company not found with ID: " + companyId);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting company by ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return null;
    }

    @Override
    public Company getCompanyByName(String name) {
        String sql = "SELECT company_id, name, location, contact_info, status, created_at FROM companies WHERE name = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return null;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Company company = mapResultSetToCompany(rs);
                System.out.println("✅ Company found by name: " + name);
                return company;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting company by name: " + e.getMessage());
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return null;
    }

    @Override
    public List<Company> getAllCompanies() {
        String sql = "SELECT company_id, name, location, contact_info, status, created_at FROM companies ORDER BY name";

        List<Company> companies = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return companies;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                companies.add(mapResultSetToCompany(rs));
            }

            System.out.println("✅ Retrieved " + companies.size() + " companies from database");

        } catch (SQLException e) {
            System.err.println("❌ Error getting all companies: " + e.getMessage());
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return companies;
    }

    @Override
    public List<Company> getActiveCompanies() {
        String sql = "SELECT company_id, name, location, contact_info, status, created_at FROM companies WHERE status = 'ACTIVE' ORDER BY name";

        List<Company> companies = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return companies;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                companies.add(mapResultSetToCompany(rs));
            }

            System.out.println("✅ Retrieved " + companies.size() + " active companies");

        } catch (SQLException e) {
            System.err.println("❌ Error getting active companies: " + e.getMessage());
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return companies;
    }

    @Override
    public boolean updateCompany(Company company) {
        String sql = "UPDATE companies SET name = ?, location = ?, contact_info = ?, status = ? WHERE company_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, company.getName());
            pstmt.setString(2, company.getLocation());
            pstmt.setString(3, company.getContactInfo());
            pstmt.setString(4, company.getStatus().name());
            pstmt.setInt(5, company.getCompanyId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Company updated successfully: " + company.getName());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating company: " + e.getMessage());
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    @Override
    public boolean deleteCompany(int companyId) {
        String sql = "UPDATE companies SET status = 'INACTIVE' WHERE company_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, companyId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Company deactivated successfully: " + companyId);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error deleting company: " + e.getMessage());
        } finally {
            DbUtil.closeAllResources(conn, pstmt, null);
        }

        return false;
    }

    @Override
    public boolean activateCompany(int companyId) {
        return updateCompanyStatus(companyId, "ACTIVE");
    }

    @Override
    public boolean deactivateCompany(int companyId) {
        return updateCompanyStatus(companyId, "INACTIVE");
    }

    @Override
    public int getCompanyCabinCount(int companyId) {
        String sql = "SELECT COUNT(*) FROM cabins WHERE company_id = ? AND status = 'ACTIVE'";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return 0;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, companyId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("📊 Company " + companyId + " has " + count + " active cabins");
                return count;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting company cabin count: " + e.getMessage());
        } finally {
            DbUtil.closeAllResources(conn, pstmt, rs);
        }

        return 0;
    }

    // PRIVATE UTILITY METHODS

    private boolean updateCompanyStatus(int companyId, String status) {
        String sql = "UPDATE companies SET status = ? WHERE company_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DbUtil.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, companyId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Company " + companyId + " status updated to: " + status);
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
        company.setCompanyId(rs.getInt("company_id"));
        company.setName(rs.getString("name"));
        company.setLocation(rs.getString("location"));
        company.setContactInfo(rs.getString("contact_info"));
        company.setStatus(Company.Status.valueOf(rs.getString("status")));
        company.setCreatedAt(rs.getTimestamp("created_at"));
        return company;
    }
}
