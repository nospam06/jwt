package org.example.jwt.endpoint.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.jwt.security.SecurityTokenException;
import org.example.jwt.security.api.TokenService;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter implements Filter {
    public static final String AUTHORIZATION = "AUTHORIZATION";
    public static final String TOKEN_PATH = "/api/jwt/token";
    private final TokenService tokenService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            String path = httpRequest.getRequestURI();
            Object authorization = httpRequest.getHeader(AUTHORIZATION);
            if (authorization == null && !path.startsWith(TOKEN_PATH)) {
                throw new SecurityTokenException("need security token to access secured path", null);
            }
            if (authorization != null) {
                tokenService.verifyToken(authorization.toString().replace("bearer ", ""));
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
