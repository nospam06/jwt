package org.example.jwt.endpoint.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.jwt.logic.api.UserService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION = "AUTHORIZATION";
    private static final String BEARER = "bearer";
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authorization = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(authorization)) {
            String[] parts = authorization.split(" ");
            if (parts[0].equalsIgnoreCase(BEARER)) {
                userService.validateToken(parts[1]);
            }
        }
        chain.doFilter(request, response);
    }
}
