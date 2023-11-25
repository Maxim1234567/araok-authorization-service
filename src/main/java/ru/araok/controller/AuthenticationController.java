package ru.araok.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.araok.dto.JwtRequest;
import ru.araok.dto.JwtResponse;
import ru.araok.dto.RefreshJwtRequest;
import ru.araok.dto.UserDto;
import ru.araok.service.AuthService;
import ru.araok.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthService authService;

    private final UserService userService;

    @PostMapping("/auth/registration")
    public ResponseEntity<JwtResponse> registration(@RequestBody UserDto user) {
        userService.save(user);

        final JwtResponse token = authService.login(JwtRequest.builder()
                .phone(user.getPhone())
                .password(user.getPassword())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) {
        log.info("/auth/login");

        final JwtResponse token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/auth/token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        log.info("/auth/token");

        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) {
        log.info("/auth/refresh");

        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
