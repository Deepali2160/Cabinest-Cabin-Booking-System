package com.yash.cabinbooking.service;

import com.yash.cabinbooking.model.Cabin;
import com.yash.cabinbooking.model.User;
import com.yash.cabinbooking.model.Company;
import java.util.List;

/**
 * CABIN SERVICE INTERFACE
 *
 * Business logic layer for cabin management
 * Provides high-level operations for cabin CRUD and business rules
 */
public interface CabinService {

    // Core CRUD Operations
    boolean addCabin(Cabin cabin);
    boolean updateCabin(Cabin cabin);
    boolean deleteCabin(int cabinId);
    Cabin getCabinById(int cabinId);
    List<Cabin> getAllCabins();

    // Business Operations
    List<Cabin> getCabinsByCompany(int companyId);
    List<Cabin> getAvailableCabinsForUser(int companyId, User user);
    List<Cabin> getVIPCabins(int companyId);

    // Admin Operations
    List<Cabin> getAllCabinsForAdmin();
    boolean updateCabinStatus(int cabinId, Cabin.Status status);
    int getTotalCabinCount();
    int getActiveCabinCount(int companyId);

    // Search and Filter Operations
    List<Cabin> searchCabinsByCapacity(int minCapacity, int maxCapacity);
    List<Cabin> searchCabinsByAmenities(String amenities);
    List<Cabin> getCabinsByLocation(String location);

    // AI and Analytics Operations
    List<Cabin> getPopularCabins(int companyId);
    List<Cabin> getRecommendedCabins(User user, int companyId);
    List<Cabin> getSimilarCabins(int cabinId);

    // Validation Operations
    boolean validateCabinData(Cabin cabin);
    boolean isCabinNameUnique(String name, int companyId);
    boolean canUserAccessCabin(int cabinId, User user);
}
