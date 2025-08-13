// ====================================
// BOOKING PAGE - ENHANCED FUNCTIONALITY
// ====================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('📅 Booking form initialized for Yash Technology');

    // ✅ ADD DEBUG INFO FOR CONTEXT PATH
    console.log('🔍 DEBUG INFO:');
    console.log('   - window.location.pathname:', window.location.pathname);
    console.log('   - window.contextPath:', window.contextPath);
    console.log('   - nav contextPath:', document.querySelector('nav')?.dataset?.contextPath);

    // Initialize booking system
    initializeBookingForm();
    initializeBookingTypeSelection();
    initializeCabinSelection();
    initializeDurationSelection();
    initializeTimeSelection();
    initializeDateSelection();
    initializePurposeSuggestions();
    initializeSidebarInteractions();

    // Set minimum dates
    setMinimumDates();
});

// Global variables
let selectedBookingType = '';
let selectedDuration = 0;
let selectedStartTime = '';
let availabilityTimeout;

// Initialize main booking form
function initializeBookingForm() {
    const form = document.getElementById('bookingForm');

    form.addEventListener('submit', function(e) {
        if (!validateBookingForm()) {
            e.preventDefault();
            return false;
        }

        // Show loading state
        const submitBtn = document.getElementById('submitBtn');
        const submitText = document.getElementById('submitText');

        submitBtn.disabled = true;
        submitText.innerHTML = '⏳ Processing Booking...';

        console.log('📝 Booking form submitted successfully');
    });
}

// Initialize booking type selection
function initializeBookingTypeSelection() {
    const typeCards = document.querySelectorAll('.booking-type-card');

    typeCards.forEach(card => {
        card.addEventListener('click', function() {
            // Remove selected class from all cards
            typeCards.forEach(c => c.classList.remove('selected'));

            // Add selected class to clicked card
            this.classList.add('selected');

            selectedBookingType = this.dataset.type;
            document.getElementById('bookingType').value = selectedBookingType;

            console.log('📅 Booking type selected:', selectedBookingType);

            // Show appropriate section
            showBookingSection(selectedBookingType);

            // Update tips and purpose suggestions
            updateBookingTips(selectedBookingType);
            updatePurposeSuggestions(selectedBookingType);

            // Reset form state
            resetAvailabilityStatus();
            enableSubmitButton(false);
        });
    });
}

// Show appropriate booking section
function showBookingSection(type) {
    const singleDaySection = document.getElementById('singleDaySection');
    const multiDaySection = document.getElementById('multiDaySection');

    if (type === 'SINGLE_DAY') {
        singleDaySection.style.display = 'block';
        multiDaySection.style.display = 'none';

        // Set required fields
        document.getElementById('bookingDate').required = true;
        document.getElementById('startDate').required = false;
        document.getElementById('endDate').required = false;

        console.log('📅 Single day section shown');
    } else if (type === 'MULTI_DAY') {
        singleDaySection.style.display = 'none';
        multiDaySection.style.display = 'block';

        // Set required fields
        document.getElementById('bookingDate').required = false;
        document.getElementById('startDate').required = true;
        document.getElementById('endDate').required = true;

        // Set time slot for multi-day (full day)
        document.getElementById('timeSlot').value = '09:00-18:00';

        console.log('📆 Multi-day section shown');
    }
}

// Initialize cabin selection
function initializeCabinSelection() {
    const cabinSelect = document.getElementById('cabinId');

    cabinSelect.addEventListener('change', function() {
        updateCabinDetails();

        // Check availability when cabin changes
        if (selectedBookingType === 'SINGLE_DAY' && selectedDuration && selectedStartTime) {
            scheduleAvailabilityCheck();
        } else if (selectedBookingType === 'MULTI_DAY') {
            scheduleMultiDayAvailabilityCheck();
        }
    });

    // Update cabin details on page load
    updateCabinDetails();
}

// Update cabin details display
function updateCabinDetails() {
    const select = document.getElementById('cabinId');
    const selectedOption = select.options[select.selectedIndex];
    const detailsElement = document.getElementById('cabinDetails');

    if (selectedOption.value) {
        const capacity = selectedOption.dataset.capacity;
        const location = selectedOption.dataset.location;
        const isVip = selectedOption.dataset.vip === 'true';

        let details = `👥 Capacity: ${capacity} people | 📍 Location: ${location}`;
        if (isVip) {
            details += ' | <span class="vip-badge">⭐ VIP Only</span>';
        }

        detailsElement.innerHTML = details;
        detailsElement.style.display = 'block';

        console.log('🏠 Cabin selected:', selectedOption.text);
    } else {
        detailsElement.style.display = 'none';
    }
}

