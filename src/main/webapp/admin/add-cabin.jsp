<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New Cabin - Yash Technology Admin</title>
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/admin-add-cabin.css" rel="stylesheet">
</head>
<body>
    <!-- Admin Navigation -->
    <nav class="admin-nav">
        <div class="nav-container">
            <div class="nav-brand">
                <h2>🔧 Yash Technology Admin</h2>
                <span>Add New Cabin</span>
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
                    <h1>➕ Add New Cabin</h1>
                    <p>Create a new meeting room for Yash Technology - Indore</p>
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

        <!-- Add Cabin Form Section -->
        <section class="form-section">
            <div class="form-container">
                <div class="form-card">
                    <div class="form-header">
                        <h2>🏠 Cabin Information</h2>
                        <p>Fill in the details for the new meeting room</p>
                    </div>

                    <form method="post" action="${pageContext.request.contextPath}/admin/add-cabin"
                          id="addCabinForm" class="cabin-form">

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
                                       placeholder="Maximum number of people"
                                       required
                                       min="1"
                                       max="50"
                                       autocomplete="off">
                                <div class="form-feedback" id="capacityError"></div>
                            </div>
                        </div>

                        <!-- Location and Floor -->
                        <div class="form-row">
                            <div class="form-group">
                                <label for="location" class="form-label">
                                    📍 Location <span class="required">*</span>
                                </label>
                                <input type="text"
                                       class="form-input"
                                       id="location"
                                       name="location"
                                       placeholder="e.g., 2nd Floor Wing A, Ground Floor Reception"
                                       required
                                       maxlength="200"
                                       autocomplete="off">
                                <div class="form-feedback" id="locationError"></div>
                            </div>

                            <div class="form-group">
                                <label for="floor" class="form-label">
                                    🏢 Floor Level
                                </label>
                                <select class="form-select" id="floor" name="floor">
                                    <option value="">Select Floor</option>
                                    <option value="Ground Floor">Ground Floor</option>
                                    <option value="1st Floor">1st Floor</option>
                                    <option value="2nd Floor">2nd Floor</option>
                                    <option value="3rd Floor">3rd Floor</option>
                                    <option value="4th Floor">4th Floor</option>
                                    <option value="5th Floor">5th Floor</option>
                                </select>
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
                                        <input type="checkbox" name="amenityCheck" value="Projector">
                                        <span class="amenity-label">📽️ Projector</span>
                                    </label>
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Whiteboard">
                                        <span class="amenity-label">📝 Whiteboard</span>
                                    </label>
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Air Conditioning">
                                        <span class="amenity-label">❄️ AC</span>
                                    </label>
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Wi-Fi">
                                        <span class="amenity-label">📶 Wi-Fi</span>
                                    </label>
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Video Conferencing">
                                        <span class="amenity-label">🎥 Video Call</span>
                                    </label>
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Sound System">
                                        <span class="amenity-label">🔊 Sound System</span>
                                    </label>
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Coffee Machine">
                                        <span class="amenity-label">☕ Coffee</span>
                                    </label>
                                    <label class="amenity-item">
                                        <input type="checkbox" name="amenityCheck" value="Flip Chart">
                                        <span class="amenity-label">📊 Flip Chart</span>
                                    </label>
                                </div>
                            </div>

                            <textarea class="form-textarea"
                                      id="amenities"
                                      name="amenities"
                                      rows="3"
                                      placeholder="Additional amenities or custom equipment..."
                                      maxlength="500"></textarea>
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
                                    <input type="radio" name="accessLevel" value="ALL" checked>
                                    <div class="option-card all-users">
                                        <div class="option-icon">👥</div>
                                        <div class="option-content">
                                            <h4>All Users</h4>
                                            <p>Available to all employees at Yash Technology</p>
                                        </div>
                                    </div>
                                </label>

                                <label class="access-option">
                                    <input type="radio" name="accessLevel" value="VIP">
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
                            <input type="hidden" id="isVipOnly" name="isVipOnly" value="false">
                        </div>

                        <!-- Company Info (Hidden - Default to Yash Technology) -->
                        <input type="hidden" name="companyId" value="1">

                        <!-- Form Actions -->
                        <div class="form-actions">
                            <a href="${pageContext.request.contextPath}/admin/manage-cabins"
                               class="form-btn secondary">
                                ↩️ Cancel
                            </a>
                            <button type="submit" class="form-btn primary" id="submitBtn">
                                ➕ Add Cabin
                            </button>
                        </div>
                    </form>
                </div>

                <!-- Quick Tips Sidebar -->
                <div class="tips-card">
                    <div class="tips-header">
                        <h3>💡 Quick Tips</h3>
                    </div>

                    <div class="tips-content">
                        <div class="tip-item">
                            <div class="tip-icon">🏷️</div>
                            <div class="tip-content">
                                <h4>Cabin Naming</h4>
                                <p>Use descriptive names like "Conference Room A", "Board Room", or "Training Hall 1"</p>
                            </div>
                        </div>

                        <div class="tip-item">
                            <div class="tip-icon">👥</div>
                            <div class="tip-content">
                                <h4>Capacity Planning</h4>
                                <p>Enter the comfortable seating capacity, not the maximum possible occupancy</p>
                            </div>
                        </div>

                        <div class="tip-item">
                            <div class="tip-icon">📍</div>
                            <div class="tip-content">
                                <h4>Location Details</h4>
                                <p>Include floor, wing, and nearby landmarks for easy navigation</p>
                            </div>
                        </div>

                        <div class="tip-item">
                            <div class="tip-icon">⭐</div>
                            <div class="tip-content">
                                <h4>VIP Access</h4>
                                <p>VIP cabins are exclusively available to VIP users and administrators</p>
                            </div>
                        </div>

                        <div class="tip-item">
                            <div class="tip-icon">🌟</div>
                            <div class="tip-content">
                                <h4>Amenities List</h4>
                                <p>Accurate amenity information helps users choose the right cabin</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </main>

    <!-- Footer -->
    <footer class="admin-footer">
        <p>&copy; 2024 Yash Technology - Cabin Management System</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/admin-add-cabin.js"></script>
</body>
</html>
