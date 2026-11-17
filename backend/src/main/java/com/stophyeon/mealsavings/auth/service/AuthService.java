package com.stophyeon.mealsavings.auth.service;

import com.stophyeon.mealsavings.auth.domain.AuthIdentity;
import com.stophyeon.mealsavings.auth.domain.AuthProvider;
import com.stophyeon.mealsavings.auth.domain.UserAccount;
import com.stophyeon.mealsavings.auth.domain.UserStatus;
import com.stophyeon.mealsavings.auth.dto.AuthTokenResponse;
import com.stophyeon.mealsavings.auth.dto.LoginRequest;
import com.stophyeon.mealsavings.auth.dto.SignupRequest;
import com.stophyeon.mealsavings.auth.dto.UserSummary;
import com.stophyeon.mealsavings.auth.exception.DuplicateEmailException;
import com.stophyeon.mealsavings.auth.exception.InvalidCredentialsException;
import com.stophyeon.mealsavings.auth.repository.AuthIdentityRepository;
import com.stophyeon.mealsavings.auth.repository.UserAccountRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {

    private static final int BCRYPT_MAX_PASSWORD_BYTES = 72;

    private final UserAccountRepository userAccountRepository;
    private final AuthIdentityRepository authIdentityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final String dummyPasswordHash;

    public AuthService(
            UserAccountRepository userAccountRepository,
            AuthIdentityRepository authIdentityRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.authIdentityRepository = authIdentityRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.dummyPasswordHash = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    @Transactional
    public AuthTokenResponse signup(SignupRequest request) {
        String email = normalizeEmail(request.email());
        validateBcryptPassword(request.password());

        if (userAccountRepository.existsByEmail(email)
                || authIdentityRepository.existsByProviderAndProviderSubject(AuthProvider.LOCAL, email)) {
            throw new DuplicateEmailException();
        }

        UserAccount account = userAccountRepository.saveAndFlush(
                new UserAccount(request.name().trim(), email, passwordEncoder.encode(request.password()))
        );
        try {
            authIdentityRepository.saveAndFlush(new AuthIdentity(account, AuthProvider.LOCAL, email));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEmailException();
        }

        return jwtTokenService.issueFor(account, AuthProvider.LOCAL);
    }

    @Transactional(readOnly = true)
    public AuthTokenResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        validateBcryptPassword(request.password());

        AuthIdentity identity = authIdentityRepository
                .findByProviderAndProviderSubject(AuthProvider.LOCAL, email)
                .orElse(null);
        if (identity == null) {
            passwordEncoder.matches(request.password(), dummyPasswordHash);
            throw new InvalidCredentialsException();
        }
        UserAccount account = identity.getUserAccount();
        String passwordHash = account.getPasswordHash();
        boolean passwordMatches = passwordHash == null
                ? passwordEncoder.matches(request.password(), dummyPasswordHash)
                : passwordEncoder.matches(request.password(), passwordHash);

        if (account.getStatus() != UserStatus.ACTIVE
                || !passwordMatches) {
            throw new InvalidCredentialsException();
        }

        return jwtTokenService.issueFor(account, AuthProvider.LOCAL);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void validateBcryptPassword(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_PASSWORD_BYTES) {
            throw new IllegalArgumentException("비밀번호는 UTF-8 기준 72바이트 이하여야 합니다.");
        }
    }
}
