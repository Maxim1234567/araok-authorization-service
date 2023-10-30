package ru.araok.service;

import io.jsonwebtoken.Claims;
import ru.araok.dto.UserDto;

public interface JwtProviderService {
    String generateAccessToken(UserDto user);

    String generateRefreshToken(UserDto user);

    boolean validateAccessToken(String accessToken);

    boolean validateRefreshToken(String refreshToken);

    Claims getAccessClaims(String token);

    Claims getRefreshClaims(String token);
}
