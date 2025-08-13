// ====================================
// ADMIN EDIT CABIN - MINIMAL FUNCTIONALITY
// ====================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('✏️ Admin Edit Cabin initialized for Yash Technology');

    // Store original values for change tracking
    window.originalValues = {
        name: document.getElementById('name').value,
        capacity: document.getElementById('capacity').value,
        location: document.getElementById('location').value,
        status: document.getElementById('status').value,
        amenities: document.getElementById('amenities').value,
        accessLevel: document.querySelector('input[name="accessLevel"]:checked').value
    };

    // Initialize components
    initializeForm();
    initializeValidation();
    initializeAmenities();
    initializeAccessLevel();
    initializeDeleteAction();
    initializeStatusWarnings();
    initializeChangeTracking();
    initializeDropdowns();
    initializeMessageHandling();

    // Auto-hide messages after 5 seconds
    setTimeout(hideMessages, 5000);

    // Mark pre-filled fields
    markPreFilledFields();
});

// Initialize form functionality
function initializeForm() {
    const form = document.getElementById('editCabinForm');
    const submitBtn = document.getElementById('submitBtn');

    if (form && submitBtn) {
        form.addEventListener('submit', function(e) {
            // Validate form before submission
            if (!validateForm()) {
                e.preventDefault();
                showNotification('❌ Please fix the errors before submitting', 'error');
                return;
            }

            // Check if any changes were made
            if (!hasChanges()) {
                e.preventDefault();
                showNotification('ℹ️ No changes detected', 'info');
                return;
            }

            // Show loading state
            showLoadingState(submitBtn);
        });
    }
}

// Check if form has changes
function hasChanges() {
    const currentValues = {
        name: document.getElementById('name').value,
        capacity: document.getElementById('capacity').value,
        location: document.getElementById('location').value,
        status: document.getElementById('status').value,
        amenities: document.getElementById('amenities').value,
        accessLevel: document.querySelector('input[name="accessLevel"]:checked').value
    };

    for (let key in currentValues) {
        if (currentValues[key] !== window.originalValues[key]) {
            return true;
        }
    }

    return false;
}

// Show loading state on submit button
function showLoadingState(button) {
    const originalText = button.innerHTML;

    button.innerHTML = '⏳ Updating Cabin...';
    button.disabled = true;
    button.classList.add('saving');

    console.log('⏳ Form submitted - showing loading state');
}

// Initialize real-time form validation (reuse from add-cabin)
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
            clearValidationState(this);
            trackFieldChange(this, 'name');
        });
    }

    // Capacity validation
    if (capacityInput) {
        capacityInput.addEventListener('blur', function() {
            validateCapacity(parseInt(this.value));
        });

        capacityInput.addEventListener('input', function() {
            clearValidationState(this);
            trackFieldChange(this, 'capacity');
        });
    }

    // Location validation
    if (locationInput) {
        locationInput.addEventListener('input', function() {
            clearValidationState(this);
            trackFieldChange(this, 'location');
        });

        locationInput.addEventListener('blur', function() {
            validateLocation(this.value.trim());
        });
    }
}

// Track field changes
function trackFieldChange(field, fieldName) {
    const formGroup = field.closest('.form-group');
    const originalValue = window.originalValues[fieldName];
    const currentValue = field.value;

    if (currentValue !== originalValue) {
        formGroup.classList.add('changed');
        showOriginalValue(formGroup, originalValue);
    } else {
        formGroup.classList.remove('changed');
        hideOriginalValue(formGroup);
    }

    updateFormState();
}

// Show original value
function showOriginalValue(formGroup, originalValue) {
    let originalValueDiv = formGroup.querySelector('.original-value');

    if (!originalValueDiv) {
        originalValueDiv = document.createElement('div');
        originalValueDiv.className = 'original-value';
        formGroup.appendChild(originalValueDiv);
    }

    originalValueDiv.textContent = `Original: ${originalValue}`;
}

// Hide original value
function hideOriginalValue(formGroup) {
    const originalValueDiv = formGroup.querySelector('.original-value');
    if (originalValueDiv) {
        originalValueDiv.remove();
    }
}

