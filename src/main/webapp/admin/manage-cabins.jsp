<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Cabins - Yash Technology Admin</title>
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/admin-manage-cabins.css" rel="stylesheet">
</head>
<body>
    <!-- Admin Navigation -->
    <nav class="admin-nav">
        <div class="nav-container">
            <div class="nav-brand">
                <h2>🔧 Yash Technology Admin</h2>
                <span>Cabin Management</span>
            </div>

            <div class="nav-menu">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link">
                    📊 Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/admin/bookings" class="nav-link">
                    📅 Manage Bookings
                </a>
                <a href="${pageContext.request.contextPath}/admin/manage-cabins" class="nav-link active">
                    🏠 Manage Cabins
                </a>
                <a href="${pageContext.request.contextPath}/admin/users" class="nav-link">
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
                    <h1>🏠 Cabin Management</h1>
                    <p>Manage meeting rooms and cabins at Yash Technology - Indore</p>
                </div>

                <div class="header-actions">
                    <a href="${pageContext.request.contextPath}/admin/add-cabin" class="action-btn primary">
                        ➕ Add New Cabin
                    </a>
                </div>
            </div>
        </section>

        <!-- Messages -->
        <div id="message-container">
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="message success-message">
                    ✅ ${sessionScope.successMessage}
                </div>
                <c:remove var="successMessage" scope="session"/>
            </c:if>

            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="message error-message">
                    ❌ ${sessionScope.errorMessage}
                </div>
                <c:remove var="errorMessage" scope="session"/>
            </c:if>
        </div>

        <!-- Filters Section -->
        <section class="filters-section">
            <div class="filters-card">
                <form method="get" action="${pageContext.request.contextPath}/admin/manage-cabins" class="filters-form">
                    <div class="filter-group">
                        <label for="status">🔍 Filter by Status</label>
                        <select name="status" id="status" class="filter-select">
                            <option value="">All Status</option>
                            <option value="ACTIVE" ${selectedStatus eq 'ACTIVE' ? 'selected' : ''}>✅ Active</option>
                            <option value="MAINTENANCE" ${selectedStatus eq 'MAINTENANCE' ? 'selected' : ''}>🔧 Maintenance</option>
                            <option value="INACTIVE" ${selectedStatus eq 'INACTIVE' ? 'selected' : ''}>❌ Inactive</option>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label for="accessLevel">👥 Filter by Access</label>
                        <select name="accessLevel" id="accessLevel" class="filter-select">
                            <option value="">All Access Levels</option>
                            <option value="ALL" ${selectedAccessLevel eq 'ALL' ? 'selected' : ''}>👤 All Users</option>
                            <option value="VIP" ${selectedAccessLevel eq 'VIP' ? 'selected' : ''}>⭐ VIP Only</option>
                        </select>
                    </div>

                    <div class="filter-actions">
                        <button type="submit" class="filter-btn apply">
                            🔍 Apply Filters
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/manage-cabins" class="filter-btn clear">
                            ↩️ Clear Filters
                        </a>
                    </div>
                </form>
            </div>
        </section>

        <!-- Statistics Cards -->
        <section class="stats-section">
            <div class="stats-grid">
                <div class="stat-card total">
                    <div class="stat-icon">🏠</div>
                    <div class="stat-content">
                        <div class="stat-number">${totalCabins}</div>
                        <div class="stat-label">Total Cabins</div>
                    </div>
                    <div class="stat-trend">
                        <span class="trend-indicator">📊</span>
                    </div>
                </div>

                <div class="stat-card active">
                    <div class="stat-icon">✅</div>
                    <div class="stat-content">
                        <div class="stat-number">${activeCabins}</div>
                        <div class="stat-label">Active Cabins</div>
                    </div>
                    <div class="stat-trend">
                        <span class="trend-indicator positive">↗️</span>
                    </div>
                </div>

                <div class="stat-card vip">
                    <div class="stat-icon">⭐</div>
                    <div class="stat-content">
                        <div class="stat-number">${vipCabins}</div>
                        <div class="stat-label">VIP Cabins</div>
                    </div>
                    <div class="stat-trend">
                        <span class="trend-indicator">👑</span>
                    </div>
                </div>

                <div class="stat-card maintenance">
                    <div class="stat-icon">🔧</div>
                    <div class="stat-content">
                        <div class="stat-number">${maintenanceCabins}</div>
                        <div class="stat-label">Under Maintenance</div>
                    </div>
                    <div class="stat-trend">
                        <span class="trend-indicator warning">⚠️</span>
                    </div>
                </div>
            </div>
        </section>

        <!-- Cabins Table Section -->
        <section class="cabins-section">
            <div class="cabins-card">
                <div class="cabins-header">
                    <h2>📋 Cabin Inventory</h2>
                    <div class="cabins-count">
                        <span class="count-badge">${totalCabins} cabins</span>
                        <span class="company-info">🏢 Yash Technology</span>
                    </div>
                </div>

                <div class="cabins-content">
                    <c:choose>
                        <c:when test="${empty cabins}">
                            <div class="empty-state">
                                <div class="empty-icon">🏠</div>
                                <h3>No Cabins Found</h3>
                                <p>There are no cabins matching your criteria at Yash Technology.</p>
                                <a href="${pageContext.request.contextPath}/admin/add-cabin" class="empty-action">
                                    ➕ Add First Cabin
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="cabins-table">
                                <div class="table-header">
                                    <div class="header-cell">🏠 Cabin Details</div>
                                    <div class="header-cell">📏 Capacity</div>
                                    <div class="header-cell">📊 Status</div>
                                    <div class="header-cell">🔑 Access Level</div>
                                    <div class="header-cell">📅 Created</div>
                                    <div class="header-cell">⚙️ Actions</div>
                                </div>

                                <div class="table-body">
                                    <c:forEach var="cabin" items="${cabins}">
                                        <div class="table-row cabin-row">
                                            <div class="table-cell cabin-details-cell">
                                                <div class="cabin-info">
                                                    <div class="cabin-name">${cabin.name}</div>
                                                    <div class="cabin-location">
                                                        📍 ${cabin.location}
                                                    </div>
                                                    <c:if test="${not empty cabin.amenities}">
                                                        <div class="cabin-amenities">
                                                            🌟 ${cabin.amenities}
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </div>

                                            <div class="table-cell capacity-cell">
                                                <div class="capacity-badge">
                                                    <span class="capacity-number">${cabin.capacity}</span>
                                                    <span class="capacity-label">people</span>
                                                </div>
                                            </div>

                                            <div class="table-cell status-cell">
                                                <c:choose>
                                                    <c:when test="${cabin.status.name() eq 'ACTIVE'}">
                                                        <span class="status-badge active">✅ Active</span>
                                                    </c:when>
                                                    <c:when test="${cabin.status.name() eq 'MAINTENANCE'}">
                                                        <span class="status-badge maintenance">🔧 Maintenance</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge inactive">❌ Inactive</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>

                                            <div class="table-cell access-cell">
                                                <c:choose>
                                                    <c:when test="${cabin.vipOnly}">
                                                        <span class="access-badge vip">⭐ VIP Only</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="access-badge all">👤 All Users</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>

                                            <div class="table-cell date-cell">
                                                <div class="created-date">
                                                    <c:choose>
                                                        <c:when test="${not empty cabin.createdAt}">
                                                            <fmt:formatDate value="${cabin.createdAt}" pattern="MMM dd, yyyy"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            📅 N/A
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>

                                            <div class="table-cell actions-cell">
                                                <div class="action-buttons">
                                                    <a href="${pageContext.request.contextPath}/admin/edit-cabin?cabinId=${cabin.cabinId}"
                                                       class="action-btn edit" title="Edit Cabin">
                                                        ✏️ Edit
                                                    </a>

                                                    <div class="status-dropdown">
                                                        <button class="action-btn status" id="statusBtn-${cabin.cabinId}">
                                                            🔧 Status ▼
                                                        </button>
                                                        <div class="dropdown-menu" id="statusMenu-${cabin.cabinId}">
                                                            <button class="dropdown-item status-change"
                                                                    data-cabin-id="${cabin.cabinId}"
                                                                    data-cabin-name="${cabin.name}"
                                                                    data-status="ACTIVE">
                                                                ✅ Set Active
                                                            </button>
                                                            <button class="dropdown-item status-change"
                                                                    data-cabin-id="${cabin.cabinId}"
                                                                    data-cabin-name="${cabin.name}"
                                                                    data-status="MAINTENANCE">
                                                                🔧 Set Maintenance
                                                            </button>
                                                            <button class="dropdown-item status-change"
                                                                    data-cabin-id="${cabin.cabinId}"
                                                                    data-cabin-name="${cabin.name}"
                                                                    data-status="INACTIVE">
                                                                ❌ Set Inactive
                                                            </button>
                                                        </div>
                                                    </div>

                                                    <button class="action-btn delete delete-cabin"
                                                            data-cabin-id="${cabin.cabinId}"
                                                            data-cabin-name="${cabin.name}"
                                                            title="Delete Cabin">
                                                        🗑️ Delete
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
            </div>
        </section>
    </main>

    <!-- Status Change Modal -->
    <div class="modal-overlay" id="statusModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>🔧 Change Cabin Status</h3>
                <button class="modal-close" id="statusModalClose">×</button>
            </div>

            <div class="modal-body">
                <div class="status-info">
                    <p>Are you sure you want to change the status of <strong id="statusCabinName"></strong>?</p>
                    <div class="new-status-display">
                        New Status: <span id="newStatusDisplay" class="status-preview"></span>
                    </div>
                </div>

                <div class="status-warning" id="statusWarning" style="display: none;">
                    ⚠️ <strong>Warning:</strong> <span id="warningText"></span>
                </div>
            </div>

            <div class="modal-footer">
                <button class="modal-btn secondary" id="cancelStatusChange">
                    ↩️ Cancel
                </button>
                <button class="modal-btn primary" id="confirmStatusChange">
                    🔧 Change Status
                </button>
            </div>
        </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal-overlay" id="deleteModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>🗑️ Delete Cabin</h3>
                <button class="modal-close" id="deleteModalClose">×</button>
            </div>

            <div class="modal-body">
                <div class="delete-warning">
                    ⚠️ <strong>Danger Zone:</strong> This action cannot be undone!
                </div>

                <div class="delete-info">
                    <p>Are you sure you want to permanently delete <strong id="deleteCabinName"></strong>?</p>
                    <p>This will remove all associated data and bookings for this cabin.</p>
                </div>
            </div>

            <div class="modal-footer">
                <button class="modal-btn secondary" id="cancelDelete">
                    ↩️ Cancel
                </button>
                <button class="modal-btn danger" id="confirmDelete">
                    🗑️ Delete Cabin
                </button>
            </div>
        </div>
    </div>

    <!-- Hidden Forms -->
    <form id="statusForm" method="post" action="${pageContext.request.contextPath}/admin/update-cabin-status" style="display: none;">
        <input type="hidden" name="cabinId" id="statusCabinId">
        <input type="hidden" name="status" id="statusValue">
    </form>

    <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/admin/delete-cabin" style="display: none;">
        <input type="hidden" name="cabinId" id="deleteCabinId">
    </form>

    <!-- Footer -->
    <footer class="admin-footer">
        <p>&copy; 2024 Yash Technology - Cabin Management System</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/admin-manage-cabins.js"></script>
</body>
</html>
