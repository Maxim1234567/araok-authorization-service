package ru.araok.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.araok.domain.User;
import ru.araok.enums.RoleEnum;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements UserDetails {
    private Long id;

    private String name;

    private String phone;

    private String password;

    private LocalDate birthDate;

    private RoleEnum role;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return String.valueOf(id);
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

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
