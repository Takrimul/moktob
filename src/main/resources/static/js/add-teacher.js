document.addEventListener('DOMContentLoaded', () => {
    initializeForm();
});

function initializeForm() {
    setupFormValidation();
    setupFormSubmission();
}

function setupFormValidation() {
    const form = document.getElementById('addTeacherForm');
    const inputs = form.querySelectorAll('input[required], select[required]');
    
    inputs.forEach(input => {
        input.addEventListener('blur', () => validateField(input));
        input.addEventListener('input', () => clearFieldError(input));
    });
}

function validateField(field) {
    const value = field.value.trim();
    let isValid = true;
    
    if (field.hasAttribute('required') && !value) {
        isValid = false;
    }
    
    if (field.type === 'email' && value && !isValidEmail(value)) {
        isValid = false;
    }
    
    if (isValid) {
        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
    } else {
        field.classList.remove('is-valid');
        field.classList.add('is-invalid');
    }
    
    return isValid;
}

function clearFieldError(field) {
    field.classList.remove('is-invalid');
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function setupFormSubmission() {
    const form = document.getElementById('addTeacherForm');
    form.addEventListener('submit', handleFormSubmit);
}

async function handleFormSubmit(event) {
    event.preventDefault();
    
    const submitBtn = event.target.querySelector('button[type="submit"]');
    
    try {
        // Disable submit button and show loading
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Saving...';
        
        // Collect form data
        const formData = {
            name: document.getElementById('name').value.trim(),
            email: document.getElementById('email').value.trim(),
            phoneNumber: document.getElementById('phoneNumber').value.trim() || null,
            qualification: document.getElementById('qualification').value.trim() || null,
            joiningDate: document.getElementById('joiningDate').value || null,
            isActive: document.getElementById('isActive').value === 'true'
        };
        
        // Remove null values
        Object.keys(formData).forEach(key => {
            if (formData[key] === null || formData[key] === '') {
                delete formData[key];
            }
        });
        
        // Validate required fields
        if (!formData.name) {
            throw new Error('Teacher name is required');
        }
        if (!formData.email) {
            throw new Error('Email is required');
        }
        
        // Submit the form
        const response = await MoktobApp.apiRequest('/moktob/api/teachers', {
            method: 'POST',
            body: JSON.stringify(formData)
        });
        
        // Show success message
        MoktobPopup.success({
            title: 'Success',
            message: 'Teacher created successfully!',
            onConfirm: () => {
                window.location.href = '/moktob/teachers';
            }
        });
        
    } catch (error) {
        console.error('Error creating teacher:', error);
        
        // Show error message
        MoktobPopup.error({
            title: 'Error',
            message: error.message || 'Failed to create teacher. Please try again.'
        });
        
        // Re-enable submit button
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-save me-1"></i>Save Teacher';
    }
}
