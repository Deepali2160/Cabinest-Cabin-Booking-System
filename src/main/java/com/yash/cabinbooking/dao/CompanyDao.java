package com.yash.cabinbooking.dao;

import com.yash.cabinbooking.model.Company;
import java.util.List;

/**
 * COMPANY DAO INTERFACE
 */
public interface CompanyDao {

    // CRUD operations
    boolean createCompany(Company company);
    Company getCompanyById(int companyId);
    Company getCompanyByName(String name);
    List<Company> getAllCompanies();
    List<Company> getActiveCompanies();
    boolean updateCompany(Company company);
    boolean deleteCompany(int companyId);

    // Business operations
    boolean activateCompany(int companyId);
    boolean deactivateCompany(int companyId);
    int getCompanyCabinCount(int companyId);
}
