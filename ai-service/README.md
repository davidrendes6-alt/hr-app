# AI Service API

AI Service for HR Management Application - Provides text polishing capabilities using HuggingFace AI models.

## Overview

This service implements a REST API endpoint to polish/enhance text using AI models from HuggingFace. It's designed to integrate with the HR management system to improve feedback and other text content.

## Features

- **Text Polishing**: Enhance and improve text using AI models
- **HuggingFace Integration**: Uses free HuggingFace models (google/flan-t5-base)
- **Context-Aware**: Supports optional context for better text enhancement
- **CORS Enabled**: Configured for frontend integration (http://localhost:5173)
- **Error Handling**: Comprehensive error handling with proper status codes
- **Reactive Architecture**: Built with Spring WebFlux for non-blocking operations

## Prerequisites

- **Java 21** or higher
- **Gradle** (or use the included Gradle wrapper)
- Internet connection (to access HuggingFace API)

## Configuration

The service is configured to run on port **8003** as specified in the API specification.

### application.yaml

```yaml
server:
  port: 8003

huggingface:
  api:
    url: https://api-inference.huggingface.co/models
    model: google/flan-t5-base
    timeout: 30000
```

## Building the Project

### Using Gradle Wrapper (Recommended)

```bash
# Windows
.\gradlew.bat clean build

# Linux/Mac
./gradlew clean build
```

### Note on Java Version

This project requires Java 21. If you encounter JAVA_HOME issues:

**Windows:**
```cmd
set JAVA_HOME=C:\Path\To\Java21
set PATH=%JAVA_HOME%\bin;%PATH%
```

**Linux/Mac:**
```bash
export JAVA_HOME=/path/to/java21
export PATH=$JAVA_HOME/bin:$PATH
```

## Running the Application

### Using Gradle

```bash
# Windows
.\gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

### Using JAR

```bash
java -jar build/libs/ai-service-0.0.1-SNAPSHOT.jar
```

The service will start on `http://localhost:8003`

## Running with Docker

You can run the AI Service using Docker or Docker Compose. This is the recommended way for deployment or local development without installing Java/Gradle.

### Prerequisites
- [Docker](https://www.docker.com/get-started) installed
- (Optional) [Docker Compose](https://docs.docker.com/compose/) for multi-service orchestration

### Build and Run with Docker

1. **Build the Docker image:**
   ```bash
   docker build -t hr-ai-service .
   ```
2. **Run the container:**
   ```bash
   docker run -p 8003:8003 --name hr-ai-service hr-ai-service
   ```

### Build and Run with Docker Compose

1. **Start the service:**
   ```bash
   docker-compose up --build -d
   ```
2. **View logs:**
   ```bash
   docker-compose logs -f ai-service
   ```
3. **Stop the service:**
   ```bash
   docker-compose down
   ```

### Notes
- The first request to the HuggingFace model may take longer as the model loads (cold start).
- The service exposes port **8003** by default.
- You can configure environment variables in `docker-compose.yaml` or override them at runtime.

### Testing the Service in Docker
You can test the running service as described in the [Testing the API](#testing-the-api) section, e.g.:
```bash
curl -X POST http://localhost:8003/polish \
  -H "Content-Type: application/json" \
  -d '{"text": "this is some text that needs improvement"}'
```

## API Documentation

### POST /polish

Polish/enhance text using AI.

**Endpoint:** `POST http://localhost:8003/polish`

**Request Body:**
```json
{
  "text": "string (required)",
  "context": "string (optional)"
}
```

**Success Response (200 OK):**
```json
{
  "originalText": "The original input text",
  "polishedText": "The enhanced, polished text",
  "model": "google/flan-t5-base"
}
```

**Error Responses:**

- **400 Bad Request**: Invalid input (missing text field)
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Text is required",
  "timestamp": "2025-11-22T10:30:00"
}
```

- **500 Internal Server Error**: AI model error
```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "AI model error: <error details>",
  "timestamp": "2025-11-22T10:30:00"
}
```

## Testing the API

### Using cURL

**Basic Request:**
```bash
curl -X POST http://localhost:8003/polish \
  -H "Content-Type: application/json" \
  -d "{\"text\": \"this is some text that needs improvement\"}"
```

**With Context:**
```bash
curl -X POST http://localhost:8003/polish \
  -H "Content-Type: application/json" \
  -d "{\"text\": \"good work on project\", \"context\": \"Performance feedback for employee\"}"
```

### Using PowerShell

```powershell
$body = @{
    text = "this is some text that needs improvement"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8003/polish" -Method POST -Body $body -ContentType "application/json"
```

### Using a REST Client (e.g., Postman, Insomnia)

1. Create a POST request to `http://localhost:8003/polish`
2. Set Content-Type header to `application/json`
3. Add request body:
```json
{
  "text": "this is some text that needs improvement",
  "context": "Employee feedback"
}
```

## Project Structure

```
src/main/java/com/hr_manager/ai_service/
├── AiServiceApplication.java         # Main application class
├── config/
│   └── CorsConfig.java               # CORS configuration
├── controller/
│   └── PolishController.java         # REST endpoint controller
├── dto/
│   ├── PolishRequest.java            # Request DTO
│   ├── PolishResponse.java           # Response DTO
│   └── ErrorResponse.java            # Error response DTO
├── exception/
│   └── GlobalExceptionHandler.java   # Global exception handling
└── service/
    └── TextPolishingService.java     # HuggingFace integration service
```

## Key Components

### TextPolishingService
- Integrates with HuggingFace Inference API
- Handles prompt construction with optional context
- Implements retry logic for transient failures
- Extracts and processes AI model responses

### PolishController
- Exposes the `/polish` endpoint
- Validates incoming requests
- Returns reactive responses using Spring WebFlux

### GlobalExceptionHandler
- Catches validation errors (400)
- Catches AI model errors (500)
- Returns consistent error response format

### CorsConfig
- Enables CORS for `http://localhost:5173`
- Allows all standard HTTP methods
- Configured for credentials support

## HuggingFace Models

The service uses **google/flan-t5-base** by default. You can change the model in `application.yaml`:

### Alternative Models

- `facebook/bart-large-cnn` - Good for summarization and polishing
- `google/flan-t5-large` - Larger version, better quality
- `bigscience/bloom-560m` - Multilingual support

To change the model, update `application.yaml`:
```yaml
huggingface:
  api:
    model: facebook/bart-large-cnn
```

## Error Handling

The service implements comprehensive error handling:

1. **Validation Errors**: Missing or invalid request fields
2. **AI Model Errors**: Issues communicating with HuggingFace
3. **Timeout Errors**: Requests that take too long (30s timeout)
4. **Network Errors**: Connection issues to HuggingFace API

All errors return appropriate HTTP status codes and detailed error messages.

## CORS Configuration

CORS is enabled for the frontend running at `http://localhost:5173`. Allowed methods:
- GET
- POST
- PUT
- DELETE
- OPTIONS

## Logging

The service uses SLF4J with Logback for logging:
- INFO level: Successful operations
- ERROR level: Failures and exceptions

Logs include:
- Request processing
- AI model interactions
- Error details

## Integration with HR Service

The HR Service calls this endpoint when users request AI-powered text polishing for feedback:

```java
// Example integration from HR Service
POST /profiles/{id}/feedback
{
  "content": "Original feedback text",
  "polishWithAI": true
}
```

The HR Service will:
1. Call `POST /polish` with the feedback content
2. Use the `polishedText` from the response
3. Save the polished feedback with `isPolished: true`

## Troubleshooting

### Service won't start
- Check if port 8003 is available
- Verify Java 21 is installed: `java -version`
- Check JAVA_HOME environment variable

### AI model errors
- Verify internet connectivity
- Check HuggingFace API status
- Model may be loading (first request can be slow)
- Try a different model in configuration

### CORS errors
- Verify frontend is running on http://localhost:5173
- Check browser console for specific CORS errors
- Ensure CorsConfig is properly loaded

## Performance Notes

- First request may be slower (model cold start on HuggingFace)
- Subsequent requests are faster
- Timeout set to 30 seconds
- Retry mechanism for transient failures (2 retries with backoff)

## License

Part of HR Management Application

## Support

For issues or questions, please contact the development team.
