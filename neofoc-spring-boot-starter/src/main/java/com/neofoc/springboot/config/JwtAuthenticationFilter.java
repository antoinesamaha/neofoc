package com.neofoc.springboot.config;

import com.foc.rest.FocSimpleTokenAuth;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//ALERT
//@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private List<String> excludedUrls;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void initFilterBean() throws ServletException {
        String excludePattern = getFilterConfig().getInitParameter("excludeUrlPatterns");
        if (excludePattern != null && !excludePattern.isEmpty()) {
            this.excludedUrls = Arrays.asList(excludePattern.split(",")); // Split by comma or other delimiter
            logger.info("JWT Filter excluding URLs: {}", excludedUrls);
        } else {
            this.excludedUrls = List.of();
            logger.info("JWT Filter has no excluded URLs");
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        logger.debug("JWT Filter processing request for path: {}", path);

        if (shouldNotFilterApplicationURL(request)) {
            logger.debug("JWT Filter skipping path: {} (excluded)", path);
            filterChain.doFilter(request, response);
            return;
        }

        // Your JWT authentication logic here
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);

            FocSimpleTokenAuth simpeToken = new FocSimpleTokenAuth();
            if (simpeToken.verifyToken(jwtToken)) {
                logger.debug("JWT token verified for path: {}", path);
                filterChain.doFilter(request, response);
            } else {
                logger.warn("Invalid JWT token for path: {}", path);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            logger.warn("Missing or invalid Authorization header for path: {}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean shouldNotFilterApplicationURL(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean shouldNotFilter = excludedUrls.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));

        // Always exclude /foc/auth/login regardless of configured exclusions
        if (path.equals("/foc/auth/login") || path.startsWith("/foc/auth/login/")) {
            logger.debug("JWT Filter explicitly excluding login path: {}", path);
            return true;
        }

        return shouldNotFilter;
    }
}