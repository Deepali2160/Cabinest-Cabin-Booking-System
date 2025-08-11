<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Cabin Booking System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="#">
                <i class="fas fa-building"></i> Cabin Booking System
            </a>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/dashboard">
                            <i class="fas fa-tachometer-alt"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/book">
                            <i class="fas fa-plus-circle"></i> New Booking
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/mybookings">
                            <i class="fas fa-calendar-check"></i> My Bookings
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/companies">
                            <i class="fas fa-search"></i> Browse Companies
                        </a>
                    </li>
                </ul>

                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                            <i class="fas fa-user-circle"></i> ${user.name}
                            <c:if test="${user.userType == 'VIP'}">
                                <span class="badge badge-vip ms-1">VIP</span>
                            </c:if>
                            <c:if test="${user.admin}">
                                <span class="badge bg-danger ms-1">ADMIN</span>
                            </c:if>
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile">
                                <i class="fas fa-user-edit"></i> Profile</a></li>
                            <c:if test="${user.admin}">
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/dashboard">
                                    <i class="fas fa-cogs"></i> Admin Panel</a></li>
                            </c:if>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                                <i class="fas fa-sign-out-alt"></i> Logout</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container mt-4">

        <!-- Success/Error Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Welcome Section -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card dashboard-card bg-gradient text-white" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <h2 class="mb-2">
                                    <i class="fas fa-sun"></i> Welcome back, ${user.name}!
                                </h2>
                                <p class="mb-2">
                                    <i class="fas fa-building"></i> ${company.name} - ${company.location}
                                </p>
                                <p class="mb-0">
                                    <i class="fas fa-star"></i> Your Booking Score: <strong>${bookingScore}/100</strong>
                                    <c:if test="${user.userType == 'VIP'}">
                                        | <span class="badge badge-vip">VIP Member</span>
                                    </c:if>
                                </p>
                            </div>
                            <div class="col-md-4 text-center">
                                <div class="stat-number">${totalBookings}</div>
                                <div class="stat-label">Total Bookings</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="row mb-4">
            <div class="col-md-4 mb-3">
                <div class="card dashboard-card text-center">
                    <div class="card-body">
                        <i class="fas fa-plus-circle fa-3x text-primary mb-3"></i>
                        <h5>Book a Cabin</h5>
                        <p class="text-muted">Make a new booking with AI recommendations</p>
                        <a href="${pageContext.request.contextPath}/book" class="btn btn-primary">
                            <i class="fas fa-calendar-plus"></i> Book Now
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-4 mb-3">
                <div class="card dashboard-card text-center">
                    <div class="card-body">
                        <i class="fas fa-calendar-check fa-3x text-success mb-3"></i>
                        <h5>My Bookings</h5>
                        <p class="text-muted">View and manage your bookings</p>
                        <a href="${pageContext.request.contextPath}/mybookings" class="btn btn-success">
                            <i class="fas fa-eye"></i> View All
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-4 mb-3">
                <div class="card dashboard-card text-center">
                    <div class="card-body">
                        <i class="fas fa-search fa-3x text-info mb-3"></i>
                        <h5>Browse Cabins</h5>
                        <p class="text-muted">Explore available meeting rooms</p>
                        <a href="${pageContext.request.contextPath}/company/browse" class="btn btn-info">
                            <i class="fas fa-compass"></i> Explore
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- AI Recommendations Section -->
        <c:if test="${not empty recommendedCabins}">
            <div class="row mb-4">
                <div class="col-12">
                    <div class="card dashboard-card">
                        <div class="card-header bg-gradient text-white" style="background: linear-gradient(45deg, #6f42c1, #9c27b0);">
                            <h5 class="mb-0">
                                <i class="fas fa-robot"></i> AI Recommendations for You
                                <span class="ai-badge badge ms-2">AI POWERED</span>
                            </h5>
                        </div>
                        <div class="card-body">
                            <p class="text-muted mb-3">Based on your booking history and preferences:</p>
                            <div class="row">
                                <c:forEach var="cabin" items="${recommendedCabins}" varStatus="status">
                                    <c:if test="${status.index < 3}">
                                        <div class="col-md-4 mb-3">
                                            <div class="card ai-recommendation">
                                                <div class="card-body">
                                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                                        <h6 class="card-title mb-0">${cabin.name}</h6>
                                                        <c:if test="${cabin.vipOnly}">
                                                            <span class="badge badge-vip">VIP Only</span>
                                                        </c:if>
                                                    </div>
                                                    <p class="card-text small text-muted mb-2">
                                                        <i class="fas fa-users"></i> Capacity: ${cabin.capacity} people<br>
                                                        <i class="fas fa-map-marker-alt"></i> ${cabin.location}
                                                    </p>
                                                    <c:if test="${not empty cabin.amenities}">
                                                        <p class="small mb-2">
                                                            <i class="fas fa-star"></i> ${cabin.amenities}
                                                        </p>
                                                    </c:if>
                                                    <a href="${pageContext.request.contextPath}/book?cabinId=${cabin.cabinId}"
                                                       class="btn btn-sm btn-outline-primary">
                                                        <i class="fas fa-calendar-plus"></i> Book This
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Available Cabins & Recent Bookings -->
        <div class="row">
            <!-- Available Cabins -->
            <div class="col-md-8 mb-4">
                <div class="card dashboard-card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">
                            <i class="fas fa-home"></i> Available Cabins (${company.name})
                        </h5>
                        <small class="text-muted">${cabins.size()} cabins available</small>
                    </div>
                    <div class="card-body">
                        <c:if test="${empty cabins}">
                            <div class="text-center py-4">
                                <i class="fas fa-info-circle fa-2x text-muted mb-2"></i>
                                <p class="text-muted">No cabins available for your access level.</p>
                            </div>
                        </c:if>

                        <div class="row">
                            <c:forEach var="cabin" items="${cabins}" varStatus="status">
                                <c:if test="${status.index < 6}">
                                    <div class="col-md-6 mb-3">
                                        <div class="card border">
                                            <div class="card-body p-3">
                                                <div class="d-flex justify-content-between align-items-start mb-2">
                                                    <h6 class="card-title mb-0">${cabin.name}</h6>
                                                    <div>
                                                        <c:if test="${cabin.vipOnly}">
                                                            <span class="badge badge-vip">VIP</span>
                                                        </c:if>
                                                        <c:choose>
                                                            <c:when test="${cabin.status == 'ACTIVE'}">
                                                                <span class="badge bg-success">Available</span>
                                                            </c:when>
                                                            <c:when test="${cabin.status == 'MAINTENANCE'}">
                                                                <span class="badge bg-warning">Maintenance</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-secondary">Inactive</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                                <p class="card-text small mb-2">
                                                    <i class="fas fa-users"></i> ${cabin.capacity} people |
                                                    <i class="fas fa-map-marker-alt"></i> ${cabin.location}
                                                </p>
                                                <c:if test="${cabin.status == 'ACTIVE'}">
                                                    <a href="${pageContext.request.contextPath}/book?cabinId=${cabin.cabinId}"
                                                       class="btn btn-sm btn-primary">
                                                        <i class="fas fa-calendar-plus"></i> Book
                                                    </a>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>

                        <c:if test="${cabins.size() > 6}">
                            <div class="text-center mt-3">
                                <a href="${pageContext.request.contextPath}/company/${company.companyId}"
                                   class="btn btn-outline-primary">
                                    <i class="fas fa-eye"></i> View All Cabins (${cabins.size()})
                                </a>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>

            <!-- Recent Bookings & Popular Times -->
            <div class="col-md-4">
                <!-- Recent Bookings -->
                <div class="card dashboard-card mb-4">
                    <div class="card-header">
                        <h6 class="mb-0">
                            <i class="fas fa-history"></i> Recent Bookings
                        </h6>
                    </div>
                    <div class="card-body">
                        <c:if test="${empty recentBookings}">
                            <div class="text-center py-3">
                                <i class="fas fa-calendar text-muted fa-2x mb-2"></i>
                                <p class="text-muted small mb-0">No recent bookings</p>
                            </div>
                        </c:if>

                        <c:forEach var="booking" items="${recentBookings}">
                            <div class="d-flex justify-content-between align-items-center mb-2 p-2 border-bottom">
                                <div>
                                    <div class="fw-bold small">${booking.cabinName}</div>
                                    <div class="text-muted small">
                                        <fmt:formatDate value="${booking.bookingDate}" pattern="MMM dd, yyyy"/>
                                        | ${booking.timeSlot}
                                    </div>
                                </div>
                                <div>
                                    <c:choose>
                                        <c:when test="${booking.status == 'PENDING'}">
                                            <span class="badge badge-pending">Pending</span>
                                        </c:when>
                                        <c:when test="${booking.status == 'APPROVED'}">
                                            <span class="badge badge-approved">Approved</span>
                                        </c:when>
                                        <c:when test="${booking.status == 'REJECTED'}">
                                            <span class="badge badge-rejected">Rejected</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${booking.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>

                        <c:if test="${not empty recentBookings}">
                            <div class="text-center mt-3">
                                <a href="${pageContext.request.contextPath}/mybookings"
                                   class="btn btn-sm btn-outline-primary">
                                    <i class="fas fa-eye"></i> View All
                                </a>
                            </div>
                        </c:if>
                    </div>
                </div>

                <!-- Popular Time Slots -->
                <div class="card dashboard-card">
                    <div class="card-header">
                        <h6 class="mb-0">
                            <i class="fas fa-clock"></i> Popular Time Slots
                            <span class="ai-badge badge ms-2">AI INSIGHTS</span>
                        </h6>
                    </div>
                    <div class="card-body">
                        <c:if test="${empty popularTimeSlots}">
                            <p class="text-muted small">No data available</p>
                        </c:if>

                        <c:forEach var="timeSlot" items="${popularTimeSlots}" varStatus="status">
                            <c:if test="${status.index < 5}">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <span class="small">${timeSlot}</span>
                                    <span class="badge bg-info">Popular</span>
                                </div>
                            </c:if>
                        </c:forEach>

                        <div class="text-center mt-3">
                            <small class="text-muted">
                                <i class="fas fa-lightbulb"></i> AI recommends booking during less popular times
                            </small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-light text-center py-3 mt-5">
        <div class="container">
            <p class="mb-0">&copy; 2024 Cabin Booking System with AI Recommendations</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Auto-hide alerts after 5 seconds
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert-dismissible');
            alerts.forEach(alert => {
                const closeButton = alert.querySelector('.btn-close');
                if (closeButton) {
                    closeButton.click();
                }
            });
        }, 5000);

        // Add loading animation to buttons
        document.querySelectorAll('a[href*="book"]').forEach(button => {
            button.addEventListener('click', function() {
                if (!this.classList.contains('disabled')) {
                    this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Loading...';
                }
            });
        });
    </script>
</body>
</html>
