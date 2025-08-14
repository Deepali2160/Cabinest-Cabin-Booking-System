<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - Yash Technology</title>
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/profile.css" rel="stylesheet">
</head>
<body>
    <!-- Header Navigation -->
    <nav class="profile-nav">
        <div class="nav-container">
            <div class="nav-brand">
                <h2>🏢 Yash Technology</h2>
                <span>My Profile</span>
            </div>

            <div class="nav-menu">
                <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">
                    🏠 Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/book" class="nav-link">
                    📅 New Booking
                </a>
                <a href="${pageContext.request.contextPath}/mybookings" class="nav-link">
                    📋 My Bookings
                </a>
                <a href="${pageContext.request.contextPath}/profile" class="nav-link active">
                    👤 Profile
                </a>
            </div>

            <div class="nav-user">
                <span class="user-name">${user.name}</span>
                <c:if test="${user.userType == 'VIP'}">
                    <span class="vip-badge">⭐ VIP</span>
                </c:if>
                <c:if test="${user.admin}">
                    <span class="admin-badge">👨‍💼 Admin</span>
                </c:if>
                <div class="user-dropdown">
                    <button class="dropdown-btn" id="userDropdown">⚙️</button>
                    <div class="dropdown-menu" id="dropdownMenu">
                        <a href="${pageContext.request.contextPath}/logout">🚪 Logout</a>
                    </div>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <main class="profile-main">

        <!-- Page Header -->
        <section class="profile-header">
            <div class="header-content">
                <h1>👤 My Profile</h1>
                <p>Manage your account information and preferences at Yash Technology</p>
            </div>
        </section>

        <!-- Messages -->
        <div id="message-container">
            <c:if test="${not empty successMessage}">
                <div class="message success-message">
                    ✅ ${successMessage}
                </div>
            </c:if>

            <c:if test="${not empty error}">
                <div class="message error-message">
                    ❌ ${error}
                </div>
            </c:if>
        </div>

        <!-- Profile Content -->
        <div class="profile-container">

            <!-- Profile Form Section -->
            <div class="profile-form-section">
                <div class="form-card">
                    <div class="form-header">
                        <h2>📝 Account Information</h2>
                        <p>Update your personal and account details</p>
                    </div>

                    <form id="profileForm" action="${pageContext.request.contextPath}/profile" method="post" class="profile-form">

                        <!-- Basic Information -->
                        <div class="form-section">
                            <h3>👤 Personal Information</h3>

                            <div class="form-row">
                                <div class="form-group">
                                    <label for="name">Full Name <span class="required">*</span></label>
                                    <input type="text" id="name" name="name" value="${user.name}"
                                           required maxlength="100" class="form-input">
                                </div>

                                <div class="form-group">
                                    <label for="email">Email Address <span class="required">*</span></label>
                                    <input type="email" id="email" name="email" value="${user.email}"
                                           required class="form-input">
                                    <small class="form-help">Used for login and notifications</small>
                                </div>
                            </div>
                        </div>

                        <!-- Company Information (Hidden - Single Company) -->
                        <div class="form-section company-section">
                            <h3>🏢 Company Information</h3>

                            <div class="company-display">
                                <div class="company-info">
                                    <div class="company-icon">🏢</div>
                                    <div class="company-details">
                                        <h4>Yash Technology</h4>
                                        <p>📍 Indore, Madhya Pradesh</p>
                                        <p>📞 contact@yashtech.com</p>
                                    </div>
                                </div>
                                <div class="user-role">
                                    <c:choose>
                                        <c:when test="${user.userType == 'VIP'}">
                                            <span class="role-badge vip">⭐ VIP Member</span>
                                        </c:when>
                                        <c:when test="${user.admin}">
                                            <span class="role-badge admin">👨‍💼 Administrator</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="role-badge normal">👤 Employee</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <!-- Hidden input for company ID -->
                            <input type="hidden" name="companyId" value="1">
                        </div>

                        <!-- Password Change Section -->
                        <div class="form-section password-section">
                            <h3>🔒 Change Password (Optional)</h3>
                            <p class="section-description">Leave blank if you don't want to change your password</p>

                            <div class="form-row">
                                <div class="form-group">
                                    <label for="currentPassword">Current Password</label>
                                    <input type="password" id="currentPassword" name="currentPassword"
                                           class="form-input">
                                    <small class="form-help">Enter your current password</small>
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label for="newPassword">New Password</label>
                                    <input type="password" id="newPassword" name="newPassword"
                                           minlength="6" class="form-input">
                                    <small class="form-help">Minimum 6 characters</small>
                                    <div class="password-strength" id="passwordStrength" style="display: none;">
                                        <div class="strength-bar" id="strengthBar"></div>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="confirmPassword">Confirm New Password</label>
                                    <input type="password" id="confirmPassword" name="confirmPassword"
                                           class="form-input">
                                    <div class="match-indicator" id="matchIndicator"></div>
                                </div>
                            </div>
                        </div>

                        <!-- Submit Section -->
                        <div class="submit-section">
                            <div class="form-actions">
                                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">
                                    ← Back to Dashboard
                                </a>
                                <button type="submit" id="updateBtn" class="btn btn-primary">
                                    💾 Update Profile
                                </button>
                            </div>
                            <div class="submit-info">
                                🔒 Your information is secure and encrypted
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Profile Sidebar -->
            <aside class="profile-sidebar">

                <!-- Profile Summary -->
                <div class="sidebar-card">
                    <h3>📋 Profile Summary</h3>

                    <div class="profile-summary">
                        <div class="avatar">
                            <div class="avatar-placeholder">
                                👤
                            </div>
                        </div>

                        <div class="user-info">
                            <h4>${user.name}</h4>
                            <p class="user-email">${user.email}</p>

                            <div class="user-badges">
                                <c:choose>
                                    <c:when test="${user.userType == 'VIP'}">
                                        <span class="user-badge vip">⭐ VIP Member</span>
                                    </c:when>
                                    <c:when test="${user.admin}">
                                        <span class="user-badge admin">👨‍💼 Administrator</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="user-badge normal">👤 Employee</span>
                                    </c:otherwise>
                                </c:choose>

                                <c:choose>
                                    <c:when test="${user.status == 'ACTIVE'}">
                                        <span class="status-badge active">✅ Active</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-badge inactive">❌ Inactive</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="member-since">
                                📅 Member since:
                                <c:choose>
                                    <c:when test="${user.createdAt != null}">
                                        <fmt:formatDate value="${user.createdAt}" pattern="MMM yyyy"/>
                                    </c:when>
                                    <c:otherwise>Recently</c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Booking Statistics -->
                <div class="sidebar-card">
                    <h3>📊 Your Statistics</h3>

                    <div class="stats-grid">
                        <div class="stat-item">
                            <div class="stat-number">${totalBookings}</div>
                            <div class="stat-label">Total Bookings</div>
                        </div>

                        <div class="stat-item">
                            <div class="stat-number">${approvedBookings}</div>
                            <div class="stat-label">Approved</div>
                        </div>

                        <div class="stat-item">
                            <div class="stat-number">
                                <c:choose>
                                    <c:when test="${totalBookings > 0}">
                                        ${Math.round((approvedBookings * 100.0) / totalBookings)}%
                                    </c:when>
                                    <c:otherwise>0%</c:otherwise>
                                </c:choose>
                            </div>
                            <div class="stat-label">Success Rate</div>
                        </div>

                        <div class="stat-item">
                            <div class="stat-number">
                                <c:choose>
                                    <c:when test="${user.userType == 'VIP'}">⭐</c:when>
                                    <c:when test="${user.admin}">👨‍💼</c:when>
                                    <c:otherwise>👤</c:otherwise>
                                </c:choose>
                            </div>
                            <div class="stat-label">User Type</div>
                        </div>
                    </div>
                </div>

                <!-- Quick Actions -->
                <div class="sidebar-card">
                    <h3>⚡ Quick Actions</h3>

                    <div class="quick-actions">
                        <a href="${pageContext.request.contextPath}/book" class="quick-action-btn primary">
                            📅 Book Cabin
                        </a>
                        <a href="${pageContext.request.contextPath}/mybookings" class="quick-action-btn secondary">
                            📋 My Bookings
                        </a>
                        <a href="${pageContext.request.contextPath}/dashboard" class="quick-action-btn tertiary">
                            🏠 Dashboard
                        </a>
                        <c:if test="${user.admin}">
                            <a href="${pageContext.request.contextPath}/admin/dashboard" class="quick-action-btn admin">
                                🔧 Admin Panel
                            </a>
                        </c:if>
                    </div>
                </div>

                <!-- Account Tips -->
                <div class="sidebar-card">
                    <h3>💡 Account Tips</h3>

                    <div class="tips-list">
                        <div class="tip-item">
                            🔒 Use a strong password with at least 6 characters
                        </div>
                        <div class="tip-item">
                            📧 Keep your email updated for booking notifications
                        </div>
                        <div class="tip-item">
                            👤 Complete your profile for better experience
                        </div>
                        <c:if test="${user.userType == 'VIP'}">
                            <div class="tip-item vip-tip">
                                ⭐ VIP members get priority booking approval
                            </div>
                        </c:if>
                    </div>
                </div>
            </aside>
        </div>
    </main>

    <!-- Footer -->
    <footer class="profile-footer">
        <p>&copy; 2025 Yash Technology - Cabin Booking System</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/profile.js"></script>
</body>
</html>
