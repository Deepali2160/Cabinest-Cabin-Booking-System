package com.yash.cabinbooking.service;

import com.yash.cabinbooking.model.Cabin;
import com.yash.cabinbooking.model.User;
import java.util.List;

public interface CabinService {

    // Core CRUD Operations (no company ID needed)
    boolean addCabin(Cabin cabin);
    boolean updateCabin(Cabin cabin);
    boolean deleteCabin(int cabinId);
    Cabin getCabinById(int cabinId);
    List<Cabin> getAllCabins();

    // Business Operations (simplified for single company)
    List<Cabin> getAvailableCabinsForUser(User user);
    List<Cabin> getVIPCabins();
    List<Cabin> getActiveCabins();

    // Admin Operations
    List<Cabin> getAllCabinsForAdmin();
    boolean updateCabinStatus(int cabinId, Cabin.Status status);
    int getTotalCabinCount();
    int getActiveCabinCount();

    // Search and Filter Operations
    List<Cabin> searchCabinsByCapacity(int minCapacity, int maxCapacity);
    List<Cabin> searchCabinsByAmenities(String amenities);
    List<Cabin> getCabinsByLocation(String location);

    // AI and Analytics Operations
    List<Cabin> getPopularCabins();
    List<Cabin> getRecommendedCabins(User user);
    List<Cabin> getSimilarCabins(int cabinId);

    // Validation Operations
    boolean validateCabinData(Cabin cabin);
    boolean isCabinNameUnique(String name);
    boolean canUserAccessCabin(int cabinId, User user);
}
