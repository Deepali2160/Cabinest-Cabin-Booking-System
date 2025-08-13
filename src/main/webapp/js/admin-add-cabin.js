// ====================================
// ADMIN ADD CABIN - MINIMAL FUNCTIONALITY
// ====================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('➕ Admin Add Cabin initialized for Yash Technology');

    // Initialize components
    initializeForm();
    initializeValidation();
    initializeAmenities();
    initializeAccessLevel();
    initializeDropdowns();
    initializeMessageHandling();

    // Auto-hide messages after 5 seconds
    setTimeout(hideMessages, 5000);
});

// Initialize form functionality
function initializeForm() {
    const form = document.getElementById('addCabinForm');
    const submitBtn = document.getElementById('submitBtn');

    if (form && submitBtn) {
        form.addEventListener('submit', function(e) {
            // Validate form before submission
            if (!validateForm()) {
                e.preventDefault();
                showNotification('❌ Please fix the errors before submitting', 'error');
                return;
            }

            // Show loading state
            showLoadingState(submitBtn);
        });
    }
}

// Show loading state on submit button
function showLoadingState(button) {
    const originalText = button.innerHTML;

    button.innerHTML = '⏳ Adding Cabin...';
    button.disabled = true;
    button.classList.add('loading');

    console.log('⏳ Form submitted - showing loading state');
}

// Initialize real-time form validation
function initializeValidation() {
    const nameInput = document.getElementById('name');
    const capacityInput = document.getElementById('capacity');
    const locationInput = document.getElementById('location');

    // Name validation
    if (nameInput) {
        nameInput.addEventListener('blur', function() {
            validateName(this.value.trim());
        });

        nameInput.addEventListener('input', function() {
            // Clear validation state on input
            clearValidationState(this);
        });
    }

    // Capacity validation
    if (capacityInput) {
        capacityInput.addEventListener('blur', function() {
            validateCapacity(parseInt(this.value));
        });

        capacityInput.addEventListener('input', function() {
            clearValidationState(this);
        });
    }

    // Location validation
    if (locationInput) {
        locationInput.addEventListener('blur', function() {
            validateLocation(this.value.trim());
        });

        locationInput.addEventListener('input', function() {
            clearValidationState(this);
        });
    }
}

// Validate cabin name
function validateName(name) {
    const nameInput = document.getElementById('name');
    const errorElement = document.getElementById('nameError');

    if (name.length < 3) {
        showFieldError(nameInput, errorElement, 'Cabin name must be at least 3 characters long');
        return false;
    }

    if (name.length > 100) {
        showFieldError(nameInput, errorElement, 'Cabin name cannot exceed 100 characters');
        return false;
    }

    if (!/^[a-zA-Z0-9\s\-_]+$/.test(name)) {
        showFieldError(nameInput, errorElement, 'Only letters, numbers, spaces, hyphens and underscores allowed');
        return false;
    }

    showFieldSuccess(nameInput, errorElement);
    return true;
}

// Validate capacity
function validateCapacity(capacity) {
    const capacityInput = document.getElementById('capacity');
    const errorElement = document.getElementById('capacityError');

    if (isNaN(capacity) || capacity < 1) {
        showFieldError(capacityInput, errorElement, 'Capacity must be at least 1 person');
        return false;
    }

    if (capacity > 50) {
        showFieldError(capacityInput, errorElement, 'Capacity cannot exceed 50 people');
        return false;
    }

    showFieldSuccess(capacityInput, errorElement);
    return true;
}

// Validate location
function validateLocation(location) {
    const locationInput = document.getElementById('location');
    const errorElement = document.getElementById('locationError');

    if (location.length < 5) {
        showFieldError(locationInput, errorElement, 'Location must be at least 5 characters long');
        return false;
    }

    if (location.length > 200) {
        showFieldError(locationInput, errorElement, 'Location cannot exceed 200 characters');
        return false;
    }

    showFieldSuccess(locationInput, errorElement);
    return true;
}

// Show field error
function showFieldError(input, errorElement, message) {
    input.classList.remove('valid');
    input.classList.add('invalid');

    if (errorElement) {
        errorElement.textContent = message;
        errorElement.classList.add('show');
    }
}

