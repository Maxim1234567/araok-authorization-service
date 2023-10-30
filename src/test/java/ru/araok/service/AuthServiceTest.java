package ru.araok.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.araok.dto.JwtRequest;
import ru.araok.dto.JwtResponse;
import ru.araok.dto.UserDto;
import ru.araok.exception.AuthException;
import ru.araok.service.impl.AuthServiceImpl;

import java.time.LocalDate;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNjk4Njg1NTAwLCJyb2xlIjoiVVNFUiIsIm5hbWUiOiJNYXhpbSJ9.-ozJvK3by0TCPl0wpZrTEv4qAlOB4UbMNYpMvYif3D2BT7KfBeebu3eTjrsV05JZIDE7gDlPhoKx9UM3VoTlsw";

    private static final String REFRESH_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzAxMjc3MjAwfQ.b07UmYm6JnUDKx8FPAtyFhQWpIMeKB5-xwFCnTH5xyu-VDPBpR_PauNX34m4SXf3Id3IAnwOfg4EPLbq2v2RSg";

    @Mock
    private UserService userService;

    @Mock
    private JwtProviderService jwtProviderService;

    private AuthService authService;

    private UserDto user;

    private JwtRequest authRequest;

    private JwtResponse jwtResponse;

    @BeforeEach
    public void setUp() {
        user = UserDto.builder()
                .id(1L)
                .name("Maxim")
                .phone("89999999999")
                .password("12345")
                .birthDate(LocalDate.now())
                .role("USER")
                .build();

        authRequest = JwtRequest.builder()
                .name("maxim")
                .password("12345")
                .build();

        jwtResponse = JwtResponse.builder()
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .build();

        authService = new AuthServiceImpl(
                userService, jwtProviderService
        );
    }

    @Test
    public void shouldCorrectLoginReturnJwtResponse() {
        given(userService.getByName(eq("maxim")))
                .willReturn(user);

        given(jwtProviderService.generateAccessToken(user))
                .willReturn(ACCESS_TOKEN);

        given(jwtProviderService.generateRefreshToken(user))
                .willReturn(REFRESH_TOKEN);

        JwtResponse response = authService.login(authRequest);

        verify(userService, times(1)).getByName(eq("maxim"));

        verify(jwtProviderService, times(1)).generateAccessToken(any(UserDto.class));

        verify(jwtProviderService, times(1)).generateRefreshToken(any(UserDto.class));

        assertThat(response).isNotNull()
                .matches(r -> r.getAccessToken().equals(ACCESS_TOKEN))
                .matches(r -> r.getRefreshToken().equals(REFRESH_TOKEN))
                .matches(r -> r.getType().equals("Bearer"));
    }

    @Test
    public void shouldThrowAuthExceptionIncorrectPassword() {
        user.setPassword("Incorrect password");

        given(userService.getByName("maxim"))
                .willReturn(user);

        assertThrows(AuthException.class, () -> authService.login(authRequest));

        verify(userService, times(1)).getByName(eq("maxim"));
    }

    @Test
    public void shouldReturnEmptyJwtResponseIfValidateFalse() {
        JwtResponse response = authService.getAccessToken(REFRESH_TOKEN);

        assertThat(response).isNotNull()
                .matches(r -> Objects.isNull(r.getAccessToken()))
                .matches(r -> Objects.isNull(r.getRefreshToken()));
    }

    @Test
    public void shouldThrowAuthExceptionIfValidateRefreshToken() {
        assertThrows(AuthException.class, () -> authService.refresh(REFRESH_TOKEN));
    }
}
