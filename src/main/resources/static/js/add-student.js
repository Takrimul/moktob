document.addEventListener('DOMContentLoaded', () => {
    loadClasses();
    initializeForm();
});

let classes = [];

async function loadClasses() {
    try {
        classes = await MoktobApp.apiRequest('/moktob/api/classes/dropdown');
        populateClassDropdown();
    } catch (error) {
        console.error('Error loading classes:', error);
        MoktobPopup.error({
            title: 'Error',
            message: 'Failed to load classes. Please refresh the page and try again.'
        });
    }
}

function populateClassDropdown() {
    const classSelect = document.getElementById('currentClassId');
    classSelect.innerHTML = '<option value="">Select a class</option>';
    
    classes.forEach(cls => {
        const option = document.createElement('option');
        option.value = cls.id;
        option.textContent = `${cls.className} (${cls.teacherName || 'No Teacher'})`;
        classSelect.appendChild(option);
    });
}

function initializeForm() {
    const form = document.getElementById('addStudentForm');
    const submitBtn = document.getElementById('submitBtn');

    form.addEventListener('submit', handleFormSubmit);

    // Real-time validation
    const requiredFields = ['name'];
    requiredFields.forEach(fieldName => {
        const field = document.getElementById(fieldName);
        field.addEventListener('blur', () => validateField(field));
        field.addEventListener('input', () => clearFieldError(field));
    });
}

function validateField(field) {
    const value = field.value.trim();
    const isValid = value.length > 0;
    
    if (!isValid) {
        field.classList.add('is-invalid');
        field.classList.remove('is-valid');
    } else {
        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
    }
    
    return isValid;
}

function validateEmail(field) {
    const email = field.value.trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const isValid = email.length > 0 && emailRegex.test(email);
    
    if (!isValid && email.length > 0) {
        field.classList.add('is-invalid');
        field.classList.remove('is-valid');
    } else if (isValid) {
        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
    }
    
    return isValid;
}

function clearFieldError(field) {
    field.classList.remove('is-invalid');
}

function validateForm() {
    const requiredFields = ['name', 'email'];
    let isValid = true;
    
    requiredFields.forEach(fieldName => {
        const field = document.getElementById(fieldName);
        if (!validateField(field)) {
            isValid = false;
        }
    });
    
    return isValid;
}

async function handleFormSubmit(event) {
    event.preventDefault();
    
    if (!validateForm()) {
        MoktobPopup.error({
            title: 'Validation Error',
            message: 'Please fill in all required fields correctly.'
        });
        return;
    }
    
    const submitBtn = document.getElementById('submitBtn');
    const originalText = submitBtn.innerHTML;
    
    try {
        // Disable submit button and show loading
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Saving...';
        
        // Collect form data
        const formData = {
            name: document.getElementById('name').value.trim(),
            guardianName: document.getElementById('guardianName').value.trim() || null,
            dob: document.getElementById('dateOfBirth').value || null,
            guardianContact: document.getElementById('guardianContact').value.trim() || null,
            address: document.getElementById('address').value.trim() || null,
            classId: document.getElementById('currentClassId').value || null
        };
        
        // Remove null values
        Object.keys(formData).forEach(key => {
            if (formData[key] === null || formData[key] === '') {
                delete formData[key];
            }
        });
        
        console.log('Submitting student data:', formData);
        
        // Submit to API
        const response = await MoktobApp.apiRequest('/moktob/api/students', {
            method: 'POST',
            body: JSON.stringify(formData)
        });
        
        console.log('Student created successfully:', response);
        
        // Show success popup
        MoktobPopup.success({
            title: 'Success!',
            message: 'Student has been added successfully.',
            confirmText: 'OK',
            onConfirm: () => {
                // Redirect back to students page
                window.location.href = '/moktob/students';
            }
        });
        
    } catch (error) {
        console.error('Error creating student:', error);
        
        let errorMessage = 'Failed to add student. Please try again.';
        
        if (error.message.includes('409') || error.message.includes('Conflict')) {
            errorMessage = 'A student with this email already exists.';
        } else if (error.message.includes('400') || error.message.includes('Bad Request')) {
            errorMessage = 'Please check your input data and try again.';
        } else if (error.message.includes('401') || error.message.includes('Unauthorized')) {
            errorMessage = 'Your session has expired. Please login again.';
            setTimeout(() => {
                window.location.href = '/moktob/login';
            }, 2000);
        }
        
        MoktobPopup.error({
            title: 'Error',
            message: errorMessage
        });
        
    } finally {
        // Re-enable submit button
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalText;
    }
}

// Utility function to format date for display
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString();
}

// Utility function to format phone number
function formatPhone(phone) {
    if (!phone) return '';
    return phone.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2-$3');
}