// Show field success
function showFieldSuccess(input, errorElement) {
    input.classList.remove('invalid');
    input.classList.add('valid');

    if (errorElement) {
        errorElement.textContent = '';
        errorElement.classList.remove('show');
    }
}

// Clear validation state
function clearValidationState(input) {
    input.classList.remove('valid', 'invalid');

    const errorElement = document.getElementById(input.id + 'Error');
    if (errorElement) {
        errorElement.textContent = '';
        errorElement.classList.remove('show');
    }
}

// Validate entire form
function validateForm() {
    const name = document.getElementById('name').value.trim();
    const capacity = parseInt(document.getElementById('capacity').value);
    const location = document.getElementById('location').value.trim();

    let isValid = true;

    if (!validateName(name)) isValid = false;
    if (!validateCapacity(capacity)) isValid = false;
    if (!validateLocation(location)) isValid = false;

    return isValid;
}

// Initialize amenities functionality
function initializeAmenities() {
    const amenityCheckboxes = document.querySelectorAll('input[name="amenityCheck"]');
    const amenitiesTextarea = document.getElementById('amenities');

    // Update textarea when checkboxes change
    amenityCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            updateAmenitiesText();
        });
    });

    // Update checkboxes when textarea changes
    if (amenitiesTextarea) {
        amenitiesTextarea.addEventListener('input', function() {
            // Optional: Sync checkboxes with textarea content
            syncCheckboxesWithText();
        });
    }
}

// Update amenities textarea based on selected checkboxes
function updateAmenitiesText() {
    const selectedAmenities = [];
    const checkboxes = document.querySelectorAll('input[name="amenityCheck"]:checked');

    checkboxes.forEach(checkbox => {
        selectedAmenities.push(checkbox.value);
    });

    const amenitiesTextarea = document.getElementById('amenities');
    if (amenitiesTextarea) {
        const existingText = amenitiesTextarea.value.trim();
        const checkboxText = selectedAmenities.join(', ');

        if (existingText && checkboxText) {
            amenitiesTextarea.value = checkboxText + ', ' + existingText;
        } else if (checkboxText) {
            amenitiesTextarea.value = checkboxText;
        }
    }

    console.log('🌟 Amenities updated:', selectedAmenities);
}

// Sync checkboxes with textarea content
function syncCheckboxesWithText() {
    const amenitiesTextarea = document.getElementById('amenities');
    const checkboxes = document.querySelectorAll('input[name="amenityCheck"]');

    if (!amenitiesTextarea) return;

    const textContent = amenitiesTextarea.value.toLowerCase();

    checkboxes.forEach(checkbox => {
        const value = checkbox.value.toLowerCase();
        checkbox.checked = textContent.includes(value);
    });
}

// Initialize access level functionality
function initializeAccessLevel() {
    const accessRadios = document.querySelectorAll('input[name="accessLevel"]');
    const vipHiddenInput = document.getElementById('isVipOnly');

    accessRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            updateAccessLevel(this.value);
            updateVipHiddenField(this.value);
        });
    });

    // Initialize with default value
    const defaultAccess = document.querySelector('input[name="accessLevel"]:checked');
    if (defaultAccess && vipHiddenInput) {
        updateVipHiddenField(defaultAccess.value);
    }
}

// Update access level UI
function updateAccessLevel(accessLevel) {
    const allCards = document.querySelectorAll('.option-card');

    allCards.forEach(card => {
        card.classList.remove('selected');
    });

    const selectedCard = document.querySelector(`input[value="${accessLevel}"] + .option-card`);
    if (selectedCard) {
        selectedCard.classList.add('selected');
    }

    console.log('🔑 Access level changed to:', accessLevel);
}

// Update hidden VIP field for backend compatibility
function updateVipHiddenField(accessLevel) {
    const vipHiddenInput = document.getElementById('isVipOnly');

    if (vipHiddenInput) {
        vipHiddenInput.value = (accessLevel === 'VIP') ? 'true' : 'false';
    }
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

    // Close dropdowns on escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            document.querySelectorAll('.dropdown-menu').forEach(menu => {
                menu.classList.remove('show');
            });
        }
    });
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
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

// Initialize form animations
function initializeAnimations() {
    const formGroups = document.querySelectorAll('.form-group');

    formGroups.forEach((group, index) => {
        group.style.animationDelay = (index * 0.1) + 's';
        group.style.animation = 'fadeInLeft 0.5s ease-out both';
    });

    const tipItems = document.querySelectorAll('.tip-item');
    tipItems.forEach((tip, index) => {
        tip.style.animationDelay = (index * 0.15 + 0.5) + 's';
    });
}

