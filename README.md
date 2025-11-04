# VestIA — Back-end (Spring Boot)

O **VestIA** é uma plataforma gamificada de estudos para vestibulares (ENEM, Fuvest e afins).
Este repositório contém o **back-end** responsável pela API REST, autenticação e persistência de dados que viabilizam funcionalidades como cadastro/autenticação de usuários, gerenciamento de questões e pontuação por acertos.

---

## 🧱 Arquitetura e Tecnologias

* Spring Boot 3.5 (Web, Validation)
* Spring Security com JWT (JJWT 0.12)
* Spring Data JPA (MySQL em produção; H2 em testes)
* OpenAPI/Swagger UI (springdoc-openapi)
* Java 17 e Maven

Configuração por variáveis de ambiente para banco de dados (MySQL):
`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
Em testes, é utilizado H2 em memória, habilitando execução isolada e reprodutível.

---

## 🗃️ Modelagem de Dados (resumo)

* `Usuario`: nome, usuário (e-mail), senha (BCrypt), pontos.
* `Questao`: enunciado (texto longo), alternativas A–E, resposta correta, assunto.

---

## 🔐 Segurança e Autenticação

Autenticação stateless com JWT:

* Fluxo: `/usuarios/logar` emite um token JWT (expiração padrão: 60 minutos).
* Envio do token: use o cabeçalho `Authorization: Bearer <token>`.
* Hash de senhas com `BCryptPasswordEncoder`.
* Filtro `OncePerRequestFilter` valida o token e popula o contexto de segurança.

**Endpoints públicos:**
`POST /usuarios/logar`, `POST /usuarios/cadastrar`, `GET /questao/all`, `GET /questao/{id}`
e a documentação (`/swagger-ui/**`, `/v3/api-docs/**`).
Demais rotas requerem autenticação.

---

## 🚀 Endpoints principais

**Usuários (`/usuarios`):**

* `POST /cadastrar`: cadastro de usuário.
* `POST /logar`: autenticação; retorna token JWT.
* `PUT /atualizar`: atualização de dados (autenticado).
* `GET /all`: lista usuários (autenticado).
* `GET /{id}`: busca por id (autenticado).
* `PUT /{id}/adicionar-pontos`: incrementa pontuação (autenticado).

**Questões (`/questao`):**

* `POST /post`: cria questão (autenticado).
* `POST /post/batch`: cria questões em lote (autenticado).
* `PUT /put`: atualiza questão (autenticado).
* `GET /all`: lista todas.
* `GET /{id}`: busca por id.
* `GET /assunto/{assunto}`: filtra por assunto (autenticado).
* `DELETE /{id}`: remove questão (autenticado).

---

## 📘 Documentação da API

A documentação interativa está disponível em tempo de execução:

* Swagger UI: [`/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html)
* OpenAPI JSON: [`/v3/api-docs`](http://localhost:8080/v3/api-docs)

> 💡 Observação: na UI, insira apenas o token JWT (sem o prefixo `"Bearer "`), pois o esquema o adiciona automaticamente.

---

## 💻 Como Rodar Localmente

### 🧩 Requisitos

* [Java JDK 17+](https://adoptium.net/)
* [Maven](https://maven.apache.org/)
* Uma IDE (Eclipse, IntelliJ IDEA ou VS Code)
* [MySQL](https://dev.mysql.com/downloads/) configurado (ex.: via MySQL Workbench)

### ⚙️ Passo a passo

1. **Importe o projeto** como um projeto Maven na IDE.

2. **Instale as dependências:**

   ```bash
   mvn clean install
   ```

3. **Ative o perfil de desenvolvimento:**
   No arquivo `src/main/resources/application.properties`, altere:

   ```properties
   spring.profiles.active=prod
   ```

   para:

   ```properties
   spring.profiles.active=dev
   ```

4. **Configure o ambiente de desenvolvimento:**
   No arquivo `src/main/resources/application-dev.properties`, substitua as linhas:

   ```properties
   spring.datasource.username=${DB_USERNAME:}
   spring.datasource.password=${DB_PASSWORD:}
   ```

   por algo como:

   ```properties
   spring.datasource.username=${DB_USERNAME:seu_usuario_do_mysql}
   spring.datasource.password=${DB_PASSWORD:sua_senha_do_mysql}
   ```

5. **Execute o back-end:**

   ```bash
   mvn spring-boot:run
   ```

   ou, pela IDE, execute a classe principal anotada com `@SpringBootApplication`.

> ⚠️ Mantenha o **MySQL Workbench aberto** durante a execução para evitar falhas de conexão.

6. **Acesse o Swagger da API:**

   ```
   http://localhost:8080/swagger-ui/index.html#/
   ```

   (a porta pode variar conforme sua configuração)

---

## 🧪 Execução de Testes

Os testes são automatizados com **JUnit 5** e **Spring Boot Test**, usando H2 em memória e `TestRestTemplate` para validar o comportamento ponta a ponta dos controladores com segurança JWT.

Comando:

```bash
mvn test
```

O pacote de testes cobre:

* Autenticação e emissão de token (`/usuarios/logar`).
* Cadastro, atualização e deduplicação de usuários.
* Listagem e busca de usuários e questões (incluindo por id e por assunto).
* CRUD completo de questões (criação, atualização e exclusão) com verificação de códigos HTTP e corpo da resposta.
* Obtenção e injeção do token nos headers via utilitários de teste.

Características dos testes:

* `@SpringBootTest(webEnvironment = RANDOM_PORT)`: levanta o contexto web real para testes de integração de API.
* Banco H2 isolado e volátil garantido por `src/test/resources/application.properties`.
* Builders e helpers específicos (ex.: criação de payloads e inclusão de JWT no header) promovem legibilidade e repetibilidade.
* Asserções verificam status, payload e efeitos de persistência, assegurando contratos da API.

---

## ☁️ Deploy e Serviços em Nuvem

O projeto está configurado para execução e deploy em ambiente **cloud-native**, com suporte a containers e pipelines contínuos.

### 🔹 Infraestrutura e serviços utilizados

* **Docker**
  O repositório inclui um `Dockerfile` configurado para build e execução da aplicação Spring Boot em ambiente isolado.
  Isso garante portabilidade e compatibilidade em produção e desenvolvimento.

* **Neon (PostgreSQL em nuvem)**
  Serviço de banco de dados gerenciado hospedado em **servidores AWS**, utilizado como banco de produção remoto.
  Ele provê alta disponibilidade e integração com ferramentas CI/CD.

* **Render**
  Plataforma utilizada para **deploy automático e contínuo** da aplicação back-end, consumindo a imagem Docker e integrando-se diretamente ao repositório GitHub.
  O Render executa o build a partir do Dockerfile e disponibiliza o serviço em uma URL pública.

Esses serviços garantem escalabilidade, segurança e facilidade de manutenção para o back-end do VestIA.
