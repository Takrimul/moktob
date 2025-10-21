// Assessment Management JavaScript
let assessments = [];
let students = [];
let classes = [];
let currentView = 'table';

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    loadInitialData();
    setDefaultDates();
});

async function loadInitialData() {
    try {
        await Promise.all([
            loadAssessments(),
            loadClassesForAssessment()
        ]);
        updateStatistics();
        populateFilters();
    } catch (error) {
        console.error('Error loading initial data:', error);
        showError('Failed to load assessment data');
    }
}

async function loadAssessments() {
    try {
        const response = await MoktobApp.apiRequest('/moktob/api/assessments');
        assessments = response || [];
        renderAssessments();
    } catch (error) {
        console.error('Error loading assessments:', error);
        showError('Failed to load assessments');
    }
}

async function loadClassesForAssessment() {
    try {
        const response = await MoktobApp.apiRequest('/moktob/api/assessments/classes');
        classes = response || [];
        populateClassSelect();
        populateClassFilter();
    } catch (error) {
        console.error('Error loading classes for assessment:', error);
        showError('Failed to load classes');
    }
}

async function loadStudentsForClass(classId) {
    try {
        const response = await MoktobApp.apiRequest(`/moktob/api/assessments/classes/${classId}/students`);
        students = response || [];
        populateStudentSelect();
    } catch (error) {
        console.error('Error loading students for class:', error);
        showError('Failed to load students for selected class');
    }
}

function populateStudentSelect() {
    const select = document.getElementById('studentSelect');
    select.innerHTML = '<option value="">Select Student</option>';
    
    students.forEach(student => {
        const option = document.createElement('option');
        option.value = student.id;
        option.textContent = `${student.name} (${student.className || 'No Class'})`;
        select.appendChild(option);
    });
}

function populateClassSelect() {
    const select = document.getElementById('classSelect');
    select.innerHTML = '<option value="">Select Class</option>';
    
    classes.forEach(cls => {
        const option = document.createElement('option');
        option.value = cls.id;
        option.textContent = `${cls.className} (${cls.teacherName || 'No Teacher'})`;
        select.appendChild(option);
    });
}

function populateClassFilter() {
    const select = document.getElementById('classFilter');
    select.innerHTML = '<option value="">All Classes</option>';
    
    classes.forEach(cls => {
        const option = document.createElement('option');
        option.value = cls.id;
        option.textContent = cls.className;
        select.appendChild(option);
    });
}

function populateFilters() {
    // Additional filter population logic if needed
}

function setDefaultDates() {
    const today = new Date();
    const lastMonth = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
    
    document.getElementById('dateFrom').value = lastMonth.toISOString().split('T')[0];
    document.getElementById('dateTo').value = today.toISOString().split('T')[0];
    document.getElementById('assessmentDate').value = today.toISOString().split('T')[0];
}

function renderAssessments() {
    if (currentView === 'table') {
        renderTableView();
    } else {
        renderCardView();
    }
}

