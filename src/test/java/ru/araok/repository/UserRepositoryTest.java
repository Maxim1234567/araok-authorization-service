package ru.araok.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.araok.domain.User;
import ru.araok.enums.RoleEnum;

import java.time.LocalDate;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .name("Test")
                .phone("89999999999")
                .password("12345")
                .birthDate(LocalDate.of(1994, 8, 5))
                .role(RoleEnum.USER)
                .build();
    }

    @Test
    public void shouldCorrectSaveUser() {
        User saveUser = userRepository.save(user);
        User result = userRepository.findById(saveUser.getId()).get();

        assertThat(result).isNotNull()
                .matches(u -> Objects.nonNull(u.getId()))
                .matches(u -> u.getName().equals(result.getName()))
                .matches(u -> u.getPhone().equals(result.getPhone()))
                .matches(u -> u.getPassword().equals(result.getPassword()))
                .matches(u -> u.getBirthDate().equals(result.getBirthDate()))
                .matches(u -> u.getRole().equals(result.getRole()));
    }
}
