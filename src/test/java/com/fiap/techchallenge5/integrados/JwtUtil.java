package com.fiap.techchallenge5.integrados;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class JwtUtil {

    public static String geraJwt() {
        return geraJwt(LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00")));
    }

    public static String geraJwt(Instant data) {
        return JWT.create()
                .withIssuer("auth-api")
                .withSubject("teste")
                .withClaim("role", "ADMIN")
                .withExpiresAt(data)
                .sign(Algorithm.HMAC256("segredoMaisSeguroDoMundo"));
    }

    public static String geraJwt(String role,
                                 String usuario) {
        return JWT.create()
                .withIssuer("auth-api")
                .withSubject(usuario)
                .withClaim("role", role)
                .withExpiresAt(LocalDateTime.now()
                        .plusHours(2)
                        .toInstant(ZoneOffset.of("-03:00")))
                .sign(Algorithm.HMAC256("segredoMaisSeguroDoMundo"));
    }

}
