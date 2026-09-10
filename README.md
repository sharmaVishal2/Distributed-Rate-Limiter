# Distributed Rate Limiter Service

A production-ready distributed rate limiting backend built with Spring Boot, Redis, and PostgreSQL. Supports multiple rate limiting algorithms with shared state across application instances. No frontend required — all APIs are explorable via Swagger UI.

## Features

- Distributed rate limiting backed by Redis (shared state across all instances)
- Three algorithms: Token Bucket, Fixed Window Counter, Sliding Window Log
- Atomic Lua scripts for race-condition-free Redis operations
- Client-specific configurable rules stored in PostgreSQL
- Default rule: 5 requests per client per endpoint per 60 seconds (Fixed Window)
- JWT authentication with admin/user RBAC
- Admin-only rule management (`ROLE_ADMIN`)
- Audit log for every allowed and blocked request
- Metrics endpoint (top clients, top endpoints, allowed/blocked counts)
- Spring Boot Actuator health endpoint
- Swagger / OpenAPI UI for live API exploration
- Docker Compose for local deployment
- Environment-variable-driven configuration (no hardcoded secrets)

## Architecture

```
Client Request
      |
      v
Spring Boot Application  (one or more instances)
      |
      +---> Redis          (shared atomic rate-limit counters via Lua scripts)
      |
      +---> PostgreSQL     (rate limit rules, audit log, users)
```

Redis is the source of truth for all rate-limit decisions. Every instance executes the same Lua scripts against the same Redis keys, so horizontal scaling does not break rate limiting — a client's counter is shared across all running instances.

## Local Setup

### Prerequisites

- Docker and Docker Compose

### Run with Docker Compose

```bash
docker compose up --build
```

The service starts at `http://localhost:8080`.

Default credentials seeded on first start:

| Username | Password  | Role       |
|----------|-----------|------------|
| admin    | adminpass | ROLE_ADMIN |
| user     | userpass  | ROLE_USER  |

### Run without Docker (requires local PostgreSQL and Redis)

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/rate_limiter
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=yourpassword
export SPRING_REDIS_HOST=localhost
export SPRING_REDIS_PORT=6379
export JWT_SECRET=your-secret-key-min-32-characters-long

mvn spring-boot:run
```

## Environment Variables

| Variable                  | Description                              | Default                                        |
|---------------------------|------------------------------------------|------------------------------------------------|
| `PORT`                    | HTTP port the application listens on     | `8080`                                         |
| `SPRING_DATASOURCE_URL`   | PostgreSQL JDBC URL                      | `jdbc:postgresql://localhost:5432/rate_limiter` |
| `SPRING_DATASOURCE_USERNAME` | PostgreSQL username                   | `postgres`                                     |
| `SPRING_DATASOURCE_PASSWORD` | PostgreSQL password                   | `postgres`                                     |
| `SPRING_REDIS_HOST`       | Redis hostname                           | `localhost`                                    |
| `SPRING_REDIS_PORT`       | Redis port                               | `6379`                                         |
| `SPRING_REDIS_PASSWORD`   | Redis password (leave empty if none)     | *(empty)*                                      |
| `JWT_SECRET`              | JWT signing secret (min 32 characters)   | `change-this-secret-in-production-min-32-chars!!` |
| `JWT_EXPIRATION_MS`       | JWT token expiry in milliseconds         | `3600000` (1 hour)                             |
| `CORS_ALLOWED_ORIGINS`    | Comma-separated list of allowed origins  | `http://localhost:5173,http://localhost:8080`  |

**Never commit real secret values. Set all secrets via environment variables in your deployment platform.**

## API Documentation

Swagger UI (interactive, no frontend needed):
```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON spec:
```
http://localhost:8080/v3/api-docs
```

Health check:
```
http://localhost:8080/actuator/health
```

Service info:
```
http://localhost:8080/
```

## API Examples

### 1. Login and get JWT token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"adminpass"}'
```

Response:
```json
{"accessToken":"<jwt>","tokenType":"Bearer"}
```

### 2. Check rate limit (default rule: 5 req / 60s per client+endpoint)

```bash
curl -X POST http://localhost:8080/api/check \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"clientId":"client-a","endpoint":"/api/check"}'
```

Allowed response (HTTP 200):
```json
{"allowed":true,"reason":"allowed"}
```

Blocked response after limit exceeded (HTTP 429):
```json
{"status":429,"error":"Too Many Requests","message":"Rate limit exceeded. Maximum 5 requests allowed.","retryAfter":60}
```

### 3. Create a client-specific rule (admin only)

```bash
curl -X POST http://localhost:8080/api/rules \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"clientId":"client-a","endpoint":"/api/check","algorithm":"TOKEN_BUCKET","limit":10,"refillRate":1,"windowSize":60,"enabled":true}'
```

Algorithms: `TOKEN_BUCKET`, `FIXED_WINDOW_COUNTER`, `SLIDING_WINDOW_LOG`

### 4. List all rules

```bash
curl http://localhost:8080/api/rules \
  -H "Authorization: Bearer <token>"
```

### 5. Update a rule

```bash
curl -X PUT http://localhost:8080/api/rules/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"clientId":"client-a","endpoint":"/api/check","algorithm":"SLIDING_WINDOW_LOG","limit":20,"refillRate":1,"windowSize":60,"enabled":true}'
```

### 6. Delete a rule

```bash
curl -X DELETE http://localhost:8080/api/rules/1 \
  -H "Authorization: Bearer <token>"
```

### 7. View metrics

```bash
curl http://localhost:8080/api/metrics \
  -H "Authorization: Bearer <token>"
```

## Deployment (Render / Railway)

This is a backend-only service. No frontend is needed — use Swagger UI or Postman to demonstrate all APIs.

### Steps

1. Push this repository to GitHub.
2. Create a new Web Service on [Render](https://render.com) or [Railway](https://railway.app).
3. Set the build command: `mvn -DskipTests package`
4. Set the start command: `java -jar target/distributed-rate-limiter-service-0.0.1-SNAPSHOT.jar`
   - Or use Docker: the included `Dockerfile` works out of the box.
5. Provision a managed PostgreSQL database and a managed Redis instance on your platform.
6. Set all required environment variables (see table above) in the platform dashboard.
7. Deploy. The service will be publicly accessible.

### Docker

Build and run locally:
```bash
docker compose up --build
```

Build image only:
```bash
docker build -t distributed-rate-limiter .
```

Run container with environment variables:
```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/rate_limiter \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  -e SPRING_REDIS_HOST=host \
  -e SPRING_REDIS_PORT=6379 \
  -e JWT_SECRET=your-secret-min-32-chars \
  distributed-rate-limiter
```

## Build and Test

```bash
# Compile only
mvn -DskipTests compile

# Run all tests
mvn test

# Package JAR
mvn -DskipTests package
```

## Rate Limiting Algorithms

| Algorithm              | Description                                                                 |
|------------------------|-----------------------------------------------------------------------------|
| `FIXED_WINDOW_COUNTER` | Counts requests in fixed time windows. Simple and fast. Default algorithm.  |
| `TOKEN_BUCKET`         | Tokens refill at a constant rate. Allows short bursts up to bucket capacity.|
| `SLIDING_WINDOW_LOG`   | Tracks exact request timestamps. Most accurate, slightly higher Redis cost. |

All algorithms use atomic Redis Lua scripts — no race conditions under concurrent load.
