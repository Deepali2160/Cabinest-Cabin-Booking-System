<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Cabin - Yash Technology</title>
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/booking.css" rel="stylesheet">
</head>
<body>
    <!-- ✅ KEEP THIS: Set context path for JavaScript -->
    <script>
        window.contextPath = '${pageContext.request.contextPath}';
        console.log('🌐 Context path set to:', window.contextPath);
    </script>

    <!-- Header Navigation -->
    <nav class="booking-nav" data-context-path="${pageContext.request.contextPath}">
        <div class="nav-container">
            <div class="nav-brand">
                <h2>🏢 Yash Technology</h2>
                <span>Cabin Booking System</span>
            </div>

            <div class="nav-actions">
                <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">
                    ← Back to Dashboard
                </a>
                <div class="user-info">
                    <span>${user.name}</span>
                    <c:if test="${user.userType == 'VIP'}">
                        <span class="vip-badge">⭐ VIP</span>
                    </c:if>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <main class="booking-main">

        <!-- Header Section -->
        <section class="booking-header">
            <div class="header-content">
                <h1>📅 Book a Cabin</h1>
                <p>Reserve your meeting space at Yash Technology - Indore</p>
                <c:if test="${user.userType == 'VIP'}">
                    <div class="vip-notice">
                        ⭐ VIP Priority: Your booking will receive high priority approval
                    </div>
                </c:if>
            </div>
        </section>

        <!-- Messages -->
        <div id="message-container">
            <c:if test="${not empty error}">
                <div class="message error-message">
                    ❌ ${error}
                </div>
            </c:if>

            <!-- Alternative Suggestions -->
            <c:if test="${not empty alternativeSlots}">
                <div class="message info-message">
                    <h4>💡 Alternative Time Slots Available:</h4>
                    <div class="alternatives-list">
                        <c:forEach var="altSlot" items="${alternativeSlots}">
                            <button class="alternative-btn" data-time="${altSlot}">
                                ${altSlot}
                            </button>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
        </div>

        <!-- Booking Form -->
        <div class="booking-container">
            <div class="booking-form-section">
                <div class="form-card">
                    <div class="form-header">
                        <h2>📋 Booking Details</h2>
                        <p>Fill in your booking information</p>
                    </div>

                    <form id="bookingForm" action="${pageContext.request.contextPath}/book" method="post" class="booking-form">

                        <!-- Booking Type Selection -->
                        <div class="form-section">
                            <h3>📅 Select Booking Type</h3>
                            <div class="booking-type-grid">
                                <div class="booking-type-card" data-type="SINGLE_DAY">
                                    <div class="type-icon">📅</div>
                                    <h4>Single Day</h4>
                                    <p>Book for specific hours on one day</p>
                                    <ul class="type-features">
                                        <li>15 minutes to 8+ hours</li>
                                        <li>Perfect for meetings</li>
                                        <li>15-minute precision</li>
                                    </ul>
                                </div>

                                <div class="booking-type-card" data-type="MULTI_DAY">
                                    <div class="type-icon">📆</div>
                                    <h4>Multiple Days</h4>
                                    <p>Book for consecutive days</p>
                                    <ul class="type-features">
                                        <li>2-30 consecutive days</li>
                                        <li>Full day access</li>
                                        <li>Great for workshops</li>
                                    </ul>
                                </div>
                            </div>
                            <input type="hidden" id="bookingType" name="bookingType" required>
                        </div>

                        <!-- Cabin Selection -->
                        <div class="form-section">
                            <label for="cabinId" class="form-label">
                                🏠 Select Cabin <span class="required">*</span>
                            </label>
                            <select id="cabinId" name="cabinId" class="form-select" required>
                                <option value="">Choose a cabin...</option>
                                <c:forEach var="cabin" items="${cabins}">
                                    <option value="${cabin.cabinId}"
                                            data-capacity="${cabin.capacity}"
                                            data-location="${cabin.location}"
                                            data-vip="${cabin.vipOnly}"
                                            ${selectedCabin != null && selectedCabin.cabinId == cabin.cabinId ? 'selected' : ''}>
                                        ${cabin.name} (${cabin.capacity} people) - ${cabin.location}
                                        <c:if test="${cabin.vipOnly}"> - VIP Only</c:if>
                                    </option>
                                </c:forEach>
                            </select>
                            <div class="cabin-details" id="cabinDetails"></div>
                        </div>

                        <!-- Single Day Section -->
                        <div id="singleDaySection" class="booking-section" style="display: none;">
                            <h3>📅 Single Day Booking</h3>

                            <!-- Date Selection -->
                            <div class="form-group">
                                <label for="bookingDate" class="form-label">
                                    📅 Booking Date <span class="required">*</span>
                                </label>
                                <input type="date" id="bookingDate" name="bookingDate"
                                       class="form-input" min="${todayDate}">
                            </div>

                            <!-- Duration Selection -->
                            <div class="form-group">
                                <label class="form-label">⏰ Duration <span class="required">*</span></label>
                                <div class="duration-grid">
                                    <c:forEach var="duration" items="${availableDurations}">
                                        <div class="duration-card" data-duration="${duration}">
                                            <div class="duration-time">
                                                <c:choose>
                                                    <c:when test="${duration < 60}">
                                                        ${duration} min
                                                    </c:when>
                                                    <c:when test="${duration == 60}">
                                                        1 hour
                                                    </c:when>
                                                    <c:when test="${duration == 90}">
                                                        1.5 hours
                                                    </c:when>
                                                    <c:when test="${duration == 120}">
                                                        2 hours
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${duration / 60} hours
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="duration-type">
                                                <c:choose>
                                                    <c:when test="${duration <= 30}">Quick</c:when>
                                                    <c:when test="${duration <= 60}">Standard</c:when>
                                                    <c:when test="${duration <= 120}">Extended</c:when>
                                                    <c:otherwise>Workshop</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                                <input type="hidden" id="duration" name="duration">
                            </div>

                            <!-- Start Time Selection -->
                            <div class="form-group" id="startTimeGroup" style="display: none;">
                                <label for="startTime" class="form-label">
                                    🕐 Start Time <span class="required">*</span>
                                </label>
                                <select id="startTime" name="startTime" class="form-select">
                                    <option value="">Choose start time...</option>
                                    <c:forEach var="time" items="${startTimeOptions}">
                                        <option value="${time}">${time}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <!-- Time Slot Preview -->
                            <div id="timeSlotPreview" class="time-preview" style="display: none;">
                                <div class="preview-content">
                                    <h4>⏰ Selected Time Slot</h4>
                                    <div class="time-display" id="selectedTimeSlot"></div>
                                </div>
                            </div>
                        </div>

                        <!-- Multi-Day Section -->
                        <div id="multiDaySection" class="booking-section multi-day-section" style="display: none;">
                            <h3>📆 Multi-Day Booking</h3>

                            <div class="date-range-container">
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="startDate" class="form-label">
                                            📅 Start Date <span class="required">*</span>
                                        </label>
                                        <input type="date" id="startDate" name="startDate"
                                               class="form-input" min="${todayDate}">
                                    </div>

                                    <div class="form-group">
                                        <label for="endDate" class="form-label">
                                            📅 End Date <span class="required">*</span>
                                        </label>
                                        <input type="date" id="endDate" name="endDate"
                                               class="form-input" min="${todayDate}">
                                    </div>
                                </div>

                                <!-- Date Range Preview -->
                                <div id="dateRangePreview" class="date-preview" style="display: none;">
                                    <div class="preview-content">
                                        <div class="day-count">
                                            <span class="count-number" id="dayCount">0</span>
                                            <span class="count-label">Days</span>
                                        </div>
                                        <div class="range-details">
                                            <div id="dateRangeText" class="range-text"></div>
                                            <div class="range-info">Full-day access (9:00 AM - 6:00 PM)</div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="multi-day-info">
                                <h4>📋 Multi-Day Booking Information</h4>
                                <ul>
                                    <li>Full-day access (9:00 AM - 6:00 PM) for all days</li>
                                    <li>Maximum 30 consecutive days per booking</li>
                                    <li>Weekend days included in consecutive bookings</li>
                                    <li>Higher approval priority for extended bookings</li>
                                </ul>
                            </div>
                        </div>

                        <!-- Purpose -->
                        <div class="form-section">
                            <label for="purpose" class="form-label">
                                💬 Purpose of Booking <span class="required">*</span>
                            </label>
                            <textarea id="purpose" name="purpose" class="form-textarea"
                                      rows="4" required maxlength="500"
                                      placeholder="Enter the purpose of your booking..."></textarea>

                            <div class="purpose-suggestions">
                                <span class="suggestions-label">💡 Quick suggestions:</span>
                                <c:forEach var="suggestion" items="${suggestedPurposes}">
                                    <button type="button" class="purpose-btn" data-purpose="${suggestion}">
                                        ${suggestion}
                                    </button>
                                </c:forEach>

                                <!-- Multi-day specific suggestions (hidden by default) -->
                                <button type="button" class="purpose-btn multi-day-purpose"
                                        data-purpose="Training Workshop" style="display: none;">
                                    Training Workshop
                                </button>
                                <button type="button" class="purpose-btn multi-day-purpose"
                                        data-purpose="Team Retreat" style="display: none;">
                                    Team Retreat
                                </button>
                                <button type="button" class="purpose-btn multi-day-purpose"
                                        data-purpose="Product Launch" style="display: none;">
                                    Product Launch
                                </button>
                            </div>
                        </div>

                        <!-- Availability Status -->
                        <div id="availabilityStatus" class="availability-status" style="display: none;"></div>

                        <!-- Hidden Fields -->
                        <input type="hidden" id="timeSlot" name="timeSlot">

                        <!-- Submit Section -->
                        <div class="submit-section">
                            <div class="form-actions">
                                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">
                                    ✖️ Cancel
                                </a>
                                <button type="submit" id="submitBtn" class="btn btn-primary" disabled>
                                    <span id="submitText">📅 Book Cabin</span>
                                </button>
                            </div>
                            <div class="submit-info">
                                🔒 Your booking will be submitted for approval and you'll receive confirmation
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Sidebar -->
            <aside class="booking-sidebar">

                <!-- Recommended Cabins -->
                <c:if test="${not empty recommendedCabins}">
                    <div class="sidebar-card">
                        <h3>💡 Recommended for You</h3>
                        <div class="recommended-list">
                            <c:forEach var="cabin" items="${recommendedCabins}" varStatus="status">
                                <c:if test="${status.index < 3}">
                                    <div class="recommended-item">
                                        <h4>${cabin.name}</h4>
                                        <p>👥 ${cabin.capacity} people | 📍 ${cabin.location}</p>
                                        <c:if test="${cabin.vipOnly}">
                                            <span class="vip-badge">⭐ VIP Only</span>
                                        </c:if>
                                        <button class="select-cabin-btn"
                                                data-cabin-id="${cabin.cabinId}">
                                            Select This Cabin
                                        </button>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>

                <!-- Popular Time Slots -->
                <div class="sidebar-card">
                    <h3>🕐 Popular Times</h3>
                    <div class="popular-times">
                        <c:forEach var="timeSlot" items="${popularTimeSlots}" varStatus="status">
                            <c:if test="${status.index < 5}">
                                <button class="popular-time-btn" data-time="${timeSlot}">
                                    ${timeSlot}
                                </button>
                            </c:if>
                        </c:forEach>
                    </div>
                    <div class="popular-info">
                        💡 Most frequently booked time slots
                    </div>
                </div>

                <!-- Booking Tips -->
                <div class="sidebar-card">
                    <h3>📝 Booking Tips</h3>
                    <div id="bookingTips" class="tips-list">
                        <div class="tip-item">📋 Select your booking type first</div>
                        <div class="tip-item">🏠 Choose from available cabins</div>
                        <div class="tip-item">⏰ Pick your preferred time</div>
                        <div class="tip-item">✅ Check availability before submitting</div>
                    </div>
                </div>
            </aside>
        </div>
    </main>

    <!-- Footer -->
    <footer class="booking-footer">
        <p>&copy; 2024 Yash Technology - Cabin Booking System</p>
    </footer>

    <!-- ✅ REMOVED: Temporary script section that was causing conflicts -->
    <script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/booking.js"></script>
</body>
</html>
