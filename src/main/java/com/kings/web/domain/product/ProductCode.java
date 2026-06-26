package com.kings.web.domain.product;

import java.util.regex.Pattern;

public final class ProductCode {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");

    private ProductCode() {
    }

    public static boolean isValid(String code) {
        return code != null && VALID_PATTERN.matcher(code).matches();
    }
}
