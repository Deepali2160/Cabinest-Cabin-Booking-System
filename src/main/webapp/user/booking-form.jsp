<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Cabin - Flexible Duration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
    <style>
        .duration-option {
            cursor: pointer;
            transition: all 0.3s ease;
            border: 2px solid #dee2e6;
        }
        .duration-option:hover {
            background-color: #f8f9fa;
            border-color: #007bff;
        }
        .duration-option.selected {
            background-color: #007bff;
            color: white;
            border-color: #007bff;
        }
        /* ✅ NEW: Booking Type Styles */
        .booking-type-option {
            cursor: pointer;
            transition: all 0.3s ease;
            border: 2px solid #dee2e6;
            background: linear-gradient(135deg, #f8f9fa, #e9ecef);
        }
        .booking-type-option:hover {
            border-color: #28a745;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .booking-type-option.selected {
            background: linear-gradient(135deg, #28a745, #20c997);
            color: white;
            border-color: #28a745;
            box-shadow: 0 6px 12px rgba(40,167,69,0.3);
        }
        /* ✅ NEW: Multi-day specific styles */
        .multi-day-section {
            background: linear-gradient(135deg, #e3f2fd, #f3e5f5);
            border-left: 4px solid #2196f3;
            padding: 20px;
            border-radius: 8px;
            margin: 15px 0;
        }
        .date-range-picker {
            background: white;
            border-radius: 6px;
            padding: 15px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .time-slot-preview {
            background-color: #e9ecef;
            padding: 15px;
            border-radius: 8px;
            margin-top: 15px;
            border-left: 4px solid #007bff;
        }
        .availability-status {
            margin-top: 15px;
            padding: 15px;
            border-radius: 8px;
            border-left: 4px solid;
        }
        .available {
            background-color: #d4edda;
            color: #155724;
            border-left-color: #28a745;
        }
        .unavailable {
            background-color: #f8d7da;
            color: #721c24;
            border-left-color: #dc3545;
        }
        .duration-label {
            font-weight: 600;
            color: #495057;
        }
        .alternative-btn {
            margin: 2px;
        }
        .card-header-gradient {
            background: linear-gradient(135deg, #007bff, #0056b3);
            color: white;
        }
        /* ✅ NEW: Multi-day calendar preview */
        .multi-day-preview {
            background: linear-gradient(135deg, #fff3e0, #fce4ec);
            border: 2px dashed #ff9800;
            border-radius: 10px;
            padding: 15px;
            text-align: center;
            margin-top: 15px;
        }
        .day-counter {
            font-size: 2rem;
            font-weight: bold;
            color: #ff5722;
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
                <i class="fas fa-building"></i> Cabin Booking System
            </a>

            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                    <i class="fas fa-arrow-left"></i> Back to Dashboard
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                    <i class="fas fa-sign-out-alt"></i> Logout
                </a>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container mt-4">

        <!-- Page Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card dashboard-card">
                    <div class="card-body card-header-gradient">
                        <h2 class="mb-2 text-white">
                            <i class="fas fa-calendar-plus"></i> Book a Cabin - Single Day or Multi-Day System
                        </h2>
                        <p class="mb-0" style="color: #e3f2fd;">
                            <i class="fas fa-building"></i> ${company.name} - ${company.location}
                            <c:if test="${user.userType == 'VIP'}">
                                | <span class="badge badge-warning">VIP Priority Booking</span>
                            </c:if>
                            | <i class="fas fa-clock"></i> Choose single day (15 min - 8+ hours) or multiple consecutive days
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Error/Success Messages -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Alternative Suggestions -->
        <c:if test="${not empty alternativeSlots || not empty alternativeCabins}">
            <div class="alert alert-ai" role="alert" style="background-color: #e7f3ff; border-color: #b8daff;">
                <h5 class="alert-heading">
                    <i class="fas fa-robot"></i> AI Alternative Suggestions
                </h5>

                <c:if test="${not empty alternativeSlots}">
                    <p><strong>Alternative Time Slots for ${requestedCabin.name}:</strong></p>
                    <div class="mb-3">
                        <c:forEach var="altSlot" items="${alternativeSlots}">
                            <button class="btn btn-outline-primary btn-sm alternative-btn select-alternative-slot"
                                    data-time="${altSlot}">
                                ${altSlot}
                            </button>
                        </c:forEach>
                    </div>
                </c:if>

                <c:if test="${not empty alternativeCabins}">
                    <p><strong>Alternative Cabins for ${requestedTimeSlot}:</strong></p>
                    <div class="row">
                        <c:forEach var="altCabin" items="${alternativeCabins}">
                            <div class="col-md-4 mb-2">
                                <div class="card border-primary">
                                    <div class="card-body p-2">
                                        <h6 class="card-title mb-1">${altCabin.name}</h6>
                                        <p class="card-text small mb-1">
                                            <i class="fas fa-users"></i> ${altCabin.capacity} people
                                        </p>
                                        <button class="btn btn-sm btn-primary select-cabin"
                                                data-cabin-id="${altCabin.cabinId}"
                                                data-cabin-name="${altCabin.name}">
                                            Select This Cabin
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
        </c:if>

        <div class="row">
            <!-- Booking Form -->
            <div class="col-md-8">
                <div class="card dashboard-card shadow">
                    <div class="card-header card-header-gradient">
                        <h5 class="mb-0 text-white">
                            <i class="fas fa-form"></i> Flexible Booking System - Single Day or Multi-Day
                        </h5>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/book" method="post" id="bookingForm">

                            <!-- ✅ NEW: Booking Type Selection -->
                            <div class="mb-4">
                                <label class="form-label duration-label">
                                    <i class="fas fa-calendar-alt me-1"></i>Select Booking Type <span class="text-danger">*</span>
                                </label>
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <div class="booking-type-option border rounded p-4 text-center h-100" data-type="SINGLE_DAY">
                                            <div class="mb-3">
                                                <i class="fas fa-calendar-day fa-3x text-primary"></i>
                                            </div>
                                            <h5 class="fw-bold mb-2">Single Day</h5>
                                            <p class="mb-2 small">Book cabin for specific hours on one day</p>
                                            <ul class="list-unstyled small text-muted">
                                                <li><i class="fas fa-check text-success me-1"></i>15 minutes to 8+ hours</li>
                                                <li><i class="fas fa-check text-success me-1"></i>15-minute precision</li>
                                                <li><i class="fas fa-check text-success me-1"></i>Perfect for meetings</li>
                                            </ul>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="booking-type-option border rounded p-4 text-center h-100" data-type="MULTI_DAY">
                                            <div class="mb-3">
                                                <i class="fas fa-calendar-week fa-3x text-success"></i>
                                            </div>
                                            <h5 class="fw-bold mb-2">Multiple Days</h5>
                                            <p class="mb-2 small">Book cabin for consecutive days</p>
                                            <ul class="list-unstyled small text-muted">
                                                <li><i class="fas fa-check text-success me-1"></i>2-30 consecutive days</li>
                                                <li><i class="fas fa-check text-success me-1"></i>Full day bookings</li>
                                                <li><i class="fas fa-check text-success me-1"></i>Great for workshops</li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                                <input type="hidden" id="selectedBookingType" name="bookingType" required>
                                <div class="form-text">
                                    <i class="fas fa-info-circle me-1"></i>
                                    Choose single day for meetings/presentations or multi-day for workshops/training programs
                                </div>
                            </div>

                            <!-- Cabin Selection -->
                            <div class="mb-4">
                                <label for="cabinId" class="form-label duration-label">
                                    <i class="fas fa-building me-1"></i>Select Cabin <span class="text-danger">*</span>
                                </label>
                                <select class="form-select" id="cabinId" name="cabinId" required>
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
                                <div class="form-text">
                                    <span id="cabinDetails" class="text-muted"></span>
                                </div>
                            </div>

                            <!-- ✅ SINGLE DAY SECTION -->
                            <div id="singleDaySection" style="display: none;">

                                <!-- Date Selection for Single Day -->
                                <div class="mb-4">
                                    <label for="bookingDate" class="form-label duration-label">
                                        <i class="fas fa-calendar me-1"></i>Booking Date <span class="text-danger">*</span>
                                    </label>
                                    <input type="date" class="form-control" id="bookingDate" name="bookingDate"
                                           min="${todayDate}">
                                    <div class="form-text">Select a date (today or future dates only)</div>
                                </div>

                                <!-- Duration Selection -->
                                <div class="mb-4">
                                    <label class="form-label duration-label">
                                        <i class="fas fa-clock me-1"></i>Select Duration <span class="text-danger">*</span>
                                    </label>
                                    <div class="row g-3">
                                        <c:forEach var="duration" items="${availableDurations}">
                                            <div class="col-md-3 col-sm-6">
                                                <div class="duration-option border rounded p-3 text-center h-100"
                                                     data-duration="${duration}">
                                                    <div class="mb-2">
                                                        <i class="fas fa-clock fa-2x text-muted"></i>
                                                    </div>
                                                    <div class="fw-bold">
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
                                                            <c:when test="${duration == 180}">
                                                                3 hours
                                                            </c:when>
                                                            <c:when test="${duration == 240}">
                                                                4 hours
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${duration / 60} hours
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <small class="text-muted">
                                                        <c:choose>
                                                            <c:when test="${duration <= 30}">Quick meeting</c:when>
                                                            <c:when test="${duration <= 60}">Standard meeting</c:when>
                                                            <c:when test="${duration <= 120}">Extended session</c:when>
                                                            <c:otherwise>Workshop/Training</c:otherwise>
                                                        </c:choose>
                                                    </small>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <input type="hidden" id="selectedDuration" name="duration">
                                    <div class="form-text">
                                        <i class="fas fa-info-circle me-1"></i>
                                        Choose your booking duration - from quick 15-minute meetings to 4+ hour workshops
                                    </div>
                                </div>

                                <!-- Start Time Selection -->
                                <div class="mb-4" id="startTimeSection" style="display: none;">
                                    <label for="startTime" class="form-label duration-label">
                                        <i class="fas fa-clock me-1"></i>Select Start Time <span class="text-danger">*</span>
                                    </label>
                                    <select class="form-select" id="startTime" name="startTime">
                                        <option value="">Choose start time...</option>
                                        <c:forEach var="time" items="${startTimeOptions}">
                                            <option value="${time}">${time}</option>
                                        </c:forEach>
                                    </select>
                                    <div class="form-text">
                                        <i class="fas fa-info-circle me-1"></i>
                                        Available times are shown in 15-minute intervals from 9:00 AM to 6:00 PM
                                    </div>
                                </div>

                                <!-- Time Slot Preview -->
                                <div id="timeSlotPreview" class="time-slot-preview" style="display: none;">
                                    <div class="d-flex align-items-center">
                                        <i class="fas fa-clock me-2 text-primary"></i>
                                        <div>
                                            <strong>Selected Time Slot:</strong>
                                            <span id="previewTimeSlot" class="text-primary fs-5"></span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- ✅ NEW: MULTI-DAY SECTION -->
                            <div id="multiDaySection" class="multi-day-section" style="display: none;">
                                <div class="text-center mb-4">
                                    <h5 class="text-primary">
                                        <i class="fas fa-calendar-week me-2"></i>Multi-Day Booking Configuration
                                    </h5>
                                    <p class="text-muted">Book the cabin for consecutive days with full-day access</p>
                                </div>

                                <div class="date-range-picker">
                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label for="startDate" class="form-label duration-label">
                                                <i class="fas fa-calendar-plus me-1"></i>Start Date <span class="text-danger">*</span>
                                            </label>
                                            <input type="date" class="form-control" id="startDate" name="startDate" min="${todayDate}">
                                            <div class="form-text">First day of your booking</div>
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label for="endDate" class="form-label duration-label">
                                                <i class="fas fa-calendar-minus me-1"></i>End Date <span class="text-danger">*</span>
                                            </label>
                                            <input type="date" class="form-control" id="endDate" name="endDate" min="${todayDate}">
                                            <div class="form-text">Last day of your booking</div>
                                        </div>
                                    </div>

                                    <!-- Multi-day Preview -->
                                    <div id="multiDayPreview" class="multi-day-preview" style="display: none;">
                                        <div class="row align-items-center">
                                            <div class="col-md-4">
                                                <div class="day-counter" id="dayCount">0</div>
                                                <small class="text-muted">Total Days</small>
                                            </div>
                                            <div class="col-md-8 text-start">
                                                <div id="dateRangeDisplay" class="fw-bold text-primary mb-2"></div>
                                                <div id="multiDayDetails" class="small text-muted"></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="alert alert-info mt-3">
                                    <i class="fas fa-info-circle me-2"></i>
                                    <strong>Multi-day Booking Info:</strong>
                                    <ul class="mb-0 mt-2">
                                        <li>Full-day access (9:00 AM - 6:00 PM) for all selected days</li>
                                        <li>Maximum 30 consecutive days per booking</li>
                                        <li>Weekend days included in consecutive bookings</li>
                                        <li>Higher approval priority for extended bookings</li>
                                    </ul>
                                </div>
                            </div>

                            <!-- Availability Status -->
                            <div id="availabilityStatus" style="display: none;"></div>

                            <!-- Hidden fields -->
                            <input type="hidden" id="timeSlot" name="timeSlot">

                            <!-- Purpose -->
                            <div class="mb-4">
                                <label for="purpose" class="form-label duration-label">
                                    <i class="fas fa-comment me-1"></i>Purpose of Booking <span class="text-danger">*</span>
                                </label>
                                <textarea class="form-control" id="purpose" name="purpose" rows="3"
                                          placeholder="Enter the purpose of your booking..."
                                          required maxlength="500"></textarea>
                                <div class="form-text">
                                    <i class="fas fa-lightbulb me-1"></i>
                                    AI Suggestions:
                                    <c:forEach var="suggestion" items="${suggestedPurposes}" varStatus="status">
                                        <span class="badge bg-secondary me-1 purpose-suggestion" style="cursor: pointer;">${suggestion}</span>
                                    </c:forEach>
                                    <!-- Multi-day specific suggestions -->
                                    <span class="badge bg-info me-1 purpose-suggestion multi-day-purpose" style="cursor: pointer; display: none;">Training Workshop</span>
                                    <span class="badge bg-info me-1 purpose-suggestion multi-day-purpose" style="cursor: pointer; display: none;">Conference</span>
                                    <span class="badge bg-info me-1 purpose-suggestion multi-day-purpose" style="cursor: pointer; display: none;">Team Retreat</span>
                                    <span class="badge bg-info me-1 purpose-suggestion multi-day-purpose" style="cursor: pointer; display: none;">Product Launch</span>
                                </div>
                            </div>

                            <!-- VIP Priority Info -->
                            <c:if test="${user.userType == 'VIP'}">
                                <div class="alert alert-info" role="alert">
                                    <i class="fas fa-star"></i> <strong>VIP Priority:</strong>
                                    Your booking will receive high priority and faster approval.
                                </div>
                            </c:if>

                            <!-- Submit Buttons -->
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="${pageContext.request.contextPath}/dashboard"
                                   class="btn btn-secondary me-md-2">
                                    <i class="fas fa-times"></i> Cancel
                                </a>
                                <button type="submit" class="btn btn-primary btn-lg px-4" id="submitBtn" disabled>
                                    <i class="fas fa-calendar-check"></i> <span id="submitBtnText">Book This Cabin</span>
                                </button>
                            </div>

                            <div class="text-center mt-2">
                                <small class="text-muted">
                                    <i class="fas fa-shield-alt me-1"></i>
                                    Your booking will be submitted for approval and you'll receive confirmation
                                </small>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- AI Recommendations Sidebar -->
            <div class="col-md-4">

                <!-- AI Recommendations -->
                <c:if test="${not empty recommendedCabins}">
                    <div class="card dashboard-card mb-4 shadow">
                        <div class="card-header text-white"
                             style="background: linear-gradient(45deg, #6f42c1, #9c27b0);">
                            <h6 class="mb-0">
                                <i class="fas fa-robot"></i> AI Recommendations
                            </h6>
                        </div>
                        <div class="card-body">
                            <p class="small text-muted mb-3">Based on your preferences:</p>
                            <c:forEach var="cabin" items="${recommendedCabins}" varStatus="status">
                                <c:if test="${status.index < 3}">
                                    <div class="card border-primary mb-2">
                                        <div class="card-body p-2">
                                            <h6 class="card-title mb-1">${cabin.name}</h6>
                                            <p class="card-text small mb-1">
                                                <i class="fas fa-users"></i> ${cabin.capacity} people<br>
                                                <i class="fas fa-map-marker-alt"></i> ${cabin.location}
                                            </p>
                                            <button class="btn btn-sm btn-outline-primary select-cabin"
                                                    data-cabin-id="${cabin.cabinId}"
                                                    data-cabin-name="${cabin.name}">
                                                <i class="fas fa-magic"></i> Select
                                            </button>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>

                <!-- Popular Time Slots -->
                <div class="card dashboard-card mb-4 shadow">
                    <div class="card-header">
                        <h6 class="mb-0">
                            <i class="fas fa-clock"></i> Popular Times
                            <span class="ai-badge badge bg-info ms-1">AI</span>
                        </h6>
                    </div>
                    <div class="card-body">
                        <c:forEach var="timeSlot" items="${popularTimeSlots}" varStatus="status">
                            <c:if test="${status.index < 4}">
                                <button class="btn btn-outline-info btn-sm mb-1 me-1 select-popular-slot"
                                        data-time="${timeSlot}">
                                    ${timeSlot}
                                </button>
                            </c:if>
                        </c:forEach>
                        <div class="text-center mt-2">
                            <small class="text-muted">
                                <i class="fas fa-lightbulb"></i> Most booked times
                            </small>
                        </div>
                    </div>
                </div>

                <!-- Booking Tips -->
                <div class="card dashboard-card shadow">
                    <div class="card-header">
                        <h6 class="mb-0">
                            <i class="fas fa-tips"></i> Booking Tips
                        </h6>
                    </div>
                    <div class="card-body">
                        <ul class="list-unstyled small mb-0" id="bookingTips">
                            <!-- Tips will be dynamically updated based on booking type -->
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-light text-center py-3 mt-5">
        <div class="container">
            <p class="mb-0">&copy; 2024 Cabin Booking System with Single Day & Multi-Day Options</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // ✅ ENHANCED: Single Day + Multi-Day Booking System

        let selectedBookingType = '';
        let selectedDuration = 0;
        let selectedStartTime = '';
        let availabilityCheckTimeout;

        // ✅ NEW: Booking Type Selection
        document.querySelectorAll('.booking-type-option').forEach(option => {
            option.addEventListener('click', function() {
                // Remove selected class from all options
                document.querySelectorAll('.booking-type-option').forEach(opt => opt.classList.remove('selected'));

                // Add selected class to clicked option
                this.classList.add('selected');

                selectedBookingType = this.dataset.type;
                document.getElementById('selectedBookingType').value = selectedBookingType;

                console.log('Booking type selected:', selectedBookingType);

                // Show appropriate section
                if (selectedBookingType === 'SINGLE_DAY') {
                    showSingleDaySection();
                } else if (selectedBookingType === 'MULTI_DAY') {
                    showMultiDaySection();
                }

                updateBookingTips();
                resetAvailabilityStatus();
            });
        });

        // Show single day booking section
        function showSingleDaySection() {
            document.getElementById('singleDaySection').style.display = 'block';
            document.getElementById('multiDaySection').style.display = 'none';

            // Set required fields
            document.getElementById('bookingDate').required = true;
            document.getElementById('startDate').required = false;
            document.getElementById('endDate').required = false;

            // Update submit button
            document.getElementById('submitBtnText').textContent = 'Book This Time Slot';

            console.log('Single day section shown');
        }

        // Show multi-day booking section
        function showMultiDaySection() {
            document.getElementById('singleDaySection').style.display = 'none';
            document.getElementById('multiDaySection').style.display = 'block';

            // Set required fields
            document.getElementById('bookingDate').required = false;
            document.getElementById('startDate').required = true;
            document.getElementById('endDate').required = true;

            // Update submit button
            document.getElementById('submitBtnText').textContent = 'Book Multi-Day Cabin';

            // Show multi-day purpose suggestions
            document.querySelectorAll('.multi-day-purpose').forEach(el => el.style.display = 'inline-block');

            console.log('Multi-day section shown');
        }

        // ✅ SINGLE DAY: Duration selection
        document.querySelectorAll('.duration-option').forEach(option => {
            option.addEventListener('click', function() {
                if (selectedBookingType !== 'SINGLE_DAY') return;

                // Remove selected class from all options
                document.querySelectorAll('.duration-option').forEach(opt => opt.classList.remove('selected'));

                // Add selected class to clicked option
                this.classList.add('selected');

                selectedDuration = parseInt(this.dataset.duration);
                document.getElementById('selectedDuration').value = selectedDuration;

                // Show start time selection
                document.getElementById('startTimeSection').style.display = 'block';

                console.log('Duration selected:', selectedDuration, 'minutes');

                // Reset availability status
                resetAvailabilityStatus();

                // Update preview if start time is already selected
                if (selectedStartTime) {
                    updateTimeSlotPreview();
                    scheduleAvailabilityCheck();
                }
            });
        });

        // ✅ SINGLE DAY: Start time selection
        document.getElementById('startTime').addEventListener('change', function() {
            selectedStartTime = this.value;

            if (selectedStartTime && selectedDuration > 0) {
                updateTimeSlotPreview();
                scheduleAvailabilityCheck();
            }
        });

        // ✅ MULTI-DAY: Date range selection
        document.getElementById('startDate').addEventListener('change', function() {
            updateMultiDayPreview();
            updateEndDateMin();
            scheduleMultiDayAvailabilityCheck();
        });

        document.getElementById('endDate').addEventListener('change', function() {
            updateMultiDayPreview();
            scheduleMultiDayAvailabilityCheck();
        });

        // Update multi-day preview
        function updateMultiDayPreview() {
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;
            const previewDiv = document.getElementById('multiDayPreview');

            if (startDate && endDate) {
                const start = new Date(startDate);
                const end = new Date(endDate);

                if (end >= start) {
                    const diffTime = Math.abs(end - start);
                    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;

                    if (diffDays <= 30) {
                        document.getElementById('dayCount').textContent = diffDays;
                        document.getElementById('dateRangeDisplay').textContent =
                            formatDate(startDate) + ' → ' + formatDate(endDate);
                        document.getElementById('multiDayDetails').textContent =
                            `Full-day access (9:00 AM - 6:00 PM) for ${diffDays} consecutive day${diffDays > 1 ? 's' : ''}`;

                        previewDiv.style.display = 'block';

                        // Set time slot for multi-day (full day)
                        document.getElementById('timeSlot').value = '09:00-18:00';

                        return true;
                    } else {
                        showMultiDayError('Maximum 30 consecutive days allowed per booking');
                        return false;
                    }
                } else {
                    showMultiDayError('End date must be same or after start date');
                    return false;
                }
            } else {
                previewDiv.style.display = 'none';
                return false;
            }
        }

        // Update minimum end date
        function updateEndDateMin() {
            const startDate = document.getElementById('startDate').value;
            if (startDate) {
                document.getElementById('endDate').min = startDate;
            }
        }

        // Show multi-day error
        function showMultiDayError(message) {
            const previewDiv = document.getElementById('multiDayPreview');
            previewDiv.style.display = 'block';
            previewDiv.className = 'multi-day-preview border-danger';
            previewDiv.innerHTML = `
                <div class="text-danger">
                    <i class="fas fa-exclamation-triangle fa-2x mb-2"></i>
                    <div><strong>Error:</strong> ${message}</div>
                </div>
            `;
        }

        // Format date for display
        function formatDate(dateStr) {
            const date = new Date(dateStr);
            return date.toLocaleDateString('en-US', {
                weekday: 'short',
                month: 'short',
                day: 'numeric'
            });
        }

        // Purpose suggestions
        document.querySelectorAll('.purpose-suggestion').forEach(suggestion => {
            suggestion.addEventListener('click', function() {
                document.getElementById('purpose').value = this.textContent;
            });
        });

        // Update time slot preview (single day)
        function updateTimeSlotPreview() {
            if (selectedStartTime && selectedDuration > 0) {
                const endTime = calculateEndTime(selectedStartTime, selectedDuration);
                const timeSlot = selectedStartTime + '-' + endTime;

                document.getElementById('previewTimeSlot').textContent = timeSlot;
                document.getElementById('timeSlotPreview').style.display = 'block';
                document.getElementById('timeSlot').value = timeSlot;
            }
        }

        // Calculate end time based on start time and duration
        function calculateEndTime(startTime, durationMinutes) {
            const [hours, minutes] = startTime.split(':').map(Number);
            const totalMinutes = hours * 60 + minutes + durationMinutes;
            const endHours = Math.floor(totalMinutes / 60);
            const endMins = totalMinutes % 60;

            return String(endHours).padStart(2, '0') + ':' + String(endMins).padStart(2, '0');
        }

        // Schedule availability check with debouncing
        function scheduleAvailabilityCheck() {
            if (selectedBookingType === 'SINGLE_DAY') {
                clearTimeout(availabilityCheckTimeout);
                availabilityCheckTimeout = setTimeout(checkAvailability, 500);
            }
        }

        function scheduleMultiDayAvailabilityCheck() {
            if (selectedBookingType === 'MULTI_DAY') {
                clearTimeout(availabilityCheckTimeout);
                availabilityCheckTimeout = setTimeout(checkMultiDayAvailability, 500);
            }
        }

        // Check availability (single day)
        function checkAvailability() {
            const cabinId = document.getElementById('cabinId').value;
            const date = document.getElementById('bookingDate').value;

            if (!cabinId || !date || !selectedStartTime || selectedDuration <= 0) {
                return;
            }

            console.log('Checking single day availability:', {
                cabinId, date, startTime: selectedStartTime, duration: selectedDuration
            });

            showLoadingState();

            const contextPath = '${pageContext.request.contextPath}';
            fetch(contextPath + '/book?action=availability', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `cabinId=${cabinId}&date=${date}&startTime=${selectedStartTime}&duration=${selectedDuration}`
            })
            .then(response => response.json())
            .then(data => {
                console.log('Availability response:', data);
                showAvailabilityStatus(data);
            })
            .catch(error => {
                console.error('Error checking availability:', error);
                showAvailabilityStatus({
                    available: false,
                    error: 'Network error. Please check your connection.'
                });
            });
        }

        // Check multi-day availability
        function checkMultiDayAvailability() {
            const cabinId = document.getElementById('cabinId').value;
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;

            if (!cabinId || !startDate || !endDate) {
                return;
            }

            if (!updateMultiDayPreview()) {
                return; // Invalid date range
            }

            console.log('Checking multi-day availability:', { cabinId, startDate, endDate });

            showLoadingState();

            const contextPath = '${pageContext.request.contextPath}';
            fetch(contextPath + '/book?action=multiDayAvailability', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `cabinId=${cabinId}&startDate=${startDate}&endDate=${endDate}&bookingType=MULTI_DAY`
            })
            .then(response => response.json())
            .then(data => {
                console.log('Multi-day availability response:', data);
                showMultiDayAvailabilityStatus(data);
            })
            .catch(error => {
                console.error('Error checking multi-day availability:', error);
                showMultiDayAvailabilityStatus({
                    available: false,
                    error: 'Network error. Please check your connection.'
                });
            });
        }

        // Show loading state
        function showLoadingState() {
            const statusDiv = document.getElementById('availabilityStatus');
            statusDiv.style.display = 'block';
            statusDiv.className = 'availability-status';
            statusDiv.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Checking availability...';
            document.getElementById('submitBtn').disabled = true;
        }

        // Show availability status (single day)
        function showAvailabilityStatus(data) {
            const statusDiv = document.getElementById('availabilityStatus');
            const submitBtn = document.getElementById('submitBtn');

            statusDiv.style.display = 'block';

            if (data.available) {
                statusDiv.className = 'availability-status available';
                statusDiv.innerHTML = `
                    <div class="d-flex align-items-center">
                        <i class="fas fa-check-circle me-2"></i>
                        <div>
                            <strong>Great! This time slot is available!</strong><br>
                            <small>You can proceed with your booking.</small>
                        </div>
                    </div>
                `;
                submitBtn.disabled = false;
            } else {
                statusDiv.className = 'availability-status unavailable';
                let html = `
                    <div class="d-flex align-items-start">
                        <i class="fas fa-exclamation-circle me-2 mt-1"></i>
                        <div>
                            <strong>This time slot is not available</strong><br>
                `;

                if (data.error) {
                    html += `<small>Error: ${data.error}</small><br>`;
                }

                if (data.alternatives && data.alternatives.length > 0) {
                    html += `<small class="mb-2 d-block">Alternative time slots:</small><div>`;
                    data.alternatives.forEach(alt => {
                        html += `<button type="button" class="btn btn-sm btn-outline-primary alternative-btn me-1 mb-1" onclick="selectAlternative('${alt}')">${alt}</button>`;
                    });
                    html += `</div>`;
                } else {
                    html += `<small>No alternative slots available.</small>`;
                }

                html += `</div></div>`;
                statusDiv.innerHTML = html;
                submitBtn.disabled = true;
            }
        }

        // Show multi-day availability status
        function showMultiDayAvailabilityStatus(data) {
            const statusDiv = document.getElementById('availabilityStatus');
            const submitBtn = document.getElementById('submitBtn');

            statusDiv.style.display = 'block';

            if (data.available) {
                statusDiv.className = 'availability-status available';
                statusDiv.innerHTML = `
                    <div class="d-flex align-items-center">
                        <i class="fas fa-check-circle me-2"></i>
                        <div>
                            <strong>Excellent! All days are available for booking!</strong><br>
                            <small>Your multi-day booking can be processed.</small>
                        </div>
                    </div>
                `;
                submitBtn.disabled = false;
            } else {
                statusDiv.className = 'availability-status unavailable';
                let html = `
                    <div class="d-flex align-items-start">
                        <i class="fas fa-exclamation-circle me-2 mt-1"></i>
                        <div>
                            <strong>Some days in this range are not available</strong><br>
                `;

                if (data.conflictDays && data.conflictDays.length > 0) {
                    html += `<small class="mb-2 d-block">Conflicting dates: ${data.conflictDays.join(', ')}</small>`;
                }

                if (data.alternativeRanges && data.alternativeRanges.length > 0) {
                    html += `<small class="mb-2 d-block">Alternative date ranges:</small><div>`;
                    data.alternativeRanges.forEach(range => {
                        html += `<button type="button" class="btn btn-sm btn-outline-primary alternative-btn me-1 mb-1" onclick="selectAlternativeRange('${range.start}', '${range.end}')">${range.display}</button>`;
                    });
                    html += `</div>`;
                } else {
                    html += `<small>Please try a different date range.</small>`;
                }

                html += `</div></div>`;
                statusDiv.innerHTML = html;
                submitBtn.disabled = true;
            }
        }

        // Select alternative time slot
        function selectAlternative(timeSlot) {
            const [startTime, endTime] = timeSlot.split('-');
            document.getElementById('startTime').value = startTime;
            selectedStartTime = startTime;
            updateTimeSlotPreview();
            checkAvailability();
        }

        // Select alternative date range
        function selectAlternativeRange(startDate, endDate) {
            document.getElementById('startDate').value = startDate;
            document.getElementById('endDate').value = endDate;
            updateMultiDayPreview();
            checkMultiDayAvailability();
        }

        // Reset availability status
        function resetAvailabilityStatus() {
            document.getElementById('availabilityStatus').style.display = 'none';
            document.getElementById('submitBtn').disabled = true;
        }

        // Update cabin details
        function updateCabinDetails() {
            const select = document.getElementById('cabinId');
            const selectedOption = select.options[select.selectedIndex];
            const detailsElement = document.getElementById('cabinDetails');

            if (selectedOption.value) {
                const capacity = selectedOption.dataset.capacity;
                const location = selectedOption.dataset.location;
                const isVip = selectedOption.dataset.vip === 'true';

                let details = `Capacity: ${capacity} people | Location: ${location}`;
                if (isVip) {
                    details += ' | <span class="badge bg-warning">VIP Only</span>';
                }

                detailsElement.innerHTML = details;
            } else {
                detailsElement.innerHTML = '';
            }
        }

        // Update booking tips based on selected type
        function updateBookingTips() {
            const tipsContainer = document.getElementById('bookingTips');

            if (selectedBookingType === 'SINGLE_DAY') {
                tipsContainer.innerHTML = `
                    <li class="mb-2"><i class="fas fa-check text-success"></i> Choose 15 minutes to 8+ hours duration</li>
                    <li class="mb-2"><i class="fas fa-check text-success"></i> 15-minute precision scheduling</li>
                    <li class="mb-2"><i class="fas fa-check text-success"></i> Perfect for meetings & presentations</li>
                    <li class="mb-2"><i class="fas fa-check text-success"></i> Real-time availability checking</li>
                    <li class="mb-0"><i class="fas fa-check text-success"></i> AI suggests best alternatives</li>
                `;
            } else if (selectedBookingType === 'MULTI_DAY') {
                tipsContainer.innerHTML = `
                    <li class="mb-2"><i class="fas fa-check text-success"></i> Full-day access (9 AM - 6 PM)</li>
                    <li class="mb-2"><i class="fas fa-check text-success"></i> Up to 30 consecutive days</li>
                    <li class="mb-2"><i class="fas fa-check text-success"></i> Great for workshops & training</li>
                    <li class="mb-2"><i class="fas fa-check text-success"></i> Weekend days included</li>
                    <li class="mb-0"><i class="fas fa-check text-success"></i> Higher approval priority</li>
                `;
            }
        }

        // Auto-check availability when cabin changes
        document.getElementById('cabinId').addEventListener('change', function() {
            updateCabinDetails();

            if (selectedBookingType === 'SINGLE_DAY' && selectedStartTime && selectedDuration > 0) {
                scheduleAvailabilityCheck();
            } else if (selectedBookingType === 'MULTI_DAY') {
                scheduleMultiDayAvailabilityCheck();
            }
        });

        // Form validation and submission
        document.getElementById('bookingForm').addEventListener('submit', function(e) {
            if (!selectedBookingType) {
                e.preventDefault();
                alert('Please select booking type (Single Day or Multi-Day).');
                return false;
            }

            if (selectedBookingType === 'SINGLE_DAY') {
                if (!selectedDuration || !selectedStartTime || !document.getElementById('timeSlot').value) {
                    e.preventDefault();
                    alert('Please select duration and start time for single day booking.');
                    return false;
                }
            } else if (selectedBookingType === 'MULTI_DAY') {
                if (!document.getElementById('startDate').value || !document.getElementById('endDate').value) {
                    e.preventDefault();
                    alert('Please select start and end dates for multi-day booking.');
                    return false;
                }
            }

            if (!document.getElementById('purpose').value.trim()) {
                e.preventDefault();
                alert('Please enter the purpose of your booking.');
                return false;
            }

            // Show submitting state
            const submitBtn = document.getElementById('submitBtn');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Processing Booking...';
        });

        // Initialize form state
        document.addEventListener('DOMContentLoaded', function() {
            updateCabinDetails();

            // Set today's date as minimum
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('bookingDate').min = today;
            document.getElementById('startDate').min = today;
            document.getElementById('endDate').min = today;

            console.log('Single Day + Multi-Day booking form initialized');
        });
    </script>
</body>
</html>
