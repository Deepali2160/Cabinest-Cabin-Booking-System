package com.yash.cabinbooking.dao;

import com.yash.cabinbooking.model.Company;

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
