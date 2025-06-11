package security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // filter initialization
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Add CORS headers
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Handle preflight requests
        if ("OPTIONS".equals(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Skip authentication for login and public endpoints
        String path = httpRequest.getRequestURI();
        if (path.endsWith("/auth/login") || path.endsWith("/health") || path.endsWith("/auth/demo-login")) {
            chain.doFilter(request, response);
            return;
        }

        // Extract JWT token from Authorization header
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if (JWTUtil.validateToken(token)) {
                    // Token is valid, continue with request
                    chain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                // Token validation failed
            }
        }

        // No valid token, return unauthorized
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.setContentType("application/json");
        httpResponse.getWriter().write("{\"error\":\"Unauthorized - Invalid or missing token\"}");
    }

    @Override
    public void destroy() {
        // filter cleanup
    }
}
