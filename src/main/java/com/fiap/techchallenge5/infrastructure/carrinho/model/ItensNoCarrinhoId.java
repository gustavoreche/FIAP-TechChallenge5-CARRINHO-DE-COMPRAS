package com.fiap.techchallenge5.infrastructure.carrinho.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItensNoCarrinhoId {

    private Long idCarrinho;
    private Long ean;

}
