package com.fiap.techchallenge5.infrastructure.carrinho.repository;

import com.fiap.techchallenge5.domain.StatusEnum;
import com.fiap.techchallenge5.infrastructure.carrinho.model.CarrinhoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<CarrinhoEntity, Long> {

    Optional<CarrinhoEntity> findByUsuarioAndStatus(final String token,
                                                    final StatusEnum status);

}
