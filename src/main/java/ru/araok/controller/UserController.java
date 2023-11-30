package ru.araok.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.araok.dto.UserDto;
import ru.araok.service.impl.UserAuthentication;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserAuthentication userAuthentication;

    @GetMapping("/auth/user")
    public ResponseEntity<UserDto> getUserById() {
        log.info("/auth/user");

        return ResponseEntity.ok(
                UserDto.builder()
                        .id(userAuthentication.getId())
                        .name(userAuthentication.getName())
                        .birthDate(userAuthentication.getBirthDate())
                        .phone(userAuthentication.getPhone())
                        .password(userAuthentication.getPassword())
                        .role(userAuthentication.getRole())
                        .build()
        );
    }
}
