# Duplicate Email Error Investigation

## Issue Description
User reported seeing error message "Denne email er allerede i brug" (Danish) when attempting to create an employee with an email that already exists in the system.

## Current Implementation Analysis

### Error Flow
1. **Controller** (`EmployeeController.createEmployeePost()`):
   - Normalizes email: `email.trim().toLowerCase()` (line 47)
   - Validates password complexity
   - Calls `employeeService.createEmployee()`
   - Maps `CreateEmployeeResult` to error messages (lines 73-77)
   - **Current message**: "That email is already in use" (English)

2. **Service** (`EmployeeService.createEmployee()`):
   - Pre-check: `existsByEmail(email)` (line 28)
   - Attempts INSERT via repository
   - Catches `DataIntegrityViolationException` and re-checks (lines 36-44)
   - Returns `CreateEmployeeResult.EMAIL_ALREADY_IN_USE`

3. **Repository** (`EmployeeRepository`):
   - `existsByEmail()`: Uses `LOWER(email) = LOWER(?)` (line 77) - case-insensitive
   - `createEmployee()`: Direct INSERT with email parameter
   - Database constraint: `email VARCHAR(100) NOT NULL UNIQUE`

4. **Template** (`create-employee.html`):
   - Displays error: `<p th:if="${error}" th:text="${error}" class="error-message"></p>` (line 39)
   - Preserves form data: `model.addAttribute("employee", employee)` (line 79)

## Findings

### ✅ Positive Aspects
1. **Defensive Programming**: Double-check pattern (pre-check + exception handling)
2. **Case-Insensitive Check**: Both controller and repository normalize email
3. **Form Data Preservation**: User input is preserved when error occurs
4. **Proper Error Display**: Error message is styled and visible

### ⚠️ Issues Identified

#### 1. Language Inconsistency
**Severity**: LOW
- **Screenshot shows**: "Denne email er allerede i brug" (Danish)
- **Code shows**: "That email is already in use" (English)
- **Possible causes**:
  - Code was updated after screenshot
  - Different version/branch
  - Translation mechanism not visible in current codebase

**Location**: `EmployeeController.java:74`

#### 2. Potential Race Condition
**Severity**: MEDIUM
- **Issue**: Between `existsByEmail()` check (line 28) and `createEmployee()` INSERT (line 33), another concurrent request could insert the same email
- **Current Mitigation**: `DataIntegrityViolationException` catch block re-checks (lines 36-44)
- **Risk**: Low - database UNIQUE constraint provides final protection
- **Impact**: User might see generic error instead of specific "email in use" message in rare cases

**Location**: `EmployeeService.java:24-44`

#### 3. Redundant Email Normalization
**Severity**: LOW
- **Issue**: Email is lowercased in controller (line 47) AND repository uses `LOWER()` function (line 77)
- **Impact**: Minor performance overhead, but safe
- **Recommendation**: Choose one normalization point (prefer controller)

**Locations**:
- `EmployeeController.java:47`
- `EmployeeRepository.java:77`

#### 4. Error Message Not Field-Specific
**Severity**: MEDIUM
- **Issue**: Error message appears below form, not next to email field
- **Impact**: Less clear which field has the error
- **UX Issue**: User must scan to find which field caused the error

**Location**: `create-employee.html:39`

#### 5. No Client-Side Validation
**Severity**: MEDIUM
- **Issue**: No real-time feedback before form submission
- **Impact**: User must submit form to discover duplicate email
- **UX Issue**: Slower feedback, requires server round-trip

**Location**: `create-employee.html`

## Code Flow Diagram

```
User submits form
    ↓
EmployeeController.createEmployeePost()
    ↓
Email normalized: email.trim().toLowerCase()
    ↓
Password validation
    ↓
EmployeeService.createEmployee()
    ↓
[Pre-check] existsByEmail(email) → true/false
    ↓ (if false)
Repository.createEmployee() → INSERT
    ↓ (if UNIQUE constraint violation)
DataIntegrityViolationException caught
    ↓
[Re-check] existsByEmail(email) → true
    ↓
Return EMAIL_ALREADY_IN_USE
    ↓
Controller maps to error message
    ↓
Display error in template
```

## Database Schema
```sql
CREATE TABLE employee (
    employee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,  -- UNIQUE constraint prevents duplicates
    ...
);
```

## Recommendations

### High Priority
1. **Add Field-Specific Error Display**
   - Show error message next to email input field
   - Use `aria-describedby` for accessibility
   - Highlight email field with error styling

2. **Add Client-Side Validation**
   - Real-time email availability check (AJAX)
   - Immediate feedback before form submission
   - Better UX

### Medium Priority
3. **Standardize Language**
   - Decide on English or Danish
   - Use consistent language throughout
   - Consider i18n if both languages needed

4. **Improve Race Condition Handling**
   - Add database-level transaction isolation
   - Consider optimistic locking
   - Ensure error message accuracy even in race conditions

### Low Priority
5. **Optimize Email Normalization**
   - Remove redundant `LOWER()` in repository if controller already normalizes
   - Document normalization strategy

6. **Add Logging**
   - Log duplicate email attempts for security monitoring
   - Track frequency of this error

## Testing Recommendations

1. **Test Duplicate Email**:
   - Create employee with email "test@example.com"
   - Attempt to create another with same email
   - Verify error message displays correctly

2. **Test Case Sensitivity**:
   - Create with "Test@Example.com"
   - Attempt with "test@example.com"
   - Should be detected as duplicate

3. **Test Race Condition**:
   - Simulate concurrent requests with same email
   - Verify one succeeds, one fails gracefully

4. **Test Form Data Preservation**:
   - Submit form with duplicate email
   - Verify all fields (except password) are preserved

## Related Code Review Findings

This issue relates to findings in:
- `08-ui-issues.md`: Missing form validation feedback, inconsistent error messages
- `02-code-quality-issues.md`: Code duplication in validation logic
- `03-security-concerns.md`: General validation and error handling

## Conclusion

The duplicate email detection **works correctly** from a functional perspective:
- ✅ Database constraint prevents duplicates
- ✅ Service layer checks before insert
- ✅ Exception handling provides fallback
- ✅ Error message is displayed to user

**Main Issues**:
1. Language inconsistency (Danish vs English)
2. Error message placement (not field-specific)
3. No client-side validation (requires server round-trip)
4. Minor race condition risk (mitigated by database constraint)

The system is **functionally correct** but could benefit from UX improvements.
