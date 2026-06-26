package com.kings.web.application.product;

import java.util.List;

public record ProductDeleteCommand(
        List<String> productIds
) {
}
