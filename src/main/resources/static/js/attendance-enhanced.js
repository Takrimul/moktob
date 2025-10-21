// Enhanced Attendance Management JavaScript
class AttendanceManager {
    constructor() {
        this.currentClassId = null;
        this.currentDate = null;
        this.students = [];
        this.attendanceData = [];
        this.isEditMode = false;
        this.currentAttendanceSummary = null;
        
        this.initializeEventListeners();
        this.loadClasses();
        this.setTodayDate();
    }

    initializeEventListeners() {
        // Add event listeners for real-time updates
        document.addEventListener('change', (e) => {
            if (e.target.name && e.target.name.startsWith('attendance_')) {
                this.updateSummary();
            }
        });
    }

    // Load classes for selection
    async loadClasses() {
        try {
            const classes = await MoktobApp.apiRequest('/moktob/api/classes');
            this.populateClassDropdown(classes);
        } catch (error) {
            console.error('Error loading classes:', error);
            MoktobPopup.error({
                title: 'Error',
                message: 'Failed to load classes. Please refresh the page and try again.'
            });
        }
    }

    populateClassDropdown(classes) {
        const select = document.getElementById('classSelect');
        select.innerHTML = '<option value="">Select a class...</option>';
        
        if (classes && classes.length > 0) {
            classes.forEach(cls => {
                const option = document.createElement('option');
                option.value = cls.id;
                option.textContent = `${cls.className} - ${cls.teacherName || 'No Teacher'}`;
                select.appendChild(option);
            });
        }
    }

    // Set today's date
    setTodayDate() {
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('attendanceDate').value = today;
        this.currentDate = today;
    }

    // Set yesterday's date
    setYesterday() {
        const yesterday = new Date();
        yesterday.setDate(yesterday.getDate() - 1);
        const yesterdayStr = yesterday.toISOString().split('T')[0];
        document.getElementById('attendanceDate').value = yesterdayStr;
        this.currentDate = yesterdayStr;
        this.loadAttendance();
    }

    // Set today's date
    setToday() {
        this.setTodayDate();
        this.loadAttendance();
    }

    // Load attendance for specific class and date
    async loadAttendance() {
        const classId = document.getElementById('classSelect').value;
        const date = document.getElementById('attendanceDate').value;
        
        if (!classId || !date) {
            this.hideAttendanceForm();
            return;
        }

        this.currentClassId = parseInt(classId);
        this.currentDate = date;

        try {
            // Show loading state
            this.showLoadingState();

            // Try to get existing attendance
            const attendance = await MoktobApp.apiRequest(`/moktob/api/attendance/class/${classId}/date/${date}`);
            
            if (attendance && attendance.studentDetails && attendance.studentDetails.length > 0) {
                // Existing attendance found
                this.displayExistingAttendance(attendance);
                this.isEditMode = true;
                document.getElementById('formTitle').textContent = 'Edit Attendance';
            } else {
                // No attendance found, load students for new attendance
                await this.loadStudentsForNewAttendance(classId, date);
                this.isEditMode = false;
                document.getElementById('formTitle').textContent = 'Mark Attendance';
            }

            this.updateClassInfo(classId);
            this.showAttendanceForm();

        } catch (error) {
            if (error.status === 404) {
                // No attendance found, load students for new attendance
                await this.loadStudentsForNewAttendance(classId, date);
                this.isEditMode = false;
                document.getElementById('formTitle').textContent = 'Mark Attendance';
                this.updateClassInfo(classId);
                this.showAttendanceForm();
            } else {
                console.error('Error loading attendance:', error);
                MoktobPopup.error({
                    title: 'Error',
                    message: 'Failed to load attendance. Please try again.'
                });
            }
        }
    }

    // Load students for new attendance
    async loadStudentsForNewAttendance(classId, date) {
        try {
            const students = await MoktobApp.apiRequest(`/moktob/api/students/class/${classId}`);
            this.students = students;
            this.displayStudentsForAttendance(students);
        } catch (error) {
            console.error('Error loading students:', error);
            MoktobPopup.error({
                title: 'Error',
                message: 'Failed to load students. Please try again.'
            });
        }
    }

