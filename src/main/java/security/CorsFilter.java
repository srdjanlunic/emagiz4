package security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Filter to add Cross-Origin Resource Sharing (CORS) headers to HTTP responses.
 * <p>
 * This filter allows the API to be accessed from any origin and supports common HTTP methods
 * and headers for frontend integrations.
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {
    
    /**
     * Adds CORS headers to the HTTP response.
     *
     * @param requestContext  the context of the HTTP request
     * @param responseContext the context of the HTTP response
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        
        // Allow CORS for frontend integration
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add("Access-Control-Allow-Headers",
                                         "origin, content-type, accept, authorization");
        responseContext.getHeaders().add("Access-Control-Allow-Methods",
                                         "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().add("Access-Control-Max-Age", "1209600");
    }
}
