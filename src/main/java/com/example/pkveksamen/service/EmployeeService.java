package com.example.pkveksamen.service;

import com.example.pkveksamen.model.Employee;
import com.example.pkveksamen.repository.EmployeeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public boolean createEmployee(String username, String password, String email, String role) {
        try {
            employeeRepository.createEmployee(username, password, email, role);
            System.out.println("Bruger oprettet: " + username + " " + email);
            return true;
        } catch (DataIntegrityViolationException e) {
            System.out.println("Email allerede i brug: " + email);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.out.println("Uventet fejl ved oprettelse af bruger: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Integer validateLogin(String username, String password) {
        return employeeRepository.validateLogin(username, password);
    }

    public Employee getEmployeeById(int employeeId) {
        return employeeRepository.findEmployeeById(employeeId);
    }

    // Tilføj til EmployeeService.java
    public List<Employee> getAllTeamMembers() {
        return employeeRepository.getAllTeamMembers();
    }
}
