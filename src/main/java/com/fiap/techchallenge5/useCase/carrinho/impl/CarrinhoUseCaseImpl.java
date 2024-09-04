package com.fiap.techchallenge5.useCase.carrinho.impl;

import com.fiap.techchallenge5.domain.Ean;
import com.fiap.techchallenge5.domain.Item;
import com.fiap.techchallenge5.domain.StatusEnum;
import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.AdicionaItemDTO;
import com.fiap.techchallenge5.infrastructure.carrinho.model.CarrinhoEntity;
import com.fiap.techchallenge5.infrastructure.carrinho.model.ItensNoCarrinhoEntity;
import com.fiap.techchallenge5.infrastructure.carrinho.model.ItensNoCarrinhoId;
import com.fiap.techchallenge5.infrastructure.carrinho.repository.CarrinhoRepository;
import com.fiap.techchallenge5.infrastructure.carrinho.repository.ItensNoCarrinhoRepository;
import com.fiap.techchallenge5.infrastructure.item.client.ItemClient;
import com.fiap.techchallenge5.infrastructure.usuario.client.UsuarioClient;
import com.fiap.techchallenge5.useCase.carrinho.CarrinhoUseCase;
import com.fiap.techchallenge5.useCase.token.TokenUseCase;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


@Service
@Slf4j
public class CarrinhoUseCaseImpl implements CarrinhoUseCase {

    private final ItemClient clientItem;
    private final UsuarioClient clientUsuario;
    private final CarrinhoRepository repositoryCarrinho;
    private final ItensNoCarrinhoRepository repositoryItensNoCarrinho;
    private final TokenUseCase serviceToken;

    public CarrinhoUseCaseImpl(final ItemClient clientItem,
                               final UsuarioClient clientUsuario,
                               final CarrinhoRepository repositoryCarrinho,
                               final ItensNoCarrinhoRepository repositoryItensNoCarrinho,
                               final TokenUseCase serviceToken) {
        this.clientItem = clientItem;
        this.clientUsuario = clientUsuario;
        this.repositoryCarrinho = repositoryCarrinho;
        this.repositoryItensNoCarrinho = repositoryItensNoCarrinho;
        this.serviceToken = serviceToken;
    }


    @Override
    @Transactional
    public boolean insere(final AdicionaItemDTO dadosItem,
                          final String token) {
        final var validaItem = new Item(
                dadosItem.ean(),
                dadosItem.quantidade()
        );

        final var item = this.clientItem.pegaItem(validaItem.ean(), token);
        if(Objects.isNull(item)) {
            log.error("Item não encontrado");
            return false;
        }

        final var usuario = this.pegaUsuario(token);
        if(Objects.isNull(usuario)) {
            return false;
        }

        final var carrinho = this.repositoryCarrinho
                .findByUsuarioAndStatus(usuario, StatusEnum.ABERTO);
        if(carrinho.isEmpty()) {
            final var valorTotal = item.preco().multiply(new BigDecimal(validaItem.quantidade()));

            final var carrinhoEntity = CarrinhoEntity.builder()
                    .usuario(usuario)
                    .status(StatusEnum.ABERTO)
                    .dataDeCriacao(LocalDateTime.now())
                    .valorTotal(valorTotal)
                    .build();
            final var carrinhoSalvo = this.repositoryCarrinho.save(carrinhoEntity);

            final var itemEntity = ItensNoCarrinhoEntity.builder()
                    .id(ItensNoCarrinhoId
                            .builder()
                            .idCarrinho(carrinhoSalvo.getId())
                            .ean(item.ean()).build()
                    )
                    .precoTotal(valorTotal)
                    .build();
            this.repositoryItensNoCarrinho.save(itemEntity);

            return true;
        }

        final var carrinhoExistente = carrinho.get();

        final var valorTotalItem = item.preco().multiply(new BigDecimal(validaItem.quantidade()));
        final var itemEntity = ItensNoCarrinhoEntity.builder()
                .id(ItensNoCarrinhoId
                        .builder()
                        .idCarrinho(carrinhoExistente.getId())
                        .ean(item.ean()).build()
                )
                .precoTotal(valorTotalItem)
                .build();
        this.repositoryItensNoCarrinho.save(itemEntity);

        final var valorTotal = this.repositoryItensNoCarrinho.findByIdIdCarrinho(carrinhoExistente.getId())
                .stream()
                .map(ItensNoCarrinhoEntity::getPrecoTotal)
                .reduce(BigDecimal::add)
                .get();

        carrinhoExistente.setValorTotal(valorTotal);
        this.repositoryCarrinho.save(carrinhoExistente);
        return true;
    }