// Update form state
function updateFormState() {
    const submitBtn = document.getElementById('submitBtn');

    if (hasChanges()) {
        submitBtn.innerHTML = '💾 Save Changes';
        submitBtn.classList.add('has-changes');
    } else {
        submitBtn.innerHTML = '💾 Update Cabin';
        submitBtn.classList.remove('has-changes');
    }
}

// Mark pre-filled fields
function markPreFilledFields() {
    const inputs = document.querySelectorAll('.form-input, .form-select, .form-textarea');

    inputs.forEach(input => {
        if (input.value) {
            input.classList.add('pre-filled');
        }
    });

    // Mark pre-selected amenities
    const checkedAmenities = document.querySelectorAll('input[name="amenityCheck"]:checked');
    checkedAmenities.forEach(checkbox => {
        checkbox.closest('.amenity-item').classList.add('pre-selected');
    });
}

// Initialize change tracking
function initializeChangeTracking() {
    // Track status changes
    const statusSelect = document.getElementById('status');
    if (statusSelect) {
        statusSelect.addEventListener('change', function() {
            trackFieldChange(this, 'status');
            handleStatusChange(this.value);
        });
    }

    // Track amenities changes
    const amenitiesTextarea = document.getElementById('amenities');
    if (amenitiesTextarea) {
        amenitiesTextarea.addEventListener('input', function() {
            trackFieldChange(this, 'amenities');
        });
    }

    // Track access level changes
    const accessRadios = document.querySelectorAll('input[name="accessLevel"]');
    accessRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            trackAccessLevelChange(this.value);
        });
    });
}

// Track access level changes
function trackAccessLevelChange(newValue) {
    const originalValue = window.originalValues.accessLevel;
    const accessSection = document.querySelector('.access-section');

    if (newValue !== originalValue) {
        accessSection.classList.add('changed');
    } else {
        accessSection.classList.remove('changed');
    }

    updateVipHiddenField(newValue);
    updateFormState();
}

// Initialize amenities functionality (reuse from add-cabin with modifications)
function initializeAmenities() {
    const amenityCheckboxes = document.querySelectorAll('input[name="amenityCheck"]');
    const amenitiesTextarea = document.getElementById('amenities');

    // Update textarea when checkboxes change
    amenityCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            updateAmenitiesText();
            trackFieldChange(amenitiesTextarea, 'amenities');
        });
    });

    // Update checkboxes when textarea changes
    if (amenitiesTextarea) {
        amenitiesTextarea.addEventListener('input', function() {
            syncCheckboxesWithText();
        });
    }
}

// Initialize access level functionality (reuse from add-cabin)
function initializeAccessLevel() {
    const accessRadios = document.querySelectorAll('input[name="accessLevel"]');
    const vipHiddenInput = document.getElementById('isVipOnly');

    accessRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            updateAccessLevel(this.value);
            updateVipHiddenField(this.value);
        });
    });

    // Initialize with current value
    const currentAccess = document.querySelector('input[name="accessLevel"]:checked');
    if (currentAccess && vipHiddenInput) {
        updateVipHiddenField(currentAccess.value);
        updateAccessLevel(currentAccess.value);
    }
}

// Initialize delete action
function initializeDeleteAction() {
    const deleteCabinBtn = document.getElementById('deleteCabinBtn');
    const deleteModal = document.getElementById('deleteModal');
    const deleteModalClose = document.getElementById('deleteModalClose');
    const cancelDelete = document.getElementById('cancelDelete');
    const confirmDelete = document.getElementById('confirmDelete');

    // Show delete modal
    if (deleteCabinBtn) {
        deleteCabinBtn.addEventListener('click', function() {
            showDeleteModal();
        });
    }

    // Close delete modal
    [deleteModalClose, cancelDelete].forEach(btn => {
        if (btn) {
            btn.addEventListener('click', function() {
                hideDeleteModal();
            });
        }
    });

    // Confirm delete
    if (confirmDelete) {
        confirmDelete.addEventListener('click', function() {
            processDelete();
        });
    }

    // Close modal on overlay click
    if (deleteModal) {
        deleteModal.addEventListener('click', function(e) {
            if (e.target === deleteModal) {
                hideDeleteModal();
            }
        });
    }
}

