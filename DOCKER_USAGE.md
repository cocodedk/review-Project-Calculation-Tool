# Docker Usage Guide

## Quick Start

### Prerequisites
- Docker installed
- Docker Compose installed

### 1. Setup Environment Variables

Copy the example environment file:
```bash
cp .env.example .env
```

Edit `.env` and set your database passwords and other configuration.

### 2. Build and Run

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Stop and remove volumes (deletes database data)
docker-compose down -v
```

### 3. Access the Application

- Application: http://localhost:8080
- MySQL: localhost:3306

## Development Workflow

### Rebuild After Code Changes

```bash
# Rebuild and restart
docker-compose up -d --build

# Or rebuild just the app
docker-compose build app
docker-compose up -d app
```

### View Logs

```bash
# All services
docker-compose logs -f

# Just the app
docker-compose logs -f app

# Just MySQL
docker-compose logs -f mysql
```

### Database Access

```bash
# Connect to MySQL container
docker-compose exec mysql mysql -u appuser -p calculationstool

# Or use root
docker-compose exec mysql mysql -u root -p
```

### Run Tests

```bash
# Run tests in container
docker-compose exec app mvn test

# Or build with tests
docker-compose run --rm app mvn test
```

### Running Test Scripts in Container

The container includes test login scripts (`test-login.sh`). To use them, you need to provide test credentials via environment variables:

**Option 1: Set in docker-compose.yml or .env file**
```bash
# Add to your .env file (not committed to git)
TEST_DEFAULT_USERNAME=test@example.com
TEST_DEFAULT_PASSWORD=testpassword
TEST_DEFAULT_HOST=localhost:8080
```

Then run the test script:
```bash
docker-compose exec app /app/test-login.sh
```

**Option 2: Pass environment variables directly**
```bash
docker-compose exec -e TEST_DEFAULT_USERNAME=test@example.com -e TEST_DEFAULT_PASSWORD=testpass app /app/test-login.sh
```

**Option 3: Pass credentials as arguments**
```bash
docker-compose exec app /app/test-login.sh test@example.com testpass localhost:8080
```

**For Java LoginTestScript:**
```bash
# Set environment variables in container
docker-compose exec -e TEST_DEFAULT_USERNAME=test@example.com -e TEST_DEFAULT_PASSWORD=testpass app sh -c "java -cp app.jar com.example.pkveksamen.LoginTestScript"
```

⚠️ **Note**: Test credentials are for development/testing only. See `TEST_CREDENTIALS.md` for more details.

## Production Deployment

### Build Image

```bash
docker build -t pct-app:latest .
```

### Run with External Database

```bash
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=mysql \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://your-db-host:3306/calculationstool \
  -e SPRING_DATASOURCE_USERNAME=your_user \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  pct-app:latest
```

## Troubleshooting

### Application won't start

1. Check if MySQL is healthy:
   ```bash
   docker-compose ps
   ```

2. Check application logs:
   ```bash
   docker-compose logs app
   ```

3. Verify database connection:
   ```bash
   docker-compose exec mysql mysql -u appuser -p -e "SHOW DATABASES;"
   ```

### Database initialization issues

1. Check if schema.sql and data.sql are being executed:
   ```bash
   docker-compose logs mysql | grep "Executing"
   ```

2. Manually initialize if needed:
   ```bash
   docker-compose exec mysql mysql -u root -p calculationstool < src/main/resources/schema.sql
   ```

### Port conflicts

If port 8080 or 3306 are already in use, change them in `.env`:
```
APP_PORT=8081
MYSQL_PORT=3307
```

## Security Notes

⚠️ **IMPORTANT**:
- Never commit `.env` file with real credentials
- Use secrets management in production (Docker secrets, Kubernetes secrets, etc.)
- Change default passwords
- Use strong passwords for production

## Health Checks

The application includes a health check endpoint. To use it, add Spring Boot Actuator:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Then enable health endpoint in `application.properties`:
```properties
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=when-authorized
```
