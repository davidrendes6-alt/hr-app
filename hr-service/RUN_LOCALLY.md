# Running HR Service Locally

This guide explains different ways to run the HR Service locally for development.

## ⚠️ Important: Database Required

The HR Service **requires** a PostgreSQL database to run. Choose one of the options below.

---

## Option 1: Docker for Database Only (Recommended) 🐳

Run PostgreSQL in Docker and the application locally with Gradle. This gives you the best of both worlds: easy database setup with local debugging.

### Prerequisites

- **Docker Desktop** installed and running
- **Java 21** installed
- Download Docker: https://www.docker.com/products/docker-desktop/

### Steps

1. **Verify Docker is Running**
   ```powershell
   docker --version
   docker ps
   ```

2. **Start PostgreSQL Container**
   ```powershell
   cd C:\Users\drendes\Documents\as\hr-app\hr-service
   docker-compose up hr-db -d
   ```

3. **Wait for Database to be Ready**
   ```powershell
   # Wait ~10 seconds, then verify
   docker-compose ps
   
   # Should show hr-service-db as healthy
   ```

4. **Run the Application**
   ```powershell
   .\gradlew.bat bootRun
   ```

5. **Access the Application**
   - API: http://localhost:8002
   - Health check: http://localhost:8002/actuator/health

6. **Stop the Database (when done)**
   ```powershell
   docker-compose down
   ```

### Advantages
- ✅ No PostgreSQL installation needed
- ✅ Clean database on each restart
- ✅ Easy to debug locally
- ✅ Fast iteration during development

---

## Option 2: Everything in Docker

Run both the application and database in Docker containers.

### Steps

1. **Build and Start All Services**
   ```powershell
   docker-compose up --build -d
   ```

2. **View Logs**
   ```powershell
   # All services
   docker-compose logs -f
   
   # Just the application
   docker-compose logs -f hr-service
   
   # Just the database
   docker-compose logs -f hr-db
   ```

3. **Access the Application**
   - API: http://localhost:8002

4. **Stop Services**
   ```powershell
   # Stop (preserves data)
   docker-compose down
   
   # Stop and delete data
   docker-compose down -v
   ```

### Advantages
- ✅ Production-like environment
- ✅ No local Java/Gradle needed
- ✅ Easy to share with team
- ❌ Slower rebuild times
- ❌ Harder to debug

---

## Option 3: Local PostgreSQL Installation

Install PostgreSQL directly on Windows and run everything locally.

### Prerequisites

- **Java 21** installed
- **PostgreSQL 12+** installed locally

### Steps

1. **Install PostgreSQL**
   - Download from: https://www.postgresql.org/download/windows/
   - Install with default settings (port 5432)
   - Remember the postgres user password!

2. **Create Database**
   ```powershell
   # Connect using pgAdmin or psql
   psql -U postgres
   ```
   
   ```sql
   -- Create database
   CREATE DATABASE hr_service_db;
   
   -- Verify
   \l
   
   -- Exit
   \q
   ```

3. **Configure Application**
   
   Edit `src/main/resources/application.yaml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/hr_service_db
       username: postgres
       password: your_actual_password  # Change this!
   ```
   
   Or use environment variables (recommended):
   ```powershell
   $env:SPRING_DATASOURCE_USERNAME="postgres"
   $env:SPRING_DATASOURCE_PASSWORD="your_password"
   ```

4. **Run the Application**
   ```powershell
   .\gradlew.bat bootRun
   ```

5. **Access the Application**
   - API: http://localhost:8002

### Advantages
- ✅ Full control over database
- ✅ Persists data between restarts
- ✅ Good for long-term development
- ❌ Requires PostgreSQL installation
- ❌ Manual database management

### Managing PostgreSQL Service

```powershell
# Check if PostgreSQL is running
Get-Service postgresql*

# Start PostgreSQL
Start-Service postgresql-x64-16  # Version may vary

# Stop PostgreSQL
Stop-Service postgresql-x64-16

# Restart PostgreSQL
Restart-Service postgresql-x64-16
```

---

## Database Tools

### pgAdmin (GUI)
- Usually installed with PostgreSQL
- Access at http://localhost:5050 (default)
- Great for viewing tables and data

### psql (Command Line)
```powershell
# Connect to database
psql -U postgres -d hr_service_db

# List tables
\dt

# View table structure
\d users

# Query data
SELECT * FROM users;
SELECT * FROM flyway_schema_history;

# Exit
\q
```

### DBeaver (Universal Tool)
- Download from https://dbeaver.io/
- Supports PostgreSQL and many other databases
- Good alternative to pgAdmin

---

## Troubleshooting

### "Connection refused" or "Connection to localhost:5432 refused"

**Docker Database**:
```powershell
# Check if container is running
docker-compose ps

# Should show hr-service-db as "Up" and "healthy"

# If not, view logs
docker-compose logs hr-db

# Restart
docker-compose restart hr-db
```

