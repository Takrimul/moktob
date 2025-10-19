document.addEventListener('DOMContentLoaded', () => {
    initializeForm();
});

let teachers = [];

async function initializeForm() {
    await loadTeachers();
    setupFormValidation();
    setupFormSubmission();
}

async function loadTeachers() {
    try {
        teachers = await MoktobApp.apiRequest('/moktob/api/teachers');
        populateTeacherDropdown();
    } catch (error) {
        console.error('Error loading teachers:', error);
        MoktobPopup.error({
            title: 'Error',
            message: 'Failed to load teachers. Please refresh the page and try again.'
        });
    }
}

function populateTeacherDropdown() {
    const teacherSelect = document.getElementById('teacherId');
    teacherSelect.innerHTML = '<option value="">Select a teacher</option>';
    
    teachers.forEach(teacher => {
        const option = document.createElement('option');
        option.value = teacher.id;
        option.textContent = `${teacher.name} (${teacher.email})`;
        teacherSelect.appendChild(option);
    });
}

function setupFormValidation() {
    const form = document.getElementById('addClassForm');
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
    const form = document.getElementById('addClassForm');
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
            className: document.getElementById('className').value.trim(),
            teacherId: document.getElementById('teacherId').value || null,
            startTime: document.getElementById('startTime').value || null,
            endTime: document.getElementById('endTime').value || null,
            daysOfWeek: getSelectedDays()
        };
        
        // Remove null values
        Object.keys(formData).forEach(key => {
            if (formData[key] === null || formData[key] === '') {
                delete formData[key];
            }
        });
        
        // Validate required fields
        if (!formData.className) {
            throw new Error('Class name is required');
        }
        
        // Submit the form
        const response = await MoktobApp.apiRequest('/moktob/api/classes', {
            method: 'POST',
            body: JSON.stringify(formData)
        });
        
        // Show success message
        MoktobPopup.success({
            title: 'Success',
            message: 'Class created successfully!',
            onConfirm: () => {
                window.location.href = '/moktob/classes';
            }
        });
        
    } catch (error) {
        console.error('Error creating class:', error);
        
        // Show error message
        MoktobPopup.error({
            title: 'Error',
            message: error.message || 'Failed to create class. Please try again.'
        });
        
        // Re-enable submit button
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-save me-1"></i>Save Class';
    }
}

function getSelectedDays() {
    const checkboxes = document.querySelectorAll('input[name="daysOfWeek"]:checked');
    const days = Array.from(checkboxes).map(cb => cb.value);
    return days.length > 0 ? days.join(', ') : null;
}
