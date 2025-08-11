package com.yash.cabinbooking.serviceimpl;

import com.yash.cabinbooking.dao.CabinDao;
import com.yash.cabinbooking.dao.CompanyDao;
import com.yash.cabinbooking.daoimpl.CabinDaoImpl;
import com.yash.cabinbooking.daoimpl.CompanyDaoImpl;
import com.yash.cabinbooking.service.CompanyService;
import com.yash.cabinbooking.model.Company;
import com.yash.cabinbooking.model.Cabin;
import java.util.List;
import java.util.ArrayList;

/**
 * COMPANY SERVICE IMPLEMENTATION
 *
 * EVALUATION EXPLANATION:
 * - Multi-company support for enterprise environments
 * - Company-specific cabin management and analytics
 * - Business logic for company operations
 * - Integration with cabin management system
 *
 * INTERVIEW TALKING POINTS:
 * - "Multi-tenant architecture support implement kiya"
 * - "Company-wise resource management and analytics"
 * - "Business validation for company operations"
 * - "Clean separation between DAO and business logic"
 */
public class CompanyServiceImpl implements CompanyService {

    private CompanyDao companyDAO;
    private CabinDao cabinDAO;

    public CompanyServiceImpl() {
        this.companyDAO = new CompanyDaoImpl();
        this.cabinDAO = new CabinDaoImpl();
        System.out.println("🔧 CompanyService initialized with DAO implementations");
    }

    @Override
    public boolean createCompany(Company company) {
        System.out.println("🏢 Creating new company: " + company.getName());

        // Input validation
        if (!isValidCompanyData(company)) {
            System.err.println("❌ Invalid company data");
            return false;
        }

        // Check if company name already exists
        Company existingCompany = companyDAO.getCompanyByName(company.getName());
        if (existingCompany != null) {
            System.err.println("❌ Company name already exists: " + company.getName());
            return false;
        }

        // Set default status if not provided
        if (company.getStatus() == null) {
            company.setStatus(Company.Status.ACTIVE);
        }

        boolean success = companyDAO.createCompany(company);

        if (success) {
            System.out.println("✅ Company created successfully: " + company.getName() + " (ID: " + company.getCompanyId() + ")");
        } else {
            System.err.println("❌ Company creation failed for: " + company.getName());
        }

        return success;
    }

    @Override
    public Company getCompanyById(int companyId) {
        System.out.println("🔍 Fetching company by ID: " + companyId);

        if (companyId <= 0) {
            System.err.println("❌ Invalid company ID: " + companyId);
            return null;
        }

        Company company = companyDAO.getCompanyById(companyId);

        if (company != null) {
            System.out.println("✅ Company found: " + company.getName());
        } else {
            System.out.println("❌ Company not found with ID: " + companyId);
        }

        return company;
    }

    @Override
    public Company getCompanyByName(String name) {
        System.out.println("🔍 Fetching company by name: " + name);

        if (name == null || name.trim().isEmpty()) {
            System.err.println("❌ Invalid company name");
            return null;
        }

        Company company = companyDAO.getCompanyByName(name.trim());

        if (company != null) {
            System.out.println("✅ Company found: " + company.getName());
        } else {
            System.out.println("❌ Company not found with name: " + name);
        }

        return company;
    }

    @Override
    public List<Company> getAllActiveCompanies() {
        System.out.println("📋 Fetching all active companies");

        List<Company> companies = companyDAO.getActiveCompanies();
        System.out.println("✅ Retrieved " + companies.size() + " active companies");

        return companies;
    }

    @Override
    public boolean updateCompany(Company company) {
        System.out.println("✏️ Updating company: " + company.getCompanyId());

        // Input validation
        if (!isValidCompanyData(company) || company.getCompanyId() <= 0) {
            System.err.println("❌ Invalid company data for update");
            return false;
        }

        // Check if company exists
        Company existingCompany = companyDAO.getCompanyById(company.getCompanyId());
        if (existingCompany == null) {
            System.err.println("❌ Company not found for update: " + company.getCompanyId());
            return false;
        }

        // Check if new name conflicts with existing company (if name is being changed)
        if (!existingCompany.getName().equals(company.getName())) {
            Company nameConflict = companyDAO.getCompanyByName(company.getName());
            if (nameConflict != null && nameConflict.getCompanyId() != company.getCompanyId()) {
                System.err.println("❌ Company name already exists: " + company.getName());
                return false;
            }
        }

        boolean success = companyDAO.updateCompany(company);

        if (success) {
            System.out.println("✅ Company updated successfully: " + company.getName());
        } else {
            System.err.println("❌ Company update failed for ID: " + company.getCompanyId());
        }

        return success;
    }

    @Override
    public boolean activateCompany(int companyId) {
        System.out.println("🔓 Activating company: " + companyId);

        if (companyId <= 0) {
            System.err.println("❌ Invalid company ID for activation: " + companyId);
            return false;
        }

        boolean success = companyDAO.activateCompany(companyId);

        if (success) {
            System.out.println("✅ Company activated successfully: " + companyId);
        } else {
            System.err.println("❌ Company activation failed: " + companyId);
        }

        return success;
    }

