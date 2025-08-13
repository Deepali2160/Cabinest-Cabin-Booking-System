<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Yash Technology</title>
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/admin-dashboard.css" rel="stylesheet">
</head>
<body>
    <!-- Admin Navigation -->
    <nav class="admin-nav">
        <div class="nav-container">
            <div class="nav-brand">
                <h2>🔧 Yash Technology Admin</h2>
                <span>Management Dashboard</span>
            </div>

            <div class="nav-menu">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link active">
                    📊 Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/admin/bookings" class="nav-link">
                    📅 Manage Bookings
                </a>
                <div class="nav-dropdown">
                    <button class="nav-link dropdown-toggle" id="cabinDropdown">
                        🏠 Cabin Management ▼
                    </button>
                    <div class="dropdown-menu" id="cabinDropdownMenu">
                        <a href="${pageContext.request.contextPath}/admin/add-cabin">➕ Add New Cabin</a>
                        <a href="${pageContext.request.contextPath}/admin/manage-cabins">📋 Manage Cabins</a>
                        <a href="${pageContext.request.contextPath}/admin/manage-cabins?status=MAINTENANCE">🔧 Maintenance</a>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/admin/users" class="nav-link">
                    👥 User Management
                </a>
                <a href="${pageContext.request.contextPath}/admin/analytics" class="nav-link">
                    📈 Analytics
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

        <!-- Admin Welcome Section -->
        <section class="admin-header">
            <div class="header-content">
                <div class="welcome-info">
                    <h1>🛡️ Welcome, ${admin.name}!</h1>
                    <p>
                        <c:choose>
                            <c:when test="${admin.userType == 'SUPER_ADMIN'}">
                                👑 Super Administrator - Full System Control at Yash Technology
                            </c:when>
                            <c:otherwise>
                                👨‍💼 Administrator - Booking & User Management at Yash Technology
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <div class="last-login">
                        🕐 Last Login: Recently | 📍 Yash Technology - Indore
                    </div>
                </div>

                <div class="priority-alert">
                    <div class="alert-number">${pendingCount}</div>
                    <div class="alert-label">Pending Approvals</div>
                    <c:if test="${pendingCount > 0}">
                        <a href="${pageContext.request.contextPath}/admin/bookings?filter=pending" class="alert-action">
                            ⚡ Review Now
                        </a>
                    </c:if>
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

        <!-- Quick Stats Grid -->
        <section class="stats-section">
            <div class="stats-grid">
                <div class="stat-card pending">
                    <div class="stat-icon">⏳</div>
                    <div class="stat-content">
                        <div class="stat-number">${pendingCount}</div>
                        <div class="stat-label">Pending Bookings</div>
                    </div>
                    <a href="${pageContext.request.contextPath}/admin/bookings?filter=pending" class="stat-action">
                        👁️ Review
                    </a>
                </div>

                <div class="stat-card vip">
                    <div class="stat-icon">⭐</div>
                    <div class="stat-content">
                        <div class="stat-number">${not empty vipBookings ? vipBookings.size() : 0}</div>
                        <div class="stat-label">VIP Priority</div>
                    </div>
                    <a href="${pageContext.request.contextPath}/admin/bookings?filter=vip" class="stat-action">
                        👑 Priority
                    </a>
                </div>

                <div class="stat-card users">
                    <div class="stat-icon">👥</div>
                    <div class="stat-content">
                        <div class="stat-number">${totalUsers}</div>
                        <div class="stat-label">Total Users</div>
                    </div>
                    <a href="${pageContext.request.contextPath}/admin/users" class="stat-action">
                        ⚙️ Manage
                    </a>
                </div>

                <div class="stat-card bookings">
                    <div class="stat-icon">📅</div>
                    <div class="stat-content">
                        <div class="stat-number">${totalBookings}</div>
                        <div class="stat-label">Total Bookings</div>
                    </div>
                    <a href="${pageContext.request.contextPath}/admin/analytics" class="stat-action">
                        📈 Analytics
                    </a>
                </div>
            </div>
        </section>

        <!-- Cabin Management Hub -->
        <section class="cabin-hub-section">
            <div class="section-card">
                <div class="section-header">
                    <h2>🏠 Cabin Management Hub</h2>
                    <p>Complete cabin administration for Yash Technology</p>
                </div>

                <div class="cabin-actions-grid">
                    <div class="cabin-action-card add-cabin"
                         onclick="window.location.href='${pageContext.request.contextPath}/admin/add-cabin'">
                        <div class="action-icon">➕</div>
                        <h3>Add New Cabin</h3>
                        <p>Create new meeting rooms</p>
                    </div>

                    <div class="cabin-action-card manage-cabins"
                         onclick="window.location.href='${pageContext.request.contextPath}/admin/manage-cabins'">
                        <div class="action-icon">📋</div>
                        <h3>Manage Cabins</h3>
                        <p>Edit, view, delete cabins</p>
                    </div>

                    <div class="cabin-action-card maintenance"
                         onclick="window.location.href='${pageContext.request.contextPath}/admin/manage-cabins?status=MAINTENANCE'">
                        <div class="action-icon">🔧</div>
                        <h3>Maintenance</h3>
                        <p>Cabins under maintenance</p>
                    </div>

                    <div class="cabin-action-card vip-cabins"
                         onclick="window.location.href='${pageContext.request.contextPath}/admin/manage-cabins?filter=vip'">
                        <div class="action-icon">👑</div>
                        <h3>VIP Cabins</h3>
                        <p>Premium access rooms</p>
                    </div>
                </div>
            </div>
        </section>

        <!-- Main Content Grid -->
        <div class="admin-content-grid">

            <!-- Recent Bookings Section -->
            <section class="bookings-section">
                <div class="section-card">
                    <div class="section-header">
                        <h2>📋 Recent Bookings</h2>
                        <span class="item-count">${not empty recentBookings ? recentBookings.size() : 0} items</span>
                    </div>

                    <div class="bookings-content">
                        <c:choose>
                            <c:when test="${empty recentBookings}">
                                <div class="empty-state">
                                    <div class="empty-icon">📅</div>
                                    <h3>No recent bookings</h3>
                                    <p>All bookings are up to date</p>
                                    <a href="${pageContext.request.contextPath}/admin/bookings" class="empty-action">
                                        👁️ View All Bookings
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="bookings-table">
                                    <div class="table-header">
                                        <div class="header-cell">User</div>
                                        <div class="header-cell">Cabin</div>
                                        <div class="header-cell">Date & Time</div>
                                        <div class="header-cell">Status</div>
                                        <div class="header-cell">Actions</div>
                                    </div>

                                    <div class="table-body">
                                        <c:forEach var="booking" items="${recentBookings}">
                                            <div class="table-row">
                                                <div class="table-cell user-cell">
                                                    <div class="user-info">
                                                        <div class="user-avatar">👤</div>
                                                        <div class="user-details">
                                                            <div class="user-name">${booking.userName}</div>
                                                            <div class="user-id">ID: ${booking.userId}</div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="table-cell cabin-cell">
                                                    <div class="cabin-name">${booking.cabinName}</div>
                                                    <div class="booking-purpose">${booking.purpose}</div>
                                                </div>

                                                <div class="table-cell datetime-cell">
                                                    <div class="booking-date">
                                                        <c:choose>
                                                            <c:when test="${not empty booking.bookingDate}">
                                                                <fmt:formatDate value="${booking.bookingDate}" pattern="MMM dd, yyyy"/>
                                                            </c:when>
                                                            <c:otherwise>Date TBD</c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div class="booking-time">${booking.timeSlot}</div>
                                                </div>

                                                <div class="table-cell status-cell">
                                                    <c:choose>
                                                        <c:when test="${booking.status == 'PENDING'}">
                                                            <span class="status-badge pending">⏳ Pending</span>
                                                        </c:when>
                                                        <c:when test="${booking.status == 'APPROVED'}">
                                                            <span class="status-badge approved">✅ Approved</span>
                                                        </c:when>
                                                        <c:when test="${booking.status == 'REJECTED'}">
                                                            <span class="status-badge rejected">❌ Rejected</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="status-badge other">${booking.status}</span>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <c:if test="${booking.priorityLevel == 'VIP'}">
                                                        <span class="priority-badge vip">⭐ VIP</span>
                                                    </c:if>
                                                </div>

                                                <div class="table-cell actions-cell">
                                                    <c:if test="${booking.status == 'PENDING'}">
                                                        <div class="action-buttons">
                                                            <button class="action-btn approve-btn"
                                                                    data-booking-id="${booking.bookingId}"
                                                                    title="Approve Booking">
                                                                ✅ Approve
                                                            </button>
                                                            <button class="action-btn reject-btn"
                                                                    data-booking-id="${booking.bookingId}"
                                                                    title="Reject Booking">
                                                                ❌ Reject
                                                            </button>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>

                                <div class="table-footer">
                                    <a href="${pageContext.request.contextPath}/admin/bookings" class="view-all-btn">
                                        👁️ View All Bookings
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>

            <!-- Admin Sidebar -->
            <aside class="admin-sidebar">

                <!-- Today's Summary -->
                <div class="sidebar-card">
                    <h3>📅 Today's Summary</h3>

                    <div class="today-stats">
                        <div class="today-stat">
                            <div class="stat-value">${not empty todayBookings ? todayBookings.size() : 0}</div>
                            <div class="stat-desc">Today's Bookings</div>
                        </div>
                        <div class="today-stat">
                            <div class="stat-value">${pendingCount}</div>
                            <div class="stat-desc">Need Approval</div>
                        </div>
                    </div>

                    <div class="current-date">
                        <jsp:useBean id="currentDate" class="java.util.Date" />
                        📍 <fmt:formatDate value="${currentDate}" pattern="EEEE, MMM dd, yyyy"/>
                    </div>
                </div>

                <!-- Cabin Statistics -->
                <div class="sidebar-card">
                    <h3>🏠 Cabin Statistics</h3>

                    <div class="cabin-stats-grid">
                        <div class="cabin-stat">
                            <div class="cabin-stat-number">${totalCabins != null ? totalCabins : 0}</div>
                            <div class="cabin-stat-label">Total Cabins</div>
                        </div>
                        <div class="cabin-stat">
                            <div class="cabin-stat-number">${activeCabins != null ? activeCabins : 0}</div>
                            <div class="cabin-stat-label">Active</div>
                        </div>
                        <div class="cabin-stat">
                            <div class="cabin-stat-number">${vipCabins != null ? vipCabins : 0}</div>
                            <div class="cabin-stat-label">VIP Only</div>
                        </div>
                        <div class="cabin-stat">
                            <div class="cabin-stat-number">${maintenanceCabins != null ? maintenanceCabins : 0}</div>
                            <div class="cabin-stat-label">Maintenance</div>
                        </div>
                    </div>

                    <div class="cabin-stats-action">
                        <a href="${pageContext.request.contextPath}/admin/manage-cabins" class="stats-btn">
                            👁️ View All Cabins
                        </a>
                    </div>
                </div>

                <!-- User Distribution -->
                <div class="sidebar-card">
                    <h3>👥 User Distribution</h3>

                    <c:choose>
                        <c:when test="${totalUsers > 0}">
                            <div class="user-distribution">
                                <div class="user-type-item">
                                    <div class="user-type-info">
                                        <span class="user-type-label">👤 Normal Users</span>
                                        <span class="user-type-count">${normalUsers}</span>
                                    </div>
                                    <div class="progress-bar">
                                        <div class="progress-fill normal" style="width: ${normalUsers * 100 / totalUsers}%"></div>
                                    </div>
                                </div>

                                <div class="user-type-item">
                                    <div class="user-type-info">
                                        <span class="user-type-label">⭐ VIP Users</span>
                                        <span class="user-type-count">${vipUsers}</span>
                                    </div>
                                    <div class="progress-bar">
                                        <div class="progress-fill vip" style="width: ${vipUsers * 100 / totalUsers}%"></div>
                                    </div>
                                </div>

                                <div class="user-type-item">
                                    <div class="user-type-info">
                                        <span class="user-type-label">👨‍💼 Admins</span>
                                        <span class="user-type-count">${adminUsers}</span>
                                    </div>
                                    <div class="progress-bar">
                                        <div class="progress-fill admin" style="width: ${adminUsers * 100 / totalUsers}%"></div>
                                    </div>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-users">
                                <div class="empty-icon">👥</div>
                                <p>No user data available</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Quick Actions -->
                <div class="sidebar-card">
                    <h3>⚡ Quick Actions</h3>

                    <div class="quick-actions">
                        <!-- Cabin Actions -->
                        <a href="${pageContext.request.contextPath}/admin/add-cabin" class="quick-action primary">
                            ➕ Add New Cabin
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/manage-cabins" class="quick-action info">
                            🏠 Manage Cabins
                        </a>

                        <!-- Booking Actions -->
                        <a href="${pageContext.request.contextPath}/admin/bookings?filter=pending" class="quick-action warning">
                            ⏳ Review Pending (${pendingCount})
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/bookings?filter=vip" class="quick-action vip">
                            ⭐ VIP Priority (${not empty vipBookings ? vipBookings.size() : 0})
                        </a>

                        <!-- User Actions -->
                        <a href="${pageContext.request.contextPath}/admin/users" class="quick-action success">
                            👥 Manage Users
                        </a>

                        <!-- Analytics -->
                        <a href="${pageContext.request.contextPath}/admin/analytics" class="quick-action secondary">
                            📈 View Analytics
                        </a>
                    </div>
                </div>
            </aside>
        </div>
    </main>

    <!-- Footer -->
    <footer class="admin-footer">
        <p>&copy; 2025 Yash Technology - Admin Panel | Logged in as: ${admin.name}</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/admin-dashboard.js"></script>
</body>
</html>
