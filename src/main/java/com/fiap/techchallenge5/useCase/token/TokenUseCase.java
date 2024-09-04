package com.fiap.techchallenge5.useCase.token;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface TokenUseCase {

    DecodedJWT pegaJwt(String token);

    String pegaUsuario(DecodedJWT jwt);

    Collection<? extends GrantedAuthority> pegaPermissoes(DecodedJWT jwt);
}
