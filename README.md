<h1 align="center">Projeto FinSupp API</h1>  

[](https://github.com/uGustavoB/FinSupp-API#finsupp-api)

## 🏦 Descrição

O **FinSupp API** é uma aplicação REST desenvolvida com Spring Boot que oferece uma solução completa para controle financeiro pessoal. Ela permite o gerenciamento de contas bancárias, cartões, categorias, transações, assinaturas e faturas, juntamente com autenticação JWT para segurança robusta.

Este projeto representa um marco significativo na minha jornada de aprendizado com Java e Spring. Foi desenvolvido com muito carinho, empenho e dedicação. Aplicar na prática os conhecimentos adquiridos ao longo de muitas e muitas horas de estudo foi um desafio, mas também uma oportunidade valiosa.

Encarar cada desafio, pontos de melhoria e soluções ao longo do desenvolvimento foi uma experiência intensa e transformadora. Cada obstáculo superado contribuiu diretamente para meu crescimento técnico e pessoal.  
O resultado é uma base sólida de uma aplicação real de back-end, que marca um passo importante na minha evolução como desenvolvedor.

## ✨ Funcionalidades Principais

[](https://github.com/uGustavoB/FinSupp-API#-funcionalidades-principais)

- ✅ Cadastro e login com autenticação JWT
- 🧑‍💼 CRUD de usuários com controle de acessos
- 💳 Gerenciamento de contas e cartões
- 📊 Registro e filtros de transações, faturas e assinaturas
- 🔁 Gestão de assinaturas recorrentes
- 🧾 Geração, consulta e pagamento de faturas
- 🗂 Organização por categorias
- 🔐 Proteção de endpoints com controle por roles (`ROLE_USER`, `ROLE_ADMIN`)

## 🛠 Tecnologias Utilizadas

[](https://github.com/uGustavoB/FinSupp-API#-tecnologias-utilizadas)

- Java 23
- Spring Boot 3
- Spring Security 6
- JSON Web Tokens (JWT)
- Lombok
- Hibernate/JPA
- PostgreSQL
- Swagger/OpenAPI 3

## 🌍 Acesse a plataforma

Não sei exatamente quando você está lendo isso, mas neste momento a aplicação está hospedada no **Vercel** (frontend) e no **Heroku** (backend).  
A interface disponível foi gerada com o auxílio da Inteligência Artificial `v0` e representa uma **versão simplificada do projeto**, com o objetivo principal de demonstrar, de forma prática, como seria a integração com um front-end real.

🔗 **Acesse a versão demonstrativa da FinSupp API clicando**  [aqui 👈](https://finsupp-v0.vercel.app/auth).

## 🚀 Instalação

[](https://github.com/uGustavoB/FinSupp-API#-come%C3%A7ando)

### Pré-requisitos

[](https://github.com/uGustavoB/FinSupp-API#pr%C3%A9-requisitos)

- Java 23
- Maven
- Docker

### Começando

[](https://github.com/uGustavoB/FinSupp-API#instala%C3%A7%C3%A3o)

1. Clone o repositório:

```  
git clone https://github.com/uGustavoB/FinSupp-API.git  
```  

2. Navegue até o diretório do projeto:

```  
cd FinSupp-API  
```  

3. Rode o docker-compose para criar o banco de dados(certifique-se de que o Docker esteja rodando):

```  
docker-compose up -d  
```  

4. Execute a aplicação:

```  
mvn spring-boot:run  
```  

5. Para encerrar a aplicação, pressione  `Ctrl + C` no terminal onde a aplicação está rodando.
6. Para parar o banco de dados, execute:

```  
docker-compose down  
```  

## ⌨️ Endpoints Principais

### Autenticação

| Método | Endpoint       | Descrição         |  
|--------|----------------|-------------------|  
| POST   | /auth/login    | Autentica usuário |  
| POST   | /auth/register | Cria novo usuário |  

### Usuários

| Método | Endpoint             | Descrição                  | Acesso     |  
|--------|----------------------|----------------------------|------------|  
| GET    | /users/me            | Obter usuário autenticado  | ROLE_USER  |  
| PUT    | /users/me            | Editar usuário autenticado | ROLE_USER  |  
| GET    | /users               | Ver todos os usuários      | ROLE_ADMIN |  
| DELETE | /users/{uuid}        | Deletar usuário            | ROLE_ADMIN |  
| POST   | /users/{uuid}/roles/ | Atribuir role ao usuário   | ROLE_ADMIN |  

### Contas

| Método | Endpoint       | Descrição            |  
|--------|----------------|----------------------|  
| GET    | /accounts      | Ver todas as contas  |  
| POST   | /accounts      | Criar nova conta     |  
| GET    | /accounts/{id} | Ver conta específica |  
| PUT    | /accounts/{id} | Editar conta         |  
| DELETE | /accounts/{id} | Deletar conta        |  

### Cartões

| Método | Endpoint       | Descrição             |  
|--------|----------------|-----------------------|  
| GET    | /cards         | Ver todos os cartões  |  
| POST   | /cards         | Criar novo cartão     |  
| GET    | /cards/{id}    | Ver cartão específico |  
| PUT    | /cards/{id}    | Editar cartão         |  
| DELETE | /cards/{id}    | Deletar cartão        |  

### Categorias

| Método | Endpoint         | Descrição                |  
|--------|------------------|--------------------------|  
| GET    | /categories      | Ver todas as categorias  |  
| POST   | /categories      | Criar nova categoria     |  
| GET    | /categories/{id} | Ver categoria específica |  
| PUT    | /categories/{id} | Editar categoria         |  
| DELETE | /categories/{id} | Deletar categoria        |  

### Transações

| Método | Endpoint           | Descrição                |  
|--------|--------------------|--------------------------|  
| GET    | /transactions      | Ver todas as transações  |  
| POST   | /transactions      | Criar nova transação     |  
| GET    | /transactions/{id} | Ver transação específica |  
| PUT    | /transactions/{id} | Editar transação         |  
| DELETE | /transactions/{id} | Deletar transação        |  

### Assinaturas

| Método | Endpoint            | Descrição                 |  
|--------|---------------------|---------------------------|  
| GET    | /subscriptions      | Ver todas as assinaturas  |  
| POST   | /subscriptions      | Criar nova assinatura     |  
| GET    | /subscriptions/{id} | Ver assinatura específica |  
| PUT    | /subscriptions/{id} | Editar assinatura         |  

### Faturas

| Método | Endpoint         | Descrição                      |  
|--------|------------------|--------------------------------|  
| GET    | /bills           | Ver todas as faturas           |  
| GET    | /bills{id}/items | Ver itens que compõem a fatura |  
| PATCH  | /bills{id}/pay   | Pagar fatura                   |  

## ✅ Exemplo de respostas

### Sucesso:

```json  
{  
  "message": "Transaction created",  
  "type": "Success",  
  "data": {  
      "id": 1,  
      "description": "Minecraft Movie",  
      "amount": 50,  
      "installments": 1,  
      "transactionDate": "2025-04-09",  
      "type": "WITHDRAW",  
      "category": 4,  
      "cardId": 1,  
      "recipientAccountId": null  
  }  
}  
```  

### Erro:

```json  
{
  "code": 422,
  "message": "Validation error",
  "type": "Error",
  "dataList": [
    {
      "description": "Transaction amount must be greater than zero",
      "field": "amount"
    },
    {
      "description": "Description must be less than 30 characters",
      "field": "description"
    }
  ]
}  
```  

## 📚 Documentação da API

[](https://github.com/uGustavoB/FinSupp-API#-documenta%C3%A7%C3%A3o-da-api)

Caso queira visualizar com detalhes a documentação da API, acesse após iniciar a aplicação:

- Swagger UI:  `http://localhost:8080/swagger-ui/index.html#/`

- OpenAPI Spec:  `http://localhost:8080/v3/api-docs`

## 📄 Licença

MIT License - veja [LICENSE](https://github.com/uGustavoB/FinSupp-API?tab=MIT-1-ov-file) para detalhes.
  
----------  

Feito por Gustavo Gabriel.  
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue)](https://www.linkedin.com/in/gustavobatistaa/)
