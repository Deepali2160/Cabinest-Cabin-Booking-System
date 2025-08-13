<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Bookings - Yash Technology</title>
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/my-bookings.css" rel="stylesheet">
</head>
<body>
    <!-- Header Navigation -->
    <nav class="bookings-nav">
        <div class="nav-container">
            <div class="nav-brand">
                <h2>🏢 Yash Technology</h2>
                <span>My Bookings</span>
            </div>

            <div class="nav-menu">
                <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">
                    🏠 Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/book" class="nav-link">
                    📅 New Booking
                </a>
                <a href="${pageContext.request.contextPath}/mybookings" class="nav-link active">
                    📋 My Bookings
                </a>
            </div>

            <div class="nav-user">
                <span class="user-name">${user.name}</span>
                <c:if test="${user.userType == 'VIP'}">
                    <span class="vip-badge">⭐ VIP</span>
                </c:if>
                <div class="user-dropdown">
                    <button class="dropdown-btn" id="userDropdown">⚙️</button>
                    <div class="dropdown-menu" id="dropdownMenu">
                        <a href="${pageContext.request.contextPath}/profile">👤 Profile</a>
                        <a href="${pageContext.request.contextPath}/logout">🚪 Logout</a>
                    </div>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <main class="bookings-main">

        <!-- Page Header -->
        <section class="bookings-header">
            <div class="header-content">
                <div class="header-info">
                    <h1>📋 My Bookings</h1>
                    <p>Track and manage all your cabin reservations at Yash Technology</p>
                </div>

                <div class="booking-stats">
                    <div class="stat-card">
                        <div class="stat-number">${totalBookings}</div>
                        <div class="stat-label">Total Bookings</div>
                    </div>
                    <div class="stat-card pending">
                        <div class="stat-number">${pendingBookings.size()}</div>
                        <div class="stat-label">Pending</div>
                    </div>
                    <div class="stat-card approved">
                        <div class="stat-number">${approvedBookings.size()}</div>
                        <div class="stat-label">Approved</div>
                    </div>
                    <div class="stat-card rejected">
                        <div class="stat-number">${rejectedBookings.size()}</div>
                        <div class="stat-label">Rejected</div>
                    </div>
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

        <!-- Bookings Content -->
        <div class="bookings-container">
            <div class="bookings-section">

                <!-- Filter Tabs -->
                <div class="filter-tabs">
                    <button class="filter-tab active" data-filter="all">
                        📋 All (${totalBookings})
                    </button>
                    <button class="filter-tab" data-filter="pending">
                        ⏳ Pending (${pendingBookings.size()})
                    </button>
                    <button class="filter-tab" data-filter="approved">
                        ✅ Approved (${approvedBookings.size()})
                    </button>
                    <button class="filter-tab" data-filter="rejected">
                        ❌ Rejected (${rejectedBookings.size()})
                    </button>
                </div>

                <!-- Bookings List -->
                <div class="bookings-list">

                    <!-- All Bookings Tab -->
                    <div class="booking-tab-content active" id="all-content">
                        <c:choose>
                            <c:when test="${empty allBookings}">
                                <div class="empty-state">
                                    <div class="empty-icon">📅</div>
                                    <h3>No bookings yet</h3>
                                    <p>You haven't made any cabin bookings yet. Start by booking your first cabin!</p>
                                    <a href="${pageContext.request.contextPath}/book" class="empty-action-btn">
                                        📅 Make First Booking
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="booking" items="${allBookings}">
                                    <div class="booking-card" data-status="${booking.status.name().toLowerCase()}">
                                        <div class="booking-header">
                                            <div class="booking-title">
                                                <h3>${booking.cabinName}</h3>
                                                <div class="booking-meta">
                                                    <span class="booking-date">
                                                        📅 <fmt:formatDate value="${booking.bookingDate}" pattern="EEEE, MMM dd, yyyy"/>
                                                    </span>
                                                    <span class="booking-time">
                                                        🕐 ${booking.timeSlot}
                                                    </span>
                                                </div>
                                            </div>
                                            <div class="booking-status">
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

                                                <c:if test="${user.userType == 'VIP'}">
                                                    <span class="vip-priority">⭐ VIP Priority</span>
                                                </c:if>
                                            </div>
                                        </div>

                                        <div class="booking-body">
                                            <div class="booking-purpose">
                                                <strong>Purpose:</strong> ${booking.purpose}
                                            </div>

                                            <div class="booking-details">
                                                <div class="detail-item">
                                                    <span class="detail-label">Booking ID:</span>
                                                    <span class="detail-value">#${booking.bookingId}</span>
                                                </div>
                                                <div class="detail-item">
                                                    <span class="detail-label">Created:</span>
                                                    <span class="detail-value">
                                                        <fmt:formatDate value="${booking.createdAt}" pattern="MMM dd, yyyy 'at' hh:mm a"/>
                                                    </span>
                                                </div>
                                                <c:if test="${booking.approvedAt != null}">
                                                    <div class="detail-item">
                                                        <span class="detail-label">Approved:</span>
                                                        <span class="detail-value">
                                                            <fmt:formatDate value="${booking.approvedAt}" pattern="MMM dd, yyyy 'at' hh:mm a"/>
                                                        </span>
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>

                                        <div class="booking-actions">
                                            <c:choose>
                                                <c:when test="${booking.status == 'PENDING'}">
                                                    <button class="action-btn cancel-btn"
                                                            data-booking-id="${booking.bookingId}"
                                                            data-cabin-name="${booking.cabinName}">
                                                        🗑️ Cancel Booking
                                                    </button>
                                                    <div class="pending-info">
                                                        ⏳ Awaiting admin approval
                                                    </div>
                                                </c:when>
                                                <c:when test="${booking.status == 'APPROVED'}">
                                                    <div class="approved-info">
                                                        ✅ Your booking is confirmed!
                                                    </div>
                                                    <div class="approved-note">
                                                        Ready for your meeting
                                                    </div>
                                                </c:when>
                                                <c:when test="${booking.status == 'REJECTED'}">
                                                    <a href="${pageContext.request.contextPath}/book" class="action-btn book-again-btn">
                                                        🔄 Book Again
                                                    </a>
                                                    <div class="rejected-info">
                                                        ❌ Booking was not approved
                                                    </div>
                                                </c:when>
                                            </c:choose>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Pending Bookings Tab -->
                    <div class="booking-tab-content" id="pending-content">
                        <c:choose>
                            <c:when test="${empty pendingBookings}">
                                <div class="empty-state">
                                    <div class="empty-icon">⏳</div>
                                    <h3>No pending bookings</h3>
                                    <p>All your bookings have been processed.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="booking" items="${pendingBookings}">
                                    <div class="booking-card pending-highlight">
                                        <div class="booking-header">
                                            <div class="booking-title">
                                                <h3>${booking.cabinName}</h3>
                                                <div class="booking-meta">
                                                    <span class="booking-date">
                                                        📅 <fmt:formatDate value="${booking.bookingDate}" pattern="MMM dd, yyyy"/>
                                                    </span>
                                                    <span class="booking-time">🕐 ${booking.timeSlot}</span>
                                                </div>
                                            </div>
                                            <div class="booking-status">
                                                <span class="status-badge pending">⏳ Awaiting Approval</span>
                                            </div>
                                        </div>

                                        <div class="booking-body">
                                            <div class="booking-purpose">
                                                <strong>Purpose:</strong> ${booking.purpose}
                                            </div>
                                        </div>

                                        <div class="booking-actions">
                                            <button class="action-btn cancel-btn"
                                                    data-booking-id="${booking.bookingId}"
                                                    data-cabin-name="${booking.cabinName}">
                                                🗑️ Cancel
                                            </button>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Approved Bookings Tab -->
                    <div class="booking-tab-content" id="approved-content">
                        <c:choose>
                            <c:when test="${empty approvedBookings}">
                                <div class="empty-state">
                                    <div class="empty-icon">✅</div>
                                    <h3>No approved bookings</h3>
                                    <p>You don't have any approved bookings yet.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="booking" items="${approvedBookings}">
                                    <div class="booking-card approved-highlight">
                                        <div class="booking-header">
                                            <div class="booking-title">
                                                <h3>${booking.cabinName}</h3>
                                                <div class="booking-meta">
                                                    <span class="booking-date">
                                                        📅 <fmt:formatDate value="${booking.bookingDate}" pattern="MMM dd, yyyy"/>
                                                    </span>
                                                    <span class="booking-time">🕐 ${booking.timeSlot}</span>
                                                </div>
                                            </div>
                                            <div class="booking-status">
                                                <span class="status-badge approved">✅ Confirmed</span>
                                            </div>
                                        </div>

                                        <div class="booking-body">
                                            <div class="booking-purpose">
                                                <strong>Purpose:</strong> ${booking.purpose}
                                            </div>
                                            <c:if test="${booking.approvedAt != null}">
                                                <div class="approved-date">
                                                    Approved on: <fmt:formatDate value="${booking.approvedAt}" pattern="MMM dd, yyyy"/>
                                                </div>
                                            </c:if>
                                        </div>

                                        <div class="booking-actions">
                                            <div class="confirmed-badge">
                                                🎉 Ready for your meeting!
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Rejected Bookings Tab -->
                    <div class="booking-tab-content" id="rejected-content">
                        <c:choose>
                            <c:when test="${empty rejectedBookings}">
                                <div class="empty-state">
                                    <div class="empty-icon">❌</div>
                                    <h3>No rejected bookings</h3>
                                    <p>All your booking requests have been successful!</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="booking" items="${rejectedBookings}">
                                    <div class="booking-card rejected-highlight">
                                        <div class="booking-header">
                                            <div class="booking-title">
                                                <h3>${booking.cabinName}</h3>
                                                <div class="booking-meta">
                                                    <span class="booking-date">
                                                        📅 <fmt:formatDate value="${booking.bookingDate}" pattern="MMM dd, yyyy"/>
                                                    </span>
                                                    <span class="booking-time">🕐 ${booking.timeSlot}</span>
                                                </div>
                                            </div>
                                            <div class="booking-status">
                                                <span class="status-badge rejected">❌ Not Approved</span>
                                            </div>
                                        </div>

                                        <div class="booking-body">
                                            <div class="booking-purpose">
                                                <strong>Purpose:</strong> ${booking.purpose}
                                            </div>
                                        </div>

                                        <div class="booking-actions">
                                            <a href="${pageContext.request.contextPath}/book" class="action-btn book-again-btn">
                                                🔄 Try Booking Again
                                            </a>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Sidebar -->
            <aside class="bookings-sidebar">

                <!-- Quick Actions -->
                <div class="sidebar-card">
                    <h3>⚡ Quick Actions</h3>
                    <div class="quick-actions">
                        <a href="${pageContext.request.contextPath}/book" class="quick-action-btn primary">
                            📅 New Booking
                        </a>
                        <a href="${pageContext.request.contextPath}/dashboard" class="quick-action-btn secondary">
                            🏠 Dashboard
                        </a>
                        <a href="${pageContext.request.contextPath}/profile" class="quick-action-btn tertiary">
                            👤 Profile
                        </a>
                    </div>
                </div>

                <!-- Booking Summary -->
                <div class="sidebar-card">
                    <h3>📊 Booking Summary</h3>
                    <div class="summary-stats">
                        <div class="summary-item">
                            <span class="summary-label">Total Bookings:</span>
                            <span class="summary-value">${totalBookings}</span>
                        </div>
                        <div class="summary-item">
                            <span class="summary-label">Success Rate:</span>
                            <span class="summary-value">
                                <c:choose>
                                    <c:when test="${totalBookings > 0}">
                                        ${Math.round((approvedBookings.size() * 100.0) / totalBookings)}%
                                    </c:when>
                                    <c:otherwise>0%</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="summary-item">
                            <span class="summary-label">Most Recent:</span>
                            <span class="summary-value">
                                <c:choose>
                                    <c:when test="${not empty allBookings}">
                                        <fmt:formatDate value="${allBookings[0].createdAt}" pattern="MMM dd"/>
                                    </c:when>
                                    <c:otherwise>None</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                </div>

                <!-- Tips -->
                <div class="sidebar-card">
                    <h3>💡 Booking Tips</h3>
                    <div class="tips-list">
                        <div class="tip-item">
                            📋 Book in advance for better availability
                        </div>
                        <div class="tip-item">
                            ⏰ Avoid peak hours (10 AM - 2 PM) for quicker approval
                        </div>
                        <div class="tip-item">
                            💬 Be specific about meeting purpose for faster approval
                        </div>
                        <c:if test="${user.userType == 'VIP'}">
                            <div class="tip-item vip-tip">
                                ⭐ VIP bookings receive priority processing
                            </div>
                        </c:if>
                    </div>
                </div>
            </aside>
        </div>
    </main>

    <!-- Cancel Confirmation Modal -->
    <div class="modal-overlay" id="cancelModal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>⚠️ Cancel Booking</h3>
                <button class="modal-close" id="modalClose">×</button>
            </div>

            <div class="modal-body">
                <p>Are you sure you want to cancel your booking for:</p>
                <div class="cancel-booking-info">
                    <strong id="cancelCabinName"></strong>
                </div>
                <div class="cancel-warning">
                    ⚠️ This action cannot be undone.
                </div>
            </div>

            <div class="modal-footer">
                <button class="modal-btn secondary" id="keepBooking">
                    ↩️ Keep Booking
                </button>
                <button class="modal-btn danger" id="confirmCancel">
                    🗑️ Yes, Cancel Booking
                </button>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bookings-footer">
        <p>&copy; 2024 Yash Technology - Cabin Booking System</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/my-bookings.js"></script>
</body>
</html>
