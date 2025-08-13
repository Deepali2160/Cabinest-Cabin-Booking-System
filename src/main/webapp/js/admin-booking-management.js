// ====================================
// ADMIN BOOKING MANAGEMENT - COMPLETE FIXED VERSION
// ====================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('📅 Admin Booking Management initialized for Yash Technology');

    // Initialize components
    initializeSearch();
    initializeBulkActions();
    initializeIndividualActions();
    initializeModals();
    initializeDropdowns();
    initializeMessageHandling();

    // Auto-hide messages after 5 seconds
    setTimeout(hideMessages, 5000);

    // Initialize tooltips if any
    initializeTooltips();
});

// Global variables
let currentBookingId = null;
let currentAction = null;
let selectedBookings = [];

// Initialize search functionality
function initializeSearch() {
    const searchInput = document.getElementById('searchInput');

    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase().trim();
            filterBookings(searchTerm);
        });
    }
}

// Filter bookings based on search term
function filterBookings(searchTerm) {
    const bookingRows = document.querySelectorAll('.booking-row');
    let visibleCount = 0;

    bookingRows.forEach(row => {
        const userName = (row.dataset.userName || '').toLowerCase();
        const cabinName = (row.dataset.cabinName || '').toLowerCase();
        const purpose = (row.dataset.purpose || '').toLowerCase();

        if (userName.includes(searchTerm) ||
            cabinName.includes(searchTerm) ||
            purpose.includes(searchTerm)) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });

    // Update visible count
    const visibleCountElement = document.getElementById('visibleCount');
    if (visibleCountElement) {
        visibleCountElement.textContent = visibleCount;
    }

    // Update bulk action buttons
    updateBulkActionButtons();

    console.log('🔍 Search filtered bookings:', visibleCount, 'visible for term:', searchTerm);
}

// Initialize bulk actions
function initializeBulkActions() {
    const selectAllCheckbox = document.getElementById('selectAll');
    const bookingCheckboxes = document.querySelectorAll('.booking-checkbox');
    const bulkApproveBtn = document.getElementById('bulkApproveBtn');
    const bulkRejectBtn = document.getElementById('bulkRejectBtn');
    const bulkActions = document.getElementById('bulkActions');

    // Select all functionality
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', function() {
            const isChecked = this.checked;

            bookingCheckboxes.forEach(checkbox => {
                const row = checkbox.closest('.booking-row');
                if (row && row.style.display !== 'none') {
                    checkbox.checked = isChecked;
                }
            });

            updateBulkActionButtons();
        });
    }

    // Individual checkbox listeners
    bookingCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            updateBulkActionButtons();
            updateSelectAllState();
        });
    });

    // Bulk approve button
    if (bulkApproveBtn) {
        bulkApproveBtn.addEventListener('click', function() {
            const selectedIds = getSelectedBookingIds();
            if (selectedIds.length > 0) {
                showBulkConfirmModal('Bulk Approve', selectedIds.length, 'approve');
            }
        });
    }

    // Bulk reject button
    if (bulkRejectBtn) {
        bulkRejectBtn.addEventListener('click', function() {
            const selectedIds = getSelectedBookingIds();
            if (selectedIds.length > 0) {
                showBulkConfirmModal('Bulk Reject', selectedIds.length, 'reject');
            }
        });
    }
}

// Update bulk action buttons state
function updateBulkActionButtons() {
    const selectedIds = getSelectedBookingIds();
    const hasSelection = selectedIds.length > 0;

    const bulkApproveBtn = document.getElementById('bulkApproveBtn');
    const bulkRejectBtn = document.getElementById('bulkRejectBtn');
    const bulkActions = document.getElementById('bulkActions');

    if (bulkApproveBtn) bulkApproveBtn.disabled = !hasSelection;
    if (bulkRejectBtn) bulkRejectBtn.disabled = !hasSelection;

    // Show/hide bulk actions container
    if (bulkActions) {
        bulkActions.style.display = hasSelection ? 'flex' : 'none';
    }

    selectedBookings = selectedIds;

    console.log('📊 Bulk actions updated - Selected:', selectedIds.length);
}

