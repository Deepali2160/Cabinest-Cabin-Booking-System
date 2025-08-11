<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Cabin Booking System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">

    <!-- Custom CSS for admin dashboard -->
    <style>
        .dashboard-card {
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
            border: 1px solid rgba(0, 0, 0, 0.125);
            transition: all 0.3s ease;
        }

        .dashboard-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
        }

        .stat-number {
            font-size: 2.5rem;
            font-weight: 700;
        }

        .stat-label {
            font-size: 0.875rem;
            color: #6c757d;
        }

        .badge-pending {
            background-color: #ffc107;
            color: #212529;
        }

        .badge-approved {
            background-color: #198754;
        }

        .badge-rejected {
            background-color: #dc3545;
        }

        .badge-vip {
            background-color: #fd7e14;
        }

        .bg-gradient {
            background: linear-gradient(135deg, #343a40 0%, #495057 100%) !important;
        }

        .cabin-action-card {
            transition: all 0.3s ease;
            cursor: pointer;
        }

        .cabin-action-card:hover {
            transform: scale(1.05);
            box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
        }
    </style>
</head>
<body>
    <!-- Admin Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="fas fa-cogs"></i> Admin Panel
            </a>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#adminNav">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="adminNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/admin/dashboard">
                            <i class="fas fa-tachometer-alt"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/bookings">
                            <i class="fas fa-calendar-check"></i> Manage Bookings
                        </a>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                            <i class="fas fa-home"></i> Cabin Management
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/add-cabin">
                                <i class="fas fa-plus"></i> Add New Cabin</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/manage-cabins">
                                <i class="fas fa-list"></i> Manage Cabins</a></li>
                        </ul>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/users">
                            <i class="fas fa-users"></i> User Management
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/analytics">
                            <i class="fas fa-chart-bar"></i> Analytics
                        </a>
                    </li>
                    <c:if test="${admin.userType == 'SUPER_ADMIN'}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/admin/companies">
                                <i class="fas fa-building"></i> Companies
                            </a>
                        </li>
                    </c:if>
                </ul>

                <ul class="navbar-nav">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                            <i class="fas fa-user-shield"></i> ${admin.name}
                            <c:choose>
                                <c:when test="${admin.userType == 'SUPER_ADMIN'}">
                                    <span class="badge bg-warning text-dark ms-1">SUPER ADMIN</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger ms-1">ADMIN</span>
                                </c:otherwise>
                            </c:choose>
                        </a>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/dashboard">
                                <i class="fas fa-user"></i> User Dashboard</a></li>
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
    <div class="container-fluid mt-4">

        <!-- Admin Welcome Section -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card dashboard-card">
                    <div class="card-body bg-gradient text-white">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <h2 class="mb-2">
                                    <i class="fas fa-shield-alt"></i> Welcome, ${admin.name}!
                                </h2>
                                <p class="mb-2">
                                    <c:choose>
                                        <c:when test="${admin.userType == 'SUPER_ADMIN'}">
                                            <i class="fas fa-crown"></i> Super Administrator - Full System Access
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fas fa-user-shield"></i> Administrator - Booking Management Access
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                                <p class="mb-0">
                                    <i class="fas fa-clock"></i> Last Login: Recently
                                </p>
                            </div>
                            <div class="col-md-4 text-center">
                                <div class="stat-number">${pendingCount}</div>
                                <div class="stat-label">Pending Approvals</div>
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

        <!-- Quick Stats Cards -->
        <div class="row mb-4">
            <div class="col-md-3 col-sm-6 mb-3">
                <div class="card dashboard-card border-primary">
                    <div class="card-body text-center">
                        <div class="display-6 text-primary mb-2">
                            <i class="fas fa-clock"></i>
                        </div>
                        <h4 class="text-primary">${pendingCount}</h4>
                        <p class="text-muted mb-0">Pending Bookings</p>
                        <a href="${pageContext.request.contextPath}/admin/bookings?filter=pending"
                           class="btn btn-outline-primary btn-sm mt-2">
                            <i class="fas fa-eye"></i> Review
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-3 col-sm-6 mb-3">
                <div class="card dashboard-card border-warning">
                    <div class="card-body text-center">
                        <div class="display-6 text-warning mb-2">
                            <i class="fas fa-star"></i>
                        </div>
                        <h4 class="text-warning">${not empty vipBookings ? vipBookings.size() : 0}</h4>
                        <p class="text-muted mb-0">VIP Priority</p>
                        <a href="${pageContext.request.contextPath}/admin/bookings?filter=vip"
                           class="btn btn-outline-warning btn-sm mt-2">
                            <i class="fas fa-crown"></i> Priority
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-3 col-sm-6 mb-3">
                <div class="card dashboard-card border-success">
                    <div class="card-body text-center">
                        <div class="display-6 text-success mb-2">
                            <i class="fas fa-users"></i>
                        </div>
                        <h4 class="text-success">${totalUsers}</h4>
                        <p class="text-muted mb-0">Total Users</p>
                        <a href="${pageContext.request.contextPath}/admin/users"
                           class="btn btn-outline-success btn-sm mt-2">
                            <i class="fas fa-cog"></i> Manage
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-3 col-sm-6 mb-3">
                <div class="card dashboard-card border-info">
                    <div class="card-body text-center">
                        <div class="display-6 text-info mb-2">
                            <i class="fas fa-calendar-check"></i>
                        </div>
                        <h4 class="text-info">${totalBookings}</h4>
                        <p class="text-muted mb-0">Total Bookings</p>
                        <a href="${pageContext.request.contextPath}/admin/analytics"
                           class="btn btn-outline-info btn-sm mt-2">
                            <i class="fas fa-chart-line"></i> Analytics
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- ✅ NEW: Cabin Management Quick Actions Section -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card dashboard-card border-success">
                    <div class="card-header bg-success text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-home"></i> Cabin Management Hub
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row g-3">
                            <div class="col-md-3">
                                <div class="card cabin-action-card border-primary h-100"
                                     onclick="window.location.href='${pageContext.request.contextPath}/admin/add-cabin'">
                                    <div class="card-body text-center">
                                        <i class="fas fa-plus-circle fa-3x text-primary mb-2"></i>
                                        <h6 class="text-primary">Add New Cabin</h6>
                                        <small class="text-muted">Create new meeting rooms</small>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="card cabin-action-card border-info h-100"
                                     onclick="window.location.href='${pageContext.request.contextPath}/admin/manage-cabins'">
                                    <div class="card-body text-center">
                                        <i class="fas fa-list-alt fa-3x text-info mb-2"></i>
                                        <h6 class="text-info">Manage Cabins</h6>
                                        <small class="text-muted">Edit, view, delete cabins</small>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="card cabin-action-card border-warning h-100"
                                     onclick="window.location.href='${pageContext.request.contextPath}/admin/manage-cabins?status=MAINTENANCE'">
                                    <div class="card-body text-center">
                                        <i class="fas fa-tools fa-3x text-warning mb-2"></i>
                                        <h6 class="text-warning">Maintenance</h6>
                                        <small class="text-muted">Cabins under maintenance</small>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="card cabin-action-card border-secondary h-100"
                                     onclick="window.location.href='${pageContext.request.contextPath}/admin/manage-cabins?filter=vip'">
                                    <div class="card-body text-center">
                                        <i class="fas fa-crown fa-3x text-secondary mb-2"></i>
                                        <h6 class="text-secondary">VIP Cabins</h6>
                                        <small class="text-muted">Premium access cabins</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <!-- Recent Bookings -->
            <div class="col-md-8 mb-4">
                <div class="card dashboard-card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">
                            <i class="fas fa-list"></i> Recent Bookings
                        </h5>
                        <span class="badge bg-primary">${not empty recentBookings ? recentBookings.size() : 0} items</span>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty recentBookings}">
                                <div class="text-center py-4">
                                    <i class="fas fa-calendar-times fa-3x text-muted mb-3"></i>
                                    <p class="text-muted">No recent bookings available</p>
                                    <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-primary">
                                        <i class="fas fa-plus"></i> View All Bookings
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead class="table-light">
                                            <tr>
                                                <th>User</th>
                                                <th>Cabin</th>
                                                <th>Date & Time</th>
                                                <th>Status</th>
                                                <th>Priority</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="booking" items="${recentBookings}">
                                                <tr>
                                                    <td>
                                                        <div class="d-flex align-items-center">
                                                            <i class="fas fa-user-circle fa-2x text-muted me-2"></i>
                                                            <div>
                                                                <div class="fw-bold">${booking.userName}</div>
                                                                <small class="text-muted">ID: ${booking.userId}</small>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <div class="fw-bold">${booking.cabinName}</div>
                                                        <small class="text-muted">${booking.purpose}</small>
                                                    </td>
                                                    <td>
                                                        <div>
                                                            <c:choose>
                                                                <c:when test="${not empty booking.bookingDate}">
                                                                    <fmt:formatDate value="${booking.bookingDate}" pattern="MMM dd, yyyy"/>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    Date TBD
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                        <small class="text-muted">${booking.timeSlot}</small>
                                                    </td>
                                                    <td>
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
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${booking.priorityLevel == 'VIP'}">
                                                                <span class="badge badge-vip">VIP</span>
                                                            </c:when>
                                                            <c:when test="${booking.priorityLevel == 'HIGH'}">
                                                                <span class="badge bg-warning">High</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-secondary">Normal</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:if test="${booking.status == 'PENDING'}">
                                                            <div class="btn-group btn-group-sm" role="group">
                                                                <button type="button" class="btn btn-success approve-booking"
                                                                        data-booking-id="${booking.bookingId}"
                                                                        title="Approve Booking">
                                                                    <i class="fas fa-check"></i>
                                                                </button>
                                                                <button type="button" class="btn btn-danger reject-booking"
                                                                        data-booking-id="${booking.bookingId}"
                                                                        title="Reject Booking">
                                                                    <i class="fas fa-times"></i>
                                                                </button>
                                                            </div>
                                                        </c:if>
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

            <!-- Admin Sidebar -->
            <div class="col-md-4">

                <!-- Today's Summary -->
                <div class="card dashboard-card mb-4">
                    <div class="card-header">
                        <h6 class="mb-0">
                            <i class="fas fa-calendar-day"></i> Today's Summary
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="row text-center">
                            <div class="col-6 mb-3">
                                <div class="stat-number text-info">${not empty todayBookings ? todayBookings.size() : 0}</div>
                                <div class="stat-label small">Today's Bookings</div>
                            </div>
                            <div class="col-6 mb-3">
                                <div class="stat-number text-warning">${pendingCount}</div>
                                <div class="stat-label small">Need Approval</div>
                            </div>
                        </div>

                        <div class="text-center">
                            <small class="text-muted">
                                <jsp:useBean id="currentDate" class="java.util.Date" />
                                <fmt:formatDate value="${currentDate}" pattern="EEEE, MMM dd, yyyy"/>
                            </small>
                        </div>
                    </div>
                </div>

                <!-- ✅ NEW: Cabin Statistics -->
                <div class="card dashboard-card mb-4">
                    <div class="card-header">
                        <h6 class="mb-0">
                            <i class="fas fa-home"></i> Cabin Statistics
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="row text-center">
                            <div class="col-6 mb-2">
                                <div class="h5 text-primary">${totalCabins != null ? totalCabins : 0}</div>
                                <div class="small text-muted">Total Cabins</div>
                            </div>
                            <div class="col-6 mb-2">
                                <div class="h5 text-success">${activeCabins != null ? activeCabins : 0}</div>
                                <div class="small text-muted">Active</div>
                            </div>
                            <div class="col-6 mb-2">
                                <div class="h5 text-warning">${vipCabins != null ? vipCabins : 0}</div>
                                <div class="small text-muted">VIP Only</div>
                            </div>
                            <div class="col-6 mb-2">
                                <div class="h5 text-info">${maintenanceCabins != null ? maintenanceCabins : 0}</div>
                                <div class="small text-muted">Maintenance</div>
                            </div>
                        </div>
                        <hr>
                        <div class="d-grid">
                            <a href="${pageContext.request.contextPath}/admin/manage-cabins"
                               class="btn btn-outline-primary btn-sm">
                                <i class="fas fa-eye"></i> View All Cabins
                            </a>
                        </div>
                    </div>
                </div>

                <!-- User Type Breakdown -->
                <div class="card dashboard-card mb-4">
                    <div class="card-header">
                        <h6 class="mb-0">
                            <i class="fas fa-pie-chart"></i> User Distribution
                        </h6>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${totalUsers > 0}">
                                <div class="mb-3">
                                    <div class="d-flex justify-content-between mb-1">
                                        <span>Normal Users</span>
                                        <span>${normalUsers}</span>
                                    </div>
                                    <div class="progress" style="height: 8px;">
                                        <div class="progress-bar bg-primary" style="width: ${normalUsers * 100 / totalUsers}%"></div>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <div class="d-flex justify-content-between mb-1">
                                        <span>VIP Users</span>
                                        <span>${vipUsers}</span>
                                    </div>
                                    <div class="progress" style="height: 8px;">
                                        <div class="progress-bar bg-warning" style="width: ${vipUsers * 100 / totalUsers}%"></div>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <div class="d-flex justify-content-between mb-1">
                                        <span>Admins</span>
                                        <span>${adminUsers}</span>
                                    </div>
                                    <div class="progress" style="height: 8px;">
                                        <div class="progress-bar bg-danger" style="width: ${adminUsers * 100 / totalUsers}%"></div>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-3">
                                    <i class="fas fa-users fa-2x text-muted mb-2"></i>
                                    <p class="text-muted mb-0">No user data available</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Enhanced Quick Actions -->
                <div class="card dashboard-card">
                    <div class="card-header">
                        <h6 class="mb-0">
                            <i class="fas fa-bolt"></i> Quick Actions
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="d-grid gap-2">
                            <!-- ✅ NEW: Cabin Management Actions -->
                            <a href="${pageContext.request.contextPath}/admin/add-cabin"
                               class="btn btn-outline-primary">
                                <i class="fas fa-plus"></i> Add New Cabin
                            </a>

                            <a href="${pageContext.request.contextPath}/admin/manage-cabins"
                               class="btn btn-outline-info">
                                <i class="fas fa-home"></i> Manage Cabins
                            </a>

                            <!-- Existing Actions -->
                            <a href="${pageContext.request.contextPath}/admin/bookings?filter=pending"
                               class="btn btn-primary">
                                <i class="fas fa-clock"></i> Review Pending (${pendingCount})
                            </a>

                            <a href="${pageContext.request.contextPath}/admin/bookings?filter=vip"
                               class="btn btn-warning">
                                <i class="fas fa-star"></i> VIP Priority (${not empty vipBookings ? vipBookings.size() : 0})
                            </a>

                            <a href="${pageContext.request.contextPath}/admin/users"
                               class="btn btn-success">
                                <i class="fas fa-users"></i> Manage Users
                            </a>

                            <c:if test="${admin.userType == 'SUPER_ADMIN'}">
                                <a href="${pageContext.request.contextPath}/admin/companies"
                                   class="btn btn-info">
                                    <i class="fas fa-building"></i> Manage Companies
                                </a>
                            </c:if>

                            <a href="${pageContext.request.contextPath}/admin/analytics"
                               class="btn btn-outline-secondary">
                                <i class="fas fa-chart-bar"></i> View Analytics
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
            <p class="mb-0">&copy; 2024 Cabin Booking System - Admin Panel | Logged in as: ${admin.name}</p>
        </div>
    </footer>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Quick approve/reject functionality
        document.querySelectorAll('.approve-booking').forEach(button => {
            button.addEventListener('click', function() {
                const bookingId = this.dataset.bookingId;
                if (confirm('Are you sure you want to approve this booking?')) {
                    window.location.href = '${pageContext.request.contextPath}/admin/approve-booking?bookingId=' + bookingId;
                }
            });
        });

        document.querySelectorAll('.reject-booking').forEach(button => {
            button.addEventListener('click', function() {
                const bookingId = this.dataset.bookingId;
                if (confirm('Are you sure you want to reject this booking?')) {
                    window.location.href = '${pageContext.request.contextPath}/admin/reject-booking?bookingId=' + bookingId;
                }
            });
        });

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

        // Console log for debugging (remove in production)
        console.log('Admin Dashboard loaded successfully');
        console.log('Pending Count: ${pendingCount}');
        console.log('Total Users: ${totalUsers}');
    </script>
</body>
</html>