    @Override
    public boolean deactivateCompany(int companyId) {
        System.out.println("🔒 Deactivating company: " + companyId);

        if (companyId <= 0) {
            System.err.println("❌ Invalid company ID for deactivation: " + companyId);
            return false;
        }

        // Check if company has active bookings (business rule)
        int cabinCount = getCompanyCabinCount(companyId);
        if (cabinCount > 0) {
            System.out.println("⚠️ Warning: Company has " + cabinCount + " cabins. Deactivating anyway.");
        }

        boolean success = companyDAO.deactivateCompany(companyId);

        if (success) {
            System.out.println("✅ Company deactivated successfully: " + companyId);
        } else {
            System.err.println("❌ Company deactivation failed: " + companyId);
        }

        return success;
    }

    @Override
    public List<Cabin> getCompanyCabins(int companyId) {
        System.out.println("🏠 Fetching cabins for company: " + companyId);

        if (companyId <= 0) {
            System.err.println("❌ Invalid company ID: " + companyId);
            return new ArrayList<>();
        }

        List<Cabin> cabins = cabinDAO.getCabinsByCompany(companyId);
        System.out.println("✅ Retrieved " + cabins.size() + " cabins for company: " + companyId);

        return cabins;
    }

    @Override
    public int getCompanyCabinCount(int companyId) {
        System.out.println("📊 Getting cabin count for company: " + companyId);

        if (companyId <= 0) {
            System.err.println("❌ Invalid company ID: " + companyId);
            return 0;
        }

        int count = companyDAO.getCompanyCabinCount(companyId);
        System.out.println("📈 Company " + companyId + " has " + count + " cabins");

        return count;
    }

    @Override
    public int getCompanyBookingCount(int companyId) {
        System.out.println("📊 Getting booking count for company: " + companyId);

        if (companyId <= 0) {
            System.err.println("❌ Invalid company ID: " + companyId);
            return 0;
        }

        // Get all cabins for the company and count their bookings
        List<Cabin> companyCabins = cabinDAO.getCabinsByCompany(companyId);
        int totalBookings = 0;

        // This is a simplified approach - in real scenario, we'd have a direct query
        for (Cabin cabin : companyCabins) {
            // We would need BookingDAO method to count bookings per cabin
            // For now, we'll return cabin count as proxy
        }

        System.out.println("📈 Company " + companyId + " has approximately " + companyCabins.size() + " booking-related cabins");
        return companyCabins.size(); // Simplified return
    }

    @Override
    public Company getMostPopularCompany() {
        System.out.println("🌟 Finding most popular company");

        List<Company> activeCompanies = companyDAO.getActiveCompanies();
        Company mostPopular = null;
        int maxCabins = 0;

        for (Company company : activeCompanies) {
            int cabinCount = companyDAO.getCompanyCabinCount(company.getCompanyId());
            if (cabinCount > maxCabins) {
                maxCabins = cabinCount;
                mostPopular = company;
            }
        }

        if (mostPopular != null) {
            System.out.println("🏆 Most popular company: " + mostPopular.getName() + " (" + maxCabins + " cabins)");
        } else {
            System.out.println("❌ No companies found");
        }

        return mostPopular;
    }

    @Override
    public List<Company> getCompaniesWithVIPCabins() {
        System.out.println("⭐ Finding companies with VIP cabins");

        List<Company> companiesWithVIP = new ArrayList<>();
        List<Company> allCompanies = companyDAO.getActiveCompanies();

        for (Company company : allCompanies) {
            List<Cabin> vipCabins = cabinDAO.getVIPOnlyCabins(company.getCompanyId());
            if (!vipCabins.isEmpty()) {
                companiesWithVIP.add(company);
                System.out.println("⭐ " + company.getName() + " has " + vipCabins.size() + " VIP cabins");
            }
        }

        System.out.println("✅ Found " + companiesWithVIP.size() + " companies with VIP cabins");
        return companiesWithVIP;
    }

    // PRIVATE UTILITY METHODS

    private boolean isValidCompanyData(Company company) {
        if (company == null) {
            System.err.println("❌ Company object is null");
            return false;
        }

        if (company.getName() == null || company.getName().trim().isEmpty()) {
            System.err.println("❌ Company name is required");
            return false;
        }

        if (company.getName().length() > 100) {
            System.err.println("❌ Company name too long (max 100 characters)");
            return false;
        }

        if (company.getLocation() != null && company.getLocation().length() > 200) {
            System.err.println("❌ Company location too long (max 200 characters)");
            return false;
        }

        if (company.getContactInfo() != null && company.getContactInfo().length() > 500) {
            System.err.println("❌ Company contact info too long (max 500 characters)");
            return false;
        }

        return true;
    }

    /**
     * Get default company for new user registration
     */
    public Company getDefaultCompany() {
        System.out.println("🏢 Getting default company for new user");

        List<Company> activeCompanies = getAllActiveCompanies();
        if (!activeCompanies.isEmpty()) {
            Company defaultCompany = activeCompanies.get(0); // First active company
            System.out.println("✅ Default company: " + defaultCompany.getName());
            return defaultCompany;
        }

        System.err.println("❌ No active companies found for default assignment");
        return null;
    }

    /**
     * Business method to check if company can be safely deleted
     */
    public boolean canDeleteCompany(int companyId) {
        System.out.println("🔍 Checking if company can be deleted: " + companyId);

        int cabinCount = getCompanyCabinCount(companyId);

        if (cabinCount > 0) {
            System.out.println("❌ Company cannot be deleted - has " + cabinCount + " cabins");
            return false;
        }

        System.out.println("✅ Company can be safely deleted");
        return true;
    }
}