// Update select all checkbox state
function updateSelectAllState() {
    const selectAllCheckbox = document.getElementById('selectAll');
    const visibleCheckboxes = getVisibleCheckboxes();
    const checkedVisibleCheckboxes = visibleCheckboxes.filter(cb => cb.checked);

    if (selectAllCheckbox && visibleCheckboxes.length > 0) {
        selectAllCheckbox.indeterminate = checkedVisibleCheckboxes.length > 0 &&
                                         checkedVisibleCheckboxes.length < visibleCheckboxes.length;
        selectAllCheckbox.checked = checkedVisibleCheckboxes.length === visibleCheckboxes.length;
    }
}

// Get visible checkboxes
function getVisibleCheckboxes() {
    const checkboxes = document.querySelectorAll('.booking-checkbox');
    return Array.from(checkboxes).filter(checkbox => {
        const row = checkbox.closest('.booking-row');
        return row && row.style.display !== 'none';
    });
}

// Get selected booking IDs
function getSelectedBookingIds() {
    const checkedBoxes = document.querySelectorAll('.booking-checkbox:checked');
    return Array.from(checkedBoxes)
        .filter(checkbox => {
            const row = checkbox.closest('.booking-row');
            return row && row.style.display !== 'none';
        })
        .map(checkbox => checkbox.value);
}

// Initialize individual actions
function initializeIndividualActions() {
    // Approve buttons
    const approveButtons = document.querySelectorAll('.approve-single');
    approveButtons.forEach(button => {
        button.addEventListener('click', function() {
            const bookingId = this.dataset.bookingId;
            const userName = this.dataset.userName;

            if (!bookingId) {
                console.error('❌ No booking ID found!');
                showNotification('❌ Error: Booking ID not found', 'error');
                return;
            }

            currentBookingId = bookingId;
            currentAction = 'approve';

            showConfirmModal(
                '✅ Approve Booking',
                '⚠️',
                'Are you sure you want to approve the booking for ' + userName + '?',
                function() {
                    processIndividualAction('approve', bookingId);
                }
            );
        });
    });

    // Reject buttons
    const rejectButtons = document.querySelectorAll('.reject-single');
    rejectButtons.forEach(button => {
        button.addEventListener('click', function() {
            const bookingId = this.dataset.bookingId;
            const userName = this.dataset.userName;

            if (!bookingId) {
                console.error('❌ No booking ID found!');
                showNotification('❌ Error: Booking ID not found', 'error');
                return;
            }

            currentBookingId = bookingId;
            currentAction = 'reject';

            showConfirmModal(
                '❌ Reject Booking',
                '⚠️',
                'Are you sure you want to reject the booking for ' + userName + '?',
                function() {
                    processIndividualAction('reject', bookingId);
                }
            );
        });
    });
}

// ✅ FIXED: Process individual action
function processIndividualAction(action, bookingId) {
    console.log('🔄 Processing', action, 'for booking:', bookingId);

    // Show loading state on button
    const button = document.querySelector('[data-booking-id="' + bookingId + '"].' + action + '-single');
    if (button) {
        showButtonLoading(button);
    }

    // ✅ CORRECT: Send POST request to /admin/bookings
    const contextPath = getContextPath();
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = contextPath + '/admin/bookings';
    form.style.display = 'none';

    // Add hidden inputs
    const actionInput = document.createElement('input');
    actionInput.type = 'hidden';
    actionInput.name = 'action';
    actionInput.value = action;

    const bookingIdInput = document.createElement('input');
    bookingIdInput.type = 'hidden';
    bookingIdInput.name = 'bookingId';
    bookingIdInput.value = bookingId;

    form.appendChild(actionInput);
    form.appendChild(bookingIdInput);

    // Submit form
    document.body.appendChild(form);
    console.log('🔗 Submitting form to:', form.action, 'with action:', action, 'and bookingId:', bookingId);
    form.submit();
}

