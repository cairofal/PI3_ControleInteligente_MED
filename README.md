# PI3_ControleInteligente_MED
Projeto Integrador III - UNIVESP 2025
O aplicativo em desenvolvimento tem como finalidade auxiliar pacientes idosos e crônicos no gerenciamento de sua saúde domiciliar, promovendo a adesão terapêutica através de lembretes inteligentes para medicação, registro de parâmetros vitais (como pressão arterial e glicemia), alertas sobre validade de receitas e um canal de comunicação direto com profissionais de saúde. Utilizando tecnologias como Kotlin para Android, Spring Framework no backend, React JS para interfaces web e bancos de dados PostgresDB, a solução prioriza acessibilidade e usabilidade, e vislumbra a integração de análises preditivas para personalizar intervenções e melhorar a qualidade de vida dos usuários.

Conta atualmente com uma api RestFUL (SUS Companion API) que servirá tanto o front-end da aplicação web quanto o app Android.


## SUS Companion API

API para gerenciamento de medicamentos, receitas médicas e monitoramento de saúde para pacientes do SUS.

### Tecnologias Utilizadas (Backend)

- Java 21
- Spring Boot 3.2+
- Spring Security com JWT
- PostgreSQL 17+
- Flyway para migrações de banco de dados
- Docker e Docker Compose
- OpenAPI/Swagger para documentação da API

### Requisitos

- Java 21 JDK
- Docker e Docker Compose (para a db)
- Maven

