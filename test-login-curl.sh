#!/bin/sh
# Login test script using curl (better cookie/session support)
# This script properly handles CSRF tokens and sessions
#
# Credentials are loaded from environment variables (TEST_DEFAULT_USERNAME, TEST_DEFAULT_PASSWORD, TEST_DEFAULT_HOST)
# or from a .env.test file in the project root (for development only).
#
# WARNING: Test credentials are for development/testing purposes only and should NEVER be used in production.
# If these credentials were ever exposed in version control, they should be rotated immediately.

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Load .env.test if it exists (simple key=value format)
if [ -f .env.test ]; then
  export $(grep -v '^#' .env.test | xargs)
fi

# Default values from environment variables or command line arguments
if [ -z "$1" ]; then
  USERNAME_OR_EMAIL="${TEST_DEFAULT_USERNAME}"
else
  USERNAME_OR_EMAIL="$1"
fi

if [ -z "$2" ]; then
  PASSWORD="${TEST_DEFAULT_PASSWORD}"
else
  PASSWORD="$2"
fi

if [ -z "$3" ]; then
  HOST="${TEST_DEFAULT_HOST:-localhost:8080}"
else
  HOST="$3"
fi

# Validate required credentials
if [ -z "$USERNAME_OR_EMAIL" ]; then
  echo "${RED}ERROR: TEST_DEFAULT_USERNAME not set. Please set environment variable TEST_DEFAULT_USERNAME or create a .env.test file.${NC}"
  exit 1
fi

if [ -z "$PASSWORD" ]; then
  echo "${RED}ERROR: TEST_DEFAULT_PASSWORD not set. Please set environment variable TEST_DEFAULT_PASSWORD or create a .env.test file.${NC}"
  exit 1
fi

COOKIE_JAR="/tmp/login_cookies.txt"

# Clean up on exit
cleanup() {
  rm -f "$COOKIE_JAR"
}
trap cleanup EXIT

# Handle password masking safely
PASSWORD_PREVIEW=$(echo "$PASSWORD" | cut -c1-3)

echo "${YELLOW}=== Login Test Script (using curl) ===${NC}"
echo "Username/Email: $USERNAME_OR_EMAIL"
echo "Password: ${PASSWORD_PREVIEW}***"
echo "Host: $HOST"
echo ""

# Check if curl is available
if ! command -v curl >/dev/null 2>&1; then
  echo "${RED}ERROR: curl is not available${NC}"
  echo "Please install curl or use the wget-based test script"
  exit 1
fi

# Step 1: Get login page to establish session and get CSRF token
echo "${YELLOW}Step 1: Fetching login page to get CSRF token...${NC}"
LOGIN_HTML=$(curl -s -c "$COOKIE_JAR" "http://${HOST}/login")

if [ $? -ne 0 ]; then
  echo "${RED}ERROR: Failed to fetch login page${NC}"
  exit 1
fi

# Extract CSRF token from HTML
CSRF_TOKEN=$(echo "$LOGIN_HTML" | grep -o 'name="_csrf"[^>]*value="[^"]*"' | sed 's/.*value="\([^"]*\)".*/\1/' | head -1)

if [ -z "$CSRF_TOKEN" ]; then
  echo "${YELLOW}Warning: Could not extract CSRF token from HTML${NC}"
  echo "Attempting login without CSRF token (may fail if CSRF protection is enabled)"
  CSRF_PARAM=""
else
  CSRF_PREVIEW=$(echo "$CSRF_TOKEN" | cut -c1-15)
  echo "${GREEN}✓ CSRF token extracted: ${CSRF_PREVIEW}...${NC}"
  CSRF_PARAM="_csrf=${CSRF_TOKEN}&"
fi

# Step 2: Test login with email/username
echo ""
echo "${YELLOW}Test 1: Login with email/username${NC}"

# Build POST data (URL encode special characters)
ENCODED_USERNAME=$(echo "$USERNAME_OR_EMAIL" | sed 's/+/%2B/g' | sed 's/@/%40/g' | sed 's/ /%20/g')
ENCODED_PASSWORD=$(echo "$PASSWORD" | sed 's/+/%2B/g' | sed 's/@/%40/g' | sed 's/ /%20/g')
POST_DATA="${CSRF_PARAM}username=${ENCODED_USERNAME}&password=${ENCODED_PASSWORD}"

