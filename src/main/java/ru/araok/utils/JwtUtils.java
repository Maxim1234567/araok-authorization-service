package ru.araok.utils;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.araok.domain.JwtAuthentication;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {
    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRole(getRole(claims));
        jwtInfoToken.setName(claims.get("name", String.class));
        jwtInfoToken.setId(Long.parseLong(claims.getSubject()));
        return jwtInfoToken;
    }

    private static String getRole(Claims claims) {
        return claims.get("role", String.class);
    }
}
