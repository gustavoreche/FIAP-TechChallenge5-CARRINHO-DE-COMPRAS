package com.fiap.techchallenge5.infrastructure.carrinho.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public record AdicionaItemDTO(

		@JsonInclude(JsonInclude.Include.NON_NULL)
		Long ean,

		@JsonInclude(JsonInclude.Include.NON_NULL)
		Long quantidade
) {}
