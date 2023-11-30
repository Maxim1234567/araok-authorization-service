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
import ru.araok.dto.UserWithJwtResponse;
import ru.araok.exception.AuthException;
import ru.araok.service.AuthService;
import ru.araok.service.JwtService;
import ru.araok.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;

    private final Map<Long, String> refreshStorage = new HashMap<>();

    private final JwtService jwtService;


    @Override
    public UserWithJwtResponse saveAndGenerateToken(UserDto user) {
        UserDto createUser = userService.save(user);

        final JwtResponse token = login(JwtRequest.builder()
                .phone(user.getPhone())
                .password(user.getPassword())
                .build());

        return UserWithJwtResponse.builder()
                .user(createUser)
                .token(token)
                .build();
    }

    @Override
    @HystrixCommand(commandKey = "login")
    public JwtResponse login(JwtRequest authRequest) {
        log.info("auth user phone: {}", authRequest.getPhone());
        log.info("auth user password: {}", authRequest.getPassword());

        final UserDto user = userService.getByPhoneAndPassword(authRequest.getPhone(), authRequest.getPassword());

        log.info("password equals");

        final String accessToken = jwtService.generateAccessToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);
        refreshStorage.put(user.getId(), refreshToken);

        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    @HystrixCommand(commandKey = "getAccessToken")
    public JwtResponse getAccessToken(String refreshToken) {
        if(jwtService.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtService.getRefreshClaims(refreshToken);
            final Long id = Long.parseLong(claims.getSubject());
            final String saveRefreshToken = refreshStorage.get(id);

            if(saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final UserDto user = userService.getById(id);
                final String accessToken = jwtService.generateAccessToken(user);
                return new JwtResponse(accessToken, null);
            }
        }

        return new JwtResponse(null, null);
    }

    @Override
    @HystrixCommand(commandKey = "refresh")
    public JwtResponse refresh(String refreshToken) {
        if(jwtService.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtService.getRefreshClaims(refreshToken);
            final Long id = Long.parseLong(claims.getSubject());
            final String saveRefreshToken = refreshStorage.get(id);

            if(saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final UserDto user = userService.getById(id);
                final String accessToken = jwtService.generateAccessToken(user);
                final String newRefreshToken = jwtService.generateRefreshToken(user);
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
