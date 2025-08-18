package com.yash.cabinbooking.service;

import com.yash.cabinbooking.model.*;
import java.sql.Date;
import java.util.List;

public interface AIRecommendationService {

    // Smart cabin recommendations
    List<Cabin> getRecommendedCabinsForUser(User user, int companyId);
    List<Cabin> getSimilarCabins(int cabinId, User user);
    List<Cabin> getPopularCabins(int companyId, User user);
    List<Cabin> getCabinsByUserPreferences(User user, int companyId);

    // Smart time slot recommendations
    List<String> getRecommendedTimeSlots(User user, int cabinId, Date date);
    List<String> getAlternativeTimeSlots(int cabinId, Date date, String requestedSlot);
    String getBestTimeSlotForUser(User user, Date date);

    // Conflict resolution
    List<Cabin> findAlternativeCabins(int requestedCabinId, Date date, String timeSlot, User user);
    List<String> suggestAlternativeDates(int cabinId, String timeSlot, Date requestedDate);

    // User preference learning
    boolean updateUserPreferences(User user, Booking booking);
    boolean analyzeUserBookingPattern(int userId);
    String getUserPreferredTimeSlot(int userId);
    int getUserPreferredCapacity(int userId);

    // Popular recommendations for new users
    List<Cabin> getNewUserRecommendations(User user, int companyId);
    List<String> getPopularTimeSlots();
    List<String> getPopularPurposes();

    // AI analytics and insights
    double calculateUserBookingScore(User user);
    String predictUserPreferredTimeSlot(User user);
    int predictUserPreferredCapacity(User user);
    List<String> suggestBookingPurposes(User user);

    // Smart suggestions
    boolean shouldRecommendVIPCabin(User user, String purpose);
    List<Cabin> getUnderbookedCabins(int companyId, Date date);
    String generateBookingInsight(Booking booking);
}
