package ru.araok.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.araok.domain.User;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    private String name;

    private String phone;

    private String password;

    private LocalDate birthDate;

    private String role;

    public static User toDomainObject(UserDto dto) {
        return User.builder()
                .id(dto.id)
                .name(dto.name)
                .phone(dto.phone)
                .password(dto.password)
                .birthDate(dto.birthDate)
                .role(dto.role)
                .build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .password(user.getPassword())
                .birthDate(user.getBirthDate())
                .role(user.getRole())
                .build();
    }
}
