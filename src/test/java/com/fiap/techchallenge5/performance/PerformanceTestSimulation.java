package com.fiap.techchallenge5.performance;

import com.fiap.techchallenge5.integrados.JwtUtil;
import io.gatling.javaapi.core.ActionBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.extern.slf4j.Slf4j;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;


@Slf4j
public class PerformanceTestSimulation extends Simulation {

    private final String token = JwtUtil.geraJwt();
    private final String tokenTeste1 = "Bearer " + this.token;
    private final String tokenTeste2 = "Bearer " + JwtUtil.geraJwt("USER", "novoLogin");
    private final String usuarioDoToken3 = "loginDoGET"+System.currentTimeMillis();
    private final String tokenTeste3 = "Bearer " + JwtUtil.geraJwt("USER", usuarioDoToken3);
    private final String tokenTeste4 = "Bearer " + JwtUtil.geraJwt("USER", "tokenFinaliza");
    private final ClientAndServer mockServerItem = this.criaMockServerItem(this.tokenTeste1, this.tokenTeste2, this.tokenTeste3, this.tokenTeste4);
    private final ClientAndServer mockServerUsuario = this.criaMockServerUsuario(this.tokenTeste1, this.tokenTeste2, this.tokenTeste3, this.tokenTeste4);
    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8082");

    ActionBuilder insereItemNoCarrinhoRequest = http("insere item no carrinho")
            .post("/carrinho")
            .header("Content-Type", "application/json")
            .header("Authorization", "${token}")
            .body(StringBody("""
                              {
                                "ean": ${ean},
                                "quantidade": 1
                              }
                    """))
            .check(status().is(201));

    ActionBuilder removeItemNoCarrinhoRequest = http("remove item no carrinho")
            .delete("/carrinho/456")
            .header("Content-Type", "application/json")
            .header("Authorization", this.tokenTeste2)
            .check(status().is(200));

    ActionBuilder carrinhoDisponivelParaPagamentoRequest = http("carrinho disponivel para pagamento")
            .get("/carrinho/disponivel-para-pagamento")
            .header("Content-Type", "application/json")
            .header("Authorization", this.tokenTeste3)
            .check(status().is(200));

    ActionBuilder finalizaCarrinhoRequest = http("finaliza carrinho")
            .put("/carrinho/finaliza")
            .header("Content-Type", "application/json")
            .header("Authorization", this.tokenTeste4)
            .check(status().is(200));

    ScenarioBuilder cenarioInsereItemNoCarrinho = scenario("Insere item no carrinho")
            .exec(session -> {
                Map<String, Object> sessions = new HashMap<>();
                sessions.put("ean", 123);
                sessions.put("token", this.tokenTeste1);
                return session.setAll(sessions);
            })
            .exec(insereItemNoCarrinhoRequest);

    ScenarioBuilder cenarioRemoveItemNoCarrinho = scenario("Remove item no carrinho")
            .exec(session -> {
                Map<String, Object> sessions = new HashMap<>();
                sessions.put("ean", 456);
                sessions.put("token", this.tokenTeste2);
                return session.setAll(sessions);
            })
            .exec(insereItemNoCarrinhoRequest)
            .exec(removeItemNoCarrinhoRequest);

    ScenarioBuilder cenarioCarrinhoDisponivelParaPagamento = scenario("Carrinho disponivel para pagamento")
            .exec(session -> {
                Map<String, Object> sessions = new HashMap<>();
                sessions.put("ean", 789);
                sessions.put("token", this.tokenTeste3);
                return session.setAll(sessions);
            })
            .exec(insereItemNoCarrinhoRequest)
            .exec(carrinhoDisponivelParaPagamentoRequest);

    ScenarioBuilder cenarioFinalizaCarrinho = scenario("Finaliza carrinho")
            .exec(session -> {
                Map<String, Object> sessions = new HashMap<>();
                sessions.put("ean", 333);
                sessions.put("token", this.tokenTeste4);
                return session.setAll(sessions);
            })
            .exec(insereItemNoCarrinhoRequest)
            .exec(finalizaCarrinhoRequest);

    {

        setUp(
                cenarioInsereItemNoCarrinho.injectOpen(
                        rampUsersPerSec(1)
                                .to(10)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(10)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10)
                                .to(1)
                                .during(Duration.ofSeconds(10))),
                cenarioRemoveItemNoCarrinho.injectOpen(
                        rampUsersPerSec(1)
                                .to(10)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(10)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10)
                                .to(1)
                                .during(Duration.ofSeconds(10))),
                cenarioCarrinhoDisponivelParaPagamento.injectOpen(
                        rampUsersPerSec(1)
                                .to(10)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(10)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10)
                                .to(1)
                                .during(Duration.ofSeconds(10))),
                cenarioFinalizaCarrinho.injectOpen(
                        rampUsersPerSec(1)
                                .to(10)
                                .during(Duration.ofSeconds(10)),
                        constantUsersPerSec(10)
                                .during(Duration.ofSeconds(20)),
                        rampUsersPerSec(10)
                                .to(1)
                                .during(Duration.ofSeconds(10)))
        )
                .protocols(httpProtocol)
                .assertions(
                        global().responseTime().max().lt(600),
                        global().failedRequests().count().is(0L));

    }

    private ClientAndServer criaMockServerItem(final String token1,
                                               final String token2,
                                               final String token3,
                                               final String token4) {
        final var clientAndServer = ClientAndServer.startClientAndServer(8081);

        clientAndServer.when(
               HttpRequest.request()
                       .withMethod("GET")
                       .withPath("/item/123")
                       .withHeader("Authorization", token1)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody("""
                                        {
                                            "ean": 123,
                                            "preco": 100.00
                                        }
                                        """)
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/item/456")
                                .withHeader("Authorization", token2)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody("""
                                        {
                                            "ean": 456,
                                            "preco": 100.00
                                        }
                                        """)
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/item/789")
                                .withHeader("Authorization", token3)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody("""
                                        {
                                            "ean": 789,
                                            "preco": 150.00
                                        }
                                        """)
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/item/333")
                                .withHeader("Authorization", token4)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody("""
                                        {
                                            "ean": 333,
                                            "preco": 250.00
                                        }
                                        """)
                );

        return clientAndServer;
    }

    private ClientAndServer criaMockServerUsuario(final String token1,
                                                  final String token2,
                                                  final String token3,
                                                  final String token4) {
        final var clientAndServer = ClientAndServer.startClientAndServer(8080);

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/usuario/teste")
                                .withHeader("Authorization", token1)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody(String.valueOf(true))
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/usuario/novoLogin")
                                .withHeader("Authorization", token2)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody(String.valueOf(true))
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/usuario/"+usuarioDoToken3)
                                .withHeader("Authorization", token3)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody(String.valueOf(true))
                );

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/usuario/tokenFinaliza")
                                .withHeader("Authorization", token4)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody(String.valueOf(true))
                );

        return clientAndServer;
    }

}