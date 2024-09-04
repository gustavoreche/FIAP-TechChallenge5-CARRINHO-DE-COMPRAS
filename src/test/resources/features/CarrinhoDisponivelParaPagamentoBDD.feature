# language: pt

Funcionalidade: Teste de verificação se carrinho esta disponivel para pagamento

  Cenário: Verifica carrinho disponivel para pagamento
    Dado que verifico um carrinho que esteja disponivel para pagamento
    Quando verifico esse carrinho
    Entao recebo uma resposta que o carrinho esta disponivel para pagamento

  Cenário: Verifica carrinho já finalizado
    Dado que verifico um carrinho que ja esteja finalizado
    Quando verifico esse carrinho
    Entao recebo uma resposta que o carrinho não esta disponivel para pagamento

  Cenário: Verifica carrinho não existe
    Dado que verifico um carrinho que não existe
    Quando verifico esse carrinho
    Entao recebo uma resposta que o carrinho não esta disponivel para pagamento

  Cenário: Verifica carrinho com usuário que não existe no sistema
    Dado que verifico um carrinho com um usuário que não existe no sistema
    Quando verifico esse carrinho
    Entao recebo uma resposta que o carrinho não esta disponivel para pagamento
