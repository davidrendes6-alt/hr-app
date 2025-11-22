# Test Setup Guide

## Running Tests Without PostgreSQL

The test suite is now configured to use H2 in-memory database instead of PostgreSQL, so you can run tests without needing to set up a database.

### Running Tests

```powershell
# Run all tests
.\gradlew.bat test

# Run tests with detailed output
.\gradlew.bat test --info

# Run a specific test class
.\gradlew.bat test --tests "com.hr_manager.hr_service.HrServiceApplicationTests"
```

### Configuration

Tests use a separate configuration file:
- **Location**: `src/test/resources/application.yaml`
- **Database**: H2 in-memory database
- **Auto-configured**: No manual setup required

### What Changed

1. **Added H2 Dependency**: `com.h2database:h2` for in-memory database during tests
2. **Test Configuration**: Separate `application.yaml` in `src/test/resources` that:
   - Uses H2 instead of PostgreSQL
   - Disables Flyway (H2 uses `ddl-auto: create-drop` instead)
   - Enables H2 console for debugging

### Running with Real PostgreSQL (Optional)

If you want to run tests against real PostgreSQL instead of H2:

1. Start PostgreSQL (via Docker or locally)
2. Delete or rename `src/test/resources/application.yaml`
3. Tests will use the main `application.yaml` configuration

### View Test Reports

After running tests, view the HTML report at:
```
build/reports/tests/test/index.html
```

### Common Test Scenarios

#### Test with Coverage
```powershell
.\gradlew.bat test jacocoTestReport
```

#### Clean and Test
```powershell
.\gradlew.bat clean test
```

#### Skip Tests (when building)
```powershell
.\gradlew.bat build -x test
```

### Debugging Tests

The H2 console is enabled in test mode:
- URL: http://localhost:8080/h2-console (when app is running in test mode)
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

### Notes

- Tests run in isolation with a clean database each time
- Mockito warnings about Java agents are expected and can be ignored
- Spring Security creates a random password for test runs (not needed for these tests)

