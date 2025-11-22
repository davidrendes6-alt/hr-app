# Backend API Specification

This document outlines the expected API contracts for the three backend services.

## Service Ports

- **Auth Service**: `http://localhost:8001`
- **HR Service**: `http://localhost:8002`
- **AI Service**: `http://localhost:8003`

---

## Auth Service API

### POST `/auth/login`
**Description**: Authenticate user and receive JWT token

**Request Body**:
```json
{
  "email": "string",
  "password": "string"
}
```

**Response** (200 OK):
```json
{
  "token": "string (JWT)",
  "user": {
    "id": "string",
    "name": "string",
    "email": "string",
    "role": "employee" | "manager",
    "department": "string?",
    "position": "string?"
  }
}
```

**Error Responses**:
- 401: Invalid credentials
- 400: Validation error

---

### POST `/auth/logout`
**Description**: Invalidate current token (optional)

**Headers**: `Authorization: Bearer <token>`

**Response** (200 OK):
```json
{
  "message": "Logged out successfully"
}
```

---

### GET `/auth/validate`
**Description**: Validate current token

**Headers**: `Authorization: Bearer <token>`

**Response** (200 OK):
```json
{
  "valid": true,
  "user": { /* user object */ }
}
```

**Error Responses**:
- 401: Invalid or expired token

---

## HR Service API

All endpoints require authentication: `Authorization: Bearer <token>`

### Profile Endpoints

#### GET `/profiles/me`
**Description**: Get current user's full profile (including sensitive data)

**Response** (200 OK):
```json
{
  "id": "string",
  "name": "string",
  "email": "string",
  "role": "string",
  "department": "string?",
  "position": "string?",
  "hireDate": "ISO8601 date string?",
  "salary": "number?",
  "phoneNumber": "string?",
  "address": "string?",
  "emergencyContact": "string?",
  "bankAccount": "string?",
  "ssn": "string?"
}
```

---

#### GET `/profiles/:id`
**Description**: Get profile by ID

**Permission Logic**:
- If requester is manager OR profile owner: return full profile with sensitive data
- If requester is coworker: return public profile (no sensitive data)

**Response** (200 OK) - Full Profile:
```json
{
  "id": "string",
  "name": "string",
  "email": "string",
  "role": "string",
  "department": "string?",
  "position": "string?",
  "hireDate": "ISO8601 date string?",
  "salary": "number?",
  "phoneNumber": "string?",
  "address": "string?",
  "emergencyContact": "string?",
  "ssn": "string?"
}
```

**Response** (200 OK) - Public Profile:
```json
{
  "id": "string",
  "name": "string",
  "email": "string",
  "department": "string?",
  "position": "string?"
}
```

**Error Responses**:
- 404: Profile not found
- 403: Access denied

---

#### PUT `/profiles/:id`
**Description**: Update profile

**Permissions**: Owner or Manager only

**Request Body**:
```json
{
  "name": "string?",
  "email": "string?",
  "department": "string?",
  "position": "string?",
  "phoneNumber": "string?",
  "address": "string?",
  "emergencyContact": "string?",
  "salary": "number?"
}
```

**Response** (200 OK):
```json
{
  /* Updated full profile object */
}
```

**Error Responses**:
- 403: Not authorized to update this profile
- 400: Validation error

---

#### GET `/profiles`
**Description**: Get list of all employees (public profiles only)

**Response** (200 OK):
```json
[
  {
    "id": "string",
    "name": "string",
    "email": "string",
    "department": "string?",
    "position": "string?"
  }
]
```

---

### Feedback Endpoints

#### GET `/profiles/:profileId/feedback`
**Description**: Get all feedback for a profile

**Response** (200 OK):
```json
[
  {
    "id": "string",
    "profileId": "string",
    "authorId": "string",
    "authorName": "string",
    "content": "string",
    "createdAt": "ISO8601 date string",
    "isPolished": "boolean?"
  }
]
```

---

#### POST `/profiles/:profileId/feedback`
**Description**: Leave feedback on a profile

**Request Body**:
```json
{
  "content": "string",
  "polishWithAI": "boolean?"
}
```

**Workflow**:
1. If `polishWithAI` is true, call AI service to polish the text
2. Save feedback with polished content if AI was used
3. Set `isPolished: true` if AI enhancement was used

**Response** (201 Created):
```json
{
  "id": "string",
  "profileId": "string",
  "authorId": "string",
  "authorName": "string",
  "content": "string (polished if AI was used)",
  "createdAt": "ISO8601 date string",
  "isPolished": "boolean"
}
```

**Error Responses**:
- 400: Validation error
- 403: Cannot leave feedback on own profile
- 500: AI service error (if polishing fails)

---

### Absence Request Endpoints

#### GET `/absences/me`
**Description**: Get current user's absence requests

**Response** (200 OK):
```json
[
  {
    "id": "string",
    "employeeId": "string",
    "employeeName": "string",
    "startDate": "ISO8601 date string",
    "endDate": "ISO8601 date string",
    "reason": "string",
    "status": "pending" | "approved" | "rejected",
    "createdAt": "ISO8601 date string"
  }
]
```

