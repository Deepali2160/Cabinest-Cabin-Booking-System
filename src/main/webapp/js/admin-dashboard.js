// ====================================
// ADMIN DASHBOARD - ENHANCED VERSION WITH WORKING DROPDOWN
// ====================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Admin Dashboard DOM loaded for Yash Technology');

    // Small delay to ensure all elements are rendered
    setTimeout(() => {
        console.log('🔧 Starting component initialization...');

        // Initialize dropdowns first (most important)
        initializeDropdowns();

        // Then other components
        initializeBookingActions();
        initializeMessageHandling();
        initializeQuickActions();
        initializeCabinCardAnimations();
        initializeTableAnimations();

        console.log('✅ All components initialized successfully');

        // Perform health check
        performHealthCheck();

    }, 100); // 100ms delay to ensure DOM is fully ready

    // Auto-refresh pending count every 30 seconds
    setInterval(updatePendingCount, 30000);
});

// ✅ ENHANCED DROPDOWN INITIALIZATION WITH DEBUGGING
function initializeDropdowns() {
    console.log('🔧 Initializing dropdown functionality...');

    // ✅ USER DROPDOWN - Enhanced with debugging
    const userDropdown = document.getElementById('userDropdown');
    const userDropdownMenu = document.getElementById('userDropdownMenu');

    console.log('🔍 User dropdown elements:', {
        userDropdown: !!userDropdown,
        userDropdownMenu: !!userDropdownMenu,
        userDropdownExists: userDropdown ? 'Found' : 'Missing',
        menuExists: userDropdownMenu ? 'Found' : 'Missing'
    });

    if (userDropdown && userDropdownMenu) {
        console.log('✅ User dropdown elements found, adding event listeners...');

        userDropdown.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();

            console.log('🔽 User dropdown button clicked!');

            // Close cabin dropdown first
            const cabinDropdownMenu = document.getElementById('cabinDropdownMenu');
            if (cabinDropdownMenu) {
                cabinDropdownMenu.classList.remove('show');
            }

            // Toggle user dropdown
            const isCurrentlyOpen = userDropdownMenu.classList.contains('show');
            console.log('🔽 Dropdown current state:', isCurrentlyOpen ? 'Open' : 'Closed');

            if (isCurrentlyOpen) {
                userDropdownMenu.classList.remove('show');
                console.log('🔽 Closing dropdown');
            } else {
                userDropdownMenu.classList.add('show');
                console.log('🔽 Opening dropdown');
            }

            // Log final state
            console.log('🔽 Dropdown final state:', userDropdownMenu.classList.contains('show') ? 'Open' : 'Closed');
        });

        console.log('✅ User dropdown click handler added');
    } else {
        console.error('❌ User dropdown elements not found!');
        console.error('Missing elements:', {
            userDropdown: !userDropdown ? 'userDropdown button missing' : 'OK',
            userDropdownMenu: !userDropdownMenu ? 'userDropdownMenu div missing' : 'OK'
        });
    }

    // ✅ CABIN DROPDOWN - Enhanced functionality
    const cabinDropdown = document.getElementById('cabinDropdown');
    const cabinDropdownMenu = document.getElementById('cabinDropdownMenu');

    if (cabinDropdown && cabinDropdownMenu) {
        console.log('✅ Cabin dropdown found, adding handlers...');

        cabinDropdown.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            console.log('🏠 Cabin dropdown clicked');

            // Close user dropdown
            if (userDropdownMenu) {
                userDropdownMenu.classList.remove('show');
            }

            // Toggle cabin dropdown
            cabinDropdownMenu.classList.toggle('show');
        });
    }

    // ✅ ENHANCED CLICK OUTSIDE TO CLOSE
    document.addEventListener('click', function(e) {
        console.log('🔍 Document clicked, checking if should close dropdowns...');

        // Check if click is outside any dropdown
        const clickedInsideUser = userDropdown && (userDropdown.contains(e.target) || (userDropdownMenu && userDropdownMenu.contains(e.target)));
        const clickedInsideCabin = cabinDropdown && (cabinDropdown.contains(e.target) || (cabinDropdownMenu && cabinDropdownMenu.contains(e.target)));

        if (!clickedInsideUser && !clickedInsideCabin) {
            console.log('🔍 Click outside dropdowns, closing all...');

            const allDropdowns = document.querySelectorAll('.dropdown-menu');
            allDropdowns.forEach(dropdown => {
                if (dropdown.classList.contains('show')) {
                    console.log('🔽 Closing dropdown:', dropdown.id);
                }
                dropdown.classList.remove('show');
            });
        }
    });

    // ✅ ESCAPE KEY TO CLOSE
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            console.log('⌨️ Escape key pressed, closing all dropdowns');
            const allDropdowns = document.querySelectorAll('.dropdown-menu');
            allDropdowns.forEach(dropdown => {
                dropdown.classList.remove('show');
            });
        }
    });

    console.log('✅ Dropdown initialization complete');
}

