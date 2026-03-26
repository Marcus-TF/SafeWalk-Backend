# SafeWalk Backend

API REST em Java Spring Boot para o aplicativo SafeWalk de segurança urbana.

## Tecnologias

- Java 17
- Spring Boot 3.2.4
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL / H2
- Maven
- Lombok

## Requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL 15+ (para produção)

## Configuração

### Desenvolvimento (H2)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

O servidor estará disponível em `http://localhost:8080`

Console H2: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:safewalkdb`
- Username: `sa`
- Password: (vazio)

### Produção (PostgreSQL)

1. Certifique-se de que o PostgreSQL está rodando
2. Crie o banco de dados:

```sql
CREATE DATABASE safewalk;
```

3. Execute:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Docker

```bash
docker-compose up -d
```

## Endpoints

### Autenticação (públicos)

**POST** `/api/auth/signup`
```json
{
  "name": "João Silva",
  "email": "joao@test.com",
  "password": "123456"
}
```

**POST** `/api/auth/login`
```json
{
  "email": "joao@test.com",
  "password": "123456"
}
```

Resposta:
```json
{
  "token": "eyJhbGc...",
  "user": {
    "id": 1,
    "name": "João Silva",
    "email": "joao@test.com"
  }
}
```

### Ocorrências (requer autenticação)

Adicione o header: `Authorization: Bearer {token}`

**GET** `/api/occurrences` - Listar todas

**GET** `/api/occurrences/my` - Minhas ocorrências

**GET** `/api/occurrences/{id}` - Buscar por ID

**POST** `/api/occurrences` - Criar
```json
{
  "type": "Assalto",
  "description": "Descrição do incidente",
  "latitude": -23.5505,
  "longitude": -46.6333,
  "location": "Rua Exemplo, 123",
  "risk": "high"
}
```

**DELETE** `/api/occurrences/{id}` - Deletar (apenas dono)

## Dados de Teste (perfil dev)

Usuários:
- `joao@test.com` / `123456`
- `maria@test.com` / `123456`
- `pedro@test.com` / `123456`

## Estrutura

```
src/main/java/com/safewalk/
├── SafeWalkApplication.java
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   └── OccurrenceController.java
├── dto/
├── exception/
├── model/
├── repository/
├── security/
├── service/
└── util/
```

## Build

```bash
mvn clean package
java -jar target/safewalk-backend-1.0.0.jar
```