// Initialize duration selection
function initializeDurationSelection() {
    const durationCards = document.querySelectorAll('.duration-card');

    durationCards.forEach(card => {
        card.addEventListener('click', function() {
            if (selectedBookingType !== 'SINGLE_DAY') return;

            // Remove selected class from all cards
            durationCards.forEach(c => c.classList.remove('selected'));

            // Add selected class to clicked card
            this.classList.add('selected');

            selectedDuration = parseInt(this.dataset.duration);
            document.getElementById('duration').value = selectedDuration;

            // Show start time selection
            document.getElementById('startTimeGroup').style.display = 'block';

            console.log('⏰ Duration selected:', selectedDuration, 'minutes');

            // Update time slot preview if start time is selected
            if (selectedStartTime) {
                updateTimeSlotPreview();
                scheduleAvailabilityCheck();
            }

            resetAvailabilityStatus();
        });
    });
}

// Initialize time selection
function initializeTimeSelection() {
    const startTimeSelect = document.getElementById('startTime');

    startTimeSelect.addEventListener('change', function() {
        selectedStartTime = this.value;

        if (selectedStartTime && selectedDuration > 0) {
            updateTimeSlotPreview();
            scheduleAvailabilityCheck();
        }

        console.log('🕐 Start time selected:', selectedStartTime);
    });
}

// Update time slot preview
function updateTimeSlotPreview() {
    if (selectedStartTime && selectedDuration > 0) {
        const endTime = calculateEndTime(selectedStartTime, selectedDuration);
        const timeSlot = selectedStartTime + '-' + endTime;

        document.getElementById('selectedTimeSlot').textContent = timeSlot;
        document.getElementById('timeSlotPreview').style.display = 'block';
        document.getElementById('timeSlot').value = timeSlot;

        console.log('⏰ Time slot preview updated:', timeSlot);
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

// Initialize date selection for multi-day
function initializeDateSelection() {
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');

    startDateInput.addEventListener('change', function() {
        updateEndDateMin();
        updateDateRangePreview();
        scheduleMultiDayAvailabilityCheck();
    });

    endDateInput.addEventListener('change', function() {
        updateDateRangePreview();
        scheduleMultiDayAvailabilityCheck();
    });

    // Single day date
    const bookingDateInput = document.getElementById('bookingDate');
    bookingDateInput.addEventListener('change', function() {
        if (selectedDuration && selectedStartTime) {
            scheduleAvailabilityCheck();
        }
    });
}

// Update minimum end date
function updateEndDateMin() {
    const startDate = document.getElementById('startDate').value;
    if (startDate) {
        document.getElementById('endDate').min = startDate;
    }
}

// Update date range preview
function updateDateRangePreview() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const previewDiv = document.getElementById('dateRangePreview');

    if (startDate && endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);

        if (end >= start) {
            const diffTime = Math.abs(end - start);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;

            if (diffDays <= 30) {
                document.getElementById('dayCount').textContent = diffDays;
                document.getElementById('dateRangeText').textContent =
                    formatDate(startDate) + ' → ' + formatDate(endDate);

                previewDiv.style.display = 'block';

                console.log('📆 Date range preview updated:', diffDays, 'days');
                return true;
            } else {
                showDateRangeError('Maximum 30 consecutive days allowed');
                return false;
            }
        } else {
            showDateRangeError('End date must be same or after start date');
            return false;
        }
    } else {
        previewDiv.style.display = 'none';
        return false;
    }
}

