package ru.araok.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.araok.dto.UserDto;
import ru.araok.service.DateService;
import ru.araok.service.JwtProviderService;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtProviderServiceImpl implements JwtProviderService {

    private final SecretKey jwtAccessSecret;

    private final SecretKey jwtRefreshSecret;

    private final DateService dateService;

    public JwtProviderServiceImpl(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret,
            DateService dateService
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
        this.dateService = dateService;
    }

    @Override
    public String generateAccessToken(UserDto user) {
        log.info("generate access token");

        final LocalDateTime now = dateService.getDateNow();
        final Instant accessExpirationInstant = now.plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setExpiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("role", user.getRole())
                .claim("phone", user.getPhone())
                .compact();
    }

    @Override
    public String generateRefreshToken(UserDto user) {
        log.info("generate refresh token");

        final LocalDateTime now = dateService.getDateNow();
        final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        log.info("check validation access token");

        return validateToken(accessToken, jwtAccessSecret);
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(String token, Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            expEx.printStackTrace();
        } catch (UnsupportedJwtException unsEx) {
            unsEx.printStackTrace();
        } catch (MalformedJwtException mjEx) {
            mjEx.printStackTrace();
        } catch (SignatureException sEx) {
            sEx.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Claims getAccessClaims(String token) {
        return getClaims(token, jwtAccessSecret);
    }

    @Override
    public Claims getRefreshClaims(String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(String token,Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
