# HR Service API

This is the HR Service backend for the HR Management Application. It provides endpoints for profile management, feedback, and absence requests.

## Features

- **Profile Management**: View and update employee profiles with role-based access control
- **Feedback System**: Leave feedback on employee profiles with optional AI polishing
- **Absence Requests**: Create and manage time-off requests with approval workflow
- **JWT Authentication**: Secure endpoints with JWT token validation
- **Role-Based Authorization**: Different permissions for employees and managers
- **AI Integration**: Automatic feedback polishing using external AI service

## Technology Stack

- **Java 21**
- **Spring Boot 3.5.9**
- **Spring Security** with JWT
- **Spring Data JPA** with PostgreSQL
- **Flyway** for database migrations
- **Spring Cloud OpenFeign** for AI service integration
- **Lombok** for reducing boilerplate code

## Prerequisites

- Java 21 or higher
- Docker Desktop (for containerized setup - **Recommended**)
- OR PostgreSQL 12+ (for local setup)
- AI Service running on port 8003 (optional, for feedback polishing)

## Running the Application

### Option 1: Docker Compose (Recommended)

The easiest way to run the entire stack with zero manual database setup.

#### Clean Start

If you encounter any Docker networking or container errors, start fresh:

```powershell
# Stop and remove all containers and networks
docker-compose down -v
docker network prune -f

# Start fresh
docker-compose up --build -d
```

#### Normal Start

```powershell
# Start all services (database + application)
docker-compose up --build -d

# View logs
docker-compose logs -f

# Check status
docker-compose ps
```

#### Database Only

If you want to run the app locally but use Docker for PostgreSQL:

```powershell
# Start only the database
docker-compose up hr-db -d

# Wait 10-15 seconds for database to be ready
Start-Sleep -Seconds 15

# Run the application with Gradle
.\gradlew.bat bootRun
```

#### Access Points

- **HR Service API**: http://localhost:8002
- **PostgreSQL**: localhost:5432
  - Database: `hr_service_db`
  - Username: `postgres`
  - Password: `postgres`

#### Stopping Services

```powershell
# Stop services (preserves data)
docker-compose down

# Stop and remove all data
docker-compose down -v
```

### Option 2: Local Setup (Without Docker)

See `RUN_LOCALLY.md` for detailed instructions on setting up PostgreSQL manually and running locally.

## Troubleshooting Docker Issues

### Network Errors

If you see errors like "network not found" or "failed to set up container networking":

```powershell
# Remove all unused networks
docker network prune -f

# If hr-network already exists and causing issues
docker network rm hr-network

# Restart Docker Desktop, then try again
docker-compose up -d
```

### Flyway Checksum Mismatch

This occurs when migration files are modified after being applied to the database.

**Error Example**:
```
Migration checksum mismatch for migration version 2
-> Applied to database : 546961697
-> Resolved locally    : -1426371483
```

**Solution 1: Fresh Start (Recommended)**
```powershell
# This will delete all database data
docker-compose down -v
docker-compose up --build -d
```

**Solution 2: Run Flyway Repair** (if you need to preserve data)
```powershell
# Connect to the database container
docker exec -it hr-service-db psql -U postgres -d hr_service_db

# Delete problematic migration entry
DELETE FROM flyway_schema_history WHERE version = '2';

# Exit
\q

# Restart the application
docker-compose restart hr-service
```

### Container Won't Start

```powershell
# Check container logs
docker-compose logs hr-service
docker-compose logs hr-db

# Check if ports are already in use
netstat -ano | findstr :8002
netstat -ano | findstr :5432

# Remove everything and start fresh
docker-compose down -v
docker system prune -f
docker-compose up --build -d
```

### Database Connection Failed

```powershell
# Check if database is healthy
docker-compose ps

# View database logs
docker-compose logs hr-db

# Verify database is accepting connections
docker exec hr-service-db pg_isready -U postgres
```

## Test Data

The application comes with pre-loaded test users (password for all: `password123`):

### Manager Account
- **Email**: manager@company.com
- **Role**: MANAGER
- **Name**: Jane Manager
- **Department**: Management
- **Position**: HR Manager

### Employee Accounts
- **Email**: employee1@company.com
  - **Name**: John Employee
  - **Department**: Engineering
  - **Position**: Software Developer

- **Email**: employee2@company.com
  - **Name**: Alice Developer
  - **Department**: Engineering
  - **Position**: Senior Developer

- **Email**: employee3@company.com
  - **Name**: Bob Smith
  - **Department**: Sales
  - **Position**: Sales Representative

**Note**: You need to authenticate through the Auth Service (port 8001) to get a JWT token before using these endpoints.

## API Endpoints

All endpoints require JWT authentication via `Authorization: Bearer <token>` header (except actuator endpoints).

### Profile Endpoints

#### `GET /profiles/me`
Get current authenticated user's full profile (including sensitive data like salary, SSN)

