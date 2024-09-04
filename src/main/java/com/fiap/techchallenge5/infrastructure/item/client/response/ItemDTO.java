package com.fiap.techchallenge5.infrastructure.item.client.response;

import java.math.BigDecimal;

public record ItemDTO(

		Long ean,
		BigDecimal preco
) {}