---

#### GET `/absences/pending`
**Description**: Get all pending absence requests (managers only)

**Permissions**: Manager only

**Response** (200 OK):
```json
[
  {
    "id": "string",
    "employeeId": "string",
    "employeeName": "string",
    "startDate": "ISO8601 date string",
    "endDate": "ISO8601 date string",
    "reason": "string",
    "status": "pending",
    "createdAt": "ISO8601 date string"
  }
]
```

**Error Responses**:
- 403: Not authorized (not a manager)

---

#### POST `/absences`
**Description**: Create new absence request

**Request Body**:
```json
{
  "startDate": "ISO8601 date string",
  "endDate": "ISO8601 date string",
  "reason": "string"
}
```

**Response** (201 Created):
```json
{
  "id": "string",
  "employeeId": "string",
  "employeeName": "string",
  "startDate": "ISO8601 date string",
  "endDate": "ISO8601 date string",
  "reason": "string",
  "status": "pending",
  "createdAt": "ISO8601 date string"
}
```

**Error Responses**:
- 400: Validation error (e.g., end date before start date)

---

#### PATCH `/absences/:id/approve`
**Description**: Approve an absence request (managers only)

**Permissions**: Manager only

**Response** (200 OK):
```json
{
  "message": "Absence request approved",
  "id": "string"
}
```

**Error Responses**:
- 403: Not authorized
- 404: Absence request not found

---

#### PATCH `/absences/:id/reject`
**Description**: Reject an absence request (managers only)

**Permissions**: Manager only

**Response** (200 OK):
```json
{
  "message": "Absence request rejected",
  "id": "string"
}
```

**Error Responses**:
- 403: Not authorized
- 404: Absence request not found

---

## AI Service API

### POST `/polish`
**Description**: Polish/enhance text using AI (HuggingFace model)

**Request Body**:
```json
{
  "text": "string",
  "context": "string?" 
}
```

**Response** (200 OK):
```json
{
  "originalText": "string",
  "polishedText": "string",
  "model": "string (e.g., 'facebook/bart-large-cnn')"
}
```

**Error Responses**:
- 400: Invalid input
- 500: AI model error
- 503: AI service unavailable

---

## Authentication & Authorization

### JWT Token
All authenticated requests should include the JWT token in the Authorization header:
```
Authorization: Bearer <token>
```

### Token Claims (Minimum)
```json
{
  "userId": "string",
  "email": "string",
  "role": "employee" | "manager",
  "exp": "number (expiration timestamp)"
}
```

### Role-Based Access

**Employee**:
- View own full profile
- Edit own profile
- View all public profiles
- Leave feedback on other profiles
- Create absence requests
- View own absence requests

**Manager**:
- All employee permissions
- View all full profiles (including sensitive data)
- Edit any profile
- View all pending absence requests
- Approve/reject absence requests

---

## Error Response Format

All error responses should follow this format:

```json
{
  "error": "string (error type)",
  "message": "string (human-readable message)",
  "statusCode": "number"
}
```

Examples:
```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "statusCode": 401
}
```

```json
{
  "error": "Forbidden",
  "message": "You don't have permission to access this resource",
  "statusCode": 403
}
```

```json
{
  "error": "Validation Error",
  "message": "Email is required",
  "statusCode": 400
}
```

---

## CORS Configuration

All backend services should allow CORS from the frontend origin:
- Development: `http://localhost:5173`
- Production: Configure based on deployment

**Required Headers**:
```
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Allow-Credentials: true
```

---

## Database Schema Suggestions

### Users Table
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL, -- 'employee' or 'manager'
  department VARCHAR(255),
  position VARCHAR(255),
  hire_date DATE,
  salary DECIMAL(10, 2),
  phone_number VARCHAR(50),
  address TEXT,
  emergency_contact TEXT,
  bank_account VARCHAR(100),
  ssn VARCHAR(11),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Absence Requests Table
```sql
CREATE TABLE absence_requests (
  id UUID PRIMARY KEY,
  employee_id UUID NOT NULL REFERENCES users(id),
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  reason TEXT NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'pending', -- 'pending', 'approved', 'rejected'
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Feedback Table
```sql
CREATE TABLE feedback (
  id UUID PRIMARY KEY,
  profile_id UUID NOT NULL REFERENCES users(id),
  author_id UUID NOT NULL REFERENCES users(id),
  content TEXT NOT NULL,
  is_polished BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## Testing Endpoints

Suggested test data:

### Test Users
```json
[
  {
    "email": "manager@company.com",
    "password": "password123",
    "role": "manager",
    "name": "Jane Manager"
  },
  {
    "email": "employee1@company.com",
    "password": "password123",
    "role": "employee",
    "name": "John Employee"
  },
  {
    "email": "employee2@company.com",
    "password": "password123",
    "role": "employee",
    "name": "Alice Developer"
  }
]
```

You can use tools like Postman, Insomnia, or Thunder Client to test these endpoints during development.
