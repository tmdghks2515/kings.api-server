package com.kings.web.application.user;

import java.util.List;

public record UserDeleteCommand(
        List<String> usernames
) {
}
