// ====================================
// PROFILE PAGE - MINIMAL FUNCTIONALITY
// ====================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('👤 Profile page initialized for Yash Technology');

    // Initialize components
    initializePasswordValidation();
    initializeFormValidation();
    initializeUserDropdown();
    initializeMessageHandling();

    // Initialize password strength checker
    initializePasswordStrength();
});

// Password validation and matching
function initializePasswordValidation() {
    const currentPasswordInput = document.getElementById('currentPassword');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const matchIndicator = document.getElementById('matchIndicator');

    // Password matching validation
    function validatePasswordMatch() {
        if (newPasswordInput.value && confirmPasswordInput.value) {
            if (newPasswordInput.value === confirmPasswordInput.value) {
                matchIndicator.textContent = '✅ Passwords match';
                matchIndicator.className = 'match-indicator match';
                confirmPasswordInput.classList.remove('error');
                confirmPasswordInput.classList.add('success');
            } else {
                matchIndicator.textContent = '❌ Passwords do not match';
                matchIndicator.className = 'match-indicator no-match';
                confirmPasswordInput.classList.remove('success');
                confirmPasswordInput.classList.add('error');
            }
        } else {
            matchIndicator.textContent = '';
            matchIndicator.className = 'match-indicator';
            confirmPasswordInput.classList.remove('error', 'success');
        }
    }

    // Real-time password matching
    if (newPasswordInput && confirmPasswordInput) {
        newPasswordInput.addEventListener('input', validatePasswordMatch);
        confirmPasswordInput.addEventListener('input', validatePasswordMatch);
    }

    // Password change validation
    function validatePasswordFields() {
        const currentPassword = currentPasswordInput?.value || '';
        const newPassword = newPasswordInput?.value || '';
        const confirmPassword = confirmPasswordInput?.value || '';

        // If any password field is filled, all related fields must be filled
        if (currentPassword || newPassword || confirmPassword) {
            if (!currentPassword) {
                Utils.showMessage('Please enter your current password', 'error');
                currentPasswordInput.focus();
                return false;
            }

            if (!newPassword || newPassword.length < 6) {
                Utils.showMessage('New password must be at least 6 characters long', 'error');
                newPasswordInput.focus();
                return false;
            }

            if (newPassword !== confirmPassword) {
                Utils.showMessage('New passwords do not match', 'error');
                confirmPasswordInput.focus();
                return false;
            }
        }

        return true;
    }

    // Export validation function for form submission
    window.validatePasswordFields = validatePasswordFields;
}

// Password strength checker
function initializePasswordStrength() {
    const newPasswordInput = document.getElementById('newPassword');
    const strengthContainer = document.getElementById('passwordStrength');
    const strengthBar = document.getElementById('strengthBar');

    if (!newPasswordInput || !strengthContainer || !strengthBar) return;

    function checkPasswordStrength(password) {
        let strength = 0;
        let feedback = [];

        if (password.length >= 6) strength++;
        if (password.match(/[a-z]+/)) strength++;
        if (password.match(/[A-Z]+/)) strength++;
        if (password.match(/[0-9]+/)) strength++;
        if (password.match(/[$@#&!]+/)) strength++;

        let level = 'weak';
        if (strength >= 3) level = 'medium';
        if (strength >= 4) level = 'strong';

        return { strength, level, feedback };
    }

    newPasswordInput.addEventListener('input', function() {
        const password = this.value;

        if (password.length > 0) {
            strengthContainer.style.display = 'block';
            const result = checkPasswordStrength(password);

            strengthBar.className = `strength-bar ${result.level}`;

            console.log('🔒 Password strength:', result.level);
        } else {
            strengthContainer.style.display = 'none';
        }
    });
}

// Form validation and submission
function initializeFormValidation() {
    const profileForm = document.getElementById('profileForm');
    const updateBtn = document.getElementById('updateBtn');

    if (!profileForm || !updateBtn) return;

    profileForm.addEventListener('submit', function(e) {
        console.log('📝 Profile form submitted');

        // Clear previous messages
        Utils.clearMessages();

        // Validate password fields
        if (!window.validatePasswordFields()) {
            e.preventDefault();
            return false;
        }

        // Validate basic form fields
        if (!validateBasicFields()) {
            e.preventDefault();
            return false;
        }

        // Show loading state
        Utils.showLoading(updateBtn);
        updateBtn.innerHTML = '⏳ Updating Profile...';

        console.log('✅ Profile form validation passed, submitting...');

        // Form will submit normally
        return true;
    });

    function validateBasicFields() {
        const nameInput = document.getElementById('name');
        const emailInput = document.getElementById('email');

        if (!nameInput.value.trim()) {
            Utils.showMessage('Name is required', 'error');
            nameInput.focus();
            return false;
        }

        if (nameInput.value.trim().length < 2) {
            Utils.showMessage('Name must be at least 2 characters long', 'error');
            nameInput.focus();
            return false;
        }

        if (!emailInput.value.trim()) {
            Utils.showMessage('Email is required', 'error');
            emailInput.focus();
            return false;
        }

        if (!Utils.isValidEmail(emailInput.value.trim())) {
            Utils.showMessage('Please enter a valid email address', 'error');
            emailInput.focus();
            return false;
        }

        return true;
    }
}

// User dropdown functionality
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

// Message handling
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

// Real-time form validation
function initializeRealTimeValidation() {
    const nameInput = document.getElementById('name');
    const emailInput = document.getElementById('email');

    if (nameInput) {
        nameInput.addEventListener('blur', function() {
            if (this.value.trim() && this.value.trim().length >= 2) {
                this.classList.remove('error');
                this.classList.add('success');
            } else {
                this.classList.remove('success');
                this.classList.add('error');
            }
        });
    }

    if (emailInput) {
        emailInput.addEventListener('blur', function() {
            if (this.value.trim() && Utils.isValidEmail(this.value.trim())) {
                this.classList.remove('error');
                this.classList.add('success');
            } else {
                this.classList.remove('success');
                this.classList.add('error');
            }
        });
    }
}

// Initialize real-time validation
initializeRealTimeValidation();

// Keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + S = Save Profile
    if ((e.ctrlKey || e.metaKey) && e.key === 's') {
        e.preventDefault();
        const profileForm = document.getElementById('profileForm');
        if (profileForm) {
            profileForm.dispatchEvent(new Event('submit'));
        }
    }

    // Ctrl/Cmd + H = Dashboard
    if ((e.ctrlKey || e.metaKey) && e.key === 'h') {
        e.preventDefault();
        window.location.href = getContextPath() + '/dashboard';
    }
});

// Utility functions
function getContextPath() {
    const path = window.location.pathname;
    return path.substring(0, path.indexOf('/', 1)) || '';
}

// Profile utilities
const ProfileUtils = {
    // Format name properly
    formatName: function(name) {
        return name.trim()
            .split(' ')
            .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
            .join(' ');
    },

    // Validate email format
    isStrongPassword: function(password) {
        return password.length >= 8 &&
               /[a-z]/.test(password) &&
               /[A-Z]/.test(password) &&
               /[0-9]/.test(password);
    },

    // Show password tips
    showPasswordTips: function() {
        Utils.showMessage('Tip: Use at least 8 characters with uppercase, lowercase, and numbers for a strong password', 'info');
    }
};

// Export for global use
window.ProfileUtils = ProfileUtils;

console.log('🎯 Profile JavaScript loaded successfully');
