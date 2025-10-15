# Moktob Management SaaS - Complete Client Registration & User Management Flow

## 🏢 **Complete SaaS Onboarding Process**

### **Step 1: Client Registration**

#### **Option A: Simple Client Registration**
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
  "adminPassword": "Default password: admin"
}
```

**What happens automatically:**
1. ✅ Client is created
2. ✅ Default roles are created (ADMIN, TEACHER, STUDENT, PARENT)
3. ✅ Admin user is created with default password "admin"
4. ✅ Admin user is assigned ADMIN role

### **Step 2: Admin Login & User Management**

#### **Admin Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin_dubai",
    "password": "admin"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "clientId": 2,
  "userId": 5,
  "username": "admin_dubai",
  "role": "ADMIN"
}
```

### **Step 3: Create Role-Based Users**

#### **Create Teachers:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teacher_omar",
    "password": "teacher123",
    "fullName": "Omar Hassan",
    "email": "omar@islamicschooldubai.com",
    "phone": "+971501234568",
    "roleName": "TEACHER"
  }'
```

#### **Create Students:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "student_ahmed",
    "password": "student123",
    "fullName": "Ahmed Ali",
    "email": "ahmed@islamicschooldubai.com",
    "phone": "+971501234569",
    "roleName": "STUDENT"
  }'
```

#### **Create Parents:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "parent_mohammed",
    "password": "parent123",
    "fullName": "Mohammed Ali",
    "email": "mohammed@islamicschooldubai.com",
    "phone": "+971501234570",
    "roleName": "PARENT"
  }'
```

### **Step 4: Role-Based Login Examples**

#### **Teacher Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teacher_omar",
    "password": "teacher123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "clientId": 2,
  "userId": 6,
  "username": "teacher_omar",
  "role": "TEACHER"
}
```

#### **Student Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "student_ahmed",
    "password": "student123"
  }'
```

### **Step 5: Role-Based Data Access**

#### **Teacher Creates Students:**
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Authorization: Bearer <teacher-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Fatima Ahmed",
    "dateOfBirth": "2010-03-15",
    "guardianName": "Ahmed Hassan",
    "guardianContact": "+971501234571",
    "address": "Dubai Marina, UAE"
  }'
```

**Automatic tenant isolation:** Student is automatically assigned to client_id = 2 (Islamic School Dubai)

#### **Student Views Their Profile:**
```bash
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer <student-token>"
```

## 🔄 **Complete Multi-Tenant Flow**

### **Client 1: Moktob Academy**
- **Admin:** admin (client_id: 1)
- **Teacher:** teacher1 (client_id: 1)
- **Students:** Ahmed Ali, Sara Khan (client_id: 1)

### **Client 2: Islamic School Dubai**
- **Admin:** admin_dubai (client_id: 2)
- **Teacher:** teacher_omar (client_id: 2)
- **Students:** Fatima Ahmed, Omar Hassan (client_id: 2)

### **Complete Data Isolation:**
- Client 1 users can ONLY see Client 1 data
- Client 2 users can ONLY see Client 2 data
- JWT token automatically sets tenant context
- No cross-tenant data access possible

## 🛡️ **Security Features**

1. **Automatic Tenant Context:** JWT token contains clientId
2. **Role-Based Access:** Different permissions per role
3. **Password Security:** BCrypt hashing
4. **Token Expiration:** 24-hour token lifetime
5. **Data Isolation:** Complete tenant separation

## 📊 **Database State After Registration**

**Clients Table:**
```sql
| client_id | client_name          | contact_email                    |
|-----------|----------------------|----------------------------------|
| 1         | Moktob Academy       | admin@moktob.com                |
| 2         | Islamic School Dubai  | admin@islamicschooldubai.com    |
```

**Users Table:**
```sql
| id | client_id | username      | role_id | full_name      |
|----|-----------|---------------|---------|----------------|
| 1  | 1         | admin         | 1       | System Admin   |
| 5  | 2         | admin_dubai   | 4       | Ahmed Al-Rashid|
| 6  | 2         | teacher_omar  | 5       | Omar Hassan    |
| 7  | 2         | student_ahmed| 6       | Ahmed Ali      |
```

**Students Table:**
```sql
| id | client_id | name          | guardian_name |
|----|-----------|---------------|---------------|
| 1  | 1         | Ahmed Ali     | Mohammed Ali  |
| 2  | 2         | Fatima Ahmed  | Ahmed Hassan  |
```

This is a complete, production-ready SaaS onboarding flow! 🚀