function renderTableView() {
    const tbody = document.getElementById('assessmentsTableBody');
    
    if (assessments.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="9" class="text-center text-muted">
                    <i class="fas fa-info-circle me-2"></i>
                    No assessments found
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = assessments.map(assessment => `
        <tr>
            <td>
                <div class="d-flex align-items-center">
                    <div class="avatar-sm bg-primary rounded-circle d-flex align-items-center justify-content-center me-2">
                        <i class="fas fa-user text-white"></i>
                    </div>
                    <div>
                        <div class="fw-bold">${assessment.studentName || 'Unknown'}</div>
                        <small class="text-muted">${assessment.className || 'No Class'}</small>
                    </div>
                </div>
            </td>
            <td>
                <span class="badge assessment-type-badge bg-info">${assessment.assessmentType || 'N/A'}</span>
            </td>
            <td>
                <div>
                    <div class="fw-bold">${assessment.surahName || 'N/A'}</div>
                    ${assessment.startAyah && assessment.endAyah ? 
                        `<small class="text-muted">Ayah ${assessment.startAyah}-${assessment.endAyah}</small>` : 
                        ''
                    }
                </div>
            </td>
            <td>
                <div class="small">
                    ${assessment.recitationScore !== null ? `<div>Recitation: ${assessment.recitationScore}</div>` : ''}
                    ${assessment.tajweedScore !== null ? `<div>Tajweed: ${assessment.tajweedScore}</div>` : ''}
                    ${assessment.memorizationScore !== null ? `<div>Memorization: ${assessment.memorizationScore}</div>` : ''}
                    ${assessment.comprehensionScore !== null ? `<div>Comprehension: ${assessment.comprehensionScore}</div>` : ''}
                    ${assessment.disciplineScore !== null ? `<div>Discipline: ${assessment.disciplineScore}</div>` : ''}
                </div>
            </td>
            <td>
                <span class="badge score-badge bg-primary">${assessment.overallScore ? assessment.overallScore.toFixed(1) : 'N/A'}</span>
            </td>
            <td>
                <span class="badge score-badge grade-${getGradeClass(assessment.grade)}">${assessment.grade || 'N/A'}</span>
            </td>
            <td>
                <div>
                    <div>${formatDate(assessment.assessmentDate)}</div>
                    <small class="text-muted">${assessment.assessmentTime ? formatTime(assessment.assessmentTime) : ''}</small>
                </div>
            </td>
            <td>
                <span class="badge ${assessment.isCompleted ? 'bg-success' : 'bg-warning'}">
                    ${assessment.isCompleted ? 'Completed' : 'Pending'}
                </span>
            </td>
            <td>
                <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-primary" onclick="viewAssessment(${assessment.id})" title="View">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-outline-secondary" onclick="editAssessment(${assessment.id})" title="Edit">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-outline-danger" onclick="deleteAssessment(${assessment.id})" title="Delete">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function renderCardView() {
    const container = document.getElementById('assessmentsCardContainer');
    
    if (assessments.length === 0) {
        container.innerHTML = `
            <div class="col-12 text-center text-muted py-5">
                <i class="fas fa-info-circle fa-3x mb-3"></i>
                <h5>No assessments found</h5>
            </div>
        `;
        return;
    }
    
    container.innerHTML = assessments.map(assessment => `
        <div class="col-md-6 col-lg-4 mb-3">
            <div class="card assessment-card h-100">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h6 class="mb-0">${assessment.studentName || 'Unknown'}</h6>
                    <span class="badge assessment-type-badge bg-info">${assessment.assessmentType || 'N/A'}</span>
                </div>
                <div class="card-body">
                    <div class="mb-2">
                        <strong>Surah:</strong> ${assessment.surahName || 'N/A'}
                        ${assessment.startAyah && assessment.endAyah ? 
                            `<br><small class="text-muted">Ayah ${assessment.startAyah}-${assessment.endAyah}</small>` : 
                            ''
                        }
                    </div>
                    <div class="mb-2">
                        <strong>Overall Score:</strong> 
                        <span class="badge score-badge bg-primary">${assessment.overallScore ? assessment.overallScore.toFixed(1) : 'N/A'}</span>
                        <span class="badge score-badge grade-${getGradeClass(assessment.grade)} ms-1">${assessment.grade || 'N/A'}</span>
                    </div>
                    <div class="mb-2">
                        <strong>Date:</strong> ${formatDate(assessment.assessmentDate)}
                        ${assessment.assessmentTime ? `<br><small class="text-muted">${formatTime(assessment.assessmentTime)}</small>` : ''}
                    </div>
                    <div class="mb-2">
                        <strong>Status:</strong> 
                        <span class="badge ${assessment.isCompleted ? 'bg-success' : 'bg-warning'}">
                            ${assessment.isCompleted ? 'Completed' : 'Pending'}
                        </span>
                    </div>
                    ${assessment.teacherFeedback ? `
                        <div class="mb-2">
                            <strong>Feedback:</strong>
                            <p class="small text-muted mb-0">${truncateText(assessment.teacherFeedback, 100)}</p>
                        </div>
                    ` : ''}
                </div>
                <div class="card-footer">
                    <div class="btn-group w-100">
                        <button class="btn btn-outline-primary btn-sm" onclick="viewAssessment(${assessment.id})">
                            <i class="fas fa-eye me-1"></i>View
                        </button>
                        <button class="btn btn-outline-secondary btn-sm" onclick="editAssessment(${assessment.id})">
                            <i class="fas fa-edit me-1"></i>Edit
                        </button>
                        <button class="btn btn-outline-danger btn-sm" onclick="deleteAssessment(${assessment.id})">
                            <i class="fas fa-trash me-1"></i>Delete
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

function updateStatistics() {
    const total = assessments.length;
    const completed = assessments.filter(a => a.isCompleted).length;
    const pending = total - completed;
    const averageScore = assessments.length > 0 ? 
        assessments.reduce((sum, a) => sum + (a.overallScore || 0), 0) / assessments.length : 0;
    
    document.getElementById('totalAssessments').textContent = total;
    document.getElementById('completedAssessments').textContent = completed;
    document.getElementById('pendingAssessments').textContent = pending;
    document.getElementById('averageScore').textContent = averageScore.toFixed(1);
}

function filterAssessments() {
    const classFilter = document.getElementById('classFilter').value;
    const typeFilter = document.getElementById('typeFilter').value;
    const gradeFilter = document.getElementById('gradeFilter').value;
    const dateFrom = document.getElementById('dateFrom').value;
    const dateTo = document.getElementById('dateTo').value;
    
    let filtered = [...assessments];
    
    if (classFilter) {
        filtered = filtered.filter(a => a.classId == classFilter);
    }
    
    if (typeFilter) {
        filtered = filtered.filter(a => a.assessmentType === typeFilter);
    }
    
    if (gradeFilter) {
        filtered = filtered.filter(a => a.grade === gradeFilter);
    }
    
    if (dateFrom) {
        filtered = filtered.filter(a => new Date(a.assessmentDate) >= new Date(dateFrom));
    }
    
    if (dateTo) {
        filtered = filtered.filter(a => new Date(a.assessmentDate) <= new Date(dateTo));
    }
    
    // Temporarily replace assessments with filtered results
    const originalAssessments = assessments;
    assessments = filtered;
    renderAssessments();
    updateStatistics();
    assessments = originalAssessments;
}

function toggleView() {
    const tableView = document.getElementById('tableView');
    const cardView = document.getElementById('cardView');
    const toggleIcon = document.getElementById('viewToggleIcon');
    
    if (currentView === 'table') {
        tableView.style.display = 'none';
        cardView.style.display = 'block';
        toggleIcon.className = 'fas fa-list';
        currentView = 'card';
    } else {
        tableView.style.display = 'block';
        cardView.style.display = 'none';
        toggleIcon.className = 'fas fa-th';
        currentView = 'table';
    }
    
    renderAssessments();
}

function showAddAssessmentModal() {
    // Reset form
    document.getElementById('assessmentForm').reset();
    document.getElementById('studentSelect').disabled = true;
    document.getElementById('studentSelect').innerHTML = '<option value="">Select Class First</option>';
    setDefaultDates();
    
    const modal = new bootstrap.Modal(document.getElementById('addAssessmentModal'));
    modal.show();
}

// Handle class selection change
function onClassSelectChange() {
    const classSelect = document.getElementById('classSelect');
    const studentSelect = document.getElementById('studentSelect');
    const selectedClassId = classSelect.value;
    
    if (selectedClassId) {
        studentSelect.disabled = false;
        studentSelect.innerHTML = '<option value="">Loading students...</option>';
        loadStudentsForClass(selectedClassId);
    } else {
        studentSelect.disabled = true;
        studentSelect.innerHTML = '<option value="">Select Class First</option>';
    }
}

async function saveAssessment() {
    const form = document.getElementById('assessmentForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const assessmentData = {
        classId: parseInt(document.getElementById('classSelect').value),
        studentId: parseInt(document.getElementById('studentSelect').value),
        assessmentType: document.getElementById('assessmentType').value,
        assessmentDate: document.getElementById('assessmentDate').value,
        assessmentDurationMinutes: parseInt(document.getElementById('duration').value) || null,
        surahName: document.getElementById('surahName').value || null,
        startAyah: parseInt(document.getElementById('startAyah').value) || null,
        endAyah: parseInt(document.getElementById('endAyah').value) || null,
        recitationScore: parseInt(document.getElementById('recitationScore').value) || null,
        tajweedScore: parseInt(document.getElementById('tajweedScore').value) || null,
        memorizationScore: parseInt(document.getElementById('memorizationScore').value) || null,
        comprehensionScore: parseInt(document.getElementById('comprehensionScore').value) || null,
        disciplineScore: parseInt(document.getElementById('disciplineScore').value) || null,
        teacherFeedback: document.getElementById('teacherFeedback').value || null,
        studentStrengths: document.getElementById('studentStrengths').value || null,
        areasForImprovement: document.getElementById('areasForImprovement').value || null,
        homeworkAssigned: document.getElementById('homeworkAssigned').value || null,
        nextAssessmentDate: document.getElementById('nextAssessmentDate').value || null,
        isCompleted: document.getElementById('isCompleted').checked
    };
    
    try {
        const response = await MoktobApp.apiRequest('/moktob/api/assessments', {
            method: 'POST',
            body: JSON.stringify(assessmentData)
        });
        
        showSuccess('Assessment created successfully!');
        bootstrap.Modal.getInstance(document.getElementById('addAssessmentModal')).hide();
        form.reset();
        setDefaultDates();
        loadAssessments();
    } catch (error) {
        console.error('Error saving assessment:', error);
        showError('Failed to save assessment');
    }
}

function viewAssessment(id) {
    const assessment = assessments.find(a => a.id === id);
    if (!assessment) return;
    
    // Create a detailed view modal
    const modalHtml = `
        <div class="modal fade" id="viewAssessmentModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">
                            <i class="fas fa-eye me-2"></i>Assessment Details
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h6>Student Information</h6>
                                <p><strong>Name:</strong> ${assessment.studentName}</p>
                                <p><strong>Class:</strong> ${assessment.className || 'N/A'}</p>
                                <p><strong>Teacher:</strong> ${assessment.teacherName || 'N/A'}</p>
                            </div>
                            <div class="col-md-6">
                                <h6>Assessment Details</h6>
                                <p><strong>Type:</strong> ${assessment.assessmentType}</p>
                                <p><strong>Date:</strong> ${formatDate(assessment.assessmentDate)}</p>
                                <p><strong>Duration:</strong> ${assessment.assessmentDurationMinutes ? assessment.assessmentDurationMinutes + ' minutes' : 'N/A'}</p>
                            </div>
                        </div>
                        
                        ${assessment.surahName ? `
                            <div class="mt-3">
                                <h6>Content Assessed</h6>
                                <p><strong>Surah:</strong> ${assessment.surahName}</p>
                                ${assessment.startAyah && assessment.endAyah ? 
                                    `<p><strong>Verses:</strong> ${assessment.startAyah} - ${assessment.endAyah}</p>` : 
                                    ''
                                }
                            </div>
                        ` : ''}
                        
                        <div class="mt-3">
                            <h6>Scores</h6>
                            <div class="row">
                                ${assessment.recitationScore !== null ? `<div class="col-md-4"><strong>Recitation:</strong> ${assessment.recitationScore}</div>` : ''}
                                ${assessment.tajweedScore !== null ? `<div class="col-md-4"><strong>Tajweed:</strong> ${assessment.tajweedScore}</div>` : ''}
                                ${assessment.memorizationScore !== null ? `<div class="col-md-4"><strong>Memorization:</strong> ${assessment.memorizationScore}</div>` : ''}
                                ${assessment.comprehensionScore !== null ? `<div class="col-md-4"><strong>Comprehension:</strong> ${assessment.comprehensionScore}</div>` : ''}
                                ${assessment.disciplineScore !== null ? `<div class="col-md-4"><strong>Discipline:</strong> ${assessment.disciplineScore}</div>` : ''}
                            </div>
                            <div class="mt-2">
                                <strong>Overall Score:</strong> 
                                <span class="badge bg-primary">${assessment.overallScore ? assessment.overallScore.toFixed(1) : 'N/A'}</span>
                                <span class="badge grade-${getGradeClass(assessment.grade)} ms-1">${assessment.grade || 'N/A'}</span>
                            </div>
                        </div>
                        
                        ${assessment.teacherFeedback ? `
                            <div class="mt-3">
                                <h6>Teacher Feedback</h6>
                                <p>${assessment.teacherFeedback}</p>
                            </div>
                        ` : ''}
                        
                        ${assessment.studentStrengths ? `
                            <div class="mt-3">
                                <h6>Student Strengths</h6>
                                <p>${assessment.studentStrengths}</p>
                            </div>
                        ` : ''}
                        
                        ${assessment.areasForImprovement ? `
                            <div class="mt-3">
                                <h6>Areas for Improvement</h6>
                                <p>${assessment.areasForImprovement}</p>
                            </div>
                        ` : ''}
                        
                        ${assessment.homeworkAssigned ? `
                            <div class="mt-3">
                                <h6>Homework Assigned</h6>
                                <p>${assessment.homeworkAssigned}</p>
                            </div>
                        ` : ''}
                        
                        ${assessment.nextAssessmentDate ? `
                            <div class="mt-3">
                                <h6>Next Assessment</h6>
                                <p><strong>Date:</strong> ${formatDate(assessment.nextAssessmentDate)}</p>
                            </div>
                        ` : ''}
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-primary" onclick="editAssessment(${assessment.id})">
                            <i class="fas fa-edit me-1"></i>Edit Assessment
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Remove existing modal if any
    const existingModal = document.getElementById('viewAssessmentModal');
    if (existingModal) {
        existingModal.remove();
    }
    
    // Add modal to body
    document.body.insertAdjacentHTML('beforeend', modalHtml);
    
    // Show modal
    const modal = new bootstrap.Modal(document.getElementById('viewAssessmentModal'));
    modal.show();
    
    // Clean up modal when hidden
    document.getElementById('viewAssessmentModal').addEventListener('hidden.bs.modal', function() {
        this.remove();
    });
}

function editAssessment(id) {
    // Implementation for editing assessment
    showInfo('Edit functionality coming soon...');
}

async function deleteAssessment(id) {
    if (!confirm('Are you sure you want to delete this assessment?')) {
        return;
    }
    
    try {
        await MoktobApp.apiRequest(`/moktob/api/assessments/${id}`, {
            method: 'DELETE'
        });
        
        showSuccess('Assessment deleted successfully!');
        loadAssessments();
    } catch (error) {
        console.error('Error deleting assessment:', error);
        showError('Failed to delete assessment');
    }
}

function refreshAssessments() {
    loadAssessments();
}

function exportAssessments() {
    showInfo('Export functionality coming soon...');
}

function showAnalytics() {
    showInfo('Analytics functionality coming soon...');
}

// Utility functions
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString();
}

function formatTime(dateTimeString) {
    if (!dateTimeString) return '';
    return new Date(dateTimeString).toLocaleTimeString();
}

function getGradeClass(grade) {
    if (!grade) return 'secondary';
    if (grade.startsWith('A')) return 'A';
    if (grade.startsWith('B')) return 'B';
    if (grade.startsWith('C')) return 'C';
    if (grade.startsWith('D')) return 'D';
    return 'F';
}

function truncateText(text, maxLength) {
    if (!text) return '';
    return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
}

function showSuccess(message) {
    // You can implement a toast notification system here
    alert(message);
}

function showError(message) {
    // You can implement a toast notification system here
    alert('Error: ' + message);
}

function showInfo(message) {
    // You can implement a toast notification system here
    alert(message);
}