// Initialize animations
initializeAnimations();

// Keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + Enter = Submit form
    if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
        e.preventDefault();
        const submitBtn = document.getElementById('submitBtn');
        if (submitBtn && !submitBtn.disabled) {
            submitBtn.click();
        }
    }

    // Escape = Cancel/Go back
    if (e.key === 'Escape') {
        e.preventDefault();
        if (confirm('Are you sure you want to cancel? Any unsaved changes will be lost.')) {
            window.location.href = getContextPath() + '/admin/manage-cabins';
        }
    }
});

// Auto-save functionality (draft)
function initializeAutoSave() {
    const form = document.getElementById('addCabinForm');
    const inputs = form.querySelectorAll('input, textarea, select');

    inputs.forEach(input => {
        input.addEventListener('input', function() {
            saveFormDraft();
        });
    });

    // Load saved draft on page load
    loadFormDraft();
}

// Save form data to localStorage
function saveFormDraft() {
    const formData = {
        name: document.getElementById('name').value,
        capacity: document.getElementById('capacity').value,
        location: document.getElementById('location').value,
        floor: document.getElementById('floor').value,
        amenities: document.getElementById('amenities').value,
        accessLevel: document.querySelector('input[name="accessLevel"]:checked')?.value,
        timestamp: new Date().toISOString()
    };

    localStorage.setItem('cabinFormDraft', JSON.stringify(formData));
    console.log('💾 Form draft saved');
}

// Load form data from localStorage
function loadFormDraft() {
    const draft = localStorage.getItem('cabinFormDraft');

    if (draft) {
        try {
            const formData = JSON.parse(draft);

            // Only load if draft is less than 1 hour old
            const draftTime = new Date(formData.timestamp);
            const now = new Date();
            const hoursDiff = (now - draftTime) / (1000 * 60 * 60);

            if (hoursDiff < 1) {
                document.getElementById('name').value = formData.name || '';
                document.getElementById('capacity').value = formData.capacity || '';
                document.getElementById('location').value = formData.location || '';
                document.getElementById('floor').value = formData.floor || '';
                document.getElementById('amenities').value = formData.amenities || '';

                if (formData.accessLevel) {
                    const accessRadio = document.querySelector(`input[name="accessLevel"][value="${formData.accessLevel}"]`);
                    if (accessRadio) {
                        accessRadio.checked = true;
                        updateAccessLevel(formData.accessLevel);
                        updateVipHiddenField(formData.accessLevel);
                    }
                }

                console.log('📋 Form draft loaded');
                showNotification('📋 Draft restored from previous session', 'info');
            } else {
                // Clear old draft
                localStorage.removeItem('cabinFormDraft');
            }
        } catch (error) {
            console.error('Error loading draft:', error);
            localStorage.removeItem('cabinFormDraft');
        }
    }
}

// Clear draft after successful submission
function clearFormDraft() {
    localStorage.removeItem('cabinFormDraft');
    console.log('🗑️ Form draft cleared');
}

// Initialize auto-save
initializeAutoSave();

// Utility functions
function getContextPath() {
    const path = window.location.pathname;
    return path.substring(0, path.indexOf('/', 1)) || '';
}

// Form utilities
const AddCabinUtils = {
    // Get form data
    getFormData: function() {
        const form = document.getElementById('addCabinForm');
        const formData = new FormData(form);
        const data = {};

        for (let [key, value] of formData.entries()) {
            data[key] = value;
        }

        return data;
    },

    // Reset form
    resetForm: function() {
        const form = document.getElementById('addCabinForm');
        form.reset();

        // Clear validation states
        const inputs = form.querySelectorAll('input, textarea, select');
        inputs.forEach(input => {
            clearValidationState(input);
        });

        // Reset access level
        updateAccessLevel('ALL');

        // Clear draft
        clearFormDraft();

        console.log('🔄 Form reset');
    },

    // Validate form
    validateForm: function() {
        return validateForm();
    }
};

// Make utilities available globally
window.AddCabinUtils = AddCabinUtils;

console.log('🎯 Add Cabin JavaScript loaded successfully');
