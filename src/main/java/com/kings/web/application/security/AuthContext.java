package com.kings.web.application.security;

import java.util.Optional;

public final class AuthContext {

    private static final ThreadLocal<AuthPrincipal> AUTH_PRINCIPAL_HOLDER = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(AuthPrincipal authPrincipal) {
        AUTH_PRINCIPAL_HOLDER.set(authPrincipal);
    }

    public static Optional<AuthPrincipal> get() {
        return Optional.ofNullable(AUTH_PRINCIPAL_HOLDER.get());
    }

    public static Optional<String> getUsername() {
        return get().map(AuthPrincipal::username);
    }

    public static void clear() {
        AUTH_PRINCIPAL_HOLDER.remove();
    }
}
