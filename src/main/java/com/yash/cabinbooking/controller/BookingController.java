package com.yash.cabinbooking.controller;

import com.yash.cabinbooking.dao.CabinDao;
import com.yash.cabinbooking.daoimpl.CabinDaoImpl;
import com.yash.cabinbooking.daoimpl.BookingDaoImpl;
import com.yash.cabinbooking.service.BookingService;
import com.yash.cabinbooking.service.UserService;
import com.yash.cabinbooking.service.CompanyService;
import com.yash.cabinbooking.service.AIRecommendationService;
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

/**
 * BOOKING CONTROLLER - FLEXIBLE DURATION SYSTEM
 */
@WebServlet(name = "BookingController", urlPatterns = {"/booking/*", "/book", "/mybookings"})
public class BookingController extends HttpServlet {

    private BookingService bookingService;
    private UserService userService;
    private CompanyService companyService;
    private AIRecommendationService aiService;
    private CabinDao cabinDAO;

    @Override
    public void init() throws ServletException {
        this.bookingService = new BookingServiceImpl();
        this.userService = new UserServiceImpl();
        this.companyService = new CompanyServiceImpl();
        this.aiService = new AIRecommendationServiceImpl();
        this.cabinDAO = new CabinDaoImpl();
        System.out.println("🔧 BookingController initialized successfully");
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
            case "availability":
                checkAvailability(request, response, currentUser);
                break;
            default:
                showMyBookings(request, response, currentUser);
                break;
        }
    }

    private void showBookingForm(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("📝 Showing booking form for user: " + user.getName());

        try {
            String cabinIdStr = request.getParameter("cabinId");
            String companyIdStr = request.getParameter("companyId");

            int companyId = user.getDefaultCompanyId();
            if (companyIdStr != null && !companyIdStr.trim().isEmpty()) {
                try {
                    companyId = Integer.parseInt(companyIdStr);
                } catch (NumberFormatException e) {
                    System.err.println("❌ Invalid company ID: " + companyIdStr);
                }
            }

            Company company = companyService.getCompanyById(companyId);
            if (company == null) {
                System.err.println("❌ Company not found: " + companyId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Company not found");
                return;
            }

            List<Cabin> accessibleCabins = cabinDAO.getAccessibleCabins(companyId, user);
            List<Cabin> recommendedCabins = aiService.getRecommendedCabinsForUser(user, companyId);
            List<String> popularTimeSlots = aiService.getPopularTimeSlots();
            List<String> suggestedPurposes = aiService.suggestBookingPurposes(user);

            // ✅ NEW: Add duration options and start times
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
            System.out.println("   - AI recommendations: " + recommendedCabins.size());
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

            if (!bookingService.isSlotAvailable(cabinId, bookingDate, timeSlot)) {
                System.err.println("❌ Time slot not available: " + timeSlot);

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

    // ✅ ENHANCED: Flexible duration availability checking
    private void checkAvailability(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("🔍 Checking availability for AJAX request");

        String cabinIdStr = request.getParameter("cabinId");
        String dateStr = request.getParameter("date");
        String startTime = request.getParameter("startTime");
        String durationStr = request.getParameter("duration");

        response.setContentType("application/json");

        try {
            // ✅ FIX: Validate all parameters
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

            // ✅ Generate time slot from start time + duration
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

    private void showMyBookings(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        System.out.println("📋 Loading bookings for user: " + user.getName());

        try {
            List<Booking> userBookings = bookingService.getUserBookings(user.getUserId());

            List<Booking> pendingBookings = userBookings.stream()
                    .filter(booking -> booking.getStatus() == Booking.Status.PENDING)
                    .collect(java.util.stream.Collectors.toList());

            List<Booking> approvedBookings = userBookings.stream()
                    .filter(booking -> booking.getStatus() == Booking.Status.APPROVED)
                    .collect(java.util.stream.Collectors.toList());

            List<Booking> rejectedBookings = userBookings.stream()
                    .filter(booking -> booking.getStatus() == Booking.Status.REJECTED)
                    .collect(java.util.stream.Collectors.toList());

            List<Cabin> recommendedCabins = aiService.getRecommendedCabinsForUser(user, user.getDefaultCompanyId());
            double bookingScore = aiService.calculateUserBookingScore(user);

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
        System.out.println("🔄 Getting AI alternatives for user: " + user.getName());

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

    // UTILITY METHODS

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

    // ✅ NEW: Generate time slot from start time + duration
    private String generateTimeSlot(String startTime, int durationMinutes) {
        try {
            String[] parts = startTime.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            // Calculate end time
            int totalMinutes = hours * 60 + minutes + durationMinutes;
            int endHours = totalMinutes / 60;
            int endMins = totalMinutes % 60;

            // Check if within business hours (9 AM to 6 PM)
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

    // ✅ NEW: Generate start time options with 15-minute intervals
    private List<String> generateStartTimeOptions() {
        List<String> startTimes = new ArrayList<>();

        for (int hour = 9; hour < 18; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                startTimes.add(String.format("%02d:%02d", hour, minute));
            }
        }

        return startTimes;
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
}
