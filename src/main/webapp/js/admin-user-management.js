// ====================================
// ADMIN USER MANAGEMENT - YASH TECHNOLOGY - UPDATED VERSION
// ====================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('👥 Admin User Management initialized for Yash Technology');

    // Initialize components
    initializeTabSystem();
    initializeSearch();
    initializeDropdowns();
    initializePromotions();
    initializeUserActions();
});

// Global variables
let currentPromotionUserId = null;
let currentPromotionType = null;

// Initialize tab system
function initializeTabSystem() {
    const tabBtns = document.querySelectorAll('.tab-btn');
    const tabPanels = document.querySelectorAll('.tab-panel');

    tabBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const tabId = this.dataset.tab;

            // Remove active class from all tabs
            tabBtns.forEach(t => t.classList.remove('active'));
            tabPanels.forEach(panel => panel.classList.remove('active'));

            // Add active class to clicked tab
            this.classList.add('active');

            // Show corresponding panel
            const targetPanel = document.getElementById(tabId + '-tab');
            if (targetPanel) {
                targetPanel.classList.add('active');
            }

            console.log('📋 Tab switched to:', tabId);
        });
    });
}

// Initialize search functionality
function initializeSearch() {
    const searchInput = document.getElementById('userSearch');

    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase().trim();
            filterUsers(searchTerm);
        });
    }
}

// Filter users based on search term
function filterUsers(searchTerm) {
    const userRows = document.querySelectorAll('.user-row');
    const userCards = document.querySelectorAll('.user-card');

    let visibleCount = 0;

    // Filter table rows
    userRows.forEach(row => {
        const userName = row.dataset.userName.toLowerCase();
        const userEmail = row.dataset.userEmail.toLowerCase();

        if (userName.includes(searchTerm) || userEmail.includes(searchTerm)) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });

    // Filter user cards
    userCards.forEach(card => {
        const userName = card.querySelector('h4').textContent.toLowerCase();
        const userEmail = card.querySelector('p').textContent.toLowerCase();

        if (userName.includes(searchTerm) || userEmail.includes(searchTerm)) {
            card.style.display = '';
        } else {
            card.style.display = 'none';
        }
    });

    console.log('🔍 Search filtered users:', visibleCount, 'visible for term:', searchTerm);
}

// Initialize dropdown functionality
function initializeDropdowns() {
    const quickActionsBtn = document.getElementById('quickActionsBtn');
    const quickActionsMenu = document.getElementById('quickActionsMenu');

    if (quickActionsBtn && quickActionsMenu) {
        quickActionsBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            quickActionsMenu.classList.toggle('show');
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!quickActionsBtn.contains(e.target) && !quickActionsMenu.contains(e.target)) {
                quickActionsMenu.classList.remove('show');
            }
        });

        // Handle dropdown actions
        const actionItems = quickActionsMenu.querySelectorAll('.action-item');
        actionItems.forEach(item => {
            item.addEventListener('click', function() {
                const action = this.dataset.action;
                handleQuickAction(action);
                quickActionsMenu.classList.remove('show');
            });
        });
    }

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
}

// Handle quick actions
function handleQuickAction(action) {
    console.log('⚡ Quick action triggered:', action);

    switch(action) {
        case 'promote-admin':
            console.log('👨‍💼 Promote to Admin action');
            break;
        case 'promote-vip':
            console.log('⭐ Promote to VIP action');
            break;
        case 'export':
            exportUsers();
            break;
        default:
            console.log('Unknown action:', action);
    }
}