// Show date range error
function showDateRangeError(message) {
    const previewDiv = document.getElementById('dateRangePreview');
    previewDiv.style.display = 'block';
    previewDiv.innerHTML = `
        <div class="preview-content error">
            <div class="error-icon">❌</div>
            <div class="error-message">
                <strong>Error:</strong> ${message}
            </div>
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

// Initialize purpose suggestions
function initializePurposeSuggestions() {
    const purposeButtons = document.querySelectorAll('.purpose-btn');
    const purposeTextarea = document.getElementById('purpose');

    purposeButtons.forEach(button => {
        button.addEventListener('click', function() {
            const purpose = this.dataset.purpose;
            purposeTextarea.value = purpose;

            console.log('💬 Purpose suggestion selected:', purpose);

            // Validate form after purpose selection
            validateBookingForm();
        });
    });
}

// Update purpose suggestions based on booking type
function updatePurposeSuggestions(type) {
    const multiDayPurposes = document.querySelectorAll('.multi-day-purpose');

    if (type === 'MULTI_DAY') {
        multiDayPurposes.forEach(btn => btn.style.display = 'inline-block');
    } else {
        multiDayPurposes.forEach(btn => btn.style.display = 'none');
    }
}

// Initialize sidebar interactions
function initializeSidebarInteractions() {
    // Recommended cabin selection
    const selectCabinButtons = document.querySelectorAll('.select-cabin-btn');
    selectCabinButtons.forEach(button => {
        button.addEventListener('click', function() {
            const cabinId = this.dataset.cabinId;
            document.getElementById('cabinId').value = cabinId;
            updateCabinDetails();

            console.log('🏠 Recommended cabin selected:', cabinId);
        });
    });

    // Popular time selection
    const popularTimeButtons = document.querySelectorAll('.popular-time-btn');
    popularTimeButtons.forEach(button => {
        button.addEventListener('click', function() {
            if (selectedBookingType === 'SINGLE_DAY') {
                const timeSlot = this.dataset.time;
                const [startTime] = timeSlot.split('-');

                document.getElementById('startTime').value = startTime;
                selectedStartTime = startTime;

                if (selectedDuration) {
                    updateTimeSlotPreview();
                    scheduleAvailabilityCheck();
                }

                console.log('🕐 Popular time selected:', startTime);
            }
        });
    });

    // Alternative time slot buttons (using event delegation for dynamic buttons)
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('alternative-btn')) {
            const timeSlot = e.target.dataset.time;
            const [startTime] = timeSlot.split('-');

            document.getElementById('startTime').value = startTime;
            selectedStartTime = startTime;

            updateTimeSlotPreview();
            scheduleAvailabilityCheck();

            console.log('🔄 Alternative time selected:', startTime);
        }
    });
}

// Schedule availability check with debouncing
function scheduleAvailabilityCheck() {
    clearTimeout(availabilityTimeout);
    availabilityTimeout = setTimeout(checkAvailability, 500);
}

function scheduleMultiDayAvailabilityCheck() {
    clearTimeout(availabilityTimeout);
    availabilityTimeout = setTimeout(checkMultiDayAvailability, 500);
}

// ✅ CRITICAL FIX: Better context path detection function
function getContextPath() {
    // Method 1: Use window.contextPath if set in JSP
    if (window.contextPath) {
        console.log('🌐 Using window.contextPath:', window.contextPath);
        return window.contextPath;
    }

    // Method 2: Get from nav element data attribute
    const nav = document.querySelector('nav');
    if (nav && nav.dataset.contextPath) {
        console.log('🌐 Using nav contextPath:', nav.dataset.contextPath);
        return nav.dataset.contextPath;
    }

    // Method 3: Extract from current URL pathname
    const path = window.location.pathname;
    const parts = path.split('/');

    if (parts.length > 2 && parts[1]) {
        const contextPath = '/' + parts[1];
        console.log('🌐 Extracted contextPath from URL:', contextPath);
        return contextPath;
    }

    // Method 4: Fallback - empty string for root deployment
    console.log('🌐 Using empty contextPath (root deployment)');
    return '';
}

// ✅ MAIN FIX: Check single day availability with detailed logging
function checkAvailability() {
    const cabinId = document.getElementById('cabinId').value;
    const date = document.getElementById('bookingDate').value;

    if (!cabinId || !date || !selectedStartTime || selectedDuration <= 0) {
        console.log('⚠️ Missing required fields for availability check');
        return;
    }

    console.log('🔍 Checking single day availability...');
    showLoadingAvailability();

    // ✅ CRITICAL: Get context path with detailed logging
    const contextPath = getContextPath();
    const requestBody = `cabinId=${cabinId}&date=${date}&startTime=${selectedStartTime}&duration=${selectedDuration}`;
    const fullURL = contextPath + '/book?action=availability';

    console.log('📡 Request details:');
    console.log('   - Context Path:', contextPath);
    console.log('   - Full URL:', fullURL);
    console.log('   - Request Body:', requestBody);

    fetch(fullURL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: requestBody
    })
    .then(response => {
        console.log('📡 Response received:');
        console.log('   - Status:', response.status);
        console.log('   - Content-Type:', response.headers.get('Content-Type'));

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        console.log('✅ Availability response:', data);
        showAvailabilityStatus(data);
    })
    .catch(error => {
        console.error('❌ Error checking availability:', error);
        showAvailabilityError('Network error. Please check your connection.');
    });
}

// ✅ FIXED: Check multi-day availability
function checkMultiDayAvailability() {
    const cabinId = document.getElementById('cabinId').value;
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    if (!cabinId || !startDate || !endDate || !updateDateRangePreview()) {
        return;
    }

    console.log('🔍 Checking multi-day availability...');
    showLoadingAvailability();

    // ✅ CRITICAL: Get context path with detailed logging
    const contextPath = getContextPath();
    const fullURL = contextPath + '/book?action=multiDayAvailability';

    console.log('📡 Multi-day request details:');
    console.log('   - Context Path:', contextPath);
    console.log('   - Full URL:', fullURL);

    fetch(fullURL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `cabinId=${cabinId}&startDate=${startDate}&endDate=${endDate}&bookingType=MULTI_DAY`
    })
    .then(response => {
        console.log('📡 Multi-day response status:', response.status);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        console.log('✅ Multi-day availability response:', data);
        showMultiDayAvailabilityStatus(data);
    })
    .catch(error => {
        console.error('❌ Error checking multi-day availability:', error);
        showAvailabilityError('Network error. Please check your connection.');
    });
}

// Show loading availability
function showLoadingAvailability() {
    const statusDiv = document.getElementById('availabilityStatus');
    statusDiv.className = 'availability-status loading';
    statusDiv.style.display = 'block';
    statusDiv.innerHTML = '⏳ Checking availability...';
    enableSubmitButton(false);
}

// ✅ ENHANCED: Show availability status with better alternative handling
function showAvailabilityStatus(data) {
    const statusDiv = document.getElementById('availabilityStatus');
    statusDiv.style.display = 'block';

    if (data.available) {
        statusDiv.className = 'availability-status available';
        statusDiv.innerHTML = `
            <div>
                <strong>✅ Great! This time slot is available!</strong><br>
                <small>You can proceed with your booking.</small>
            </div>
        `;
        enableSubmitButton(true);
    } else {
        statusDiv.className = 'availability-status unavailable';
        let html = `
            <div>
                <strong>❌ This time slot is not available</strong><br>
        `;

        if (data.error) {
            html += `<small>Error: ${data.error}</small><br>`;
        }

        if (data.alternatives && data.alternatives.length > 0) {
            html += `<small>Alternative time slots:</small><br>`;
            html += '<div class="alternatives-list">';
            data.alternatives.forEach(alt => {
                html += `<button class="alternative-btn" data-time="${alt}">${alt}</button>`;
            });
            html += '</div>';
        }

        html += '</div>';
        statusDiv.innerHTML = html;
        enableSubmitButton(false);
    }
}

// Show multi-day availability status
function showMultiDayAvailabilityStatus(data) {
    const statusDiv = document.getElementById('availabilityStatus');
    statusDiv.style.display = 'block';

    if (data.available) {
        statusDiv.className = 'availability-status available';
        statusDiv.innerHTML = `
            <div>
                <strong>✅ Excellent! All days are available!</strong><br>
                <small>Your multi-day booking can be processed.</small>
            </div>
        `;
        enableSubmitButton(true);
    } else {
        statusDiv.className = 'availability-status unavailable';
        let html = `
            <div>
                <strong>❌ Some days in this range are not available</strong><br>
        `;

        if (data.conflictDays && data.conflictDays.length > 0) {
            html += `<small>Conflicting dates: ${data.conflictDays.join(', ')}</small><br>`;
        }

        html += '<small>Please try a different date range.</small>';
        html += '</div>';
        statusDiv.innerHTML = html;
        enableSubmitButton(false);
    }
}

// Show availability error
function showAvailabilityError(message) {
    const statusDiv = document.getElementById('availabilityStatus');
    statusDiv.className = 'availability-status unavailable';
    statusDiv.style.display = 'block';
    statusDiv.innerHTML = `
        <div>
            <strong>❌ Error checking availability</strong><br>
            <small>${message}</small>
        </div>
    `;
    enableSubmitButton(false);
}

// Reset availability status
function resetAvailabilityStatus() {
    document.getElementById('availabilityStatus').style.display = 'none';
    enableSubmitButton(false);
}

// Enable/disable submit button
function enableSubmitButton(enable) {
    const submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = !enable;
}

// ✅ ENHANCED: Select alternative time slot
function selectAlternative(timeSlot) {
    const [startTime] = timeSlot.split('-');
    document.getElementById('startTime').value = startTime;
    selectedStartTime = startTime;
    updateTimeSlotPreview();
    scheduleAvailabilityCheck();
}

// Update booking tips
function updateBookingTips(type) {
    const tipsContainer = document.getElementById('bookingTips');

    if (type === 'SINGLE_DAY') {
        tipsContainer.innerHTML = `
            <div class="tip-item">⏰ Choose 15 minutes to 8+ hours duration</div>
            <div class="tip-item">🎯 15-minute precision scheduling</div>
            <div class="tip-item">👥 Perfect for meetings & presentations</div>
            <div class="tip-item">✅ Real-time availability checking</div>
        `;
    } else if (type === 'MULTI_DAY') {
        tipsContainer.innerHTML = `
            <div class="tip-item">📅 Full-day access (9 AM - 6 PM)</div>
            <div class="tip-item">📊 Up to 30 consecutive days</div>
            <div class="tip-item">🎓 Great for workshops & training</div>
            <div class="tip-item">⭐ Higher approval priority</div>
        `;
    } else {
        tipsContainer.innerHTML = `
            <div class="tip-item">📅 Select your booking type first</div>
            <div class="tip-item">🏠 Choose from available cabins</div>
            <div class="tip-item">⏰ Pick your preferred time</div>
            <div class="tip-item">✅ Check availability before submitting</div>
        `;
    }
}

// ✅ ENHANCED: Validate booking form with better error handling
function validateBookingForm() {
    const cabinId = document.getElementById('cabinId').value;
    const purpose = document.getElementById('purpose').value.trim();

    if (!selectedBookingType) {
        showValidationError('Please select booking type (Single Day or Multi-Day)');
        return false;
    }

    if (!cabinId) {
        showValidationError('Please select a cabin');
        return false;
    }

    if (selectedBookingType === 'SINGLE_DAY') {
        const bookingDate = document.getElementById('bookingDate').value;

        if (!bookingDate || !selectedDuration || !selectedStartTime) {
            showValidationError('Please complete all single day booking fields');
            return false;
        }
    } else if (selectedBookingType === 'MULTI_DAY') {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        if (!startDate || !endDate) {
            showValidationError('Please select start and end dates for multi-day booking');
            return false;
        }
    }

    if (!purpose) {
        showValidationError('Please enter the purpose of your booking');
        return false;
    }

    return true;
}

// ✅ NEW: Show validation error function
function showValidationError(message) {
    // Try to use Utils if available, otherwise use alert
    if (typeof Utils !== 'undefined' && Utils.showMessage) {
        Utils.showMessage(message, 'error');
    } else {
        alert('❌ ' + message);
    }
}

// Set minimum dates
function setMinimumDates() {
    const today = new Date().toISOString().split('T')[0];

    const bookingDate = document.getElementById('bookingDate');
    const startDate = document.getElementById('startDate');
    const endDate = document.getElementById('endDate');

    if (bookingDate) bookingDate.min = today;
    if (startDate) startDate.min = today;
    if (endDate) endDate.min = today;

    console.log('📅 Minimum dates set to:', today);
}

// ✅ ENHANCED: Export for global use
window.BookingUtils = {
    selectAlternative,
    selectedBookingType: () => selectedBookingType,
    selectedDuration: () => selectedDuration,
    selectedStartTime: () => selectedStartTime,
    getContextPath,
    scheduleAvailabilityCheck,
    scheduleMultiDayAvailabilityCheck
};

console.log('🎯 Booking JavaScript loaded successfully');
