package com.fiap.techchallenge5.bdd;

import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.AdicionaItemDTO;
import com.fiap.techchallenge5.integrados.JwtUtil;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static com.fiap.techchallenge5.infrastructure.carrinho.controller.CarrinhoController.URL_CARRINHO;
import static io.restassured.RestAssured.given;


public class InsereItemSteps {

    private Response response;
    private AdicionaItemDTO request;
    private Long ean;
    private String token;
    private ClientAndServer mockServerItem;
    private ClientAndServer mockServerUsuario;

    @Dado("que insiro um item no carrinho vazio")
    public void queInsiroUmItemNoCarrinhoVazio() {
        this.token = JwtUtil.geraJwt();
        this.ean = System.currentTimeMillis();
        this.request = new AdicionaItemDTO(
                this.ean,
                2L
        );

        this.mockServerItem = this.criaMockServerItem(this.ean, 11111L);
        this.mockServerUsuario = this.criaMockServerUsuario(this.token);
    }

    @Dado("que insiro um item no carrinho que já tem um item")
    public void queInsiroUmItemNoCarrinhoQueJaTemUmItem() {
        this.token = JwtUtil.geraJwt();
        this.ean = System.currentTimeMillis();
        this.request = new AdicionaItemDTO(
                this.ean,
                2L
        );

        final var novoEan = this.ean + 333333L;

        this.mockServerItem = this.criaMockServerItem(this.ean, novoEan);
        this.mockServerUsuario = this.criaMockServerUsuario(this.token);

        RestAssured.baseURI = "http://localhost:8082";
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post(URL_CARRINHO);

        this.ean = novoEan;
        this.request = new AdicionaItemDTO(
                this.ean,
                3L
        );
    }

    @Dado("que insiro um item que não esta cadastrado no sistema")
    public void queInsiroUmItemQueNaoEstaCadastradoNoSistema() {
        this.token = JwtUtil.geraJwt();
        this.request = new AdicionaItemDTO(
                33333L,
                2L
        );

        this.mockServerItem = this.criaMockServerItem(22222L, 11111L);
        this.mockServerUsuario = this.criaMockServerUsuario(this.token);
    }

    @Dado("que insiro um item com um usuário que não existe no sistema")
    public void queInsiroUmItemComUmUsuarioQueNaoExisteNoSistema() {
        this.ean = System.currentTimeMillis();
        this.request = new AdicionaItemDTO(
                this.ean,
                2L
        );

        this.token = JwtUtil.geraJwt();

        this.mockServerItem = this.criaMockServerItem(this.ean, 11111L);
        this.mockServerUsuario = this.criaMockServerUsuario(JwtUtil.geraJwt("USER", "novoLogin"));
    }

    @Quando("insiro o item no carrinho")
    public void insiroOItemNoCarrinho() {
        RestAssured.baseURI = "http://localhost:8082";
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + token)
                .body(this.request)
                .when()
                .post(URL_CARRINHO);
    }

    @Entao("recebo uma resposta que o item foi inserido com sucesso")
    public void receboUmaRespostaQueOItemFoiInseridoComSucesso() {
        this.response
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.CREATED.value())
        ;

        this.mockServerItem.stop();
        this.mockServerUsuario.stop();

    }

    @Entao("recebo uma resposta que o item não foi inserido")
    public void receboUmaRespostaQueOItemNaoFoiInserido() {
        this.response
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
        ;

        this.mockServerItem.stop();
        this.mockServerUsuario.stop();
    }

    private ClientAndServer criaMockServerItem(final Long ean,
                                               final Long novoEan) {
        final var clientAndServer = ClientAndServer.startClientAndServer(8081);

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/item/{ean}".replace("{ean}", ean.toString()))
                                .withHeader("Authorization", "Bearer " + this.token)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody("""
                                        {
                                            "ean": %s,
                                            "preco": 100.00
                                        }
                                        """.formatted(ean))
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/item/{ean}".replace("{ean}", novoEan.toString()))
                                .withHeader("Authorization", "Bearer " + this.token)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody("""
                                        {
                                            "ean": %s,
                                            "preco": 100.00
                                        }
                                        """.formatted(novoEan))
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/item/33333")
                                .withHeader("Authorization", "Bearer " + this.token)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withStatusCode(204)
                );

        return clientAndServer;
    }

    private ClientAndServer criaMockServerUsuario(final String token) {
        final var clientAndServer = ClientAndServer.startClientAndServer(8080);

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/usuario/teste")
                                .withHeader("Authorization", "Bearer " + token)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody(String.valueOf(true))
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/usuario/novoLogin")
                                .withHeader("Authorization", "Bearer " + token)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withStatusCode(204)
                );

        return clientAndServer;
    }

}
