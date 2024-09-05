# language: pt

Funcionalidade: Teste de finalização de carrinho

  Cenário: Finaliza carrinho disponivel para pagamento
    Dado que finalizo um carrinho que esteja disponivel para pagamento
    Quando finalizo esse carrinho
    Entao recebo uma resposta que o carrinho foi finalizado

  Cenário: Finaliza carrinho já finalizado
    Dado que finalizo um carrinho que ja esteja finalizado
    Quando finalizo esse carrinho
    Entao recebo uma resposta que o carrinho não foi finalizado

  Cenário: Finaliza carrinho que não existe
    Dado que finalizo um carrinho que não existe
    Quando finalizo esse carrinho
    Entao recebo uma resposta que o carrinho não foi finalizado

  Cenário: Finaliza carrinho com usuário que não existe no sistema
    Dado que finalizo um carrinho com um usuário que não existe no sistema
    Quando finalizo esse carrinho
    Entao recebo uma resposta que o carrinho não foi finalizado
