package com.stophyeon.mealsavings.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(
        name = "APP_AUTH_IDENTITY",
        uniqueConstraints = @UniqueConstraint(
                name = "UQ_APP_AUTH_PROVIDER_SUBJECT",
                columnNames = {"PROVIDER", "PROVIDER_SUBJECT"}
        )
)
public class AuthIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDENTITY_ID", nullable = false, precision = 19)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserAccount userAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROVIDER", nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "PROVIDER_SUBJECT", nullable = false, length = 255)
    private String providerSubject;

    @Column(name = "CREATED_AT", nullable = false, columnDefinition = "TIMESTAMP(6) WITH TIME ZONE")
    private OffsetDateTime createdAt;

    protected AuthIdentity() {
    }

    public AuthIdentity(UserAccount userAccount, AuthProvider provider, String providerSubject) {
        this.userAccount = userAccount;
        this.provider = provider;
        this.providerSubject = providerSubject;
    }

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public Long getId() {
        return id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public String getProviderSubject() {
        return providerSubject;
    }
}
