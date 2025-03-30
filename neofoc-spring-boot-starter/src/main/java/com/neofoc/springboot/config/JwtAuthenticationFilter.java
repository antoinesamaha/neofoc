package com.neofoc.springboot.config;

import com.foc.rest.FocSimpleTokenAuth;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private List<String> excludedUrls;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void initFilterBean() throws ServletException {
        String excludePattern = getFilterConfig().getInitParameter("excludeUrlPatterns");
        if (excludePattern != null && !excludePattern.isEmpty()) {
            this.excludedUrls = Arrays.asList(excludePattern.split(",")); // Split by comma or other delimiter
        } else {
            this.excludedUrls = List.of();
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (shouldNotFilterApplicationURL(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Your JWT authentication logic here
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);

            FocSimpleTokenAuth simpeToken = new FocSimpleTokenAuth();
            if (simpeToken.verifyToken(jwtToken)) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean shouldNotFilterApplicationURL(HttpServletRequest request) {
        return excludedUrls.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, request.getServletPath()));
    }
}