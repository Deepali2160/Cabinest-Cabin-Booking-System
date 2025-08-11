<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - Cabin Booking System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
                <i class="fas fa-building"></i> Cabin Booking System
            </a>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                            <i class="fas fa-tachometer-alt"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/mybookings">
                            <i class="fas fa-calendar-check"></i> My Bookings
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/profile">
                            <i class="fas fa-user-edit"></i> Profile
                        </a>
                    </li>
                </ul>

                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt"></i> Logout
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container mt-4">

        <!-- Page Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card dashboard-card">
                    <div class="card-body">
                        <h2 class="mb-2">
                            <i class="fas fa-user-edit"></i> My Profile
                        </h2>
                        <p class="text-muted mb-0">
                            Manage your account information and preferences
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle"></i> ${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="row">
            <!-- Profile Form -->
            <div class="col-md-8">
                <div class="card dashboard-card">
                    <div class="card-header">
                        <h5 class="mb-0">
                            <i class="fas fa-user"></i> Account Information
                        </h5>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/profile" method="post" id="profileForm">

                            <!-- Basic Information -->
                            <div class="row mb-4">
                                <div class="col-12">
                                    <h6 class="text-muted mb-3">
                                        <i class="fas fa-info-circle"></i> Basic Information
                                    </h6>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="name" class="form-label">Full Name <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="name" name="name"
                                           value="${user.name}" required maxlength="100">
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="email" class="form-label">Email Address <span class="text-danger">*</span></label>
                                    <input type="email" class="form-control" id="email" name="email"
                                           value="${user.email}" required>
                                    <div class="form-text">Used for login and notifications</div>
                                </div>
                            </div>

                            <!-- Company Information -->
                            <div class="row mb-4">
                                <div class="col-12">
                                    <h6 class="text-muted mb-3">
                                        <i class="fas fa-building"></i> Company Information
                                    </h6>
                                </div>

                                <div class="col-md-8 mb-3">
                                    <label for="companyId" class="form-label">Default Company</label>
                                    <select class="form-select" id="companyId" name="companyId">
                                        <c:forEach var="company" items="${allCompanies}">
                                            <option value="${company.companyId}"
                                                    ${company.companyId == user.defaultCompanyId ? 'selected' : ''}>
                                                ${company.name} - ${company.location}
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <div class="form-text">Your primary company for cabin bookings</div>
                                </div>

                                <div class="col-md-4 mb-3">
                                    <label class="form-label">Current Status</label>
                                    <div class="form-control-plaintext">
                                        <c:choose>
                                            <c:when test="${user.userType == 'VIP'}">
                                                <span class="badge badge-vip fs-6">VIP Member</span>
                                            </c:when>
                                            <c:when test="${user.admin}">
                                                <span class="badge bg-danger fs-6">Administrator</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-primary fs-6">Regular User</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>

                            <!-- Password Change Section -->
                            <div class="row mb-4">
                                <div class="col-12">
                                    <h6 class="text-muted mb-3">
                                        <i class="fas fa-lock"></i> Change Password (Optional)
                                    </h6>
                                </div>

                                <div class="col-md-4 mb-3">
                                    <label for="currentPassword" class="form-label">Current Password</label>
                                    <input type="password" class="form-control" id="currentPassword" name="currentPassword">
                                    <div class="form-text">Leave blank if not changing password</div>
                                </div>

                                <div class="col-md-4 mb-3">
                                    <label for="newPassword" class="form-label">New Password</label>
                                    <input type="password" class="form-control" id="newPassword" name="newPassword" minlength="6">
                                    <div class="form-text">Minimum 6 characters</div>
                                </div>

                                <div class="col-md-4 mb-3">
                                    <label for="confirmPassword" class="form-label">Confirm New Password</label>
                                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword">
                                </div>
                            </div>

                            <!-- Submit Buttons -->
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary me-md-2">
                                    <i class="fas fa-arrow-left"></i> Back to Dashboard
                                </a>
                                <button type="submit" class="btn btn-primary" id="updateBtn">
                                    <i class="fas fa-save"></i> Update Profile
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Profile Summary Sidebar -->
            <div class="col-md-4">

                <!-- Profile Summary -->
                <div class="card dashboard-card mb-4">
                    <div class="card-header">
                        <h6 class="mb-0">
                            <i class="fas fa-id-card"></i> Profile Summary
                        </h6>
                    </div>
                    <div class="card-body text-center">
                        <div class="profile-img-placeholder mb-3">
                            <i class="fas fa-user-circle fa-5x text-muted"></i>
                        </div>

                        <h5>${user.name}</h5>
                        <p class="text-muted mb-2">${user.email}</p>

                        <div class="mb-3">
                            <c:choose>
                                <c:when test="${user.userType == 'VIP'}">
                                    <span class="badge badge-vip">VIP Member</span>
                                </c:when>
                                <c:when test="${user.admin}">
                                    <span class="badge bg-danger">Administrator</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-primary">Regular User</span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="text-muted small">
                            <i class="fas fa-building"></i> ${currentCompany.name}<br>
                            <i class="fas fa-map-marker-alt"></i> ${currentCompany.location}
                        </div>
                    </div>
                </div>

                <!-- Booking Statistics -->
                <div class="card dashboard-card mb-4">
                    <div class="card-header">
                        <h6 class="mb-0">
                            <i class="fas fa-chart-bar"></i> Your Statistics
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="row text-center">
                            <div class="col-6 mb-3">
                                <div class="stat-number text-primary">${totalBookings}</div>
                                <div class="stat-label small">Total Bookings</div>
                            </div>
                            <div class="col-6 mb-3">
                                <div class="stat-number text-success">
                                    <c:choose>
                                        <c:when test="${user.status == 'ACTIVE'}">
                                            <i class="fas fa-check-circle"></i>
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fas fa-times-circle text-danger"></i>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="stat-label small">Account Status</div>
                            </div>
                        </div>

                        <div class="text-center">
                            <small class="text-muted">
                                <i class="fas fa-calendar"></i> Member since
                                <c:choose>
                                    <c:when test="${user.createdAt != null}">
                                        <fmt:formatDate value="${user.createdAt}" pattern="MMM yyyy"/>
                                    </c:when>
                                    <c:otherwise>
                                        Recently
                                    </c:otherwise>
                                </c:choose>
                            </small>
                        </div>
                    </div>
                </div>

                <!-- Quick Actions -->
                <div class="card dashboard-card">
                    <div class="card-header">
                        <h6 class="mb-0">
                            <i class="fas fa-bolt"></i> Quick Actions
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/book" class="btn btn-primary">
                                <i class="fas fa-calendar-plus"></i> New Booking
                            </a>
                            <a href="${pageContext.request.contextPath}/mybookings" class="btn btn-outline-secondary">
                                <i class="fas fa-list"></i> My Bookings
                            </a>
                            <a href="${pageContext.request.contextPath}/company/browse" class="btn btn-outline-info">
                                <i class="fas fa-search"></i> Browse Cabins
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-light text-center py-3 mt-5">
        <div class="container">
            <p class="mb-0">&copy; 2024 Cabin Booking System</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Password confirmation validation
        document.getElementById('confirmPassword').addEventListener('input', function() {
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = this.value;

            if (newPassword && confirmPassword && newPassword !== confirmPassword) {
                this.setCustomValidity('Passwords do not match');
                this.classList.add('is-invalid');
            } else {
                this.setCustomValidity('');
                this.classList.remove('is-invalid');
            }
        });

        // Password change validation
        function validatePasswordFields() {
            const currentPassword = document.getElementById('currentPassword').value;
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (currentPassword || newPassword || confirmPassword) {
                if (!currentPassword) {
                    alert('Please enter your current password to change it.');
                    return false;
                }
                if (!newPassword || newPassword.length < 6) {
                    alert('New password must be at least 6 characters long.');
                    return false;
                }
                if (newPassword !== confirmPassword) {
                    alert('New passwords do not match.');
                    return false;
                }
            }
            return true;
        }

        // Form submission
        document.getElementById('profileForm').addEventListener('submit', function(e) {
            if (!validatePasswordFields()) {
                e.preventDefault();
                return;
            }

            const updateBtn = document.getElementById('updateBtn');
            updateBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Updating...';
            updateBtn.disabled = true;
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
