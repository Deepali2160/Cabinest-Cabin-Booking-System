// ====================================
// MY BOOKINGS PAGE - MINIMAL FUNCTIONALITY
// ====================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('📋 My Bookings page initialized for Yash Technology');

    // Initialize components
    initializeTabSystem();
    initializeUserDropdown();
    initializeCancelBooking();
    initializeMessageHandling();

    // Update booking counts on tab change
    updateTabCounts();
});

// Global variables
let bookingToCancel = null;

// Initialize tab system
function initializeTabSystem() {
    const filterTabs = document.querySelectorAll('.filter-tab');
    const tabContents = document.querySelectorAll('.booking-tab-content');

    filterTabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const filter = this.dataset.filter;

            // Remove active class from all tabs
            filterTabs.forEach(t => t.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));

            // Add active class to clicked tab
            this.classList.add('active');

            // Show corresponding content
            const targetContent = document.getElementById(filter + '-content');
            if (targetContent) {
                targetContent.classList.add('active');
            }

            console.log('📋 Tab switched to:', filter);

            // Update URL without refresh (optional)
            if (history.pushState) {
                const url = new URL(window.location);
                url.searchParams.set('tab', filter);
                history.pushState(null, '', url);
            }
        });
    });

    // Load tab from URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    const tabParam = urlParams.get('tab');
    if (tabParam) {
        const targetTab = document.querySelector(`[data-filter="${tabParam}"]`);
        if (targetTab) {
            targetTab.click();
        }
    }
}

// Initialize user dropdown
function initializeUserDropdown() {
    const dropdownBtn = document.getElementById('userDropdown');
    const dropdownMenu = document.getElementById('dropdownMenu');

    if (dropdownBtn && dropdownMenu) {
        dropdownBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            dropdownMenu.classList.toggle('show');
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!dropdownBtn.contains(e.target) && !dropdownMenu.contains(e.target)) {
                dropdownMenu.classList.remove('show');
            }
        });

        // Close dropdown on escape key
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                dropdownMenu.classList.remove('show');
            }
        });
    }
}

// Initialize cancel booking functionality
function initializeCancelBooking() {
    const cancelButtons = document.querySelectorAll('.cancel-btn');
    const modal = document.getElementById('cancelModal');
    const modalClose = document.getElementById('modalClose');
    const keepBookingBtn = document.getElementById('keepBooking');
    const confirmCancelBtn = document.getElementById('confirmCancel');
    const cabinNameSpan = document.getElementById('cancelCabinName');

    if (!modal || !confirmCancelBtn) return;

    // Handle cancel button clicks
    cancelButtons.forEach(button => {
        button.addEventListener('click', function() {
            bookingToCancel = this.dataset.bookingId;
            const cabinName = this.dataset.cabinName;

            console.log('🗑️ Cancel requested for booking:', bookingToCancel);

            if (cabinNameSpan) {
                cabinNameSpan.textContent = cabinName;
            }

            showModal(modal);
        });
    });

    // Handle modal close events
    [modalClose, keepBookingBtn].forEach(btn => {
        if (btn) {
            btn.addEventListener('click', function() {
                hideModal(modal);
                bookingToCancel = null;
            });
        }
    });

    // Handle confirm cancel
    confirmCancelBtn.addEventListener('click', function() {
        if (bookingToCancel) {
            processCancelBooking(bookingToCancel);
        }
    });

    // Close modal on overlay click
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            hideModal(modal);
            bookingToCancel = null;
        }
    });

    // Close modal on escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && modal.classList.contains('show')) {
            hideModal(modal);
            bookingToCancel = null;
        }
    });
}

// Show modal
function showModal(modal) {
    modal.classList.add('show');
    document.body.style.overflow = 'hidden';
}

// Hide modal
function hideModal(modal) {
    modal.classList.remove('show');
    document.body.style.overflow = '';
}

// Process booking cancellation
function processCancelBooking(bookingId) {
    const confirmBtn = document.getElementById('confirmCancel');

    // Show loading state
    confirmBtn.innerHTML = '⏳ Cancelling...';
    confirmBtn.disabled = true;

    console.log('🗑️ Processing cancellation for booking:', bookingId);

    // Create form and submit
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = getContextPath() + '/booking/cancel';

    const bookingIdInput = document.createElement('input');
    bookingIdInput.type = 'hidden';
    bookingIdInput.name = 'bookingId';
    bookingIdInput.value = bookingId;

    form.appendChild(bookingIdInput);
    document.body.appendChild(form);
    form.submit();
}

// Initialize message handling
function initializeMessageHandling() {
    const messages = document.querySelectorAll('.message');

    messages.forEach(message => {
        // Auto-hide after 5 seconds
        setTimeout(() => {
            message.style.opacity = '0';
            setTimeout(() => {
                if (message.parentNode) {
                    message.parentNode.removeChild(message);
                }
            }, 300);
        }, 5000);
    });
}

// Update tab counts (if dynamic loading is needed)
function updateTabCounts() {
    // This function can be used for AJAX updates of booking counts
    const bookingCards = document.querySelectorAll('.booking-card');

    let counts = {
        all: bookingCards.length,
        pending: 0,
        approved: 0,
        rejected: 0
    };

    bookingCards.forEach(card => {
        const status = card.dataset.status;
        if (status && counts.hasOwnProperty(status)) {
            counts[status]++;
        }
    });

    console.log('📊 Booking counts:', counts);

    // Update tab labels if needed (for dynamic updates)
    // This would be useful if implementing real-time updates via WebSocket
}

// Utility functions
function getContextPath() {
    const path = window.location.pathname;
    return path.substring(0, path.indexOf('/', 1)) || '';
}

// Booking card animations
function addCardAnimations() {
    const bookingCards = document.querySelectorAll('.booking-card');

    bookingCards.forEach((card, index) => {
        card.style.animationDelay = (index * 0.1) + 's';

        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
            this.style.boxShadow = '0 10px 30px rgba(0,0,0,0.15)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '0 4px 15px rgba(0,0,0,0.1)';
        });
    });
}

// Initialize card animations
addCardAnimations();

// Keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + N = New Booking
    if ((e.ctrlKey || e.metaKey) && e.key === 'n') {
        e.preventDefault();
        window.location.href = getContextPath() + '/book';
    }

    // Ctrl/Cmd + H = Dashboard
    if ((e.ctrlKey || e.metaKey) && e.key === 'h') {
        e.preventDefault();
        window.location.href = getContextPath() + '/dashboard';
    }
});

// Export utilities for potential use
const MyBookingsUtils = {
    // Refresh booking status (for future AJAX implementation)
    refreshBookingStatus: function(bookingId) {
        console.log('🔄 Refreshing status for booking:', bookingId);
        // Implementation for AJAX refresh
    },

    // Filter bookings by date range
    filterByDateRange: function(startDate, endDate) {
        console.log('📅 Filtering bookings from', startDate, 'to', endDate);
        // Implementation for date filtering
    },

    // Export bookings data (for future feature)
    exportBookings: function(format = 'csv') {
        console.log('📊 Exporting bookings in', format, 'format');
        // Implementation for export functionality
    }
};

// Make utilities available globally
window.MyBookingsUtils = MyBookingsUtils;

console.log('🎯 My Bookings JavaScript loaded successfully');
