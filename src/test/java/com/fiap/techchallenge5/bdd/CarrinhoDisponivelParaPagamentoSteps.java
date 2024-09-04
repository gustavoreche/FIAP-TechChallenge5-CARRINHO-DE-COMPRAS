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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.Objects;

import static com.fiap.techchallenge5.infrastructure.carrinho.controller.CarrinhoController.URL_CARRINHO;
import static com.fiap.techchallenge5.infrastructure.carrinho.controller.CarrinhoController.URL_CARRINHO_DISPONIVEL_PARA_PAGAMENTO;
import static io.restassured.RestAssured.given;


public class CarrinhoDisponivelParaPagamentoSteps {

    private final JdbcTemplate jdbcTemplate = this.criaConexaoComBaseDeDados();
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

    @Dado("que verifico um carrinho que esteja disponivel para pagamento")
    public void queVerificoUmCarrinhoQueEstejaDisponivelParaPagamento() {
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

    @Dado("que verifico um carrinho que ja esteja finalizado")
    public void queVerificoUmCarrinhoQueJaEstejaFinalizado() {
        this.token = JwtUtil.geraJwt("USER", "loginCarrinhoFinalizado");;
        this.ean = System.currentTimeMillis();

        final var novoEan = this.ean + 333333L;

        this.mockServerItem = this.criaMockServerItem(this.ean, novoEan);
        this.mockServerUsuario = this.criaMockServerUsuario(this.token);

        jdbcTemplate.execute("""
                INSERT INTO tb_carrinho (data_de_criacao,status,usuario,valor_total) VALUES
                	 ('2024-06-26 22:57:46.037','FINALIZADO','loginCarrinhoFinalizado',30.00);
                """
        );
    }

    @Dado("que verifico um carrinho que não existe")
    public void queVerificoUmCarrinhoQueNaoExiste() {
        this.token = JwtUtil.geraJwt("USER", "loginSemCarrinho");

        this.mockServerItem = this.criaMockServerItem(22222L, 11111L);
        this.mockServerUsuario = this.criaMockServerUsuario(this.token);
    }

    @Dado("que verifico um carrinho com um usuário que não existe no sistema")
    public void queVerificoUmCarrinhoComUmUsuarioQueNaoExisteNoSistema() {
        this.ean = System.currentTimeMillis();
        this.token = JwtUtil.geraJwt();

        this.mockServerItem = this.criaMockServerItem(this.ean, 11111L);
        this.mockServerUsuario = this.criaMockServerUsuario(JwtUtil.geraJwt("USER", "novoLogin"));
    }

    @Quando("verifico esse carrinho")
    public void verificoEsseCarrinho() {
        RestAssured.baseURI = "http://localhost:8082";
        this.response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + this.token)
                .when()
                .get(URL_CARRINHO_DISPONIVEL_PARA_PAGAMENTO);
    }

    @Entao("recebo uma resposta que o carrinho esta disponivel para pagamento")
    public void receboUmaRespostaQueOCarrinhoEstaDisponivelParaPagamento() {
        this.response
                .prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value())
        ;

    }

    @Entao("recebo uma resposta que o carrinho não esta disponivel para pagamento")
    public void receboUmaRespostaQueOCarrinhoNaoEstaDisponivelParaPagamento() {
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

        clientAndServer.when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/usuario/loginSemCarrinho")
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
                                .withPath("/usuario/loginCarrinhoFinalizado")
                                .withHeader("Authorization", "Bearer " + token)
                )
                .respond(
                        HttpResponse.response()
                                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                                .withStatusCode(200)
                                .withBody(String.valueOf(true))
                );

        return clientAndServer;
    }

    private JdbcTemplate criaConexaoComBaseDeDados() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5435/tech_challenge_5_carrinho_de_compras");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return new JdbcTemplate(dataSource);
    }

}
