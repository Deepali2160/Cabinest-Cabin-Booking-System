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

@WebServlet(urlPatterns = {
        "/admin/add-cabin",           // Cabin creation
        "/admin/manage-cabins",       // Cabin management
        "/admin/edit-cabin",          // Cabin editing
        "/admin/delete-cabin",        // Cabin deletion
        "/admin/update-cabin-status", // ✅ ADDED: Status update
        "/admin/cabins"               // ✅ ADDED: Alternative cabin route
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
                case "cabins":
                    showCabinManagement(request, response);
                    break;
                case "edit-cabin":
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

        // ✅ ENHANCED: Better action detection
        String formAction = request.getParameter("action");
        if (formAction != null && !formAction.trim().isEmpty()) {
            action = formAction;
        }

        try {
            switch (action) {
                case "add-cabin":
                case "add":
                    processAddCabin(request, response);
                    break;
                case "edit-cabin":
                case "edit":
                    processEditCabin(request, response);
                    break;
                case "delete-cabin":
                case "delete":
                    processDeleteCabin(request, response);
                    break;
                case "update-cabin-status":
                case "status":
                case "updateStatus":
                    processStatusUpdate(request, response);
                    break;
                default:
                    System.err.println("❌ Unknown action: " + action);
                    setErrorMessage(request, "Invalid action: " + action);
                    response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing cabin POST request: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Error processing request", e);
        }
    }

    // ==================== GET REQUEST HANDLERS ====================

    private void showAddCabinForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("📝 Showing add cabin form");

        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
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

    private void showCabinManagement(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("📋 Showing cabin management dashboard");

        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String statusFilter = request.getParameter("status");
            List<Cabin> cabins = cabinService.getAllCabinsForAdmin();

            // Filter by status if provided
            if (statusFilter != null && !statusFilter.isEmpty()) {
                try {
                    Cabin.Status status = Cabin.Status.valueOf(statusFilter.toUpperCase());
                    cabins = cabins.stream()
                            .filter(cabin -> cabin.getStatus() == status)
                            .collect(Collectors.toList());
                    System.out.println("📋 Filtered cabins by status: " + status);
                } catch (IllegalArgumentException e) {
                    System.err.println("❌ Invalid status filter: " + statusFilter);
                }
            }

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

            Company company = companyService.getCompanyConfig();

            request.setAttribute("admin", currentUser);
            request.setAttribute("cabin", cabin);
            request.setAttribute("company", company);

            System.out.println("✅ Edit form loaded for cabin: " + cabin.getName());
            request.getRequestDispatcher("/admin/edit-cabin.jsp").forward(request, response);

        } catch (Exception e) {
            // ✅ FIXED: Single catch block for all exceptions
            System.err.println("❌ Error loading edit cabin form: " + e.getMessage());
            setErrorMessage(request, "Error loading edit form. Please check the cabin ID.");
            response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
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

            List<Cabin> similarCabins = cabinService.getSimilarCabins(cabinId);

            User currentUser = getCurrentUser(request);
            request.setAttribute("cabin", cabin);
            request.setAttribute("similarCabins", similarCabins);
            request.setAttribute("user", currentUser);

            System.out.println("✅ Cabin details loaded: " + cabin.getName());
            request.getRequestDispatcher("/cabin/details.jsp").forward(request, response);

        } catch (Exception e) {
            // ✅ FIXED: Single catch block
            System.err.println("❌ Error loading cabin details: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid cabin ID or cabin not found");
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

            Cabin cabin = new Cabin(name.trim(), capacity,
                    amenities != null ? amenities.trim() : "",
                    isVipOnly, location.trim());

            boolean success = cabinService.addCabin(cabin);

            if (success) {
                setSuccessMessage(request, "Cabin '" + cabin.getName() + "' added successfully!");
                System.out.println("✅ Cabin added successfully: " + cabin.getName() + " (ID: " + cabin.getCabinId() + ")");
                response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
            } else {
                setErrorMessage(request, "Failed to add cabin. Please try again.");
                response.sendRedirect(request.getContextPath() + "/admin/add-cabin");
            }

        } catch (Exception e) {
            // ✅ FIXED: Single catch block for all exceptions
            System.err.println("❌ Error processing add cabin: " + e.getMessage());
            setErrorMessage(request, "Error adding cabin. Please check all input values.");
            response.sendRedirect(request.getContextPath() + "/admin/add-cabin");
        }
    }

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

            boolean success = cabinService.updateCabin(cabin);

            if (success) {
                setSuccessMessage(request, "Cabin '" + cabin.getName() + "' updated successfully!");
                System.out.println("✅ Cabin updated successfully: " + cabin.getName());
            } else {
                setErrorMessage(request, "Failed to update cabin. Please try again.");
            }

            response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");

        } catch (Exception e) {
            // ✅ FIXED: Single catch block for all exceptions
            System.err.println("❌ Error processing edit cabin: " + e.getMessage());
            setErrorMessage(request, "Error updating cabin. Please check all input values.");
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

            boolean success = cabinService.deleteCabin(cabinId);

            if (success) {
                setSuccessMessage(request, "Cabin '" + cabinName + "' deleted successfully!");
                System.out.println("✅ Cabin deleted successfully: " + cabinName);
            } else {
                setErrorMessage(request, "Failed to delete cabin. It may have active bookings.");
            }

            response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");

        } catch (Exception e) {
            // ✅ FIXED: Single catch block for all exceptions
            System.err.println("❌ Error processing delete cabin: " + e.getMessage());
            setErrorMessage(request, "Error deleting cabin. Please check the cabin ID.");
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

            System.out.println("📋 Status update parameters - cabinId: " + cabinIdStr + ", status: " + statusStr);

            if (cabinIdStr == null || statusStr == null) {
                setErrorMessage(request, "Cabin ID and status are required");
                response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");
                return;
            }

            int cabinId = Integer.parseInt(cabinIdStr);
            Cabin.Status status = Cabin.Status.valueOf(statusStr.toUpperCase());

            // Get cabin for logging
            Cabin cabin = cabinService.getCabinById(cabinId);
            String cabinName = cabin != null ? cabin.getName() : "Cabin #" + cabinId;

            boolean success = cabinService.updateCabinStatus(cabinId, status);

            if (success) {
                setSuccessMessage(request, "Cabin '" + cabinName + "' status updated to " + status + " successfully!");
                System.out.println("✅ Cabin status updated: " + cabinName + " to " + status);
            } else {
                setErrorMessage(request, "Failed to update cabin status. Please try again.");
                System.err.println("❌ Failed to update cabin status for: " + cabinName);
            }

            response.sendRedirect(request.getContextPath() + "/admin/manage-cabins");

        } catch (Exception e) {
            // ✅ FIXED: Single catch block for all exceptions
            System.err.println("❌ Error processing status update: " + e.getMessage());
            setErrorMessage(request, "Error updating cabin status. Please check the parameters.");
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

            System.out.println("🔍 Processing path: " + path);

            // Handle specific admin cabin routes
            if (path.equals("/admin/update-cabin-status")) {
                return "update-cabin-status";
            } else if (path.equals("/admin/edit-cabin")) {
                return "edit-cabin";
            } else if (path.equals("/admin/delete-cabin")) {
                return "delete-cabin";
            } else if (path.equals("/admin/add-cabin")) {
                return "add-cabin";
            } else if (path.equals("/admin/manage-cabins") || path.equals("/admin/cabins")) {
                return "manage-cabins";
            } else if (path.startsWith("/admin/cabin/")) {
                return path.substring(13); // Remove "/admin/cabin/" prefix
            } else if (path.startsWith("/admin/")) {
                return path.substring(7); // Remove "/admin/" prefix
            } else if (path.startsWith("/cabin/")) {
                return path.substring(7); // Remove "/cabin/" prefix
            }

            return path.isEmpty() ? "manage-cabins" : path.substring(1);

        } catch (Exception e) {
            System.err.println("❌ Error extracting action: " + e.getMessage());
            return "manage-cabins";
        }
    }

    private void setSuccessMessage(HttpServletRequest request, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("successMessage", message);
        System.out.println("✅ Success message set: " + message);
    }

    private void setErrorMessage(HttpServletRequest request, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("errorMessage", message);
        System.out.println("❌ Error message set: " + message);
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
