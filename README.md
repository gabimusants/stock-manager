# Stock Manager

Projeto de estudo em Java + Spring Boot: uma API REST simplificada de gestão de
estoque e pedidos B2B (retailers pedindo produtos de um catálogo). Cobre camadas de
Controller/Service/Repository, JPA, testes automatizados, Docker, e mais
conforme o projeto evolui.

## Stack

- Java 21
- Spring Boot (Web, Data JPA, Validation, Actuator)
- PostgreSQL
- Lombok
- JUnit 5 + AssertJ (testes)
- Maven

## Domínio

```
Retailer (1) ──< (N) Order (1) ──< (N) OrderItem >── (N) Product
```

## Pré-requisitos

- [Java 21+](https://adoptium.net/) instalado
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e rodando

Não é necessário instalar o Maven globalmente — o projeto usa o Maven Wrapper
(`mvnw` / `mvnw.cmd`), que baixa a versão correta automaticamente na primeira
execução.

## Configuração local

### 1. Clone o repositório

```bash
git clone <url-do-seu-repositorio>
cd stock-manager
```

### 2. Configure as variáveis de ambiente

Este projeto **não** guarda credenciais de banco de dados no código. Copie o
arquivo de exemplo e preencha com seus próprios valores:

```bash
cp .env.example .env
```

Edite o `.env` e defina uma senha à sua escolha:

```
DB_NAME=stockmanager
DB_USERNAME=stockmanager_user
DB_PASSWORD=escolha_uma_senha_forte_aqui
```

> O arquivo `.env` está no `.gitignore` — ele nunca deve ser commitado.

### 3. Suba o banco de dados

O `docker-compose.yml` lê as variáveis do arquivo `.env` automaticamente:

```bash
docker compose up -d
```

Confirme que subiu:
```bash
docker ps
```

### 4. Exporte as variáveis de ambiente para a aplicação Java

O Spring Boot lê as mesmas variáveis de ambiente do sistema operacional. Você
precisa exportá-las na sessão do terminal antes de rodar a aplicação (ou
configurar na sua IDE — veja abaixo).

**PowerShell (Windows):**
```powershell
$env:DB_NAME="stockmanager"
$env:DB_USERNAME="stockmanager_user"
$env:DB_PASSWORD="a_mesma_senha_que_voce_colocou_no_.env"
```

**Linux/macOS (bash/zsh):**
```bash
export DB_NAME=stockmanager
export DB_USERNAME=stockmanager_user
export DB_PASSWORD=a_mesma_senha_que_voce_colocou_no_.env
```

**Se estiver usando IntelliJ IDEA:** abra a configuração de execução (Run
Configuration) da classe `StockManagerApplication`, vá em "Modify options" >
"Environment variables", e adicione as 3 variáveis lá — assim não precisa
exportar manualmente toda vez que abrir o projeto.

### 5. Rode a aplicação

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### 6. Valide

```bash
curl http://localhost:8080/actuator/health
```

Deve retornar `{"status":"UP"}`.

## Rodando os testes

```bash
# Windows
.\mvnw.cmd test

# Linux/macOS
./mvnw test
```

Os testes de repositório (`@DataJpaTest`) usam um banco H2 em memória — não
precisam do Postgres/Docker rodando.

## Endpoints disponíveis até o momento

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/products` | Cria um produto |
| GET | `/api/products` | Lista todos os produtos |
| GET | `/api/products/{id}` | Busca um produto por id |
| PUT | `/api/products/{id}` | Atualiza um produto |
| DELETE | `/api/products/{id}` | Remove um produto |
| POST | `/api/retailers` | Cria um retailer |
| GET | `/api/retailers` | Lista todos os retailers |
| GET | `/api/retailers/{id}` | Busca um retailer por id |
| PUT | `/api/retailers/{id}` | Atualiza um retailer |
| DELETE | `/api/retailers/{id}` | Remove um retailer |

## Roteiro de evolução

- [x] Setup do projeto e infraestrutura (Docker/Postgres)
- [x] Modelagem de entidades (Product, Retailer, Order, OrderItem)
- [x] Testes unitários das entidades
- [x] Repositories (Spring Data JPA)
- [x] Camadas Controller/Service/DTO/Mapper para Product e Retailer
- [ ] Controller/Service/DTO para Order/OrderItem (com relacionamentos)
- [ ] Validação de dados e tratamento centralizado de exceções
- [ ] Testes unitários de Service (Mockito) e de integração (Testcontainers)
- [ ] Regras de negócio (baixa de estoque, transições de status)
- [ ] Spring Security (JWT)
- [ ] Observabilidade (métricas customizadas)
- [ ] Dockerização da aplicação
- [ ] Kubernetes
- [ ] CI/CD
- [ ] Testes de carga/stress