// Show delete confirmation modal
function showDeleteModal() {
    const modal = document.getElementById('deleteModal');
    modal.classList.add('show');
    document.body.style.overflow = 'hidden';

    console.log('🗑️ Delete confirmation modal shown');
}

// Hide delete confirmation modal
function hideDeleteModal() {
    const modal = document.getElementById('deleteModal');
    modal.classList.remove('show');
    document.body.style.overflow = '';

    console.log('❌ Delete modal hidden');
}

// Process delete
function processDelete() {
    console.log('🗑️ Processing cabin deletion');

    // Show loading state
    const confirmBtn = document.getElementById('confirmDelete');
    if (confirmBtn) {
        confirmBtn.innerHTML = '⏳ Deleting...';
        confirmBtn.disabled = true;
    }

    // Submit delete form
    const form = document.getElementById('deleteForm');
    form.submit();
}

// Initialize status warnings
function initializeStatusWarnings() {
    const statusSelect = document.getElementById('status');

    if (statusSelect) {
        statusSelect.addEventListener('change', function() {
            const newStatus = this.value;
            const originalStatus = window.originalValues.status;

            if (newStatus !== originalStatus) {
                showStatusWarning(newStatus, originalStatus);
            }
        });
    }
}

// Handle status changes
function handleStatusChange(newStatus) {
    const statusSelect = document.getElementById('status');
    statusSelect.classList.add('status-changed');

    setTimeout(() => {
        statusSelect.classList.remove('status-changed');
    }, 500);

    console.log('📊 Status changed to:', newStatus);
}

// Show status warning modal
function showStatusWarning(newStatus, originalStatus) {
    const modal = document.getElementById('statusWarningModal');
    const warningContent = document.getElementById('warningContent');

    let warningHTML = '';

    switch(newStatus) {
        case 'INACTIVE':
            warningHTML = `
                <div class="warning-icon">❌</div>
                <h4>Setting Cabin to Inactive</h4>
                <p>This action will have the following consequences:</p>
                <ul class="warning-list">
                    <li>🚫 No new bookings can be made</li>
                    <li>📅 Existing future bookings will be cancelled</li>
                    <li>📧 Users will be notified of cancellations</li>
                    <li>🔍 Cabin will be hidden from booking searches</li>
                </ul>
                <p>Are you sure you want to continue?</p>
            `;
            break;

        case 'MAINTENANCE':
            warningHTML = `
                <div class="warning-icon">🔧</div>
                <h4>Setting Cabin to Maintenance</h4>
                <p>This action will have the following consequences:</p>
                <ul class="warning-list">
                    <li>🔧 Cabin will be marked as under maintenance</li>
                    <li>📅 New bookings will be temporarily blocked</li>
                    <li>⏳ Existing bookings may be affected</li>
                    <li>🔔 Maintenance status will be visible to users</li>
                </ul>
                <p>Are you sure you want to continue?</p>
            `;
            break;

        case 'ACTIVE':
            if (originalStatus === 'INACTIVE') {
                warningHTML = `
                    <div class="warning-icon">✅</div>
                    <h4>Reactivating Cabin</h4>
                    <p>This action will:</p>
                    <ul class="warning-list">
                        <li>✅ Make cabin available for new bookings</li>
                        <li>🔍 Include cabin in booking searches</li>
                        <li>📧 Notify relevant users of reactivation</li>
                        <li>📊 Resume normal cabin operations</li>
                    </ul>
                    <p>Are you sure you want to continue?</p>
                `;
            }
            break;
    }

    if (warningHTML) {
        warningContent.innerHTML = warningHTML;
        modal.classList.add('show');
        document.body.style.overflow = 'hidden';

        // Set up modal close handlers
        const statusWarningClose = document.getElementById('statusWarningClose');
        const cancelStatusChange = document.getElementById('cancelStatusChange');
        const confirmStatusChange = document.getElementById('confirmStatusChange');

        [statusWarningClose, cancelStatusChange].forEach(btn => {
            if (btn) {
                btn.onclick = function() {
                    hideStatusWarningModal();
                    // Reset status to original
                    document.getElementById('status').value = originalStatus;
                    trackFieldChange(document.getElementById('status'), 'status');
                };
            }
        });

        if (confirmStatusChange) {
            confirmStatusChange.onclick = function() {
                hideStatusWarningModal();
                // Status change is confirmed, no need to reset
            };
        }
    }
}