**Headers**: `Authorization: Bearer <token>`

**Response**: `ProfileResponse` with all fields

---

#### `GET /profiles/{id}`
Get profile by ID with role-based access:
- **Profile owner**: Returns full profile (`ProfileResponse`)
- **Managers**: Returns full profile for any user
- **Other employees**: Returns public profile only (`PublicProfileResponse`)

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**: 
- `id` (UUID): Profile ID

**Response**: `ProfileResponse` or `PublicProfileResponse`

---

#### `PUT /profiles/{id}`
Update a profile (restricted to profile owner or managers)

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**: 
- `id` (UUID): Profile ID

**Request Body**:
```json
{
  "name": "string",
  "email": "string",
  "department": "string",
  "position": "string",
  "phoneNumber": "string",
  "address": "string",
  "emergencyContact": "string",
  "salary": number
}
```

**Response**: `ProfileResponse`

---

#### `GET /profiles`
Get list of all employee public profiles (basic info only, no sensitive data)

**Headers**: `Authorization: Bearer <token>`

**Response**: Array of `PublicProfileResponse`

---

### Feedback Endpoints

#### `GET /profiles/{profileId}/feedback`
Get all feedback for a specific profile

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**: 
- `profileId` (UUID): Profile ID

**Response**: Array of `FeedbackResponse`

---

#### `POST /profiles/{profileId}/feedback`
Leave feedback on an employee profile

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**: 
- `profileId` (UUID): Profile ID

**Request Body**:
```json
{
  "content": "string",
  "polishWithAI": boolean
}
```

**Response**: `FeedbackResponse` (HTTP 201 Created)

**Constraints**: 
- Cannot leave feedback on your own profile
- If `polishWithAI: true`, content will be enhanced by AI service (if available)

---

### Absence Request Endpoints

#### `GET /absences/me`
Get all absence requests for the current authenticated user

**Headers**: `Authorization: Bearer <token>`

**Response**: Array of `AbsenceRequestResponse`

---

#### `GET /absences/pending`
Get all pending absence requests (managers only)

**Headers**: `Authorization: Bearer <token>`

**Response**: Array of `AbsenceRequestResponse`

---

#### `POST /absences`
Create a new absence request

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "startDate": "2025-12-20",
  "endDate": "2025-12-31",
  "reason": "Holiday vacation"
}
```

**Response**: `AbsenceRequestResponse` (HTTP 201 Created)

---

#### `PATCH /absences/{id}/approve`
Approve an absence request (managers only)

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**: 
- `id` (UUID): Absence request ID

**Response**: 
```json
{
  "message": "Absence request approved",
  "id": "string"
}
```

---

#### `PATCH /absences/{id}/reject`
Reject an absence request (managers only)

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**: 
- `id` (UUID): Absence request ID

**Response**: 
```json
{
  "message": "Absence request rejected",
  "id": "string"
}
```

## Database Configuration

### Docker Setup (via docker-compose.yml)
- **Container Name**: hr-service-db
- **Service Name**: hr-db
- **Host** (from host machine): localhost
- **Port**: 5432 (direct port mapping, no external port)
- **Database**: hr_service_db
- **Username**: postgres
- **Password**: postgres
- **Image**: postgres:16-alpine

The application running in Docker connects to the database using the service name:
```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://hr-db:5432/hr_service_db
```

### Local Setup (via application.yaml)
When running the application locally (not in Docker), it uses:
- **Host**: localhost
- **Port**: 5432
- **Database**: hr_service_db
- **Username**: postgres
- **Password**: postgres

Configuration location: `src/main/resources/application.yaml`

### Database Schema

The schema is managed by Flyway migrations located in `src/main/resources/db/migration/`:

- **V1__create_initial_schema.sql**: Creates tables and indexes
  - `users` - Employee profiles with sensitive data (salary, SSN, bank account, etc.)
  - `absence_requests` - Time-off requests with status tracking
  - `feedback` - Employee feedback entries with AI polishing flag
  - Indexes for optimized queries
  
- **V2__insert_test_data.sql**: Loads test data
  - 1 manager account
  - 3 employee accounts
  - Sample absence requests (pending and approved)
  - Sample feedback entries

### Flyway Schema History
The `flyway_schema_history` table tracks which migrations have been applied and their checksums. If you modify a migration file after it's been applied, you'll get a checksum mismatch error (see Troubleshooting section).

## Security Configuration

- **JWT Secret**: Configure in `application.yaml` under `jwt.secret` (change in production!)
- **CORS**: Enabled for http://localhost:5173 (frontend)
- **Token Validation**: All endpoints except actuator require valid JWT token

## Error Handling

All errors follow the standard format:

```json
{
  "error": "Error Type",
  "message": "Human-readable message",
  "statusCode": 400
}
```

## Permission Model

### Employee Permissions
- View own full profile
- Edit own profile
- View all public profiles
- Leave feedback on other profiles
- Create absence requests
- View own absence requests

### Manager Permissions
- All employee permissions
- View all full profiles (including sensitive data)
- Edit any profile
- View all pending absence requests
- Approve/reject absence requests

## AI Service Integration

The feedback system can optionally polish text using an AI service. Ensure the AI service is running on http://localhost:8003 with the following endpoint:

**POST /polish**
```json
{
  "text": "string",
  "context": "employee feedback"
}
```

If the AI service is unavailable, the application will return an error but continue to function for other operations.

## Development

### Project Structure

```
src/main/java/com/hr_manager/hr_service/
├── client/              # Feign clients (AI service)
├── controller/          # REST controllers
├── dto/                 # Data transfer objects
├── entity/              # JPA entities
├── exception/           # Custom exceptions and global handler
├── repository/          # JPA repositories
├── security/            # Security configuration and JWT handling
├── service/             # Business logic
└── HrServiceApplication.java
```

### Adding New Features

1. Create entity in `entity/` package
2. Create repository in `repository/` package
3. Create DTOs in `dto/` package
4. Implement business logic in `service/` package
5. Create REST controller in `controller/` package
6. Add database migration in `resources/db/migration/`

## Monitoring

Spring Boot Actuator endpoints are available at:
- Health: http://localhost:8002/actuator/health
- Info: http://localhost:8002/actuator/info

## Testing

Run tests using:

```bash
# Windows
.\gradlew.bat test

