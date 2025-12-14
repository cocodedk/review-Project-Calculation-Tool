package com.example.pkveksamen.web;

import com.example.pkveksamen.model.Employee;
import com.example.pkveksamen.security.AuthInterceptor;
import com.example.pkveksamen.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class GlobalModelAttributes {

    private final EmployeeService employeeService;

    public GlobalModelAttributes(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @ModelAttribute
    public void addCurrentEmployee(Model model, HttpSession session) {
        Integer employeeId = (Integer) session.getAttribute(AuthInterceptor.SESSION_AUTH_EMPLOYEE_ID);
        if (employeeId != null) {
            model.addAttribute("currentEmployeeId", employeeId);
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee != null) {
                model.addAttribute("username", employee.getUsername());
                model.addAttribute("employeeRole", employee.getRole());
            }
        }
    }
}

