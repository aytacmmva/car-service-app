#  Car Service REST API

Production-ready Spring Boot application for car service management.

## Tech Stack

- **Java 17** + **Spring Boot 3.2**
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL** + **Flyway** (DB migrations)
- **HikariCP** (connection pool)
- **MapStruct** (DTO mapping)
- **Lombok**
- **Testcontainers** (integration tests)
- **Docker** + **docker-compose**
- **GitHub Actions** (CI/CD)
- **Swagger/OpenAPI** (API docs)

## Features (Business Logic)

| Feature | Endpoint |
|---|---|
| Open a new order | `POST /api/v1/orders` |
| Assign repairers to order | `PATCH /api/v1/orders/{id}/repairers` |
| Complete an order | `PATCH /api/v1/orders/{id}/complete` |
| Cancel an order | `PATCH /api/v1/orders/{id}/cancel` |
| List orders (paginated + sorted) | `GET /api/v1/orders` |
| Get order by ID | `GET /api/v1/orders/{id}` |
| Add garage slot | `POST /api/v1/garage-slots` |
| Delete garage slot | `DELETE /api/v1/garage-slots/{id}` |
| List garage slots | `GET /api/v1/garage-slots` |

## Quick Start

### With Docker Compose (Recommended)

```bash
docker-compose up --build
```

App will be available at: http://localhost:8080  
Swagger UI: http://localhost:8080/swagger-ui.html

### Local Development (IntelliJ)

1. Start PostgreSQL:
```bash
docker-compose up postgres -d
```

2. Run the application from IntelliJ:  
   `CarServiceApplication.java` → Run

3. API Docs: http://localhost:8080/swagger-ui.html

## Configuration

Key environment variables:

| Variable | Default | Description |
|---|---|---|
| `DB_USERNAME` | `postgres` | PostgreSQL username |
| `DB_PASSWORD` | `postgres` | PostgreSQL password |
| `GARAGE_SLOTS_ENABLED` | `true` | Enable/disable garage slot management |

## Order Status Flow

```
OPENED → (assign repairers) → OPENED
OPENED → (complete) → COMPLETED
OPENED → (cancel) → CANCELLED
```

## Running Tests

```bash
# Unit tests only
./mvnw test -Dtest="*ServiceTest"

# All tests (requires Docker for Testcontainers)
./mvnw test
```

## CI/CD Pipeline

- **PR opened** → runs all tests
- **Push to main** → runs tests + builds JAR artifact + builds Docker image
