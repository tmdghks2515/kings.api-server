package com.kings.web.infra.web;

import com.kings.web.application.security.AuthContext;
import com.kings.web.application.security.AuthPrincipal;
import com.kings.web.application.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthContextFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String API_PATH_PREFIX = "/api/";
    private static final String[] NO_AUTHENTICATION_PATHS = {
            "/api/auth/login",
            "/api/users/signup"
    };
    private static final String[] NO_AUTHENTICATION_PATH_PREFIXES = {
            "/api/public/"
    };

    private final JwtTokenProvider jwtTokenProvider;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            try {
                var authPrincipal = resolveAuthPrincipal(request);

                if (requiresAuthentication(request) && authPrincipal.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
                    return;
                }

                authPrincipal.ifPresent(AuthContext::set);
            } catch (IllegalArgumentException exception) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 정보가 올바르지 않습니다.");
                return;
            }

            filterChain.doFilter(request, response);
        } finally {
            AuthContext.clear();
        }
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        var requestUri = request.getRequestURI();

        if (!requestUri.startsWith(API_PATH_PREFIX)
                || Arrays.stream(NO_AUTHENTICATION_PATHS).anyMatch(requestUri::equals)
                || Arrays.stream(NO_AUTHENTICATION_PATH_PREFIXES).anyMatch(requestUri::startsWith)
                || HttpMethod.OPTIONS.matches(request.getMethod())) {
            return false;
        }

        return !hasNoAuthenticationAnnotation(request);
    }

    private boolean hasNoAuthenticationAnnotation(HttpServletRequest request) {
        try {
            var handler = requestMappingHandlerMapping.getHandler(request);
            if (handler == null || !(handler.getHandler() instanceof HandlerMethod handlerMethod)) {
                return false;
            }

            return AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(), NoAuthentication.class)
                    || AnnotatedElementUtils.hasAnnotation(handlerMethod.getBeanType(), NoAuthentication.class);
        } catch (Exception exception) {
            return false;
        }
    }

    private Optional<AuthPrincipal> resolveAuthPrincipal(HttpServletRequest request) {
        var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return java.util.Optional.empty();
        }

        var token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            return java.util.Optional.empty();
        }

        return java.util.Optional.of(jwtTokenProvider.parse(token));
    }
}
