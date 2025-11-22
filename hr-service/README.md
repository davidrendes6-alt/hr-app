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
- PostgreSQL 12 or higher
- AI Service running on port 8003 (optional, for feedback polishing)

## Database Setup

1. Create a PostgreSQL database:

```sql
CREATE DATABASE hr_service_db;
```

2. Update the database credentials in `src/main/resources/application.yaml` if needed:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hr_service_db
    username: postgres
    password: postgres
```

3. Flyway will automatically create the schema and load test data on startup.

## Running the Application

⚠️ **Important**: You need PostgreSQL running before you start the app!

### Quick Start (Recommended - Docker)

1. **Start Docker Desktop** (install from https://docker.com if needed)
2. **Start PostgreSQL**:
   ```powershell
   docker-compose up hr-db -d
   ```
3. **Wait 15 seconds**, then run:
   ```powershell
   .\gradlew.bat bootRun
   ```

### Using the Helper Script

```powershell
.\start.ps1
```
This script automatically checks Docker, starts PostgreSQL, and runs the app!

### Manual Setup (Without Docker)

See `RUN_LOCALLY.md` for detailed instructions on installing PostgreSQL manually.

## Running with Docker

You can run the entire HR Service stack (application + PostgreSQL) using Docker and Docker Compose. This is the easiest way to get started, as it requires no manual database setup.

### 1. Build and Start the Services

From the `hr-service` directory, run:

```bash
docker-compose up --build -d
```

- This will build the HR Service Docker image, start a PostgreSQL database, and run migrations automatically.
- The first build may take several minutes as Gradle and Docker images are downloaded.

### 2. Access the Service

- HR Service API: [http://localhost:8002](http://localhost:8002)
- PostgreSQL: localhost:5432 (for debugging/development)

### 3. Stopping and Cleaning Up

To stop the services:

```bash
docker-compose down
```

To remove all data (including the database volume):

```bash
docker-compose down -v
```

### 4. Logs

To view logs for all services:

```bash
docker-compose logs -f
```

To view logs for just the HR Service:

```bash
docker-compose logs -f hr-service
```

### 5. Environment Variables

- Database and JWT secret are set in `docker-compose.yml` and override `application.yaml`.
- You can change the JWT secret and other settings in the compose file as needed.

### 6. AI Service

- The compose file includes a placeholder for the AI service. Uncomment and configure it if you have an AI service Docker image.

The service will start on **http://localhost:8002**

## Test Data

The application comes with pre-loaded test users:

### Manager Account
- **Email**: manager@company.com
- **Password**: password123
- **Role**: manager
- **Name**: Jane Manager

### Employee Accounts
- **Email**: employee1@company.com, **Password**: password123, **Name**: John Employee
- **Email**: employee2@company.com, **Password**: password123, **Name**: Alice Developer
- **Email**: employee3@company.com, **Password**: password123, **Name**: Bob Smith

**Note**: You need to authenticate through the Auth Service (port 8001) to get a JWT token before using these endpoints.

## API Endpoints

### Profile Endpoints

#### GET /profiles/me
Get current user's full profile (including sensitive data)

**Headers**: `Authorization: Bearer <token>`

#### GET /profiles/{id}
Get profile by ID
- Managers and profile owners see full profile
- Other employees see public profile only

**Headers**: `Authorization: Bearer <token>`

#### PUT /profiles/{id}
Update profile (owner or manager only)

**Headers**: `Authorization: Bearer <token>`

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

#### GET /profiles
Get list of all employees (public profiles only)

**Headers**: `Authorization: Bearer <token>`

### Feedback Endpoints

#### GET /profiles/{profileId}/feedback
Get all feedback for a profile

**Headers**: `Authorization: Bearer <token>`

#### POST /profiles/{profileId}/feedback
Leave feedback on a profile

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "content": "string",
  "polishWithAI": boolean
}
```

**Note**: Cannot leave feedback on own profile

### Absence Request Endpoints

#### GET /absences/me
Get current user's absence requests

**Headers**: `Authorization: Bearer <token>`

#### GET /absences/pending
Get all pending absence requests (managers only)

**Headers**: `Authorization: Bearer <token>`

#### POST /absences
Create new absence request

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "startDate": "2025-12-20",
  "endDate": "2025-12-31",
  "reason": "Holiday vacation"
}
```

#### PATCH /absences/{id}/approve
Approve an absence request (managers only)

**Headers**: `Authorization: Bearer <token>`

#### PATCH /absences/{id}/reject
Reject an absence request (managers only)

**Headers**: `Authorization: Bearer <token>`

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
- Ensure PostgreSQL is running on port 5432
- Verify database credentials in `application.yaml`
- Check if database `hr_service_db` exists

### JWT Validation Errors
- Ensure the JWT secret matches between Auth Service and HR Service
- Check token expiration time
- Verify token format: `Bearer <token>`

### AI Service Errors
- Check if AI service is running on port 8003
- Feedback creation will fail if AI polishing is requested but service is unavailable
- Consider making AI polishing optional in production

## Production Considerations

1. **Change JWT Secret**: Use a strong, unique secret key (min 256 bits)
2. **Database Credentials**: Use environment variables for sensitive data
3. **CORS Configuration**: Update allowed origins for production frontend URL
4. **SSL/TLS**: Enable HTTPS in production
5. **Logging**: Configure appropriate log levels
6. **Monitoring**: Set up monitoring for actuator endpoints

## License

Proprietary - Internal use only
