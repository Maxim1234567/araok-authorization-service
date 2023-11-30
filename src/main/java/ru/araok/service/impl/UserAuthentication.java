package ru.araok.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.araok.dto.UserDto;
import ru.araok.enums.RoleEnum;

import java.time.LocalDate;

@Component
public class UserAuthentication {

    public Long getId() {
        return user().getId();
    }

    public String getName() {
        return user().getName();
    }

    public String getPhone() {
        return user().getPhone();
    }

    public String getPassword() {
        return user().getPassword();
    }

    public LocalDate getBirthDate() {
        return user().getBirthDate();
    }

    public RoleEnum getRole() {
        return user().getRole();
    }

    private UserDto user() {
        return (UserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}