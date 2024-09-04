package com.fiap.techchallenge5.infrastructure.carrinho.controller;

import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.AdicionaItemDTO;
import com.fiap.techchallenge5.useCase.carrinho.CarrinhoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.fiap.techchallenge5.infrastructure.carrinho.controller.CarrinhoController.URL_CARRINHO;

@Tag(
		name = "Itens",
		description = "Serviço para realizar o gerenciamento do carrinho de compras no sistema"
)
@RestController
@RequestMapping(URL_CARRINHO)
public class CarrinhoController {

	public static final String URL_CARRINHO = "/carrinho";
	public static final String URL_CARRINHO_COM_EAN = URL_CARRINHO + "/{ean}";

	private final CarrinhoUseCase service;

	public CarrinhoController(final CarrinhoUseCase service) {
		this.service = service;
	}

	@Operation(
			summary = "Serviço para inserir um item no carrinho. Caso não exista um carrinho, será criado"
	)
	@PostMapping
	public ResponseEntity<Void> insere(@RequestBody @Valid final AdicionaItemDTO dadosItem,
									   @RequestHeader("Authorization") final String token) {
		final var inseriu = this.service.insere(dadosItem, token);
		if(inseriu) {
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.build();
		}
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.build();
	}

	@Operation(
			summary = "Serviço para remover um item do carrinho"
	)
	@DeleteMapping("/{ean}")
	public ResponseEntity<Void> remove(@PathVariable("ean") final Long ean,
									   @RequestHeader("Authorization") final String token) {
		final var removeu = this.service.remove(ean, token);
		if(removeu) {
			return ResponseEntity
					.status(HttpStatus.OK)
					.build();
		}
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.build();
	}

}
