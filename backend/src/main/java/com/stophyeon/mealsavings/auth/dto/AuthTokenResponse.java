package com.stophyeon.mealsavings.auth.dto;

public record AuthTokenResponse(
        String tokenType,
        String accessToken,
        long expiresInSeconds,
        UserSummary user
) {
}
