// ====================================
// ADMIN MANAGE CABINS - MINIMAL FUNCTIONALITY
// ====================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('🏠 Admin Cabin Management initialized for Yash Technology');

    // Initialize components
    initializeDropdowns();
    initializeStatusActions();
    initializeDeleteActions();
    initializeMessageHandling();
    initializeModals();

    // Auto-hide messages after 5 seconds
    setTimeout(hideMessages, 5000);
});

// Global variables for modals
let currentCabinId = null;
let currentCabinName = null;
let currentStatus = null;

// Initialize dropdown functionality
function initializeDropdowns() {
    // User dropdown
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

    // Status dropdowns for each cabin
    const statusButtons = document.querySelectorAll('[id^="statusBtn-"]');
    statusButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.stopPropagation();
            const cabinId = this.id.replace('statusBtn-', '');
            const menu = document.getElementById('statusMenu-' + cabinId);

            // Close all other dropdowns
            document.querySelectorAll('.dropdown-menu').forEach(m => {
                if (m !== menu) m.classList.remove('show');
            });

            // Toggle current dropdown
            menu.classList.toggle('show');
        });
    });

    // Close dropdowns when clicking outside
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.status-dropdown')) {
            document.querySelectorAll('.dropdown-menu').forEach(menu => {
                menu.classList.remove('show');
            });
        }
    });

    // Close dropdowns on escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            document.querySelectorAll('.dropdown-menu').forEach(menu => {
                menu.classList.remove('show');
            });
        }
    });
}

// Initialize status change actions
function initializeStatusActions() {
    const statusChangeButtons = document.querySelectorAll('.status-change');

    statusChangeButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();

            currentCabinId = this.dataset.cabinId;
            currentCabinName = this.dataset.cabinName;
            currentStatus = this.dataset.status;

            console.log('🔧 Status change requested:', currentCabinName, 'to', currentStatus);

            showStatusModal();
        });
    });
}

// Initialize delete actions
function initializeDeleteActions() {
    const deleteButtons = document.querySelectorAll('.delete-cabin');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            currentCabinId = this.dataset.cabinId;
            currentCabinName = this.dataset.cabinName;

            console.log('🗑️ Delete requested for cabin:', currentCabinName);

            showDeleteModal();
        });
    });
}

// Show status change modal
function showStatusModal() {
    const modal = document.getElementById('statusModal');
    const cabinNameSpan = document.getElementById('statusCabinName');
    const statusDisplay = document.getElementById('newStatusDisplay');
    const statusWarning = document.getElementById('statusWarning');
    const warningText = document.getElementById('warningText');

    if (cabinNameSpan) cabinNameSpan.textContent = currentCabinName;

    // Set status display with appropriate styling
    if (statusDisplay) {
        let statusText, className;

        switch(currentStatus) {
            case 'ACTIVE':
                statusText = '✅ Active';
                className = 'active';
                break;
            case 'MAINTENANCE':
                statusText = '🔧 Maintenance';
                className = 'maintenance';
                break;
            case 'INACTIVE':
                statusText = '❌ Inactive';
                className = 'inactive';
                break;
        }

        statusDisplay.textContent = statusText;
        statusDisplay.className = 'status-preview ' + className;
    }

    // Show warnings for specific status changes
    if (currentStatus === 'MAINTENANCE') {
        statusWarning.style.display = 'block';
        warningText.textContent = 'Setting cabin to maintenance will cancel all active bookings.';
    } else if (currentStatus === 'INACTIVE') {
        statusWarning.style.display = 'block';
        warningText.textContent = 'Inactive cabins cannot be booked by users.';
    } else {
        statusWarning.style.display = 'none';
    }

    modal.classList.add('show');
    document.body.style.overflow = 'hidden';

    console.log('📋 Status change modal shown');
}

// Show delete confirmation modal
function showDeleteModal() {
    const modal = document.getElementById('deleteModal');
    const cabinNameSpan = document.getElementById('deleteCabinName');

    if (cabinNameSpan) cabinNameSpan.textContent = currentCabinName;

    modal.classList.add('show');
    document.body.style.overflow = 'hidden';

    console.log('🗑️ Delete confirmation modal shown');
}

// Hide modals
function hideModal(modalId) {
    const modal = document.getElementById(modalId);
    modal.classList.remove('show');
    document.body.style.overflow = '';

    // Reset values
    currentCabinId = null;
    currentCabinName = null;
    currentStatus = null;

    console.log('❌ Modal hidden:', modalId);
}

// Initialize modal functionality
function initializeModals() {
    // Status Modal
    const statusModal = document.getElementById('statusModal');
    const statusModalClose = document.getElementById('statusModalClose');
    const cancelStatusChange = document.getElementById('cancelStatusChange');
    const confirmStatusChange = document.getElementById('confirmStatusChange');

    // Close buttons for status modal
    [statusModalClose, cancelStatusChange].forEach(btn => {
        if (btn) {
            btn.addEventListener('click', function() {
                hideModal('statusModal');
            });
        }
    });

    // Confirm status change
    if (confirmStatusChange) {
        confirmStatusChange.addEventListener('click', function() {
            processStatusChange();
        });
    }

    // Delete Modal
    const deleteModal = document.getElementById('deleteModal');
    const deleteModalClose = document.getElementById('deleteModalClose');
    const cancelDelete = document.getElementById('cancelDelete');
    const confirmDelete = document.getElementById('confirmDelete');

    // Close buttons for delete modal
    [deleteModalClose, cancelDelete].forEach(btn => {
        if (btn) {
            btn.addEventListener('click', function() {
                hideModal('deleteModal');
            });
        }
    });

    // Confirm delete
    if (confirmDelete) {
        confirmDelete.addEventListener('click', function() {
            processDelete();
        });
    }

    // Close modals on overlay click
    [statusModal, deleteModal].forEach(modal => {
        if (modal) {
            modal.addEventListener('click', function(e) {
                if (e.target === modal) {
                    hideModal(modal.id);
                }
            });
        }
    });

    // Close modals on escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            if (statusModal.classList.contains('show')) {
                hideModal('statusModal');
            }
            if (deleteModal.classList.contains('show')) {
                hideModal('deleteModal');
            }
        }
    });
}

