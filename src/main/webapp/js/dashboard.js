// ====================================
// DASHBOARD PAGE - MINIMAL FUNCTIONALITY
// ====================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('🏠 Dashboard initialized for Yash Technology');

    // User dropdown functionality
    initializeUserDropdown();

    // Auto-hide messages
    initializeMessageHandling();

    // Loading states for buttons
    initializeLoadingStates();

    // Mobile menu toggle (if needed)
    initializeMobileMenu();

    // Update booking score animation
    animateBookingScore();
});

// User Dropdown Management
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

        // Close dropdown when pressing escape
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                dropdownMenu.classList.remove('show');
            }
        });
    }
}

// Message Handling
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

        // Add close button functionality if needed
        const closeBtn = message.querySelector('.close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                message.style.opacity = '0';
                setTimeout(() => {
                    if (message.parentNode) {
                        message.parentNode.removeChild(message);
                    }
                }, 300);
            });
        }
    });
}

// Loading States for Action Buttons
function initializeLoadingStates() {
    const actionLinks = document.querySelectorAll('.action-card, .book-btn, .recommendation-btn, .view-all-btn');

    actionLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            if (!this.classList.contains('loading')) {
                const originalText = this.innerHTML;
                const originalHref = this.href;

                // Show loading state
                this.innerHTML = '⏳ Loading...';
                this.classList.add('loading');
                this.style.pointerEvents = 'none';

                // Navigate after short delay for UX
                setTimeout(() => {
                    window.location.href = originalHref;
                }, 500);
            }
            e.preventDefault();
        });
    });
}

// Mobile Menu Toggle (if needed in future)
function initializeMobileMenu() {
    const mobileToggle = document.getElementById('mobileToggle');
    const navMenu = document.getElementById('navMenu');

    if (mobileToggle && navMenu) {
        mobileToggle.addEventListener('click', function() {
            navMenu.classList.toggle('show');
        });
    }
}

// Animate Booking Score
function animateBookingScore() {
    const scoreElement = document.querySelector('.score-number');
    if (scoreElement) {
        const finalScore = parseInt(scoreElement.textContent);
        let currentScore = 0;
        const increment = finalScore / 50; // Animation duration control

        scoreElement.textContent = '0';

        const timer = setInterval(() => {
            currentScore += increment;
            if (currentScore >= finalScore) {
                currentScore = finalScore;
                clearInterval(timer);
            }
            scoreElement.textContent = Math.floor(currentScore);
        }, 20);
    }
}

// Dashboard Utilities
const DashboardUtils = {
    // Refresh cabin availability
    refreshCabinAvailability: function() {
        console.log('🔄 Refreshing cabin availability...');
        // Could implement AJAX refresh here
    },

    // Quick book function
    quickBook: function(cabinId) {
        if (cabinId) {
            window.location.href = `/book?cabinId=${cabinId}`;
        } else {
            window.location.href = '/book';
        }
    },

    // Show notification
    showNotification: function(message, type = 'info') {
        Utils.showMessage(message, type);
    },

    // Format booking status
    formatBookingStatus: function(status) {
        const statusMap = {
            'PENDING': '⏳ Pending',
            'APPROVED': '✅ Approved',
            'REJECTED': '❌ Rejected',
            'CANCELLED': '🚫 Cancelled'
        };
        return statusMap[status] || status;
    }
};

// Keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + B = Quick Book
    if ((e.ctrlKey || e.metaKey) && e.key === 'b') {
        e.preventDefault();
        DashboardUtils.quickBook();
    }

    // Ctrl/Cmd + M = My Bookings
    if ((e.ctrlKey || e.metaKey) && e.key === 'm') {
        e.preventDefault();
        window.location.href = '/mybookings';
    }
});

// Export for use in other scripts
window.DashboardUtils = DashboardUtils;

console.log('🎯 Dashboard JavaScript loaded successfully');
