// ====================================
// LOGIN PAGE - MINIMAL FUNCTIONALITY
// ====================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('🔐 Login page initialized');

    const loginForm = document.getElementById('loginForm');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const loginBtn = document.querySelector('.login-btn');

    // Form Validation
    function validateForm() {
        let isValid = true;

        // Clear previous validation states
        emailInput.classList.remove('error', 'success');
        passwordInput.classList.remove('error', 'success');

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
        if (!passwordInput.value.trim()) {
            passwordInput.classList.add('error');
            isValid = false;
        } else if (passwordInput.value.length < 6) {
            passwordInput.classList.add('error');
            Utils.showMessage('Password must be at least 6 characters', 'error');
            isValid = false;
        } else {
            passwordInput.classList.add('success');
        }

        return isValid;
    }

    // Real-time validation
    emailInput.addEventListener('blur', function() {
        if (this.value.trim() && Utils.isValidEmail(this.value)) {
            this.classList.remove('error');
            this.classList.add('success');
        }
    });

    passwordInput.addEventListener('blur', function() {
        if (this.value.trim() && this.value.length >= 6) {
            this.classList.remove('error');
            this.classList.add('success');
        }
    });

    // Form submission
    loginForm.addEventListener('submit', function(e) {
        console.log('📝 Login form submitted');

        // Clear previous messages
        Utils.clearMessages();

        // Validate form
        if (!validateForm()) {
            e.preventDefault();
            return false;
        }

        // Show loading state
        Utils.showLoading(loginBtn);

        // Allow form to submit
        console.log('✅ Form validation passed, submitting...');

        // Note: The form will submit normally
        // Loading state will be cleared when page reloads or redirects
    });

    // Enter key handling
    document.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            loginForm.dispatchEvent(new Event('submit'));
        }
    });

    // Focus first empty field
    if (!emailInput.value.trim()) {
        emailInput.focus();
    } else if (!passwordInput.value.trim()) {
        passwordInput.focus();
    }
});