// Initialize booking approval/rejection actions
function initializeBookingActions() {
    console.log('📋 Initializing booking actions...');

    // Approve buttons
    const approveButtons = document.querySelectorAll('.approve-btn');
    approveButtons.forEach(button => {
        button.addEventListener('click', function() {
            const bookingId = this.dataset.bookingId;

            if (confirm('Are you sure you want to approve this booking?')) {
                approveBooking(bookingId);
            }
        });
    });

    // Reject buttons
    const rejectButtons = document.querySelectorAll('.reject-btn');
    rejectButtons.forEach(button => {
        button.addEventListener('click', function() {
            const bookingId = this.dataset.bookingId;

            if (confirm('Are you sure you want to reject this booking?')) {
                rejectBooking(bookingId);
            }
        });
    });

    console.log('✅ Booking actions initialized');
}

// Approve booking function
function approveBooking(bookingId) {
    console.log('✅ Approving booking:', bookingId);

    // Show loading state
    const approveBtn = document.querySelector(`[data-booking-id="${bookingId}"].approve-btn`);
    if (approveBtn) {
        approveBtn.innerHTML = '⏳ Approving...';
        approveBtn.disabled = true;
    }

    // Create and submit form
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = getContextPath() + '/admin/approve-booking';

    const bookingIdInput = document.createElement('input');
    bookingIdInput.type = 'hidden';
    bookingIdInput.name = 'bookingId';
    bookingIdInput.value = bookingId;

    form.appendChild(bookingIdInput);
    document.body.appendChild(form);
    form.submit();
}

// Reject booking function
function rejectBooking(bookingId) {
    console.log('❌ Rejecting booking:', bookingId);

    // Show loading state
    const rejectBtn = document.querySelector(`[data-booking-id="${bookingId}"].reject-btn`);
    if (rejectBtn) {
        rejectBtn.innerHTML = '⏳ Rejecting...';
        rejectBtn.disabled = true;
    }

    // Create and submit form
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = getContextPath() + '/admin/reject-booking';

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
    console.log('💬 Initializing message handling...');

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

    console.log('✅ Message handling initialized');
}

// Initialize quick actions with loading states
function initializeQuickActions() {
    console.log('⚡ Initializing quick actions...');

    const actionLinks = document.querySelectorAll('.quick-action, .stat-action, .alert-action, .empty-action');

    actionLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            if (!this.classList.contains('loading')) {
                const originalText = this.innerHTML;

                // Show loading state
                this.innerHTML = '⏳ Loading...';
                this.classList.add('loading');
                this.style.pointerEvents = 'none';

                // Allow navigation after short delay
                setTimeout(() => {
                    window.location.href = this.href;
                }, 300);
            }
        });
    });

    console.log('✅ Quick actions initialized');
}

// Update pending count (for real-time updates)
function updatePendingCount() {
    console.log('🔄 Checking for updated pending count...');

    // Example AJAX implementation (uncomment when backend supports it):
    /*
    fetch(getContextPath() + '/admin/api/pending-count')
        .then(response => response.json())
        .then(data => {
            if (data.pendingCount !== undefined) {
                updatePendingCountDisplay(data.pendingCount);
            }
        })
        .catch(error => {
            console.error('Error updating pending count:', error);
        });
    */
}

// Update pending count display
function updatePendingCountDisplay(newCount) {
    const pendingElements = document.querySelectorAll('.alert-number, .stat-number');

    pendingElements.forEach(element => {
        if (element.textContent !== newCount.toString()) {
            element.style.transform = 'scale(1.2)';
            element.textContent = newCount;

            setTimeout(() => {
                element.style.transform = 'scale(1)';
            }, 300);
        }
    });

    console.log('📊 Pending count updated to:', newCount);
}

// Cabin action card animations
function initializeCabinCardAnimations() {
    console.log('🏠 Initializing cabin card animations...');

    const cabinCards = document.querySelectorAll('.cabin-action-card');

    cabinCards.forEach((card, index) => {
        card.style.animationDelay = (index * 0.1) + 's';

        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-8px) scale(1.02)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });

    console.log('✅ Cabin card animations initialized');
}

