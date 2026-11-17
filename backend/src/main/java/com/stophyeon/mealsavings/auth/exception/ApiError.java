package com.stophyeon.mealsavings.auth.exception;

import java.time.OffsetDateTime;
import java.util.Map;

public record ApiError(
        String code,
        String message,
        Map<String, String> details,
        OffsetDateTime timestamp
) {
}
