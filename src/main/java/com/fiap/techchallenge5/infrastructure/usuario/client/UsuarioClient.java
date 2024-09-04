package com.fiap.techchallenge5.infrastructure.usuario.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "usuario", url = "http://172.17.0.1:8080/usuario")
public interface UsuarioClient {

    @GetMapping(value = "/{login}")
    Boolean usuarioExiste(@PathVariable(value = "login") final String login,
                          @RequestHeader("Authorization") final String token);

}
