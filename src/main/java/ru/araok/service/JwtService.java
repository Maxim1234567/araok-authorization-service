package ru.araok.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import ru.araok.dto.UserDto;

import java.util.function.Function;

public interface JwtService {
    String generateAccessToken(UserDto user);

    String generateRefreshToken(UserDto user);

    boolean validateAccessToken(String accessToken, UserDetails userDetails);

    boolean validateRefreshToken(String refreshToken);

    Claims getAccessClaims(String token);

    Claims getRefreshClaims(String token);

    String extractNameFromJwtToken(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}
