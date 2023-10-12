package org.example.jwt.endpoint.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.jwt.logic.api.UserService;
import org.example.jwt.security.SecurityTokenException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SecurityFilter implements Filter {
    public static final String AUTHORIZATION = "AUTHORIZATION";
    public static final String TOKEN_PATH = "/api/jwt/token";
    public static final Set<String> ROOT = Set.of("/", "/favicon.ico");
    private final UserService userService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            String path = httpRequest.getRequestURI();
            Object authorization = httpRequest.getHeader(AUTHORIZATION);
            if (!ROOT.contains(path) && authorization == null && !path.startsWith(TOKEN_PATH)) {
                throw new SecurityTokenException("need security token to access secured path", null);
            }
            if (authorization != null) {
                userService.validateToken(authorization.toString().replace("bearer ", ""));
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
