<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New Cabin - Admin Panel</title>
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
                    <i class="fas fa-arrow-left"></i> Back to Dashboard
                </a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <!-- Page Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center">
                    <h2><i class="fas fa-plus-circle text-primary"></i> Add New Cabin</h2>
                    <a href="${pageContext.request.contextPath}/admin/manage-cabins" class="btn btn-outline-secondary">
                        <i class="fas fa-list"></i> Manage Cabins
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

        <!-- Add Cabin Form -->
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="card shadow">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0"><i class="fas fa-home"></i> Cabin Information</h5>
                    </div>
                    <div class="card-body">
                        <form method="post" action="${pageContext.request.contextPath}/admin/add-cabin" id="addCabinForm">

                            <!-- Basic Information -->
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="name" class="form-label">
                                        <i class="fas fa-tag"></i> Cabin Name <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="name" name="name"
                                           placeholder="e.g., Conference Room A" required maxlength="100">
                                </div>
                                <div class="col-md-6">
                                    <label for="capacity" class="form-label">
                                        <i class="fas fa-users"></i> Capacity <span class="text-danger">*</span>
                                    </label>
                                    <input type="number" class="form-control" id="capacity" name="capacity"
                                           placeholder="Number of people" required min="1" max="50">
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
                                            <option value="${company.companyId}">${company.name} - ${company.location}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label for="location" class="form-label">
                                        <i class="fas fa-map-marker-alt"></i> Location <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="location" name="location"
                                           placeholder="e.g., 2nd Floor, Wing A" required maxlength="200">
                                </div>
                            </div>

                            <!-- Amenities -->
                            <div class="mb-3">
                                <label for="amenities" class="form-label">
                                    <i class="fas fa-star"></i> Amenities
                                </label>
                                <textarea class="form-control" id="amenities" name="amenities" rows="3"
                                          placeholder="e.g., Projector, Whiteboard, AC, Wi-Fi, Video Conferencing"></textarea>
                                <div class="form-text">List the available amenities and equipment</div>
                            </div>

                            <!-- VIP Access -->
                            <div class="mb-4">
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" id="isVipOnly" name="isVipOnly" value="true">
                                    <label class="form-check-label" for="isVipOnly">
                                        <i class="fas fa-crown text-warning"></i> <strong>VIP Only Cabin</strong>
                                    </label>
                                    <div class="form-text">Check this if only VIP users and admins can book this cabin</div>
                                </div>
                            </div>

                            <!-- Form Buttons -->
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="${pageContext.request.contextPath}/admin/manage-cabins" class="btn btn-secondary me-md-2">
                                    <i class="fas fa-times"></i> Cancel
                                </a>
                                <button type="submit" class="btn btn-primary" id="submitBtn">
                                    <i class="fas fa-plus"></i> Add Cabin
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Quick Tips -->
                <div class="card mt-4 border-info">
                    <div class="card-header bg-info text-white">
                        <h6 class="mb-0"><i class="fas fa-lightbulb"></i> Quick Tips</h6>
                    </div>
                    <div class="card-body">
                        <ul class="mb-0">
                            <li><strong>Cabin Name:</strong> Use descriptive names like "Conference Room A" or "Meeting Hall 1"</li>
                            <li><strong>Capacity:</strong> Enter the maximum number of people who can comfortably use the cabin</li>
                            <li><strong>VIP Only:</strong> VIP cabins are exclusively available to VIP users and administrators</li>
                            <li><strong>Amenities:</strong> List key features to help users choose the right cabin</li>
                        </ul>
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
        document.getElementById('addCabinForm').addEventListener('submit', function(e) {
            const submitBtn = document.getElementById('submitBtn');
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Adding Cabin...';
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

        // Real-time validation
        document.getElementById('name').addEventListener('blur', function() {
            if (this.value.trim().length < 3) {
                this.classList.add('is-invalid');
            } else {
                this.classList.remove('is-invalid');
                this.classList.add('is-valid');
            }
        });

        document.getElementById('capacity').addEventListener('blur', function() {
            const capacity = parseInt(this.value);
            if (capacity < 1 || capacity > 50) {
                this.classList.add('is-invalid');
            } else {
                this.classList.remove('is-invalid');
                this.classList.add('is-valid');
            }
        });
    </script>
</body>
</html>