**Local PostgreSQL**:
```powershell
# Check if service is running
Get-Service postgresql*

# Start if stopped
Start-Service postgresql-x64-16
```

### Flyway Checksum Mismatch

This happens when migration files are modified after being applied.

**Error**:
```
Migration checksum mismatch for migration version 2
-> Applied to database : 546961697
-> Resolved locally    : -1426371483
```

**Solution 1 - Fresh Database** (Docker):
```powershell
# Delete everything and start fresh
docker-compose down -v
docker-compose up hr-db -d
.\gradlew.bat bootRun
```

**Solution 2 - Manual Fix** (preserves data):
```powershell
# Docker database
docker exec -it hr-service-db psql -U postgres -d hr_service_db

# OR local PostgreSQL
psql -U postgres -d hr_service_db
```

```sql
-- View migration history
SELECT * FROM flyway_schema_history;

-- Delete problematic migration
DELETE FROM flyway_schema_history WHERE version = '2';

-- Exit
\q
```

Then restart the application.

**Solution 3 - Complete Reset** (local PostgreSQL):
```sql
-- Connect to postgres database (not hr_service_db)
psql -U postgres

-- Drop and recreate
DROP DATABASE hr_service_db;
CREATE DATABASE hr_service_db;

-- Exit
\q
```

### Port 8002 Already in Use

```powershell
# Find process using port 8002
netstat -ano | findstr :8002

# Kill the process
taskkill /PID <process_id> /F

# Or change port in application.yaml
server:
  port: 8080  # or any available port
```

### Port 5432 Already in Use

If you're trying to use Docker but local PostgreSQL is running:

```powershell
# Stop local PostgreSQL
Stop-Service postgresql*

# Or run Docker on different port by editing docker-compose.yml:
ports:
  - "5433:5432"  # Use port 5433 on host
```

### Gradle Build Errors

```powershell
# Clean build
.\gradlew.bat clean build

# Skip tests
.\gradlew.bat bootRun --args='--spring.profiles.active=dev'

# Clear Gradle cache (if really stuck)
Remove-Item -Recurse -Force ~/.gradle/caches
```

### Docker Network Issues

```
Error: network not found
Error: failed to set up container networking
```

**Solution**:
```powershell
# Stop containers
docker-compose down

# Clean up networks
docker network prune -f

# Remove specific network if exists
docker network rm hr-network

# Restart Docker Desktop

# Start again
docker-compose up hr-db -d
```

### AI Service Not Available

The AI service is optional. To run without it:
- Set `polishWithAI: false` in feedback requests
- Or disable AI client by commenting out Feign client

### Test Failures

If tests fail with connection errors:
```powershell
# Tests use H2 in-memory database by default
# Check test configuration in src/test/resources/application.yaml

# Run tests
.\gradlew.bat test

# Skip tests when running
.\gradlew.bat bootRun -x test
```

---

## Switching Between Options

### From Docker to Local PostgreSQL

```powershell
# Stop Docker database
docker-compose down

# Start local PostgreSQL
Start-Service postgresql*

# Run application
.\gradlew.bat bootRun
```

### From Local to Docker

```powershell
# Stop local PostgreSQL
Stop-Service postgresql*

# Start Docker
docker-compose up hr-db -d

# Run application
.\gradlew.bat bootRun
```

### Run Both Simultaneously

Modify `docker-compose.yml` to use different port:
```yaml
ports:
  - "5433:5432"  # External port 5433
```

Then connect application to local PostgreSQL on 5432, Docker on 5433.

---

## Quick Reference

### Docker Commands

```powershell
# Start database
docker-compose up hr-db -d

# Stop database
docker-compose down

# View logs
docker-compose logs -f hr-db

# Execute SQL in container
docker exec -it hr-service-db psql -U postgres -d hr_service_db

# Check health
docker exec hr-service-db pg_isready -U postgres
```

### Gradle Commands

```powershell
# Run application
.\gradlew.bat bootRun

# Run tests
.\gradlew.bat test

# Build JAR
.\gradlew.bat build

# Clean build
.\gradlew.bat clean build
```

### Useful Database Queries

```sql
-- Check all users
SELECT id, email, name, role, department FROM users;

-- Check absence requests
SELECT * FROM absence_requests ORDER BY created_at DESC;

-- Check feedback
SELECT * FROM feedback ORDER BY created_at DESC;

-- Check Flyway migrations
SELECT * FROM flyway_schema_history;

-- Reset a user's password (BCrypt hash for 'password123')
UPDATE users 
SET password_hash = '$2a$10$tQkKlsqmJYtYGV6jVN.P9.Gi9VWfLQ2ZIH.RJAz0zPloepf5P7Fdy'
WHERE email = 'manager@company.com';
```

---

## Need Help?

- Check logs: `docker-compose logs -f` or application console output
- Review `application.yaml` for configuration
- Verify database connection: `docker-compose ps` or `Get-Service postgresql*`
- Check if ports are available: `netstat -ano | findstr :8002`
