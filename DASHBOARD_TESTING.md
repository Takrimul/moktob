# Dashboard Module Testing Guide

## Quick Test Commands

### 1. Test Dashboard Overview
```bash
curl -X GET "http://localhost:8080/moktob/api/dashboard/overview" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 2. Test Class Attendance Summaries
```bash
curl -X GET "http://localhost:8080/moktob/api/dashboard/class-attendance?startDate=2024-01-01&endDate=2024-01-31" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 3. Test Top Performing Students
```bash
curl -X GET "http://localhost:8080/moktob/api/dashboard/top-students?limit=5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 4. Test Teacher Performance
```bash
curl -X GET "http://localhost:8080/moktob/api/dashboard/teacher-performance" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 5. Test Class-wise Students
```bash
curl -X GET "http://localhost:8080/moktob/api/dashboard/class-wise-students" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 6. Test Attendance Analytics
```bash
curl -X GET "http://localhost:8080/moktob/api/dashboard/attendance-analytics?startDate=2024-01-01&endDate=2024-01-31" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 7. Test Quick Stats
```bash
curl -X GET "http://localhost:8080/moktob/api/dashboard/stats" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

## Expected Response Structure

### Dashboard Overview Response
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
  "classAttendanceSummaries": [
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
  ],
  "attendanceTrends": [
    {
      "date": "2024-01-01",
      "presentCount": 120,
      "absentCount": 20,
      "lateCount": 10,
      "attendanceRate": 80.0,
      "totalStudents": 150
    }
  ],
  "topPerformingStudents": [
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
  ],
  "teacherPerformance": [
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
}
```

## Testing Checklist

- [ ] Dashboard overview returns complete data
- [ ] Class attendance summaries show correct counts
- [ ] Attendance trends display daily data
- [ ] Top students are ranked by performance
- [ ] Teacher performance metrics are accurate
- [ ] Class-wise students include all details
- [ ] Attendance analytics provide comprehensive stats
- [ ] Quick stats endpoint returns key metrics
- [ ] Date range filtering works correctly
- [ ] JWT authentication is enforced
- [ ] Tenant isolation is maintained
- [ ] Error handling works properly

## Sample Data Setup

To test the dashboard effectively, ensure you have:
1. At least 2-3 classes with different teachers
2. 10-20 students enrolled in classes
3. Attendance records for the last 30 days
4. Assessment records for students
5. Active teachers and students

## Performance Testing

For performance testing with large datasets:
- Test with 1000+ students
- Test with 50+ classes
- Test with 10,000+ attendance records
- Monitor response times
- Check memory usage
- Verify query optimization