// Initialize promotion functionality
function initializePromotions() {
    const modal = document.getElementById('promotionModal');
    const modalClose = document.getElementById('modalClose');
    const cancelBtn = document.getElementById('cancelPromotion');
    const confirmBtn = document.getElementById('confirmPromotion');

    // Promote to VIP buttons
    const promoteVipBtns = document.querySelectorAll('.promote-vip');
    promoteVipBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const userId = this.dataset.userId;
            const userName = this.dataset.userName;

            console.log('⭐ VIP Promote clicked for user:', userId, userName);

            currentPromotionUserId = userId;
            currentPromotionType = 'VIP';

            showPromotionModal(
                userName,
                'VIP User',
                'VIP users get priority booking approval and access to VIP-only cabins.'
            );
        });
    });

    // Promote to Admin buttons
    const promoteAdminBtns = document.querySelectorAll('.promote-admin');
    promoteAdminBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const userId = this.dataset.userId;
            const userName = this.dataset.userName;

            console.log('👨‍💼 Admin Promote clicked for user:', userId, userName);

            currentPromotionUserId = userId;
            currentPromotionType = 'ADMIN';

            showPromotionModal(
                userName,
                'Administrator',
                'Administrators can manage bookings, cabins, and promote other users.'
            );
        });
    });

    // Card action buttons (VIP)
    const cardVipButtons = document.querySelectorAll('.card-action-btn.vip');
    cardVipButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const userId = this.dataset.userId;
            const userName = this.dataset.userName;

            console.log('⭐ Card VIP Promote clicked for user:', userId, userName);

            currentPromotionUserId = userId;
            currentPromotionType = 'VIP';

            showPromotionModal(
                userName,
                'VIP User',
                'VIP users get priority booking approval and access to VIP-only cabins.'
            );
        });
    });

    // Card action buttons (Admin)
    const cardAdminButtons = document.querySelectorAll('.card-action-btn.admin');
    cardAdminButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const userId = this.dataset.userId;
            const userName = this.dataset.userName;

            console.log('👨‍💼 Card Admin Promote clicked for user:', userId, userName);

            currentPromotionUserId = userId;
            currentPromotionType = 'ADMIN';

            showPromotionModal(
                userName,
                'Administrator',
                'Administrators can manage bookings, cabins, and promote other users.'
            );
        });
    });

    // Modal close events
    [modalClose, cancelBtn].forEach(btn => {
        if (btn) {
            btn.addEventListener('click', function() {
                hidePromotionModal();
            });
        }
    });

    // Confirm promotion
    if (confirmBtn) {
        confirmBtn.addEventListener('click', function() {
            processPromotion();
        });
    }

    // Close modal on overlay click
    if (modal) {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                hidePromotionModal();
            }
        });
    }

    // Close modal on escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && modal.classList.contains('show')) {
            hidePromotionModal();
        }
    });

    console.log('✅ Promotion system initialized:', {
        vipButtons: promoteVipBtns.length,
        adminButtons: promoteAdminBtns.length,
        cardVipButtons: cardVipButtons.length,
        cardAdminButtons: cardAdminButtons.length
    });
}

// Show promotion modal
function showPromotionModal(userName, newRole, description) {
    const modal = document.getElementById('promotionModal');
    const modalTitle = document.getElementById('promotionModalTitle');
    const userNameSpan = document.getElementById('promotionUserName');
    const newRoleSpan = document.getElementById('promotionNewRole');
    const descriptionDiv = document.getElementById('promotionDescription');

    if (!modal) {
        console.error('❌ Promotion modal not found!');
        // Fallback: direct promotion with confirm
        if (confirm(`Are you sure you want to promote ${userName} to ${newRole}?`)) {
            processPromotion();
        }
        return;
    }

    // Update modal content
    if (modalTitle) {
        modalTitle.innerHTML = currentPromotionType === 'VIP' ? '⭐ Promote to VIP' : '👨‍💼 Promote to Admin';
    }
    if (userNameSpan) userNameSpan.textContent = userName;
    if (newRoleSpan) newRoleSpan.textContent = newRole;
    if (descriptionDiv) descriptionDiv.textContent = description;

    // Show modal
    modal.classList.add('show');
    document.body.style.overflow = 'hidden';

    console.log('📋 Promotion modal shown for:', userName, 'to', newRole);
}

// Hide promotion modal
function hidePromotionModal() {
    const modal = document.getElementById('promotionModal');
    if (modal) {
        modal.classList.remove('show');
        document.body.style.overflow = '';
    }

    // Reset button state
    const confirmBtn = document.getElementById('confirmPromotion');
    if (confirmBtn) {
        confirmBtn.innerHTML = '🚀 Promote User';
        confirmBtn.disabled = false;
    }

    // Reset values
    currentPromotionUserId = null;
    currentPromotionType = null;

    console.log('❌ Promotion modal hidden');
}

// ✅ FIXED: Process user promotion with GET request
function processPromotion() {
    if (!currentPromotionUserId || !currentPromotionType) {
        console.error('❌ Missing promotion data');
        return;
    }

    console.log('🚀 Processing promotion:', currentPromotionUserId, 'to', currentPromotionType);

    // Show loading state
    const confirmBtn = document.getElementById('confirmPromotion');
    if (confirmBtn) {
        confirmBtn.innerHTML = '⏳ Promoting...';
        confirmBtn.disabled = true;
    }

    // ✅ CORRECT: GET request with URL parameters
    const contextPath = getContextPath();
    const role = currentPromotionType === 'VIP' ? 'vip' : 'admin';
    const url = `${contextPath}/admin/promote-user?userId=${currentPromotionUserId}&role=${role}`;

    console.log('🔗 Redirecting to:', url);

    // Hide modal first
    hidePromotionModal();

    // Show loading notification
    showNotification('⏳ Processing promotion...', 'info');

    // Redirect to promotion URL
    window.location.href = url;
}

// Initialize user actions
function initializeUserActions() {
    // View details buttons
    const viewDetailsBtns = document.querySelectorAll('.view-details');
    viewDetailsBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const userId = this.dataset.userId;
            viewUserDetails(userId);
        });
    });
}

