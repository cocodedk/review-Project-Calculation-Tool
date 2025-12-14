package com.example.pkveksamen.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CsrfInterceptor implements HandlerInterceptor {

    private static final String SESSION_CSRF_TOKEN = "CSRF_TOKEN";
    private static final String CSRF_PARAM = "_csrf";

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(true);

        String token = (String) session.getAttribute(SESSION_CSRF_TOKEN);
        if (!StringUtils.hasText(token)) {
            token = newToken();
            session.setAttribute(SESSION_CSRF_TOKEN, token);
        }

        request.setAttribute("csrfParameterName", CSRF_PARAM);
        request.setAttribute("csrfToken", token);

        if (isStateChanging(request.getMethod())) {
            String provided = request.getParameter(CSRF_PARAM);
            if (!StringUtils.hasText(provided) || !constantTimeEquals(token, provided)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token missing or invalid");
                return false;
            }
        }

        return true;
    }

    private boolean isStateChanging(String method) {
        return "POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method);
    }

    private String newToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
