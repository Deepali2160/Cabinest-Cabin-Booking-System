<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - Yash Technology Admin</title>
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/admin-user-management.css" rel="stylesheet">
</head>
<body>
    <!-- Admin Navigation -->
    <nav class="admin-nav">
        <div class="nav-container">
            <div class="nav-brand">
                <h2>🔧 Yash Technology Admin</h2>
                <span>User Management</span>
            </div>

            <div class="nav-menu">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link">
                    📊 Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/admin/bookings" class="nav-link">
                    📅 Manage Bookings
                </a>
                <a href="${pageContext.request.contextPath}/admin/manage-cabins" class="nav-link">
                    🏠 Manage Cabins
                </a>
                <a href="${pageContext.request.contextPath}/admin/users" class="nav-link active">
                    👥 User Management
                </a>
            </div>

            <div class="nav-user">
                <div class="admin-info">
                    <span class="admin-name">${admin.name}</span>
                    <c:choose>
                        <c:when test="${admin.userType == 'SUPER_ADMIN'}">
                            <span class="role-badge super-admin">👑 Super Admin</span>
                        </c:when>
                        <c:otherwise>
                            <span class="role-badge admin">👨‍💼 Admin</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="user-dropdown">
                    <button class="dropdown-btn" id="userDropdown">⚙️</button>
                    <div class="dropdown-menu" id="userDropdownMenu">
                        <a href="${pageContext.request.contextPath}/dashboard">👤 User Dashboard</a>
                        <a href="${pageContext.request.contextPath}/logout">🚪 Logout</a>
                    </div>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <main class="admin-main">

        <!-- Page Header -->
        <section class="page-header">
            <div class="header-content">
                <div class="header-info">
                    <h1>👥 User Management</h1>
                    <p>Manage user accounts, permissions, and roles at Yash Technology - Indore</p>
                </div>

                <div class="header-actions">
                    <div class="action-dropdown">
                        <button class="action-btn primary" id="quickActionsBtn">
                            ⚡ Quick Actions ▼
                        </button>
                        <div class="action-menu" id="quickActionsMenu">
                            <c:if test="${admin.userType == 'SUPER_ADMIN'}">
                                <button class="action-item" data-action="promote-admin">
                                    👨‍💼 Promote to Admin
                                </button>
                            </c:if>
                            <button class="action-item" data-action="promote-vip">
                                ⭐ Promote to VIP
                            </button>
                            <div class="action-divider"></div>
                            <button class="action-item" data-action="export">
                                📊 Export Users
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- User Statistics -->
        <section class="stats-section">
            <div class="stats-grid">
                <div class="stat-card total">
                    <div class="stat-icon">👥</div>
                    <div class="stat-content">
                        <div class="stat-number">${totalUsers}</div>
                        <div class="stat-label">Total Users</div>
                    </div>
                </div>

                <div class="stat-card normal">
                    <div class="stat-icon">👤</div>
                    <div class="stat-content">
                        <div class="stat-number">${normalUsers.size()}</div>
                        <div class="stat-label">Normal Users</div>
                    </div>
                </div>

                <div class="stat-card vip">
                    <div class="stat-icon">⭐</div>
                    <div class="stat-content">
                        <div class="stat-number">${vipUsers.size()}</div>
                        <div class="stat-label">VIP Users</div>
                    </div>
                </div>

                <div class="stat-card admin">
                    <div class="stat-icon">👨‍💼</div>
                    <div class="stat-content">
                        <div class="stat-number">${adminUsers.size()}</div>
                        <div class="stat-label">Administrators</div>
                    </div>
                </div>
            </div>
        </section>

        <!-- User Management Content -->
        <section class="users-section">
            <div class="users-card">

                <!-- Tab Navigation -->
                <div class="tab-nav">
                    <button class="tab-btn active" data-tab="all">
                        📋 All Users (${totalUsers})
                    </button>
                    <button class="tab-btn" data-tab="normal">
                        👤 Normal (${normalUsers.size()})
                    </button>
                    <button class="tab-btn" data-tab="vip">
                        ⭐ VIP (${vipUsers.size()})
                    </button>
                    <button class="tab-btn" data-tab="admin">
                        👨‍💼 Admins (${adminUsers.size()})
                    </button>
                </div>

                <!-- Search and Filter -->
                <div class="search-section">
                    <div class="search-bar">
                        <div class="search-input-container">
                            <span class="search-icon">🔍</span>
                            <input type="text" id="userSearch" class="search-input"
                                   placeholder="Search users by name or email...">
                        </div>
                    </div>
                    <div class="search-info">
                        Showing users from <strong>Yash Technology - Indore</strong>
                    </div>
                </div>

                <!-- Tab Content -->
                <div class="tab-content">

                    <!-- All Users Tab -->
                    <div class="tab-panel active" id="all-tab">
                        <c:choose>
                            <c:when test="${empty allUsers}">
                                <div class="empty-state">
                                    <div class="empty-icon">👥</div>
                                    <h3>No users found</h3>
                                    <p>There are no registered users in the system.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="users-table">
                                    <div class="table-header">
                                        <div class="header-cell">User Details</div>
                                        <div class="header-cell">User Type</div>
                                        <div class="header-cell">Bookings</div>
                                        <div class="header-cell">Status</div>
                                        <div class="header-cell">Actions</div>
                                    </div>

                                    <div class="table-body">
                                        <c:forEach var="user" items="${allUsers}">
                                            <div class="table-row user-row"
                                                 data-user-name="${user.name}"
                                                 data-user-email="${user.email}">
                                                <div class="table-cell user-cell">
                                                    <div class="user-info">
                                                        <div class="user-avatar">👤</div>
                                                        <div class="user-details">
                                                            <div class="user-name">${user.name}</div>
                                                            <div class="user-email">${user.email}</div>
                                                            <div class="user-id">ID: ${user.userId}</div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="table-cell type-cell">
                                                    <c:choose>
                                                        <c:when test="${user.userType == 'SUPER_ADMIN'}">
                                                            <span class="user-badge super-admin">👑 Super Admin</span>
                                                        </c:when>
                                                        <c:when test="${user.userType == 'ADMIN'}">
                                                            <span class="user-badge admin">👨‍💼 Admin</span>
                                                        </c:when>
                                                        <c:when test="${user.userType == 'VIP'}">
                                                            <span class="user-badge vip">⭐ VIP</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="user-badge normal">👤 Normal</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>

                                                <div class="table-cell bookings-cell">
                                                    <div class="booking-count">
                                                        <div class="count-number">
                                                            ${userBookingCounts[user.userId] != null ? userBookingCounts[user.userId] : 0}
                                                        </div>
                                                        <div class="count-label">Bookings</div>
                                                    </div>
                                                </div>

                                                <div class="table-cell status-cell">
                                                    <c:choose>
                                                        <c:when test="${user.status == 'ACTIVE'}">
                                                            <span class="status-badge active">✅ Active</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="status-badge inactive">❌ Inactive</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>

                                                <div class="table-cell actions-cell">
                                                    <div class="action-buttons">
                                                        <c:if test="${user.userType == 'NORMAL'}">
                                                            <button class="action-btn promote promote-vip"
                                                                    data-user-id="${user.userId}"
                                                                    data-user-name="${user.name}"
                                                                    title="Promote to VIP">
                                                                ⭐ VIP
                                                            </button>
                                                        </c:if>

                                                        <c:if test="${user.userType == 'NORMAL' && admin.userType == 'SUPER_ADMIN'}">
                                                            <button class="action-btn promote promote-admin"
                                                                    data-user-id="${user.userId}"
                                                                    data-user-name="${user.name}"
                                                                    title="Promote to Admin">
                                                                👨‍💼 Admin
                                                            </button>
                                                        </c:if>

                                                        <c:if test="${user.userType == 'VIP' && admin.userType == 'SUPER_ADMIN'}">
                                                            <button class="action-btn promote promote-admin"
                                                                    data-user-id="${user.userId}"
                                                                    data-user-name="${user.name}"
                                                                    title="Promote to Admin">
                                                                👨‍💼 Admin
                                                            </button>
                                                        </c:if>

                                                        <button class="action-btn view view-details"
                                                                data-user-id="${user.userId}"
                                                                title="View Details">
                                                            👁️ View
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Normal Users Tab -->
                    <div class="tab-panel" id="normal-tab">
                        <c:choose>
                            <c:when test="${empty normalUsers}">
                                <div class="empty-state">
                                    <div class="empty-icon">👤</div>
                                    <h3>No normal users</h3>
                                    <p>There are no normal users in the system.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="user-cards-grid">
                                    <c:forEach var="user" items="${normalUsers}">
                                        <div class="user-card normal-card">
                                            <div class="card-header">
                                                <div class="user-avatar">👤</div>
                                                <div class="user-info">
                                                    <h4>${user.name}</h4>
                                                    <p>${user.email}</p>
                                                </div>
                                                <span class="user-badge normal">👤 Normal</span>
                                            </div>

                                            <div class="card-body">
                                                <div class="user-stats">
                                                    <div class="stat-item">
                                                        <span class="stat-value">
                                                            ${userBookingCounts[user.userId] != null ? userBookingCounts[user.userId] : 0}
                                                        </span>
                                                        <span class="stat-label">Bookings</span>
                                                    </div>
                                                    <div class="stat-item">
                                                        <span class="stat-value">
                                                            <c:choose>
                                                                <c:when test="${user.status == 'ACTIVE'}">✅</c:when>
                                                                <c:otherwise>❌</c:otherwise>
                                                            </c:choose>
                                                        </span>
                                                        <span class="stat-label">Status</span>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="card-actions">
                                                <button class="card-action-btn vip promote-vip"
                                                        data-user-id="${user.userId}"
                                                        data-user-name="${user.name}">
                                                    ⭐ Promote to VIP
                                                </button>
                                                <c:if test="${admin.userType == 'SUPER_ADMIN'}">
                                                    <button class="card-action-btn admin promote-admin"
                                                            data-user-id="${user.userId}"
                                                            data-user-name="${user.name}">
                                                        👨‍💼 Promote to Admin
                                                    </button>
                                                </c:if>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- VIP Users Tab -->
                    <div class="tab-panel" id="vip-tab">
                        <c:choose>
                            <c:when test="${empty vipUsers}">
                                <div class="empty-state">
                                    <div class="empty-icon">⭐</div>
                                    <h3>No VIP users</h3>
                                    <p>There are no VIP users in the system.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="user-cards-grid">
                                    <c:forEach var="user" items="${vipUsers}">
                                        <div class="user-card vip-card">
                                            <div class="card-header">
                                                <div class="user-avatar">👤</div>
                                                <div class="user-info">
                                                    <h4>${user.name}</h4>
                                                    <p>${user.email}</p>
                                                </div>
                                                <span class="user-badge vip">⭐ VIP</span>
                                            </div>

                                            <div class="card-body">
                                                <div class="user-stats">
                                                    <div class="stat-item">
                                                        <span class="stat-value">
                                                            ${userBookingCounts[user.userId] != null ? userBookingCounts[user.userId] : 0}
                                                        </span>
                                                        <span class="stat-label">Bookings</span>
                                                    </div>
                                                    <div class="stat-item">
                                                        <span class="stat-value">🌟</span>
                                                        <span class="stat-label">Priority</span>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="card-actions">
                                                <c:if test="${admin.userType == 'SUPER_ADMIN'}">
                                                    <button class="card-action-btn admin promote-admin"
                                                            data-user-id="${user.userId}"
                                                            data-user-name="${user.name}">
                                                        👨‍💼 Promote to Admin
                                                    </button>
                                                </c:if>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Admin Users Tab -->
                    <div class="tab-panel" id="admin-tab">
                        <c:choose>
                            <c:when test="${empty adminUsers}">
                                <div class="empty-state">
                                    <div class="empty-icon">👨‍💼</div>
                                    <h3>No admin users</h3>
                                    <p>There are no admin users in the system.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="user-cards-grid">
                                    <c:forEach var="user" items="${adminUsers}">
                                        <div class="user-card admin-card">
                                            <div class="card-header">
                                                <div class="user-avatar">👤</div>
                                                <div class="user-info">
                                                    <h4>${user.name}</h4>
                                                    <p>${user.email}</p>
                                                </div>
                                                <c:choose>
                                                    <c:when test="${user.userType == 'SUPER_ADMIN'}">
                                                        <span class="user-badge super-admin">👑 Super Admin</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="user-badge admin">👨‍💼 Admin</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>

                                            <div class="card-body">
                                                <div class="admin-privileges">
                                                    <div class="privilege-item">
                                                        🛡️ Administrative Access
                                                    </div>
                                                    <div class="privilege-item">
                                                        📋 Booking Management
                                                    </div>
                                                    <div class="privilege-item">
                                                        👥 User Management
                                                    </div>
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
        </section>
    </main>

    <!-- Promotion Modal -->
    <div class="modal-overlay" id="promotionModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="promotionModalTitle">👤 Promote User</h3>
                <button class="modal-close" id="modalClose">×</button>
            </div>

            <div class="modal-body">
                <div class="promotion-warning">
                    ⚠️ <strong>Important:</strong> User promotions cannot be undone easily. Make sure you want to proceed.
                </div>

                <div class="promotion-info">
                    <p>Are you sure you want to promote <strong id="promotionUserName"></strong> to <strong id="promotionNewRole"></strong>?</p>
                    <div class="promotion-description" id="promotionDescription"></div>
                </div>
            </div>

            <div class="modal-footer">
                <button class="modal-btn secondary" id="cancelPromotion">
                    ↩️ Cancel
                </button>
                <button class="modal-btn primary" id="confirmPromotion">
                    🚀 Promote User
                </button>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="admin-footer">
        <p>&copy; 2024 Yash Technology - User Management System</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/admin-user-management.js"></script>
</body>
</html>
