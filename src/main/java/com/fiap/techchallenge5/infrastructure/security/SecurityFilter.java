package com.fiap.techchallenge5.infrastructure.security;

import com.fiap.techchallenge5.useCase.token.TokenUseCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    final TokenUseCase service;

    SecurityFilter(final TokenUseCase service){
        this.service = service;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().contains("swagger") || request.getRequestURI().contains("api-docs")){
            filterChain.doFilter(request, response);
            return;
        }

        final var token = this.recuperaToken(request);
        if(Objects.nonNull(token)){
            final var jwt = this.service.pegaJwt(token);
            if(Objects.nonNull(jwt)){

                final var authentication = new UsernamePasswordAuthenticationToken(
                        this.service.pegaUsuario(jwt),
                        null,
                        this.service.pegaPermissoes(jwt)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }
            else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido");
                return;
            }
        }
        else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token não informado");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String recuperaToken(final HttpServletRequest request){
        final var tokenNoHeader = request.getHeader("Authorization");
        if(Objects.nonNull(tokenNoHeader)) {
            return tokenNoHeader.replace("Bearer ", "");
        }
        return null;
    }

}