package com.fiap.techchallenge5.bdd;

import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.AdicionaItemDTO;
import com.fiap.techchallenge5.integrados.JwtUtil;
import io.cucumber.java.After;
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

import java.util.Objects;

import static com.fiap.techchallenge5.infrastructure.carrinho.controller.CarrinhoController.URL_CARRINHO;
import static com.fiap.techchallenge5.infrastructure.carrinho.controller.CarrinhoController.URL_CARRINHO_COM_EAN;
import static io.restassured.RestAssured.given;


public class RemoveItemSteps {

    private Response response;
    private AdicionaItemDTO request;
    private Long ean;
    private String token;
    private ClientAndServer mockServerItem;
    private ClientAndServer mockServerUsuario;

    @After
    public void finalizaTestePorTeste() {
        if(Objects.nonNull(this.mockServerItem)) {
            this.mockServerItem.stop();
        }
        if(Objects.nonNull(this.mockServerUsuario)) {
            this.mockServerUsuario.stop();
        }
    }

    @Dado("que removo um item no carrinho que já tem um item")
    public void queRemovoUmItemNoCarrinhoQueJaTemUmItem() {
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

        this.request = new AdicionaItemDTO(
                novoEan,
                2L
        );
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post(URL_CARRINHO);

        this.ean = novoEan;
    }

    @Dado("que removo um item que não tem carrinho para o usuário ainda")
    public void queRemovoUmItemQueNaoTemCarrinhoParaOUsuarioAinda() {
        this.token = JwtUtil.geraJwt();
        this.ean = System.currentTimeMillis();

        this.mockServerItem = this.criaMockServerItem(this.ean, 111111L);
        this.mockServerUsuario = this.criaMockServerUsuario(this.token);
    }

    @Dado("que removo um item que não tem no carrinho")
    public void queRemovoUmItemQueNaoTemNoCarrinho() {
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
    }

    @Dado("que removo o último item no carrinho")
    public void queRemovoOUltimoItemNoCarrinho() {
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
    }

    @Dado("que removo um item que não esta cadastrado no sistema")
    public void queRemovoUmItemQueNaoEstaCadastradoNoSistema() {
        this.token = JwtUtil.geraJwt();
        this.ean = 33333L;

        this.mockServerItem = this.criaMockServerItem(22222L, 11111L);
        this.mockServerUsuario = this.criaMockServerUsuario(this.token);
    }

    @Dado("que removo um item com um usuário que não existe no sistema")
    public void queRemovoUmItemComUmUsuarioQueNaoExisteNoSistema() {
        this.ean = System.currentTimeMillis();

        this.token = JwtUtil.geraJwt("USER", "novoLogin");

        this.mockServerItem = this.criaMockServerItem(this.ean, 11111L);
        this.mockServerUsuario = this.criaMockServerUsuario(this.token);
    }

    @Quando("removo o item no carrinho")
    public void removoOItemNoCarrinho() {
        RestAssured.baseURI = "http://localhost:8082";
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(URL_CARRINHO_COM_EAN.replace("{ean}", this.ean.toString()));
    }

    @Entao("recebo uma resposta que o item foi removido com sucesso")
    public void receboUmaRespostaQueOItemFoiRemovidoComSucesso() {
        this.response
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
        ;

    }

    @Entao("recebo uma resposta que o item não foi removido")
    public void receboUmaRespostaQueOItemNaoFoiRemovido() {
        this.response
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
        ;

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
