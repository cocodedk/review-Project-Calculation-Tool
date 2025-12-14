# Docker Setup Guide

## What's Needed to Dockerize This Project

### Current State
- ✅ Spring Boot 3.5.7 application
- ✅ Java 17
- ✅ Maven build system
- ✅ MySQL database dependency
- ✅ H2 database support (for development)
- ❌ No Docker configuration exists
- ⚠️ **CRITICAL**: Hardcoded database credentials in `application-mysql.properties`

### Required Files

1. **Dockerfile** - Multi-stage build for the Spring Boot application
2. **docker-compose.yml** - Orchestration for app + MySQL database
3. **.dockerignore** - Exclude unnecessary files from build context
4. **Environment configuration** - Move secrets to environment variables

### Implementation Steps

#### 1. Create Dockerfile (Multi-stage Build)

**Location**: Root directory

**Benefits**:
- Smaller final image size
- Faster builds (caching layers)
- Security (no build tools in production image)

#### 2. Create docker-compose.yml

**Purpose**:
- Run application + MySQL together
- Handle networking between containers
- Volume management for database persistence
- Environment variable management

#### 3. Create .dockerignore

**Purpose**:
- Exclude unnecessary files from Docker build context
- Faster builds
- Smaller context size

#### 4. Fix Security Issues First

**CRITICAL**: Before Dockerizing:
- Remove hardcoded credentials from `application-mysql.properties`
- Use environment variables
- Update application properties to read from environment

### Estimated Effort

- **Basic Dockerfile**: 30 minutes
- **docker-compose.yml**: 30 minutes
- **Security fixes**: 1-2 hours (move credentials to env vars)
- **Testing**: 1 hour
- **Total**: ~3-4 hours

### Considerations

1. **Database**:
   - Use MySQL container or external database
   - Handle database initialization (schema.sql, data.sql)
   - Volume for data persistence

2. **Ports**:
   - Default Spring Boot port: 8080
   - MySQL port: 3306

3. **Profiles**:
   - Development: H2 (in-memory)
   - Production: MySQL (container or external)

4. **Build**:
   - Maven wrapper included (mvnw)
   - Can use Maven or Spring Boot plugin

5. **Health Checks**:
   - Add Spring Boot Actuator for health endpoints
   - Docker healthcheck configuration

### Next Steps

Would you like me to create:
1. ✅ Dockerfile (multi-stage build)
2. ✅ docker-compose.yml (with MySQL)
3. ✅ .dockerignore
4. ✅ Updated application properties (using env vars)
5. ✅ Documentation for running with Docker
