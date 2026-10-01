# RÓRA — Backend Service (Spring Boot + PostgreSQL)

Production REST API for **RÓRA — Luxury Bags & Carry Essentials**.

---

## 1. Prerequisites

- **Java Development Kit**: JDK 21 LTS (`java -version`, `javac -version`)
- **Apache Maven**: 3.9+ (`mvn -version`)
- **PostgreSQL**: 18.x (`psql --version`) running on `localhost:5432`

---

## 2. Configuration (`.env` / `application.yml`)

The backend connects to PostgreSQL with standard environment variables:

```bash
DB_URL=jdbc:postgresql://localhost:5432/rora_db
DB_USERNAME=postgres
DB_PASSWORD=password
PORT=8080
```

---

## 3. Database Initialization

Ensure the database `rora_db` exists in your PostgreSQL instance:

```sql
CREATE DATABASE rora_db;
```

Flyway will automatically execute database migrations on application startup (`classpath:db/migration/V1__initial_schema.sql`).

---

## 4. Building and Running

### Run Tests:
```bash
mvn clean test
```

### Start Development Server:
```bash
mvn spring-boot:run
```

The backend starts at **`http://localhost:8080`**.

---

## 5. Endpoints & Documentation

- **Health Diagnostics**: `http://localhost:8080/api/v1/health`
- **Interactive Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI 3 JSON Specification**: `http://localhost:8080/v3/api-docs`
- **Spring Actuator Metrics**: `http://localhost:8080/actuator/health`
