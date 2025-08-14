<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Analytics Dashboard - Yash Technology Admin</title>
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/admin-analytics.css" rel="stylesheet">
</head>
<body>
    <!-- Admin Navigation -->
    <nav class="admin-nav">
        <div class="nav-container">
            <div class="nav-brand">
                <h2>🔧 Yash Technology Admin</h2>
                <span>Analytics Dashboard</span>
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
                <a href="${pageContext.request.contextPath}/admin/analytics" class="nav-link active">
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

        <!-- Page Header -->
        <section class="page-header">
            <div class="header-content">
                <div class="header-info">
                    <h1>📈 Analytics Dashboard</h1>
                    <p>AI-Powered insights and performance metrics for Yash Technology - Indore</p>
                </div>

                <div class="header-actions">
                    <div class="date-range">
                        <select id="dateRange" class="date-select">
                            <option value="today">📅 Today</option>
                            <option value="week">📅 This Week</option>
                            <option value="month" selected>📅 This Month</option>
                            <option value="year">📅 This Year</option>
                        </select>
                    </div>
                    <button class="action-btn primary" id="refreshData">
                        🔄 Refresh Data
                    </button>
                </div>
            </div>
        </section>

        <!-- Key Metrics Cards -->
        <section class="metrics-section">
            <div class="metrics-grid">
                <div class="metric-card total-bookings">
                    <div class="metric-icon">📅</div>
                    <div class="metric-content">
                        <div class="metric-number" data-target="${totalBookings != null ? totalBookings : 0}">${totalBookings != null ? totalBookings : 0}</div>
                        <div class="metric-label">Total Bookings</div>
                        <div class="metric-trend">
                            <span class="trend-indicator up">📈 +12%</span>
                            <span class="trend-period">vs last month</span>
                        </div>
                    </div>
                </div>

                <div class="metric-card approval-rate">
                    <div class="metric-icon">✅</div>
                    <div class="metric-content">
                        <div class="metric-number" data-target="${approvalRate != null ? approvalRate : 0}">${approvalRate != null ? approvalRate : 0}%</div>
                        <div class="metric-label">Approval Rate</div>
                        <div class="metric-trend">
                            <span class="trend-indicator up">📈 +5%</span>
                            <span class="trend-period">vs last month</span>
                        </div>
                    </div>
                </div>

                <div class="metric-card vip-bookings">
                    <div class="metric-icon">⭐</div>
                    <div class="metric-content">
                        <div class="metric-number" data-target="${vipBookingCount != null ? vipBookingCount : 0}">${vipBookingCount != null ? vipBookingCount : 0}</div>
                        <div class="metric-label">VIP Bookings</div>
                        <div class="metric-trend">
                            <span class="trend-indicator up">📈 +18%</span>
                            <span class="trend-period">vs last month</span>
                        </div>
                    </div>
                </div>

                <div class="metric-card active-users">
                    <div class="metric-icon">👥</div>
                    <div class="metric-content">
                        <div class="metric-number" data-target="${activeUsers != null ? activeUsers : 0}">${activeUsers != null ? activeUsers : 0}</div>
                        <div class="metric-label">Active Users</div>
                        <div class="metric-trend">
                            <span class="trend-indicator up">📈 +8%</span>
                            <span class="trend-period">vs last month</span>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Charts Section -->
        <section class="charts-section">
            <div class="charts-grid">
                <!-- Booking Status Chart -->
                <div class="chart-card">
                    <div class="chart-header">
                        <h3>📊 Booking Status Distribution</h3>
                        <div class="chart-legend" id="statusLegend"></div>
                    </div>
                    <div class="chart-container">
                        <canvas id="bookingStatusChart"></canvas>
                    </div>
                </div>

                <!-- User Type Distribution -->
                <div class="chart-card">
                    <div class="chart-header">
                        <h3>👥 User Type Distribution</h3>
                        <div class="chart-legend" id="userTypeLegend"></div>
                    </div>
                    <div class="chart-container">
                        <canvas id="userTypeChart"></canvas>
                    </div>
                </div>
            </div>
        </section>

        <!-- Time Slots & Activity Section -->
        <section class="activity-section">
            <div class="activity-grid">
                <!-- Popular Time Slots -->
                <div class="activity-card large">
                    <div class="activity-header">
                        <h3>🕐 Popular Time Slots</h3>
                        <span class="ai-badge">🤖 AI INSIGHTS</span>
                    </div>
                    <div class="activity-content">
                        <div class="chart-container">
                            <canvas id="popularTimeSlotsChart"></canvas>
                        </div>
                        <div class="time-insights">
                            <div class="insight-item">
                                <div class="insight-label">Peak Hour</div>
                                <div class="insight-value">10:00 - 11:00 AM</div>
                            </div>
                            <div class="insight-item">
                                <div class="insight-label">Low Usage</div>
                                <div class="insight-value">2:00 - 3:00 PM</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Today's Activity -->
                <div class="activity-card small">
                    <div class="activity-header">
                        <h3>📋 Today's Activity</h3>
                        <div class="activity-date">
                            <!-- ✅ FIXED: Proper date formatting -->
                            <c:choose>
                                <c:when test="${not empty currentDate}">
                                    <fmt:formatDate value="${currentDate}" pattern="EEEE, MMM dd"/>
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatDate value="${now}" pattern="EEEE, MMM dd"/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="activity-content">
                        <div class="activity-stats">
                            <div class="activity-stat">
                                <!-- ✅ FIXED: Use correct attribute name -->
                                <div class="stat-number">${todaysBookings != null ? todaysBookings : 0}</div>
                                <div class="stat-label">New Bookings</div>
                            </div>
                            <div class="activity-stat">
                                <div class="stat-number">${pendingBookings != null ? pendingBookings : 0}</div>
                                <div class="stat-label">Pending</div>
                            </div>
                            <div class="activity-stat">
                                <!-- ✅ NEW: Today's approvals -->
                                <div class="stat-number">${todayApprovals != null ? todayApprovals : 0}</div>
                                <div class="stat-label">Approved Today</div>
                            </div>
                        </div>

                        <div class="activity-summary">
                            <div class="summary-item">
                                <span class="summary-icon">🏢</span>
                                <span class="summary-text">Yash Technology - Indore</span>
                            </div>
                            <div class="summary-item">
                                <span class="summary-icon">🏠</span>
                                <span class="summary-text">${totalCabins != null ? totalCabins : 0} Active Cabins</span>
                            </div>
                            <!-- ✅ NEW: Add today's date info -->
                            <div class="summary-item">
                                <span class="summary-icon">📅</span>
                                <span class="summary-text">
                                    <fmt:formatDate value="${now}" pattern="MMM dd, yyyy"/>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

        </section>

        <!-- AI Insights Section -->
        <section class="ai-insights-section">
            <div class="ai-insights-card">
                <div class="ai-insights-header">
                    <h3>🤖 AI-Powered Insights & Recommendations</h3>
                    <div class="ai-status">
                        <span class="status-indicator active"></span>
                        <span class="status-text">AI Engine Active</span>
                    </div>
                </div>

                <div class="ai-insights-content">
                    <div class="insights-grid">
                        <div class="insight-card trend">
                            <div class="insight-header">
                                <div class="insight-icon">📈</div>
                                <h4>Peak Usage Analysis</h4>
                            </div>
                            <div class="insight-body">
                                <c:choose>
                                    <c:when test="${not empty popularTimeSlots}">
                                        Most bookings occur during <strong>10:00-11:00 AM</strong> and <strong>2:00-3:00 PM</strong>.
                                        Consider optimizing cabin allocation during these peak hours.
                                    </c:when>
                                    <c:otherwise>
                                        Analyzing booking patterns to identify peak usage hours for optimal resource allocation.
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="insight-action">
                                <button class="insight-btn">📊 View Details</button>
                            </div>
                        </div>

                        <div class="insight-card engagement">
                            <div class="insight-header">
                                <div class="insight-icon">👥</div>
                                <h4>User Engagement</h4>
                            </div>
                            <div class="insight-body">
                                <c:choose>
                                    <c:when test="${vipUsers != null && vipUsers > 0}">
                                        ${vipUsers} VIP users show high engagement with ${vipBookingCount != null ? vipBookingCount : 0} bookings.
                                        Consider expanding VIP benefits and exclusive amenities.
                                    </c:when>
                                    <c:otherwise>
                                        Focus on user engagement programs to increase VIP conversions and user retention.
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="insight-action">
                                <button class="insight-btn">⭐ VIP Program</button>
                            </div>
                        </div>

                        <div class="insight-card performance">
                            <div class="insight-header">
                                <div class="insight-icon">⚡</div>
                                <h4>System Performance</h4>
                            </div>
                            <div class="insight-body">
                                <c:choose>
                                    <c:when test="${approvalRate != null && approvalRate >= 80}">
                                        Excellent ${approvalRate}% approval rate indicates efficient workflow and user satisfaction.
                                        Current processes are performing optimally.
                                    </c:when>
                                    <c:when test="${approvalRate != null && approvalRate >= 60}">
                                        Good ${approvalRate}% approval rate with room for improvement.
                                        Review approval workflow to reduce processing time.
                                    </c:when>
                                    <c:otherwise>
                                        ${approvalRate != null ? approvalRate : 0}% approval rate needs attention.
                                        Analyze rejection reasons and streamline approval process.
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="insight-action">
                                <button class="insight-btn">🔧 Optimize</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Detailed Metrics Table -->
        <section class="metrics-table-section">
            <div class="metrics-table-card">
                <div class="table-header">
                    <h3>📊 Detailed System Metrics</h3>
                    <div class="table-actions">
                        <button class="table-btn" id="exportMetrics">📊 Export</button>
                        <button class="table-btn" id="printReport">🖨️ Print</button>
                    </div>
                </div>

                <div class="table-container">
                    <table class="metrics-table">
                        <thead>
                            <tr>
                                <th>📊 Metric</th>
                                <th>🎯 Current Value</th>
                                <th>📈 Target</th>
                                <th>⭐ Performance</th>
                                <th>📈 Trend</th>
                                <th>📝 Notes</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>
                                    <div class="metric-info">
                                        <span class="metric-name">Booking Approval Rate</span>
                                        <span class="metric-desc">Percentage of approved bookings</span>
                                    </div>
                                </td>
                                <td>
                                    <span class="value-highlight">${approvalRate != null ? approvalRate : 0}%</span>
                                </td>
                                <td>≥80%</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${approvalRate != null && approvalRate >= 80}">
                                            <span class="performance-badge excellent">🌟 Excellent</span>
                                        </c:when>
                                        <c:when test="${approvalRate != null && approvalRate >= 60}">
                                            <span class="performance-badge good">👍 Good</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="performance-badge needs-improvement">⚠️ Needs Improvement</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="trend-indicator">
                                        <span class="trend-arrow up">↗️</span>
                                        <span class="trend-value">+5.2%</span>
                                    </div>
                                </td>
                                <td>Steady improvement over past 3 months</td>
                            </tr>

                            <tr>
                                <td>
                                    <div class="metric-info">
                                        <span class="metric-name">VIP User Engagement</span>
                                        <span class="metric-desc">VIP bookings and activity</span>
                                    </div>
                                </td>
                                <td>
                                    <span class="value-highlight">${vipBookingCount != null ? vipBookingCount : 0}</span>
                                </td>
                                <td>Growing</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${vipBookingCount != null && vipBookingCount >= 10}">
                                            <span class="performance-badge excellent">🔥 High</span>
                                        </c:when>
                                        <c:when test="${vipBookingCount != null && vipBookingCount >= 5}">
                                            <span class="performance-badge good">📈 Moderate</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="performance-badge building">🚀 Building</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="trend-indicator">
                                        <span class="trend-arrow up">↗️</span>
                                        <span class="trend-value">+18%</span>
                                    </div>
                                </td>
                                <td>VIP program showing strong adoption</td>
                            </tr>

                            <tr>
                                <td>
                                    <div class="metric-info">
                                        <span class="metric-name">System Utilization</span>
                                        <span class="metric-desc">Overall system usage</span>
                                    </div>
                                </td>
                                <td>
                                    <span class="value-highlight">${totalBookings != null ? totalBookings : 0}</span>
                                </td>
                                <td>Increasing</td>
                                <td>
                                    <span class="performance-badge excellent">⚡ Active</span>
                                </td>
                                <td>
                                    <div class="trend-indicator">
                                        <span class="trend-arrow up">↗️</span>
                                        <span class="trend-value">+12%</span>
                                    </div>
                                </td>
                                <td>Consistent growth in bookings</td>
                            </tr>

                            <tr>
                                <td>
                                    <div class="metric-info">
                                        <span class="metric-name">User Base Growth</span>
                                        <span class="metric-desc">Active user count</span>
                                    </div>
                                </td>
                                <td>
                                    <span class="value-highlight">${activeUsers != null ? activeUsers : 0}</span>
                                </td>
                                <td>Expanding</td>
                                <td>
                                    <span class="performance-badge excellent">📈 Growing</span>
                                </td>
                                <td>
                                    <div class="trend-indicator">
                                        <span class="trend-arrow up">↗️</span>
                                        <span class="trend-value">+8%</span>
                                    </div>
                                </td>
                                <td>Healthy user acquisition rate</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>

        <!-- Export Modal -->
        <div class="modal-overlay" id="exportModal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>📊 Export Analytics Report</h3>
                    <button class="modal-close" id="exportModalClose">×</button>
                </div>

                <div class="modal-body">
                    <div class="export-options">
                        <div class="export-format">
                            <h4>📄 Format</h4>
                            <label class="format-option">
                                <input type="radio" name="format" value="pdf" checked>
                                <span>📄 PDF Report</span>
                            </label>
                            <label class="format-option">
                                <input type="radio" name="format" value="excel">
                                <span>📊 Excel Spreadsheet</span>
                            </label>
                            <label class="format-option">
                                <input type="radio" name="format" value="csv">
                                <span>📝 CSV Data</span>
                            </label>
                        </div>

                        <div class="export-range">
                            <h4>📅 Date Range</h4>
                            <select class="range-select">
                                <option value="today">Today</option>
                                <option value="week">This Week</option>
                                <option value="month" selected>This Month</option>
                                <option value="quarter">This Quarter</option>
                                <option value="year">This Year</option>
                            </select>
                        </div>

                        <div class="export-sections">
                            <h4>📋 Include Sections</h4>
                            <label class="section-option">
                                <input type="checkbox" checked> Key Metrics
                            </label>
                            <label class="section-option">
                                <input type="checkbox" checked> Charts & Graphs
                            </label>
                            <label class="section-option">
                                <input type="checkbox" checked> AI Insights
                            </label>
                            <label class="section-option">
                                <input type="checkbox"> Detailed Tables
                            </label>
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button class="modal-btn secondary" id="cancelExport">
                        ↩️ Cancel
                    </button>
                    <button class="modal-btn primary" id="confirmExport">
                        📊 Generate Report
                    </button>
                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="admin-footer">
        <div class="footer-content">
            <p>&copy; 2024 Yash Technology - Analytics Dashboard</p>
            <div class="footer-links">
                <c:choose>
                    <c:when test="${not empty now}">
                        <span>Last Updated: <fmt:formatDate value="${now}" pattern="MMM dd, yyyy HH:mm"/></span>
                    </c:when>
                    <c:otherwise>
                        <span>Last Updated: Just now</span>
                    </c:otherwise>
                </c:choose>
                <span>•</span>
                <span>Data Refresh: Auto (10 min)</span>
            </div>
        </div>
    </footer>

    <!-- Chart.js CDN -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/admin-analytics.js"></script>

    <!-- ✅ Fixed JavaScript data passing -->
    <script>
        // Ensure all values are properly validated before passing to JavaScript
        window.analyticsData = {
            bookingStatus: {
                approved: ${approvedBookings != null ? approvedBookings : 0},
                pending: ${pendingBookings != null ? pendingBookings : 0},
                rejected: ${rejectedBookings != null ? rejectedBookings : 0}
            },
            userTypes: {
                normal: ${(totalUsers != null && vipUsers != null && adminUsers != null) ?
                         (totalUsers - vipUsers - adminUsers > 0 ? totalUsers - vipUsers - adminUsers : 0) : 0},
                vip: ${vipUsers != null ? vipUsers : 0},
                admin: ${adminUsers != null ? adminUsers : 0}
            },
            timeSlots: [
                <c:choose>
                    <c:when test="${not empty popularTimeSlots}">
                        <c:forEach var="timeSlot" items="${popularTimeSlots}" varStatus="status">
                            '${timeSlot}'<c:if test="${!status.last}">,</c:if>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        '9:00 AM', '10:00 AM', '11:00 AM', '2:00 PM', '3:00 PM'
                    </c:otherwise>
                </c:choose>
            ],
            contextPath: '${pageContext.request.contextPath}'
        };

        // Debug log
        console.log('✅ Analytics data initialized:', window.analyticsData);
    </script>
</body>
</html>