# Linux/Mac
./gradlew test
```

## Troubleshooting

### Database Connection Issues

**Docker Setup**:
```powershell
# Check if database container is running
docker-compose ps

# View database logs
docker-compose logs hr-db

# Test database connection
docker exec hr-service-db pg_isready -U postgres

# Restart database
docker-compose restart hr-db
```

**Local Setup**:
- Ensure PostgreSQL service is running on port 5432
- Verify credentials in `application.yaml` match your PostgreSQL installation
- Check if database `hr_service_db` exists:
  ```powershell
  psql -U postgres -l
  ```

### Flyway Migration Errors

**Checksum Mismatch**: This occurs when a migration file is modified after being applied.

```
Migration checksum mismatch for migration version 2
-> Applied to database : 546961697
-> Resolved locally    : -1426371483
```

**Solution 1 - Clean Start** (deletes all data):
```powershell
docker-compose down -v
docker-compose up --build -d
```

**Solution 2 - Manual Repair** (preserves data):
```powershell
# Connect to database
docker exec -it hr-service-db psql -U postgres -d hr_service_db

# View migration history
SELECT * FROM flyway_schema_history;

# Delete the problematic migration entry
DELETE FROM flyway_schema_history WHERE version = '2';

# Exit
\q

# Restart application
docker-compose restart hr-service
```

### Docker Network Errors

```
Error: network not found
Error: failed to set up container networking
```

**Solution**:
```powershell
# Stop all containers
docker-compose down

# Remove unused networks
docker network prune -f

# If hr-network exists and causing issues
docker network rm hr-network

# Restart Docker Desktop

# Start fresh
docker-compose up -d
```

### Port Conflicts

**Port 8002 already in use**:
```powershell
# Find process using port 8002
netstat -ano | findstr :8002

# Kill the process (replace <PID> with actual process ID)
taskkill /PID <PID> /F
```

**Port 5432 already in use**:
```powershell
# Check if local PostgreSQL is running
Get-Service postgresql*

# Stop local PostgreSQL if using Docker
Stop-Service postgresql-x64-16  # Version may vary
```

### JWT Validation Errors

- Ensure the JWT secret matches between Auth Service and HR Service
- Both services should use the same secret key
- Check `application.yaml` and `docker-compose.yml` for consistency
- Verify token hasn't expired (default: 24 hours)
- Verify token format in request: `Authorization: Bearer <token>`

### AI Service Errors

The AI service is optional. If unavailable:
- Feedback creation will work, but AI polishing will fail
- Use `polishWithAI: false` in feedback requests
- Check if AI service is running: `curl http://localhost:8003/actuator/health`
- Review AI service logs

### Container Won't Start

```powershell
# View application logs
docker-compose logs hr-service

# Check build errors
docker-compose build hr-service

# Remove everything and rebuild
docker-compose down -v
docker system prune -f
docker-compose up --build -d
```

## Production Considerations

1. **Change JWT Secret**: Use a strong, unique secret key (min 256 bits)
2. **Database Credentials**: Use environment variables for sensitive data
3. **CORS Configuration**: Update allowed origins for production frontend URL
4. **SSL/TLS**: Enable HTTPS in production
5. **Logging**: Configure appropriate log levels
6. **Monitoring**: Set up monitoring for actuator endpoints

## License

Proprietary - Internal use only
