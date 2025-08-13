package com.yash.cabinbooking.service;

import com.yash.cabinbooking.model.Company;
import com.yash.cabinbooking.model.Cabin;
import java.util.List;

/**
 * COMPANY SERVICE INTERFACE - SINGLE COMPANY VERSION
 * Modified for Yash Technology single company usage
 */
public interface CompanyService {

    // Single company configuration
    Company getCompanyConfig();
    boolean updateCompanyConfig(Company company);

    // Company status management
    boolean activateCompany();
    boolean deactivateCompany();
    boolean isCompanyActive();

    // Company analytics (no company ID needed)
    List<Cabin> getAllCabins();
    int getTotalCabinCount();
    int getTotalBookingCount();

    // Business operations
    List<Cabin> getVIPCabins();
    boolean canPerformBooking();
}
