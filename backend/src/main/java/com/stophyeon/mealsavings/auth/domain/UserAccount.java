package com.stophyeon.mealsavings.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(
        name = "APP_USER_ACCOUNT",
        uniqueConstraints = @UniqueConstraint(name = "UQ_APP_USER_ACCOUNT_EMAIL", columnNames = "EMAIL")
)
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false, precision = 19)
    private Long id;

    @Column(name = "DISPLAY_NAME", nullable = false, length = 100)
    private String displayName;

    @Column(name = "EMAIL", length = 320)
    private String email;

    @Column(name = "PASSWORD_HASH", length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "CREATED_AT", nullable = false, columnDefinition = "TIMESTAMP(6) WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false, columnDefinition = "TIMESTAMP(6) WITH TIME ZONE")
    private OffsetDateTime updatedAt;

    protected UserAccount() {
    }

    public UserAccount(String displayName, String email, String passwordHash) {
        this.displayName = displayName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = UserStatus.ACTIVE;
    }

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserStatus getStatus() {
        return status;
    }
}
