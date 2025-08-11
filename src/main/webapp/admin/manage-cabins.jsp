<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Cabins - Admin Panel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <!-- Admin Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="fas fa-cogs"></i> Admin Panel
            </a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text text-light me-3">
                    <i class="fas fa-user-shield"></i> ${admin.name}
                </span>
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fas fa-tachometer-alt"></i> Dashboard
                </a>
            </div>
        </div>
    </nav>

    <div class="container-fluid mt-4">
        <!-- Page Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center">
                    <h2><i class="fas fa-home text-primary"></i> Cabin Management</h2>
                    <a href="${pageContext.request.contextPath}/admin/add-cabin" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Add New Cabin
                    </a>
                </div>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle"></i> ${sessionScope.errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <!-- Filters -->
        <div class="card mb-4">
            <div class="card-body">
                <form method="get" action="${pageContext.request.contextPath}/admin/manage-cabins" class="row g-3">
                    <div class="col-md-4">
                        <label for="companyId" class="form-label">Filter by Company</label>
                        <select name="companyId" id="companyId" class="form-select">
                            <option value="">All Companies</option>
                            <c:forEach var="company" items="${companies}">
                                <option value="${company.companyId}"
                                        ${selectedCompanyId eq company.companyId ? 'selected' : ''}>
                                    ${company.name}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label for="status" class="form-label">Filter by Status</label>
                        <select name="status" id="status" class="form-select">
                            <option value="">All Status</option>
                            <option value="ACTIVE" ${selectedStatus eq 'ACTIVE' ? 'selected' : ''}>Active</option>
                            <option value="MAINTENANCE" ${selectedStatus eq 'MAINTENANCE' ? 'selected' : ''}>Maintenance</option>
                            <option value="INACTIVE" ${selectedStatus eq 'INACTIVE' ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>
                    <div class="col-md-4 d-flex align-items-end">
                        <button type="submit" class="btn btn-outline-primary me-2">
                            <i class="fas fa-filter"></i> Apply Filters
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/manage-cabins" class="btn btn-outline-secondary">
                            <i class="fas fa-times"></i> Clear
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- Statistics Cards -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card bg-primary text-white">
                    <div class="card-body text-center">
                        <h5>${totalCabins}</h5>
                        <p class="mb-0">Total Cabins</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card bg-success text-white">
                    <div class="card-body text-center">
                        <h5>${cabins.stream().filter(c -> c.status.name() eq 'ACTIVE').count()}</h5>
                        <p class="mb-0">Active Cabins</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card bg-warning text-white">
                    <div class="card-body text-center">
                        <h5>${cabins.stream().filter(c -> c.vipOnly).count()}</h5>
                        <p class="mb-0">VIP Cabins</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card bg-info text-white">
                    <div class="card-body text-center">
                        <h5>${cabins.stream().filter(c -> c.status.name() eq 'MAINTENANCE').count()}</h5>
                        <p class="mb-0">Under Maintenance</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Cabins Table -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">
                    <i class="fas fa-list"></i> Cabin List
                    <span class="badge bg-primary ms-2">${totalCabins} cabins</span>
                </h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty cabins}">
                        <div class="text-center py-5">
                            <i class="fas fa-home fa-3x text-muted mb-3"></i>
                            <h4 class="text-muted">No Cabins Found</h4>
                            <p class="text-muted">There are no cabins matching your criteria.</p>
                            <a href="${pageContext.request.contextPath}/admin/add-cabin" class="btn btn-primary">
                                <i class="fas fa-plus"></i> Add First Cabin
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead class="table-dark">
                                    <tr>
                                        <th>Cabin Details</th>
                                        <th>Company</th>
                                        <th>Capacity</th>
                                        <th>Status</th>
                                        <th>Access Level</th>
                                        <th>Created</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="cabin" items="${cabins}">
                                        <tr>
                                            <td>
                                                <div>
                                                    <strong>${cabin.name}</strong><br>
                                                    <small class="text-muted">
                                                        <i class="fas fa-map-marker-alt"></i> ${cabin.location}
                                                    </small>
                                                    <c:if test="${not empty cabin.amenities}">
                                                        <br><small class="text-info">
                                                            <i class="fas fa-star"></i> ${cabin.amenities}
                                                        </small>
                                                    </c:if>
                                                </div>
                                            </td>
                                            <td>
                                                <c:forEach var="company" items="${companies}">
                                                    <c:if test="${company.companyId eq cabin.companyId}">
                                                        ${company.name}
                                                    </c:if>
                                                </c:forEach>
                                            </td>
                                            <td>
                                                <span class="badge bg-info">${cabin.capacity} people</span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${cabin.status.name() eq 'ACTIVE'}">
                                                        <span class="badge bg-success">Active</span>
                                                    </c:when>
                                                    <c:when test="${cabin.status.name() eq 'MAINTENANCE'}">
                                                        <span class="badge bg-warning">Maintenance</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">Inactive</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${cabin.vipOnly}">
                                                        <span class="badge bg-warning">
                                                            <i class="fas fa-crown"></i> VIP Only
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">All Users</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty cabin.createdAt}">
                                                        <fmt:formatDate value="${cabin.createdAt}" pattern="MMM dd, yyyy"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        N/A
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="btn-group btn-group-sm" role="group">
                                                    <a href="${pageContext.request.contextPath}/admin/cabin/edit?cabinId=${cabin.cabinId}"
                                                       class="btn btn-outline-primary" title="Edit Cabin">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                    <button type="button" class="btn btn-outline-warning dropdown-toggle"
                                                            data-bs-toggle="dropdown" title="Change Status">
                                                        <i class="fas fa-cog"></i>
                                                    </button>
                                                    <ul class="dropdown-menu">
                                                        <li><a class="dropdown-item status-change"
                                                               href="#" data-cabin-id="${cabin.cabinId}" data-status="ACTIVE">
                                                            <i class="fas fa-check text-success"></i> Set Active
                                                        </a></li>
                                                        <li><a class="dropdown-item status-change"
                                                               href="#" data-cabin-id="${cabin.cabinId}" data-status="MAINTENANCE">
                                                            <i class="fas fa-tools text-warning"></i> Set Maintenance
                                                        </a></li>
                                                        <li><a class="dropdown-item status-change"
                                                               href="#" data-cabin-id="${cabin.cabinId}" data-status="INACTIVE">
                                                            <i class="fas fa-ban text-secondary"></i> Set Inactive
                                                        </a></li>
                                                    </ul>
                                                    <button type="button" class="btn btn-outline-danger delete-cabin"
                                                            data-cabin-id="${cabin.cabinId}" data-cabin-name="${cabin.name}"
                                                            title="Delete Cabin">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-light text-center py-3 mt-5">
        <div class="container">
            <p class="mb-0">&copy; 2024 Cabin Booking System - Admin Panel</p>
        </div>
    </footer>

    <!-- Hidden Forms for Actions -->
    <form id="statusForm" method="post" action="${pageContext.request.contextPath}/admin/cabin/status" style="display: none;">
        <input type="hidden" name="cabinId" id="statusCabinId">
        <input type="hidden" name="status" id="statusValue">
    </form>

    <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/admin/cabin/delete" style="display: none;">
        <input type="hidden" name="cabinId" id="deleteCabinId">
    </form>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Status change functionality
        document.querySelectorAll('.status-change').forEach(button => {
            button.addEventListener('click', function(e) {
                e.preventDefault();
                const cabinId = this.dataset.cabinId;
                const status = this.dataset.status;

                if (confirm(`Are you sure you want to change the cabin status to ${status}?`)) {
                    document.getElementById('statusCabinId').value = cabinId;
                    document.getElementById('statusValue').value = status;
                    document.getElementById('statusForm').submit();
                }
            });
        });

        // Delete functionality
        document.querySelectorAll('.delete-cabin').forEach(button => {
            button.addEventListener('click', function() {
                const cabinId = this.dataset.cabinId;
                const cabinName = this.dataset.cabinName;

                if (confirm(`Are you sure you want to delete cabin "${cabinName}"? This action cannot be undone.`)) {
                    document.getElementById('deleteCabinId').value = cabinId;
                    document.getElementById('deleteForm').submit();
                }
            });
        });

        // Auto-hide alerts
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert-dismissible');
            alerts.forEach(alert => {
                const closeButton = alert.querySelector('.btn-close');
                if (closeButton) {
                    closeButton.click();
                }
            });
        }, 5000);
    </script>
</body>
</html>
