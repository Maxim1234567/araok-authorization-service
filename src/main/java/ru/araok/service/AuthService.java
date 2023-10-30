package ru.araok.service;

import ru.araok.domain.JwtAuthentication;
import ru.araok.dto.JwtRequest;
import ru.araok.dto.JwtResponse;

public interface AuthService {
    JwtResponse login(JwtRequest authRequest);

    JwtResponse getAccessToken(String refreshToken);

    JwtResponse refresh(String refreshToken);

    JwtAuthentication getAuthInfo();
}
