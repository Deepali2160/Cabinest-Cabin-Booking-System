// ====================================
// REGISTER PAGE - MINIMAL FUNCTIONALITY
// ====================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('📝 Registration page initialized');

    const registerForm = document.getElementById('registerForm');
    const nameInput = document.getElementById('name');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const registerBtn = document.querySelector('.register-btn');

    // Password strength checker
    function checkPasswordStrength(password) {
        let strength = 0;
        let feedback = [];

        if (password.length >= 6) strength++;
        if (password.match(/[a-z]+/)) strength++;
        if (password.match(/[A-Z]+/)) strength++;
        if (password.match(/[0-9]+/)) strength++;
        if (password.match(/[$@#&!]+/)) strength++;

        if (password.length < 6) {
            feedback.push('At least 6 characters');
        }
        if (!password.match(/[a-z]+/)) {
            feedback.push('Add lowercase letters');
        }
        if (!password.match(/[A-Z]+/)) {
            feedback.push('Add uppercase letters');
        }
        if (!password.match(/[0-9]+/)) {
            feedback.push('Add numbers');
        }

        return {
            strength: strength,
            feedback: feedback,
            level: strength < 2 ? 'weak' : strength < 4 ? 'medium' : 'strong'
        };
    }

    // Add password strength indicator
    function addPasswordStrengthIndicator() {
        if (!document.querySelector('.password-strength')) {
            const strengthDiv = document.createElement('div');
            strengthDiv.className = 'password-strength';
            strengthDiv.innerHTML = '<div class="password-strength-bar"></div>';
            passwordInput.parentNode.appendChild(strengthDiv);
        }
    }

    // Update password strength display
    function updatePasswordStrength() {
        const result = checkPasswordStrength(passwordInput.value);
        const strengthDiv = document.querySelector('.password-strength');

        if (strengthDiv) {
            strengthDiv.className = `password-strength ${result.level}`;
        }
    }

    // Form validation
    function validateForm() {
        let isValid = true;

        // Clear previous validation states
        [nameInput, emailInput, passwordInput, confirmPasswordInput].forEach(input => {
            input.classList.remove('error', 'success', 'warning');
        });

        // Validate name
        if (!nameInput.value.trim()) {
            nameInput.classList.add('error');
            isValid = false;
        } else if (nameInput.value.trim().length < 2) {
            nameInput.classList.add('warning');
            Utils.showMessage('Name should be at least 2 characters', 'warning');
            isValid = false;
        } else {
            nameInput.classList.add('success');
        }

        // Validate email
        if (!emailInput.value.trim()) {
            emailInput.classList.add('error');
            isValid = false;
        } else if (!Utils.isValidEmail(emailInput.value)) {
            emailInput.classList.add('error');
            Utils.showMessage('Please enter a valid email address', 'error');
            isValid = false;
        } else {
            emailInput.classList.add('success');
        }

        // Validate password
        const passwordStrength = checkPasswordStrength(passwordInput.value);
        if (!passwordInput.value.trim()) {
            passwordInput.classList.add('error');
            isValid = false;
        } else if (passwordInput.value.length < 6) {
            passwordInput.classList.add('error');
            Utils.showMessage('Password must be at least 6 characters', 'error');
            isValid = false;
        } else if (passwordStrength.level === 'weak') {
            passwordInput.classList.add('warning');
            Utils.showMessage('Password is weak. Consider adding more characters', 'warning');
            // Don't prevent submission for weak passwords, just warn
        } else {
            passwordInput.classList.add('success');
        }

        // Validate password confirmation
        if (!confirmPasswordInput.value.trim()) {
            confirmPasswordInput.classList.add('error');
            isValid = false;
        } else if (passwordInput.value !== confirmPasswordInput.value) {
            confirmPasswordInput.classList.add('error');
            Utils.showMessage('Passwords do not match', 'error');
            isValid = false;
        } else {
            confirmPasswordInput.classList.add('success');
        }

        return isValid;
    }

    // Real-time validation
    nameInput.addEventListener('blur', function() {
        if (this.value.trim() && this.value.trim().length >= 2) {
            this.classList.remove('error');
            this.classList.add('success');
        }
    });

    emailInput.addEventListener('blur', function() {
        if (this.value.trim() && Utils.isValidEmail(this.value)) {
            this.classList.remove('error');
            this.classList.add('success');
        }
    });

    // Password strength checking
    passwordInput.addEventListener('input', function() {
        addPasswordStrengthIndicator();
        updatePasswordStrength();

        // Also check confirm password if it has value
        if (confirmPasswordInput.value) {
            validatePasswordMatch();
        }
    });

    // Password match validation
    function validatePasswordMatch() {
        if (passwordInput.value === confirmPasswordInput.value) {
            confirmPasswordInput.classList.remove('error');
            confirmPasswordInput.classList.add('success');
        } else {
            confirmPasswordInput.classList.remove('success');
            confirmPasswordInput.classList.add('error');
        }
    }

    confirmPasswordInput.addEventListener('input', validatePasswordMatch);

    // Form submission
    registerForm.addEventListener('submit', function(e) {
        console.log('📝 Registration form submitted');

        // Clear previous messages
        Utils.clearMessages();

        // Validate form
        if (!validateForm()) {
            e.preventDefault();
            return false;
        }

        // Show loading state
        Utils.showLoading(registerBtn);

        console.log('✅ Registration form validation passed, submitting...');

        // Form will submit normally
        // Loading state will be cleared on page reload/redirect
    });

    // Enter key handling
    document.addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && e.target.tagName === 'INPUT') {
            // Move to next field or submit
            const inputs = [nameInput, emailInput, passwordInput, confirmPasswordInput];
            const currentIndex = inputs.indexOf(e.target);

            if (currentIndex >= 0 && currentIndex < inputs.length - 1) {
                e.preventDefault();
                inputs[currentIndex + 1].focus();
            } else if (currentIndex === inputs.length - 1) {
                registerForm.dispatchEvent(new Event('submit'));
            }
        }
    });

    // Focus first empty field
    if (!nameInput.value.trim()) {
        nameInput.focus();
    } else if (!emailInput.value.trim()) {
        emailInput.focus();
    }

    // Email availability check (optional enhancement)
    let emailCheckTimeout;
    emailInput.addEventListener('input', function() {
        clearTimeout(emailCheckTimeout);
        emailCheckTimeout = setTimeout(() => {
            if (Utils.isValidEmail(this.value)) {
                // Could add AJAX call to check email availability
                console.log('📧 Email format valid:', this.value);
            }
        }, 500);
    });
});

// Additional utility for registration
const RegisterUtils = {
    // Format name properly
    formatName: function(name) {
        return name.trim()
            .split(' ')
            .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
            .join(' ');
    },

    // Check if email domain is common
    isCommonEmailDomain: function(email) {
        const commonDomains = ['gmail.com', 'yahoo.com', 'hotmail.com', 'outlook.com'];
        const domain = email.split('@')[1];
        return commonDomains.includes(domain);
    }
};

// Export for use
window.RegisterUtils = RegisterUtils;
