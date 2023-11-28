package ru.araok.service;

import ru.araok.domain.JwtAuthentication;
import ru.araok.dto.JwtRequest;
import ru.araok.dto.JwtResponse;
import ru.araok.dto.UserDto;
import ru.araok.dto.UserWithJwtResponse;

public interface AuthService {
    UserWithJwtResponse saveAndGenerateToken(UserDto user);

    JwtResponse login(JwtRequest authRequest);

    JwtResponse getAccessToken(String refreshToken);

    JwtResponse refresh(String refreshToken);

    JwtAuthentication getAuthInfo();
}
