package com.fiap.techchallenge5.integrados;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.techchallenge5.domain.StatusEnum;
import com.fiap.techchallenge5.infrastructure.carrinho.controller.dto.AdicionaItemDTO;
import com.fiap.techchallenge5.infrastructure.carrinho.model.CarrinhoEntity;
import com.fiap.techchallenge5.infrastructure.carrinho.model.ItensNoCarrinhoEntity;
import com.fiap.techchallenge5.infrastructure.carrinho.model.ItensNoCarrinhoId;
import com.fiap.techchallenge5.infrastructure.carrinho.repository.CarrinhoRepository;
import com.fiap.techchallenge5.infrastructure.carrinho.repository.ItensNoCarrinhoRepository;
import com.fiap.techchallenge5.infrastructure.item.client.ItemClient;
import com.fiap.techchallenge5.infrastructure.item.client.response.ItemDTO;
import com.fiap.techchallenge5.infrastructure.usuario.client.UsuarioClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static com.fiap.techchallenge5.infrastructure.carrinho.controller.CarrinhoController.*;


@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CarrinhoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @MockBean
    ItemClient clientItem;

    @Autowired
    @MockBean
    UsuarioClient clientUsuario;

    @Autowired
    CarrinhoRepository repositoryCarrinho;

    @Autowired
    ItensNoCarrinhoRepository repositoryItensNoCarrinho;

    @Autowired
    private ObjectMapper objectMapper;

    private final String token = JwtUtil.geraJwt();

    @BeforeEach
    void inicializaLimpezaDoDatabase() {
        this.repositoryCarrinho.deleteAll();
        this.repositoryItensNoCarrinho.deleteAll();
    }

    @AfterAll
    void finalizaLimpezaDoDatabase() {
        this.repositoryCarrinho.deleteAll();
        this.repositoryItensNoCarrinho.deleteAll();
    }

    @Test
    public void insere_deveRetornar201_carrinhoVazio_salvaNaBaseDeDados() throws Exception {

        Mockito.when(this.clientItem.pegaItem(7894900011517L, "Bearer " + this.token))
                .thenReturn(
                        new ItemDTO(
                                7894900011517L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                        );

        var request = new AdicionaItemDTO(
                7894900011517L,
                5L
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_CARRINHO)
                        .header("Authorization", "Bearer " + this.token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isCreated()
                );

        var carrinho = this.repositoryCarrinho.findAll().get(0);
        var itensDoCarrinho = this.repositoryItensNoCarrinho.findAll();

        Assertions.assertEquals(StatusEnum.ABERTO, carrinho.getStatus());
        Assertions.assertEquals("teste", carrinho.getUsuario());
        Assertions.assertEquals(new BigDecimal("500.00"), carrinho.getValorTotal());
        Assertions.assertNotNull(carrinho.getDataDeCriacao());

        Assertions.assertEquals(1, itensDoCarrinho.size());
        Assertions.assertEquals(new BigDecimal("500.00"), itensDoCarrinho.get(0).getPrecoTotal());
        Assertions.assertEquals(7894900011517L, itensDoCarrinho.get(0).getId().getEan());
        Assertions.assertEquals(carrinho.getId(), itensDoCarrinho.get(0).getId().getIdCarrinho());
    }

    @Test
    public void insere_deveRetornar201_carrinhoComItens_salvaNaBaseDeDados() throws Exception {
        final var carrinhoSalvo = this.repositoryCarrinho.save(
                CarrinhoEntity.builder()
                        .usuario("teste")
                        .status(StatusEnum.ABERTO)
                        .dataDeCriacao(LocalDateTime.now())
                        .valorTotal(new BigDecimal("500.00"))
                        .build()
        );
        this.repositoryItensNoCarrinho.save(
                ItensNoCarrinhoEntity.builder()
                        .id(ItensNoCarrinhoId.builder()
                                .idCarrinho(carrinhoSalvo.getId())
                                .ean(7894900011517L)
                                .build())
                        .precoTotal(new BigDecimal("500.00"))
                        .build()
        );

        Mockito.when(this.clientItem.pegaItem(123456L, "Bearer " + this.token))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("50.00")
                        )
                );

        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        var request = new AdicionaItemDTO(
                123456L,
                3L
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_CARRINHO)
                        .header("Authorization", "Bearer " + this.token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isCreated()
                );

        var carrinho = this.repositoryCarrinho.findAll().get(0);
        var itensDoCarrinho = this.repositoryItensNoCarrinho.findAll();

        Assertions.assertEquals(StatusEnum.ABERTO, carrinho.getStatus());
        Assertions.assertEquals("teste", carrinho.getUsuario());
        Assertions.assertEquals(new BigDecimal("650.00"), carrinho.getValorTotal());
        Assertions.assertNotNull(carrinho.getDataDeCriacao());

        Assertions.assertEquals(2, itensDoCarrinho.size());
    }

    @Test
    public void insere_deveRetornar409_itemNaoEncontrado_naoSalvaNaBaseDeDados() throws Exception {

        Mockito.when(this.clientItem.pegaItem(7894900011517L, "Bearer " + this.token))
                .thenReturn(
                        new ItemDTO(
                                7894900011517L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        var request = new AdicionaItemDTO(
                1111L,
                5L
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_CARRINHO)
                        .header("Authorization", "Bearer " + this.token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isConflict()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void insere_deveRetornar409_usuarioNaoExiste_naoSalvaNaBaseDeDados() throws Exception {

        Mockito.when(this.clientItem.pegaItem(7894900011517L, "Bearer " + this.token))
                .thenReturn(
                        new ItemDTO(
                                7894900011517L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(this.clientUsuario.usuarioExiste("testeeeee", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        var request = new AdicionaItemDTO(
                7894900011517L,
                5L
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_CARRINHO)
                        .header("Authorization", "Bearer " + this.token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isConflict()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void insere_deveRetornar401_semToken() throws Exception {

        var request = new AdicionaItemDTO(
                7894900011517L,
                1L
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_CARRINHO)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void insere_deveRetornar401_tokenInvalido() throws Exception {

        var request = new AdicionaItemDTO(
                7894900011517L,
                1L
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_CARRINHO)
                        .header("Authorization", "Bearer TESTE")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void insere_deveRetornar401_tokenExpirado() throws Exception {

        var request = new AdicionaItemDTO(
                7894900011517L,
                1L
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_CARRINHO)
                        .header("Authorization", "Bearer " + JwtUtil.geraJwt(LocalDateTime.now()
                                .minusHours(3)
                                .toInstant(ZoneOffset.of("-03:00"))))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void insere_deveRetornar201_carrinhoVazioComRoleUSER_salvaNaBaseDeDados() throws Exception {
        final var tokenUser = JwtUtil.geraJwt("USER", "teste");

        Mockito.when(this.clientItem.pegaItem(7894900011517L, "Bearer " + tokenUser))
                .thenReturn(
                        new ItemDTO(
                                7894900011517L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + tokenUser))
                .thenReturn(
                        true
                );

        var request = new AdicionaItemDTO(
                7894900011517L,
                5L
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_CARRINHO)
                        .header("Authorization", "Bearer " + tokenUser)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isCreated()
                );

        var carrinho = this.repositoryCarrinho.findAll().get(0);
        var itensDoCarrinho = this.repositoryItensNoCarrinho.findAll();

        Assertions.assertEquals(StatusEnum.ABERTO, carrinho.getStatus());
        Assertions.assertEquals("teste", carrinho.getUsuario());
        Assertions.assertEquals(new BigDecimal("500.00"), carrinho.getValorTotal());
        Assertions.assertNotNull(carrinho.getDataDeCriacao());

        Assertions.assertEquals(1, itensDoCarrinho.size());
        Assertions.assertEquals(new BigDecimal("500.00"), itensDoCarrinho.get(0).getPrecoTotal());
        Assertions.assertEquals(7894900011517L, itensDoCarrinho.get(0).getId().getEan());
        Assertions.assertEquals(carrinho.getId(), itensDoCarrinho.get(0).getId().getIdCarrinho());
    }

    @Test
    public void remove_deveRetornar200_carrinhoComItens_salvaNaBaseDeDados() throws Exception {
        final var carrinhoSalvo = this.repositoryCarrinho.save(
                CarrinhoEntity.builder()
                        .usuario("teste")
                        .status(StatusEnum.ABERTO)
                        .dataDeCriacao(LocalDateTime.now())
                        .valorTotal(new BigDecimal("500.00"))
                        .build()
        );
        this.repositoryItensNoCarrinho.save(
                ItensNoCarrinhoEntity.builder()
                        .id(ItensNoCarrinhoId.builder()
                                .idCarrinho(carrinhoSalvo.getId())
                                .ean(7894900011517L)
                                .build())
                        .precoTotal(new BigDecimal("300.00"))
                        .build()
        );
        this.repositoryItensNoCarrinho.save(
                ItensNoCarrinhoEntity.builder()
                        .id(ItensNoCarrinhoId.builder()
                                .idCarrinho(carrinhoSalvo.getId())
                                .ean(123456L)
                                .build())
                        .precoTotal(new BigDecimal("200.00"))
                        .build()
        );

        Mockito.when(this.clientItem.pegaItem(123456L, "Bearer " + this.token))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_CARRINHO_COM_EAN.replace("{ean}", "123456"))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isOk()
                );

        var carrinho = this.repositoryCarrinho.findAll().get(0);
        var itensDoCarrinho = this.repositoryItensNoCarrinho.findAll();

        Assertions.assertEquals(StatusEnum.ABERTO, carrinho.getStatus());
        Assertions.assertEquals("teste", carrinho.getUsuario());
        Assertions.assertEquals(new BigDecimal("300.00"), carrinho.getValorTotal());
        Assertions.assertNotNull(carrinho.getDataDeCriacao());

        Assertions.assertEquals(1, itensDoCarrinho.size());
        Assertions.assertEquals(new BigDecimal("300.00"), itensDoCarrinho.get(0).getPrecoTotal());
        Assertions.assertEquals(7894900011517L, itensDoCarrinho.get(0).getId().getEan());
        Assertions.assertEquals(carrinho.getId(), itensDoCarrinho.get(0).getId().getIdCarrinho());
    }

    @Test
    public void remove_deveRetornar204_carrinhoNaoEncontrado_naoSalvaNaBaseDeDados() throws Exception {
        Mockito.when(this.clientItem.pegaItem(123456L, "Bearer " + this.token))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_CARRINHO_COM_EAN.replace("{ean}", "123456"))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isNoContent()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void remove_deveRetornar204_itemNaoTemNoCarrinho_naoSalvaNaBaseDeDados() throws Exception {
        final var carrinhoSalvo = this.repositoryCarrinho.save(
                CarrinhoEntity.builder()
                        .usuario("teste")
                        .status(StatusEnum.ABERTO)
                        .dataDeCriacao(LocalDateTime.now())
                        .valorTotal(new BigDecimal("300.00"))
                        .build()
        );
        this.repositoryItensNoCarrinho.save(
                ItensNoCarrinhoEntity.builder()
                        .id(ItensNoCarrinhoId.builder()
                                .idCarrinho(carrinhoSalvo.getId())
                                .ean(7894900011517L)
                                .build())
                        .precoTotal(new BigDecimal("300.00"))
                        .build()
        );

        Mockito.when(this.clientItem.pegaItem(123456L, "Bearer " + this.token))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_CARRINHO_COM_EAN.replace("{ean}", "123456"))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isNoContent()
                );

        var carrinho = this.repositoryCarrinho.findAll().get(0);
        var itensDoCarrinho = this.repositoryItensNoCarrinho.findAll();

        Assertions.assertEquals(StatusEnum.ABERTO, carrinho.getStatus());
        Assertions.assertEquals("teste", carrinho.getUsuario());
        Assertions.assertEquals(new BigDecimal("300.00"), carrinho.getValorTotal());
        Assertions.assertNotNull(carrinho.getDataDeCriacao());

        Assertions.assertEquals(1, itensDoCarrinho.size());
        Assertions.assertEquals(new BigDecimal("300.00"), itensDoCarrinho.get(0).getPrecoTotal());
        Assertions.assertEquals(7894900011517L, itensDoCarrinho.get(0).getId().getEan());
        Assertions.assertEquals(carrinho.getId(), itensDoCarrinho.get(0).getId().getIdCarrinho());
    }

    @Test
    public void remove_deveRetornar200_removeUltimoItemDoCarrinho_salvaNaBaseDeDados() throws Exception {
        final var carrinhoSalvo = this.repositoryCarrinho.save(
                CarrinhoEntity.builder()
                        .usuario("teste")
                        .status(StatusEnum.ABERTO)
                        .dataDeCriacao(LocalDateTime.now())
                        .valorTotal(new BigDecimal("200.00"))
                        .build()
        );
        this.repositoryItensNoCarrinho.save(
                ItensNoCarrinhoEntity.builder()
                        .id(ItensNoCarrinhoId.builder()
                                .idCarrinho(carrinhoSalvo.getId())
                                .ean(123456L)
                                .build())
                        .precoTotal(new BigDecimal("200.00"))
                        .build()
        );

        Mockito.when(this.clientItem.pegaItem(123456L, "Bearer " + this.token))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_CARRINHO_COM_EAN.replace("{ean}", "123456"))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isOk()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void remove_deveRetornar204_itemNaoEncontrado_naoSalvaNaBaseDeDados() throws Exception {

        Mockito.when(this.clientItem.pegaItem(7894900011517L, "Bearer " + this.token))
                .thenReturn(
                        new ItemDTO(
                                7894900011517L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_CARRINHO_COM_EAN.replace("{ean}", "1111"))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isNoContent()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void remove_deveRetornar204_usuarioNaoExiste_naoSalvaNaBaseDeDados() throws Exception {

        Mockito.when(this.clientItem.pegaItem(7894900011517L, "Bearer " + this.token))
                .thenReturn(
                        new ItemDTO(
                                7894900011517L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(this.clientUsuario.usuarioExiste("testeeeee", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_CARRINHO_COM_EAN.replace("{ean}", "7894900011517"))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isNoContent()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void remove_deveRetornar401_semToken() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_CARRINHO_COM_EAN.replace("{ean}", "7894900011517"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void remove_deveRetornar401_tokenInvalido() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_CARRINHO_COM_EAN.replace("{ean}", "7894900011517"))
                        .header("Authorization", "Bearer TESTE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void remove_deveRetornar401_tokenExpirado() throws Exception {

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_CARRINHO_COM_EAN.replace("{ean}", "7894900011517"))
                        .header("Authorization", "Bearer " + JwtUtil.geraJwt(LocalDateTime.now()
                                .minusHours(3)
                                .toInstant(ZoneOffset.of("-03:00"))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void remove_deveRetornar200_carrinhoComItensComRoleUSER_salvaNaBaseDeDados() throws Exception {
        final var tokenUser = JwtUtil.geraJwt("USER", "teste");

        final var carrinhoSalvo = this.repositoryCarrinho.save(
                CarrinhoEntity.builder()
                        .usuario("teste")
                        .status(StatusEnum.ABERTO)
                        .dataDeCriacao(LocalDateTime.now())
                        .valorTotal(new BigDecimal("500.00"))
                        .build()
        );
        this.repositoryItensNoCarrinho.save(
                ItensNoCarrinhoEntity.builder()
                        .id(ItensNoCarrinhoId.builder()
                                .idCarrinho(carrinhoSalvo.getId())
                                .ean(7894900011517L)
                                .build())
                        .precoTotal(new BigDecimal("300.00"))
                        .build()
        );
        this.repositoryItensNoCarrinho.save(
                ItensNoCarrinhoEntity.builder()
                        .id(ItensNoCarrinhoId.builder()
                                .idCarrinho(carrinhoSalvo.getId())
                                .ean(123456L)
                                .build())
                        .precoTotal(new BigDecimal("200.00"))
                        .build()
        );

        Mockito.when(this.clientItem.pegaItem(123456L, "Bearer " + tokenUser))
                .thenReturn(
                        new ItemDTO(
                                123456L,
                                new BigDecimal("100.00")
                        )
                );

        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + tokenUser))
                .thenReturn(
                        true
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_CARRINHO_COM_EAN.replace("{ean}", "123456"))
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isOk()
                );

        var carrinho = this.repositoryCarrinho.findAll().get(0);
        var itensDoCarrinho = this.repositoryItensNoCarrinho.findAll();

        Assertions.assertEquals(StatusEnum.ABERTO, carrinho.getStatus());
        Assertions.assertEquals("teste", carrinho.getUsuario());
        Assertions.assertEquals(new BigDecimal("300.00"), carrinho.getValorTotal());
        Assertions.assertNotNull(carrinho.getDataDeCriacao());

        Assertions.assertEquals(1, itensDoCarrinho.size());
        Assertions.assertEquals(new BigDecimal("300.00"), itensDoCarrinho.get(0).getPrecoTotal());
        Assertions.assertEquals(7894900011517L, itensDoCarrinho.get(0).getId().getEan());
        Assertions.assertEquals(carrinho.getId(), itensDoCarrinho.get(0).getId().getIdCarrinho());
    }

    @Test
    public void disponivelParaPagamento_deveRetornar200_sucesso() throws Exception {
        final var carrinhoSalvo = this.repositoryCarrinho.save(
                CarrinhoEntity.builder()
                        .usuario("teste")
                        .status(StatusEnum.ABERTO)
                        .dataDeCriacao(LocalDateTime.now())
                        .valorTotal(new BigDecimal("500.00"))
                        .build()
        );
        this.repositoryItensNoCarrinho.save(
                ItensNoCarrinhoEntity.builder()
                        .id(ItensNoCarrinhoId.builder()
                                .idCarrinho(carrinhoSalvo.getId())
                                .ean(7894900011517L)
                                .build())
                        .precoTotal(new BigDecimal("500.00"))
                        .build()
        );

        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_CARRINHO_DISPONIVEL_PARA_PAGAMENTO)
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isOk()
                );


        var carrinho = this.repositoryCarrinho.findAll().get(0);
        var itensDoCarrinho = this.repositoryItensNoCarrinho.findAll();

        Assertions.assertEquals(StatusEnum.ABERTO, carrinho.getStatus());
        Assertions.assertEquals("teste", carrinho.getUsuario());
        Assertions.assertEquals(new BigDecimal("500.00"), carrinho.getValorTotal());
        Assertions.assertNotNull(carrinho.getDataDeCriacao());
        Assertions.assertEquals(1, itensDoCarrinho.size());
    }

    @Test
    public void disponivelParaPagamento_deveRetornar204_carrinhoFinalizado() throws Exception {
        final var carrinhoSalvo = this.repositoryCarrinho.save(
                CarrinhoEntity.builder()
                        .usuario("teste")
                        .status(StatusEnum.FINALIZADO)
                        .dataDeCriacao(LocalDateTime.now())
                        .valorTotal(new BigDecimal("500.00"))
                        .build()
        );
        this.repositoryItensNoCarrinho.save(
                ItensNoCarrinhoEntity.builder()
                        .id(ItensNoCarrinhoId.builder()
                                .idCarrinho(carrinhoSalvo.getId())
                                .ean(7894900011517L)
                                .build())
                        .precoTotal(new BigDecimal("500.00"))
                        .build()
        );

        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_CARRINHO_DISPONIVEL_PARA_PAGAMENTO)
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isNoContent()
                );


        var carrinho = this.repositoryCarrinho.findAll().get(0);
        var itensDoCarrinho = this.repositoryItensNoCarrinho.findAll();

        Assertions.assertEquals(StatusEnum.FINALIZADO, carrinho.getStatus());
        Assertions.assertEquals("teste", carrinho.getUsuario());
        Assertions.assertEquals(new BigDecimal("500.00"), carrinho.getValorTotal());
        Assertions.assertNotNull(carrinho.getDataDeCriacao());
        Assertions.assertEquals(1, itensDoCarrinho.size());
    }

    @Test
    public void disponivelParaPagamento_deveRetornar204_carrinhonNaoExiste() throws Exception {
        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_CARRINHO_DISPONIVEL_PARA_PAGAMENTO)
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isNoContent()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void disponivelParaPagamento_deveRetornar204_usuarioNaoExiste() throws Exception {
        Mockito.when(this.clientUsuario.usuarioExiste("testeeeee", "Bearer " + this.token))
                .thenReturn(
                        true
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_CARRINHO_DISPONIVEL_PARA_PAGAMENTO)
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isNoContent()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void disponivelParaPagamento_deveRetornar401_semToken() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_CARRINHO_DISPONIVEL_PARA_PAGAMENTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void disponivelParaPagamento_deveRetornar401_tokenInvalido() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_CARRINHO_DISPONIVEL_PARA_PAGAMENTO)
                        .header("Authorization", "Bearer TESTE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void disponivelParaPagamento_deveRetornar401_tokenExpirado() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_CARRINHO_DISPONIVEL_PARA_PAGAMENTO)
                        .header("Authorization", "Bearer " + JwtUtil.geraJwt(LocalDateTime.now()
                                .minusHours(3)
                                .toInstant(ZoneOffset.of("-03:00"))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isUnauthorized()
                );

        Assertions.assertEquals(0, this.repositoryCarrinho.findAll().size());
        Assertions.assertEquals(0, this.repositoryItensNoCarrinho.findAll().size());
    }

    @Test
    public void disponivelParaPagamento_deveRetornar204_carrinhoFinalizadoComRoleUSER() throws Exception {
        final var tokenUser = JwtUtil.geraJwt("USER", "teste");

        final var carrinhoSalvo = this.repositoryCarrinho.save(
                CarrinhoEntity.builder()
                        .usuario("teste")
                        .status(StatusEnum.FINALIZADO)
                        .dataDeCriacao(LocalDateTime.now())
                        .valorTotal(new BigDecimal("500.00"))
                        .build()
        );
        this.repositoryItensNoCarrinho.save(
                ItensNoCarrinhoEntity.builder()
                        .id(ItensNoCarrinhoId.builder()
                                .idCarrinho(carrinhoSalvo.getId())
                                .ean(7894900011517L)
                                .build())
                        .precoTotal(new BigDecimal("500.00"))
                        .build()
        );

        Mockito.when(this.clientUsuario.usuarioExiste("teste", "Bearer " + tokenUser))
                .thenReturn(
                        true
                );

        this.mockMvc
                .perform(MockMvcRequestBuilders.get(URL_CARRINHO_DISPONIVEL_PARA_PAGAMENTO)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isNoContent()
                );


        var carrinho = this.repositoryCarrinho.findAll().get(0);
        var itensDoCarrinho = this.repositoryItensNoCarrinho.findAll();

        Assertions.assertEquals(StatusEnum.FINALIZADO, carrinho.getStatus());
        Assertions.assertEquals("teste", carrinho.getUsuario());
        Assertions.assertEquals(new BigDecimal("500.00"), carrinho.getValorTotal());
        Assertions.assertNotNull(carrinho.getDataDeCriacao());
        Assertions.assertEquals(1, itensDoCarrinho.size());
    }

    @ParameterizedTest
    @MethodSource("requestValidandoCampos")
    public void insere_camposInvalidos_naoBuscaNaBaseDeDados(Long ean,
                                                             Long quantidade) throws Exception {
        var request = new AdicionaItemDTO(
                ean,
                quantidade
        );
        var objectMapper = this.objectMapper
                .writer()
                .withDefaultPrettyPrinter();
        var jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc
                .perform(MockMvcRequestBuilders.post(URL_CARRINHO)
                        .header("Authorization", "Bearer " + this.token)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isBadRequest()
                );
    }

    @ParameterizedTest
    @ValueSource(longs = {
            -1L,
            0
    })
    public void remove_camposInvalidos_naoDeletaNaBaseDeDados(Long ean) throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.delete(URL_CARRINHO_COM_EAN.replace("{ean}", ean.toString()))
                        .header("Authorization", "Bearer " + this.token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .status()
                        .isBadRequest()
                );
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