// Hide status warning modal
function hideStatusWarningModal() {
    const modal = document.getElementById('statusWarningModal');
    modal.classList.remove('show');
    document.body.style.overflow = '';
}

// View bookings action
function initializeViewBookings() {
    const viewBookingsBtn = document.getElementById('viewBookingsBtn');

    if (viewBookingsBtn) {
        viewBookingsBtn.addEventListener('click', function() {
            const cabinId = document.querySelector('input[name="cabinId"]').value;
            window.open(`${getContextPath()}/admin/cabin-bookings?cabinId=${cabinId}`, '_blank');
        });
    }
}

// Initialize dropdown functionality (reuse from other files)
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

// Initialize message handling (reuse from other files)
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

// Utility functions (reuse common ones)
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

        // Smart merge: avoid duplicates
        const existingAmenities = existingText.split(',').map(s => s.trim()).filter(s => s);
        const allAmenities = [...new Set([...selectedAmenities, ...existingAmenities])];

        amenitiesTextarea.value = allAmenities.join(', ');
    }
}

function syncCheckboxesWithText() {
    const amenitiesTextarea = document.getElementById('amenities');
    const checkboxes = document.querySelectorAll('input[name="amenityCheck"]');

    if (!amenitiesTextarea) return;

    const textContent = amenitiesTextarea.value.toLowerCase();

    checkboxes.forEach(checkbox => {
        const value = checkbox.value.toLowerCase();
        checkbox.checked = textContent.includes(value);

        // Update visual state
        const amenityItem = checkbox.closest('.amenity-item');
        if (checkbox.checked) {
            amenityItem.classList.add('pre-selected');
        } else {
            amenityItem.classList.remove('pre-selected');
        }
    });
}

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

function updateVipHiddenField(accessLevel) {
    const vipHiddenInput = document.getElementById('isVipOnly');

    if (vipHiddenInput) {
        vipHiddenInput.value = (accessLevel === 'VIP') ? 'true' : 'false';
    }
}

// Form validation functions (reuse from add-cabin)
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

function showFieldError(input, errorElement, message) {
    input.classList.remove('valid');
    input.classList.add('invalid');

    if (errorElement) {
        errorElement.textContent = message;
        errorElement.classList.add('show');
    }
}

function showFieldSuccess(input, errorElement) {
    input.classList.remove('invalid');
    input.classList.add('valid');

    if (errorElement) {
        errorElement.textContent = '';
        errorElement.classList.remove('show');
    }
}

function clearValidationState(input) {
    input.classList.remove('valid', 'invalid');

    const errorElement = document.getElementById(input.id + 'Error');
    if (errorElement) {
        errorElement.textContent = '';
        errorElement.classList.remove('show');
    }
}

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

function getContextPath() {
    const path = window.location.pathname;
    return path.substring(0, path.indexOf('/', 1)) || '';
}

// Initialize view bookings functionality
initializeViewBookings();

// Keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + S = Save form
    if ((e.ctrlKey || e.metaKey) && e.key === 's') {
        e.preventDefault();
        const submitBtn = document.getElementById('submitBtn');
        if (submitBtn && !submitBtn.disabled) {
            submitBtn.click();
        }
    }

    // Escape = Cancel/Go back
    if (e.key === 'Escape') {
        if (document.querySelector('.modal-overlay.show')) {
            // Close open modal
            const openModal = document.querySelector('.modal-overlay.show');
            openModal.classList.remove('show');
            document.body.style.overflow = '';
        } else {
            // Confirm navigation away if changes exist
            if (hasChanges()) {
                if (confirm('You have unsaved changes. Are you sure you want to leave?')) {
                    window.location.href = getContextPath() + '/admin/manage-cabins';
                }
            } else {
                window.location.href = getContextPath() + '/admin/manage-cabins';
            }
        }
    }
});

// Warn before leaving page with unsaved changes
window.addEventListener('beforeunload', function(e) {
    if (hasChanges()) {
        e.preventDefault();
        e.returnValue = '';
        return '';
    }
});

console.log('🎯 Edit Cabin JavaScript loaded successfully');
