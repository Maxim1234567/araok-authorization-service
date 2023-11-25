package ru.araok.service.impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.araok.domain.JwtAuthentication;
import ru.araok.dto.JwtRequest;
import ru.araok.dto.JwtResponse;
import ru.araok.dto.UserDto;
import ru.araok.exception.AuthException;
import ru.araok.service.AuthService;
import ru.araok.service.JwtProviderService;
import ru.araok.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;

    private final Map<Long, String> refreshStorage = new HashMap<>();

    private final JwtProviderService jwtProviderService;

    @Override
    @HystrixCommand(commandKey = "login")
    public JwtResponse login(JwtRequest authRequest) {
        final UserDto user = userService.getByPhoneAndPassword(authRequest.getPhone(), authRequest.getPassword());

        log.info("password equals");

        log.info("auth user phone: {}", user.getPhone());
        log.info("auth user password: {}", user.getPassword());

        final String accessToken = jwtProviderService.generateAccessToken(user);
        final String refreshToken = jwtProviderService.generateRefreshToken(user);
        refreshStorage.put(user.getId(), refreshToken);

        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    @HystrixCommand(commandKey = "getAccessToken")
    public JwtResponse getAccessToken(String refreshToken) {
        if(jwtProviderService.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProviderService.getRefreshClaims(refreshToken);
            final Long id = Long.parseLong(claims.getSubject());
            final String saveRefreshToken = refreshStorage.get(id);

            if(saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final UserDto user = userService.getById(id);
                final String accessToken = jwtProviderService.generateAccessToken(user);
                return new JwtResponse(accessToken, null);
            }
        }

        return new JwtResponse(null, null);
    }

    @Override
    @HystrixCommand(commandKey = "refresh")
    public JwtResponse refresh(String refreshToken) {
        if(jwtProviderService.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProviderService.getRefreshClaims(refreshToken);
            final Long id = Long.parseLong(claims.getSubject());
            final String saveRefreshToken = refreshStorage.get(id);

            if(saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final UserDto user = userService.getById(id);
                final String accessToken = jwtProviderService.generateAccessToken(user);
                final String newRefreshToken = jwtProviderService.generateRefreshToken(user);
                refreshStorage.put(user.getId(), newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }

        throw new AuthException("Невалидный JWT токен");
    }

    @Override
    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