// Show button loading state
function showButtonLoading(button) {
    button.disabled = true;
    button.classList.add('loading');
    button.innerHTML = '⏳';
}

// Initialize modals
function initializeModals() {
    // Confirm modal elements
    const confirmModal = document.getElementById('confirmModal');
    const confirmModalClose = document.getElementById('confirmModalClose');
    const cancelAction = document.getElementById('cancelAction');
    const confirmActionBtn = document.getElementById('confirmActionBtn');

    // Bulk confirm modal elements
    const bulkConfirmModal = document.getElementById('bulkConfirmModal');
    const bulkModalClose = document.getElementById('bulkModalClose');
    const cancelBulkAction = document.getElementById('cancelBulkAction');
    const bulkConfirmBtn = document.getElementById('bulkConfirmBtn');

    // Close confirm modal
    [confirmModalClose, cancelAction].forEach(btn => {
        if (btn) {
            btn.addEventListener('click', function() {
                hideConfirmModal();
            });
        }
    });

    // Close bulk modal
    [bulkModalClose, cancelBulkAction].forEach(btn => {
        if (btn) {
            btn.addEventListener('click', function() {
                hideBulkConfirmModal();
            });
        }
    });

    // Close modals on overlay click
    [confirmModal, bulkConfirmModal].forEach(modal => {
        if (modal) {
            modal.addEventListener('click', function(e) {
                if (e.target === modal) {
                    if (modal.id === 'confirmModal') {
                        hideConfirmModal();
                    } else {
                        hideBulkConfirmModal();
                    }
                }
            });
        }
    });

    // Close modals on escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            hideConfirmModal();
            hideBulkConfirmModal();
        }
    });
}

// Show confirmation modal
function showConfirmModal(title, icon, message, callback) {
    const modal = document.getElementById('confirmModal');
    const modalTitle = document.getElementById('confirmModalTitle');
    const modalIcon = document.getElementById('modalIcon');
    const modalBody = document.getElementById('confirmModalBody');
    const confirmBtn = document.getElementById('confirmActionBtn');

    if (modalTitle) modalTitle.textContent = title;
    if (modalIcon) modalIcon.textContent = icon;
    if (modalBody) modalBody.textContent = message;

    if (confirmBtn) {
        // Update button appearance based on action
        if (title.includes('Approve')) {
            confirmBtn.className = 'modal-btn primary';
            confirmBtn.innerHTML = '✅ Approve';
        } else if (title.includes('Reject')) {
            confirmBtn.className = 'modal-btn danger';
            confirmBtn.innerHTML = '❌ Reject';
            confirmBtn.style.background = '#e74c3c';
        }

        // Set click handler
        confirmBtn.onclick = function() {
            hideConfirmModal();
            if (callback) callback();
        };
    }

    modal.classList.add('show');
    document.body.style.overflow = 'hidden';

    console.log('📋 Confirmation modal shown:', title);
}

// Hide confirmation modal
function hideConfirmModal() {
    const modal = document.getElementById('confirmModal');
    modal.classList.remove('show');
    document.body.style.overflow = '';

    currentBookingId = null;
    currentAction = null;

    console.log('❌ Confirmation modal hidden');
}

// Show bulk confirmation modal
function showBulkConfirmModal(title, count, type) {
    const modal = document.getElementById('bulkConfirmModal');
    const modalTitle = document.getElementById('bulkModalTitle');
    const selectedCount = document.getElementById('selectedCount');
    const confirmBtn = document.getElementById('bulkConfirmBtn');

    if (modalTitle) modalTitle.textContent = title;
    if (selectedCount) selectedCount.textContent = count;

    if (confirmBtn) {
        if (type === 'approve') {
            confirmBtn.className = 'modal-btn primary';
            confirmBtn.innerHTML = '✅ Approve All Selected';
        } else {
            confirmBtn.className = 'modal-btn danger';
            confirmBtn.innerHTML = '❌ Reject All Selected';
            confirmBtn.style.background = '#e74c3c';
        }

        confirmBtn.onclick = function() {
            processBulkAction(type);
        };
    }

    modal.classList.add('show');
    document.body.style.overflow = 'hidden';

    console.log('📋 Bulk confirmation modal shown:', title, 'for', count, 'bookings');
}

