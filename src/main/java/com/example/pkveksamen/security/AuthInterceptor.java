package com.example.pkveksamen.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String SESSION_AUTH_EMPLOYEE_ID = "AUTH_EMPLOYEE_ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        Integer sessionEmployeeId = session == null ? null : (Integer) session.getAttribute(SESSION_AUTH_EMPLOYEE_ID);
        if (sessionEmployeeId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        Integer requestedEmployeeId = getRequestedEmployeeId(request);
        if (requestedEmployeeId != null && !requestedEmployeeId.equals(sessionEmployeeId)) {
            response.sendRedirect(request.getContextPath() + "/project/list/" + sessionEmployeeId);
            return false;
        }

        return true;
    }

    private Integer getRequestedEmployeeId(HttpServletRequest request) {
        Object vars = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (vars instanceof Map<?, ?> map && map.get("employeeId") != null) {
            try {
                return Integer.valueOf(String.valueOf(map.get("employeeId")));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        String param = request.getParameter("employeeId");
        if (param != null && !param.isBlank()) {
            try {
                return Integer.valueOf(param);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }
}
