# Dashboard Module Documentation

## Overview
The Dashboard Module provides comprehensive analytics and insights for educational institutions using the Moktob Management System. It offers real-time data visualization and reporting capabilities for administrators to monitor attendance, student performance, teacher effectiveness, and overall institutional metrics.

## Features

### 📊 **Dashboard Overview**
- Total students, teachers, and classes count
- Overall attendance rate and trends
- Active vs inactive entities
- Class-wise attendance summaries
- Top performing students
- Teacher performance metrics

### 📈 **Attendance Analytics**
- Daily attendance trends
- Present/Absent/Late counts
- Attendance rate calculations
- Class-wise attendance breakdown
- Historical attendance data

### 👥 **Student Performance**
- Individual student attendance rates
- Assessment scores (Recitation, Tajweed, Discipline)
- Overall performance rankings
- Class-wise student lists
- Guardian contact information

### 👨‍🏫 **Teacher Performance**
- Teacher effectiveness metrics
- Class management statistics
- Assessment conduction rates
- Student performance under each teacher

### 🏫 **Class Management**
- Class-wise student distribution
- Time slot and schedule information
- Teacher assignments
- Class attendance rates

## API Endpoints

### 1. Dashboard Overview
```http
GET /api/dashboard/overview?startDate=2024-01-01&endDate=2024-01-31
```
**Response:**
```json
{
  "totalStudents": 150,
  "totalTeachers": 12,
  "totalClasses": 8,
  "totalAttendanceRecords": 1200,
  "overallAttendanceRate": 85.5,
  "activeStudents": 145,
  "activeTeachers": 11,
  "activeClasses": 8,
  "classAttendanceSummaries": [...],
  "attendanceTrends": [...],
  "topPerformingStudents": [...],
  "teacherPerformance": [...]
}
```

### 2. Class Attendance Summaries
```http
GET /api/dashboard/class-attendance?startDate=2024-01-01&endDate=2024-01-31
```
**Response:**
```json
[
  {
    "classId": 1,
    "className": "Grade 1 - Quran",
    "teacherName": "Ahmed Al-Rashid",
    "totalStudents": 20,
    "presentCount": 340,
    "absentCount": 60,
    "lateCount": 20,
    "attendanceRate": 85.0,
    "totalAttendanceRecords": 400,
    "timeSlot": "09:00 - 10:30",
    "daysOfWeek": "Mon,Wed,Fri"
  }
]
```

### 3. Attendance Trends
```http
GET /api/dashboard/attendance-trends?startDate=2024-01-01&endDate=2024-01-31
```
**Response:**
```json
[
  {
    "date": "2024-01-01",
    "presentCount": 120,
    "absentCount": 20,
    "lateCount": 10,
    "attendanceRate": 80.0,
    "totalStudents": 150
  }
]
```

### 4. Top Performing Students
```http
GET /api/dashboard/top-students?startDate=2024-01-01&endDate=2024-01-31&limit=10
```
**Response:**
```json
[
  {
    "studentId": 1,
    "studentName": "Fatima Al-Zahra",
    "className": "Grade 1 - Quran",
    "attendanceRate": 95.0,
    "averageRecitationScore": 85.5,
    "averageTajweedScore": 88.0,
    "averageDisciplineScore": 92.0,
    "overallScore": 88.5,
    "totalAssessments": 5,
    "totalAttendanceRecords": 20
  }
]
```

### 5. Teacher Performance
```http
GET /api/dashboard/teacher-performance?startDate=2024-01-01&endDate=2024-01-31
```
**Response:**
```json
[
  {
    "teacherId": 1,
    "teacherName": "Ahmed Al-Rashid",
    "totalClasses": 2,
    "totalStudents": 40,
    "averageClassAttendanceRate": 87.5,
    "totalAssessmentsConducted": 25,
    "averageStudentScores": 85.2,
    "qualification": "Islamic Studies",
    "isActive": true
  }
]
```

