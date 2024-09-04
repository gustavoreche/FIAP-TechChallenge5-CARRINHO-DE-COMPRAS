package com.fiap.techchallenge5.domain;

import java.util.Objects;

public record Ean(
        Long numero
) {

    public Ean {
        if (Objects.isNull(numero) || numero <= 0) {
            throw new IllegalArgumentException("EAN NAO PODE SER NULO OU MENOR E IGUAL A ZERO!");
        }
    }

}
