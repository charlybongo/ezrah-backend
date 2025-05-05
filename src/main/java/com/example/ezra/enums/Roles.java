package com.example.ezra.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

@Getter
public enum Roles {
    ADMIN(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
            new SimpleGrantedAuthority("ADMIN_PRIVILEGE"))),
    CUSTOMER(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

    private final List<GrantedAuthority> authorities;

    Roles(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    // Helper method if you need the simple role name
    public String getRoleName() {
        return "ROLE_" + name();
    }
}