package com.stophyeon.mealsavings.auth.service;

import com.stophyeon.mealsavings.auth.domain.AuthProvider;
import com.stophyeon.mealsavings.auth.domain.UserAccount;
import com.stophyeon.mealsavings.auth.dto.AuthTokenResponse;
import com.stophyeon.mealsavings.auth.dto.UserSummary;
import com.stophyeon.mealsavings.config.JwtProperties;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public JwtTokenService(JwtEncoder jwtEncoder, JwtProperties jwtProperties) {
        if (jwtProperties.accessTokenTtl().isNegative() || jwtProperties.accessTokenTtl().isZero()) {
            throw new IllegalStateException("JWT access-token-ttl must be a positive duration.");
        }
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    public AuthTokenResponse issueFor(UserAccount account, AuthProvider provider) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.accessTokenTtl());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(account.getId().toString())
                .claim("email", account.getEmail())
                .claim("name", account.getDisplayName())
                .claim("authorities", List.of("ROLE_USER"))
                .claim("auth_provider", provider.name())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .type("JWT")
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new AuthTokenResponse(
                "Bearer",
                token,
                jwtProperties.accessTokenTtl().toSeconds(),
                new UserSummary(account.getId(), account.getDisplayName(), account.getEmail())
        );
    }
}
