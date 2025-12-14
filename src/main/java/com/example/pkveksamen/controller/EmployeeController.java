package com.example.pkveksamen.controller;

import com.example.pkveksamen.model.AlphaRole;
import com.example.pkveksamen.model.Employee;
import com.example.pkveksamen.model.EmployeeRole;
import com.example.pkveksamen.security.AuthInterceptor;
import com.example.pkveksamen.service.CreateEmployeeResult;
import com.example.pkveksamen.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@Controller
@RequestMapping("/")
public class EmployeeController {

    private final EmployeeService employeeService;
    private static final Pattern PASSWORD_COMPLEXITY = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/")
    public String homepage() {
        return "homepage";
    }

    @GetMapping("login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("/create-employee")
    public String createEmployee(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("skills", AlphaRole.values());
        return "create-employee";
    }

    @PostMapping("/create-employee")
    public String createEmployeePost(Employee employee, Model model) {
        String username = employee.getUsername() == null ? null : employee.getUsername().trim();
        String email = employee.getEmail() == null ? null : employee.getEmail().trim().toLowerCase();
        String password = employee.getPassword();

        if (employee.getSkill() == null) {
            model.addAttribute("error", "Please select an Alpha Role");
            model.addAttribute("employee", employee);
            model.addAttribute("skills", AlphaRole.values());
            return "create-employee";
        }

        if (password == null || !PASSWORD_COMPLEXITY.matcher(password).matches()) {
            model.addAttribute("error", "Password must be at least 8 characters and include upper/lower case letters and a number");
            model.addAttribute("employee", employee);
            model.addAttribute("skills", AlphaRole.values());
            return "create-employee";
        }
        
        CreateEmployeeResult result = employeeService.createEmployee(
                username,
                password,
                email,
                EmployeeRole.TEAM_MEMBER.getDisplayName(),
                employee.getSkill().getDisplayName()
        );

        if (result != CreateEmployeeResult.SUCCESS) {
            String message = switch (result) {
                case EMAIL_ALREADY_IN_USE -> "That email is already in use";
                case USERNAME_ALREADY_IN_USE -> "That username is already in use";
                default -> "Unable to create employee. Please try again.";
            };
            model.addAttribute("error", message);
            model.addAttribute("employee", employee);
            model.addAttribute("skills", AlphaRole.values());
            return "create-employee";
        }

        return "redirect:/login";
    }

    @PostMapping("/validate-login")
    public String validateLogin(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                HttpSession session,
                                Model model) {
        Integer id = employeeService.validateLogin(username, password);

        if (id != null && id > 0) {
            session.setAttribute(AuthInterceptor.SESSION_AUTH_EMPLOYEE_ID, id);
            return "redirect:/project/list/" + id;
        } else {
            model.addAttribute("error", "Incorrect username or password. Please try again.");
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
