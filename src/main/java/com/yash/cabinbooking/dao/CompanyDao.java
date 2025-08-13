package com.yash.cabinbooking.dao;

import com.yash.cabinbooking.model.Company;

/**
 * COMPANY DAO INTERFACE - SINGLE COMPANY VERSION
 * Modified for Yash Technology single company usage
 */
public interface CompanyDao {

    // Single company configuration operations
    Company getCompanyConfig();
    boolean updateCompanyConfig(Company company);

    // Utility operations
    boolean isCompanyActive();
    int getTotalCabinCount();

    // Company status management
    boolean activateCompany();
    boolean deactivateCompany();
}
