<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Cabin - Yash Technology Admin</title>
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/admin-edit-cabin.css" rel="stylesheet">
</head>
<body>
    <!-- Admin Navigation -->
    <nav class="admin-nav">
        <div class="nav-container">
            <div class="nav-brand">
                <h2>🔧 Yash Technology Admin</h2>
                <span>Edit Cabin</span>
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
                    <h1>✏️ Edit Cabin</h1>
                    <p>Modify "${cabin.name}" at Yash Technology - Indore</p>
                </div>

                <div class="header-actions">
                    <a href="${pageContext.request.contextPath}/admin/manage-cabins" class="action-btn secondary">
                        🏠 Manage Cabins
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

        <!-- Edit Cabin Form Section -->
        <section class="form-section">
            <div class="form-container">
                <div class="form-card">
                    <div class="form-header">
                        <h2>✏️ Edit Cabin Information</h2>
                        <p>Update the details for "${cabin.name}"</p>
                    </div>

                    <form method="post" action="${pageContext.request.contextPath}/admin/edit-cabin"
                          id="editCabinForm" class="cabin-form">

                        <!-- Hidden Fields -->
                        <input type="hidden" name="cabinId" value="${cabin.cabinId}">
                        <input type="hidden" name="companyId" value="1">

                        <!-- Basic Information Row -->
                        <div class="form-row">
                            <div class="form-group">
                                <label for="name" class="form-label">
                                    🏷️ Cabin Name <span class="required">*</span>
                                </label>
                                <input type="text"
                                       class="form-input"
                                       id="name"
                                       name="name"
                                       value="${cabin.name}"
                                       placeholder="e.g., Conference Room A, Meeting Hall 1"
                                       required
                                       maxlength="100"
                                       autocomplete="off">
                                <div class="form-feedback" id="nameError"></div>
                            </div>

                            <div class="form-group">
                                <label for="capacity" class="form-label">
                                    👥 Capacity <span class="required">*</span>
                                </label>
                                <input type="number"
                                       class="form-input"
                                       id="capacity"
                                       name="capacity"
                                       value="${cabin.capacity}"
                                       placeholder="Maximum number of people"
                                       required
                                       min="1"
                                       max="50"
                                       autocomplete="off">
                                <div class="form-feedback" id="capacityError"></div>
                            </div>
                        </div>

                        <!-- Location and Status -->
                        <div class="form-row">
                            <div class="form-group">
                                <label for="location" class="form-label">
                                    📍 Location <span class="required">*</span>
                                </label>
                                <input type="text"
                                       class="form-input"
                                       id="location"
                                       name="location"
                                       value="${cabin.location}"
                                       placeholder="e.g., 2nd Floor Wing A, Ground Floor Reception"
                                       required
                                       maxlength="200"
                                       autocomplete="off">
                                <div class="form-feedback" id="locationError"></div>
                            </div>

                            <div class="form-group">
                                <label for="status" class="form-label">
                                    📊 Status <span class="required">*</span>
                                </label>
                                <select class="form-select" id="status" name="status" required>
                                    <option value="ACTIVE" ${cabin.status.name() eq 'ACTIVE' ? 'selected' : ''}>
                                        ✅ Active
                                    </option>
                                    <option value="MAINTENANCE" ${cabin.status.name() eq 'MAINTENANCE' ? 'selected' : ''}>
                                        🔧 Maintenance
                                    </option>
                                    <option value="INACTIVE" ${cabin.status.name() eq 'INACTIVE' ? 'selected' : ''}>
                                        ❌ Inactive
                                    </option>
                                </select>
                                <div class="form-feedback" id="statusError"></div>
                            </div>
                        </div>

                        <!-- Amenities Section -->
                        <div class="form-group full-width">
                            <label for="amenities" class="form-label">
                                🌟 Amenities & Equipment
                            </label>
                            <div class="amenities-selector">
                                <div class="amenities-grid">
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Projector"
                                               ${cabin.amenities.contains('Projector') ? 'checked' : ''}>
                                        <span class="amenity-label">📽️ Projector</span>
                                    </label>
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Whiteboard"
                                               ${cabin.amenities.contains('Whiteboard') ? 'checked' : ''}>
                                        <span class="amenity-label">📝 Whiteboard</span>
                                    </label>
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Air Conditioning"
                                               ${cabin.amenities.contains('Air Conditioning') || cabin.amenities.contains('AC') ? 'checked' : ''}>
                                        <span class="amenity-label">❄️ AC</span>
                                    </label>
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Wi-Fi"
                                               ${cabin.amenities.contains('Wi-Fi') || cabin.amenities.contains('WiFi') ? 'checked' : ''}>
                                        <span class="amenity-label">📶 Wi-Fi</span>
                                    </label>
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Video Conferencing"
                                               ${cabin.amenities.contains('Video Conferencing') || cabin.amenities.contains('Video Call') ? 'checked' : ''}>
                                        <span class="amenity-label">🎥 Video Call</span>
                                    </label>
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Sound System"
                                               ${cabin.amenities.contains('Sound System') ? 'checked' : ''}>
                                        <span class="amenity-label">🔊 Sound System</span>
                                    </label>
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Coffee Machine"
                                               ${cabin.amenities.contains('Coffee Machine') || cabin.amenities.contains('Coffee') ? 'checked' : ''}>
                                        <span class="amenity-label">☕ Coffee</span>
                                    </label>
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Flip Chart"
                                               ${cabin.amenities.contains('Flip Chart') ? 'checked' : ''}>
                                        <span class="amenity-label">📊 Flip Chart</span>
                                    </label>
                                </div>
                            </div>

                            <textarea class="form-textarea"
                                      id="amenities"
                                      name="amenities"
                                      rows="3"
                                      placeholder="Additional amenities or custom equipment..."
                                      maxlength="500">${cabin.amenities}</textarea>
                            <div class="form-hint">
                                Select common amenities above or describe custom equipment below
                            </div>
                        </div>

                        <!-- Access Level Section -->
                        <div class="form-group full-width access-section">
                            <label class="form-label">
                                🔑 Access Level
                            </label>

                            <div class="access-options">
                                <label class="access-option">
                                    <input type="radio" name="accessLevel" value="ALL" ${!cabin.vipOnly ? 'checked' : ''}>
                                    <div class="option-card all-users">
                                        <div class="option-icon">👥</div>
                                        <div class="option-content">
                                            <h4>All Users</h4>
                                            <p>Available to all employees at Yash Technology</p>
                                        </div>
                                    </div>
                                </label>

                                <label class="access-option">
                                    <input type="radio" name="accessLevel" value="VIP" ${cabin.vipOnly ? 'checked' : ''}>
                                    <div class="option-card vip-only">
                                        <div class="option-icon">⭐</div>
                                        <div class="option-content">
                                            <h4>VIP Only</h4>
                                            <p>Exclusive access for VIP users and administrators</p>
                                        </div>
                                    </div>
                                </label>
                            </div>

                            <!-- Hidden input for backend compatibility -->
                            <input type="hidden" id="isVipOnly" name="isVipOnly" value="${cabin.vipOnly}">
                        </div>

                        <!-- Form Actions -->
                        <div class="form-actions">
                            <a href="${pageContext.request.contextPath}/admin/manage-cabins"
                               class="form-btn secondary">
                                ↩️ Cancel
                            </a>
                            <button type="submit" class="form-btn primary" id="submitBtn">
                                💾 Update Cabin
                            </button>
                        </div>
                    </form>
                </div>

                <!-- Cabin Info Sidebar -->
                <div class="info-card">
                    <div class="info-header">
                        <h3>📋 Cabin Information</h3>
                    </div>

                    <div class="info-content">
                        <div class="info-item">
                            <div class="info-label">🆔 Cabin ID</div>
                            <div class="info-value">${cabin.cabinId}</div>
                        </div>

                        <div class="info-item">
                            <div class="info-label">🏢 Company</div>
                            <div class="info-value">Yash Technology</div>
                        </div>

                        <div class="info-item">
                            <div class="info-label">📊 Current Status</div>
                            <div class="info-value">
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
                        </div>

                        <div class="info-item">
                            <div class="info-label">🔑 Access Level</div>
                            <div class="info-value">
                                <c:choose>
                                    <c:when test="${cabin.vipOnly}">
                                        <span class="access-badge vip">⭐ VIP Only</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="access-badge all">👥 All Users</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <div class="info-item">
                            <div class="info-label">📅 Created</div>
                            <div class="info-value">
                                <c:choose>
                                    <c:when test="${not empty cabin.createdAt}">
                                        <fmt:formatDate value="${cabin.createdAt}" pattern="MMM dd, yyyy"/>
                                        <br><small class="text-muted">
                                            <fmt:formatDate value="${cabin.createdAt}" pattern="hh:mm a"/>
                                        </small>
                                    </c:when>
                                    <c:otherwise>
                                        📅 N/A
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <div class="info-item">
                            <div class="info-label">📏 Capacity</div>
                            <div class="info-value">
                                <span class="capacity-badge">${cabin.capacity} people</span>
                            </div>
                        </div>
                    </div>

                    <div class="info-actions">
                        <button type="button" class="info-btn danger" id="deleteCabinBtn">
                            🗑️ Delete Cabin
                        </button>
                        <button type="button" class="info-btn secondary" id="viewBookingsBtn">
                            📅 View Bookings
                        </button>
                    </div>
                </div>
            </div>
        </section>
    </main>

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
                    <p>Are you sure you want to permanently delete <strong>"${cabin.name}"</strong>?</p>
                    <ul class="delete-consequences">
                        <li>❌ Cabin will be removed from the system</li>
                        <li>📅 All future bookings will be cancelled</li>
                        <li>📊 Historical data will be preserved but cabin will be inaccessible</li>
                        <li>👥 Users will be notified of booking cancellations</li>
                    </ul>
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

    <!-- Status Change Warning Modal -->
    <div class="modal-overlay" id="statusWarningModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>⚠️ Status Change Warning</h3>
                <button class="modal-close" id="statusWarningClose">×</button>
            </div>

            <div class="modal-body">
                <div class="warning-content" id="warningContent">
                    <!-- Dynamic content based on status change -->
                </div>
            </div>

            <div class="modal-footer">
                <button class="modal-btn secondary" id="cancelStatusChange">
                    ↩️ Cancel
                </button>
                <button class="modal-btn warning" id="confirmStatusChange">
                    ⚠️ Continue
                </button>
            </div>
        </div>
    </div>

    <!-- Hidden Delete Form -->
    <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/admin/delete-cabin" style="display: none;">
        <input type="hidden" name="cabinId" value="${cabin.cabinId}">
    </form>

    <!-- Footer -->
    <footer class="admin-footer">
        <p>&copy; 2024 Yash Technology - Cabin Management System</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/admin-edit-cabin.js"></script>
</body>
</html>
