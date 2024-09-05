package com.fiap.techchallenge5.unitario;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fiap.techchallenge5.domain.StatusEnum;
import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.AdicionaItemDTO;
import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.CarrinhoDisponivelParaPagamentoDTO;
import com.fiap.techchallenge5.infrastructure.carrinho.model.CarrinhoEntity;
import com.fiap.techchallenge5.infrastructure.carrinho.model.ItensNoCarrinhoEntity;
import com.fiap.techchallenge5.infrastructure.carrinho.model.ItensNoCarrinhoId;
import com.fiap.techchallenge5.infrastructure.carrinho.repository.CarrinhoRepository;
import com.fiap.techchallenge5.infrastructure.carrinho.repository.ItensNoCarrinhoRepository;
import com.fiap.techchallenge5.infrastructure.item.client.ItemClient;
import com.fiap.techchallenge5.infrastructure.item.client.response.ItemDTO;
import com.fiap.techchallenge5.infrastructure.usuario.client.UsuarioClient;
import com.fiap.techchallenge5.useCase.carrinho.impl.CarrinhoUseCaseImpl;
import com.fiap.techchallenge5.useCase.token.impl.TokenUseCaseImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

public class CarrinhoUseCaseTest {

    @Test
    public void insere_carrinhoVazio_salvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.save(Mockito.any()))
                .thenReturn(
                        new ItensNoCarrinhoEntity(
                                new ItensNoCarrinhoId(1L, 123456L),
                                new BigDecimal("100.00")
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean insere = service.insere(
                new AdicionaItemDTO(
                        7894900011517L,
                        1L
                ),
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(1)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(1)).save(Mockito.any());
        verify(repositoryItensNoCarrinho, times(1)).save(Mockito.any());

        Assertions.assertTrue(insere);
    }

    @Test
    public void insere_carrinhoComItens_salvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.of(
                                new CarrinhoEntity(
                                        1L,
                                        "usuario de teste",
                                        StatusEnum.ABERTO,
                                        new BigDecimal("100.00"),
                                        LocalDateTime.now()
                                )
                        )
                );

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.save(Mockito.any()))
                .thenReturn(
                        new ItensNoCarrinhoEntity(
                                new ItensNoCarrinhoId(1L, 123456L),
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.findByIdIdCarrinho(Mockito.any()))
                .thenReturn(
                        List.of(
                                new ItensNoCarrinhoEntity(
                                        new ItensNoCarrinhoId(1L, 123456L),
                                        new BigDecimal("100.00")
                                )
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean insere = service.insere(
                new AdicionaItemDTO(
                        7894900011517L,
                        1L
                ),
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(1)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(1)).save(Mockito.any());
        verify(repositoryItensNoCarrinho, times(1)).save(Mockito.any());

        Assertions.assertTrue(insere);
    }

    @Test
    public void insere_itemNaoEncontrado_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        null
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.of(
                                new CarrinhoEntity(
                                        1L,
                                        "usuario de teste",
                                        StatusEnum.ABERTO,
                                        new BigDecimal("100.00"),
                                        LocalDateTime.now()
                                )
                        )
                );

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.save(Mockito.any()))
                .thenReturn(
                        new ItensNoCarrinhoEntity(
                                new ItensNoCarrinhoId(1L, 123456L),
                                new BigDecimal("100.00")
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean insere = service.insere(
                new AdicionaItemDTO(
                        7894900011517L,
                        1L
                ),
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(0)).pegaJwt(Mockito.any());
        verify(serviceToken, times(0)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(0)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).save(Mockito.any());

        Assertions.assertFalse(insere);
    }

    @Test
    public void insere_erroNoToken_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        null
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.save(Mockito.any()))
                .thenReturn(
                        new ItensNoCarrinhoEntity(
                                new ItensNoCarrinhoId(1L, 123456L),
                                new BigDecimal("100.00")
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean insere = service.insere(
                new AdicionaItemDTO(
                        7894900011517L,
                        1L
                ),
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(0)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(0)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).save(Mockito.any());

        Assertions.assertFalse(insere);
    }

    @Test
    public void insere_usuarioNaoExiste_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        null
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.save(Mockito.any()))
                .thenReturn(
                        new ItensNoCarrinhoEntity(
                                new ItensNoCarrinhoId(1L, 123456L),
                                new BigDecimal("100.00")
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean insere = service.insere(
                new AdicionaItemDTO(
                        7894900011517L,
                        1L
                ),
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).save(Mockito.any());

        Assertions.assertFalse(insere);
    }

    @Test
    public void insere_usuarioNaoExiste2_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        false
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.save(Mockito.any()))
                .thenReturn(
                        new ItensNoCarrinhoEntity(
                                new ItensNoCarrinhoId(1L, 123456L),
                                new BigDecimal("100.00")
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean insere = service.insere(
                new AdicionaItemDTO(
                        7894900011517L,
                        1L
                ),
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).save(Mockito.any());

        Assertions.assertFalse(insere);
    }

    @Test
    public void insere_usuarioNaoExiste3_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.doThrow(
                        new IllegalArgumentException("usuario nao existe!")
                )
                .when(clientUsuario)
                .usuarioExiste(
                        Mockito.any(),
                        Mockito.any()
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.save(Mockito.any()))
                .thenReturn(
                        new ItensNoCarrinhoEntity(
                                new ItensNoCarrinhoId(1L, 123456L),
                                new BigDecimal("100.00")
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean insere = service.insere(
                new AdicionaItemDTO(
                        7894900011517L,
                        1L
                ),
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).save(Mockito.any());

        Assertions.assertFalse(insere);
    }

    @Test
    public void remove_carrinhoComItens_salvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.of(
                                new CarrinhoEntity(
                                        1L,
                                        "usuario de teste",
                                        StatusEnum.ABERTO,
                                        new BigDecimal("100.00"),
                                        LocalDateTime.now()
                                )
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.findByIdIdCarrinho(Mockito.any()))
                .thenReturn(
                        List.of(
                                new ItensNoCarrinhoEntity(
                                        new ItensNoCarrinhoId(1L, 7894900011517L),
                                        new BigDecimal("100.00")
                                )
                        )
                );

        Mockito.doNothing().when(repositoryItensNoCarrinho).delete(Mockito.any());

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("10.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean remove = service.remove(
                7894900011517L,
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(1)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(2)).findByIdIdCarrinho(Mockito.any());
        verify(repositoryItensNoCarrinho, times(1)).delete(Mockito.any());
        verify(repositoryCarrinho, times(1)).save(Mockito.any());
        verify(repositoryCarrinho, times(0)).delete(Mockito.any());

        Assertions.assertTrue(remove);
    }

    @Test
    public void remove_itemNaoEncontrado_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        null
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.of(
                                new CarrinhoEntity(
                                        1L,
                                        "usuario de teste",
                                        StatusEnum.ABERTO,
                                        new BigDecimal("100.00"),
                                        LocalDateTime.now()
                                )
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.findByIdIdCarrinho(Mockito.any()))
                .thenReturn(
                        List.of(
                                new ItensNoCarrinhoEntity(
                                        new ItensNoCarrinhoId(1L, 7894900011517L),
                                        new BigDecimal("100.00")
                                )
                        )
                );

        Mockito.doNothing().when(repositoryItensNoCarrinho).delete(Mockito.any());

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("10.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean remove = service.remove(
                7894900011517L,
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(0)).pegaJwt(Mockito.any());
        verify(serviceToken, times(0)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(0)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).findByIdIdCarrinho(Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).delete(Mockito.any());
        verify(repositoryCarrinho, times(0)).delete(Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());

        Assertions.assertFalse(remove);
    }

    @Test
    public void remove_erroNoToken_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        null
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.of(
                                new CarrinhoEntity(
                                        1L,
                                        "usuario de teste",
                                        StatusEnum.ABERTO,
                                        new BigDecimal("100.00"),
                                        LocalDateTime.now()
                                )
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.findByIdIdCarrinho(Mockito.any()))
                .thenReturn(
                        List.of(
                                new ItensNoCarrinhoEntity(
                                        new ItensNoCarrinhoId(1L, 7894900011517L),
                                        new BigDecimal("100.00")
                                )
                        )
                );

        Mockito.doNothing().when(repositoryItensNoCarrinho).delete(Mockito.any());

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("10.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean remove = service.remove(
                7894900011517L,
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(0)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(0)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).findByIdIdCarrinho(Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).delete(Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());
        verify(repositoryCarrinho, times(0)).delete(Mockito.any());

        Assertions.assertFalse(remove);
    }

    @Test
    public void remove_usuarioNaoExiste_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        null
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.of(
                                new CarrinhoEntity(
                                        1L,
                                        "usuario de teste",
                                        StatusEnum.ABERTO,
                                        new BigDecimal("100.00"),
                                        LocalDateTime.now()
                                )
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.findByIdIdCarrinho(Mockito.any()))
                .thenReturn(
                        List.of(
                                new ItensNoCarrinhoEntity(
                                        new ItensNoCarrinhoId(1L, 7894900011517L),
                                        new BigDecimal("100.00")
                                )
                        )
                );

        Mockito.doNothing().when(repositoryItensNoCarrinho).delete(Mockito.any());

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("10.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean remove = service.remove(
                7894900011517L,
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).findByIdIdCarrinho(Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).delete(Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());
        verify(repositoryCarrinho, times(0)).delete(Mockito.any());

        Assertions.assertFalse(remove);
    }

    @Test
    public void remove_usuarioNaoExiste2_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        false
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.of(
                                new CarrinhoEntity(
                                        1L,
                                        "usuario de teste",
                                        StatusEnum.ABERTO,
                                        new BigDecimal("100.00"),
                                        LocalDateTime.now()
                                )
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.findByIdIdCarrinho(Mockito.any()))
                .thenReturn(
                        List.of(
                                new ItensNoCarrinhoEntity(
                                        new ItensNoCarrinhoId(1L, 7894900011517L),
                                        new BigDecimal("100.00")
                                )
                        )
                );

        Mockito.doNothing().when(repositoryItensNoCarrinho).delete(Mockito.any());

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("10.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean remove = service.remove(
                7894900011517L,
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).findByIdIdCarrinho(Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).delete(Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());
        verify(repositoryCarrinho, times(0)).delete(Mockito.any());

        Assertions.assertFalse(remove);
    }

    @Test
    public void remove_usuarioNaoExiste3_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.doThrow(
                        new IllegalArgumentException("usuario nao existe!")
                )
                .when(clientUsuario)
                .usuarioExiste(
                        Mockito.any(),
                        Mockito.any()
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.of(
                                new CarrinhoEntity(
                                        1L,
                                        "usuario de teste",
                                        StatusEnum.ABERTO,
                                        new BigDecimal("100.00"),
                                        LocalDateTime.now()
                                )
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.findByIdIdCarrinho(Mockito.any()))
                .thenReturn(
                        List.of(
                                new ItensNoCarrinhoEntity(
                                        new ItensNoCarrinhoId(1L, 7894900011517L),
                                        new BigDecimal("100.00")
                                )
                        )
                );

        Mockito.doNothing().when(repositoryItensNoCarrinho).delete(Mockito.any());

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("10.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean remove = service.remove(
                7894900011517L,
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).findByIdIdCarrinho(Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).delete(Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());
        verify(repositoryCarrinho, times(0)).delete(Mockito.any());

        Assertions.assertFalse(remove);
    }

    @Test
    public void remove_carrinhoNaoEncontrado_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        Mockito.when(repositoryItensNoCarrinho.findByIdIdCarrinho(Mockito.any()))
                .thenReturn(
                        List.of(
                                new ItensNoCarrinhoEntity(
                                        new ItensNoCarrinhoId(1L, 7894900011517L),
                                        new BigDecimal("100.00")
                                )
                        )
                );

        Mockito.doNothing().when(repositoryItensNoCarrinho).delete(Mockito.any());

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("10.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean remove = service.remove(
                7894900011517L,
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(1)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).findByIdIdCarrinho(Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).delete(Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());
        verify(repositoryCarrinho, times(0)).delete(Mockito.any());

        Assertions.assertFalse(remove);
    }

    @Test
    public void remove_itemNaoTemNoCarrinho_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.of(
                                new CarrinhoEntity(
                                        1L,
                                        "usuario de teste",
                                        StatusEnum.ABERTO,
                                        new BigDecimal("100.00"),
                                        LocalDateTime.now()
                                )
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.findByIdIdCarrinho(Mockito.any()))
                .thenReturn(
                        List.of(
                                new ItensNoCarrinhoEntity(
                                        new ItensNoCarrinhoId(1L, 123L),
                                        new BigDecimal("100.00")
                                )
                        )
                );

        Mockito.doNothing().when(repositoryItensNoCarrinho).delete(Mockito.any());

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("10.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean remove = service.remove(
                7894900011517L,
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(1)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(1)).findByIdIdCarrinho(Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).delete(Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());
        verify(repositoryCarrinho, times(0)).delete(Mockito.any());

        Assertions.assertFalse(remove);
    }

    @Test
    public void remove_removeUltimoItemDoCarrinho_salvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(clientItem.pegaItem(anyLong(), Mockito.any()))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.of(
                                new CarrinhoEntity(
                                        1L,
                                        "usuario de teste",
                                        StatusEnum.ABERTO,
                                        new BigDecimal("100.00"),
                                        LocalDateTime.now()
                                )
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.findByIdIdCarrinho(Mockito.any()))
                .thenReturn(
                        List.of(
                                new ItensNoCarrinhoEntity(
                                        new ItensNoCarrinhoId(1L, 7894900011517L),
                                        new BigDecimal("100.00")
                                )
                        ),
                        List.of()
                );

        Mockito.doNothing().when(repositoryItensNoCarrinho).delete(Mockito.any());

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("10.00"),
                                LocalDateTime.now()
                        )
                );

        Mockito.doNothing().when(repositoryCarrinho).delete(Mockito.any());

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean remove = service.remove(
                7894900011517L,
                "tokenTeste"
        );

        // avaliação
        verify(clientItem, times(1)).pegaItem(anyLong(), Mockito.any());
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(1)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(2)).findByIdIdCarrinho(Mockito.any());
        verify(repositoryItensNoCarrinho, times(1)).delete(Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());
        verify(repositoryCarrinho, times(1)).delete(Mockito.any());

        Assertions.assertTrue(remove);
    }

    @Test
    public void disponivelParaPagamento_sucesso() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.of(
                                new CarrinhoEntity(
                                        1L,
                                        "usuario de teste",
                                        StatusEnum.ABERTO,
                                        new BigDecimal("100.00"),
                                        LocalDateTime.now()
                                )
                        )
                );

        Mockito.when(repositoryItensNoCarrinho.findByIdIdCarrinho(Mockito.any()))
                .thenReturn(
                        List.of(
                                new ItensNoCarrinhoEntity(
                                        new ItensNoCarrinhoId(1L, 123456L),
                                        new BigDecimal("100.00")
                                )
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        CarrinhoDisponivelParaPagamentoDTO disponivel = service.disponivelParaPagamento("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(1)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(1)).findByIdIdCarrinho(Mockito.any());

        Assertions.assertEquals("usuario de teste", disponivel.usuario());
        Assertions.assertEquals(new BigDecimal("100.00"), disponivel.valorTotal());
        Assertions.assertEquals(1, disponivel.itens().size());
        Assertions.assertEquals(123456L, disponivel.itens().get(0).ean());
        Assertions.assertEquals(new BigDecimal("100.00"), disponivel.itens().get(0).valorTotal());
    }

    @Test
    public void disponivelParaPagamento_carrinhoFinalizadoOuCarrinhoNaoExiste() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        CarrinhoDisponivelParaPagamentoDTO disponivel = service.disponivelParaPagamento("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(1)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).findByIdIdCarrinho(Mockito.any());

        Assertions.assertNull(disponivel);
    }

    @Test
    public void disponivelParaPagamento_erroNoToken() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        null
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        CarrinhoDisponivelParaPagamentoDTO disponivel = service.disponivelParaPagamento("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(0)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(0)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).findByIdIdCarrinho(Mockito.any());

        Assertions.assertNull(disponivel);
    }

    @Test
    public void disponivelParaPagamento_usuarioNaoExiste() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        null
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        CarrinhoDisponivelParaPagamentoDTO disponivel = service.disponivelParaPagamento("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).findByIdIdCarrinho(Mockito.any());

        Assertions.assertNull(disponivel);
    }

    @Test
    public void disponivelParaPagamento_usuarioNaoExiste2() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        false
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        CarrinhoDisponivelParaPagamentoDTO disponivel = service.disponivelParaPagamento("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).findByIdIdCarrinho(Mockito.any());

        Assertions.assertNull(disponivel);
    }

    @Test
    public void disponivelParaPagamento_usuarioNaoExiste3() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.doThrow(
                        new IllegalArgumentException("usuario nao existe!")
                )
                .when(clientUsuario)
                .usuarioExiste(
                        Mockito.any(),
                        Mockito.any()
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        CarrinhoDisponivelParaPagamentoDTO disponivel = service.disponivelParaPagamento("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryItensNoCarrinho, times(0)).findByIdIdCarrinho(Mockito.any());

        Assertions.assertNull(disponivel);
    }

    @Test
    public void finaliza_sucesso_salvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.of(
                                new CarrinhoEntity(
                                        1L,
                                        "usuario de teste",
                                        StatusEnum.ABERTO,
                                        new BigDecimal("100.00"),
                                        LocalDateTime.now()
                                )
                        )
                );

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.ABERTO,
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean finaliza = service.finaliza("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(1)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(1)).save(Mockito.any());

        Assertions.assertTrue(finaliza);
    }

    @Test
    public void finaliza_carrinhoFinalizadoOuCarrinhoNaoExiste_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        Mockito.when(repositoryCarrinho.save(Mockito.any()))
                .thenReturn(
                        new CarrinhoEntity(
                                1L,
                                "usuario de teste",
                                StatusEnum.FINALIZADO,
                                new BigDecimal("100.00"),
                                LocalDateTime.now()
                        )
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean finaliza = service.finaliza("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(1)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());

        Assertions.assertFalse(finaliza);
    }

    @Test
    public void finaliza_erroNoToken_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        null
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        true
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean finaliza = service.finaliza("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(0)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(0)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());

        Assertions.assertFalse(finaliza);
    }

    @Test
    public void finaliza_usuarioNaoExiste_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        null
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean finaliza = service.finaliza("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());

        Assertions.assertFalse(finaliza);
    }

    @Test
    public void finaliza_usuarioNaoExiste2_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.when(clientUsuario.usuarioExiste(Mockito.any(), Mockito.any()))
                .thenReturn(
                        false
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean finaliza = service.finaliza("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());

        Assertions.assertFalse(finaliza);
    }

    @Test
    public void finaliza_usuarioNaoExiste3_naoSalvaNaBaseDeDados() {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        Mockito.when(serviceToken.pegaJwt(Mockito.any()))
                .thenReturn(
                        mock(DecodedJWT.class)
                );

        Mockito.when(serviceToken.pegaUsuario(Mockito.any()))
                .thenReturn(
                        "tokenTeste"
                );

        Mockito.doThrow(
                        new IllegalArgumentException("usuario nao existe!")
                )
                .when(clientUsuario)
                .usuarioExiste(
                        Mockito.any(),
                        Mockito.any()
                );

        Mockito.when(repositoryCarrinho.findByUsuarioAndStatus(Mockito.any(), Mockito.any()))
                .thenReturn(
                        Optional.empty()
                );

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução
        boolean finaliza = service.finaliza("tokenTeste");

        // avaliação
        verify(serviceToken, times(1)).pegaJwt(Mockito.any());
        verify(serviceToken, times(1)).pegaUsuario(Mockito.any());
        verify(clientUsuario, times(1)).usuarioExiste(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).findByUsuarioAndStatus(Mockito.any(), Mockito.any());
        verify(repositoryCarrinho, times(0)).save(Mockito.any());

        Assertions.assertFalse(finaliza);
    }

    @ParameterizedTest
    @MethodSource("requestValidandoCampos")
    public void insere_camposInvalidos_naoSalvaNaBaseDeDados(Long ean,
                                                             Long quantidade) {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução e avaliação
        var excecao = Assertions.assertThrows(RuntimeException.class, () -> {
            service.insere(
                    new AdicionaItemDTO(
                            ean,
                            quantidade
                    ),
                    "tokenTeste"
            );
        });
        verifyNoInteractions(clientItem);
        verifyNoInteractions(serviceToken);
        verifyNoInteractions(clientUsuario);
        verifyNoInteractions(repositoryCarrinho);
        verifyNoInteractions(repositoryItensNoCarrinho);
    }

    @ParameterizedTest
    @ValueSource(longs = {
            -1000,
            -1L,
            0
    })
    public void remove_camposInvalidos_naoBuscaNaBaseDeDados(Long ean) {
        // preparação
        var clientItem = Mockito.mock(ItemClient.class);
        var clientUsuario = Mockito.mock(UsuarioClient.class);
        var repositoryCarrinho = Mockito.mock(CarrinhoRepository.class);
        var repositoryItensNoCarrinho = Mockito.mock(ItensNoCarrinhoRepository.class);
        var serviceToken = Mockito.mock(TokenUseCaseImpl.class);

        var service = new CarrinhoUseCaseImpl(clientItem, clientUsuario, repositoryCarrinho, repositoryItensNoCarrinho, serviceToken);

        // execução e avaliação
        var excecao = Assertions.assertThrows(RuntimeException.class, () -> {
            service.remove(
                    ean,
                    "tokenTeste"
            );
        });
        verifyNoInteractions(clientItem);
        verifyNoInteractions(serviceToken);
        verifyNoInteractions(clientUsuario);
        verifyNoInteractions(repositoryCarrinho);
        verifyNoInteractions(repositoryItensNoCarrinho);
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
