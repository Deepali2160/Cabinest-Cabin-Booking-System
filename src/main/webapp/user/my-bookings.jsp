<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Bookings - Cabin Booking System</title>
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/book">
                            <i class="fas fa-plus-circle"></i> New Booking
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/mybookings">
                            <i class="fas fa-calendar-check"></i> My Bookings
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
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile">
                                <i class="fas fa-user-edit"></i> Profile</a></li>
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

        <!-- Page Header with Statistics -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card dashboard-card">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-md-6">
                                <h2 class="mb-2">
                                    <i class="fas fa-calendar-check"></i> My Bookings
                                </h2>
                                <p class="text-muted mb-0">
                                    Track and manage all your cabin reservations
                                </p>
                            </div>
                            <div class="col-md-6">
                                <div class="row text-center">
                                    <div class="col-4">
                                        <div class="stat-number text-primary">${totalBookings}</div>
                                        <div class="stat-label small">Total</div>
                                    </div>
                                    <div class="col-4">
                                        <div class="stat-number text-warning">${pendingBookings.size()}</div>
                                        <div class="stat-label small">Pending</div>
                                    </div>
                                    <div class="col-4">
                                        <div class="stat-number text-success">${approvedBookings.size()}</div>
                                        <div class="stat-label small">Approved</div>
                                    </div>
                                </div>
                            </div>
                        </div>
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

        <div class="row">
            <!-- Bookings List -->
            <div class="col-md-8">

                <!-- Filter Tabs -->
                <div class="card dashboard-card mb-4">
                    <div class="card-header">
                        <ul class="nav nav-pills card-header-pills" id="bookingTabs" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link active" id="all-tab" data-bs-toggle="pill"
                                        data-bs-target="#all-bookings" type="button" role="tab">
                                    <i class="fas fa-list"></i> All (${totalBookings})
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="pending-tab" data-bs-toggle="pill"
                                        data-bs-target="#pending-bookings" type="button" role="tab">
                                    <i class="fas fa-clock"></i> Pending (${pendingBookings.size()})
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="approved-tab" data-bs-toggle="pill"
                                        data-bs-target="#approved-bookings" type="button" role="tab">
                                    <i class="fas fa-check"></i> Approved (${approvedBookings.size()})
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="rejected-tab" data-bs-toggle="pill"
                                        data-bs-target="#rejected-bookings" type="button" role="tab">
                                    <i class="fas fa-times"></i> Rejected (${rejectedBookings.size()})
                                </button>
                            </li>
                        </ul>
                    </div>

                    <div class="card-body">
                        <div class="tab-content" id="bookingTabsContent">

                            <!-- All Bookings -->
                            <div class="tab-pane fade show active" id="all-bookings" role="tabpanel">
                                <c:choose>
                                    <c:when test="${empty allBookings}">
                                        <div class="text-center py-5">
                                            <i class="fas fa-calendar-times fa-3x text-muted mb-3"></i>
                                            <h5 class="text-muted">No bookings yet</h5>
                                            <p class="text-muted">Start by making your first cabin booking!</p>
                                            <a href="${pageContext.request.contextPath}/book" class="btn btn-primary">
                                                <i class="fas fa-plus-circle"></i> Make First Booking
                                            </a>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="booking" items="${allBookings}">
                                            <div class="card mb-3 booking-card">
                                                <div class="card-body">
                                                    <div class="row align-items-center">
                                                        <div class="col-md-6">
                                                            <h6 class="card-title mb-1">
                                                                <i class="fas fa-home"></i> ${booking.cabinName}
                                                            </h6>
                                                            <p class="card-text small text-muted mb-1">
                                                                <i class="fas fa-calendar"></i>
                                                                <fmt:formatDate value="${booking.bookingDate}" pattern="EEEE, MMM dd, yyyy"/>
                                                            </p>
                                                            <p class="card-text small text-muted mb-1">
                                                                <i class="fas fa-clock"></i> ${booking.timeSlot}
                                                            </p>
                                                            <p class="card-text small mb-2">
                                                                <i class="fas fa-comment"></i> ${booking.purpose}
                                                            </p>
                                                            <small class="text-muted">
                                                                Created: <fmt:formatDate value="${booking.createdAt}" pattern="MMM dd, yyyy"/>
                                                            </small>
                                                        </div>
                                                        <div class="col-md-3 text-center">
                                                            <c:choose>
                                                                <c:when test="${booking.status == 'PENDING'}">
                                                                    <span class="badge badge-pending fs-6 mb-2">Pending</span>
                                                                </c:when>
                                                                <c:when test="${booking.status == 'APPROVED'}">
                                                                    <span class="badge badge-approved fs-6 mb-2">Approved</span>
                                                                </c:when>
                                                                <c:when test="${booking.status == 'REJECTED'}">
                                                                    <span class="badge badge-rejected fs-6 mb-2">Rejected</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="badge bg-secondary fs-6 mb-2">${booking.status}</span>
                                                                </c:otherwise>
                                                            </c:choose>

                                                            <c:if test="${booking.priorityLevel == 'VIP'}">
                                                                <br><span class="badge badge-vip">VIP Priority</span>
                                                            </c:if>
                                                        </div>
                                                        <div class="col-md-3 text-end">
                                                            <c:if test="${booking.status == 'PENDING'}">
                                                                <button class="btn btn-sm btn-outline-danger cancel-booking"
                                                                        data-booking-id="${booking.bookingId}"
                                                                        data-cabin-name="${booking.cabinName}">
                                                                    <i class="fas fa-times"></i> Cancel
                                                                </button>
                                                            </c:if>

                                                            <c:if test="${booking.status == 'APPROVED'}">
                                                                <div class="text-success small">
                                                                    <i class="fas fa-check-circle"></i> Confirmed
                                                                </div>
                                                                <c:if test="${booking.approvedBy > 0}">
                                                                    <div class="text-muted small">
                                                                        Approved by admin
                                                                    </div>
                                                                </c:if>
                                                            </c:if>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <!-- Pending Bookings -->
                            <div class="tab-pane fade" id="pending-bookings" role="tabpanel">
                                <c:choose>
                                    <c:when test="${empty pendingBookings}">
                                        <div class="text-center py-4">
                                            <i class="fas fa-clock fa-2x text-warning mb-2"></i>
                                            <p class="text-muted">No pending bookings</p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="booking" items="${pendingBookings}">
                                            <div class="card mb-3 border-warning">
                                                <div class="card-body">
                                                    <div class="row align-items-center">
                                                        <div class="col-md-8">
                                                            <h6 class="card-title">${booking.cabinName}</h6>
                                                            <p class="card-text small">
                                                                <fmt:formatDate value="${booking.bookingDate}" pattern="MMM dd, yyyy"/>
                                                                | ${booking.timeSlot} | ${booking.purpose}
                                                            </p>
                                                        </div>
                                                        <div class="col-md-4 text-end">
                                                            <span class="badge badge-pending mb-2">Awaiting Approval</span>
                                                            <br>
                                                            <button class="btn btn-sm btn-outline-danger cancel-booking"
                                                                    data-booking-id="${booking.bookingId}"
                                                                    data-cabin-name="${booking.cabinName}">
                                                                Cancel
                                                            </button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <!-- Approved Bookings -->
                            <div class="tab-pane fade" id="approved-bookings" role="tabpanel">
                                <c:choose>
                                    <c:when test="${empty approvedBookings}">
                                        <div class="text-center py-4">
                                            <i class="fas fa-check fa-2x text-success mb-2"></i>
                                            <p class="text-muted">No approved bookings</p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="booking" items="${approvedBookings}">
                                            <div class="card mb-3 border-success">
                                                <div class="card-body">
                                                    <div class="row align-items-center">
                                                        <div class="col-md-8">
                                                            <h6 class="card-title text-success">${booking.cabinName}</h6>
                                                            <p class="card-text small">
                                                                <fmt:formatDate value="${booking.bookingDate}" pattern="MMM dd, yyyy"/>
                                                                | ${booking.timeSlot} | ${booking.purpose}
                                                            </p>
                                                            <c:if test="${booking.approvedAt != null}">
                                                                <small class="text-muted">
                                                                    Approved on: <fmt:formatDate value="${booking.approvedAt}" pattern="MMM dd, yyyy"/>
                                                                </small>
                                                            </c:if>
                                                        </div>
                                                        <div class="col-md-4 text-end">
                                                            <span class="badge badge-approved mb-2">Confirmed</span>
                                                            <br>
                                                            <i class="fas fa-calendar-check text-success"></i>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <!-- Rejected Bookings -->
                            <div class="tab-pane fade" id="rejected-bookings" role="tabpanel">
                                <c:choose>
                                    <c:when test="${empty rejectedBookings}">
                                        <div class="text-center py-4">
                                            <i class="fas fa-times fa-2x text-danger mb-2"></i>
                                            <p class="text-muted">No rejected bookings</p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="booking" items="${rejectedBookings}">
                                            <div class="card mb-3 border-danger">
                                                <div class="card-body">
                                                    <div class="row align-items-center">
                                                        <div class="col-md-8">
                                                            <h6 class="card-title text-danger">${booking.cabinName}</h6>
                                                            <p class="card-text small">
                                                                <fmt:formatDate value="${booking.bookingDate}" pattern="MMM dd, yyyy"/>
                                                                | ${booking.timeSlot} | ${booking.purpose}
                                                            </p>
                                                        </div>
                                                        <div class="col-md-4 text-end">
                                                            <span class="badge badge-rejected mb-2">Rejected</span>
                                                            <br>
                                                            <a href="${pageContext.request.contextPath}/book"
                                                               class="btn btn-sm btn-outline-primary">
                                                                Book Again
                                                            </a>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- AI Insights Sidebar -->
            <div class="col-md-4">

                <!-- Booking Score -->
                <div class="card dashboard-card mb-4">
                    <div class="card-header bg-gradient text-white"
                         style="background: linear-gradient(45deg, #6f42c1, #9c27b0);">
                        <h6 class="mb-0">
                            <i class="fas fa-chart-line"></i> Your Booking Score
                            <span class="ai-badge badge ms-1">AI</span>
                        </h6>
                    </div>
                    <div class="card-body text-center">
                        <div class="display-4 fw-bold text-primary">${bookingScore}</div>
                        <p class="text-muted mb-3">out of 100</p>

                        <div class="progress mb-3" style="height: 10px;">
                            <div class="progress-bar bg-gradient" role="progressbar"
                                 style="width: ${bookingScore}%; background: linear-gradient(45deg, #6f42c1, #9c27b0);">
                            </div>
                        </div>

                        <p class="small text-muted mb-0">
                            <c:choose>
                                <c:when test="${bookingScore >= 80}">
                                    <i class="fas fa-star text-warning"></i> Excellent booking activity!
                                </c:when>
                                <c:when test="${bookingScore >= 60}">
                                    <i class="fas fa-thumbs-up text-success"></i> Good booking patterns
                                </c:when>
                                <c:otherwise>
                                    <i class="fas fa-info-circle text-info"></i> Keep booking to improve score
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </div>

                <!-- AI Recommendations -->
                <c:if test="${not empty recommendedCabins}">
                    <div class="card dashboard-card mb-4">
                        <div class="card-header">
                            <h6 class="mb-0">
                                <i class="fas fa-robot"></i> Recommended for You
                            </h6>
                        </div>
                        <div class="card-body">
                            <p class="small text-muted mb-3">Based on your booking history:</p>
                            <c:forEach var="cabin" items="${recommendedCabins}" varStatus="status">
                                <c:if test="${status.index < 3}">
                                    <div class="card border-primary mb-2">
                                        <div class="card-body p-2">
                                            <h6 class="card-title mb-1">${cabin.name}</h6>
                                            <p class="card-text small mb-2">
                                                <i class="fas fa-users"></i> ${cabin.capacity} people
                                            </p>
                                            <a href="${pageContext.request.contextPath}/book?cabinId=${cabin.cabinId}"
                                               class="btn btn-sm btn-primary">
                                                <i class="fas fa-calendar-plus"></i> Book
                                            </a>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>

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
                                <i class="fas fa-plus-circle"></i> New Booking
                            </a>
                            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-secondary">
                                <i class="fas fa-tachometer-alt"></i> Dashboard
                            </a>
                            <a href="${pageContext.request.contextPath}/profile" class="btn btn-outline-info">
                                <i class="fas fa-user-edit"></i> Edit Profile
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Cancellation Modal -->
    <div class="modal fade" id="cancelModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="fas fa-exclamation-triangle text-warning"></i> Cancel Booking
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to cancel your booking for <strong id="cancelCabinName"></strong>?</p>
                    <div class="alert alert-warning">
                        <small><i class="fas fa-info-circle"></i> This action cannot be undone.</small>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times"></i> Keep Booking
                    </button>
                    <button type="button" class="btn btn-danger" id="confirmCancel">
                        <i class="fas fa-trash"></i> Yes, Cancel Booking
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-light text-center py-3 mt-5">
        <div class="container">
            <p class="mb-0">&copy; 2024 Cabin Booking System with AI</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Booking cancellation functionality
        let bookingToCancel = null;

        document.querySelectorAll('.cancel-booking').forEach(button => {
            button.addEventListener('click', function() {
                bookingToCancel = this.dataset.bookingId;
                const cabinName = this.dataset.cabinName;

                document.getElementById('cancelCabinName').textContent = cabinName;

                const cancelModal = new bootstrap.Modal(document.getElementById('cancelModal'));
                cancelModal.show();
            });
        });

        document.getElementById('confirmCancel').addEventListener('click', function() {
            if (bookingToCancel) {
                // Show loading state
                this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Cancelling...';
                this.disabled = true;

                // Redirect to cancel URL
                window.location.href = `${pageContext.request.contextPath}/booking/cancel?bookingId=${bookingToCancel}`;
            }
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

        // Add animation to booking cards
        document.querySelectorAll('.booking-card').forEach(card => {
            card.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-2px)';
                this.style.boxShadow = '0 0.5rem 1rem rgba(0, 0, 0, 0.15)';
            });

            card.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(0)';
                this.style.boxShadow = '0 0.125rem 0.25rem rgba(0, 0, 0, 0.075)';
            });
        });
    </script>
</body>
</html>
