<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Booking Management - Yash Technology Admin</title>
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/admin-booking-management.css" rel="stylesheet">
</head>
<body>
    <!-- Admin Navigation -->
    <nav class="admin-nav">
        <div class="nav-container">
            <div class="nav-brand">
                <h2>🔧 Yash Technology Admin</h2>
                <span>Booking Management</span>
            </div>

            <div class="nav-menu">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link">
                    📊 Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/admin/bookings" class="nav-link active">
                    📅 Manage Bookings
                </a>
                <a href="${pageContext.request.contextPath}/admin/manage-cabins" class="nav-link">
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
                        <c:when test="${admin.userType eq 'SUPER_ADMIN'}">
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

        <!-- Page Header -->
        <section class="page-header">
            <div class="header-content">
                <div class="header-info">
                    <h1>📅 Booking Management</h1>
                    <p>Review and manage cabin bookings at Yash Technology - Indore</p>
                </div>

                <div class="header-actions">
                    <div class="bulk-actions" id="bulkActions" style="display: none;">
                        <button class="action-btn success" id="bulkApproveBtn">
                            ✅ Bulk Approve
                        </button>
                        <button class="action-btn danger" id="bulkRejectBtn">
                            ❌ Bulk Reject
                        </button>
                    </div>
                </div>
            </div>
        </section>

        <!-- Filter Tabs Section -->
        <section class="filters-section">
            <div class="filters-card">
                <div class="filter-tabs">
                    <a href="${pageContext.request.contextPath}/admin/bookings?filter=pending"
                       class="filter-tab ${param.filter eq 'pending' || empty param.filter ? 'active' : ''}">
                        <div class="tab-icon">⏰</div>
                        <div class="tab-content">
                            <span class="tab-label">Pending</span>
                            <span class="tab-count">${pendingCount > 0 ? pendingCount : '0'}</span>
                        </div>
                    </a>

                    <a href="${pageContext.request.contextPath}/admin/bookings?filter=vip"
                       class="filter-tab ${param.filter eq 'vip' ? 'active' : ''}">
                        <div class="tab-icon">⭐</div>
                        <div class="tab-content">
                            <span class="tab-label">VIP Priority</span>
                            <span class="tab-count">${vipCount > 0 ? vipCount : '0'}</span>
                        </div>
                    </a>

                    <a href="${pageContext.request.contextPath}/admin/bookings?filter=approved"
                       class="filter-tab ${param.filter eq 'approved' ? 'active' : ''}">
                        <div class="tab-icon">✅</div>
                        <div class="tab-content">
                            <span class="tab-label">Approved</span>
                            <span class="tab-count">${approvedCount > 0 ? approvedCount : '0'}</span>
                        </div>
                    </a>

                    <a href="${pageContext.request.contextPath}/admin/bookings?filter=rejected"
                       class="filter-tab ${param.filter eq 'rejected' ? 'active' : ''}">
                        <div class="tab-icon">❌</div>
                        <div class="tab-content">
                            <span class="tab-label">Rejected</span>
                            <span class="tab-count">${rejectedCount > 0 ? rejectedCount : '0'}</span>
                        </div>
                    </a>

                    <a href="${pageContext.request.contextPath}/admin/bookings?filter=all"
                       class="filter-tab ${param.filter eq 'all' ? 'active' : ''}">
                        <div class="tab-icon">📋</div>
                        <div class="tab-content">
                            <span class="tab-label">All Bookings</span>
                            <span class="tab-count">${totalCount > 0 ? totalCount : '0'}</span>
                        </div>
                    </a>
                </div>

                <!-- Search Bar -->
                <div class="search-section">
                    <div class="search-bar">
                        <div class="search-input-container">
                            <span class="search-icon">🔍</span>
                            <input type="text" id="searchInput" class="search-input"
                                   placeholder="Search bookings by user, cabin, or purpose...">
                        </div>
                    </div>
                    <div class="search-info">
                        Showing <span id="visibleCount">${not empty bookings ? bookings.size() : '0'}</span> bookings
                        <c:if test="${not empty param.filter && param.filter ne 'all'}">
                            from <strong>Yash Technology</strong> (${param.filter} filter)
                        </c:if>
                    </div>
                </div>
            </div>
        </section>

        <!-- Bookings Table Section -->
        <section class="bookings-section">
            <div class="bookings-card">
                <div class="bookings-content">
                    <c:choose>
                        <c:when test="${empty bookings}">
                            <div class="empty-state">
                                <div class="empty-icon">📅</div>
                                <h3>No Bookings Found</h3>
                                <p>
                                    <c:choose>
                                        <c:when test="${param.filter eq 'pending'}">
                                            No pending bookings require approval at this time.
                                        </c:when>
                                        <c:when test="${param.filter eq 'vip'}">
                                            No VIP priority bookings found.
                                        </c:when>
                                        <c:otherwise>
                                            Try adjusting your filters or search terms.
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                                <a href="${pageContext.request.contextPath}/admin/bookings" class="empty-action">
                                    📋 View All Bookings
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <form id="bulkActionForm" method="post" class="bookings-form">
                                <div class="bookings-table">
                                    <div class="table-header">
                                        <div class="header-cell select-cell">
                                            <c:if test="${param.filter eq 'pending' || empty param.filter}">
                                                <input type="checkbox" id="selectAll" class="bulk-checkbox">
                                            </c:if>
                                        </div>
                                        <div class="header-cell">👤 User Details</div>
                                        <div class="header-cell">🏠 Cabin & Purpose</div>
                                        <div class="header-cell">📅 Date & Time</div>
                                        <div class="header-cell">⏱️ Duration</div>
                                        <div class="header-cell">📊 Status</div>
                                        <div class="header-cell">🎯 Priority</div>
                                        <div class="header-cell">📝 Created</div>
                                        <div class="header-cell">⚙️ Actions</div>
                                    </div>

                                    <div class="table-body">
                                        <c:forEach var="booking" items="${bookings}">
                                            <div class="table-row booking-row"
                                                 data-user-name="${booking.userName}"
                                                 data-cabin-name="${booking.cabinName}"
                                                 data-purpose="${booking.purpose}"
                                                 data-booking-id="${booking.bookingId}">

                                                <div class="table-cell select-cell">
                                                    <c:if test="${booking.status.toString() eq 'PENDING'}">
                                                        <input type="checkbox" name="bookingIds"
                                                               value="${booking.bookingId}"
                                                               class="booking-checkbox">
                                                    </c:if>
                                                </div>

                                                <div class="table-cell user-cell">
                                                    <div class="user-info">
                                                        <div class="user-avatar">👤</div>
                                                        <div class="user-details">
                                                            <div class="user-name">${booking.userName}</div>
                                                            <div class="user-id">ID: ${booking.userId}</div>
                                                            <c:if test="${booking.priorityLevel.toString() eq 'VIP'}">
                                                                <span class="user-badge vip">⭐ VIP</span>
                                                            </c:if>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="table-cell cabin-cell">
                                                    <div class="cabin-info">
                                                        <div class="cabin-name">${booking.cabinName}</div>
                                                        <div class="cabin-purpose" title="${booking.purpose}">
                                                            ${booking.purpose}
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="table-cell datetime-cell">
                                                    <div class="datetime-info">
                                                        <div class="booking-date">
                                                            <fmt:formatDate value="${booking.bookingDate}" pattern="MMM dd, yyyy"/>
                                                        </div>
                                                        <div class="booking-time">${booking.timeSlot}</div>
                                                    </div>
                                                </div>

                                                <div class="table-cell duration-cell">
                                                    <c:choose>
                                                        <c:when test="${not empty booking.durationMinutes}">
                                                            <span class="duration-badge custom">
                                                                ⏱️ ${booking.durationDisplay}
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="duration-badge standard">📏 Standard</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>

                                                <div class="table-cell status-cell">
                                                    <c:choose>
                                                        <c:when test="${booking.status.toString() eq 'PENDING'}">
                                                            <span class="status-badge pending">⏰ Pending</span>
                                                        </c:when>
                                                        <c:when test="${booking.status.toString() eq 'APPROVED'}">
                                                            <span class="status-badge approved">✅ Approved</span>
                                                        </c:when>
                                                        <c:when test="${booking.status.toString() eq 'REJECTED'}">
                                                            <span class="status-badge rejected">❌ Rejected</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="status-badge unknown">${booking.status}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>

                                                <div class="table-cell priority-cell">
                                                    <c:choose>
                                                        <c:when test="${booking.priorityLevel.toString() eq 'VIP'}">
                                                            <span class="priority-badge vip">⭐ VIP</span>
                                                        </c:when>
                                                        <c:when test="${booking.priorityLevel.toString() eq 'HIGH'}">
                                                            <span class="priority-badge high">⚡ High</span>
                                                        </c:when>
                                                        <c:when test="${booking.priorityLevel.toString() eq 'EMERGENCY'}">
                                                            <span class="priority-badge emergency">🚨 Emergency</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="priority-badge normal">📝 Normal</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>

                                                <div class="table-cell created-cell">
                                                    <div class="created-time">
                                                        <fmt:formatDate value="${booking.createdAt}" pattern="MMM dd"/>
                                                        <br>
                                                        <small>
                                                            <fmt:formatDate value="${booking.createdAt}" pattern="HH:mm"/>
                                                        </small>
                                                    </div>
                                                </div>

                                                <div class="table-cell actions-cell">
                                                    <c:choose>
                                                        <c:when test="${booking.status.toString() eq 'PENDING'}">
                                                            <div class="action-buttons">
                                                                <button type="button"
                                                                        class="action-btn approve approve-single"
                                                                        data-booking-id="${booking.bookingId}"
                                                                        data-user-name="${booking.userName}"
                                                                        title="Approve Booking">
                                                                    ✅
                                                                </button>
                                                                <button type="button"
                                                                        class="action-btn reject reject-single"
                                                                        data-booking-id="${booking.bookingId}"
                                                                        data-user-name="${booking.userName}"
                                                                        title="Reject Booking">
                                                                    ❌
                                                                </button>
                                                            </div>
                                                        </c:when>
                                                        <c:when test="${booking.status.toString() eq 'APPROVED'}">
                                                            <div class="action-status approved">
                                                                ✅ Approved
                                                                <c:if test="${booking.approvedAt != null}">
                                                                    <br>
                                                                    <small>
                                                                        <fmt:formatDate value="${booking.approvedAt}" pattern="MMM dd"/>
                                                                    </small>
                                                                </c:if>
                                                            </div>
                                                        </c:when>
                                                        <c:when test="${booking.status.toString() eq 'REJECTED'}">
                                                            <div class="action-status rejected">
                                                                ❌ Rejected
                                                                <c:if test="${booking.rejectedAt != null}">
                                                                    <br>
                                                                    <small>
                                                                        <fmt:formatDate value="${booking.rejectedAt}" pattern="MMM dd"/>
                                                                    </small>
                                                                </c:if>
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="action-status unknown">-</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </section>
    </main>

    <!-- Confirmation Modal -->
    <div class="modal-overlay" id="confirmModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="confirmModalTitle">Confirm Action</h3>
                <button class="modal-close" id="confirmModalClose">×</button>
            </div>

            <div class="modal-body">
                <div class="modal-icon" id="modalIcon">⚠️</div>
                <div class="modal-message" id="confirmModalBody">
                    Are you sure you want to perform this action?
                </div>
            </div>

            <div class="modal-footer">
                <button class="modal-btn secondary" id="cancelAction">
                    ↩️ Cancel
                </button>
                <button class="modal-btn primary" id="confirmActionBtn">
                    ✅ Confirm
                </button>
            </div>
        </div>
    </div>

    <!-- Bulk Action Modal -->
    <div class="modal-overlay" id="bulkConfirmModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="bulkModalTitle">Bulk Action Confirmation</h3>
                <button class="modal-close" id="bulkModalClose">×</button>
            </div>

            <div class="modal-body">
                <div class="modal-warning">
                    ⚠️ <strong>Warning:</strong> This action will affect multiple bookings and cannot be undone.
                </div>

                <div class="bulk-info">
                    <p>Selected bookings: <strong id="selectedCount">0</strong></p>
                    <p>Company: <strong>Yash Technology - Indore</strong></p>
                </div>
            </div>

            <div class="modal-footer">
                <button class="modal-btn secondary" id="cancelBulkAction">
                    ↩️ Cancel
                </button>
                <button class="modal-btn primary" id="bulkConfirmBtn">
                    🚀 Proceed
                </button>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="admin-footer">
        <p>&copy; 2025 Yash Technology - Booking Management System</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/admin-booking-management.js"></script>
</body>
</html>
