# Auth Service

Authentication microservice for HR Management Application.

## Features

- JWT-based authentication
- User login and token validation
- BCrypt password hashing
- Role-based access control (Employee/Manager)
- CORS configuration for frontend integration
- Test data included

## Prerequisites

- Java 21
- PostgreSQL database running on localhost:5432
- Gradle (or use the included Gradle wrapper)

## Database Setup

The application connects to a PostgreSQL database at `jdbc:postgresql://host.docker.internal:5432/auth_db`.

### 1. Create the Database

```sql
CREATE DATABASE auth_db;
```

### 2. Database Migration

The application uses Flyway for automatic database migrations. When you start the application, it will:
- Create the `users` table with all necessary fields
- Create indexes on `email` and `role` columns
- Insert 3 test users with hashed passwords

No manual SQL execution is needed - Flyway handles everything automatically on startup.

**Resetting the Database:**

If you need to reset the database and re-run migrations:

```sql
-- Connect to PostgreSQL
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
```

Then restart the application to trigger automatic migration.

## Running with Docker (Recommended)

Run the Auth Service in a Docker container while connecting to your local PostgreSQL database:

### 1. Build and Start the Service

```bash
# In the auth-service directory:
docker-compose up --build -d
```
- This builds the Auth Service Docker image and starts the container
- The service connects to PostgreSQL on your host machine via `host.docker.internal`
- The service will be available at [http://localhost:8001](http://localhost:8001)

### 2. View Logs

```bash
docker-compose logs -f auth-service
```

### 3. Stop the Service

```bash
docker-compose down
```

### 4. Rebuild After Code Changes

```bash
docker-compose up --build -d
```

---

## Running Locally (Manual Setup)

If you prefer to run without Docker:

1. **Build the Project**
   ```bash
   ./gradlew build
   ```
2. **Run the Application**
   ```bash
   ./gradlew bootRun
   ```
3. **The service starts on port 8001**

---

## Test Users

The application includes pre-loaded test data:

| Email | Password | Role | Name |
|-------|----------|------|------|
| manager@company.com | password123 | manager | Jane Manager |
| employee1@company.com | password123 | employee | John Employee |
| employee2@company.com | password123 | employee | Alice Developer |

## API Endpoints

### POST /auth/login
Authenticate user and receive JWT token

**Request:**
```json
{
  "email": "manager@company.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "11111111-1111-1111-1111-111111111111",
    "name": "Jane Manager",
    "email": "manager@company.com",
    "role": "manager",
    "department": "Management",
    "position": "HR Manager"
  }
}
```

### POST /auth/logout
Invalidate current token (Note: Currently returns a success message but doesn't implement token blacklisting)

**Response:**
```json
{
  "message": "Logged out successfully"
}
```

### GET /auth/validate
Validate current token

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "valid": true,
  "user": {
    "id": "11111111-1111-1111-1111-111111111111",
    "name": "Jane Manager",
    "email": "manager@company.com",
    "role": "manager",
    "department": "Management",
    "position": "HR Manager"
  }
}
```

## Error Responses

All error responses follow this format:

```json
{
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "statusCode": 401
}
```

## Testing with cURL

**Login:**
```bash
# Linux/Mac/Git Bash
curl -X POST http://localhost:8001/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"manager@company.com","password":"password123"}'

# PowerShell
curl -X POST http://localhost:8001/auth/login `
  -H "Content-Type: application/json" `
  -d '{"email":"manager@company.com","password":"password123"}'
```

**Validate Token:**
```bash
# Linux/Mac/Git Bash
curl -X GET http://localhost:8001/auth/validate \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# PowerShell
curl -X GET http://localhost:8001/auth/validate `
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**Logout:**
```bash
# Linux/Mac/Git Bash
curl -X POST http://localhost:8001/auth/logout

# PowerShell
curl -X POST http://localhost:8001/auth/logout
```

## JWT Configuration

The JWT token is configured with:
- Expiration: 24 hours
- Algorithm: HS256
- Claims: userId, email, role

⚠️ **Important:** Change the JWT secret in `application.yaml` for production use!

## CORS Configuration

The service is configured to allow CORS from `http://localhost:5173` (frontend development server).

## Database Schema

The users table includes:
- **Identity:** id (UUID), email (unique), password_hash
- **Basic Info:** name, role (employee/manager)
- **Work Details:** department, position, hire_date, salary
- **Contact Info:** phone_number, address, emergency_contact
- **Sensitive Data:** bank_account, ssn
- **Timestamps:** created_at, updated_at

**Indexes:**
- `idx_users_email` on email column for faster lookups
- `idx_users_role` on role column for filtering

## Architecture

```
├── config/              # Security and application configuration
├── controller/          # REST API endpoints
├── dto/                 # Data Transfer Objects (request/response)
├── entity/              # JPA entities
├── exception/           # Global exception handling
├── repository/          # Data access layer
├── security/            # JWT authentication filter
├── service/             # Business logic
└── util/                # JWT utility class
```

## Next Steps

This Auth Service integrates with:
- **HR Service** (port 8002) - for profile management, feedback, and absence requests
- **AI Service** (port 8003) - for text polishing features

The JWT token generated by this service should be passed to other services for authentication.
