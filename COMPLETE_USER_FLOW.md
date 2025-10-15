# Complete Client Registration & User Management Flow

## 🏢 **Complete SaaS Onboarding Process**

### **Step 1: Client Registration with Temporary Password**

```bash
curl -X POST http://localhost:8080/api/clients/register \
  -H "Content-Type: application/json" \
  -d '{
    "clientName": "Islamic School Dubai",
    "contactEmail": "admin@islamicschooldubai.com",
    "contactPhone": "+971501234567",
    "address": "Dubai, UAE",
    "subscriptionPlan": "PREMIUM",
    "expiryDate": "2024-12-31",
    "adminUsername": "admin_dubai",
    "adminFullName": "Ahmed Al-Rashid",
    "adminEmail": "ahmed@islamicschooldubai.com",
    "adminPhone": "+971501234567"
  }'
```

**Response:**
```json
{
  "client": {
    "clientId": 2,
    "clientName": "Islamic School Dubai",
    "contactEmail": "admin@islamicschooldubai.com",
    "subscriptionPlan": "PREMIUM",
    "isActive": true
  },
  "adminUsername": "admin_dubai",
  "adminPassword": "Temporary password: A1B2C3D4"
}
```

**What happens automatically:**
1. ✅ Client is created
2. ✅ Default roles are created (ADMIN, TEACHER, STUDENT, PARENT)
3. ✅ Admin user is created with **temporary password**
4. ✅ Admin user is assigned ADMIN role

### **Step 2: Admin Login with Temporary Password**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin_dubai",
    "password": "A1B2C3D4"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "clientId": 2,
  "userId": 5,
  "username": "admin_dubai",
  "role": "ADMIN"
}
```

### **Step 3: Admin Changes Password**

```bash
curl -X POST http://localhost:8080/api/users/change-password \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "A1B2C3D4",
    "newPassword": "MySecurePassword123"
  }'
```

**Response:**
```json
"Password changed successfully"
```

### **Step 4: Admin Creates Users with Temporary Passwords**

#### **Create Teacher:**
```bash
curl -X POST http://localhost:8080/api/users/create-teacher \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teacher_omar",
    "password": "TEMP1234",
    "fullName": "Omar Hassan",
    "email": "omar@islamicschooldubai.com",
    "phone": "+971501234568"
  }'
```

#### **Create Student:**
```bash
curl -X POST http://localhost:8080/api/users/create-student \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "student_ahmed",
    "password": "TEMP5678",
    "fullName": "Ahmed Ali",
    "email": "ahmed@islamicschooldubai.com",
    "phone": "+971501234569"
  }'
```

#### **Create Parent:**
```bash
curl -X POST http://localhost:8080/api/users/create-parent \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "parent_mohammed",
    "password": "TEMP9012",
    "fullName": "Mohammed Ali",
    "email": "mohammed@islamicschooldubai.com",
    "phone": "+971501234570"
  }'
```

### **Step 5: Users Login and Change Passwords**

#### **Teacher Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teacher_omar",
    "password": "TEMP1234"
  }'
```

#### **Teacher Changes Password:**
```bash
curl -X POST http://localhost:8080/api/users/change-password \
  -H "Authorization: Bearer <teacher-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "TEMP1234",
    "newPassword": "TeacherPassword123"
  }'
```

### **Step 6: Role-Based Menu Access**

#### **Teacher Menu (after login):**
```bash
# Get teacher profile
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer <teacher-token>"

# Get all students (teacher can see students)
curl -X GET http://localhost:8080/api/users/students \
  -H "Authorization: Bearer <teacher-token>"

# Create student records
curl -X POST http://localhost:8080/api/students \
  -H "Authorization: Bearer <teacher-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Fatima Ahmed",
    "dateOfBirth": "2010-03-15",
    "guardianName": "Ahmed Hassan",
    "guardianContact": "+971501234571"
  }'
```

#### **Student Menu (after login):**
```bash
# Get student profile
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer <student-token>"

# View own memorization records
curl -X GET http://localhost:8080/api/learning/memorization/student/{studentId} \
  -H "Authorization: Bearer <student-token>"
```

#### **Parent Menu (after login):**
```bash
# Get parent profile
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer <parent-token>"

# View child's progress (if linked)
curl -X GET http://localhost:8080/api/learning/assessments/student/{childStudentId} \
  -H "Authorization: Bearer <parent-token>"
```

## 🔐 **Security Features**

1. **Temporary Passwords**: Auto-generated 8-character passwords
2. **Password Change**: Users must change temporary passwords
3. **Role-Based Access**: Different menus for different roles
4. **Tenant Isolation**: Complete data separation
5. **JWT Authentication**: Secure token-based auth

## 📋 **Available Endpoints**

### **Authentication:**
- `POST /api/auth/login` - Login with username/password
- `POST /api/auth/logout` - Logout

### **User Management:**
- `POST /api/users/create-teacher` - Create teacher with temp password
- `POST /api/users/create-student` - Create student with temp password
- `POST /api/users/create-parent` - Create parent with temp password
- `POST /api/users/change-password` - Change current user's password
- `POST /api/users/{id}/reset-password` - Admin resets user password
- `GET /api/users/profile` - Get current user profile
- `GET /api/users/teachers` - Get all teachers
- `GET /api/users/students` - Get all students
- `GET /api/users/parents` - Get all parents

### **Client Management:**
- `POST /api/clients/register` - Register new client with admin user

## 🎯 **Complete Flow Summary**

1. **Client Registration** → Creates client + admin user with temp password
2. **Admin Login** → Uses temp password to get JWT token
3. **Admin Changes Password** → Sets secure password
4. **Admin Creates Users** → Creates teachers/students/parents with temp passwords
5. **Users Login** → Use temp passwords to get JWT tokens
6. **Users Change Passwords** → Set secure passwords
7. **Role-Based Access** → Different menus based on user role

This provides a complete, secure SaaS onboarding experience! 🚀
