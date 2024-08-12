# ServiServe

**ServiServe** é uma aplicação desenvolvida para modernizar e otimizar os processos de gestão de comandas em restaurantes e bares. O objetivo principal é proporcionar uma experiência mais eficiente tanto para os clientes quanto para os funcionários.

## Funcionalidades

### 1. Autenticação e Autorização
- **Usuários e Papéis**: Autenticação e autorização de usuários com papéis específicos: cozinheiro, gerente, caixa e garçom.


### 2. Realizar Pedido
- Após autenticação, garçons e gerentes podem selecionar mesas e realizar pedidos.
- O sistema exibe um menu com itens disponíveis, permitindo a seleção e especificação da quantidade de cada item.
- Pedidos são vinculados a uma conta com status "Aberta" e atualizados para "Solicitado" até a aprovação da cozinha.


### 3. Gerenciar Produtos
- Gerentes e cozinheiros podem adicionar, remover e alterar produtos.


### 4. Visualizar Pedidos
- Cozinheiros visualizam pedidos abertos para gerenciar a preparação.
- Gerentes e garçons visualizam pedidos por mesa, incluindo detalhes dos itens e total da conta.


### 5. Cancelar Pedido e Itens do Pedido
- Gerentes e garçons podem cancelar pedidos ou itens específicos dentro de um pedido.
- O sistema atualiza o status do pedido para "Cancelado" ou "Alterado" conforme necessário.

### 6. Alterar Status do Pedido
- Cozinha pode alterar status do pedido para "Aceito", "Preparando", "Finalizado" ou "Cancelado".

### 7. Solicitar Conta
- Garçom ou gerente informa o cliente sobre o total da conta após seleção da mesa correspondente no sistema e solicita a conta para posterior pagamento com o caixa.

### 8. Fechamento do Caixa
- No final do expediente, o caixa pode acessar o "Relatório Financeiro", detalhando as receitas recebidas por método de pagamento.

### 9. Gerenciamento de Funcionários
- Gerentes administram informações de funcionários, incluindo cadastro, edição de informações e exclusão.

### 10. Gerenciamento de Mesas
- Gerentes podem cadastrar, editar e excluir mesas no sistema.

## Tecnologias Utilizadas

- **Spring**
- **Thymeleaf**.
- **JDBC**: Para a conexão com o banco de dados MySQL, sem o uso de templates.
- **MySQL**
- **JavaScript, HTML, CSS, Bootstrap**

### Autores

- Projeto desenvolvido por [wislaargolo](https://github.com/wislaargolo), [RubensMatheus](https://github.com/RubensMatheus) e [Arthur-Holanda](https://github.com/Arthur-Holanda).
