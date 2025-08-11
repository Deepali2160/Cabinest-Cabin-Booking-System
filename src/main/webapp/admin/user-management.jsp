<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - Admin Panel</title>
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
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fas fa-arrow-left"></i> Back to Dashboard
                </a>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container-fluid mt-4">

        <!-- Page Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card dashboard-card">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <h2 class="mb-2">
                                    <i class="fas fa-users"></i> User Management
                                </h2>
                                <p class="text-muted mb-0">
                                    Manage user accounts, permissions, and promote users
                                </p>
                            </div>
                            <div class="col-md-4 text-end">
                                <div class="btn-group">
                                    <button type="button" class="btn btn-primary dropdown-toggle" data-bs-toggle="dropdown">
                                        <i class="fas fa-plus"></i> Quick Actions
                                    </button>
                                    <ul class="dropdown-menu">
                                        <c:if test="${admin.userType == 'SUPER_ADMIN'}">
                                            <li><a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#promoteModal">
                                                <i class="fas fa-user-shield"></i> Promote to Admin</a></li>
                                        </c:if>
                                        <li><a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#promoteVipModal">
                                            <i class="fas fa-star"></i> Promote to VIP</a></li>
                                        <li><hr class="dropdown-divider"></li>
                                        <li><a class="dropdown-item" href="#" onclick="exportUsers()">
                                            <i class="fas fa-download"></i> Export Users</a></li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- User Statistics Cards -->
        <div class="row mb-4">
            <div class="col-md-3 mb-3">
                <div class="card dashboard-card border-primary">
                    <div class="card-body text-center">
                        <div class="display-6 text-primary mb-2">
                            <i class="fas fa-users"></i>
                        </div>
                        <h4 class="text-primary">${totalUsers}</h4>
                        <p class="text-muted mb-0">Total Users</p>
                    </div>
                </div>
            </div>

            <div class="col-md-3 mb-3">
                <div class="card dashboard-card border-secondary">
                    <div class="card-body text-center">
                        <div class="display-6 text-secondary mb-2">
                            <i class="fas fa-user"></i>
                        </div>
                        <h4 class="text-secondary">${normalUsers.size()}</h4>
                        <p class="text-muted mb-0">Normal Users</p>
                    </div>
                </div>
            </div>

            <div class="col-md-3 mb-3">
                <div class="card dashboard-card border-warning">
                    <div class="card-body text-center">
                        <div class="display-6 text-warning mb-2">
                            <i class="fas fa-star"></i>
                        </div>
                        <h4 class="text-warning">${vipUsers.size()}</h4>
                        <p class="text-muted mb-0">VIP Users</p>
                    </div>
                </div>
            </div>

            <div class="col-md-3 mb-3">
                <div class="card dashboard-card border-danger">
                    <div class="card-body text-center">
                        <div class="display-6 text-danger mb-2">
                            <i class="fas fa-user-shield"></i>
                        </div>
                        <h4 class="text-danger">${adminUsers.size()}</h4>
                        <p class="text-muted mb-0">Administrators</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- User Management Tabs -->
        <div class="row">
            <div class="col-12">
                <div class="card dashboard-card">
                    <div class="card-header">
                        <ul class="nav nav-pills card-header-pills" role="tablist">
                            <li class="nav-item">
                                <button class="nav-link active" data-bs-toggle="pill" data-bs-target="#all-users" type="button">
                                    <i class="fas fa-list"></i> All Users (${totalUsers})
                                </button>
                            </li>
                            <li class="nav-item">
                                <button class="nav-link" data-bs-toggle="pill" data-bs-target="#normal-users" type="button">
                                    <i class="fas fa-user"></i> Normal (${normalUsers.size()})
                                </button>
                            </li>
                            <li class="nav-item">
                                <button class="nav-link" data-bs-toggle="pill" data-bs-target="#vip-users" type="button">
                                    <i class="fas fa-star"></i> VIP (${vipUsers.size()})
                                </button>
                            </li>
                            <li class="nav-item">
                                <button class="nav-link" data-bs-toggle="pill" data-bs-target="#admin-users" type="button">
                                    <i class="fas fa-user-shield"></i> Admins (${adminUsers.size()})
                                </button>
                            </li>
                        </ul>
                    </div>

                    <div class="card-body">
                        <!-- Search Bar -->
                        <div class="row mb-3">
                            <div class="col-md-8">
                                <div class="input-group">
                                    <span class="input-group-text">
                                        <i class="fas fa-search"></i>
                                    </span>
                                    <input type="text" class="form-control" id="userSearch"
                                           placeholder="Search users by name, email, or company...">
                                </div>
                            </div>
                            <div class="col-md-4 text-end">
                                <select class="form-select" id="companyFilter">
                                    <option value="">All Companies</option>
                                    <c:forEach var="company" items="${allCompanies}">
                                        <option value="${company.companyId}">${company.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <!-- Tab Content -->
                        <div class="tab-content">

                            <!-- All Users Tab -->
                            <div class="tab-pane fade show active" id="all-users">
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead class="table-light">
                                            <tr>
                                                <th>User Details</th>
                                                <th>Company</th>
                                                <th>User Type</th>
                                                <th>Booking Stats</th>
                                                <th>Status</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody id="allUsersTable">
                                            <c:forEach var="user" items="${allUsers}">
                                                <tr class="user-row" data-user-name="${user.name}" data-user-email="${user.email}"
                                                    data-company-id="${user.defaultCompanyId}">
                                                    <td>
                                                        <div class="d-flex align-items-center">
                                                            <i class="fas fa-user-circle fa-2x text-muted me-3"></i>
                                                            <div>
                                                                <div class="fw-bold">${user.name}</div>
                                                                <small class="text-muted">${user.email}</small>
                                                                <br>
                                                                <small class="text-muted">ID: ${user.userId}</small>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <c:forEach var="company" items="${allCompanies}">
                                                            <c:if test="${company.companyId == user.defaultCompanyId}">
                                                                <div class="fw-bold">${company.name}</div>
                                                                <small class="text-muted">${company.location}</small>
                                                            </c:if>
                                                        </c:forEach>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${user.userType == 'SUPER_ADMIN'}">
                                                                <span class="badge bg-warning text-dark">Super Admin</span>
                                                            </c:when>
                                                            <c:when test="${user.userType == 'ADMIN'}">
                                                                <span class="badge bg-danger">Admin</span>
                                                            </c:when>
                                                            <c:when test="${user.userType == 'VIP'}">
                                                                <span class="badge badge-vip">VIP</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-primary">Normal</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <div class="text-center">
                                                            <div class="fw-bold text-primary">
                                                                ${userBookingCounts[user.userId] != null ? userBookingCounts[user.userId] : 0}
                                                            </div>
                                                            <small class="text-muted">Total Bookings</small>
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${user.status == 'ACTIVE'}">
                                                                <span class="badge bg-success">Active</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-secondary">Inactive</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <div class="btn-group btn-group-sm">
                                                            <c:if test="${user.userType == 'NORMAL' && admin.userType == 'SUPER_ADMIN'}">
                                                                <button class="btn btn-outline-warning promote-vip"
                                                                        data-user-id="${user.userId}" data-user-name="${user.name}">
                                                                    <i class="fas fa-star"></i>
                                                                </button>
                                                                <button class="btn btn-outline-danger promote-admin"
                                                                        data-user-id="${user.userId}" data-user-name="${user.name}">
                                                                    <i class="fas fa-user-shield"></i>
                                                                </button>
                                                            </c:if>

                                                            <c:if test="${user.userType == 'NORMAL'}">
                                                                <button class="btn btn-outline-warning promote-vip"
                                                                        data-user-id="${user.userId}" data-user-name="${user.name}">
                                                                    <i class="fas fa-star" title="Promote to VIP"></i>
                                                                </button>
                                                            </c:if>

                                                            <button class="btn btn-outline-info view-details"
                                                                    data-user-id="${user.userId}">
                                                                <i class="fas fa-eye"></i>
                                                            </button>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <!-- Normal Users Tab -->
                            <div class="tab-pane fade" id="normal-users">
                                <c:choose>
                                    <c:when test="${empty normalUsers}">
                                        <div class="text-center py-4">
                                            <i class="fas fa-user fa-3x text-muted mb-3"></i>
                                            <p class="text-muted">No normal users found</p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="row">
                                            <c:forEach var="user" items="${normalUsers}">
                                                <div class="col-md-6 col-lg-4 mb-3">
                                                    <div class="card border-primary">
                                                        <div class="card-body">
                                                            <div class="d-flex justify-content-between align-items-start mb-2">
                                                                <h6 class="card-title mb-0">${user.name}</h6>
                                                                <span class="badge bg-primary">Normal</span>
                                                            </div>
                                                            <p class="card-text small text-muted mb-2">${user.email}</p>
                                                            <p class="card-text small mb-3">
                                                                Bookings: <strong>${userBookingCounts[user.userId] != null ? userBookingCounts[user.userId] : 0}</strong>
                                                            </p>
                                                            <div class="btn-group w-100">
                                                                <button class="btn btn-outline-warning btn-sm promote-vip"
                                                                        data-user-id="${user.userId}" data-user-name="${user.name}">
                                                                    <i class="fas fa-star"></i> VIP
                                                                </button>
                                                                <c:if test="${admin.userType == 'SUPER_ADMIN'}">
                                                                    <button class="btn btn-outline-danger btn-sm promote-admin"
                                                                            data-user-id="${user.userId}" data-user-name="${user.name}">
                                                                        <i class="fas fa-user-shield"></i> Admin
                                                                    </button>
                                                                </c:if>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <!-- VIP Users Tab -->
                            <div class="tab-pane fade" id="vip-users">
                                <c:choose>
                                    <c:when test="${empty vipUsers}">
                                        <div class="text-center py-4">
                                            <i class="fas fa-star fa-3x text-warning mb-3"></i>
                                            <p class="text-muted">No VIP users found</p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="row">
                                            <c:forEach var="user" items="${vipUsers}">
                                                <div class="col-md-6 col-lg-4 mb-3">
                                                    <div class="card border-warning">
                                                        <div class="card-body">
                                                            <div class="d-flex justify-content-between align-items-start mb-2">
                                                                <h6 class="card-title mb-0">${user.name}</h6>
                                                                <span class="badge badge-vip">VIP</span>
                                                            </div>
                                                            <p class="card-text small text-muted mb-2">${user.email}</p>
                                                            <p class="card-text small mb-3">
                                                                Bookings: <strong>${userBookingCounts[user.userId] != null ? userBookingCounts[user.userId] : 0}</strong>
                                                            </p>
                                                            <c:if test="${admin.userType == 'SUPER_ADMIN'}">
                                                                <button class="btn btn-outline-danger btn-sm w-100 promote-admin"
                                                                        data-user-id="${user.userId}" data-user-name="${user.name}">
                                                                    <i class="fas fa-user-shield"></i> Promote to Admin
                                                                </button>
                                                            </c:if>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <!-- Admin Users Tab -->
                            <div class="tab-pane fade" id="admin-users">
                                <c:choose>
                                    <c:when test="${empty adminUsers}">
                                        <div class="text-center py-4">
                                            <i class="fas fa-user-shield fa-3x text-danger mb-3"></i>
                                            <p class="text-muted">No admin users found</p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="row">
                                            <c:forEach var="user" items="${adminUsers}">
                                                <div class="col-md-6 col-lg-4 mb-3">
                                                    <div class="card border-danger">
                                                        <div class="card-body">
                                                            <div class="d-flex justify-content-between align-items-start mb-2">
                                                                <h6 class="card-title mb-0">${user.name}</h6>
                                                                <c:choose>
                                                                    <c:when test="${user.userType == 'SUPER_ADMIN'}">
                                                                        <span class="badge bg-warning text-dark">Super Admin</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="badge bg-danger">Admin</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                            <p class="card-text small text-muted mb-2">${user.email}</p>
                                                            <p class="card-text small mb-0">
                                                                <i class="fas fa-shield-alt text-success"></i> Administrative Access
                                                            </p>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Promotion Modals -->
    <div class="modal fade" id="promotionModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="promotionModalTitle">Promote User</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="alert alert-warning">
                        <i class="fas fa-exclamation-triangle"></i>
                        <strong>Important:</strong> User promotions cannot be undone easily. Make sure you want to proceed.
                    </div>
                    <p>Are you sure you want to promote <strong id="promotionUserName"></strong> to <strong id="promotionNewRole"></strong>?</p>
                    <div class="text-muted small">
                        <p id="promotionDescription"></p>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" id="confirmPromotionBtn">Promote User</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Search functionality
        document.getElementById('userSearch').addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            filterUsers();
        });

        document.getElementById('companyFilter').addEventListener('change', function() {
            filterUsers();
        });

        function filterUsers() {
            const searchTerm = document.getElementById('userSearch').value.toLowerCase();
            const companyFilter = document.getElementById('companyFilter').value;
            const rows = document.querySelectorAll('.user-row');

            rows.forEach(row => {
                const userName = row.dataset.userName.toLowerCase();
                const userEmail = row.dataset.userEmail.toLowerCase();
                const companyId = row.dataset.companyId;

                const matchesSearch = userName.includes(searchTerm) || userEmail.includes(searchTerm);
                const matchesCompany = !companyFilter || companyId === companyFilter;

                if (matchesSearch && matchesCompany) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        }

        // Promotion functionality
        let promotionUserId = null;
        let promotionType = null;

        document.querySelectorAll('.promote-vip').forEach(button => {
            button.addEventListener('click', function() {
                promotionUserId = this.dataset.userId;
                const userName = this.dataset.userName;

                showPromotionModal(userName, 'VIP', 'VIP users get priority booking approval and access to VIP-only cabins.', 'VIP');
            });
        });

        document.querySelectorAll('.promote-admin').forEach(button => {
            button.addEventListener('click', function() {
                promotionUserId = this.dataset.userId;
                const userName = this.dataset.userName;

                showPromotionModal(userName, 'Administrator', 'Administrators can manage bookings and promote other users.', 'ADMIN');
            });
        });

        function showPromotionModal(userName, newRole, description, type) {
            document.getElementById('promotionUserName').textContent = userName;
            document.getElementById('promotionNewRole').textContent = newRole;
            document.getElementById('promotionDescription').textContent = description;

            promotionType = type;

            const modal = new bootstrap.Modal(document.getElementById('promotionModal'));
            modal.show();
        }

        document.getElementById('confirmPromotionBtn').addEventListener('click', function() {
            if (promotionUserId && promotionType) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = `${pageContext.request.contextPath}/admin/promote-user`;

                const userIdInput = document.createElement('input');
                userIdInput.type = 'hidden';
                userIdInput.name = 'userId';
                userIdInput.value = promotionUserId;

                const userTypeInput = document.createElement('input');
                userTypeInput.type = 'hidden';
                userTypeInput.name = 'userType';
                userTypeInput.value = promotionType;

                form.appendChild(userIdInput);
                form.appendChild(userTypeInput);

                document.body.appendChild(form);
                form.submit();
            }
        });

        // Export users functionality
        function exportUsers() {
            const csvContent = generateUserCSV();
            downloadCSV(csvContent, 'users_export.csv');
        }

        function generateUserCSV() {
            const users = document.querySelectorAll('.user-row');
            let csv = 'Name,Email,User Type,Company,Bookings,Status\n';

            users.forEach(row => {
                const name = row.dataset.userName;
                const email = row.dataset.userEmail;
                const userType = row.querySelector('td:nth-child(3) .badge').textContent;
                const company = row.querySelector('td:nth-child(2) .fw-bold').textContent;
                const bookings = row.querySelector('td:nth-child(4) .fw-bold').textContent;
                const status = row.querySelector('td:nth-child(5) .badge').textContent;

                csv += `"${name}","${email}","${userType}","${company}","${bookings}","${status}"\n`;
            });

            return csv;
        }

        function downloadCSV(csvContent, fileName) {
            const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');

            if (link.download !== undefined) {
                const url = URL.createObjectURL(blob);
                link.setAttribute('href', url);
                link.setAttribute('download', fileName);
                link.style.visibility = 'hidden';
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
            }
        }

        // Initialize tooltips
        const tooltips = document.querySelectorAll('[data-bs-toggle="tooltip"]');
        tooltips.forEach(tooltip => {
            new bootstrap.Tooltip(tooltip);
        });
    </script>
</body>
</html>
