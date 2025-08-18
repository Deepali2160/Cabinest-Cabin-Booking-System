package com.yash.cabinbooking.serviceimpl;

import com.yash.cabinbooking.service.CabinService;
import com.yash.cabinbooking.dao.CabinDao;
import com.yash.cabinbooking.daoimpl.CabinDaoImpl;
import com.yash.cabinbooking.model.Cabin;
import com.yash.cabinbooking.model.User;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class CabinServiceImpl implements CabinService {

    private CabinDao cabinDao;

    public CabinServiceImpl() {
        this.cabinDao = new CabinDaoImpl();
        System.out.println("🔧 CabinService initialized for Yash Technology (Single Company Mode)");
    }

    @Override
    public boolean addCabin(Cabin cabin) {
        System.out.println("🏠 Adding new cabin: " + cabin.getName());

        try {
            // Validate cabin data
            if (!validateCabinData(cabin)) {
                System.err.println("❌ Cabin validation failed");
                return false;
            }

            // Check for unique cabin name (no company filter needed)
            if (!isCabinNameUnique(cabin.getName())) {
                System.err.println("❌ Cabin name already exists: " + cabin.getName());
                return false;
            }

            // Set default status if not provided
            if (cabin.getStatus() == null) {
                cabin.setStatus(Cabin.Status.ACTIVE);
            }

            // Set default company ID
            cabin.setCompanyId(Cabin.getDefaultCompanyId());

            boolean result = cabinDao.createCabin(cabin);

            if (result) {
                System.out.println("✅ Cabin added successfully: " + cabin.getName() + " (ID: " + cabin.getCabinId() + ")");
            } else {
                System.err.println("❌ Failed to add cabin: " + cabin.getName());
            }

            return result;

        } catch (Exception e) {
            System.err.println("❌ Error in addCabin service: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCabin(Cabin cabin) {
        System.out.println("📝 Updating cabin: " + cabin.getName() + " (ID: " + cabin.getCabinId() + ")");

        try {
            // Validate cabin data
            if (!validateCabinData(cabin)) {
                System.err.println("❌ Cabin validation failed for update");
                return false;
            }

            // Check if cabin exists
            Cabin existingCabin = cabinDao.getCabinById(cabin.getCabinId());
            if (existingCabin == null) {
                System.err.println("❌ Cabin not found for update: " + cabin.getCabinId());
                return false;
            }

            // Ensure company ID remains default
            cabin.setCompanyId(Cabin.getDefaultCompanyId());

            boolean result = cabinDao.updateCabin(cabin);

            if (result) {
                System.out.println("✅ Cabin updated successfully: " + cabin.getName());
            } else {
                System.err.println("❌ Failed to update cabin: " + cabin.getName());
            }

            return result;

        } catch (Exception e) {
            System.err.println("❌ Error in updateCabin service: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCabin(int cabinId) {
        System.out.println("🗑️ Deleting cabin: " + cabinId);

        try {
            // Check if cabin exists
            Cabin cabin = cabinDao.getCabinById(cabinId);
            if (cabin == null) {
                System.err.println("❌ Cabin not found for deletion: " + cabinId);
                return false;
            }

            boolean result = cabinDao.deleteCabin(cabinId);

            if (result) {
                System.out.println("✅ Cabin deleted successfully: " + cabinId);
            } else {
                System.err.println("❌ Failed to delete cabin: " + cabinId);
            }

            return result;

        } catch (Exception e) {
            System.err.println("❌ Error in deleteCabin service: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Cabin getCabinById(int cabinId) {
        System.out.println("🔍 Getting cabin by ID: " + cabinId);

        try {
            Cabin cabin = cabinDao.getCabinById(cabinId);

            if (cabin != null) {
                System.out.println("✅ Cabin found: " + cabin.getName());
            } else {
                System.out.println("❌ Cabin not found with ID: " + cabinId);
            }

            return cabin;

        } catch (Exception e) {
            System.err.println("❌ Error in getCabinById service: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Cabin> getAllCabins() {
        System.out.println("📋 Getting all cabins");

        try {
            List<Cabin> cabins = cabinDao.getAllCabins();
            System.out.println("✅ Retrieved " + cabins.size() + " cabins");
            return cabins;

        } catch (Exception e) {
            System.err.println("❌ Error in getAllCabins service: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ UPDATED: Available cabins for user without company ID
    @Override
    public List<Cabin> getAvailableCabinsForUser(User user) {
        System.out.println("👤 Getting accessible cabins for user: " + user.getName() + " (Type: " + user.getUserType() + ")");

        try {
            List<Cabin> cabins = cabinDao.getAccessibleCabins(user);
            System.out.println("✅ Retrieved " + cabins.size() + " accessible cabins for user: " + user.getName());
            return cabins;

        } catch (Exception e) {
            System.err.println("❌ Error in getAvailableCabinsForUser service: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ UPDATED: VIP cabins without company ID
    @Override
    public List<Cabin> getVIPCabins() {
        System.out.println("⭐ Getting VIP cabins");

        try {
            List<Cabin> cabins = cabinDao.getVIPOnlyCabins();
            System.out.println("✅ Retrieved " + cabins.size() + " VIP cabins");
            return cabins;

        } catch (Exception e) {
            System.err.println("❌ Error in getVIPCabins service: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ NEW: Get active cabins
    @Override
    public List<Cabin> getActiveCabins() {
        System.out.println("📋 Getting active cabins");

        try {
            List<Cabin> cabins = cabinDao.getAllActiveCabins();
            System.out.println("✅ Retrieved " + cabins.size() + " active cabins");
            return cabins;

        } catch (Exception e) {
            System.err.println("❌ Error in getActiveCabins service: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Cabin> getAllCabinsForAdmin() {
        System.out.println("👨‍💼 Getting all cabins for admin dashboard");

        try {
            List<Cabin> cabins = cabinDao.getAllCabins();
            System.out.println("✅ Retrieved " + cabins.size() + " cabins for admin");
            return cabins;

        } catch (Exception e) {
            System.err.println("❌ Error in getAllCabinsForAdmin service: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean updateCabinStatus(int cabinId, Cabin.Status status) {
        System.out.println("🔄 Updating cabin status: " + cabinId + " to " + status);

        try {
            boolean result = cabinDao.updateCabinStatus(cabinId, status);

            if (result) {
                System.out.println("✅ Cabin status updated successfully: " + cabinId + " to " + status);
            } else {
                System.err.println("❌ Failed to update cabin status: " + cabinId);
            }

            return result;

        } catch (Exception e) {
            System.err.println("❌ Error in updateCabinStatus service: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getTotalCabinCount() {
        System.out.println("📊 Getting total cabin count");

        try {
            List<Cabin> cabins = cabinDao.getAllCabins();
            int count = cabins.size();
            System.out.println("✅ Total cabin count: " + count);
            return count;

        } catch (Exception e) {
            System.err.println("❌ Error in getTotalCabinCount service: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    // ✅ UPDATED: Active cabin count without company ID
    @Override
    public int getActiveCabinCount() {
        System.out.println("📊 Getting active cabin count");

        try {
            List<Cabin> cabins = cabinDao.getAvailableCabins();
            int count = cabins.size();
            System.out.println("✅ Active cabin count: " + count);
            return count;

        } catch (Exception e) {
            System.err.println("❌ Error in getActiveCabinCount service: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public List<Cabin> searchCabinsByCapacity(int minCapacity, int maxCapacity) {
        System.out.println("🔍 Searching cabins by capacity: " + minCapacity + "-" + maxCapacity);

        try {
            List<Cabin> cabins = cabinDao.getCabinsByCapacity(minCapacity, maxCapacity);
            System.out.println("✅ Found " + cabins.size() + " cabins with capacity " + minCapacity + "-" + maxCapacity);
            return cabins;

        } catch (Exception e) {
            System.err.println("❌ Error in searchCabinsByCapacity service: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Cabin> searchCabinsByAmenities(String amenities) {
        System.out.println("🔍 Searching cabins by amenities: " + amenities);

        try {
            List<Cabin> cabins = cabinDao.getCabinsWithAmenities(amenities);
            System.out.println("✅ Found " + cabins.size() + " cabins with amenities: " + amenities);
            return cabins;

        } catch (Exception e) {
            System.err.println("❌ Error in searchCabinsByAmenities service: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Cabin> getCabinsByLocation(String location) {
        System.out.println("🔍 Getting cabins by location: " + location);

        try {
            List<Cabin> allCabins = cabinDao.getAllCabins();
            List<Cabin> filteredCabins = allCabins.stream()
                    .filter(cabin -> cabin.getLocation() != null &&
                            cabin.getLocation().toLowerCase().contains(location.toLowerCase()) &&
                            cabin.getStatus() == Cabin.Status.ACTIVE)
                    .collect(Collectors.toList());

            System.out.println("✅ Found " + filteredCabins.size() + " cabins in location: " + location);
            return filteredCabins;

        } catch (Exception e) {
            System.err.println("❌ Error in getCabinsByLocation service: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ UPDATED: Popular cabins without company ID
    @Override
    public List<Cabin> getPopularCabins() {
        System.out.println("🌟 Getting popular cabins");

        try {
            List<Cabin> cabins = cabinDao.getPopularCabins();
            System.out.println("✅ Retrieved " + cabins.size() + " popular cabins");
            return cabins;

        } catch (Exception e) {
            System.err.println("❌ Error in getPopularCabins service: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ UPDATED: Recommended cabins without company ID
    @Override
    public List<Cabin> getRecommendedCabins(User user) {
        System.out.println("🤖 Getting AI recommendations for user: " + user.getName());

        try {
            // Get accessible cabins for user
            List<Cabin> accessibleCabins = cabinDao.getAccessibleCabins(user);

            // AI logic: Prioritize popular cabins that user can access
            List<Cabin> popularCabins = cabinDao.getPopularCabins();

            // Filter popular cabins that are accessible to user
            List<Cabin> recommendations = popularCabins.stream()
                    .filter(cabin -> accessibleCabins.stream()
                            .anyMatch(accessible -> accessible.getCabinId() == cabin.getCabinId()))
                    .limit(3)
                    .collect(Collectors.toList());

            // If not enough popular cabins, add more accessible ones
            if (recommendations.size() < 3) {
                accessibleCabins.stream()
                        .filter(cabin -> recommendations.stream()
                                .noneMatch(rec -> rec.getCabinId() == cabin.getCabinId()))
                        .limit(3 - recommendations.size())
                        .forEach(recommendations::add);
            }

            System.out.println("🤖 Generated " + recommendations.size() + " recommendations for user: " + user.getName());
            return recommendations;

        } catch (Exception e) {
            System.err.println("❌ Error in getRecommendedCabins service: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Cabin> getSimilarCabins(int cabinId) {
        System.out.println("🔗 Getting similar cabins for cabin: " + cabinId);

        try {
            List<Cabin> cabins = cabinDao.getSimilarCabins(cabinId);
            System.out.println("✅ Found " + cabins.size() + " similar cabins");
            return cabins;

        } catch (Exception e) {
            System.err.println("❌ Error in getSimilarCabins service: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean validateCabinData(Cabin cabin) {
        System.out.println("✅ Validating cabin data: " + cabin.getName());

        try {
            // Check required fields
            if (cabin.getName() == null || cabin.getName().trim().isEmpty()) {
                System.err.println("❌ Cabin name is required");
                return false;
            }

            if (cabin.getCapacity() <= 0) {
                System.err.println("❌ Cabin capacity must be greater than 0");
                return false;
            }

            if (cabin.getCapacity() > 50) {
                System.err.println("❌ Cabin capacity cannot exceed 50");
                return false;
            }

            if (cabin.getLocation() == null || cabin.getLocation().trim().isEmpty()) {
                System.err.println("❌ Cabin location is required");
                return false;
            }

            System.out.println("✅ Cabin validation passed: " + cabin.getName());
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error in validateCabinData service: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ✅ UPDATED: Cabin name uniqueness without company filter
    @Override
    public boolean isCabinNameUnique(String name) {
        System.out.println("🔍 Checking cabin name uniqueness: " + name);

        try {
            List<Cabin> allCabins = cabinDao.getAllCabins();

            boolean isUnique = allCabins.stream()
                    .noneMatch(cabin -> cabin.getName().equalsIgnoreCase(name.trim()));

            if (isUnique) {
                System.out.println("✅ Cabin name is unique: " + name);
            } else {
                System.out.println("❌ Cabin name already exists: " + name);
            }

            return isUnique;

        } catch (Exception e) {
            System.err.println("❌ Error in isCabinNameUnique service: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean canUserAccessCabin(int cabinId, User user) {
        System.out.println("🔒 Checking user access for cabin: " + cabinId + " by user: " + user.getName());

        try {
            Cabin cabin = cabinDao.getCabinById(cabinId);

            if (cabin == null) {
                System.err.println("❌ Cabin not found: " + cabinId);
                return false;
            }

            boolean canAccess = cabin.isAccessibleForUser(user);

            if (canAccess) {
                System.out.println("✅ User can access cabin: " + cabin.getName());
            } else {
                System.out.println("❌ User cannot access cabin: " + cabin.getName() + " (VIP Only)");
            }

            return canAccess;

        } catch (Exception e) {
            System.err.println("❌ Error in canUserAccessCabin service: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
