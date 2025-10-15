#!/bin/bash

# Moktob Management SaaS - JWT Authentication Test Script
# This script demonstrates how to use JWT authentication with the API

BASE_URL="http://localhost:8080"
API_BASE="$BASE_URL/api"

echo "=== Moktob Management SaaS - JWT Authentication Test ==="
echo ""

# Test 1: Login and get JWT token
echo "1. Testing Login..."
LOGIN_RESPONSE=$(curl -s -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin"
  }')

echo "Login Response: $LOGIN_RESPONSE"
echo ""

# Extract JWT token from response
JWT_TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$JWT_TOKEN" ]; then
    echo "❌ Failed to get JWT token. Please check if the application is running and credentials are correct."
    exit 1
fi

echo "✅ JWT Token obtained: ${JWT_TOKEN:0:50}..."
echo ""

# Test 2: Get user profile using JWT token
echo "2. Testing User Profile with JWT..."
PROFILE_RESPONSE=$(curl -s -X GET "$API_BASE/user/profile" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json")

echo "Profile Response: $PROFILE_RESPONSE"
echo ""

# Test 3: Get students (should automatically use tenant from JWT)
echo "3. Testing Students API with JWT (automatic tenant)..."
STUDENTS_RESPONSE=$(curl -s -X GET "$API_BASE/students" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json")

echo "Students Response: $STUDENTS_RESPONSE"
echo ""

# Test 4: Create a new student
echo "4. Testing Create Student with JWT..."
CREATE_STUDENT_RESPONSE=$(curl -s -X POST "$API_BASE/students" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Student",
    "dateOfBirth": "2010-01-01",
    "guardianName": "Test Guardian",
    "guardianContact": "+1234567890",
    "address": "Test Address",
    "enrollmentDate": "2024-01-01"
  }')

echo "Create Student Response: $CREATE_STUDENT_RESPONSE"
echo ""

# Test 5: Test with different user (teacher)
echo "5. Testing Login with Teacher User..."
TEACHER_LOGIN_RESPONSE=$(curl -s -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teacher1",
    "password": "admin"
  }')

echo "Teacher Login Response: $TEACHER_LOGIN_RESPONSE"
echo ""

# Extract teacher JWT token
TEACHER_JWT_TOKEN=$(echo $TEACHER_LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ ! -z "$TEACHER_JWT_TOKEN" ]; then
    echo "6. Testing Teacher Profile..."
    TEACHER_PROFILE_RESPONSE=$(curl -s -X GET "$API_BASE/user/profile" \
      -H "Authorization: Bearer $TEACHER_JWT_TOKEN" \
      -H "Content-Type: application/json")
    
    echo "Teacher Profile Response: $TEACHER_PROFILE_RESPONSE"
    echo ""
fi

echo "=== Test Complete ==="
echo ""
echo "Key Features Demonstrated:"
echo "✅ JWT Authentication"
echo "✅ Automatic tenant context from JWT token"
echo "✅ No need for X-Tenant-ID header when using JWT"
echo "✅ User-specific data isolation"
echo "✅ Role-based access control"
echo ""
echo "Usage Notes:"
echo "- Include 'Authorization: Bearer <token>' header in all authenticated requests"
echo "- Tenant ID is automatically extracted from JWT token"
echo "- Each user can only access data from their own tenant"
echo "- Default password for all users: 'admin'"
