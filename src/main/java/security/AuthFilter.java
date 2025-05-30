package security;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

        // TODO: extract JWT token from Authorization header
        // TODO: validate token
        // TODO: set user context
        // TODO: continue filter chain or return unauthorized

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // filter cleanup
    }
}
