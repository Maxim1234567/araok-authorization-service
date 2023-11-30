package ru.araok.enums;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum RoleEnum implements GrantedAuthority {
    USER("USER"), ADMIN("ADMIN");

    final String role;

    @Override
    public String getAuthority() {
        return role;
    }
}
