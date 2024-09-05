package com.fiap.techchallenge5.unitario;

import com.fiap.techchallenge5.infrastructure.carrinho.controller.CarrinhoController;
import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.AdicionaItemDTO;
import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.CarrinhoDisponivelParaPagamentoDTO;
import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.ItensDoCarrinhoDTO;
import com.fiap.techchallenge5.useCase.carrinho.impl.CarrinhoUseCaseImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

public class CarrinhoControllerTest {

    @Test
    public void insere_deveRetornar201_salvaNaBaseDeDados() {
        // preparação
        var service = Mockito.mock(CarrinhoUseCaseImpl.class);
        Mockito.when(service.insere(
                            any(AdicionaItemDTO.class),
                            any(String.class)
                        )
                )
                .thenReturn(
                        true
                );

        var controller = new CarrinhoController(service);

        // execução
        var carrinho = controller.insere(
                new AdicionaItemDTO(
                        7894900011517L,
                        1L
                ),
                "tokenTeste"
        );

        // avaliação
        Assertions.assertEquals(HttpStatus.CREATED, carrinho.getStatusCode());
    }

    @Test
    public void insere_deveRetornar409_naoSalvaNaBaseDeDados() {
        // preparação
        var service = Mockito.mock(CarrinhoUseCaseImpl.class);
        Mockito.when(service.insere(
                            any(AdicionaItemDTO.class),
                            any(String.class)
                        )
                )
                .thenReturn(
                        false
                );

        var controller = new CarrinhoController(service);

        // execução
        var carrinho = controller.insere(
                new AdicionaItemDTO(
                        7894900011517L,
                        1L
                ),
                "tokenTeste"
        );

        // avaliação
        Assertions.assertEquals(HttpStatus.CONFLICT, carrinho.getStatusCode());
    }

    @Test
    public void remove_deveRetornar200_salvaNaBaseDeDados() {
        // preparação
        var service = Mockito.mock(CarrinhoUseCaseImpl.class);
        Mockito.when(service.remove(
                                anyLong(),
                                any(String.class)
                        )
                )
                .thenReturn(
                        true
                );

        var controller = new CarrinhoController(service);

        // execução
        var carrinho = controller.remove(1L,"tokenTeste");

        // avaliação
        Assertions.assertEquals(HttpStatus.OK, carrinho.getStatusCode());
    }

    @Test
    public void cancela_deveRetornar204_naoSalvaNaBaseDeDados() {
        // preparação
        var service = Mockito.mock(CarrinhoUseCaseImpl.class);
        Mockito.when(service.remove(
                                anyLong(),
                                any(String.class)
                        )
                )
                .thenReturn(
                        false
                );

        var controller = new CarrinhoController(service);

        // execução
        var carrinho = controller.remove(1L, "tokenTeste");

        // avaliação
        Assertions.assertEquals(HttpStatus.NO_CONTENT, carrinho.getStatusCode());
    }

    @Test
    public void disponivelParaPagamento_deveRetornar200() {
        // preparação
        var service = Mockito.mock(CarrinhoUseCaseImpl.class);
        Mockito.when(service.disponivelParaPagamento(
                                any(String.class)
                        )
                )
                .thenReturn(
                        new CarrinhoDisponivelParaPagamentoDTO(
                                "teste",
                                new BigDecimal("100.00"),
                                List.of(new ItensDoCarrinhoDTO(123456789L, new BigDecimal("100.00")))
                        )
                );

        var controller = new CarrinhoController(service);

        // execução
        var carrinho = controller.disponivelParaPagamento("tokenTeste");

        // avaliação
        Assertions.assertEquals(HttpStatus.OK, carrinho.getStatusCode());
    }

    @Test
    public void disponivelParaPagamento_deveRetornar204() {
        // preparação
        var service = Mockito.mock(CarrinhoUseCaseImpl.class);
        Mockito.when(service.disponivelParaPagamento(
                                any(String.class)
                        )
                )
                .thenReturn(
                        null
                );

        var controller = new CarrinhoController(service);

        // execução
        var carrinho = controller.disponivelParaPagamento("tokenTeste");

        // avaliação
        Assertions.assertEquals(HttpStatus.NO_CONTENT, carrinho.getStatusCode());
    }

    @Test
    public void finaliza_deveRetornar200() {
        // preparação
        var service = Mockito.mock(CarrinhoUseCaseImpl.class);
        Mockito.when(service.finaliza(
                                any(String.class)
                        )
                )
                .thenReturn(
                        true
                );

        var controller = new CarrinhoController(service);

        // execução
        var carrinho = controller.finaliza("tokenTeste");

        // avaliação
        Assertions.assertEquals(HttpStatus.OK, carrinho.getStatusCode());
    }

    @Test
    public void finaliza_deveRetornar204() {
        // preparação
        var service = Mockito.mock(CarrinhoUseCaseImpl.class);
        Mockito.when(service.finaliza(
                                any(String.class)
                        )
                )
                .thenReturn(
                        false
                );

        var controller = new CarrinhoController(service);

        // execução
        var carrinho = controller.finaliza("tokenTeste");

        // avaliação
        Assertions.assertEquals(HttpStatus.NO_CONTENT, carrinho.getStatusCode());
    }


    @ParameterizedTest
    @MethodSource("requestValidandoCampos")
    public void insere_camposInvalidos_naoSalvaNaBaseDeDados(Long ean,
                                                             Long quantidade) {
        // preparação
        var service = Mockito.mock(CarrinhoUseCaseImpl.class);
        Mockito.doThrow(
                        new IllegalArgumentException("Campos inválidos!")
                )
                .when(service)
                .insere(
                        any(AdicionaItemDTO.class),
                        any(String.class)
                );

        var controller = new CarrinhoController(service);

        // execução e avaliação
        var excecao = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            controller.insere(
                    new AdicionaItemDTO(
                            ean,
                            quantidade
                    ),
                    "tokenTeste"
            );
        });
    }

    @ParameterizedTest
    @ValueSource(longs = {
            -1000,
            -1L,
            0
    })
    public void remove_camposInvalidos_naoSalvaNaBaseDeDados(Long ean) {
        // preparação
        var service = Mockito.mock(CarrinhoUseCaseImpl.class);
        Mockito.doThrow(
                        new IllegalArgumentException("Campos inválidos!")
                )
                .when(service)
                .remove(
                        anyLong(),
                        any(String.class)
                );

        var controller = new CarrinhoController(service);

        // execução e avaliação
        var excecao = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            controller.remove(ean, "tokenTeste");
        });
    }

    private static Stream<Arguments> requestValidandoCampos() {
        return Stream.of(
                Arguments.of(null, 100L),
                Arguments.of(-1L, 100L),
                Arguments.of(0L, 100L),
                Arguments.of(123456789L, null),
                Arguments.of(123456789L, -1L),
                Arguments.of(123456789L, 0L),
                Arguments.of(123456789L, 1001L)
        );
    }

}