### 6. Class-wise Students
```http
GET /api/dashboard/class-wise-students?startDate=2024-01-01&endDate=2024-01-31
```
**Response:**
```json
[
  {
    "classId": 1,
    "className": "Grade 1 - Quran",
    "teacherName": "Ahmed Al-Rashid",
    "totalStudents": 20,
    "students": [
      {
        "studentId": 1,
        "studentName": "Fatima Al-Zahra",
        "guardianName": "Mohammed Al-Zahra",
        "guardianContact": "+971501234567",
        "enrollmentDate": "2024-01-01",
        "attendanceRate": 95.0,
        "averageScore": 88.5,
        "isActive": true
      }
    ],
    "classAttendanceRate": 85.0,
    "timeSlot": "09:00 - 10:30",
    "daysOfWeek": "Mon,Wed,Fri"
  }
]
```

### 7. Attendance Analytics
```http
GET /api/dashboard/attendance-analytics?startDate=2024-01-01&endDate=2024-01-31
```
**Response:**
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "totalAttendanceRecords": 1200,
  "presentCount": 1020,
  "absentCount": 150,
  "lateCount": 30,
  "overallAttendanceRate": 85.0,
  "averageDailyAttendance": 82.5,
  "totalUniqueStudents": 150,
  "totalUniqueClasses": 8,
  "dailyBreakdown": [...]
}
```

### 8. Dashboard Stats (Quick Stats)
```http
GET /api/dashboard/stats?startDate=2024-01-01&endDate=2024-01-31
```
**Response:**
```json
{
  "totalStudents": 150,
  "totalTeachers": 12,
  "totalClasses": 8,
  "totalAttendanceRecords": 1200,
  "overallAttendanceRate": 85.5,
  "activeStudents": 145,
  "activeTeachers": 11,
  "activeClasses": 8
}
```

## Usage Examples

### Frontend Integration
```javascript
// Get dashboard overview
const response = await fetch('/api/dashboard/overview?startDate=2024-01-01&endDate=2024-01-31', {
  headers: {
    'Authorization': 'Bearer ' + token
  }
});
const dashboardData = await response.json();

// Display key metrics
document.getElementById('totalStudents').textContent = dashboardData.totalStudents;
document.getElementById('attendanceRate').textContent = dashboardData.overallAttendanceRate + '%';
```

### Date Range Filtering
```javascript
// Get last 30 days data
const endDate = new Date();
const startDate = new Date();
startDate.setDate(startDate.getDate() - 30);

const params = new URLSearchParams({
  startDate: startDate.toISOString().split('T')[0],
  endDate: endDate.toISOString().split('T')[0]
});

const response = await fetch(`/api/dashboard/overview?${params}`);
```

## Data Models

### DashboardOverviewDTO
- Complete dashboard overview with all key metrics
- Includes nested lists for detailed breakdowns

### ClassAttendanceSummaryDTO
- Per-class attendance statistics
- Teacher and timing information

### AttendanceTrendDTO
- Daily attendance trends
- Historical data points

### StudentPerformanceDTO
- Individual student performance metrics
- Assessment scores and attendance rates

### TeacherPerformanceDTO
- Teacher effectiveness metrics
- Class management statistics

### ClassWiseStudentDTO
- Class-wise student distribution
- Detailed student information

### AttendanceAnalyticsDTO
- Comprehensive attendance analytics
- Daily breakdown and trends

## Security & Access Control
- All endpoints require JWT authentication
- Tenant-based data isolation (clientId filtering)
- Role-based access control (Admin/Teacher/Student)

## Performance Considerations
- Optimized SQL queries with proper indexing
- Caching for frequently accessed data
- Pagination support for large datasets
- Efficient data aggregation

## Error Handling
- Comprehensive error logging
- Graceful fallbacks for missing data
- HTTP status codes for different error types
- Detailed error messages for debugging

## Future Enhancements
- Real-time notifications for low attendance
- Export functionality (PDF/Excel)
- Custom date range presets
- Advanced filtering options
- Comparative analytics (year-over-year)
- Mobile-optimized endpoints
