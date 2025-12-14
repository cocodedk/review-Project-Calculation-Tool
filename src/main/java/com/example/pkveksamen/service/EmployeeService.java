package com.example.pkveksamen.service;

import com.example.pkveksamen.model.Employee;
import com.example.pkveksamen.repository.EmployeeRepository;
import com.example.pkveksamen.util.EmailLoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public CreateEmployeeResult createEmployee(String username, String password, String email, String role, String alphaRoleDisplayName) {
        if (employeeRepository.existsByUsername(username)) {
            return CreateEmployeeResult.USERNAME_ALREADY_IN_USE;
        }
        if (employeeRepository.existsByEmail(email)) {
            return CreateEmployeeResult.EMAIL_ALREADY_IN_USE;
        }

        try {
            employeeRepository.createEmployee(username, password, email, role, alphaRoleDisplayName);
            String logSafeEmailId = EmailLoggingUtil.createLogSafeEmailIdentifier(username, email);
            logger.info("Employee created: username={} emailId={} alphaRole={}", username, logSafeEmailId, alphaRoleDisplayName);
            return CreateEmployeeResult.SUCCESS;
        } catch (DataIntegrityViolationException e) {
            String logSafeEmailId = EmailLoggingUtil.createLogSafeEmailIdentifier(username, email);
            logger.warn("Employee create failed (data integrity violation): username={} emailId={}", username, logSafeEmailId, e);
            if (employeeRepository.existsByUsername(username)) {
                return CreateEmployeeResult.USERNAME_ALREADY_IN_USE;
            }
            if (employeeRepository.existsByEmail(email)) {
                return CreateEmployeeResult.EMAIL_ALREADY_IN_USE;
            }
            return CreateEmployeeResult.UNKNOWN_ERROR;
        } catch (Exception e) {
            String logSafeEmailId = EmailLoggingUtil.createLogSafeEmailIdentifier(username, email);
            logger.error("Unexpected error creating employee: username={} emailId={}", username, logSafeEmailId, e);
            return CreateEmployeeResult.UNKNOWN_ERROR;
        }
    }

    public Integer validateLogin(String username, String password) {
        return employeeRepository.validateLogin(username, password);
    }

    public Employee getEmployeeById(int employeeId) {
        return employeeRepository.findEmployeeById(employeeId);
    }

    public List<Employee> getAllTeamMembers() {
        return employeeRepository.getAllTeamMembers();
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.getAllEmployees();
    }
}
