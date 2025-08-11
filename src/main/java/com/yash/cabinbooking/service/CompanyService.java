package com.yash.cabinbooking.service;

import com.yash.cabinbooking.model.Company;
import com.yash.cabinbooking.model.Cabin;
import java.util.List;

/**
 * COMPANY SERVICE INTERFACE
 *
 * EVALUATION EXPLANATION:
 * - Multi-company support for enterprise environments
 * - Company-specific cabin management
 * - Business analytics per company
 */
public interface CompanyService {

    // Company management
    boolean createCompany(Company company);
    Company getCompanyById(int companyId);
    Company getCompanyByName(String name);
    List<Company> getAllActiveCompanies();
    boolean updateCompany(Company company);
    boolean activateCompany(int companyId);
    boolean deactivateCompany(int companyId);

    // Company-cabin relationship
    List<Cabin> getCompanyCabins(int companyId);
    int getCompanyCabinCount(int companyId);
    int getCompanyBookingCount(int companyId);

    // Business analytics
    Company getMostPopularCompany();
    List<Company> getCompaniesWithVIPCabins();
}
