package com.fiap.techchallenge5.infrastructure.carrinho.controller;

import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.AdicionaItemDTO;
import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.CarrinhoDisponivelParaPagamentoDTO;
import com.fiap.techchallenge5.useCase.carrinho.CarrinhoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
	public static final String URL_CARRINHO_DISPONIVEL_PARA_PAGAMENTO = URL_CARRINHO + "/disponivel-para-pagamento";
	public static final String URL_CARRINHO_FINALIZA = URL_CARRINHO + "/finaliza";

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

	@Operation(
			summary = "Serviço para verificar se o carrinho está disponível para realizar o pagamento"
	)
	@GetMapping("/disponivel-para-pagamento")
	public ResponseEntity<CarrinhoDisponivelParaPagamentoDTO> disponivelParaPagamento(@RequestHeader("Authorization") final String token) {
		final var disponivel = this.service.disponivelParaPagamento(token);
		if(Objects.nonNull(disponivel)) {
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(disponivel);
		}
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.build();
	}

	@Operation(
			summary = "Serviço para realizar o pagamento do carrinho"
	)
	@PutMapping("/finaliza")
	public ResponseEntity<Void> finaliza(@RequestHeader("Authorization") final String token) {
		final var finaliza = this.service.finaliza(token);
		if(finaliza) {
			return ResponseEntity
					.status(HttpStatus.OK)
					.build();
		}
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.build();
	}

}
