package com.fiap.techchallenge5.infrastructure.carrinho.controller.dto;

import java.math.BigDecimal;

public record CarrinhoDisponivelParaPagamentoDTO(

		String usuario,
		BigDecimal valorTotal
) {}
