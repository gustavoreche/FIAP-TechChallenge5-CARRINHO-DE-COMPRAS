# language: pt

Funcionalidade: Teste de inserção de itens no carrinho

  Cenário: Insere item com carrinho vazio
    Dado que insiro um item no carrinho vazio
    Quando insiro o item no carrinho
    Entao recebo uma resposta que o item foi inserido com sucesso

  Cenário: Insere item com carrinho com item
    Dado que insiro um item no carrinho que já tem um item
    Quando insiro o item no carrinho
    Entao recebo uma resposta que o item foi inserido com sucesso

  Cenário: Insere item que não esta no cadastro do sistema
    Dado que insiro um item que não esta cadastrado no sistema
    Quando insiro o item no carrinho
    Entao recebo uma resposta que o item não foi inserido

  Cenário: Insere item com usuário que não existe no sistema
    Dado que insiro um item com um usuário que não existe no sistema
    Quando insiro o item no carrinho
    Entao recebo uma resposta que o item não foi inserido
