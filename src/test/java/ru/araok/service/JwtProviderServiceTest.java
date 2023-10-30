package ru.araok.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.araok.dto.UserDto;
import ru.araok.service.impl.JwtProviderServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class JwtProviderServiceTest {
    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNjk4Njg1NTAwLCJyb2xlIjoiVVNFUiIsIm5hbWUiOiJNYXhpbSJ9.-ozJvK3by0TCPl0wpZrTEv4qAlOB4UbMNYpMvYif3D2BT7KfBeebu3eTjrsV05JZIDE7gDlPhoKx9UM3VoTlsw";
    private static final String REFRESH_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzAxMjc3MjAwfQ.b07UmYm6JnUDKx8FPAtyFhQWpIMeKB5-xwFCnTH5xyu-VDPBpR_PauNX34m4SXf3Id3IAnwOfg4EPLbq2v2RSg";

    private final String JWT_SECRET_ACCESS = "o5Ml5nd4nu7x0//6YavppFqDatneQWMV6YPpZ3beoHLXP3K23zYQhky5X1p4/H5eUdX1btPmjjBWPFR44/HIeA==";
    private final String JWT_SECRET_REFRESH = "CgsygFaRjJVArdgo5z3/RE6TuU3cU/e6/yRiXuAhdIrbn0VUlAMp/QmCWFPTMO1Psy3TxhRsZVPaIsTMX6uy+w==";

    @Mock
    private DateService dateService;

    private JwtProviderService jwtProviderService;

    private UserDto user;

    @BeforeEach
    public void setUp() {
        jwtProviderService = new JwtProviderServiceImpl(
                JWT_SECRET_ACCESS, JWT_SECRET_REFRESH, dateService
        );

        user = UserDto.builder()
                .id(1L)
                .name("Maxim")
                .phone("89999999999")
                .password("12345")
                .birthDate(LocalDate.now())
                .role("USER")
                .build();
    }

    @Test
    public void shouldCorrectReturnAccessToken() {
        given(dateService.getDateNow())
                .willReturn(LocalDateTime.of(2023, 10, 30, 20, 00));

        String resultToken = jwtProviderService.generateAccessToken(user);

        verify(dateService, times(1)).getDateNow();

        assertThat(resultToken).isNotNull()
                .matches(token -> token.equals(ACCESS_TOKEN));
    }

    @Test
    public void shouldCorrectReturnRefreshToken() {
        given(dateService.getDateNow())
                .willReturn(LocalDateTime.of(2023, 10, 30, 20, 00));

        String resultToken = jwtProviderService.generateRefreshToken(user);

        verify(dateService, times(1)).getDateNow();

        assertThat(resultToken).isNotNull()
                .matches(token -> token.equals(REFRESH_TOKEN));
    }
}