    // Display existing attendance
    displayExistingAttendance(attendanceSummary) {
        this.currentAttendanceSummary = attendanceSummary;
        const tbody = document.getElementById('attendanceTableBody');
        
        if (attendanceSummary.studentDetails && attendanceSummary.studentDetails.length > 0) {
            tbody.innerHTML = attendanceSummary.studentDetails.map((student, index) => `
                <tr class="student-row">
                    <td>${index + 1}</td>
                    <td>${student.studentName || 'N/A'}</td>
                    <td>${student.guardianName || 'N/A'}</td>
                    <td>${student.guardianContact || 'N/A'}</td>
                    <td class="text-center">
                        <input type="radio" name="attendance_${student.studentId}" value="present" 
                               class="attendance-checkbox" ${student.status === 'PRESENT' ? 'checked' : ''}>
                    </td>
                    <td class="text-center">
                        <input type="radio" name="attendance_${student.studentId}" value="absent" 
                               class="attendance-checkbox" ${student.status === 'ABSENT' ? 'checked' : ''}>
                    </td>
                    <td class="text-center">
                        <input type="radio" name="attendance_${student.studentId}" value="late" 
                               class="attendance-checkbox" ${student.status === 'LATE' ? 'checked' : ''}>
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" 
                               id="remarks_${student.studentId}" placeholder="Remarks" 
                               value="${student.remarks || ''}">
                    </td>
                </tr>
            `).join('');
        } else {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-muted">
                        <i class="fas fa-info-circle me-2"></i>
                        No students found in this class.
                    </td>
                </tr>
            `;
        }

        // Set general remarks
        document.getElementById('generalRemarks').value = attendanceSummary.remarks || '';
        
        // Update summary
        this.updateSummaryFromData(attendanceSummary);
    }

    // Display students for new attendance
    displayStudentsForAttendance(students) {
        const tbody = document.getElementById('attendanceTableBody');
        
        if (students && students.length > 0) {
            tbody.innerHTML = students.map((student, index) => `
                <tr class="student-row">
                    <td>${index + 1}</td>
                    <td>${student.name || 'N/A'}</td>
                    <td>${student.guardianName || 'N/A'}</td>
                    <td>${student.guardianContact || 'N/A'}</td>
                    <td class="text-center">
                        <input type="radio" name="attendance_${student.id}" value="present" class="attendance-checkbox">
                    </td>
                    <td class="text-center">
                        <input type="radio" name="attendance_${student.id}" value="absent" class="attendance-checkbox">
                    </td>
                    <td class="text-center">
                        <input type="radio" name="attendance_${student.id}" value="late" class="attendance-checkbox">
                    </td>
                    <td>
                        <input type="text" class="form-control form-control-sm" 
                               id="remarks_${student.id}" placeholder="Remarks">
                    </td>
                </tr>
            `).join('');
        } else {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-muted">
                        <i class="fas fa-info-circle me-2"></i>
                        No students found in this class.
                    </td>
                </tr>
            `;
        }

        // Clear general remarks
        document.getElementById('generalRemarks').value = '';
        
        // Reset summary
        this.resetSummary();
    }

    // Save attendance (bulk)
    async saveAttendance() {
        if (!this.currentClassId || !this.currentDate) {
            MoktobPopup.error({
                title: 'Error',
                message: 'Please select a class and date'
            });
            return;
        }

        try {
            const attendanceData = this.collectAttendanceData();
            
            if (attendanceData.length === 0) {
                MoktobPopup.error({
                    title: 'Error',
                    message: 'Please mark attendance for at least one student'
                });
                return;
            }

            const request = {
                classId: this.currentClassId,
                attendanceDate: this.currentDate,
                teacherId: TenantContextHolder.getTeacherId(),
                attendanceRecords: attendanceData,
                remarks: document.getElementById('generalRemarks').value
            };

            const response = await MoktobApp.apiRequest('/moktob/api/attendance/bulk-submit', {
                method: 'POST',
                body: JSON.stringify(request)
            });

            this.displayAttendanceSummary(response);
            MoktobPopup.success({
                title: 'Success',
                message: 'Attendance saved successfully!'
            });

        } catch (error) {
            console.error('Error saving attendance:', error);
            MoktobPopup.error({
                title: 'Error',
                message: 'Failed to save attendance. Please try again.'
            });
        }
    }