// Hide bulk confirmation modal
function hideBulkConfirmModal() {
    const modal = document.getElementById('bulkConfirmModal');
    modal.classList.remove('show');
    document.body.style.overflow = '';

    console.log('❌ Bulk confirmation modal hidden');
}

// ✅ FIXED: Process bulk action
function processBulkAction(type) {
    const selectedIds = getSelectedBookingIds();

    if (selectedIds.length === 0) {
        showNotification('❌ No bookings selected', 'error');
        return;
    }

    console.log('🔄 Processing bulk', type, 'for', selectedIds.length, 'bookings');

    hideBulkConfirmModal();

    // ✅ CORRECT: Create form for bulk action
    const form = document.getElementById('bulkActionForm');
    const contextPath = getContextPath();

    // Set correct action and method
    form.action = contextPath + '/admin/bookings';
    form.method = 'POST';

    // Add action input
    let actionInput = form.querySelector('input[name="action"]');
    if (!actionInput) {
        actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        form.appendChild(actionInput);
    }

    // Set bulk action value
    actionInput.value = type === 'approve' ? 'bulkApprove' : 'bulkReject';

    // Show loading state
    showNotification('⏳ Processing ' + type + ' for ' + selectedIds.length + ' bookings...', 'info');

    console.log('🔗 Submitting bulk form to:', form.action, 'with action:', actionInput.value);
    form.submit();
}

// Initialize dropdown functionality
function initializeDropdowns() {
    const userDropdown = document.getElementById('userDropdown');
    const userDropdownMenu = document.getElementById('userDropdownMenu');

    if (userDropdown && userDropdownMenu) {
        userDropdown.addEventListener('click', function(e) {
            e.stopPropagation();
            userDropdownMenu.classList.toggle('show');
        });

        // Close user dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!userDropdown.contains(e.target) && !userDropdownMenu.contains(e.target)) {
                userDropdownMenu.classList.remove('show');
            }
        });
    }
}

// Initialize message handling
function initializeMessageHandling() {
    const messages = document.querySelectorAll('.message');

    messages.forEach(message => {
        // Add close button
        const closeBtn = document.createElement('button');
        closeBtn.innerHTML = '×';
        closeBtn.className = 'message-close';
        closeBtn.style.cssText = `
            background: none;
            border: none;
            color: inherit;
            font-size: 18px;
            font-weight: bold;
            float: right;
            cursor: pointer;
            padding: 0;
            margin-left: 10px;
        `;

        closeBtn.addEventListener('click', function() {
            message.style.opacity = '0';
            setTimeout(() => {
                if (message.parentNode) {
                    message.parentNode.removeChild(message);
                }
            }, 300);
        });

        message.appendChild(closeBtn);
    });
}

// Hide messages automatically
function hideMessages() {
    const messages = document.querySelectorAll('.message');
    messages.forEach(message => {
        message.style.opacity = '0';
        setTimeout(() => {
            if (message.parentNode) {
                message.parentNode.removeChild(message);
            }
        }, 300);
    });
}

// Initialize tooltips
function initializeTooltips() {
    const tooltipElements = document.querySelectorAll('[title]');

    tooltipElements.forEach(element => {
        element.addEventListener('mouseenter', function() {
            const title = this.getAttribute('title');
            if (title) {
                showTooltip(this, title);
            }
        });

        element.addEventListener('mouseleave', function() {
            hideTooltip();
        });
    });
}

