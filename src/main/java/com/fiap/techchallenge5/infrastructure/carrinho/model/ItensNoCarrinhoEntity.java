package com.fiap.techchallenge5.infrastructure.carrinho.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_itens_no_carrinho")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItensNoCarrinhoEntity {

    @EmbeddedId
    private ItensNoCarrinhoId id;
    private BigDecimal precoTotal;

}
