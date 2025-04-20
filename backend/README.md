# SUS Companion API

API para gerenciamento de medicamentos, receitas médicas e monitoramento de saúde para pacientes do SUS.

## Tecnologias Utilizadas

- Java 21
- Spring Boot 3.2+
- Spring Security com JWT
- PostgreSQL 17+
- Flyway para migrações de banco de dados
- Docker e Docker Compose
- OpenAPI/Swagger para documentação da API

## Requisitos

- Java 21 JDK
- Docker e Docker Compose (para a db)
- Maven

## Configuração e Execução

### Usando Docker Compose

1. Clone o repositório:

2. Execute a aplicação com Docker Compose:
   ```bash
   docker-compose -f docker/docker-compose.yml up -d
   ```

3. A API estará disponível em: http://localhost:8080/api/v1
4. A documentação Swagger estará disponível em: http://localhost:8080/api/v1/swagger-ui.html

### Execução Local

1. Inicie o PostgreSQL com Docker:
   ```bash
   docker-compose -f docker/docker-compose.yml up -d db
   ```

2. Execute a aplicação:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. A API estará disponível em: http://localhost:8080/api/v1
4. A documentação Swagger estará disponível em: http://localhost:8080/api/v1/swagger-ui.html

## Variáveis de Ambiente

A aplicação utiliza um arquivo `.env` para configurar as variáveis de ambiente. Um arquivo `.env` de exemplo foi criado na raiz do projeto com valores padrão.

Para usar o arquivo `.env`:

1. Copie o arquivo `.env` para seu ambiente local (ele já está no .gitignore)
2. Modifique os valores conforme necessário para seu ambiente
3. O Docker Compose carregará automaticamente as variáveis do arquivo `.env`


## Endpoints Principais

### Autenticação

- `POST /api/v1/auth/register` - Registrar novo usuário
- `POST /api/v1/auth/login` - Autenticar usuário
- `POST /api/v1/auth/refresh-token` - Renovar token de acesso
- `POST /api/v1/auth/logout/{usuarioId}` - Encerrar sessão

### Medicamentos

- `GET /api/v1/medicamentos` - Listar medicamentos do usuário
- `GET /api/v1/medicamentos/{id}` - Obter medicamento por ID
- `POST /api/v1/medicamentos` - Cadastrar novo medicamento
- `PUT /api/v1/medicamentos/{id}` - Atualizar medicamento
- `DELETE /api/v1/medicamentos/{id}` - Excluir medicamento

### Estoque Pessoal

- `GET /api/v1/estoque` - Listar estoque pessoal do usuário
- `GET /api/v1/estoque/{id}` - Obter item do estoque por ID
- `POST /api/v1/estoque` - Adicionar item ao estoque
- `PUT /api/v1/estoque/{id}` - Atualizar item do estoque
- `DELETE /api/v1/estoque/{id}` - Excluir item do estoque
- `GET /api/v1/estoque/baixo` - Listar medicamentos com estoque baixo

### Lembretes de Medicação

- `GET /api/v1/lembretes` - Listar lembretes do usuário
- `GET /api/v1/lembretes/{id}` - Obter lembrete por ID
- `POST /api/v1/lembretes` - Criar novo lembrete
- `PUT /api/v1/lembretes/{id}` - Atualizar lembrete
- `DELETE /api/v1/lembretes/{id}` - Excluir lembrete
- `GET /api/v1/lembretes/hoje` - Listar lembretes para hoje

### Receitas Médicas

- `GET /api/v1/receitas` - Listar receitas do usuário
- `GET /api/v1/receitas/{id}` - Obter receita por ID
- `POST /api/v1/receitas` - Cadastrar nova receita
- `PUT /api/v1/receitas/{id}` - Atualizar receita
- `DELETE /api/v1/receitas/{id}` - Excluir receita
- `GET /api/v1/receitas/ativas` - Listar receitas ativas

### Monitoramento de Saúde

- `GET /api/v1/saude` - Listar registros de saúde do usuário
- `GET /api/v1/saude/{id}` - Obter registro de saúde por ID
- `POST /api/v1/saude` - Adicionar novo registro de saúde
- `PUT /api/v1/saude/{id}` - Atualizar registro de saúde
- `DELETE /api/v1/saude/{id}` - Excluir registro de saúde
- `GET /api/v1/saude/ultimos-registros` - Obter últimos registros de saúde

## Executando Testes

```bash
./mvnw test
```

Os testes unitários cobrem os seguintes componentes:

- **AuthService**: Testes para autenticação, registro e refresh token
- **MedicamentoService**: Testes para CRUD de medicamentos
- **ReceitaService**: Testes para CRUD de receitas médicas
- **LembreteMedicacaoService**: Testes para CRUD de lembretes de medicação
- **EstoquePessoalService**: Testes para CRUD de estoque pessoal
- **MonitoramentoSaudeService**: Testes para CRUD de monitoramento de saúde

## Estrutura do Projeto

```
sus-companion-api/
├── src/
│   ├── main/
│   │   ├── java/com/suscompanion/
│   │   │   ├── config/     # Configurações do Spring
│   │   │   ├── controller/ # Controladores REST
│   │   │   ├── dto/        # Objetos de Transferência de Dados
│   │   │   ├── exception/  # Exceções personalizadas
│   │   │   ├── model/      # Entidades
│   │   │   ├── repository/ # Repositórios Spring Data
│   │   │   ├── security/   # Classes relacionadas a JWT/auth
│   │   │   ├── service/    # Lógica de negócio
│   │   │   └── App.java    # Classe principal
│   │   └── resources/
│   │       ├── db/migration/ # Scripts SQL do Flyway
│   │       ├── application.yml # Configuração principal
│   │       └── application-dev.yml # Configuração de desenvolvimento
│   └── test/               # Classes de teste
└── docker/
    ├── Dockerfile          # Para construir a imagem da aplicação
    └── docker-compose.yml  # Configuração para desenvolvimento local
```
