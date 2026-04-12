package com.loanguard.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "auth_provider")
    private String authProvider;

    @Column(name = "provider_subject")
    private String providerSubject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    private String phone;

    @Column(length = 500)
    private String address;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public User() {}

    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email    = email;
        this.password = password;
        this.role     = UserRole.USER;
    }

    public User(String fullName, String email, String password, UserRole role) {
        this.fullName = fullName;
        this.email    = email;
        this.password = password;
        this.role     = role != null ? role : UserRole.USER;
    }

    // Getters & Setters
    public Long getId()                            { return id; }
    public void setId(Long id)                     { this.id = id; }
    public String getFullName()                    { return fullName; }
    public void setFullName(String v)              { this.fullName = v; }
    public String getEmail()                       { return email; }
    public void setEmail(String v)                 { this.email = v; }
    public String getPassword()                    { return password; }
    public void setPassword(String v)              { this.password = v; }
    public String getAuthProvider()                { return authProvider; }
    public void setAuthProvider(String v)           { this.authProvider = v; }
    public String getProviderSubject()             { return providerSubject; }
    public void setProviderSubject(String v)        { this.providerSubject = v; }
    public UserRole getRole()                      { return role; }
    public void setRole(UserRole v)                { this.role = v; }
    public String getPhone()                       { return phone; }
    public void setPhone(String v)                 { this.phone = v; }
    public String getAddress()                     { return address; }
    public void setAddress(String v)               { this.address = v; }
    public Boolean getIsActive()                   { return isActive; }
    public void setIsActive(Boolean v)             { this.isActive = v; }
    public LocalDateTime getLastLogin()            { return lastLogin; }
    public void setLastLogin(LocalDateTime v)      { this.lastLogin = v; }
    public LocalDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(LocalDateTime v)      { this.createdAt = v; }
    public LocalDateTime getUpdatedAt()            { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v)      { this.updatedAt = v; }
}