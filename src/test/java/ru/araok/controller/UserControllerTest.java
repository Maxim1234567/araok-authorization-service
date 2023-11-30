package ru.araok.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.araok.dto.UserDto;
import ru.araok.enums.RoleEnum;
import ru.araok.filter.JwtFilter;
import ru.araok.service.UserService;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    private static String JSON_USER =
            """
                {
                    "id": 1,
                    "name": "Test",
                    "phone": "89999999999",
                    "password": "12345",
                    "birthDate": "1994-08-05",
                    "role": "USER"
                }
            """;

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

    private UserDto user;

    @BeforeEach
    public void setUp() {
        user = UserDto.builder()
                .id(1L)
                .name("Test")
                .phone("89999999999")
                .password("12345")
                .birthDate(LocalDate.of(1994, 8, 5))
                .role(RoleEnum.USER)
                .build();

        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldCorrectReturnUserById() throws Exception {
        given(userService.getById(eq(user.getId())))
                .willReturn(user);

        mvc.perform(get("/auth/user/" + user.getId())
                .with(csrf())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
        ).andExpect(status().isOk())
        .andExpect(content().json(JSON_USER));

        verify(userService, times(1))
                .getById(eq(user.getId()));
    }
}