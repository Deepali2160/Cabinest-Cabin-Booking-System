<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Cabin - Admin Panel</title>
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
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/manage-cabins">
                    <i class="fas fa-arrow-left"></i> Back to Cabins
                </a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <!-- Page Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center">
                    <h2><i class="fas fa-edit text-warning"></i> Edit Cabin: ${cabin.name}</h2>
                    <div>
                        <a href="${pageContext.request.contextPath}/admin/manage-cabins" class="btn btn-outline-secondary">
                            <i class="fas fa-list"></i> Manage Cabins
                        </a>
                    </div>
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

        <!-- Edit Cabin Form -->
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="card shadow">
                    <div class="card-header bg-warning text-dark">
                        <h5 class="mb-0"><i class="fas fa-edit"></i> Edit Cabin Information</h5>
                    </div>
                    <div class="card-body">
                        <form method="post" action="${pageContext.request.contextPath}/admin/cabin/edit" id="editCabinForm">

                            <!-- Hidden Cabin ID -->
                            <input type="hidden" name="cabinId" value="${cabin.cabinId}">

                            <!-- Basic Information -->
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="name" class="form-label">
                                        <i class="fas fa-tag"></i> Cabin Name <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="name" name="name"
                                           value="${cabin.name}" required maxlength="100">
                                </div>
                                <div class="col-md-6">
                                    <label for="capacity" class="form-label">
                                        <i class="fas fa-users"></i> Capacity <span class="text-danger">*</span>
                                    </label>
                                    <input type="number" class="form-control" id="capacity" name="capacity"
                                           value="${cabin.capacity}" required min="1" max="50">
                                </div>
                            </div>

                            <!-- Company and Location -->
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="companyId" class="form-label">
                                        <i class="fas fa-building"></i> Company <span class="text-danger">*</span>
                                    </label>
                                    <select class="form-select" id="companyId" name="companyId" required>
                                        <option value="">Select Company</option>
                                        <c:forEach var="company" items="${companies}">
                                            <option value="${company.companyId}"
                                                    ${company.companyId eq cabin.companyId ? 'selected' : ''}>
                                                ${company.name} - ${company.location}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label for="location" class="form-label">
                                        <i class="fas fa-map-marker-alt"></i> Location <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="location" name="location"
                                           value="${cabin.location}" required maxlength="200">
                                </div>
                            </div>

                            <!-- Amenities -->
                            <div class="mb-3">
                                <label for="amenities" class="form-label">
                                    <i class="fas fa-star"></i> Amenities
                                </label>
                                <textarea class="form-control" id="amenities" name="amenities" rows="3">${cabin.amenities}</textarea>
                                <div class="form-text">List the available amenities and equipment</div>
                            </div>

                            <!-- Status and VIP Access -->
                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <label for="status" class="form-label">
                                        <i class="fas fa-flag"></i> Status <span class="text-danger">*</span>
                                    </label>
                                    <select class="form-select" id="status" name="status" required>
                                        <option value="ACTIVE" ${cabin.status.name() eq 'ACTIVE' ? 'selected' : ''}>Active</option>
                                        <option value="MAINTENANCE" ${cabin.status.name() eq 'MAINTENANCE' ? 'selected' : ''}>Maintenance</option>
                                        <option value="INACTIVE" ${cabin.status.name() eq 'INACTIVE' ? 'selected' : ''}>Inactive</option>
                                    </select>
                                </div>
                                <div class="col-md-6 d-flex align-items-end">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" id="isVipOnly" name="isVipOnly"
                                               value="true" ${cabin.vipOnly ? 'checked' : ''}>
                                        <label class="form-check-label" for="isVipOnly">
                                            <i class="fas fa-crown text-warning"></i> <strong>VIP Only Cabin</strong>
                                        </label>
                                    </div>
                                </div>
                            </div>

                            <!-- Form Buttons -->
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="${pageContext.request.contextPath}/admin/manage-cabins" class="btn btn-secondary me-md-2">
                                    <i class="fas fa-times"></i> Cancel
                                </a>
                                <button type="submit" class="btn btn-warning" id="submitBtn">
                                    <i class="fas fa-save"></i> Update Cabin
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Cabin Information -->
                <div class="card mt-4 border-info">
                    <div class="card-header bg-info text-white">
                        <h6 class="mb-0"><i class="fas fa-info-circle"></i> Cabin Information</h6>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <p><strong>Cabin ID:</strong> ${cabin.cabinId}</p>
                                <p><strong>Current Status:</strong>
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
                                </p>
                            </div>
                            <div class="col-md-6">
                                <p><strong>Access Level:</strong>
                                    <c:choose>
                                        <c:when test="${cabin.vipOnly}">
                                            <span class="badge bg-warning"><i class="fas fa-crown"></i> VIP Only</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">All Users</span>
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Created:</strong>
                                    <c:choose>
                                        <c:when test="${not empty cabin.createdAt}">
                                            <fmt:formatDate value="${cabin.createdAt}" pattern="MMM dd, yyyy 'at' hh:mm a"/>
                                        </c:when>
                                        <c:otherwise>
                                            N/A
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-light text-center py-3 mt-5">
        <div class="container">
            <p class="mb-0">&copy; 2024 Cabin Booking System - Admin Panel</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Form validation and enhancement
        document.getElementById('editCabinForm').addEventListener('submit', function(e) {
            const submitBtn = document.getElementById('submitBtn');
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Updating Cabin...';
            submitBtn.disabled = true;
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

        // Status change warning
        document.getElementById('status').addEventListener('change', function() {
            if (this.value === 'INACTIVE') {
                if (!confirm('Setting cabin to INACTIVE will prevent all future bookings. Continue?')) {
                    this.value = '${cabin.status.name()}'; // Reset to original value
                }
            }
        });
    </script>
</body>
</html>
