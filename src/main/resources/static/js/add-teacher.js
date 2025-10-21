document.addEventListener('DOMContentLoaded', () => {
    initializeForm();
    checkEditMode();
});

let isEditMode = false;
let teacherId = null;

function initializeForm() {
    setupFormValidation();
    setupFormSubmission();
}

function checkEditMode() {
    const isEditField = document.getElementById('isEdit');
    const teacherIdField = document.getElementById('teacherId');
    
    if (isEditField && isEditField.value === 'true') {
        isEditMode = true;
        teacherId = teacherIdField ? teacherIdField.value : null;
        
        if (teacherId) {
            loadTeacherData(teacherId);
        }
    }
}

async function loadTeacherData(id) {
    try {
        const teacher = await MoktobApp.apiRequest(`/moktob/api/teachers/${id}`);
        
        // Populate form fields with teacher data
        if (teacher) {
            document.getElementById('name').value = teacher.name || '';
            document.getElementById('email').value = teacher.email || '';
            document.getElementById('phoneNumber').value = teacher.phone || '';
            document.getElementById('qualification').value = teacher.qualification || '';
            document.getElementById('joiningDate').value = teacher.joiningDate || '';
            document.getElementById('isActive').value = teacher.isActive ? 'true' : 'false';
            document.getElementById('sendCredentials').checked = teacher.sendCredentials || false;
        }
    } catch (error) {
        console.error('Error loading teacher data:', error);
        if (typeof MoktobPopup !== 'undefined' && MoktobPopup.error) {
            MoktobPopup.error({
                title: 'Error',
                message: 'Failed to load teacher data. Please try again.'
            });
        } else {
            alert('Error: Failed to load teacher data. Please try again.');
        }
    }
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
        submitBtn.innerHTML = `<i class="fas fa-spinner fa-spin me-1"></i>${isEditMode ? 'Updating...' : 'Saving...'}`;
        
        // Collect form data
        const formData = {
            name: document.getElementById('name').value.trim(),
            email: document.getElementById('email').value.trim(),
            phoneNumber: document.getElementById('phoneNumber').value.trim() || null,
            qualification: document.getElementById('qualification').value.trim() || null,
            joiningDate: document.getElementById('joiningDate').value || null,
            isActive: document.getElementById('isActive').value === 'true',
            sendCredentials: document.getElementById('sendCredentials').checked
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
        
        console.log('Submitting teacher data:', formData);
        
        // Determine API endpoint and method based on edit mode
        const apiUrl = isEditMode ? `/moktob/api/teachers/${teacherId}` : '/moktob/api/teachers';
        const method = isEditMode ? 'PUT' : 'POST';
        
        // Submit the form
        const response = await MoktobApp.apiRequest(apiUrl, {
            method: method,
            body: JSON.stringify(formData)
        });
        
        console.log(`Teacher ${isEditMode ? 'updated' : 'created'} successfully:`, response);
        
        // Show success message
        MoktobPopup.success({
            title: 'Success',
            message: `Teacher ${isEditMode ? 'updated' : 'created'} successfully!`,
            onConfirm: () => {
                window.location.href = '/moktob/teachers';
            }
        });
        
    } catch (error) {
        console.error(`Error ${isEditMode ? 'updating' : 'creating'} teacher:`, error);
        
        // Show error message
        MoktobPopup.error({
            title: 'Error',
            message: error.message || `Failed to ${isEditMode ? 'update' : 'create'} teacher. Please try again.`
        });
        
        // Re-enable submit button
        submitBtn.disabled = false;
        submitBtn.innerHTML = `<i class="fas fa-save me-1"></i>${isEditMode ? 'Update Teacher' : 'Save Teacher'}`;
    }
}
