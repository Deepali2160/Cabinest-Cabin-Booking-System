package com.yash.cabinbooking.controller;

import com.yash.cabinbooking.dao.CabinDao;
import com.yash.cabinbooking.daoimpl.CabinDaoImpl;
import com.yash.cabinbooking.daoimpl.BookingDaoImpl;
import com.yash.cabinbooking.service.BookingService;
import com.yash.cabinbooking.service.UserService;
import com.yash.cabinbooking.service.CompanyService;
import com.yash.cabinbooking.serviceimpl.*;
import com.yash.cabinbooking.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

/**
 * BOOKING CONTROLLER - SINGLE COMPANY VERSION
 * Modified for Yash Technology single company usage
 */
@WebServlet(name = "BookingController", urlPatterns = {"/booking/*", "/book", "/mybookings"})
public class BookingController extends HttpServlet {

    private BookingService bookingService;
    private UserService userService;
    private CompanyService companyService;
    // ✅ REMOVED: AIRecommendationService (causing errors)
    private CabinDao cabinDAO;

    @Override
    public void init() throws ServletException {
        this.bookingService = new BookingServiceImpl();
        this.userService = new UserServiceImpl();
        this.companyService = new CompanyServiceImpl();
        // ✅ REMOVED: this.aiService = new AIRecommendationServiceImpl();
        this.cabinDAO = new CabinDaoImpl();
        System.out.println("🔧 BookingController initialized for Yash Technology (Single Company)");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            System.out.println("🔒 Unauthorized booking access, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = getActionFromRequest(request);
        System.out.println("🌐 Booking GET Request: " + action + " by user: " + currentUser.getName());

        switch (action) {
            case "book":
                showBookingForm(request, response, currentUser);
                break;
            case "mybookings":
                showMyBookings(request, response, currentUser);
                break;
            case "cancel":
                cancelBooking(request, response, currentUser);
                break;
            case "availability":
                checkAvailability(request, response, currentUser);
                break;
            case "alternatives":
                getAlternatives(request, response, currentUser);
                break;
            default:
                showMyBookings(request, response, currentUser);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = getActionFromRequest(request);
        System.out.println("📝 Booking POST Request: " + action + " by user: " + currentUser.getName());

        switch (action) {
            case "book":
                processBooking(request, response, currentUser);
                break;
            case "availability":  // ✅ ADD THIS CASE
                checkAvailability(request, response, currentUser);
                break;
            case "multiDayAvailability":  // ✅ ADD THIS CASE TOO
                checkMultiDayAvailability(request, response, currentUser);
                break;
            default:
                showMyBookings(request, response, currentUser);
                break;
        }
    }

    // ✅ UPDATED: Single company booking form
    private void showBookingForm(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("📝 Showing booking form for user: " + user.getName());

        try {
            String cabinIdStr = request.getParameter("cabinId");

            // ✅ SINGLE COMPANY: Get company config instead of by ID
            Company company = companyService.getCompanyConfig();
            if (company == null) {
                System.err.println("❌ Company configuration not found");
                request.setAttribute("error", "Company configuration not available");
                request.getRequestDispatcher("/common/error.jsp").forward(request, response);
                return;
            }

            // ✅ UPDATED: Get accessible cabins without company ID
            List<Cabin> accessibleCabins = cabinDAO.getAccessibleCabins(user);

            // ✅ REMOVED: AI recommendations (causing errors)
            // List<Cabin> recommendedCabins = aiService.getRecommendedCabinsForUser(user, companyId);
            List<Cabin> recommendedCabins = new ArrayList<>();

            // ✅ SIMPLIFIED: Popular time slots without AI
            List<String> popularTimeSlots = bookingService.getPopularTimeSlots();

            // ✅ REMOVED: AI suggested purposes
            List<String> suggestedPurposes = getCommonBookingPurposes();

            // Duration options and start times
            BookingDaoImpl bookingDao = new BookingDaoImpl();
            List<Integer> availableDurations = bookingDao.getAvailableDurations();
            List<String> startTimeOptions = generateStartTimeOptions();

            Cabin selectedCabin = null;
            if (cabinIdStr != null && !cabinIdStr.trim().isEmpty()) {
                try {
                    int cabinId = Integer.parseInt(cabinIdStr);
                    selectedCabin = cabinDAO.getCabinById(cabinId);

                    if (selectedCabin != null && !selectedCabin.isAccessibleForUser(user)) {
                        System.err.println("❌ User cannot access cabin: " + cabinId);
                        selectedCabin = null;
                        request.setAttribute("error", "You don't have access to this cabin");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("❌ Invalid cabin ID: " + cabinIdStr);
                }
            }

            // Set all attributes
            request.setAttribute("user", user);
            request.setAttribute("company", company);
            request.setAttribute("cabins", accessibleCabins);
            request.setAttribute("recommendedCabins", recommendedCabins);
            request.setAttribute("selectedCabin", selectedCabin);
            request.setAttribute("popularTimeSlots", popularTimeSlots);
            request.setAttribute("suggestedPurposes", suggestedPurposes);
            request.setAttribute("availableDurations", availableDurations);
            request.setAttribute("startTimeOptions", startTimeOptions);
            request.setAttribute("todayDate", new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

            System.out.println("✅ Booking form loaded successfully");
            System.out.println("   - Available cabins: " + accessibleCabins.size());
            System.out.println("   - Duration options: " + availableDurations.size());

            request.getRequestDispatcher("/user/booking-form.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error loading booking form: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading booking form. Please try again.");
            request.getRequestDispatcher("/common/error.jsp").forward(request, response);
        }
    }

    private void processBooking(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("📅 Processing booking request for user: " + user.getName());

        // ✅ ADD THESE DEBUG LINES:
        System.out.println("🔍 User Type: " + user.getUserType());
        System.out.println("🔍 Is VIP: " + user.isVIP());

        try {
            String cabinIdStr = request.getParameter("cabinId");
            String bookingDateStr = request.getParameter("bookingDate");
            String timeSlot = request.getParameter("timeSlot");
            String purpose = request.getParameter("purpose");

            System.out.println("📋 Booking details - Cabin: " + cabinIdStr + ", Date: " + bookingDateStr + ", Time: " + timeSlot);

            String validationError = validateBookingInput(cabinIdStr, bookingDateStr, timeSlot, purpose);
            if (validationError != null) {
                System.err.println("❌ Booking validation failed: " + validationError);
                request.setAttribute("error", validationError);
                showBookingForm(request, response, user);
                return;
            }

            int cabinId = Integer.parseInt(cabinIdStr);
            Date bookingDate = Date.valueOf(bookingDateStr);

            Cabin cabin = cabinDAO.getCabinById(cabinId);
            if (cabin == null || !cabin.isAccessibleForUser(user)) {
                System.err.println("❌ Cabin not accessible: " + cabinId);
                request.setAttribute("error", "Selected cabin is not available or accessible to you");
                showBookingForm(request, response, user);
                return;
            }

            // ✅ NEW: Check slot availability first
            boolean slotAvailable = bookingService.isSlotAvailable(cabinId, bookingDate, timeSlot);
            System.out.println("🔍 Slot Available: " + slotAvailable);

            // ✅ VIP OVERRIDE LOGIC - THE MAIN FIX:
            if (user.isVIP() && !slotAvailable) {
                System.out.println("⭐⭐⭐ VIP USER DETECTED WITH OCCUPIED SLOT - TRIGGERING OVERRIDE!");

                // Create VIP booking object
                Booking vipBooking = new Booking();
                vipBooking.setUserId(user.getUserId());
                vipBooking.setCabinId(cabinId);
                vipBooking.setBookingDate(bookingDate);
                vipBooking.setTimeSlot(timeSlot);
                vipBooking.setPurpose(purpose.trim());

                // Force VIP booking
                boolean vipSuccess = bookingService.forceBookingForVIP(vipBooking, user);

                if (vipSuccess) {
                    System.out.println("⭐ VIP OVERRIDE SUCCESSFUL!");
                    HttpSession session = request.getSession();
                    session.setAttribute("successMessage",
                            "⭐ VIP Booking successful! Conflicting users have been automatically reallocated.");
                    response.sendRedirect(request.getContextPath() + "/mybookings");
                    return;
                } else {
                    System.err.println("❌ VIP override failed");
                    request.setAttribute("error", "VIP booking failed. Please contact admin.");
                    showBookingForm(request, response, user);
                    return;
                }
            }

            // ✅ NORMAL USER LOGIC - If not VIP or slot is available
            if (!slotAvailable) {
                System.err.println("❌ Time slot not available for normal user: " + timeSlot);

                BookingDaoImpl bookingDao = new BookingDaoImpl();
                List<String> alternativeSlots = bookingDao.getAlternativeTimeSlots(cabinId, bookingDate, timeSlot, 3);

                request.setAttribute("error", "Selected time slot is not available");
                request.setAttribute("alternativeSlots", alternativeSlots);
                request.setAttribute("requestedCabin", cabin);
                request.setAttribute("requestedDate", bookingDateStr);
                request.setAttribute("requestedTimeSlot", timeSlot);

                showBookingForm(request, response, user);
                return;
            }

            // ✅ NORMAL BOOKING CREATION
            Booking newBooking = new Booking();
            newBooking.setUserId(user.getUserId());
            newBooking.setCabinId(cabinId);
            newBooking.setBookingDate(bookingDate);
            newBooking.setTimeSlot(timeSlot);
            newBooking.setPurpose(purpose.trim());
            newBooking.setStatus(Booking.Status.PENDING);

            boolean bookingSuccess = bookingService.createBooking(newBooking, user);

            if (bookingSuccess) {
                System.out.println("✅ Booking created successfully: ID " + newBooking.getBookingId());

                HttpSession session = request.getSession();
                session.setAttribute("successMessage",
                        "Booking created successfully! " +
                                (user.isVIP() ? "VIP priority applied. " : ""));

                response.sendRedirect(request.getContextPath() + "/mybookings");

            } else {
                System.err.println("❌ Booking creation failed");
                request.setAttribute("error", "Failed to create booking. Please try again.");
                showBookingForm(request, response, user);
            }

        } catch (Exception e) {
            System.err.println("❌ Error processing booking: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error processing booking. Please try again.");
            showBookingForm(request, response, user);
        }
    }


    private void checkAvailability(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("🔍 Checking availability for AJAX request");

        // ✅ ADD VIP CHECK HERE:
        System.out.println("🔍 User Type: " + user.getUserType());
        System.out.println("🔍 Is VIP: " + user.isVIP());

        String cabinIdStr = request.getParameter("cabinId");
        String dateStr = request.getParameter("date");
        String startTime = request.getParameter("startTime");
        String durationStr = request.getParameter("duration");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (cabinIdStr == null || cabinIdStr.trim().isEmpty() ||
                    dateStr == null || dateStr.trim().isEmpty() ||
                    startTime == null || startTime.trim().isEmpty() ||
                    durationStr == null || durationStr.trim().isEmpty()) {

                System.err.println("❌ Missing parameters in availability check");
                response.getWriter().write("{\"available\": false, \"error\": \"Missing parameters\"}");
                return;
            }

            int cabinId = Integer.parseInt(cabinIdStr);
            Date date = Date.valueOf(dateStr);
            int duration = Integer.parseInt(durationStr);

            String timeSlot = generateTimeSlot(startTime, duration);

            if (timeSlot == null) {
                System.err.println("❌ Invalid time slot generated");
                response.getWriter().write("{\"available\": false, \"error\": \"Invalid time slot\"}");
                return;
            }

            System.out.println("🔍 Checking: Cabin " + cabinId + ", Date " + date + ", Slot " + timeSlot);

            boolean isAvailable = bookingService.isSlotAvailable(cabinId, date, timeSlot);

            if (isAvailable) {
                response.getWriter().write("{\"available\": true, \"timeSlot\": \"" + timeSlot + "\"}");
            } else {
                // ✅ VIP OVERRIDE FOR AJAX:
                if (user.isVIP()) {
                    System.out.println("⭐⭐⭐ VIP USER DETECTED IN AJAX - ALLOWING OVERRIDE!");
                    response.getWriter().write("{\"available\": true, \"vipOverride\": true, \"message\": \"VIP Override: You can book this slot. Conflicting users will be reallocated.\", \"timeSlot\": \"" + timeSlot + "\"}");
                    return;
                }

                // Normal user - show alternatives
                BookingDaoImpl bookingDao = new BookingDaoImpl();
                List<String> alternativeSlots = bookingDao.getAlternativeTimeSlots(cabinId, date, timeSlot, 3);

                StringBuilder alternatives = new StringBuilder();
                for (int i = 0; i < alternativeSlots.size(); i++) {
                    if (i > 0) alternatives.append(", ");
                    alternatives.append("\"").append(alternativeSlots.get(i)).append("\"");
                }

                response.getWriter().write("{\"available\": false, \"alternatives\": [" + alternatives + "]}");
            }

        } catch (Exception e) {
            System.err.println("❌ Error checking availability: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("{\"available\": false, \"error\": \"Server error: " + e.getMessage() + "\"}");
        }
    }



    // ✅ UPDATED: Single company my bookings
    private void showMyBookings(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("📋 Loading bookings for user: " + user.getName());

        try {
            List<Booking> userBookings = bookingService.getUserBookings(user.getUserId());

            List<Booking> pendingBookings = userBookings.stream()
                    .filter(booking -> booking.getStatus() == Booking.Status.PENDING)
                    .collect(Collectors.toList());

            List<Booking> approvedBookings = userBookings.stream()
                    .filter(booking -> booking.getStatus() == Booking.Status.APPROVED)
                    .collect(Collectors.toList());

            List<Booking> rejectedBookings = userBookings.stream()
                    .filter(booking -> booking.getStatus() == Booking.Status.REJECTED)
                    .collect(Collectors.toList());

            // ✅ SIMPLIFIED: Without AI recommendations
            List<Cabin> recommendedCabins = cabinDAO.getVIPOnlyCabins();
            if (recommendedCabins.size() > 3) {
                recommendedCabins = recommendedCabins.subList(0, 3);
            }

            // ✅ SIMPLIFIED: Basic booking score
            double bookingScore = calculateBasicBookingScore(userBookings);

            request.setAttribute("user", user);
            request.setAttribute("allBookings", userBookings);
            request.setAttribute("pendingBookings", pendingBookings);
            request.setAttribute("approvedBookings", approvedBookings);
            request.setAttribute("rejectedBookings", rejectedBookings);
            request.setAttribute("recommendedCabins", recommendedCabins);
            request.setAttribute("bookingScore", Math.round(bookingScore));
            request.setAttribute("totalBookings", userBookings.size());

            System.out.println("✅ Bookings loaded successfully");
            System.out.println("   - Total bookings: " + userBookings.size());
            System.out.println("   - Pending: " + pendingBookings.size());
            System.out.println("   - Approved: " + approvedBookings.size());
            System.out.println("   - Rejected: " + rejectedBookings.size());

            request.getRequestDispatcher("/user/my-bookings.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error loading bookings: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading your bookings. Please try again.");
            request.getRequestDispatcher("/common/error.jsp").forward(request, response);
        }
    }

    private void cancelBooking(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("🚫 Processing booking cancellation for user: " + user.getName());

        String bookingIdStr = request.getParameter("bookingId");

        try {
            if (bookingIdStr == null || bookingIdStr.trim().isEmpty()) {
                System.err.println("❌ No booking ID provided for cancellation");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Booking ID is required");
                return;
            }

            int bookingId = Integer.parseInt(bookingIdStr);

            Booking booking = bookingService.getBookingById(bookingId);
            if (booking == null) {
                System.err.println("❌ Booking not found: " + bookingId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Booking not found");
                return;
            }

            if (booking.getUserId() != user.getUserId() && !user.isAdmin()) {
                System.err.println("❌ Unauthorized cancellation attempt: " + bookingId + " by user: " + user.getUserId());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
                return;
            }

            if (booking.getStatus() != Booking.Status.PENDING) {
                System.err.println("❌ Cannot cancel non-pending booking: " + bookingId + " (Status: " + booking.getStatus() + ")");
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Only pending bookings can be cancelled");
                response.sendRedirect(request.getContextPath() + "/mybookings");
                return;
            }

            boolean cancellationSuccess = bookingService.cancelBooking(bookingId, user);

            HttpSession session = request.getSession();
            if (cancellationSuccess) {
                System.out.println("✅ Booking cancelled successfully: " + bookingId);
                session.setAttribute("successMessage", "Booking cancelled successfully");
            } else {
                System.err.println("❌ Booking cancellation failed: " + bookingId);
                session.setAttribute("errorMessage", "Failed to cancel booking. Please try again.");
            }

            response.sendRedirect(request.getContextPath() + "/mybookings");

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid booking ID format: " + bookingIdStr);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid booking ID");
        } catch (Exception e) {
            System.err.println("❌ Error cancelling booking: " + e.getMessage());
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Error cancelling booking. Please try again.");
            response.sendRedirect(request.getContextPath() + "/mybookings");
        }
    }

    private void getAlternatives(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("🔄 Getting alternatives for user: " + user.getName());

        String cabinIdStr = request.getParameter("cabinId");
        String dateStr = request.getParameter("date");
        String timeSlot = request.getParameter("timeSlot");

        response.setContentType("application/json");

        try {
            int cabinId = Integer.parseInt(cabinIdStr);
            Date date = Date.valueOf(dateStr);

            BookingDaoImpl bookingDao = new BookingDaoImpl();
            List<String> alternativeSlots = bookingDao.getAlternativeTimeSlots(cabinId, date, timeSlot, 5);

            StringBuilder jsonResponse = new StringBuilder();
            jsonResponse.append("{\"timeSlots\": [");
            for (int i = 0; i < alternativeSlots.size(); i++) {
                if (i > 0) jsonResponse.append(", ");
                jsonResponse.append("\"").append(alternativeSlots.get(i)).append("\"");
            }
            jsonResponse.append("]}");

            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            System.err.println("❌ Error getting alternatives: " + e.getMessage());
            response.getWriter().write("{\"error\": \"Failed to get alternatives\"}");
        }
    }

    // ================================
    // UTILITY METHODS
    // ================================

    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }

    private String getActionFromRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String action = requestURI.substring(contextPath.length());

        if (action.startsWith("/")) {
            action = action.substring(1);
        }

        String queryAction = request.getParameter("action");
        if (queryAction != null && !queryAction.trim().isEmpty()) {
            return queryAction;
        }

        if (action.contains("/")) {
            String[] parts = action.split("/");
            if (parts.length > 1 && !parts[1].isEmpty()) {
                return parts[1];
            }
            return parts[0];
        }

        return action.isEmpty() ? "mybookings" : action;
    }

    private String generateTimeSlot(String startTime, int durationMinutes) {
        try {
            String[] parts = startTime.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            int totalMinutes = hours * 60 + minutes + durationMinutes;
            int endHours = totalMinutes / 60;
            int endMins = totalMinutes % 60;

            if (endHours > 18) {
                return null; // Beyond business hours
            }

            String endTime = String.format("%02d:%02d", endHours, endMins);
            return startTime + "-" + endTime;

        } catch (Exception e) {
            System.err.println("❌ Error generating time slot: " + e.getMessage());
            return null;
        }
    }

    private List<String> generateStartTimeOptions() {
        List<String> startTimes = new ArrayList<>();

        for (int hour = 9; hour < 18; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                startTimes.add(String.format("%02d:%02d", hour, minute));
            }
        }

        return startTimes;
    }

    // ✅ NEW: Common booking purposes (replacement for AI suggestions)
    private List<String> getCommonBookingPurposes() {
        List<String> purposes = new ArrayList<>();
        purposes.add("Team Meeting");
        purposes.add("Client Presentation");
        purposes.add("Training Session");
        purposes.add("Project Discussion");
        purposes.add("Interview");
        purposes.add("Workshop");
        purposes.add("Conference Call");
        purposes.add("Strategy Planning");
        return purposes;
    }

    // ✅ NEW: Basic booking score calculation (replacement for AI)
    private double calculateBasicBookingScore(List<Booking> bookings) {
        if (bookings.isEmpty()) return 0.0;

        long approvedBookings = bookings.stream()
                .filter(b -> b.getStatus() == Booking.Status.APPROVED)
                .count();

        double approvalRate = (double) approvedBookings / bookings.size();
        return Math.min(100.0, approvalRate * 100.0);
    }

    private String validateBookingInput(String cabinIdStr, String bookingDateStr, String timeSlot, String purpose) {
        if (cabinIdStr == null || cabinIdStr.trim().isEmpty()) {
            return "Please select a cabin";
        }

        try {
            Integer.parseInt(cabinIdStr);
        } catch (NumberFormatException e) {
            return "Invalid cabin selection";
        }

        if (bookingDateStr == null || bookingDateStr.trim().isEmpty()) {
            return "Please select a booking date";
        }

        try {
            Date bookingDate = Date.valueOf(bookingDateStr);
            Date today = new Date(System.currentTimeMillis());
            if (bookingDate.before(today)) {
                return "Cannot book for past dates";
            }
        } catch (IllegalArgumentException e) {
            return "Invalid date format";
        }

        if (timeSlot == null || timeSlot.trim().isEmpty()) {
            return "Please select a time slot";
        }

        if (purpose == null || purpose.trim().isEmpty()) {
            return "Please enter the purpose of booking";
        }

        if (purpose.length() > 500) {
            return "Purpose cannot exceed 500 characters";
        }

        return null;
    }
    private void checkMultiDayAvailability(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("🔍 Checking multi-day availability for AJAX request");

        String cabinIdStr = request.getParameter("cabinId");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (cabinIdStr == null || cabinIdStr.trim().isEmpty() ||
                    startDateStr == null || startDateStr.trim().isEmpty() ||
                    endDateStr == null || endDateStr.trim().isEmpty()) {

                System.err.println("❌ Missing parameters in multi-day availability check");
                response.getWriter().write("{\"available\": false, \"error\": \"Missing parameters\"}");
                return;
            }

            int cabinId = Integer.parseInt(cabinIdStr);
            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);

            // Check if date range is valid (max 30 days)
            long diffInMillies = endDate.getTime() - startDate.getTime();
            long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);

