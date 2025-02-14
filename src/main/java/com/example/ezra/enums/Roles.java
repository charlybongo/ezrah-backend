package com.example.ezra.enums;

import org.springframework.security.core.GrantedAuthority;
import java.util.List;

public enum Roles implements GrantedAuthority {
    ADMIN,
    MEMBER,
    CHURCH,
    SUPER_ADMIN;

    @Override
    public String getAuthority() {
        return name();  // ✅ Returns "ADMIN", "MEMBER", etc.
    }

    public List<GrantedAuthority> getAuthorities() {
        return List.of(this);  // ✅ Returns itself as a single authority
    }
}
