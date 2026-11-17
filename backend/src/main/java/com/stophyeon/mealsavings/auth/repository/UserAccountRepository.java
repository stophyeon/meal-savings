package com.stophyeon.mealsavings.auth.repository;

import com.stophyeon.mealsavings.auth.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    boolean existsByEmail(String email);
}
