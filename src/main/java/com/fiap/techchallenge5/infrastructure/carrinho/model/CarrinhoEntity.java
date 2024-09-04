package com.fiap.techchallenge5.infrastructure.carrinho.model;

import com.fiap.techchallenge5.domain.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_carrinho")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrinhoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String usuario;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    private BigDecimal valorTotal;
    private LocalDateTime dataDeCriacao;

}