    // Collect attendance data from form
    collectAttendanceData() {
        const attendanceData = [];
        const attendanceInputs = document.querySelectorAll('input[type="radio"]:checked');
        
        attendanceInputs.forEach(input => {
            const studentId = input.name.split('_')[1];
            const remarks = document.getElementById(`remarks_${studentId}`)?.value || '';
            
            attendanceData.push({
                studentId: parseInt(studentId),
                status: input.value.toUpperCase(),
                remarks: remarks
            });
        });

        return attendanceData;
    }

    // Update attendance summary in real-time
    updateSummary() {
        const presentCount = document.querySelectorAll('input[name^="attendance_"][value="present"]:checked').length;
        const absentCount = document.querySelectorAll('input[name^="attendance_"][value="absent"]:checked').length;
        const lateCount = document.querySelectorAll('input[name^="attendance_"][value="late"]:checked').length;
        const totalStudents = this.students.length;
        const attendancePercentage = totalStudents > 0 ? ((presentCount + lateCount) / totalStudents * 100).toFixed(1) : 0;

        document.getElementById('presentCount').textContent = presentCount;
        document.getElementById('absentCount').textContent = absentCount;
        document.getElementById('lateCount').textContent = lateCount;
        document.getElementById('attendancePercentage').textContent = attendancePercentage + '%';
    }

    // Update summary from existing data
    updateSummaryFromData(attendanceSummary) {
        document.getElementById('presentCount').textContent = attendanceSummary.presentCount || 0;
        document.getElementById('absentCount').textContent = attendanceSummary.absentCount || 0;
        document.getElementById('lateCount').textContent = attendanceSummary.lateCount || 0;
        document.getElementById('attendancePercentage').textContent = (attendanceSummary.attendancePercentage || 0).toFixed(1) + '%';
    }

    // Reset summary
    resetSummary() {
        document.getElementById('presentCount').textContent = '0';
        document.getElementById('absentCount').textContent = '0';
        document.getElementById('lateCount').textContent = '0';
        document.getElementById('attendancePercentage').textContent = '0%';
    }

    // Display attendance summary
    displayAttendanceSummary(summary) {
        this.updateSummaryFromData(summary);
    }

    // Bulk actions
    selectAllPresent() {
        document.querySelectorAll('input[name^="attendance_"]').forEach(input => {
            if (input.value === 'present') {
                input.checked = true;
            }
        });
        this.updateSummary();
    }

    selectAllAbsent() {
        document.querySelectorAll('input[name^="attendance_"]').forEach(input => {
            if (input.value === 'absent') {
                input.checked = true;
            }
        });
        this.updateSummary();
    }

    clearAll() {
        document.querySelectorAll('input[name^="attendance_"]').forEach(input => {
            input.checked = false;
        });
        this.updateSummary();
    }

    // Load attendance history
    async loadAttendanceHistory() {
        if (!this.currentClassId) {
            MoktobPopup.error({
                title: 'Error',
                message: 'Please select a class first'
            });
            return;
        }

        const startDate = document.getElementById('historyStartDate').value;
        const endDate = document.getElementById('historyEndDate').value;

        if (!startDate || !endDate) {
            MoktobPopup.error({
                title: 'Error',
                message: 'Please select start and end dates for history'
            });
            return;
        }

        try {
            const history = await MoktobApp.apiRequest(
                `/moktob/api/attendance/class/${this.currentClassId}/history?startDate=${startDate}&endDate=${endDate}`
            );
            
            this.displayAttendanceHistory(history.content || history);
        } catch (error) {
            console.error('Error loading attendance history:', error);
            MoktobPopup.error({
                title: 'Error',
                message: 'Failed to load attendance history. Please try again.'
            });
        }
    }

