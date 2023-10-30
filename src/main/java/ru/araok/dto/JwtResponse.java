package ru.araok.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class JwtResponse {
    private final String type = "Bearer";

    private String accessToken;

    private String refreshToken;
}
