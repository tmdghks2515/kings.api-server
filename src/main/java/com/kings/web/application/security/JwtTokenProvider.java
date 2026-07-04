package com.kings.web.application.security;

import com.kings.web.domain.user.Role;
import com.kings.web.domain.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    void initialize() {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        var now = Instant.now();
        var expiresAt = now.plusSeconds(jwtProperties.accessTokenExpirationSeconds());

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    public long getAccessTokenExpirationSeconds() {
        return jwtProperties.accessTokenExpirationSeconds();
    }

    public AuthPrincipal parse(String token) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            var rolesClaim = claims.get("roles");
            var roles = Set.<Role>of();

            if (rolesClaim instanceof String[] roleArray) {
                roles = Arrays.stream(roleArray)
                        .map(Role::valueOf)
                        .collect(Collectors.toSet());
            } else if (rolesClaim instanceof Iterable<?> roleIterable) {
                roles = toRoles(roleIterable);
            }

            return new AuthPrincipal(claims.getSubject(), roles);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new IllegalArgumentException("인증 정보가 올바르지 않습니다.", exception);
        }
    }

    private Set<Role> toRoles(Iterable<?> roleIterable) {
        return java.util.stream.StreamSupport.stream(roleIterable.spliterator(), false)
                .map(String::valueOf)
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}
