// Teachers page JavaScript
document.addEventListener('DOMContentLoaded', function() {
    loadTeachers();
});

function loadTeachers() {
    const tableBody = document.getElementById('teachersTableBody');
    
    // Show loading state
    tableBody.innerHTML = `
        <tr>
            <td colspan="8" class="text-center text-muted">
                <i class="fas fa-spinner fa-spin me-2"></i>
                Loading teachers...
            </td>
        </tr>
    `;
    
    // Check if user is authenticated
    const token = MoktobApp.getToken();
    if (!token) {
        MoktobApp.showAlert('Please login to view teachers', 'warning');
        setTimeout(() => {
            window.location.href = '/moktob/login';
        }, 2000);
        return;
    }
    
    // Fetch teachers data
    MoktobApp.apiRequest('/moktob/api/teachers')
        .then(teachers => {
            console.log('Teachers loaded:', teachers);
            displayTeachers(teachers);
        })
        .catch(error => {
            console.error('Error loading teachers:', error);
            tableBody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-danger">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Error loading teachers: ${error.message}
                    </td>
                </tr>
            `;
            MoktobApp.showAlert('Failed to load teachers', 'error');
        });
}

function displayTeachers(teachers) {
    const tableBody = document.getElementById('teachersTableBody');
    
    if (!teachers || teachers.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center text-muted">
                    <i class="fas fa-user-slash me-2"></i>
                    No teachers found
                </td>
            </tr>
        `;
        return;
    }
    
    tableBody.innerHTML = teachers.map(teacher => `
        <tr>
            <td>${teacher.id || '-'}</td>
            <td>
                <div class="d-flex align-items-center">
                    ${teacher.photoUrl ? 
                        `<img src="${teacher.photoUrl}" alt="${teacher.name}" class="rounded-circle me-2" width="32" height="32">` :
                        `<div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center me-2" style="width: 32px; height: 32px; font-size: 14px;">
                            ${teacher.name ? teacher.name.charAt(0).toUpperCase() : 'T'}
                        </div>`
                    }
                    <div>
                        <div class="fw-bold">${teacher.name || '-'}</div>
                        <small class="text-muted">${teacher.email || '-'}</small>
                    </div>
                </div>
            </td>
            <td>${teacher.phone || '-'}</td>
            <td>${teacher.qualification || '-'}</td>
            <td>${teacher.specialization || '-'}</td>
            <td>${teacher.departmentName || '-'}</td>
            <td>
                <span class="badge ${teacher.isActive ? 'bg-success' : 'bg-secondary'}">
                    ${teacher.isActive ? 'Active' : 'Inactive'}
                </span>
            </td>
            <td>
                <div class="btn-group" role="group">
                    <button type="button" class="btn btn-sm btn-outline-primary" onclick="viewTeacher(${teacher.id})" title="View">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-warning" onclick="editTeacher(${teacher.id})" title="Edit">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-danger" onclick="deleteTeacher(${teacher.id})" title="Delete">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function viewTeacher(teacherId) {
    MoktobApp.showAlert(`View teacher ${teacherId} - Feature coming soon!`, 'info');
}

function editTeacher(teacherId) {
    MoktobApp.showAlert(`Edit teacher ${teacherId} - Feature coming soon!`, 'info');
}

function deleteTeacher(teacherId) {
    if (confirm('Are you sure you want to delete this teacher?')) {
        MoktobApp.apiRequest(`/moktob/api/teachers/${teacherId}`, {
            method: 'DELETE'
        })
        .then(() => {
            MoktobApp.showAlert('Teacher deleted successfully', 'success');
            loadTeachers(); // Reload the list
        })
        .catch(error => {
            console.error('Error deleting teacher:', error);
            MoktobApp.showAlert('Failed to delete teacher', 'error');
        });
    }
}

// Search functionality
function searchTeachers() {
    const searchTerm = document.getElementById('teacherSearch').value.toLowerCase();
    const rows = document.querySelectorAll('#teachersTableBody tr');
    
    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        if (text.includes(searchTerm)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

// Add event listener for search input
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('teacherSearch');
    if (searchInput) {
        searchInput.addEventListener('input', searchTeachers);
    }
});