// Process status change
function processStatusChange() {
    if (!currentCabinId || !currentStatus) {
        console.error('❌ Missing status change data');
        return;
    }

    console.log('🔧 Processing status change:', currentCabinId, 'to', currentStatus);

    // Show loading state
    const confirmBtn = document.getElementById('confirmStatusChange');
    if (confirmBtn) {
        confirmBtn.innerHTML = '⏳ Changing...';
        confirmBtn.disabled = true;
    }

    // Create and submit form
    const form = document.getElementById('statusForm');
    document.getElementById('statusCabinId').value = currentCabinId;
    document.getElementById('statusValue').value = currentStatus;

    form.submit();
}

// Process delete
function processDelete() {
    if (!currentCabinId) {
        console.error('❌ Missing delete data');
        return;
    }

    console.log('🗑️ Processing delete:', currentCabinId);

    // Show loading state
    const confirmBtn = document.getElementById('confirmDelete');
    if (confirmBtn) {
        confirmBtn.innerHTML = '⏳ Deleting...';
        confirmBtn.disabled = true;
    }

    // Create and submit form
    const form = document.getElementById('deleteForm');
    document.getElementById('deleteCabinId').value = currentCabinId;

    form.submit();
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

// Initialize table row animations
function initializeTableAnimations() {
    const tableRows = document.querySelectorAll('.table-row');

    tableRows.forEach((row, index) => {
        row.style.animationDelay = (index * 0.1) + 's';

        row.addEventListener('mouseenter', function() {
            this.style.transform = 'translateX(10px)';
            this.style.boxShadow = '0 6px 20px rgba(0,0,0,0.15)';
        });

        row.addEventListener('mouseleave', function() {
            this.style.transform = 'translateX(0)';
            this.style.boxShadow = 'none';
        });
    });
}

// Initialize stat card animations
function initializeStatAnimations() {
    const statCards = document.querySelectorAll('.stat-card');

    statCards.forEach((card, index) => {
        card.style.animationDelay = (index * 0.15) + 's';

        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-8px) scale(1.02)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });
}

// Initialize animations
initializeTableAnimations();
initializeStatAnimations();

// Keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + N = Add New Cabin
    if ((e.ctrlKey || e.metaKey) && e.key === 'n') {
        e.preventDefault();
        window.location.href = getContextPath() + '/admin/add-cabin';
    }

    // Ctrl/Cmd + H = Dashboard
    if ((e.ctrlKey || e.metaKey) && e.key === 'h') {
        e.preventDefault();
        window.location.href = getContextPath() + '/admin/dashboard';
    }

    // Ctrl/Cmd + R = Refresh page
    if ((e.ctrlKey || e.metaKey) && e.key === 'r') {
        e.preventDefault();
        window.location.reload();
    }
});

// Utility functions
function getContextPath() {
    const path = window.location.pathname;
    return path.substring(0, path.indexOf('/', 1)) || '';
}

// Cabin management utilities
const CabinManagementUtils = {
    // Get cabin statistics
    getCabinStatistics: function() {
        const totalCabins = document.querySelectorAll('.table-row').length;
        const activeCabins = document.querySelectorAll('.status-badge.active').length;
        const vipCabins = document.querySelectorAll('.access-badge.vip').length;
        const maintenanceCabins = document.querySelectorAll('.status-badge.maintenance').length;

        return {
            total: totalCabins,
            active: activeCabins,
            vip: vipCabins,
            maintenance: maintenanceCabins
        };
    },

    // Bulk operations (future feature)
    bulkStatusChange: function(cabinIds, status) {
        console.log('🔄 Bulk status change:', cabinIds, 'to', status);
        // Implementation for bulk operations
    },

    // Export cabin data (future feature)
    exportCabinData: function(format = 'csv') {
        console.log('📊 Exporting cabin data in', format, 'format');
        // Implementation for export functionality
    },

    // Advanced filtering
    advancedFilter: function(criteria) {
        console.log('🔍 Advanced filtering with criteria:', criteria);
        // Implementation for advanced filtering
    }
};

// Make utilities available globally
window.CabinManagementUtils = CabinManagementUtils;

// Performance monitoring
function performHealthCheck() {
    const healthMetrics = {
        totalCabins: document.querySelectorAll('.table-row').length,
        activeCabins: document.querySelectorAll('.status-badge.active').length,
        pageLoadTime: performance.now(),
        timestamp: new Date().toISOString()
    };

    console.log('🏥 Cabin Management Health Check:', healthMetrics);
    return healthMetrics;
}

// Perform initial health check
performHealthCheck();

// Loading state management
function showLoading(element, text = 'Loading...') {
    if (element) {
        element.disabled = true;
        element.innerHTML = '⏳ ' + text;
    }
}

function hideLoading(element, originalText) {
    if (element) {
        element.disabled = false;
        element.innerHTML = originalText;
    }
}

console.log('🎯 Cabin Management JavaScript loaded successfully');
