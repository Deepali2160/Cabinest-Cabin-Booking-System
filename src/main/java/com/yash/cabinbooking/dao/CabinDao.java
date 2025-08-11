package com.yash.cabinbooking.dao;

import com.yash.cabinbooking.model.Cabin;
import com.yash.cabinbooking.model.User;
import java.util.List;

/**
 * CABIN DAO INTERFACE
 */
public interface CabinDao {

    // CRUD operations
    boolean createCabin(Cabin cabin);
    Cabin getCabinById(int cabinId);
    List<Cabin> getAllCabins();
    List<Cabin> getCabinsByCompany(int companyId);
    boolean updateCabin(Cabin cabin);
    boolean deleteCabin(int cabinId);

    // Business operations
    List<Cabin> getAvailableCabins(int companyId);
    List<Cabin> getAccessibleCabins(int companyId, User user);
    List<Cabin> getVIPOnlyCabins(int companyId);
    List<Cabin> getCabinsByCapacity(int minCapacity, int maxCapacity);

    // AI related operations
    List<Cabin> getCabinsWithAmenities(String amenities);
    List<Cabin> getPopularCabins(int companyId);
    List<Cabin> getSimilarCabins(int cabinId);
}
