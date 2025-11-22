package com.hr_manager.hr_service.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
public class UserPrincipal {
    private UUID userId;
    private String email;
    private String role;
    private String name;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

    public boolean isManager() {
        return "manager".equalsIgnoreCase(role);
    }
}

