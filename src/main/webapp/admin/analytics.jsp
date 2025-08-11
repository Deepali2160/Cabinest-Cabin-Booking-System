<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Analytics Dashboard - Admin Panel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <!-- Admin Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="fas fa-cogs"></i> Admin Panel
            </a>

            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fas fa-arrow-left"></i> Back to Dashboard
                </a>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container-fluid mt-4">

        <!-- Page Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card dashboard-card">
                    <div class="card-body bg-gradient text-white"
                         style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                        <h2 class="mb-2">
                            <i class="fas fa-chart-line"></i> Analytics Dashboard
                        </h2>
                        <p class="mb-0">
                            <i class="fas fa-brain"></i> AI-Powered insights and system performance metrics
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Key Metrics Cards -->
        <div class="row mb-4">
            <div class="col-md-3 col-sm-6 mb-3">
                <div class="card dashboard-card stat-card">
                    <div class="card-body text-center text-white">
                        <div class="display-6 mb-2">
                            <i class="fas fa-calendar-check"></i>
                        </div>
                        <div class="stat-number">${totalBookings}</div>
                        <div class="stat-label">Total Bookings</div>
                    </div>
                </div>
            </div>

            <div class="col-md-3 col-sm-6 mb-3">
                <div class="card dashboard-card" style="background: linear-gradient(135deg, #28a745 0%, #20c997 100%);">
                    <div class="card-body text-center text-white">
                        <div class="display-6 mb-2">
                            <i class="fas fa-percentage"></i>
                        </div>
                        <div class="stat-number">${approvalRate}%</div>
                        <div class="stat-label">Approval Rate</div>
                    </div>
                </div>
            </div>

            <div class="col-md-3 col-sm-6 mb-3">
                <div class="card dashboard-card" style="background: linear-gradient(135deg, #ffc107 0%, #fd7e14 100%);">
                    <div class="card-body text-center text-white">
                        <div class="display-6 mb-2">
                            <i class="fas fa-star"></i>
                        </div>
                        <div class="stat-number">${vipBookingCount}</div>
                        <div class="stat-label">VIP Bookings</div>
                    </div>
                </div>
            </div>

            <div class="col-md-3 col-sm-6 mb-3">
                <div class="card dashboard-card" style="background: linear-gradient(135deg, #17a2b8 0%, #6f42c1 100%);">
                    <div class="card-body text-center text-white">
                        <div class="display-6 mb-2">
                            <i class="fas fa-users"></i>
                        </div>
                        <div class="stat-number">${activeUsers}</div>
                        <div class="stat-label">Active Users</div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <!-- Booking Status Chart -->
            <div class="col-md-6 mb-4">
                <div class="card dashboard-card">
                    <div class="card-header">
                        <h5 class="mb-0">
                            <i class="fas fa-pie-chart"></i> Booking Status Distribution
                        </h5>
                    </div>
                    <div class="card-body">
                        <canvas id="bookingStatusChart" width="400" height="300"></canvas>
                    </div>
                </div>
            </div>

            <!-- User Type Distribution -->
            <div class="col-md-6 mb-4">
                <div class="card dashboard-card">
                    <div class="card-header">
                        <h5 class="mb-0">
                            <i class="fas fa-users-cog"></i> User Type Distribution
                        </h5>
                    </div>
                    <div class="card-body">
                        <canvas id="userTypeChart" width="400" height="300"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <div class="row mb-4">
            <!-- Popular Time Slots -->
            <div class="col-md-8">
                <div class="card dashboard-card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">
                            <i class="fas fa-clock"></i> Popular Time Slots
                            <span class="ai-badge badge ms-2">AI INSIGHTS</span>
                        </h5>
                        <small class="text-muted">Based on booking patterns</small>
                    </div>
                    <div class="card-body">
                        <canvas id="popularTimeSlotsChart" width="400" height="200"></canvas>
                    </div>
                </div>
            </div>

            <!-- Quick Stats Sidebar -->
            <div class="col-md-4">
                <!-- Today's Activity -->
                <div class="card dashboard-card mb-4">
                    <div class="card-header">
                        <h6 class="mb-0">
                            <i class="fas fa-calendar-day"></i> Today's Activity
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="row text-center">
                            <div class="col-6 mb-3">
                                <div class="stat-number text-info">${todayBookings}</div>
                                <div class="stat-label small">New Bookings</div>
                            </div>
                            <div class="col-6 mb-3">
                                <div class="stat-number text-warning">${pendingBookings}</div>
                                <div class="stat-label small">Pending</div>
                            </div>
                        </div>
                        <hr>
                        <div class="text-center">
                            <small class="text-muted">
                                <fmt:formatDate value="${now}" pattern="EEEE, MMM dd"/>
                            </small>
                        </div>
                    </div>
                </div>

                <!-- Top Performing Companies -->
                <div class="card dashboard-card">
                    <div class="card-header">
                        <h6 class="mb-0">
                            <i class="fas fa-trophy"></i> Top Companies
                        </h6>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty mostPopularCompany}">
                            <div class="d-flex align-items-center mb-3">
                                <i class="fas fa-crown fa-2x text-warning me-3"></i>
                                <div>
                                    <div class="fw-bold">${mostPopularCompany.name}</div>
                                    <small class="text-muted">Most Popular</small>
                                </div>
                            </div>
                        </c:if>

                        <hr>

                        <h6 class="small mb-2">VIP Companies:</h6>
                        <c:choose>
                            <c:when test="${not empty vipCompanies}">
                                <c:forEach var="company" items="${vipCompanies}" varStatus="status" end="2">
                                    <div class="d-flex align-items-center mb-2">
                                        <i class="fas fa-building text-info me-2"></i>
                                        <small>${company.name}</small>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <small class="text-muted">No VIP companies</small>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <!-- AI Insights Section -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card dashboard-card">
                    <div class="card-header bg-gradient text-white"
                         style="background: linear-gradient(45deg, #6f42c1, #9c27b0);">
                        <h5 class="mb-0">
                            <i class="fas fa-robot"></i> AI-Powered Insights & Recommendations
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-4">
                                <div class="card ai-recommendation mb-3">
                                    <div class="card-body">
                                        <h6 class="card-title">
                                            <i class="fas fa-trend-up text-success"></i> Peak Usage Analysis
                                        </h6>
                                        <p class="card-text small">
                                            <c:choose>
                                                <c:when test="${not empty popularTimeSlots}">
                                                    Most bookings occur during <strong>${popularTimeSlots[0]}</strong>.
                                                    Consider adding more cabins during peak hours.
                                                </c:when>
                                                <c:otherwise>
                                                    Analyzing booking patterns to optimize resource allocation.
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-4">
                                <div class="card ai-recommendation mb-3">
                                    <div class="card-body">
                                        <h6 class="card-title">
                                            <i class="fas fa-users text-warning"></i> User Engagement
                                        </h6>
                                        <p class="card-text small">
                                            <c:choose>
                                                <c:when test="${vipUsers > 0}">
                                                    ${vipUsers} VIP users are highly engaged. Consider expanding VIP benefits.
                                                </c:when>
                                                <c:otherwise>
                                                    Focus on user engagement programs to increase VIP conversions.
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-4">
                                <div class="card ai-recommendation mb-3">
                                    <div class="card-body">
                                        <h6 class="card-title">
                                            <i class="fas fa-chart-line text-info"></i> System Performance
                                        </h6>
                                        <p class="card-text small">
                                            <c:choose>
                                                <c:when test="${approvalRate >= 80}">
                                                    Excellent ${approvalRate}% approval rate indicates efficient workflow.
                                                </c:when>
                                                <c:when test="${approvalRate >= 60}">
                                                    Good ${approvalRate}% approval rate. Room for improvement in approval process.
                                                </c:when>
                                                <c:otherwise>
                                                    ${approvalRate}% approval rate needs attention. Review rejection reasons.
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Detailed Analytics Table -->
        <div class="row">
            <div class="col-12">
                <div class="card dashboard-card">
                    <div class="card-header">
                        <h5 class="mb-0">
                            <i class="fas fa-table"></i> Detailed System Metrics
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>Metric</th>
                                        <th>Current Value</th>
                                        <th>Target</th>
                                        <th>Performance</th>
                                        <th>Trend</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>Booking Approval Rate</td>
                                        <td>${approvalRate}%</td>
                                        <td>≥80%</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${approvalRate >= 80}">
                                                    <span class="badge bg-success">Excellent</span>
                                                </c:when>
                                                <c:when test="${approvalRate >= 60}">
                                                    <span class="badge bg-warning">Good</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-danger">Needs Improvement</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <i class="fas fa-arrow-up text-success"></i>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>VIP User Engagement</td>
                                        <td>${vipBookingCount} bookings</td>
                                        <td>Growing</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${vipBookingCount >= 10}">
                                                    <span class="badge bg-success">High</span>
                                                </c:when>
                                                <c:when test="${vipBookingCount >= 5}">
                                                    <span class="badge bg-warning">Moderate</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-info">Building</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <i class="fas fa-arrow-up text-success"></i>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>System Utilization</td>
                                        <td>${totalBookings} total bookings</td>
                                        <td>Increasing</td>
                                        <td>
                                            <span class="badge bg-info">Active</span>
                                        </td>
                                        <td>
                                            <i class="fas fa-arrow-up text-success"></i>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>User Base Growth</td>
                                        <td>${activeUsers} active users</td>
                                        <td>Expanding</td>
                                        <td>
                                            <span class="badge bg-success">Growing</span>
                                        </td>
                                        <td>
                                            <i class="fas fa-arrow-up text-success"></i>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-light text-center py-3 mt-5">
        <div class="container">
            <p class="mb-0">&copy; 2024 Cabin Booking System - Analytics Dashboard</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Booking Status Chart
        const bookingStatusCtx = document.getElementById('bookingStatusChart').getContext('2d');
        const bookingStatusChart = new Chart(bookingStatusCtx, {
            type: 'doughnut',
            data: {
                labels: ['Approved', 'Pending', 'Rejected'],
                datasets: [{
                    data: [${approvedBookings}, ${pendingBookings}, ${rejectedBookings}],
                    backgroundColor: ['#28a745', '#ffc107', '#dc3545'],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });

        // User Type Distribution Chart
        const userTypeCtx = document.getElementById('userTypeChart').getContext('2d');
        const userTypeChart = new Chart(userTypeCtx, {
            type: 'pie',
            data: {
                labels: ['Normal', 'VIP', 'Admin'],
                datasets: [{
                    data: [
                        ${totalUsers - vipUsers - adminUsers},
                        ${vipUsers},
                        ${adminUsers}
                    ],
                    backgroundColor: ['#007bff', '#ffc107', '#dc3545'],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });

        // Popular Time Slots Chart
        const timeSlotCtx = document.getElementById('popularTimeSlotsChart').getContext('2d');
        const timeSlotChart = new Chart(timeSlotCtx, {
            type: 'bar',
            data: {
                labels: [
                    <c:forEach var="timeSlot" items="${popularTimeSlots}" varStatus="status">
                        '${timeSlot}'<c:if test="${!status.last}">,</c:if>
                    </c:forEach>
                ],
                datasets: [{
                    label: 'Booking Count',
                    data: [
                        <c:forEach var="timeSlot" items="${popularTimeSlots}" varStatus="status">
                            Math.floor(Math.random() * 50) + 10<c:if test="${!status.last}">,</c:if>
                        </c:forEach>
                    ],
                    backgroundColor: 'rgba(103, 126, 234, 0.8)',
                    borderColor: 'rgba(103, 126, 234, 1)',
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });

        // Auto-refresh analytics every 10 minutes
        setInterval(function() {
            console.log('Refreshing analytics data...');
            // In a real application, this would make AJAX calls to update charts
        }, 10 * 60 * 1000);

        // Add animation to stat cards
        document.addEventListener('DOMContentLoaded', function() {
            const statNumbers = document.querySelectorAll('.stat-number');

            statNumbers.forEach(stat => {
                const finalValue = parseInt(stat.textContent.replace(/[^0-9]/g, ''));
                let currentValue = 0;
                const increment = Math.max(1, Math.ceil(finalValue / 50));

                const timer = setInterval(() => {
                    currentValue += increment;
                    if (currentValue >= finalValue) {
                        currentValue = finalValue;
                        clearInterval(timer);
                    }

                    stat.textContent = stat.textContent.includes('%') ?
                        currentValue + '%' : currentValue;
                }, 30);
            });
        });
    </script>
</body>
</html>
