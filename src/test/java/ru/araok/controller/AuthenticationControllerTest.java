package ru.araok.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.araok.dto.JwtRequest;
import ru.araok.dto.JwtResponse;
import ru.araok.dto.UserDto;
import ru.araok.filter.JwtFilter;
import ru.araok.service.AuthService;
import ru.araok.service.UserService;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    private static final String ACCESS_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNjk4Njg1NTAwLCJyb2xlIjoiVVNFUiIsIm5hbWUiOiJNYXhpbSJ9.-ozJvK3by0TCPl0wpZrTEv4qAlOB4UbMNYpMvYif3D2BT7KfBeebu3eTjrsV05JZIDE7gDlPhoKx9UM3VoTlsw";

    private static final String REFRESH_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzAxMjc3MjAwfQ.b07UmYm6JnUDKx8FPAtyFhQWpIMeKB5-xwFCnTH5xyu-VDPBpR_PauNX34m4SXf3Id3IAnwOfg4EPLbq2v2RSg";

    private static String JSON_USER =
            """
                {
                    "name": "Test",
                    "phone": "89999999999",
                    "password": "12345",
                    "birthDate": "1994-08-05",
                    "role": "USER"
                }
            """;

    private static String JSON_AUTH_REQUEST = "{\"phone\":\"89999999999\",\"password\":\"12345\"}";

    private static String JSON_JWT_RESPONSE = "{\"type\":\"Bearer\",\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNjk4Njg1NTAwLCJyb2xlIjoiVVNFUiIsIm5hbWUiOiJNYXhpbSJ9.-ozJvK3by0TCPl0wpZrTEv4qAlOB4UbMNYpMvYif3D2BT7KfBeebu3eTjrsV05JZIDE7gDlPhoKx9UM3VoTlsw\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzAxMjc3MjAwfQ.b07UmYm6JnUDKx8FPAtyFhQWpIMeKB5-xwFCnTH5xyu-VDPBpR_PauNX34m4SXf3Id3IAnwOfg4EPLbq2v2RSg\"}";

    private static String JSON_REFRESH_JWT_REQUEST = "{\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzAxMjc3MjAwfQ.b07UmYm6JnUDKx8FPAtyFhQWpIMeKB5-xwFCnTH5xyu-VDPBpR_PauNX34m4SXf3Id3IAnwOfg4EPLbq2v2RSg\"}";

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper mapper;

    private JwtRequest authRequest;

    private JwtResponse jwtResponse;

    private UserDto user;

    @BeforeEach
    public void setUp() {
        user = UserDto.builder()
                .name("Test")
                .phone("89999999999")
                .password("12345")
                .birthDate(LocalDate.of(1994, 8, 5))
                .role("USER")
                .build();

        authRequest = JwtRequest.builder()
                .phone("89999999999")
                .password("12345")
                .build();

        jwtResponse = JwtResponse.builder()
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .build();

        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldCorrectRegistrationUser() throws Exception {
        System.out.println(mapper.writeValueAsString(user));

        given(authService.login(any(JwtRequest.class)))
                .willReturn(jwtResponse);

        mvc.perform(post("/auth/registration")
                .with(csrf())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .content(JSON_USER)
        ).andExpect(status().isCreated())
        .andExpect(content().json(JSON_JWT_RESPONSE));

        verify(userService, times(1)).save(any(UserDto.class));
        verify(authService, times(1)).login(any(JwtRequest.class));
    }

    @Test
    public void shouldCorrectReturnJwtResponseLogin() throws Exception {
        given(authService.login(any(JwtRequest.class)))
                .willReturn(jwtResponse);

        mvc.perform(post("/auth/login")
                .with(csrf())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .content(JSON_AUTH_REQUEST)
        ).andExpect(status().isOk())
        .andExpect(content().json(JSON_JWT_RESPONSE));

        verify(authService, times(1)).login(any(JwtRequest.class));
    }

    @Test
    public void shouldCorrectReturnJwtResponseNewAccessToken() throws Exception {
        given(authService.getAccessToken(eq(REFRESH_TOKEN)))
                .willReturn(jwtResponse);

        mvc.perform(post("/auth/token")
                .with(csrf())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .content(JSON_REFRESH_JWT_REQUEST)
        ).andExpect(status().isOk())
        .andExpect(content().json(JSON_JWT_RESPONSE));

        verify(authService, times(1)).getAccessToken(eq(REFRESH_TOKEN));
    }

    @Test
    public void shouldCorrectReturnJwtResponseNewRefreshToken() throws Exception {
        given(authService.refresh(eq(REFRESH_TOKEN)))
                .willReturn(jwtResponse);

        mvc.perform(post("/auth/refresh")
                .with(csrf())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .content(JSON_REFRESH_JWT_REQUEST)
        ).andExpect(status().isOk())
        .andExpect(content().json(JSON_JWT_RESPONSE));

        verify(authService, times(1)).refresh(eq(REFRESH_TOKEN));
    }
}
