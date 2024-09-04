package com.fiap.techchallenge5.useCase.token.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fiap.techchallenge5.useCase.token.TokenUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


@Service
@Slf4j
public class TokenUseCaseImpl implements TokenUseCase {

    @Value("${api.security.token.secret}")
    private String secret;

    @Override
    public DecodedJWT pegaJwt(String token) {
        try {
            final var algorithm = Algorithm.HMAC256(this.secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token);
        } catch (Exception error) {
            log.error("Erro ao decodificar o token", error);
            return null;
        }
    }

    @Override
    public String pegaUsuario(DecodedJWT jwt) {
        return jwt.getSubject();
    }

    @Override
    public Collection<? extends GrantedAuthority> pegaPermissoes(DecodedJWT jwt) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + jwt.getClaim("role").asString()));
    }

}
