# HR Management Application

A comprehensive HR management system built with microservices architecture, featuring employee management, authentication, and AI-powered text assistance.

## Overview

This application consists of three microservices:

1. **Auth Service** (port 8001) - JWT-based authentication and user management
2. **HR Service** (port 8002) - Employee profiles, feedback, and absence management
3. **AI Service** (port 8003) - Text polishing and improvement using OpenAI
4. **Frontend** (port 5173) - React-based user interface

## Architecture

```
hr-app/
├── auth-service/     # Authentication microservice
├── hr-service/       # HR management microservice
├── ai-service/       # AI text processing microservice
└── frontend/         # React frontend application
```

## Prerequisites

- **Java 21** - for backend services
- **Node.js 18+** - for frontend
- **PostgreSQL** - database server
- **Docker & Docker Compose** - for containerized deployment
- **OpenAI API Key** - for AI service (optional)

## Quick Start

### 1. Database Setup

Create the required databases:

```sql
CREATE DATABASE auth_db;
CREATE DATABASE hr_db;
```

### 2. Environment Configuration

**AI Service** - Create `ai-service/src/main/resources/application.yaml`:

```yaml
huggingface:
  api:
    key: add_your_hf_token
```

### 3. Running with Docker (Recommended)

From the `hr-app` root directory:

```bash
# Start all services
docker-compose up --build -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

**Services will be available at:**
- Frontend: http://localhost:5173
- Auth Service: http://localhost:8001
- HR Service: http://localhost:8002
- AI Service: http://localhost:8003

### 4. Running Locally (Development)

**Backend Services:**

```bash
# Auth Service
cd auth-service
./gradlew bootRun

# HR Service (in another terminal)
cd hr-service
./gradlew bootRun

# AI Service (in another terminal)
cd ai-service
./gradlew bootRun
```

**Frontend:**

```bash
cd frontend
npm install
npm run dev
```

## Test Users

The system comes with pre-loaded test accounts:

| Email | Password | Role | Description |
|-------|----------|------|-------------|
| manager@company.com | password123 | manager | Can view all employees, approve requests |
| employee1@company.com | password123 | employee | Standard employee access |
| employee2@company.com | password123 | employee | Standard employee access |

## Features

### Authentication
- JWT-based authentication
- Secure password hashing with BCrypt
- Token validation and refresh
- Role-based access control (Manager/Employee)

### HR Management
- **Employee Profiles**: View and update personal information
- **Feedback System**: Submit and review employee feedback
- **Absence Management**: Request and track time off
- **Manager Dashboard**: Approve/reject absence requests and view team data

### AI Assistance
- Text polishing and improvement
- Professional tone adjustment
- Grammar and clarity enhancement
- Powered by OpenAI GPT-4

## API Endpoints

### Auth Service (`:8001`)
- `POST /auth/login` - User authentication
- `POST /auth/logout` - Token invalidation
- `GET /auth/validate` - Token validation

### HR Service (`:8002`)
- `GET /api/employees` - List employees (managers only)
- `GET /api/employees/{id}` - Get employee details
- `PUT /api/employees/{id}` - Update employee profile
- `POST /api/feedback` - Submit feedback
- `GET /api/feedback/employee/{id}` - Get employee feedback
- `POST /api/absences` - Request absence
- `GET /api/absences/employee/{id}` - Get employee absences
- `GET /api/absences` - Get all absences (managers only)
- `PUT /api/absences/{id}` - Approve/reject absence

### AI Service (`:8003`)
- `POST /api/ai/polish` - Polish and improve text

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.5.9
- **Language**: Java 21
- **Database**: PostgreSQL
- **Migration**: Flyway
- **Security**: JWT, BCrypt
- **Build Tool**: Gradle

### Frontend
- **Framework**: React 18
- **Language**: TypeScript
- **Styling**: CSS Modules
- **HTTP Client**: Axios
- **Build Tool**: Vite

### DevOps
- **Containerization**: Docker
- **Orchestration**: Docker Compose

## Database Schema

### Auth Service (`auth_db`)
- `users` - User accounts with authentication details
- `flyway_schema_history` - Migration tracking

### HR Service (`hr_db`)
- `employees` - Employee profiles and information
- `feedback` - Employee feedback records
- `absences` - Absence requests and approvals
- `flyway_schema_history` - Migration tracking

## Development

### Running Tests

```bash
# Backend services
./gradlew test

# Frontend
cd frontend
npm test
```

### Building for Production

```bash
# Backend services
./gradlew build

# Frontend
cd frontend
npm run build
```

## Troubleshooting

### Database Connection Issues
- Ensure PostgreSQL is running on `localhost:5432`
- Verify databases `auth_db` and `hr_db` exist
- Check credentials in `application.yaml` files

### Flyway Migration Errors
Reset the database schema:

```sql
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
```

### Port Conflicts
Ensure ports 8001, 8002, 8003, and 5173 are available.

### Docker Issues
```bash
# Clean rebuild
docker-compose down -v
docker-compose up --build
```

## Security Notes

⚠️ **Important for Production:**
- Change JWT secrets in all `application.yaml` files
- Use environment variables for sensitive data
- Enable HTTPS/TLS
- Implement proper token blacklisting
- Secure OpenAI API key
- Configure production CORS policies

## Project Structure

### Auth Service
See [auth-service/README.md](auth-service/README.md) for detailed documentation on:
- Authentication endpoints
- JWT configuration
- Database schema
- Test users

### HR Service
Manages employee data, feedback, and absence requests.

### AI Service
Integrates with OpenAI API to provide text improvement features.

### Frontend
React application providing the user interface for all features.

## Future improvements
- Add unit and integration tests
- Add a new "Gateway" service with which the frontend communicates where the requests are validated and routed further to the other services
- The communication between hr-service and ai-service could be done using Kafka (wanted to do it that way from the begining, but stuck to this approach in the end)

## License

This is a demo application for educational purposes.

