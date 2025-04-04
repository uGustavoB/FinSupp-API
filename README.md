# FinSupp-API

## 🚀 Começando

### Pré-requisitos
- Java 23
- Maven
- Docker

### Instalação
1. Clone o repositório:
```bash
git clone https://github.com/uGustavoB/FinSupp-API.git
```
2. Navegue até o diretório do projeto:
```bash
cd FinSupp-API
```
3. Rode o docker-compose para criar o banco de dados(certifique-se de que o Docker esteja rodando):
```bash
docker-compose up -d
```
4.  Execute a aplicação:
```
mvn spring-boot:run
```
5. Para encerrar a aplicação, pressione `Ctrl + C` no terminal onde a aplicação está rodando.
6. Para parar o banco de dados, execute:
```bash
docker-compose down
```

## 📚 Documentação da API

Acesse após iniciar a aplicação:

-   Swagger UI:  `http://localhost:8080/swagger-ui/index.html#/`

-   OpenAPI Spec:  `http://localhost:8080/v3/api-docs`

[//]: # (como encerrar aplicação)

