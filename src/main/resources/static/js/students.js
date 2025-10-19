document.addEventListener('DOMContentLoaded', () => {
    loadStudents();
});

async function loadStudents() {
    const studentsTableBody = document.getElementById('studentsTableBody');
    studentsTableBody.innerHTML = `
        <tr>
            <td colspan="8" class="text-center text-muted">
                <i class="fas fa-spinner fa-spin me-2"></i>
                Loading students...
            </td>
        </tr>
    `;

    try {
        const students = await MoktobApp.apiRequest('/api/students');
        renderStudentsTable(students);
    } catch (error) {
        console.error('Error loading students:', error);
        studentsTableBody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center text-danger">
                    <i class="fas fa-exclamation-circle me-2"></i>
                    Failed to load students. Please try again.
                </td>
            </tr>
        `;
        MoktobPopup.error({
            title: 'Error',
            message: 'Failed to load students. Please refresh the page and try again.'
        });
    }
}

function renderStudentsTable(students) {
    const studentsTableBody = document.getElementById('studentsTableBody');
    studentsTableBody.innerHTML = ''; // Clear loading message

    if (students.length === 0) {
        studentsTableBody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center text-muted">
                    <i class="fas fa-info-circle me-2"></i>
                    No students found. Add your first student to get started.
                </td>
            </tr>
        `;
        return;
    }

    students.forEach(student => {
        const row = studentsTableBody.insertRow();
        row.innerHTML = `
            <td>${student.id}</td>
            <td>
                <div class="d-flex align-items-center">
                    <img src="${student.photoUrl || '/images/default-avatar.png'}" alt="Avatar" class="rounded-circle me-2" width="30" height="30">
                    <span>${student.name}</span>
                </div>
            </td>
            <td>${student.email || 'N/A'}</td>
            <td>${student.phone || 'N/A'}</td>
            <td>${student.className || 'N/A'}</td>
            <td>${student.guardianName || 'N/A'}</td>
            <td>
                <span class="badge bg-success">Active</span>
            </td>
            <td>
                <button class="btn btn-sm btn-info me-1" title="View" onclick="viewStudent(${student.id})">
                    <i class="fas fa-eye"></i>
                </button>
                <button class="btn btn-sm btn-warning me-1" title="Edit" onclick="editStudent(${student.id})">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-danger" title="Delete" onclick="deleteStudent(${student.id})">
                    <i class="fas fa-trash-alt"></i>
                </button>
            </td>
        `;
    });
}

function viewStudent(studentId) {
    MoktobPopup.alert({
        title: 'View Student',
        message: `View functionality for student ID ${studentId} will be implemented soon.`,
        confirmText: 'OK'
    });
}

function editStudent(studentId) {
    MoktobPopup.alert({
        title: 'Edit Student',
        message: `Edit functionality for student ID ${studentId} will be implemented soon.`,
        confirmText: 'OK'
    });
}

function deleteStudent(studentId) {
    MoktobPopup.confirm({
        title: 'Delete Student',
        message: 'Are you sure you want to delete this student? This action cannot be undone.',
        confirmText: 'Delete',
        cancelText: 'Cancel',
        type: 'error',
        onConfirm: async () => {
            try {
                await MoktobApp.apiRequest(`/api/students/${studentId}`, {
                    method: 'DELETE'
                });
                
                MoktobPopup.success({
                    title: 'Success',
                    message: 'Student has been deleted successfully.',
                    onConfirm: () => {
                        loadStudents(); // Reload the table
                    }
                });
            } catch (error) {
                console.error('Error deleting student:', error);
                MoktobPopup.error({
                    title: 'Error',
                    message: 'Failed to delete student. Please try again.'
                });
            }
        }
    });
}