    // Display attendance history
    displayAttendanceHistory(history) {
        const tbody = document.getElementById('historyTableBody');
        
        if (history && history.length > 0) {
            tbody.innerHTML = history.map(item => `
                <tr>
                    <td>${new Date(item.attendanceDate).toLocaleDateString()}</td>
                    <td class="text-center">
                        <span class="badge bg-success">${item.presentCount || 0}</span>
                    </td>
                    <td class="text-center">
                        <span class="badge bg-danger">${item.absentCount || 0}</span>
                    </td>
                    <td class="text-center">
                        <span class="badge bg-warning">${item.lateCount || 0}</span>
                    </td>
                    <td class="text-center">
                        <span class="badge bg-info">${(item.attendancePercentage || 0).toFixed(1)}%</span>
                    </td>
                    <td class="text-center">
                        <button class="btn btn-sm btn-outline-primary" onclick="editAttendanceHistory('${item.attendanceDate}')">
                            <i class="fas fa-edit"></i>
                        </button>
                    </td>
                </tr>
            `).join('');
        } else {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center text-muted">
                        <i class="fas fa-info-circle me-2"></i>
                        No attendance history found for the selected period.
                    </td>
                </tr>
            `;
        }
    }

    // Edit attendance history
    editAttendanceHistory(date) {
        document.getElementById('attendanceDate').value = date;
        this.currentDate = date;
        this.loadAttendance();
    }

    // Update class information
    updateClassInfo(classId) {
        const classInfo = document.getElementById('classInfo');
        const selectedOption = document.getElementById('classSelect').selectedOptions[0];
        
        if (selectedOption) {
            classInfo.innerHTML = `
                <div class="row">
                    <div class="col-12">
                        <h6 class="text-primary">${selectedOption.textContent}</h6>
                        <p class="text-muted mb-1">Class ID: ${classId}</p>
                        <p class="text-muted mb-0">Date: ${this.currentDate}</p>
                    </div>
                </div>
            `;
        }
    }

    // Show/hide methods
    showAttendanceForm() {
        document.getElementById('attendanceFormCard').style.display = 'block';
        document.getElementById('statusPanel').style.display = 'block';
        document.getElementById('attendanceHistoryCard').style.display = 'block';
        document.getElementById('noClassCard').style.display = 'none';
    }

    hideAttendanceForm() {
        document.getElementById('attendanceFormCard').style.display = 'none';
        document.getElementById('statusPanel').style.display = 'none';
        document.getElementById('attendanceHistoryCard').style.display = 'none';
        document.getElementById('noClassCard').style.display = 'block';
    }

    showLoadingState() {
        const tbody = document.getElementById('attendanceTableBody');
        tbody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center text-muted">
                    <i class="fas fa-spinner fa-spin me-2"></i>
                    Loading attendance data...
                </td>
            </tr>
        `;
    }
}

// Global functions for backward compatibility
let attendanceManager;

// Initialize when page loads
document.addEventListener('DOMContentLoaded', function() {
    attendanceManager = new AttendanceManager();
    
    // Set default history date range (last 30 days)
    const today = new Date();
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(today.getDate() - 30);
    
    document.getElementById('historyStartDate').value = thirtyDaysAgo.toISOString().split('T')[0];
    document.getElementById('historyEndDate').value = today.toISOString().split('T')[0];
});

// Global functions
function loadClasses() {
    if (attendanceManager) {
        attendanceManager.loadClasses();
    }
}

function loadAttendance() {
    if (attendanceManager) {
        attendanceManager.loadAttendance();
    }
}

function saveAttendance() {
    if (attendanceManager) {
        attendanceManager.saveAttendance();
    }
}

function selectAllPresent() {
    if (attendanceManager) {
        attendanceManager.selectAllPresent();
    }
}

function selectAllAbsent() {
    if (attendanceManager) {
        attendanceManager.selectAllAbsent();
    }
}

function clearAll() {
    if (attendanceManager) {
        attendanceManager.clearAll();
    }
}

function loadAttendanceHistory() {
    if (attendanceManager) {
        attendanceManager.loadAttendanceHistory();
    }
}

function setToday() {
    if (attendanceManager) {
        attendanceManager.setToday();
    }
}

function setYesterday() {
    if (attendanceManager) {
        attendanceManager.setYesterday();
    }
}

function refreshAttendance() {
    if (attendanceManager) {
        attendanceManager.loadClasses();
        attendanceManager.loadAttendance();
    }
}

function editAttendanceHistory(date) {
    if (attendanceManager) {
        attendanceManager.editAttendanceHistory(date);
    }
}
