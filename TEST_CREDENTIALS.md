# Test Credentials Configuration

## Overview

Test credentials are used for development and testing purposes only. These credentials should **NEVER** be used in production environments.

## Security Warning

⚠️ **IMPORTANT**: If test credentials were ever exposed in version control or used in production, they **MUST be rotated immediately**.

## Configuration Methods

Test credentials can be provided in two ways:

### Method 1: Environment Variables (Recommended for CI/CD)

Set the following environment variables:

```bash
export TEST_DEFAULT_USERNAME="your.test.email@example.com"
export TEST_DEFAULT_PASSWORD="your_test_password"
export TEST_DEFAULT_HOST="localhost:8080"  # Optional, defaults to localhost:8080
```

### Method 2: Local Configuration File (For Development)

1. Copy the example file:
   ```bash
   cp .env.test.example .env.test
   ```

2. Edit `.env.test` and fill in your test credentials:
   ```properties
   TEST_DEFAULT_USERNAME=your.test.email@example.com
   TEST_DEFAULT_PASSWORD=your_test_password
   TEST_DEFAULT_HOST=localhost:8080
   ```

3. The `.env.test` file is automatically gitignored and will not be committed to version control.

## Usage in Test Scripts

### LoginTestScript.java

The Java test script (`src/test/java/com/example/pkveksamen/LoginTestScript.java`) will:
1. First check for environment variables (`TEST_DEFAULT_USERNAME`, `TEST_DEFAULT_PASSWORD`, `TEST_DEFAULT_HOST`)
2. If not found, attempt to load from `.env.test` file
3. Throw an error if neither is available

Usage:
```bash
# Using environment variables
export TEST_DEFAULT_USERNAME="test@example.com"
export TEST_DEFAULT_PASSWORD="testpass"
java LoginTestScript

# Or pass credentials as command-line arguments
java LoginTestScript test@example.com testpass localhost:8080
```

### test-login.sh

The shell script (`test-login.sh`) will:
1. Load `.env.test` if it exists
2. Check for environment variables
3. Accept command-line arguments as overrides

Usage:
```bash
# Using .env.test or environment variables
./test-login.sh

# Or pass credentials as arguments
./test-login.sh test@example.com testpass localhost:8080
```

### test-login-curl.sh

Same behavior as `test-login.sh` but uses `curl` for better cookie/session support.

## Container Usage

Since the application runs in Docker containers, test credentials should be provided via environment variables (the `.env.test` file approach is primarily for local development outside containers).

### Docker Compose

The `docker-compose.yml` already includes support for test credential environment variables. Add them to your `.env` file:

```bash
# Add to .env file (gitignored)
TEST_DEFAULT_USERNAME=test@example.com
TEST_DEFAULT_PASSWORD=testpassword
TEST_DEFAULT_HOST=localhost:8080
```

Then test scripts will automatically pick them up:
```bash
docker-compose exec app /app/test-login.sh
```

### Running Test Scripts in Container

**test-login.sh:**
```bash
# Using environment variables from docker-compose
docker-compose exec app /app/test-login.sh

# Or pass credentials as arguments
docker-compose exec app /app/test-login.sh test@example.com testpass localhost:8080
```

**LoginTestScript.java:**

Note: `LoginTestScript.java` is in the test directory and not included in the production JAR. To use it in a container, you have two options:

1. **Compile and run from test sources** (requires rebuilding container with test classes):
   ```bash
   # This requires modifying Dockerfile to include test classes or mounting them
   docker-compose exec app sh -c "cd /app && javac -cp app.jar src/test/java/com/example/pkveksamen/LoginTestScript.java && java -cp app.jar:. com.example.pkveksamen.LoginTestScript"
   ```

2. **Use environment variables** (recommended for containers):
   ```bash
   docker-compose exec -e TEST_DEFAULT_USERNAME=test@example.com -e TEST_DEFAULT_PASSWORD=testpass app sh -c "java -cp app.jar com.example.pkveksamen.LoginTestScript test@example.com testpass localhost:8080"
   ```

**Note**: For containerized environments, the shell scripts (`test-login.sh` and `test-login-curl.sh`) are recommended as they're already included in the container and easier to use.

### Mounting .env.test (Alternative)

If you prefer using a `.env.test` file, you can mount it as a volume in `docker-compose.yml`:

```yaml
volumes:
  - ./.env.test:/app/.env.test:ro
```

However, environment variables are recommended for containers as they're more secure and easier to manage.

## CI/CD Configuration

For continuous integration environments, set the environment variables in your CI/CD platform:

### GitHub Actions Example

```yaml
env:
  TEST_DEFAULT_USERNAME: ${{ secrets.TEST_DEFAULT_USERNAME }}
  TEST_DEFAULT_PASSWORD: ${{ secrets.TEST_DEFAULT_PASSWORD }}
  TEST_DEFAULT_HOST: localhost:8080
```

### GitLab CI Example

```yaml
variables:
  TEST_DEFAULT_USERNAME: $TEST_DEFAULT_USERNAME
  TEST_DEFAULT_PASSWORD: $TEST_DEFAULT_PASSWORD
  TEST_DEFAULT_HOST: "localhost:8080"
```

### Docker Compose Example

Add to your `docker-compose.yml`:

```yaml
services:
  app:
    environment:
      - TEST_DEFAULT_USERNAME=${TEST_DEFAULT_USERNAME}
      - TEST_DEFAULT_PASSWORD=${TEST_DEFAULT_PASSWORD}
      - TEST_DEFAULT_HOST=${TEST_DEFAULT_HOST:-localhost:8080}
```

Or use an `.env` file (which should also be gitignored):

```bash
TEST_DEFAULT_USERNAME=test@example.com
TEST_DEFAULT_PASSWORD=testpass
TEST_DEFAULT_HOST=localhost:8080
```

## Test Account Setup

### Development Environment

1. Create a test account in your development database using the seed data scripts
2. Use this account exclusively for testing
3. Never use production accounts for testing

### Seed Data

Test accounts should be created via seed data scripts (e.g., `data.sql` or `data-mysql.sql`). These scripts should:
- Only be used in development/test environments
- Never contain production credentials
- Be clearly documented as development-only

## Credential Rotation

If credentials were exposed:

1. **Immediately** change the password in the database:
   ```sql
   UPDATE employee SET password = '<new_hashed_password>' WHERE email = '<test_email>';
   ```

2. Update all test configuration files and CI/CD secrets

3. Notify all team members to update their local `.env.test` files

4. Review git history to ensure no other credentials were exposed

## Best Practices

1. ✅ Use separate test accounts for development/testing
2. ✅ Never commit `.env.test` to version control
3. ✅ Use environment variables in CI/CD pipelines
4. ✅ Rotate credentials if exposed
5. ✅ Document test account usage clearly
6. ❌ Never use production credentials for testing
7. ❌ Never hardcode credentials in source code
8. ❌ Never commit credentials to version control

## Troubleshooting

### "TEST_DEFAULT_USERNAME not set" Error

This means neither environment variables nor `.env.test` file are available. Solutions:

1. Set environment variables:
   ```bash
   export TEST_DEFAULT_USERNAME="test@example.com"
   export TEST_DEFAULT_PASSWORD="testpass"
   ```

2. Create `.env.test` file:
   ```bash
   cp .env.test.example .env.test
   # Edit .env.test with your credentials
   ```

3. Pass credentials as command-line arguments (for scripts that support it)

### Scripts Not Finding .env.test

Ensure:
- The `.env.test` file is in the project root directory
- The file has proper key=value format (no spaces around `=`)
- The file is readable by the user running the script