// Show tooltip
function showTooltip(element, text) {
    // Simple tooltip implementation
    let tooltip = document.getElementById('customTooltip');

    if (!tooltip) {
        tooltip = document.createElement('div');
        tooltip.id = 'customTooltip';
        tooltip.style.cssText = `
            position: absolute;
            background: #2c3e50;
            color: white;
            padding: 8px 12px;
            border-radius: 6px;
            font-size: 12px;
            z-index: 9999;
            opacity: 0;
            transition: opacity 0.3s ease;
            pointer-events: none;
        `;
        document.body.appendChild(tooltip);
    }

    tooltip.textContent = text;

    const rect = element.getBoundingClientRect();
    tooltip.style.left = (rect.left + rect.width / 2 - tooltip.offsetWidth / 2) + 'px';
    tooltip.style.top = (rect.top - tooltip.offsetHeight - 8) + 'px';
    tooltip.style.opacity = '1';
}

// Hide tooltip
function hideTooltip() {
    const tooltip = document.getElementById('customTooltip');
    if (tooltip) {
        tooltip.style.opacity = '0';
    }
}

// Show notification
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        background: ${type === 'success' ? '#27ae60' : type === 'error' ? '#e74c3c' : '#3498db'};
        color: white;
        border-radius: 8px;
        font-weight: 600;
        z-index: 9999;
        opacity: 0;
        transform: translateX(100%);
        transition: all 0.3s ease;
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.opacity = '1';
        notification.style.transform = 'translateX(0)';
    }, 100);

    setTimeout(() => {
        notification.style.opacity = '0';
        notification.style.transform = 'translateX(100%)';

        setTimeout(() => {
            if (document.body.contains(notification)) {
                document.body.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// Utility functions
function getContextPath() {
    const path = window.location.pathname;
    return path.substring(0, path.indexOf('/', 1)) || '';
}

// Auto-refresh functionality
function initializeAutoRefresh() {
    // Auto-refresh pending count every 30 seconds
    setInterval(function() {
        const currentUrl = window.location.href;
        if (currentUrl.includes('filter=pending') ||
            (!currentUrl.includes('filter=') && currentUrl.includes('/admin/bookings'))) {
            console.log('🔄 Checking for new pending bookings...');
            // Could implement AJAX refresh here
            checkForNewBookings();
        }
    }, 30000);
}

// Check for new bookings (AJAX implementation)
function checkForNewBookings() {
    // Implementation for AJAX call to check new bookings
    console.log('📡 Checking for new bookings via AJAX...');
    // This would be implemented based on your backend API
}

// Keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + A = Select all visible
    if ((e.ctrlKey || e.metaKey) && e.key === 'a') {
        const selectAllCheckbox = document.getElementById('selectAll');
        if (selectAllCheckbox && document.activeElement.tagName !== 'INPUT') {
            e.preventDefault();
            selectAllCheckbox.checked = !selectAllCheckbox.checked;
            selectAllCheckbox.dispatchEvent(new Event('change'));
        }
    }

    // Ctrl/Cmd + R = Refresh page
    if ((e.ctrlKey || e.metaKey) && e.key === 'r') {
        // Allow default refresh behavior
        console.log('🔄 Manual page refresh');
    }
});

// Initialize auto-refresh
initializeAutoRefresh();

// Booking management utilities
const BookingManagementUtils = {
    // Get booking statistics
    getBookingStatistics: function() {
        const pendingCount = document.querySelectorAll('.status-badge.pending').length;
        const approvedCount = document.querySelectorAll('.status-badge.approved').length;
        const rejectedCount = document.querySelectorAll('.status-badge.rejected').length;
        const vipCount = document.querySelectorAll('.priority-badge.vip').length;

        return {
            pending: pendingCount,
            approved: approvedCount,
            rejected: rejectedCount,
            vip: vipCount,
            total: pendingCount + approvedCount + rejectedCount
        };
    },

    // Filter by date range
    filterByDateRange: function(startDate, endDate) {
        console.log('📅 Filtering by date range:', startDate, 'to', endDate);
        // Implementation for date range filtering
    },

    // Export bookings data
    exportBookings: function(format = 'csv') {
        console.log('📊 Exporting bookings in', format, 'format');
        // Implementation for export functionality
    }
};

// Make utilities available globally
window.BookingManagementUtils = BookingManagementUtils;

console.log('🎯 ✅ FIXED Booking Management JavaScript loaded successfully - 404 Error Resolved!');
