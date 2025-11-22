# How to Run HR Service Locally

## ⚠️ Important: You Need PostgreSQL Running!

The HR Service **requires** a PostgreSQL database to run. You have 3 options:

---

## Option 1: Using Docker (Recommended - Easiest) 🐳

### Prerequisites
- **Docker Desktop** must be installed AND running on Windows
- Download from: https://www.docker.com/products/docker-desktop/

### Steps

1. **Start Docker Desktop** 
   - Open Docker Desktop application
   - Wait until it shows "Docker Desktop is running"
   - You should see the Docker icon in your system tray

2. **Verify Docker is running**:
   ```powershell
   docker --version
   docker ps
   ```
   If these commands work, Docker is ready!

3. **Open PowerShell** and navigate to the project:
   ```powershell
   cd C:\Users\drendes\Documents\as\hr-app\hr-service
   ```

4. **Option A: Start database only** (then run app with Gradle):
   ```powershell
   # Start PostgreSQL
   docker-compose up hr-db -d
   
   # Wait 10-15 seconds, then verify it's running
   docker ps
   
   # Now run the app
   .\gradlew.bat bootRun
   ```

   **Option B: Start everything with Docker** (database + app):
   ```powershell
   docker-compose up --build
   ```
   
   This will:
   - Build the HR Service application
   - Start PostgreSQL database
   - Run Flyway migrations automatically
   - Start the HR Service on port 8002

5. **Access the app**:
   - API: http://localhost:8002
   - Check health: http://localhost:8002/actuator/health (if enabled)

6. **View logs**:
   ```powershell
   docker-compose logs -f
   ```

7. **Stop the services**:
   ```powershell
   docker-compose down
   ```

---

## Option 2: Local PostgreSQL Installation

### Prerequisites
- Java 21 installed
- PostgreSQL 12+ installed and running locally

### Steps

1. **Install PostgreSQL** (if not already installed):
   - Download from: https://www.postgresql.org/download/windows/
   - Install with default settings
   - Remember the password you set for the postgres user

2. **Create the database**:
   ```sql
   -- Connect to PostgreSQL using pgAdmin or psql
   CREATE DATABASE hr_service_db;
   ```

3. **Update credentials** (if different from defaults):
   - Edit `src/main/resources/application.yaml`
   - Update username/password if needed:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/hr_service_db
       username: postgres
       password: YOUR_PASSWORD_HERE
   ```

4. **Run the application**:
   ```powershell
   cd C:\Users\drendes\Documents\as\hr-app\hr-service
   .\gradlew.bat bootRun
   ```

5. **Access the app**:
   - API: http://localhost:8002

---

## Option 3: Run Tests with In-Memory Database

If you just want to run tests without setting up PostgreSQL, you can use an H2 in-memory database for testing.

See the `TEST_SETUP.md` file for instructions.

---

## Troubleshooting

### Test Failures
If you see connection errors when running tests:
```
java.net.ConnectException
org.postgresql.util.PSQLException
```

**Solution**: Make sure PostgreSQL is running (Docker or local installation) before running tests.

### Port Already in Use
If port 8002 is already in use:
```
java.net.BindException: Address already in use
```

**Solution**: Stop any other service using port 8002, or change the port in `application.yaml`:
```yaml
server:
  port: 8080  # or any available port
```

### Docker Issues
If Docker commands fail:
- Ensure Docker Desktop is running
- Check Docker Desktop settings -> Resources
- Try: `docker ps` to verify Docker is working

### AI Service Not Available
The app will still work without the AI service. Feedback polishing features will fail gracefully.
To run the AI service, follow its setup instructions separately.

