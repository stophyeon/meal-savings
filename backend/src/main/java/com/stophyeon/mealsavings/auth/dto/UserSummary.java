package com.stophyeon.mealsavings.auth.dto;

public record UserSummary(
        Long id,
        String name,
        String email
) {
}
