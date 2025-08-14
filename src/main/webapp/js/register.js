// ====================================
// REGISTER PAGE - ENHANCED WITH BCRYPT SECURITY REQUIREMENTS
// ====================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('📝 Registration page initialized with BCrypt security');

    const registerForm = document.getElementById('registerForm');
    const nameInput = document.getElementById('name');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const registerBtn = document.querySelector('.register-btn');

    // ✅ ENHANCED: Updated password strength checker for 8-char requirement
    function checkPasswordStrength(password) {
        let strength = 0;
        let feedback = [];
        let requirements = {
            length: false,
            lowercase: false,
            uppercase: false,
            digit: false,
            special: false
        };

        // Check length (minimum 8)
        if (password.length >= 8) {
            strength++;
            requirements.length = true;
        } else {
            feedback.push('At least 8 characters required');
        }

        // Check lowercase
        if (password.match(/[a-z]+/)) {
            strength++;
            requirements.lowercase = true;
        } else {
            feedback.push('Add lowercase letters (a-z)');
        }

        // Check uppercase
        if (password.match(/[A-Z]+/)) {
            strength++;
            requirements.uppercase = true;
        } else {
            feedback.push('Add uppercase letters (A-Z)');
        }

        // Check digits
        if (password.match(/[0-9]+/)) {
            strength++;
            requirements.digit = true;
        } else {
            feedback.push('Add numbers (0-9)');
        }

        // ✅ ENHANCED: Updated special character check
        if (password.match(/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/)) {
            strength++;
            requirements.special = true;
        } else {
            feedback.push('Add special characters (!@#$%^&*)');
        }

        // ✅ ENHANCED: Better strength levels
        let level;
        if (strength === 0) {
            level = 'none';
        } else if (strength <= 2) {
            level = 'very-weak';
        } else if (strength === 3) {
            level = 'weak';
        } else if (strength === 4) {
            level = 'moderate';
        } else if (strength === 5) {
            level = 'strong';
        }

        return {
            strength: strength,
            feedback: feedback,
            level: level,
            requirements: requirements,
            isValid: strength >= 5 // All 5 requirements must be met
        };
    }

    // ✅ ENHANCED: Add comprehensive password strength indicator
    function addPasswordStrengthIndicator() {
        if (!document.querySelector('.password-strength-indicator')) {
            const strengthDiv = document.createElement('div');
            strengthDiv.className = 'password-strength-indicator';
            strengthDiv.innerHTML = `
                <div class="password-strength-bar">
                    <div class="strength-fill"></div>
                </div>
                <div class="password-strength-text"></div>
                <div class="password-requirements-check">
                    <small id="req-length">❌ 8+ characters</small>
                    <small id="req-lower">❌ Lowercase (a-z)</small>
                    <small id="req-upper">❌ Uppercase (A-Z)</small>
                    <small id="req-digit">❌ Number (0-9)</small>
                    <small id="req-special">❌ Special (!@#$%^&*)</small>
                </div>
            `;
            passwordInput.parentNode.appendChild(strengthDiv);

            // Add CSS styles
            const style = document.createElement('style');
            style.textContent = `
                .password-strength-indicator {
                    margin-top: 8px;
                }
                .password-strength-bar {
                    width: 100%;
                    height: 6px;
                    background: #e0e0e0;
                    border-radius: 3px;
                    overflow: hidden;
                    margin-bottom: 5px;
                }
                .strength-fill {
                    height: 100%;
                    width: 0%;
                    transition: all 0.3s ease;
                    border-radius: 3px;
                }
                .password-strength-text {
                    font-size: 0.9em;
                    font-weight: bold;
                    margin-bottom: 5px;
                }
                .password-requirements-check {
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 3px;
                }
                .password-requirements-check small {
                    font-size: 0.8em;
                    transition: color 0.3s ease;
                }
                .req-met { color: #28a745; }
                .req-unmet { color: #dc3545; }

                /* Strength level colors */
                .strength-none .strength-fill { background: #ddd; }
                .strength-very-weak .strength-fill { background: #ff4444; }
                .strength-weak .strength-fill { background: #ff8800; }
                .strength-moderate .strength-fill { background: #ffbb00; }
                .strength-strong .strength-fill { background: #28a745; }
            `;
            document.head.appendChild(style);
        }
    }

    // ✅ ENHANCED: Update password strength display with requirements
    function updatePasswordStrength() {
        const result = checkPasswordStrength(passwordInput.value);
        const strengthDiv = document.querySelector('.password-strength-indicator');

        if (strengthDiv) {
            const strengthFill = strengthDiv.querySelector('.strength-fill');
            const strengthText = strengthDiv.querySelector('.password-strength-text');

            // Update strength bar
            strengthDiv.className = `password-strength-indicator strength-${result.level}`;
            strengthFill.style.width = `${(result.strength / 5) * 100}%`;

            // Update strength text
            const strengthMessages = {
                'none': '',
                'very-weak': '🔴 Very Weak',
                'weak': '🟡 Weak',
                'moderate': '🟠 Moderate',
                'strong': '🟢 Strong - Secure!'
            };
            strengthText.textContent = strengthMessages[result.level];

            // ✅ NEW: Update individual requirements
            updateRequirement('req-length', result.requirements.length);
            updateRequirement('req-lower', result.requirements.lowercase);
            updateRequirement('req-upper', result.requirements.uppercase);
            updateRequirement('req-digit', result.requirements.digit);
            updateRequirement('req-special', result.requirements.special);
        }
    }

    // ✅ NEW: Update individual requirement status
    function updateRequirement(reqId, met) {
        const reqElement = document.getElementById(reqId);
        if (reqElement) {
            if (met) {
                reqElement.textContent = reqElement.textContent.replace('❌', '✅');
                reqElement.className = 'req-met';
            } else {
                reqElement.textContent = reqElement.textContent.replace('✅', '❌');
                reqElement.className = 'req-unmet';
            }
        }
    }

    // ✅ ENHANCED: Updated form validation with 8-char requirement
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

        // ✅ ENHANCED: Validate password with BCrypt requirements
        const passwordStrength = checkPasswordStrength(passwordInput.value);
        if (!passwordInput.value.trim()) {
            passwordInput.classList.add('error');
            Utils.showMessage('Password is required', 'error');
            isValid = false;
        } else if (passwordInput.value.length < 8) {
            passwordInput.classList.add('error');
            Utils.showMessage('Password must be at least 8 characters long', 'error');
            isValid = false;
        } else if (!passwordStrength.isValid) {
            passwordInput.classList.add('error');
            Utils.showMessage('Password must meet all security requirements: ' + passwordStrength.feedback.join(', '), 'error');
            isValid = false;
        } else {
            passwordInput.classList.add('success');
        }

        // Validate password confirmation
        if (!confirmPasswordInput.value.trim()) {
            confirmPasswordInput.classList.add('error');
            Utils.showMessage('Please confirm your password', 'error');
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

    // ✅ ENHANCED: Password strength checking with real-time feedback
    passwordInput.addEventListener('input', function() {
        addPasswordStrengthIndicator();
        updatePasswordStrength();

        // Real-time validation feedback
        const result = checkPasswordStrength(this.value);
        if (this.value.length > 0) {
            if (result.isValid) {
                this.classList.remove('error', 'warning');
                this.classList.add('success');
            } else if (result.strength >= 3) {
                this.classList.remove('error', 'success');
                this.classList.add('warning');
            } else {
                this.classList.remove('warning', 'success');
                this.classList.add('error');
            }
        }

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

    // ✅ ENHANCED: Form submission with BCrypt validation
    registerForm.addEventListener('submit', function(e) {
        console.log('📝 Registration form submitted with BCrypt validation');

        // Clear previous messages
        Utils.clearMessages();

        // Validate form
        if (!validateForm()) {
            e.preventDefault();
            return false;
        }

        // ✅ ADDITIONAL: Final password security check
        const passwordStrength = checkPasswordStrength(passwordInput.value);
        if (!passwordStrength.isValid) {
            e.preventDefault();
            Utils.showMessage('Please ensure your password meets all security requirements for BCrypt encryption.', 'error');
            return false;
        }

        // Show loading state
        Utils.showLoading(registerBtn, '🔐 Creating Secure Account...');

        console.log('✅ Registration form validation passed with strong password, submitting...');

        // Form will submit normally with BCrypt hashing on server
    });

    // Enter key handling
    document.addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && e.target.tagName === 'INPUT') {
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

    // Email availability check with enhanced feedback
    let emailCheckTimeout;
    emailInput.addEventListener('input', function() {
        clearTimeout(emailCheckTimeout);
        emailCheckTimeout = setTimeout(() => {
            if (Utils.isValidEmail(this.value)) {
                console.log('📧 Email format valid for BCrypt registration:', this.value);

                // Could add AJAX call to check email availability
                // Example: checkEmailAvailability(this.value);
            }
        }, 500);
    });
});

// ✅ ENHANCED: Additional utility for secure registration
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
        const commonDomains = ['gmail.com', 'yahoo.com', 'hotmail.com', 'outlook.com', 'yashtech.com'];
        const domain = email.split('@')[1];
        return commonDomains.includes(domain);
    },

    // ✅ NEW: Generate password suggestions
    generatePasswordSuggestion: function() {
        const chars = 'abcdefghijklmnopqrstuvwxyz';
        const upperChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
        const numbers = '0123456789';
        const specials = '!@#$%^&*';

        let password = '';
        password += chars[Math.floor(Math.random() * chars.length)];
        password += upperChars[Math.floor(Math.random() * upperChars.length)];
        password += numbers[Math.floor(Math.random() * numbers.length)];
        password += specials[Math.floor(Math.random() * specials.length)];

        // Add more random characters
        const allChars = chars + upperChars + numbers + specials;
        for (let i = 0; i < 4; i++) {
            password += allChars[Math.floor(Math.random() * allChars.length)];
        }

        return password;
    },

    // ✅ NEW: Check password against common patterns
    isCommonPassword: function(password) {
        const commonPasswords = ['password', '12345678', 'qwerty123', 'admin123'];
        return commonPasswords.some(common =>
            password.toLowerCase().includes(common.toLowerCase())
        );
    }
};

// Export for use
window.RegisterUtils = RegisterUtils;
