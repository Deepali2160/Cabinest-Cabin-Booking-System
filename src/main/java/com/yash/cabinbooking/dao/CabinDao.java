package com.yash.cabinbooking.dao;

import com.yash.cabinbooking.model.Cabin;
import com.yash.cabinbooking.model.User;
import java.util.List;

public interface CabinDao {

    // CRUD operations (no company ID needed)
    boolean createCabin(Cabin cabin);
    Cabin getCabinById(int cabinId);
    List<Cabin> getAllCabins();
    List<Cabin> getAllActiveCabins(); // ✅ Already exists
    boolean updateCabin(Cabin cabin);
    boolean deleteCabin(int cabinId);

    // Business operations (no company ID parameters)
    List<Cabin> getAvailableCabins();
    List<Cabin> getAccessibleCabins(User user);
    List<Cabin> getVIPOnlyCabins(); // ✅ Already exists
    List<Cabin> getCabinsByCapacity(int minCapacity, int maxCapacity);

    // AI related operations (simplified)
    List<Cabin> getCabinsWithAmenities(String amenities);
    List<Cabin> getPopularCabins();
    List<Cabin> getSimilarCabins(int cabinId);

    // Status management
    boolean updateCabinStatus(int cabinId, Cabin.Status status);
    boolean deactivateCabin(int cabinId);

    // ✅ NEW METHODS - Required for AI Recommendation Service

    /**
     * Get all cabins by company ID (for AI compatibility)
     * @param companyId Company ID (will always be 1 for single company)
     * @return List of cabins
     */
    List<Cabin> getCabinsByCompany(int companyId);

    /**
     * Get accessible cabins with company ID (for AI compatibility)
     * @param companyId Company ID
     * @param user User object
     * @return List of accessible cabins
     */
    List<Cabin> getAccessibleCabins(int companyId, User user);

    /**
     * Get VIP only cabins with company ID (for AI compatibility)
     * @param companyId Company ID
     * @return List of VIP cabins
     */
    List<Cabin> getVIPOnlyCabins(int companyId);
}
