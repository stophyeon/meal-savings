package com.stophyeon.mealsavings.auth.repository;

import com.stophyeon.mealsavings.auth.domain.AuthIdentity;
import com.stophyeon.mealsavings.auth.domain.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthIdentityRepository extends JpaRepository<AuthIdentity, Long> {

    Optional<AuthIdentity> findByProviderAndProviderSubject(AuthProvider provider, String providerSubject);

    boolean existsByProviderAndProviderSubject(AuthProvider provider, String providerSubject);
}
