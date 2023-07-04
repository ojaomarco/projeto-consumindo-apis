# Aplicativo de Gerenciamento de Clientes e Pedidos
Este aplicativo permite que os usuários realizem operações de cadastro, consulta e atualização de clientes, além de criar e alterar pedidos. Utiliza os web services disponibilizados na URL http://argo.td.utfpr.edu.br/clients/ws/<recurso> para obter e enviar dados.

## Funcionalidades
Cadastro de novos clientes ou consulta e atualização de dados de clientes já cadastrados, por meio da busca por CPF. O endereço é obtido automaticamente utilizando o web service da viacep.com.br.
Consulta dos setores e produtos disponíveis para venda, para auxiliar na criação de pedidos.
Criação de pedidos, adição de produtos cadastrados e envio para a API. É possível selecionar o setor, escolher os produtos, informar a quantidade e adicionar ao pedido. Também é possível alterar um pedido, adicionando, alterando ou removendo itens. A lista de itens modificada é enviada completa, substituindo a anterior.
