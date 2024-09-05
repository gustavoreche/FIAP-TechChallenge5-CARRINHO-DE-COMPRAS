package com.fiap.techchallenge5.useCase.carrinho;

import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.AdicionaItemDTO;
import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.CarrinhoDisponivelParaPagamentoDTO;

public interface CarrinhoUseCase {

    boolean insere(final AdicionaItemDTO dadosItem,
                   final String token);

    boolean remove(final Long ean,
                   final String token);

    CarrinhoDisponivelParaPagamentoDTO disponivelParaPagamento(final String token);

    boolean finaliza(final String token);
}
