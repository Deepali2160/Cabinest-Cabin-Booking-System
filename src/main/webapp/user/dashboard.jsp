<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Yash Technology Cabin Booking</title>
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/dashboard.css" rel="stylesheet">
</head>
<body>
    <!-- Header Navigation -->
    <nav class="dashboard-nav">
        <div class="nav-container">
            <div class="nav-brand">
                <h2>🏢 Yash Technology</h2>
                <span>Cabin Booking System</span>
            </div>

            <div class="nav-menu" id="navMenu">
                <a href="${pageContext.request.contextPath}/dashboard" class="nav-link active">
                    🏠 Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/book" class="nav-link">
                    📅 New Booking
                </a>
                <a href="${pageContext.request.contextPath}/mybookings" class="nav-link">
                    📋 My Bookings
                </a>
                <a href="${pageContext.request.contextPath}/profile" class="nav-link">
                    👤 Profile
                </a>
            </div>

            <div class="nav-user">
                <div class="user-info">
                    <span class="user-name">${user.name}</span>
                    <c:if test="${user.userType == 'VIP'}">
                        <span class="vip-badge">⭐ VIP</span>
                    </c:if>
                    <c:if test="${user.admin}">
                        <span class="admin-badge">👨‍💼 Admin</span>
                    </c:if>
                </div>
                <div class="user-dropdown">
                    <button class="dropdown-btn" id="userDropdown">⚙️</button>
                    <div class="dropdown-menu" id="dropdownMenu">
                        <a href="${pageContext.request.contextPath}/profile">👤 Profile</a>
                        <c:if test="${user.admin}">
                            <a href="${pageContext.request.contextPath}/admin/dashboard">🔧 Admin Panel</a>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/logout">🚪 Logout</a>
                    </div>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <main class="dashboard-main">

        <!-- Messages -->
        <div id="message-container">
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="message success-message">
                    ✅ ${sessionScope.successMessage}
                </div>
                <c:remove var="successMessage" scope="session"/>
            </c:if>

            <c:if test="${not empty error}">
                <div class="message error-message">
                    ❌ ${error}
                </div>
            </c:if>
        </div>

        <!-- Welcome Section -->
        <section class="welcome-section">
            <div class="welcome-card">
                <div class="welcome-content">
                    <h1>🌟 Welcome back, ${user.name}!</h1>
                    <p class="company-info">📍 Yash Technology - Indore</p>
                    <div class="user-stats">
                        <div class="stat-item">
                            <span class="stat-number">${totalBookings}</span>
                            <span class="stat-label">Total Bookings</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-number">${pendingBookings}</span>
                            <span class="stat-label">Pending</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-number">${approvedBookings}</span>
                            <span class="stat-label">Approved</span>
                        </div>
                    </div>
                </div>
                <div class="booking-score">
                    <div class="score-circle">
                        <span class="score-number">${bookingScore}</span>
                        <span class="score-max">/100</span>
                    </div>
                    <p>Booking Score</p>
                </div>
            </div>
        </section>

        <!-- Quick Actions -->
        <section class="actions-section">
            <h2>⚡ Quick Actions</h2>
            <div class="action-cards">
                <a href="${pageContext.request.contextPath}/book" class="action-card">
                    <div class="action-icon">📅</div>
                    <h3>Book Cabin</h3>
                    <p>Reserve a meeting room</p>
                </a>

                <a href="${pageContext.request.contextPath}/mybookings" class="action-card">
                    <div class="action-icon">📋</div>
                    <h3>My Bookings</h3>
                    <p>View booking history</p>
                </a>

                <a href="${pageContext.request.contextPath}/profile" class="action-card">
                    <div class="action-icon">⚙️</div>
                    <h3>Profile</h3>
                    <p>Update account settings</p>
                </a>
            </div>
        </section>

        <!-- Main Content Grid -->
        <div class="dashboard-grid">

            <!-- Available Cabins -->
            <section class="cabins-section">
                <div class="section-header">
                    <h2>🏠 Available Cabins</h2>
                    <span class="cabin-count">${cabins.size()} available</span>
                </div>

                <div class="cabins-grid">
                    <c:if test="${empty cabins}">
                        <div class="empty-state">
                            <div class="empty-icon">🏢</div>
                            <p>No cabins available for your access level</p>
                        </div>
                    </c:if>

                    <c:forEach var="cabin" items="${cabins}" varStatus="status">
                        <c:if test="${status.index < 6}">
                            <div class="cabin-card">
                                <div class="cabin-header">
                                    <h4>${cabin.name}</h4>
                                    <div class="cabin-badges">
                                        <c:if test="${cabin.vipOnly}">
                                            <span class="vip-badge">⭐ VIP</span>
                                        </c:if>
                                        <c:choose>
                                            <c:when test="${cabin.status == 'ACTIVE'}">
                                                <span class="status-badge available">✅ Available</span>
                                            </c:when>
                                            <c:when test="${cabin.status == 'MAINTENANCE'}">
                                                <span class="status-badge maintenance">🔧 Maintenance</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge inactive">❌ Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                                <div class="cabin-info">
                                    <p>👥 ${cabin.capacity} people</p>
                                    <p>📍 ${cabin.location}</p>
                                    <c:if test="${not empty cabin.amenities}">
                                        <p class="amenities">✨ ${cabin.amenities}</p>
                                    </c:if>
                                </div>

                                <c:if test="${cabin.status == 'ACTIVE'}">
                                    <a href="${pageContext.request.contextPath}/book?cabinId=${cabin.cabinId}"
                                       class="book-btn">
                                        📅 Book Now
                                    </a>
                                </c:if>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>

                <c:if test="${cabins.size() > 6}">
                    <div class="view-all">
                        <a href="${pageContext.request.contextPath}/company" class="view-all-btn">
                            👁️ View All Cabins (${cabins.size()})
                        </a>
                    </div>
                </c:if>
            </section>

            <!-- Sidebar -->
            <aside class="dashboard-sidebar">

                <!-- Recent Bookings -->
                <section class="recent-section">
                    <h3>📅 Recent Bookings</h3>

                    <c:if test="${empty recentBookings}">
                        <div class="empty-state small">
                            <div class="empty-icon">📅</div>
                            <p>No recent bookings</p>
                        </div>
                    </c:if>

                    <div class="booking-list">
                        <c:forEach var="booking" items="${recentBookings}">
                            <div class="booking-item">
                                <div class="booking-info">
                                    <h5>${booking.cabinName}</h5>
                                    <p class="booking-date">
                                        <fmt:formatDate value="${booking.bookingDate}" pattern="MMM dd, yyyy"/>
                                    </p>
                                    <p class="booking-time">${booking.timeSlot}</p>
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
                                            <span class="status-badge">${booking.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <c:if test="${not empty recentBookings}">
                        <a href="${pageContext.request.contextPath}/mybookings" class="view-all-link">
                            👁️ View All Bookings
                        </a>
                    </c:if>
                </section>

                <!-- Popular Times -->
                <section class="popular-section">
                    <h3>🕐 Popular Time Slots</h3>

                    <c:if test="${empty popularTimeSlots}">
                        <p class="no-data">No data available</p>
                    </c:if>

                    <div class="popular-list">
                        <c:forEach var="timeSlot" items="${popularTimeSlots}" varStatus="status">
                            <c:if test="${status.index < 5}">
                                <div class="popular-item">
                                    <span class="time-slot">${timeSlot}</span>
                                    <span class="popular-badge">🔥 Popular</span>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>

                    <div class="tip">
                        💡 Book during less popular times for better availability
                    </div>
                </section>
            </aside>
        </div>

        <!-- Recommended Cabins -->
        <c:if test="${not empty recommendedCabins}">
            <section class="recommendations-section">
                <h2>💡 Recommended for You</h2>
                <p class="recommendations-subtitle">Based on your booking history</p>

                <div class="recommendations-grid">
                    <c:forEach var="cabin" items="${recommendedCabins}" varStatus="status">
                        <c:if test="${status.index < 3}">
                            <div class="recommendation-card">
                                <div class="recommendation-header">
                                    <h4>${cabin.name}</h4>
                                    <c:if test="${cabin.vipOnly}">
                                        <span class="vip-badge">⭐ VIP</span>
                                    </c:if>
                                </div>
                                <div class="recommendation-info">
                                    <p>👥 ${cabin.capacity} people</p>
                                    <p>📍 ${cabin.location}</p>
                                    <c:if test="${not empty cabin.amenities}">
                                        <p class="amenities">✨ ${cabin.amenities}</p>
                                    </c:if>
                                </div>
                                <a href="${pageContext.request.contextPath}/book?cabinId=${cabin.cabinId}"
                                   class="recommendation-btn">
                                    📅 Book This
                                </a>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </section>
        </c:if>
    </main>

    <!-- Footer -->
    <footer class="dashboard-footer">
        <p>&copy; 2024 Yash Technology - Cabin Booking System</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
</body>
</html>
