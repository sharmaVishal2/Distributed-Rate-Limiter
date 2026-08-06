# Distributed Rate Limiter Service

A production-ready Spring Boot microservice that provides distributed rate limiting using PostgreSQL and Redis.

## Architecture

- Spring Boot 3 + Java 21
- Spring Data JPA for persistence
- PostgreSQL for rule storage and audit history
- Redis for runtime counters and distributed rate limiter state
- JWT security with admin/user RBAC
- Swagger / OpenAPI documentation
- Docker Compose for local deployment

## Features

- Multiple algorithms: Token Bucket, Fixed Window Counter, Sliding Window Log
- Client-specific configurable rules
- Redis-backed atomic counters and Lua scripts
- Admin-only rule management
- Audit log for allowed and blocked requests
- Metrics endpoint for monitoring
- JWT authentication with role-based access control
- Health check and observability-ready design

## Folder Structure

- `src/main/java`: service source code
- `src/main/resources`: application configuration
- `src/test/java`: unit and controller tests

## Run Locally with Docker

```bash
docker compose up --build
```

The service will be available at `http://localhost:8080`.

## API Endpoints

- `POST /api/auth/login`
- `POST /api/rules`
- `PUT /api/rules/{id}`
- `DELETE /api/rules/{id}`
- `GET /api/rules`
- `GET /api/rules/{id}`
- `POST /api/check`
- `GET /api/metrics`
- `GET /actuator/health`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Sample Requests

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"adminpass"}'
```

```bash
curl -X POST http://localhost:8080/api/rules \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"clientId":"client-a","endpoint":"/api/check","algorithm":"TOKEN_BUCKET","limit":10,"refillRate":1,"windowSize":60,"enabled":true}'
```

## Future Improvements

- API key authentication and client onboarding
- Redis Pub/Sub configuration synchronization
- Prometheus and Grafana integration
- CI/CD workflow with GitHub Actions
- Load testing with k6 or JMeter
- Extend metrics dashboard and caching layer
