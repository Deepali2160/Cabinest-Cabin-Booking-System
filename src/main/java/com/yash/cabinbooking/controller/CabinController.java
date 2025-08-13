package com.yash.cabinbooking.controller;

import com.yash.cabinbooking.service.CabinService;
import com.yash.cabinbooking.service.CompanyService;
import com.yash.cabinbooking.serviceimpl.CabinServiceImpl;
import com.yash.cabinbooking.serviceimpl.CompanyServiceImpl;
import com.yash.cabinbooking.model.Cabin;
import com.yash.cabinbooking.model.User;
import com.yash.cabinbooking.model.Company;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CABIN CONTROLLER - SINGLE COMPANY VERSION
 *
 * Modified for Yash Technology single company usage
 * - Removed multi-company complexity
 * - Simplified cabin management
 * - Single organization focus
 * - Updated to match new service layer
 */
@WebServlet(urlPatterns = {
        "/admin/add-cabin",        // Cabin creation
        "/admin/manage-cabins",    // Cabin management
        "/admin/edit-cabin",       // Cabin editing
        "/admin/delete-cabin"      // Cabin deletion
})
public class CabinController extends HttpServlet {

    private CabinService cabinService;
    private CompanyService companyService;

    @Override
    public void init() throws ServletException {
        this.cabinService = new CabinServiceImpl();
        this.companyService = new CompanyServiceImpl();
        System.out.println("🔧 CabinController initialized for Yash Technology (Single Company)");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getActionFromRequest(request);
        System.out.println("🌐 Cabin GET Request: " + action);

        try {
            switch (action) {
                case "add-cabin":
                    showAddCabinForm(request, response);
                    break;
                case "manage-cabins":
                    showCabinManagement(request, response);
                    break;
                case "edit":
                    showEditCabinForm(request, response);
                    break;
                case "details":
                    showCabinDetails(request, response);
                    break;
                case "search":
                    searchCabins(request, response);
                    break;
                default:
                    showCabinManagement(request, response);
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing cabin GET request: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error processing request", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getActionFromRequest(request);
        System.out.println("📝 Cabin POST Request: " + action);

        try {
            switch (action) {
                case "add-cabin":
                    processAddCabin(request, response);
                    break;
                case "edit":
                    processEditCabin(request, response);
                    break;
                case "delete":
                    processDeleteCabin(request, response);
                    break;
                case "status":
                    processStatusUpdate(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing cabin POST request: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error processing request", e);
        }
    }

    // ==================== GET REQUEST HANDLERS ====================

    // ✅ UPDATED: Single company add cabin form
    private void showAddCabinForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("📝 Showing add cabin form");

        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // ✅ SINGLE COMPANY: Get company configuration instead of all companies
            Company company = companyService.getCompanyConfig();

            request.setAttribute("company", company);
            request.setAttribute("admin", currentUser);

            System.out.println("✅ Add cabin form loaded for " + (company != null ? company.getName() : "Yash Technology"));
            request.getRequestDispatcher("/admin/add-cabin.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error loading add cabin form: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error loading add cabin form", e);
        }
    }

    // ✅ UPDATED: Single company cabin management
    private void showCabinManagement(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("📋 Showing cabin management dashboard");

        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // ✅ SINGLE COMPANY: No company filtering needed
            String statusFilter = request.getParameter("status");

            List<Cabin> cabins = cabinService.getAllCabinsForAdmin();

            // Filter by status if provided
            if (statusFilter != null && !statusFilter.isEmpty()) {
                Cabin.Status status = Cabin.Status.valueOf(statusFilter.toUpperCase());
                cabins = cabins.stream()
                        .filter(cabin -> cabin.getStatus() == status)
                        .collect(Collectors.toList());
                System.out.println("📋 Filtered cabins by status: " + status);
            }

            // ✅ SINGLE COMPANY: Get company config for display
            Company company = companyService.getCompanyConfig();

            // Cabin statistics
            int totalCabins = cabins.size();
            int activeCabins = cabinService.getActiveCabinCount();
            List<Cabin> vipCabins = cabinService.getVIPCabins();

            request.setAttribute("admin", currentUser);
            request.setAttribute("cabins", cabins);
            request.setAttribute("company", company);
            request.setAttribute("selectedStatus", statusFilter);
            request.setAttribute("totalCabins", totalCabins);
            request.setAttribute("activeCabins", activeCabins);
            request.setAttribute("vipCabins", vipCabins.size());

            System.out.println("✅ Cabin management loaded with " + cabins.size() + " cabins");
            request.getRequestDispatcher("/admin/manage-cabins.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error loading cabin management: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error loading cabin management", e);
        }
    }

    // ✅ UPDATED: Single company edit cabin form
    private void showEditCabinForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("✏️ Showing edit cabin form");

        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String cabinIdStr = request.getParameter("cabinId");
            if (cabinIdStr == null || cabinIdStr.trim().isEmpty()) {
                setErrorMessage(request, "Cabin ID is required");
                response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
                return;
            }

            int cabinId = Integer.parseInt(cabinIdStr);
            Cabin cabin = cabinService.getCabinById(cabinId);

            if (cabin == null) {
                setErrorMessage(request, "Cabin not found");
                response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
                return;
            }

            // ✅ SINGLE COMPANY: Get company config instead of all companies
            Company company = companyService.getCompanyConfig();

            request.setAttribute("admin", currentUser);
            request.setAttribute("cabin", cabin);
            request.setAttribute("company", company);

            System.out.println("✅ Edit form loaded for cabin: " + cabin.getName());
            request.getRequestDispatcher("/admin/edit-cabin.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid cabin ID");
            response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
        } catch (Exception e) {
            System.err.println("❌ Error loading edit cabin form: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error loading edit form", e);
        }
    }

    private void showCabinDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("👁️ Showing cabin details");

        try {
            String cabinIdStr = request.getParameter("cabinId");
            if (cabinIdStr == null || cabinIdStr.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cabin ID is required");
                return;
            }

            int cabinId = Integer.parseInt(cabinIdStr);
            Cabin cabin = cabinService.getCabinById(cabinId);

            if (cabin == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Cabin not found");
                return;
            }

            // Get similar cabins for recommendations
            List<Cabin> similarCabins = cabinService.getSimilarCabins(cabinId);

            User currentUser = getCurrentUser(request);
            request.setAttribute("cabin", cabin);
            request.setAttribute("similarCabins", similarCabins);
            request.setAttribute("user", currentUser);

            System.out.println("✅ Cabin details loaded: " + cabin.getName());
            request.getRequestDispatcher("/cabin/details.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid cabin ID");
        } catch (Exception e) {
            System.err.println("❌ Error loading cabin details: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error loading cabin details", e);
        }
    }

    private void searchCabins(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("🔍 Processing cabin search");

        try {
            String searchType = request.getParameter("type");
            String searchValue = request.getParameter("value");

            List<Cabin> cabins = new java.util.ArrayList<>();

            if (searchType != null && searchValue != null && !searchValue.trim().isEmpty()) {
                switch (searchType) {
                    case "capacity":
                        int capacity = Integer.parseInt(searchValue);
                        cabins = cabinService.searchCabinsByCapacity(capacity - 2, capacity + 2);
                        break;
                    case "amenities":
                        cabins = cabinService.searchCabinsByAmenities(searchValue);
                        break;
                    case "location":
                        cabins = cabinService.getCabinsByLocation(searchValue);
                        break;
                    default:
                        System.err.println("❌ Invalid search type: " + searchType);
                        break;
                }
            }

            request.setAttribute("cabins", cabins);
            request.setAttribute("searchType", searchType);
            request.setAttribute("searchValue", searchValue);
            request.setAttribute("resultCount", cabins.size());

            System.out.println("✅ Search completed: " + cabins.size() + " results");
            request.getRequestDispatcher("/cabin/search-results.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error processing cabin search: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error processing search", e);
        }
    }

    // ==================== POST REQUEST HANDLERS ====================

    // ✅ UPDATED: Single company add cabin process
    private void processAddCabin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("➕ Processing add cabin request");

        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String name = request.getParameter("name");
            String capacityStr = request.getParameter("capacity");
            String amenities = request.getParameter("amenities");
            String location = request.getParameter("location");
            String isVipOnlyStr = request.getParameter("isVipOnly");

            // Basic validation
            if (name == null || name.trim().isEmpty()) {
                setErrorMessage(request, "Cabin name is required");
                response.sendRedirect(request.getContextPath() + "/admin/add-cabin");
                return;
            }

            if (capacityStr == null || capacityStr.trim().isEmpty()) {
                setErrorMessage(request, "Capacity is required");
                response.sendRedirect(request.getContextPath() + "/admin/add-cabin");
                return;
            }

            if (location == null || location.trim().isEmpty()) {
                setErrorMessage(request, "Location is required");
                response.sendRedirect(request.getContextPath() + "/admin/add-cabin");
                return;
            }

            // Parse values
            int capacity = Integer.parseInt(capacityStr);
            boolean isVipOnly = "true".equals(isVipOnlyStr) || "on".equals(isVipOnlyStr);

            // ✅ SINGLE COMPANY: Create cabin without company ID parameter
            Cabin cabin = new Cabin(name.trim(), capacity,
                    amenities != null ? amenities.trim() : "",
                    isVipOnly, location.trim());

            // Add cabin using service
            boolean success = cabinService.addCabin(cabin);

            if (success) {
                setSuccessMessage(request, "Cabin '" + cabin.getName() + "' added successfully!");
                System.out.println("✅ Cabin added successfully: " + cabin.getName() + " (ID: " + cabin.getCabinId() + ")");
                response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
            } else {
                setErrorMessage(request, "Failed to add cabin. Please try again.");
                response.sendRedirect(request.getContextPath() + "/admin/add-cabin");
            }

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid numeric input in add cabin");
            setErrorMessage(request, "Please enter valid numeric values for capacity");
            response.sendRedirect(request.getContextPath() + "/admin/add-cabin");
        } catch (Exception e) {
            System.err.println("❌ Error processing add cabin: " + e.getMessage());
            e.printStackTrace();
            setErrorMessage(request, "Error adding cabin. Please try again.");
            response.sendRedirect(request.getContextPath() + "/admin/add-cabin");
        }
    }

    // ✅ UPDATED: Single company edit cabin process
    private void processEditCabin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("✏️ Processing edit cabin request");

        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String cabinIdStr = request.getParameter("cabinId");
            String name = request.getParameter("name");
            String capacityStr = request.getParameter("capacity");
            String amenities = request.getParameter("amenities");
            String location = request.getParameter("location");
            String isVipOnlyStr = request.getParameter("isVipOnly");
            String statusStr = request.getParameter("status");

            // Basic validation
            if (cabinIdStr == null || name == null || capacityStr == null ||
                    location == null || statusStr == null) {
                setErrorMessage(request, "All required fields must be provided");
                response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
                return;
            }

            // Parse values
            int cabinId = Integer.parseInt(cabinIdStr);
            int capacity = Integer.parseInt(capacityStr);
            boolean isVipOnly = "true".equals(isVipOnlyStr) || "on".equals(isVipOnlyStr);
            Cabin.Status status = Cabin.Status.valueOf(statusStr.toUpperCase());

            // Get existing cabin
            Cabin cabin = cabinService.getCabinById(cabinId);
            if (cabin == null) {
                setErrorMessage(request, "Cabin not found");
                response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
                return;
            }

            // Update cabin data
            cabin.setName(name.trim());
            cabin.setCapacity(capacity);
            cabin.setAmenities(amenities != null ? amenities.trim() : "");
            cabin.setLocation(location.trim());
            cabin.setVipOnly(isVipOnly);
            cabin.setStatus(status);
            // ✅ SINGLE COMPANY: Company ID remains default (1)

            // Update cabin using service
            boolean success = cabinService.updateCabin(cabin);

            if (success) {
                setSuccessMessage(request, "Cabin '" + cabin.getName() + "' updated successfully!");
                System.out.println("✅ Cabin updated successfully: " + cabin.getName());
            } else {
                setErrorMessage(request, "Failed to update cabin. Please try again.");
            }

            response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");

        } catch (Exception e) {
            System.err.println("❌ Error processing edit cabin: " + e.getMessage());
            e.printStackTrace();
            setErrorMessage(request, "Error updating cabin. Please try again.");
            response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
        }
    }

    private void processDeleteCabin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("🗑️ Processing delete cabin request");

        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String cabinIdStr = request.getParameter("cabinId");
            if (cabinIdStr == null || cabinIdStr.trim().isEmpty()) {
                setErrorMessage(request, "Cabin ID is required");
                response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
                return;
            }

            int cabinId = Integer.parseInt(cabinIdStr);

            // Get cabin name for success message
            Cabin cabin = cabinService.getCabinById(cabinId);
            String cabinName = cabin != null ? cabin.getName() : "Cabin #" + cabinId;

            // Delete cabin using service
            boolean success = cabinService.deleteCabin(cabinId);

            if (success) {
                setSuccessMessage(request, "Cabin '" + cabinName + "' deleted successfully!");
                System.out.println("✅ Cabin deleted successfully: " + cabinName);
            } else {
                setErrorMessage(request, "Failed to delete cabin. It may have active bookings.");
            }

            response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");

        } catch (Exception e) {
            System.err.println("❌ Error processing delete cabin: " + e.getMessage());
            e.printStackTrace();
            setErrorMessage(request, "Error deleting cabin. Please try again.");
            response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
        }
    }

    private void processStatusUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("🔄 Processing cabin status update");

        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String cabinIdStr = request.getParameter("cabinId");
            String statusStr = request.getParameter("status");

            if (cabinIdStr == null || statusStr == null) {
                setErrorMessage(request, "Cabin ID and status are required");
                response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
                return;
            }

            int cabinId = Integer.parseInt(cabinIdStr);
            Cabin.Status status = Cabin.Status.valueOf(statusStr.toUpperCase());

            // Update status using service
            boolean success = cabinService.updateCabinStatus(cabinId, status);

            if (success) {
                setSuccessMessage(request, "Cabin status updated to " + status + " successfully!");
                System.out.println("✅ Cabin status updated: " + cabinId + " to " + status);
            } else {
                setErrorMessage(request, "Failed to update cabin status. Please try again.");
            }

            response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");

        } catch (Exception e) {
            System.err.println("❌ Error processing status update: " + e.getMessage());
            e.printStackTrace();
            setErrorMessage(request, "Error updating cabin status. Please try again.");
            response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
        }
    }

    // ==================== UTILITY METHODS ====================

    private User getCurrentUser(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                return (User) session.getAttribute("user");
            }
        } catch (Exception e) {
            System.err.println("❌ Error getting current user: " + e.getMessage());
        }
        return null;
    }

    private String getActionFromRequest(HttpServletRequest request) {
        try {
            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();
            String path = requestURI.substring(contextPath.length());

            if (path.startsWith("/admin/cabin/")) {
                return path.substring(13); // Remove "/admin/cabin/" prefix
            } else if (path.startsWith("/admin/")) {
                return path.substring(7); // Remove "/admin/" prefix
            } else if (path.startsWith("/cabin/")) {
                return path.substring(7); // Remove "/cabin/" prefix
            }

            return path.isEmpty() ? "manage-cabins" : path;

        } catch (Exception e) {
            System.err.println("❌ Error extracting action: " + e.getMessage());
            return "manage-cabins";
        }
    }

    private void setSuccessMessage(HttpServletRequest request, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("successMessage", message);
    }

    private void setErrorMessage(HttpServletRequest request, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("errorMessage", message);
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response,
                             String message, Exception e) throws ServletException, IOException {
        try {
            setErrorMessage(request, message);
            response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        }
    }
}
