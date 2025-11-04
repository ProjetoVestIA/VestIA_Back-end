# VestIA — Back-end (Spring Boot)

O VestAI é uma plataforma gamificada de estudos para vestibulares (ENEM, Fuvest e afins). Este repositório contém o back-end responsável pela API REST, autenticação e persistência de dados que viabilizam funcionalidades como cadastro/autenticação de usuários, gerenciamento de questões e pontuação por acertos.

## Arquitetura e Tecnologias
- Spring Boot 3.5 (Web, Validation)
- Spring Security com JWT (JJWT 0.12)
- Spring Data JPA (MySQL em produção; H2 em testes)
- OpenAPI/Swagger UI (springdoc-openapi)
- Java 17 e Maven

Configuração por variáveis de ambiente para banco de dados (MySQL): `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. Em testes, é utilizado H2 em memória, habilitando execução isolada e reprodutível.

## Modelagem de Dados (resumo)
- `Usuario`: nome, usuário (e-mail), senha (BCrypt), pontos.
- `Questao`: enunciado (texto longo), alternativas A–E, resposta correta, assunto.

## Segurança e Autenticação
Autenticação stateless com JWT:
- Fluxo: `/usuarios/logar` emite um token JWT (tempo de expiração padrão de 60 minutos).
- Envio do token: utilize o cabeçalho `Authorization: Bearer <token>`.
- Hash de senhas com `BCryptPasswordEncoder`.
- Filtro `OncePerRequestFilter` valida o token e popula o contexto de segurança.

Endpoints públicos: `POST /usuarios/logar`, `POST /usuarios/cadastrar`, `GET questao/all`, `GET questao/{id}`, além da documentação (`/swagger-ui/**`, `/v3/api-docs/**`). Demais rotas requerem autenticação.

## Endpoints principais
- Usuários (`/usuarios`):
  - `POST /cadastrar`: cadastro de usuário.
  - `POST /logar`: autenticação; retorna token JWT.
  - `PUT /atualizar`: atualização de dados (autenticado).
  - `GET /all`: lista usuários (autenticado).
  - `GET /{id}`: busca por id (autenticado).
  - `PUT /{id}/adicionar-pontos`: incrementa pontuação (autenticado).
- Questões (`/questao`):
  - `POST /post`: cria questão (autenticado).
  - `POST /post/batch`: cria questões em lote (autenticado).
  - `PUT /put`: atualiza questão (autenticado).
  - `GET /all`: lista todas.
  - `GET /{id}`: busca por id.
  - `GET /assunto/{assunto}`: filtra por assunto (autenticado).
  - `DELETE /{id}`: remove questão (autenticado).

## Documentação da API
A documentação interativa está disponível em tempo de execução em:
- Swagger UI: `/swagger-ui/index.html`
- OpenAPI JSON: `/v3/api-docs`

Observação: na UI, insira apenas o token JWT (sem o prefixo "Bearer "), pois o esquema o adiciona automaticamente.

## Configuração e execução
- Requisitos: Java 17 e Maven.
- Banco de dados: configure `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` no ambiente para uso com MySQL. O modo de DDL está como `update` para evolução automática do schema.

### Execução de testes
Os testes são automatizados com JUnit 5 e Spring Boot Test, usando H2 em memória e `TestRestTemplate` para validar o comportamento ponta a ponta dos controladores com segurança JWT.

Comando:
```bash
mvn test
```

O pacote de testes cobre:
- Autenticação e emissão de token (`/usuarios/logar`).
- Cadastro, atualização e deduplicação de usuários.
- Listagem e busca de usuários e questões (incluindo por id e por assunto).
- CRUD completo de questões (criação, atualização e exclusão) com verificação de códigos HTTP e corpo da resposta.
- Obtenção e injeção do token nos headers via utilitários de teste.

Características dos testes:
- `@SpringBootTest(webEnvironment = RANDOM_PORT)`: levanta o contexto web real para testes de integração de API.
- Banco H2 isolado e volátil garantido por `src/test/resources/application.properties`.
- Builders e helpers específicos (ex.: criação de payloads e inclusão de JWT no header) promovem legibilidade e repetibilidade.
- Asserções verificam status, payload e efeitos de persistência, assegurando contratos da API.

## Visão Geral do Projeto
- **Framework web**: Spring Boot (REST).
- **Banco de dados**: JPA + MySQL (produção) e H2 (testes).
- **Nuvem**: configuração 12‑factor (variáveis de ambiente), pronta para deploy em plataformas de nuvem compatíveis com Spring Boot.
- **Uso de API**: endpoints REST documentados com OpenAPI/Swagger.
- **Acessibilidade**: documentação interativa, CORS habilitado e respostas HTTP padronizadas favorecem integrabilidade.
- **Controle de versão**: Git.
- **Testes**: suíte automatizada com JUnit/Spring Boot Test validando fluxos críticos.