// Table row hover effects
function initializeTableAnimations() {
    console.log('📊 Initializing table animations...');

    const tableRows = document.querySelectorAll('.table-row');

    tableRows.forEach(row => {
        row.addEventListener('mouseenter', function() {
            this.style.transform = 'translateX(5px)';
            this.style.boxShadow = '0 4px 15px rgba(0,0,0,0.1)';
        });

        row.addEventListener('mouseleave', function() {
            this.style.transform = 'translateX(0)';
            this.style.boxShadow = 'none';
        });
    });

    console.log('✅ Table animations initialized');
}

// ✅ TEMPORARY DEBUG FUNCTION - For testing dropdown
function testDropdown() {
    console.log('🧪 Testing dropdown manually...');

    const userDropdownMenu = document.getElementById('userDropdownMenu');
    if (userDropdownMenu) {
        console.log('✅ Found dropdown menu, toggling...');
        userDropdownMenu.classList.toggle('show');
        console.log('🔽 Dropdown state:', userDropdownMenu.classList.contains('show') ? 'OPEN' : 'CLOSED');
    } else {
        console.error('❌ Dropdown menu not found!');
    }
}

// Make test function available in browser console
window.testDropdown = testDropdown;

// Keyboard shortcuts for admin
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + P = Pending Bookings
    if ((e.ctrlKey || e.metaKey) && e.key === 'p') {
        e.preventDefault();
        window.location.href = getContextPath() + '/admin/bookings?filter=pending';
    }

    // Ctrl/Cmd + U = Users
    if ((e.ctrlKey || e.metaKey) && e.key === 'u') {
        e.preventDefault();
        window.location.href = getContextPath() + '/admin/users';
    }

    // Ctrl/Cmd + C = Cabins
    if ((e.ctrlKey || e.metaKey) && e.key === 'c') {
        e.preventDefault();
        window.location.href = getContextPath() + '/admin/manage-cabins';
    }

    // Ctrl/Cmd + A = Analytics
    if ((e.ctrlKey || e.metaKey) && e.key === 'a') {
        e.preventDefault();
        window.location.href = getContextPath() + '/admin/analytics';
    }
});

// Utility functions
function getContextPath() {
    const path = window.location.pathname;
    return path.substring(0, path.indexOf('/', 1)) || '';
}

// Admin dashboard utilities
const AdminDashboardUtils = {
    // Refresh dashboard data
    refreshDashboard: function() {
        console.log('🔄 Refreshing dashboard data...');
        window.location.reload();
    },

    // Quick navigation
    navigateTo: function(path) {
        window.location.href = getContextPath() + path;
    },

    // Show notification
    showNotification: function(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `message ${type}-message`;
        notification.textContent = message;

        const container = document.getElementById('message-container') || document.body;
        container.appendChild(notification);

        // Auto-remove after 5 seconds
        setTimeout(() => {
            if (notification.parentNode) {
                notification.style.opacity = '0';
                setTimeout(() => {
                    notification.parentNode.removeChild(notification);
                }, 300);
            }
        }, 5000);
    },

    // Bulk approve bookings (future feature)
    bulkApprove: function(bookingIds) {
        console.log('🔄 Bulk approving bookings:', bookingIds);
    },

    // Export dashboard data (future feature)
    exportDashboardData: function(format = 'csv') {
        console.log('📊 Exporting dashboard data in', format, 'format');
    }
};

// Make utilities available globally
window.AdminDashboardUtils = AdminDashboardUtils;

// Dashboard health check
function performHealthCheck() {
    const healthMetrics = {
        dropdownsInitialized: !!(document.getElementById('userDropdown') && document.getElementById('userDropdownMenu')),
        pendingCount: document.querySelector('.alert-number')?.textContent || '0',
        totalUsers: document.querySelector('.stat-number')?.textContent || '0',
        pageLoadTime: performance.now(),
        timestamp: new Date().toISOString()
    };

    console.log('🏥 Dashboard Health Check:', healthMetrics);
    return healthMetrics;
}

// Show success message
function showSuccessMessage(message) {
    AdminDashboardUtils.showNotification(message, 'success');
}

// Show error message
function showErrorMessage(message) {
    AdminDashboardUtils.showNotification(message, 'error');
}

console.log('🎯 Enhanced Admin Dashboard JavaScript loaded successfully');
