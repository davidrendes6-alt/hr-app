# HR Service API Testing Guide

This document provides example requests for testing the HR Service API endpoints.

## Setup

1. First, authenticate through the Auth Service (port 8001) to get a JWT token:

```bash
POST http://localhost:8001/auth/login
Content-Type: application/json

{
  "email": "manager@company.com",
  "password": "password123"
}
```

2. Copy the `token` from the response
3. Use it in the `Authorization` header for all HR Service requests

## Example Requests

### Profile Endpoints

#### Get My Profile
```bash
GET http://localhost:8002/profiles/me
Authorization: Bearer <your-token>
```

#### Get Profile by ID
```bash
# Manager viewing any profile (full data)
GET http://localhost:8002/profiles/22222222-2222-2222-2222-222222222222
Authorization: Bearer <manager-token>

# Employee viewing own profile (full data)
GET http://localhost:8002/profiles/22222222-2222-2222-2222-222222222222
Authorization: Bearer <employee1-token>

# Employee viewing other profile (public data only)
GET http://localhost:8002/profiles/33333333-3333-3333-3333-333333333333
Authorization: Bearer <employee1-token>
```

#### Update Profile
```bash
PUT http://localhost:8002/profiles/22222222-2222-2222-2222-222222222222
Authorization: Bearer <token>
Content-Type: application/json

{
  "phoneNumber": "+1-555-9999",
  "address": "New Address, City, State",
  "emergencyContact": "New Contact: +1-555-8888"
}
```

#### List All Profiles
```bash
GET http://localhost:8002/profiles
Authorization: Bearer <token>
```

### Feedback Endpoints

#### Get Feedback for Profile
```bash
GET http://localhost:8002/profiles/22222222-2222-2222-2222-222222222222/feedback
Authorization: Bearer <token>
```

#### Create Feedback (without AI)
```bash
POST http://localhost:8002/profiles/22222222-2222-2222-2222-222222222222/feedback
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "Great work on the recent project! Very professional and detail-oriented.",
  "polishWithAI": false
}
```

#### Create Feedback (with AI polishing)
```bash
POST http://localhost:8002/profiles/22222222-2222-2222-2222-222222222222/feedback
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "good job nice work",
  "polishWithAI": true
}
```

#### Try to Leave Feedback on Own Profile (Should Fail)
```bash
POST http://localhost:8002/profiles/22222222-2222-2222-2222-222222222222/feedback
Authorization: Bearer <employee1-token>
Content-Type: application/json

{
  "content": "I'm awesome!",
  "polishWithAI": false
}

# Expected: 403 Forbidden - "Cannot leave feedback on own profile"
```

### Absence Request Endpoints

#### Get My Absence Requests
```bash
GET http://localhost:8002/absences/me
Authorization: Bearer <token>
```

#### Get Pending Requests (Manager Only)
```bash
GET http://localhost:8002/absences/pending
Authorization: Bearer <manager-token>
```

#### Create Absence Request
```bash
POST http://localhost:8002/absences
Authorization: Bearer <employee-token>
Content-Type: application/json

{
  "startDate": "2025-12-23",
  "endDate": "2025-12-27",
  "reason": "Holiday vacation with family"
}
```

#### Create Invalid Absence Request (Should Fail)
```bash
POST http://localhost:8002/absences
Authorization: Bearer <token>
Content-Type: application/json

{
  "startDate": "2025-12-27",
  "endDate": "2025-12-23",
  "reason": "Test"
}

# Expected: 400 Bad Request - "End date cannot be before start date"
```

#### Approve Absence Request (Manager Only)
```bash
PATCH http://localhost:8002/absences/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/approve
Authorization: Bearer <manager-token>
```

#### Reject Absence Request (Manager Only)
```bash
PATCH http://localhost:8002/absences/cccccccc-cccc-cccc-cccc-cccccccccccc/reject
Authorization: Bearer <manager-token>
```

#### Try to Approve as Employee (Should Fail)
```bash
PATCH http://localhost:8002/absences/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/approve
Authorization: Bearer <employee-token>

# Expected: 403 Forbidden - "Not authorized - manager role required"
```

## Test User IDs

Use these IDs for testing:

- **Manager (Jane Manager)**: 11111111-1111-1111-1111-111111111111
- **Employee1 (John Employee)**: 22222222-2222-2222-2222-222222222222
- **Employee2 (Alice Developer)**: 33333333-3333-3333-3333-333333333333
- **Employee3 (Bob Smith)**: 44444444-4444-4444-4444-444444444444

## Pre-loaded Absence Requests

- **ID**: aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa (John's request - PENDING)
- **ID**: bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb (Alice's request - APPROVED)
- **ID**: cccccccc-cccc-cccc-cccc-cccccccccccc (Bob's request - PENDING)

## Pre-loaded Feedback

- **ID**: dddddddd-dddd-dddd-dddd-dddddddddddd (Manager → John)
- **ID**: eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee (Manager → Alice)
- **ID**: ffffffff-ffff-ffff-ffff-ffffffffffff (Alice → John)

## Testing Permission Scenarios

### Scenario 1: Employee viewing sensitive data
- Login as employee1@company.com
- Try to view employee2's profile
- Should receive PUBLIC profile only (no salary, SSN, etc.)

### Scenario 2: Manager viewing sensitive data
- Login as manager@company.com
- View any employee profile
- Should receive FULL profile with all sensitive data

### Scenario 3: Self-feedback prevention
- Login as employee1@company.com
- Try to leave feedback on own profile (ID: 22222222-2222-2222-2222-222222222222)
- Should fail with 403 Forbidden

### Scenario 4: Manager-only operations
- Login as employee1@company.com
- Try to approve an absence request
- Should fail with 403 Forbidden
- Login as manager@company.com and retry
- Should succeed

### Scenario 5: AI feedback polishing
- Login as any user
- Create feedback with polishWithAI: true
- If AI service is running, content should be enhanced
- If AI service is down, should receive 500 error

## Expected Error Responses

### 400 Bad Request
```json
{
  "error": "Bad Request",
  "message": "End date cannot be before start date",
  "statusCode": 400
}
```

### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "statusCode": 401
}
```

### 403 Forbidden
```json
{
  "error": "Forbidden",
  "message": "Not authorized to update this profile",
  "statusCode": 403
}
```

### 404 Not Found
```json
{
  "error": "Not Found",
  "message": "Profile not found",
  "statusCode": 404
}
```

## Testing Tools

Recommended tools for API testing:
- Postman
- Insomnia
- Thunder Client (VS Code extension)
- curl (command line)

## Notes

- All dates should be in ISO 8601 format (YYYY-MM-DD)
- All timestamps are in ISO 8601 format with timezone
- UUIDs must be valid and match existing records
- JWT tokens expire based on the configured expiration time (default: 24 hours)

