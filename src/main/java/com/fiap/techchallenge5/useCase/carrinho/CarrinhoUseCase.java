package com.fiap.techchallenge5.useCase.carrinho;

import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.AdicionaItemDTO;

public interface CarrinhoUseCase {

    boolean insere(final AdicionaItemDTO dadosItem,
                   final String token);

    boolean remove(final Long ean,
                   final String token);

    boolean disponivelParaPagamento(final String token);
}
