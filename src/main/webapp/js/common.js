// ====================================
// COMMON JAVASCRIPT - MINIMAL UTILITY
// ====================================

// Utility Functions
const Utils = {
    // Show loading state
    showLoading: function(button) {
        if (button) {
            button.classList.add('loading');
            button.disabled = true;
        }
    },

    // Hide loading state
    hideLoading: function(button) {
        if (button) {
            button.classList.remove('loading');
            button.disabled = false;
        }
    },

    // Validate email format
    isValidEmail: function(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    },

    // Show message
    showMessage: function(message, type = 'info') {
        const container = document.getElementById('message-container');
        if (container) {
            const messageDiv = document.createElement('div');
            messageDiv.className = `message ${type}-message`;
            messageDiv.textContent = message;

            container.innerHTML = '';
            container.appendChild(messageDiv);

            // Auto hide after 5 seconds
            setTimeout(() => {
                messageDiv.style.opacity = '0';
                setTimeout(() => {
                    if (messageDiv.parentNode) {
                        messageDiv.parentNode.removeChild(messageDiv);
                    }
                }, 300);
            }, 5000);
        }
    },

    // Clear messages
    clearMessages: function() {
        const container = document.getElementById('message-container');
        if (container) {
            container.innerHTML = '';
        }
    }
};

// Global Event Listeners
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Yash Technology Cabin Booking System - Page Loaded');

    // Auto-hide messages after 10 seconds
    const messages = document.querySelectorAll('.message');
    messages.forEach(message => {
        setTimeout(() => {
            message.style.opacity = '0';
            setTimeout(() => {
                if (message.parentNode) {
                    message.parentNode.removeChild(message);
                }
            }, 300);
        }, 10000);
    });
});

// Export for use in other files
window.Utils = Utils;