    @Override
    @Transactional
    public boolean remove(final Long ean,
                          final String token) {
        final var validaEan = new Ean(ean);

        final var item = this.clientItem.pegaItem(validaEan.numero(), token);
        if(Objects.isNull(item)) {
            log.error("Item não encontrado");
            return false;
        }

        final var usuario = this.pegaUsuario(token);
        if(Objects.isNull(usuario)) {
            return false;
        }

        final var carrinho = this.repositoryCarrinho
                .findByUsuarioAndStatus(usuario, StatusEnum.ABERTO);
        if(carrinho.isEmpty()) {
            log.error("Carrinho não encontrado");
            return false;
        }

        final var carrinhoExistente = carrinho.get();
        final var itensDoCarrinho = this.repositoryItensNoCarrinho.findByIdIdCarrinho(carrinhoExistente.getId());
        long count = itensDoCarrinho
                .stream()
                .filter(itemDoCarrinho -> itemDoCarrinho.getId().getEan().equals(validaEan.numero()))
                .count();
        if(count == 0) {
            log.error("Item não encontrado no carrinho");
            return false;
        }

        final var itemEntity = ItensNoCarrinhoEntity.builder()
                .id(ItensNoCarrinhoId
                        .builder()
                        .idCarrinho(carrinhoExistente.getId())
                        .ean(item.ean()).build()
                )
                .build();
        this.repositoryItensNoCarrinho.delete(itemEntity);

        final var valorTotal = this.repositoryItensNoCarrinho.findByIdIdCarrinho(carrinhoExistente.getId())
                .stream()
                .map(ItensNoCarrinhoEntity::getPrecoTotal)
                .reduce(BigDecimal::add);
        if(valorTotal.isPresent()) {
            carrinhoExistente.setValorTotal(valorTotal.get());
            this.repositoryCarrinho.save(carrinhoExistente);
            return true;
        }
        log.info("Carrinho zerado");
        this.repositoryCarrinho.delete(carrinhoExistente);
        return true;
    }

    @Override
    public boolean disponivelParaPagamento(String token) {
        final var usuario = this.pegaUsuario(token);
        if(Objects.isNull(usuario)) {
            return false;
        }

        final var carrinho = this.repositoryCarrinho
                .findByUsuarioAndStatus(usuario, StatusEnum.ABERTO);
        if(carrinho.isEmpty()) {
            log.error("Carrinho não encontrado");
            return false;
        }

        return true;
    }

    private String pegaUsuario(final String token) {
        final var jwt = this.serviceToken.pegaJwt(token.replace("Bearer ", ""));
        if(Objects.isNull(jwt)){
            log.error("Token inválido");
            return null;
        }

        final var usuario = this.serviceToken.pegaUsuario(jwt);
        try {
            final var usuarioExiste = this.clientUsuario.usuarioExiste(usuario, token);
            if(Objects.isNull(usuarioExiste) || !usuarioExiste) {
                log.error("Usuario não encontrado");
                return null;
            }
        } catch (Exception e) {
            log.error("Erro ao buscar usuário ", e);
            return null;
        }
        return usuario;
    }

}
