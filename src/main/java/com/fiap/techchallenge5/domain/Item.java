package com.fiap.techchallenge5.domain;

import java.util.Objects;


public record Item(
    Long ean,
    Long quantidade
) {

        public Item {
            if (Objects.isNull(quantidade) || (quantidade <= 0 || quantidade > 1000)) {
                throw new IllegalArgumentException("QUANTIDADE NAO PODE SER NULO OU MENOR E IGUAL A ZERO E MAIOR QUE 1000!");
            }

            ean = new Ean(ean).numero();
        }
}