// View user details
function viewUserDetails(userId) {
    console.log('👁️ Viewing details for user:', userId);
    // This could open a modal or navigate to user details page
    // For now, just log the action
    showNotification('👁️ User details feature coming soon for user ID: ' + userId, 'info');
}

// Export users functionality
function exportUsers() {
    console.log('📊 Exporting users data...');

    try {
        const csvContent = generateUserCSV();
        downloadCSV(csvContent, 'yash_technology_users_export.csv');

        // Show success message
        showNotification('✅ Users exported successfully!', 'success');
    } catch (error) {
        console.error('❌ Error exporting users:', error);
        showNotification('❌ Error exporting users', 'error');
    }
}

// Generate CSV content
function generateUserCSV() {
    let csv = 'Name,Email,User Type,Bookings,Status,Join Date\n';

    const userRows = document.querySelectorAll('.user-row');

    userRows.forEach(row => {
        const name = row.dataset.userName;
        const email = row.dataset.userEmail;
        const userType = row.querySelector('.user-badge').textContent.trim();
        const bookings = row.querySelector('.count-number').textContent.trim();
        const status = row.querySelector('.status-badge').textContent.trim();
        const joinDate = new Date().toLocaleDateString(); // Placeholder

        csv += `"${name}","${email}","${userType}","${bookings}","${status}","${joinDate}"\n`;
    });

    return csv;
}

// Download CSV file
function downloadCSV(csvContent, fileName) {
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');

    if (link.download !== undefined) {
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', fileName);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        console.log('📥 CSV file downloaded:', fileName);
    }
}

// Show notification
function showNotification(message, type = 'info') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    // Style the notification
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
        max-width: 350px;
        word-wrap: break-word;
    `;

    document.body.appendChild(notification);

    // Show notification
    setTimeout(() => {
        notification.style.opacity = '1';
        notification.style.transform = 'translateX(0)';
    }, 100);

    // Hide notification after 3 seconds
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

// Initialize user card animations
function initializeCardAnimations() {
    const userCards = document.querySelectorAll('.user-card');

    userCards.forEach((card, index) => {
        card.style.animationDelay = (index * 0.1) + 's';

        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-8px) scale(1.02)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });
}

// Initialize table row animations
function initializeTableAnimations() {
    const tableRows = document.querySelectorAll('.table-row');

    tableRows.forEach(row => {
        row.addEventListener('mouseenter', function() {
            this.style.transform = 'translateX(10px)';
            this.style.boxShadow = '0 4px 15px rgba(0,0,0,0.1)';
        });

        row.addEventListener('mouseleave', function() {
            this.style.transform = 'translateX(0)';
            this.style.boxShadow = 'none';
        });
    });
}

// Auto-hide messages after 5 seconds
function hideMessages() {
    setTimeout(() => {
        const messages = document.querySelectorAll('.message');
        messages.forEach(message => {
            message.style.opacity = '0';
            setTimeout(() => {
                if (message.parentNode) {
                    message.parentNode.removeChild(message);
                }
            }, 300);
        });
    }, 5000);
}

// Initialize animations
setTimeout(() => {
    initializeCardAnimations();
    initializeTableAnimations();
}, 100);

// Hide initial messages
hideMessages();

// Keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + F = Focus search
    if ((e.ctrlKey || e.metaKey) && e.key === 'f') {
        e.preventDefault();
        const searchInput = document.getElementById('userSearch');
        if (searchInput) {
            searchInput.focus();
        }
    }

    // Ctrl/Cmd + E = Export users
    if ((e.ctrlKey || e.metaKey) && e.key === 'e') {
        e.preventDefault();
        exportUsers();
    }
});

// User management utilities
const UserManagementUtils = {
    // Get user statistics
    getUserStatistics: function() {
        const totalUsers = document.querySelectorAll('.user-row').length;
        const activeUsers = document.querySelectorAll('.status-badge.active').length;
        const vipUsers = document.querySelectorAll('.user-badge.vip').length;
        const adminUsers = document.querySelectorAll('.user-badge.admin').length;

        return {
            total: totalUsers,
            active: activeUsers,
            vip: vipUsers,
            admin: adminUsers
        };
    },

    // Bulk operations (future feature)
    bulkPromote: function(userIds, targetType) {
        console.log('🔄 Bulk promoting users:', userIds, 'to', targetType);
        // Implementation for bulk promotions
    },

    // Search utilities
    advancedSearch: function(criteria) {
        console.log('🔍 Advanced search with criteria:', criteria);
        // Implementation for advanced search
    }
};

// Make utilities available globally
window.UserManagementUtils = UserManagementUtils;

console.log('🎯 ✅ UPDATED Admin User Management JavaScript loaded successfully with promote functionality!');
