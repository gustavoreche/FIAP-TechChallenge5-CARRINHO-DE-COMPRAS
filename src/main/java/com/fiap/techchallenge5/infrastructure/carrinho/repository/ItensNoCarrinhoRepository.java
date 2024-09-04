package com.fiap.techchallenge5.infrastructure.carrinho.repository;

import com.fiap.techchallenge5.infrastructure.carrinho.model.ItensNoCarrinhoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItensNoCarrinhoRepository extends JpaRepository<ItensNoCarrinhoEntity, Long> {

    List<ItensNoCarrinhoEntity> findByIdIdCarrinho(final Long idCarrinho);

}
