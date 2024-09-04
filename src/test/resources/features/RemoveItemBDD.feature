# language: pt

Funcionalidade: Teste de remoção de itens no carrinho

  Cenário: Remove item com carrinho com item
    Dado que removo um item no carrinho que já tem um item
    Quando removo o item no carrinho
    Entao recebo uma resposta que o item foi removido com sucesso

  Cenário: Remove item com carrinho não existente
    Dado que removo um item que não tem carrinho para o usuário ainda
    Quando removo o item no carrinho
    Entao recebo uma resposta que o item não foi removido

  Cenário: Remove item que não tem no carrinho
    Dado que removo um item que não tem no carrinho
    Quando removo o item no carrinho
    Entao recebo uma resposta que o item não foi removido

  Cenário: Remove último item do carrinho
    Dado que removo o último item no carrinho
    Quando removo o item no carrinho
    Entao recebo uma resposta que o item foi removido com sucesso

  Cenário: Remove item que não esta no cadastro do sistema
    Dado que removo um item que não esta cadastrado no sistema
    Quando removo o item no carrinho
    Entao recebo uma resposta que o item não foi removido

  Cenário: Remove item com usuário que não existe no sistema
    Dado que removo um item com um usuário que não existe no sistema
    Quando removo o item no carrinho
    Entao recebo uma resposta que o item não foi removido