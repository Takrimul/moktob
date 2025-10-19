// Authentication JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeAuthForms();
    initializePasswordToggle();
    initializeFormValidation();
});

// Initialize authentication forms
function initializeAuthForms() {
    const loginForm = document.querySelector('form[action*="login"]');
    const registerForm = document.querySelector('form[action*="register"]');
    
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
}

// Handle login form submission
function handleLogin(event) {
    event.preventDefault();
    
    const form = event.target;
    const formData = new FormData(form);
    const loginData = {
        username: formData.get('username'),
        password: formData.get('password')
    };
    
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    
    // Show loading state
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Logging in...';
    submitBtn.disabled = true;
    
    // Make API request
    fetch('/moktob/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginData)
    })
    .then(response => response.json())
    .then(data => {
        console.log('Login response:', data);
        if (data.token) {
            // Store token
            const rememberMe = document.getElementById('rememberMe').checked;
            MoktobApp.setToken(data.token, rememberMe);
            
            // Show success message
            MoktobApp.showAlert('Login successful! Redirecting...', 'success');
            
            // Redirect to dashboard
            setTimeout(() => {
                window.location.href = '/moktob/dashboard';
            }, 1000);
        } else {
            throw new Error(data.message || 'Login failed');
        }
    })
    .catch(error => {
        console.error('Login error:', error);
        MoktobApp.showAlert(error.message || 'Login failed. Please try again.', 'danger');
        
        // Reset button
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    });
}

// Handle registration form submission
function handleRegister(event) {
    event.preventDefault();
    
    const form = event.target;
    const formData = new FormData(form);
    
    // Convert FormData to object
    const registrationData = {};
    for (let [key, value] of formData.entries()) {
        registrationData[key] = value;
    }
    
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    
    // Show loading state
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Registering...';
    submitBtn.disabled = true;
    
    // Make API request
    fetch('/moktob/api/clients/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(registrationData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.client) {
            MoktobApp.showAlert('Registration successful! Please check your email for login credentials.', 'success');
            
            // Redirect to login page
            setTimeout(() => {
                window.location.href = '/moktob/login';
            }, 2000);
        } else {
            throw new Error(data.message || 'Registration failed');
        }
    })
    .catch(error => {
        console.error('Registration error:', error);
        MoktobApp.showAlert(error.message || 'Registration failed. Please try again.', 'danger');
        
        // Reset button
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    });
}

// Initialize password toggle functionality
function initializePasswordToggle() {
    const toggleBtn = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');
    
    if (toggleBtn && passwordInput) {
        toggleBtn.addEventListener('click', function() {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            
            const icon = toggleBtn.querySelector('i');
            icon.classList.toggle('fa-eye');
            icon.classList.toggle('fa-eye-slash');
        });
    }
}

// Initialize form validation
function initializeFormValidation() {
    const forms = document.querySelectorAll('form');
    
    forms.forEach(form => {
        const inputs = form.querySelectorAll('input, select, textarea');
        
        inputs.forEach(input => {
            // Real-time validation
            input.addEventListener('blur', function() {
                validateField(this);
            });
            
            input.addEventListener('input', function() {
                clearFieldError(this);
            });
        });
    });
}

// Validate individual field
function validateField(field) {
    const value = field.value.trim();
    const fieldName = field.name;
    let isValid = true;
    let errorMessage = '';
    
    // Required field validation
    if (field.hasAttribute('required') && !value) {
        isValid = false;
        errorMessage = `${getFieldLabel(field)} is required.`;
    }
    
    // Email validation
    if (fieldName.includes('email') && value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
            isValid = false;
            errorMessage = 'Please enter a valid email address.';
        }
    }
    
    // Phone validation
    if (fieldName.includes('phone') && value) {
        const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
        if (!phoneRegex.test(value.replace(/\s/g, ''))) {
            isValid = false;
            errorMessage = 'Please enter a valid phone number.';
        }
    }
    
    // Username validation
    if (fieldName.includes('username') && value) {
        if (value.length < 3) {
            isValid = false;
            errorMessage = 'Username must be at least 3 characters long.';
        }
    }
    
    // Password validation
    if (fieldName.includes('password') && value) {
        if (value.length < 6) {
            isValid = false;
            errorMessage = 'Password must be at least 6 characters long.';
        }
    }
    
    // Update field appearance
    if (isValid) {
        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
        clearFieldError(field);
    } else {
        field.classList.remove('is-valid');
        field.classList.add('is-invalid');
        showFieldError(field, errorMessage);
    }
    
    return isValid;
}

// Get field label
function getFieldLabel(field) {
    const label = document.querySelector(`label[for="${field.id}"]`);
    return label ? label.textContent.replace('*', '').trim() : field.name;
}

// Show field error
function showFieldError(field, message) {
    clearFieldError(field);
    
    const errorDiv = document.createElement('div');
    errorDiv.className = 'invalid-feedback';
    errorDiv.textContent = message;
    
    field.parentNode.appendChild(errorDiv);
}

// Clear field error
function clearFieldError(field) {
    const errorDiv = field.parentNode.querySelector('.invalid-feedback');
    if (errorDiv) {
        errorDiv.remove();
    }
}

// Validate entire form
function validateForm(form) {
    const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!validateField(input)) {
            isValid = false;
        }
    });
    
    return isValid;
}

// Format phone number
function formatPhoneNumber(input) {
    let value = input.value.replace(/\D/g, '');
    if (value.length >= 6) {
        value = value.replace(/(\d{3})(\d{3})(\d+)/, '$1-$2-$3');
    }
    input.value = value;
}

// Initialize phone formatting
document.addEventListener('DOMContentLoaded', function() {
    const phoneInputs = document.querySelectorAll('input[type="tel"]');
    phoneInputs.forEach(input => {
        input.addEventListener('input', function() {
            formatPhoneNumber(this);
        });
    });
});

// Auto-resize textareas
function initializeTextareaAutoResize() {
    const textareas = document.querySelectorAll('textarea');
    textareas.forEach(textarea => {
        textarea.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = this.scrollHeight + 'px';
        });
    });
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    initializeTextareaAutoResize();
});

// Export functions for global use
window.AuthApp = {
    validateForm,
    validateField,
    formatPhoneNumber
};