# Perform login POST request (don't follow redirects to see the redirect response)
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\nREDIRECT:%{redirect_url}" -b "$COOKIE_JAR" -c "$COOKIE_JAR" \
  -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-raw "$POST_DATA" \
  "http://${HOST}/validate-login" 2>&1)

# Extract HTTP code and redirect URL
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | sed 's/.*HTTP_CODE://' | tr -d '\n')
REDIRECT_URL=$(echo "$RESPONSE" | grep "REDIRECT:" | sed 's/.*REDIRECT://' | tr -d '\n')
BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d' | sed '/REDIRECT:/d')

echo "HTTP Response Code: $HTTP_CODE"
if [ -n "$REDIRECT_URL" ] && [ "$REDIRECT_URL" != "http://${HOST}/validate-login" ]; then
  echo "Redirect URL: $REDIRECT_URL"
fi

# Check if login was successful
SUCCESS=false
if [ "$HTTP_CODE" = "200" ] && echo "$REDIRECT_URL" | grep -q "project/list"; then
  echo "${GREEN}✓ SUCCESS: Login successful! Redirected to project list.${NC}"
  SUCCESS=true
elif [ "$HTTP_CODE" = "302" ]; then
  if echo "$REDIRECT_URL" | grep -q "project/list"; then
    echo "${GREEN}✓ SUCCESS: Login successful! (302 redirect to project list)${NC}"
    SUCCESS=true
  else
    echo "${YELLOW}Got 302 redirect but not to project/list: $REDIRECT_URL${NC}"
  fi
elif echo "$BODY" | grep -qi "incorrect.*username.*password\|error"; then
  echo "${RED}✗ FAILED: Login unsuccessful - Incorrect username or password${NC}"
elif [ "$HTTP_CODE" = "403" ]; then
  echo "${RED}✗ FAILED: HTTP 403 - CSRF token validation failed${NC}"
  echo "This might indicate a session/cookie issue"
else
  echo "${RED}✗ FAILED: Login unsuccessful (HTTP $HTTP_CODE)${NC}"
  if [ -n "$BODY" ]; then
    echo "Response preview: $(echo "$BODY" | head -3 | tr '\n' ' ')"
  fi
fi

# Step 3: Test with invalid password
echo ""
echo "${YELLOW}Test 2: Login with invalid password${NC}"

# Get fresh CSRF token
LOGIN_HTML=$(curl -s -c "$COOKIE_JAR" "http://${HOST}/login")
CSRF_TOKEN=$(echo "$LOGIN_HTML" | grep -o 'name="_csrf"[^>]*value="[^"]*"' | sed 's/.*value="\([^"]*\)".*/\1/' | head -1)

if [ -n "$CSRF_TOKEN" ]; then
  CSRF_PARAM="_csrf=${CSRF_TOKEN}&"
else
  CSRF_PARAM=""
fi

POST_DATA="${CSRF_PARAM}username=${USERNAME_OR_EMAIL}&password=wrongpassword123"
RESPONSE=$(curl -s -w "\n%{http_code}" -b "$COOKIE_JAR" -c "$COOKIE_JAR" \
  -X POST \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-raw "$POST_DATA" \
  "http://${HOST}/validate-login" 2>&1)

HTTP_CODE=$(echo "$RESPONSE" | tail -1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" = "200" ] && echo "$BODY" | grep -qi "incorrect.*username.*password\|error"; then
  echo "${GREEN}✓ Invalid password correctly rejected${NC}"
else
  echo "${YELLOW}  HTTP Code: $HTTP_CODE (expected 200 with error message)${NC}"
fi

# Summary
echo ""
echo "${YELLOW}=== Test Summary ===${NC}"
if [ "$SUCCESS" = "true" ]; then
  echo "${GREEN}✓ Login functionality is working correctly${NC}"
  exit 0
else
  echo "${RED}✗ Login functionality has issues${NC}"
  exit 1
fi
