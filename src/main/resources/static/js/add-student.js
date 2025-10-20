document.addEventListener('DOMContentLoaded', () => {
    // Wait a bit to ensure all scripts are loaded
    setTimeout(() => {
        loadClasses();
        initializeForm();
        checkEditMode();
    }, 100);
});

let classes = [];
let isEditMode = false;
let studentId = null;

async function loadClasses() {
    try {
        classes = await MoktobApp.apiRequest('/moktob/api/classes/dropdown');
        populateClassDropdown();
    } catch (error) {
        console.error('Error loading classes:', error);
        if (typeof MoktobPopup !== 'undefined' && MoktobPopup.error) {
            MoktobPopup.error({
                title: 'Error',
                message: 'Failed to load classes. Please refresh the page and try again.'
            });
        } else {
            alert('Error: Failed to load classes. Please refresh the page and try again.');
        }
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

function checkEditMode() {
    const isEditField = document.getElementById('isEdit');
    const studentIdField = document.getElementById('studentId');
    
    if (isEditField && isEditField.value === 'true') {
        isEditMode = true;
        studentId = studentIdField ? studentIdField.value : null;
        
        if (studentId) {
            loadStudentData(studentId);
        }
    }
}

async function loadStudentData(id) {
    try {
        const student = await MoktobApp.apiRequest(`/moktob/api/students/${id}`);
        
        // Populate form fields with student data
        if (student) {
            document.getElementById('name').value = student.name || '';
            document.getElementById('dateOfBirth').value = student.dob || '';
            document.getElementById('guardianName').value = student.guardianName || '';
            document.getElementById('guardianContact').value = student.guardianContact || '';
            document.getElementById('address').value = student.address || '';
            
            // Set class selection - wait for classes to be loaded if needed
            if (student.classId) {
                if (classes.length > 0) {
                    document.getElementById('currentClassId').value = student.classId;
                } else {
                    // If classes not loaded yet, wait and try again
                    setTimeout(() => {
                        if (classes.length > 0) {
                            document.getElementById('currentClassId').value = student.classId;
                        }
                    }, 500);
                }
            }
        }
    } catch (error) {
        console.error('Error loading student data:', error);
        if (typeof MoktobPopup !== 'undefined' && MoktobPopup.error) {
            MoktobPopup.error({
                title: 'Error',
                message: 'Failed to load student data. Please try again.'
            });
        } else {
            alert('Error: Failed to load student data. Please try again.');
        }
    }
}

function initializeForm() {
    const form = document.getElementById('addStudentForm');
    const submitBtn = document.getElementById('submitBtn');

    form.addEventListener('submit', handleFormSubmit);

    // Real-time validation
    const requiredFields = ['name'];
    requiredFields.forEach(fieldName => {
        const field = document.getElementById(fieldName);
        if (field) {
            field.addEventListener('blur', () => validateField(field));
            field.addEventListener('input', () => clearFieldError(field));
        }
    });
}

function validateField(field) {
    if (!field) return false;
    
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
    if (field) {
        field.classList.remove('is-invalid');
    }
}

function validateForm() {
    const requiredFields = ['name'];
    let isValid = true;
    
    requiredFields.forEach(fieldName => {
        const field = document.getElementById(fieldName);
        if (field && !validateField(field)) {
            isValid = false;
        }
    });
    
    return isValid;
}

async function handleFormSubmit(event) {
    event.preventDefault();
    
    // Debug: Check if MoktobPopup is available
    console.log('MoktobPopup available:', typeof MoktobPopup);
    console.log('MoktobPopup.success available:', typeof MoktobPopup?.success);
    
    if (!validateForm()) {
        if (typeof MoktobPopup !== 'undefined' && MoktobPopup.error) {
            MoktobPopup.error({
                title: 'Validation Error',
                message: 'Please fill in all required fields correctly.'
            });
        } else {
            // Fallback to browser alert
            alert('Validation Error: Please fill in all required fields correctly.');
        }
        return;
    }
    
    const submitBtn = document.getElementById('submitBtn');
    const originalText = submitBtn.innerHTML;
    
    try {
        // Disable submit button and show loading
        submitBtn.disabled = true;
        submitBtn.innerHTML = `<i class="fas fa-spinner fa-spin me-1"></i>${isEditMode ? 'Updating...' : 'Saving...'}`;
        
        // Collect form data
        const formData = {
            name: document.getElementById('name')?.value?.trim() || '',
            guardianName: document.getElementById('guardianName')?.value?.trim() || null,
            dob: document.getElementById('dateOfBirth')?.value || null,
            guardianContact: document.getElementById('guardianContact')?.value?.trim() || null,
            address: document.getElementById('address')?.value?.trim() || null,
            classId: document.getElementById('currentClassId')?.value || null
        };
        
        // Remove null values
        Object.keys(formData).forEach(key => {
            if (formData[key] === null || formData[key] === '') {
                delete formData[key];
            }
        });
        
        console.log('Submitting student data:', formData);
        
        // Determine API endpoint and method based on edit mode
        const apiUrl = isEditMode ? `/moktob/api/students/${studentId}` : '/moktob/api/students';
        const method = isEditMode ? 'PUT' : 'POST';
        
        // Submit to API
        const response = await MoktobApp.apiRequest(apiUrl, {
            method: method,
            body: JSON.stringify(formData)
        });
        
        console.log(`Student ${isEditMode ? 'updated' : 'created'} successfully:`, response);
        
        // Show success popup
        if (typeof MoktobPopup !== 'undefined' && MoktobPopup.success) {
            MoktobPopup.success({
                title: 'Success!',
                message: `Student has been ${isEditMode ? 'updated' : 'added'} successfully.`,
                confirmText: 'OK',
                onConfirm: () => {
                    // Redirect back to students page
                    window.location.href = '/moktob/students';
                }
            });
        } else {
            // Fallback to browser alert
            alert(`Student has been ${isEditMode ? 'updated' : 'added'} successfully!`);
            window.location.href = '/moktob/students';
        }
        
    } catch (error) {
        console.error(`Error ${isEditMode ? 'updating' : 'creating'} student:`, error);
        
        let errorMessage = `Failed to ${isEditMode ? 'update' : 'add'} student. Please try again.`;
        
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
        
        // Show error message
        if (typeof MoktobPopup !== 'undefined' && MoktobPopup.error) {
            MoktobPopup.error({
                title: 'Error',
                message: errorMessage
            });
        } else {
            // Fallback to browser alert
            alert('Error: ' + errorMessage);
        }
        
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
