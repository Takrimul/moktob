let selectedClassId = null;
let selectedDate = null;
let selectedTeacherId = null;
let students = [];
let attendanceData = {};
let classes = [];

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    loadClasses();
    setDefaultDate();
    loadUserInfo();
});

function setDefaultDate() {
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('attendanceDate').value = today;
}

function loadUserInfo() {
    const token = localStorage.getItem('jwt');
    if (token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            document.getElementById('userName').textContent = payload.username || 'User';
        } catch (error) {
            console.error('Error parsing JWT token:', error);
        }
    }
}

async function loadClasses() {
    try {
        classes = await MoktobApp.apiRequest('/moktob/api/classes/dropdown');
        const classSelect = document.getElementById('classSelect');
        
        classSelect.innerHTML = '<option value="">Select a class...</option>';
        classes.forEach(cls => {
            const option = document.createElement('option');
            option.value = cls.id;
            option.textContent = `${cls.className} - ${cls.teacherName}`;
            option.dataset.teacherId = cls.teacherId; // Store teacherId in dataset
            classSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Error loading classes:', error);
        MoktobPopup.error({
            title: 'Error',
            message: 'Failed to load classes. Please refresh the page and try again.'
        });
    }
}

async function loadClassStudents() {
    const classSelect = document.getElementById('classSelect');
    const dateInput = document.getElementById('attendanceDate');
    
    selectedClassId = classSelect.value;
    selectedDate = dateInput.value;
    
    // Get teacherId from selected option
    const selectedOption = classSelect.selectedOptions[0];
    selectedTeacherId = selectedOption ? selectedOption.dataset.teacherId : null;
    
    console.log('Selected class:', selectedClassId, 'Teacher ID:', selectedTeacherId);
    
    if (!selectedClassId || !selectedDate || !selectedTeacherId) {
        hideAttendanceForm();
        return;
    }
    
    try {
        // Load students for the selected class
        const allStudents = await MoktobApp.apiRequest('/moktob/api/students');
        students = allStudents.filter(student => student.currentClassId == selectedClassId);
        
        // Load existing attendance for the date
        await loadExistingAttendance();
        
        // Update class info
        updateClassInfo();
        
        // Show attendance form
        showAttendanceForm();
        
    } catch (error) {
        console.error('Error loading students:', error);
        MoktobPopup.error({
            title: 'Error',
            message: 'Failed to load students. Please try again.'
        });
    }
}

async function loadExistingAttendance() {
    try {
        const existingAttendance = await MoktobApp.apiRequest(`/moktob/api/attendance/by-date?date=${selectedDate}&classId=${selectedClassId}`);
        
        // Initialize attendance data
        attendanceData = {};
        students.forEach(student => {
            attendanceData[student.id] = 'PRESENT'; // Default to present
        });
        
        // Apply existing attendance
        existingAttendance.forEach(attendance => {
            if (attendanceData.hasOwnProperty(attendance.studentId)) {
                attendanceData[attendance.studentId] = attendance.status;
            }
        });
        
    } catch (error) {
        console.error('Error loading existing attendance:', error);
        // Initialize with default values
        attendanceData = {};
        students.forEach(student => {
            attendanceData[student.id] = 'PRESENT';
        });
    }
}

function updateClassInfo() {
    const classInfo = document.getElementById('classInfo');
    const selectedOption = document.getElementById('classSelect').selectedOptions[0];
    
    if (selectedOption && selectedOption.value) {
        const className = selectedOption.textContent.split(' - ')[0];
        const teacherName = selectedOption.textContent.split(' - ')[1];
        
        classInfo.innerHTML = `
            <div class="row">
                <div class="col-6">
                    <strong>Class:</strong><br>
                    <span class="text-primary">${className}</span>
                </div>
                <div class="col-6">
                    <strong>Teacher:</strong><br>
                    <span class="text-success">${teacherName}</span>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-6">
                    <strong>Total Students:</strong><br>
                    <span class="text-info">${students.length}</span>
                </div>
                <div class="col-6">
                    <strong>Date:</strong><br>
                    <span class="text-warning">${formatDate(selectedDate)}</span>
                </div>
            </div>
        `;
    }
}

function showAttendanceForm() {
    document.getElementById('attendanceFormCard').style.display = 'block';
    document.getElementById('noClassCard').style.display = 'none';
    renderAttendanceTable();
}

function hideAttendanceForm() {
    document.getElementById('attendanceFormCard').style.display = 'none';
    document.getElementById('noClassCard').style.display = 'block';
}

function renderAttendanceTable() {
    const tbody = document.getElementById('attendanceTableBody');
    
    if (students.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted py-4">
                    <i class="fas fa-info-circle me-2"></i>
                    No students found in this class.
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = students.map((student, index) => {
        const currentStatus = attendanceData[student.id] || 'PRESENT';
        
        return `
            <tr class="student-row" data-student-id="${student.id}">
                <td>${index + 1}</td>
                <td>
                    <strong>${student.name || 'N/A'}</strong>
                </td>
                <td>${student.guardianName || 'N/A'}</td>
                <td>${student.guardianContact || 'N/A'}</td>
                <td class="text-center">
                    <input type="radio" 
                           name="attendance_${student.id}" 
                           value="PRESENT" 
                           class="form-check-input attendance-checkbox"
                           ${currentStatus === 'PRESENT' ? 'checked' : ''}
                           onchange="updateAttendanceStatus(${student.id}, 'PRESENT')">
                </td>
                <td class="text-center">
                    <input type="radio" 
                           name="attendance_${student.id}" 
                           value="ABSENT" 
                           class="form-check-input attendance-checkbox"
                           ${currentStatus === 'ABSENT' ? 'checked' : ''}
                           onchange="updateAttendanceStatus(${student.id}, 'ABSENT')">
                </td>
                <td class="text-center">
                    <input type="radio" 
                           name="attendance_${student.id}" 
                           value="LATE" 
                           class="form-check-input attendance-checkbox"
                           ${currentStatus === 'LATE' ? 'checked' : ''}
                           onchange="updateAttendanceStatus(${student.id}, 'LATE')">
                </td>
            </tr>
        `;
    }).join('');
    
    // Update row colors based on attendance status
    updateRowColors();
}

function updateAttendanceStatus(studentId, status) {
    attendanceData[studentId] = status;
    updateRowColors();
}

function updateRowColors() {
    const rows = document.querySelectorAll('.student-row');
    rows.forEach(row => {
        const studentId = parseInt(row.dataset.studentId);
        const status = attendanceData[studentId];
        
        // Remove existing status classes
        row.classList.remove('status-present', 'status-absent', 'status-late');
        
        // Add new status class
        if (status === 'PRESENT') {
            row.classList.add('status-present');
        } else if (status === 'ABSENT') {
            row.classList.add('status-absent');
        } else if (status === 'LATE') {
            row.classList.add('status-late');
        }
    });
}

async function saveAttendance() {
    if (!selectedClassId || !selectedDate) {
        MoktobPopup.error({
            title: 'Error',
            message: 'Please select a class and date first.'
        });
        return;
    }
    
    if (students.length === 0) {
        MoktobPopup.error({
            title: 'Error',
            message: 'No students found in the selected class.'
        });
        return;
    }
    
    try {
        // Prepare attendance data
        const attendanceRecords = students.map(student => ({
            classId: parseInt(selectedClassId),
            studentId: student.id,
            teacherId: parseInt(selectedTeacherId),
            attendanceDate: selectedDate,
            status: attendanceData[student.id] || 'PRESENT'
        }));
        
        console.log('Saving attendance:', attendanceRecords);
        
        // Save attendance
        const response = await MoktobApp.apiRequest('/moktob/api/attendance/bulk', {
            method: 'POST',
            body: JSON.stringify(attendanceRecords)
        });
        
        console.log('Attendance saved successfully:', response);
        
        MoktobPopup.success({
            title: 'Success',
            message: `Attendance saved successfully for ${students.length} students.`,
            onConfirm: () => {
                // Optionally refresh the data
                loadClassStudents();
            }
        });
        
    } catch (error) {
        console.error('Error saving attendance:', error);
        MoktobPopup.error({
            title: 'Error',
            message: 'Failed to save attendance. Please try again.'
        });
    }
}

function refreshAttendance() {
    if (selectedClassId && selectedDate) {
        loadClassStudents();
    } else {
        loadClasses();
    }
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

function logout() {
    MoktobPopup.confirm({
        title: 'Logout',
        message: 'Are you sure you want to logout?',
        confirmText: 'Logout',
        cancelText: 'Cancel',
        type: 'warning',
        onConfirm: () => {
            localStorage.removeItem('jwt');
            window.location.href = '/moktob/login';
        }
    });
}