            if (diffInDays > 30) {
                response.getWriter().write("{\"available\": false, \"error\": \"Maximum 30 days allowed\"}");
                return;
            }

            if (diffInDays < 0) {
                response.getWriter().write("{\"available\": false, \"error\": \"End date must be after start date\"}");
                return;
            }

            // Check availability for each day in the range
            List<String> conflictDays = new ArrayList<>();
            Date currentDate = new Date(startDate.getTime());

            while (!currentDate.after(endDate)) {
                if (!bookingService.isSlotAvailable(cabinId, currentDate, "09:00-18:00")) {
                    conflictDays.add(currentDate.toString());
                }
                // Move to next day
                currentDate = new Date(currentDate.getTime() + (1000 * 60 * 60 * 24));
            }

            if (conflictDays.isEmpty()) {
                response.getWriter().write("{\"available\": true}");
            } else {
                StringBuilder conflicts = new StringBuilder();
                for (int i = 0; i < conflictDays.size(); i++) {
                    if (i > 0) conflicts.append(", ");
                    conflicts.append("\"").append(conflictDays.get(i)).append("\"");
                }
                response.getWriter().write("{\"available\": false, \"conflictDays\": [" + conflicts + "]}");
            }

        } catch (Exception e) {
            System.err.println("❌ Error checking multi-day availability: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("{\"available\": false, \"error\": \"Server error: " + e.getMessage() + "\"}");
        }
    }

}
