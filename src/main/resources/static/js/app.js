// Moktob Management System - Main JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize all components
    initializeSidebar();
    initializeCharts();
    initializeDataTables();
    initializeTooltips();
    initializeAlerts();
    initializeForms();
});

// Sidebar functionality
function initializeSidebar() {
    const sidebarToggle = document.querySelector('[data-bs-toggle="offcanvas"]');
    const sidebar = document.querySelector('#sidebar');
    
    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', function() {
            sidebar.classList.toggle('show');
        });
    }
    
    // Close sidebar when clicking outside
    document.addEventListener('click', function(event) {
        if (sidebar && !sidebar.contains(event.target) && !sidebarToggle.contains(event.target)) {
            sidebar.classList.remove('show');
        }
    });
}

// Chart initialization
function initializeCharts() {
    // Initialize any charts that are present
    const chartElements = document.querySelectorAll('canvas');
    chartElements.forEach(canvas => {
        if (canvas.id === 'attendanceTrendChart') {
            initializeAttendanceTrendChart(canvas);
        } else if (canvas.id === 'classAttendanceChart') {
            initializeClassAttendanceChart(canvas);
        }
    });
}

// Attendance Trend Chart
function initializeAttendanceTrendChart(canvas) {
    const ctx = canvas.getContext('2d');
    const chart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: 'Attendance Rate',
                data: [],
                borderColor: 'rgb(78, 115, 223)',
                backgroundColor: 'rgba(78, 115, 223, 0.1)',
                tension: 0.1,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    ticks: {
                        callback: function(value) {
                            return value + '%';
                        }
                    }
                }
            },
            elements: {
                point: {
                    radius: 4,
                    hoverRadius: 6
                }
            }
        }
    });
    
    // Load data from API
    loadAttendanceTrendData(chart);
}

// Class Attendance Chart
function initializeClassAttendanceChart(canvas) {
    const ctx = canvas.getContext('2d');
    const chart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Present', 'Absent', 'Late'],
            datasets: [{
                data: [0, 0, 0],
                backgroundColor: ['#4e73df', '#1cc88a', '#36b9cc'],
                hoverBackgroundColor: ['#2e59d9', '#17a673', '#2c9faf'],
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 20,
                        usePointStyle: true
                    }
                }
            }
        }
    });
    
    // Load data from API
    loadClassAttendanceData(chart);
}

// Load attendance trend data from API
function loadAttendanceTrendData(chart) {
    fetch('/moktob/api/dashboard/attendance-trends')
        .then(response => response.json())
        .then(data => {
            const labels = data.map(item => new Date(item.date).toLocaleDateString());
            const attendanceRates = data.map(item => item.attendanceRate);
            
            chart.data.labels = labels;
            chart.data.datasets[0].data = attendanceRates;
            chart.update();
        })
        .catch(error => {
            console.error('Error loading attendance trend data:', error);
        });
}

// Load class attendance data from API
function loadClassAttendanceData(chart) {
    fetch('/moktob/api/dashboard/overview')
        .then(response => response.json())
        .then(data => {
            const presentCount = data.classAttendanceSummaries.reduce((sum, item) => sum + item.presentCount, 0);
            const absentCount = data.classAttendanceSummaries.reduce((sum, item) => sum + item.absentCount, 0);
            const lateCount = data.classAttendanceSummaries.reduce((sum, item) => sum + item.lateCount, 0);
            
            chart.data.datasets[0].data = [presentCount, absentCount, lateCount];
            chart.update();
        })
        .catch(error => {
            console.error('Error loading class attendance data:', error);
        });
}

// Data Tables initialization
function initializeDataTables() {
    const tables = document.querySelectorAll('.table');
    tables.forEach(table => {
        // Add hover effects
        const rows = table.querySelectorAll('tbody tr');
        rows.forEach(row => {
            row.addEventListener('mouseenter', function() {
                this.style.backgroundColor = '#f8f9fc';
            });
            row.addEventListener('mouseleave', function() {
                this.style.backgroundColor = '';
            });
        });
    });
}

// Tooltips initialization
function initializeTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

// Alert management
function initializeAlerts() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        // Auto-dismiss alerts after 5 seconds
        if (alert.classList.contains('alert-success')) {
            setTimeout(() => {
                alert.style.opacity = '0';
                setTimeout(() => {
                    alert.remove();
                }, 300);
            }, 5000);
        }
    });
}

// Form handling
function initializeForms() {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(event) {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.classList.add('btn-loading');
                submitBtn.disabled = true;
            }
        });
    });
}

// Utility functions
function showAlert(message, type = 'info') {
    const alertContainer = document.querySelector('.alert-container') || createAlertContainer();
    const alert = document.createElement('div');
    alert.className = `alert alert-${type} alert-dismissible fade show`;
    alert.innerHTML = `
        <i class="fas fa-${getAlertIcon(type)} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    alertContainer.appendChild(alert);
    
    // Auto-dismiss after 5 seconds
    setTimeout(() => {
        alert.remove();
    }, 5000);
}

function createAlertContainer() {
    const container = document.createElement('div');
    container.className = 'alert-container position-fixed top-0 end-0 p-3';
    container.style.zIndex = '9999';
    document.body.appendChild(container);
    return container;
}

function getAlertIcon(type) {
    const icons = {
        'success': 'check-circle',
        'danger': 'exclamation-triangle',
        'warning': 'exclamation-circle',
        'info': 'info-circle'
    };
    return icons[type] || 'info-circle';
}

// API helper functions
function apiRequest(url, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}`
        }
    };
    
    return fetch(url, { ...defaultOptions, ...options })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        });
}

function getToken() {
    return localStorage.getItem('authToken') || sessionStorage.getItem('authToken');
}

function setToken(token, remember = false) {
    // Set token in localStorage/sessionStorage for API calls
    if (remember) {
        localStorage.setItem('authToken', token);
    } else {
        sessionStorage.setItem('authToken', token);
    }
    
    // Also set token in cookie for web page requests
    const expires = remember ? 30 : 1; // 30 days or 1 day
    const date = new Date();
    date.setTime(date.getTime() + (expires * 24 * 60 * 60 * 1000));
    const expiresStr = date.toUTCString();
    
    document.cookie = `authToken=${token}; expires=${expiresStr}; path=/; SameSite=Strict`;
}

function removeToken() {
    localStorage.removeItem('authToken');
    sessionStorage.removeItem('authToken');
    
    // Also remove token from cookie
    document.cookie = 'authToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
}

// Export functions for global use
window.MoktobApp = {
    showAlert,
    apiRequest,
    getToken,
    setToken,
    removeToken
};
