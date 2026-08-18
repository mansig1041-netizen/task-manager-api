# Task Manager API

A production-style REST API for managing tasks, built with **Java 17** and **Spring Boot 3**. Built as a portfolio project to demonstrate clean API design, testing discipline, and containerized deployment — not just CRUD.

![CI](https://github.com/YOUR_USERNAME/task-manager-api/actions/workflows/ci.yml/badge.svg)

## Why this project

Most CRUD demos stop at "it works on my machine." This one is built the way a real team would ship it:
- Layered architecture (Controller → Service → Repository) instead of fat controllers
- Input validation with proper error responses, not stack traces
- Unit tests (Mockito) **and** integration tests (MockMvc) — not just happy-path
- Multi-stage Docker build for a small, non-root production image
- CI pipeline that builds, tests, and Docker-builds on every push
- API documentation via OpenAPI/Swagger

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3, Spring Data JPA, Spring Validation |
| Database | H2 (in-memory, swappable for MySQL/Postgres) |
| Testing | JUnit 5, Mockito, AssertJ, MockMvc |
| Docs | springdoc-openapi (Swagger UI) |
| Containerization | Docker, Docker Compose |
| CI/CD | GitHub Actions |

## API Overview

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/tasks` | List all tasks (optional `?status=` filter) |
| GET | `/api/v1/tasks/{id}` | Get a single task |
| POST | `/api/v1/tasks` | Create a task |
| PUT | `/api/v1/tasks/{id}` | Update a task |
| DELETE | `/api/v1/tasks/{id}` | Delete a task |
| GET | `/actuator/health` | Health check (for Docker/K8s probes) |
| GET | `/swagger-ui.html` | Interactive API docs |

### Example request

```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Deploy to cloud","description":"Push container to Azure","priority":"HIGH"}'
```

## Running locally

**Option 1 — Maven**
```bash
mvn spring-boot:run
```

**Option 2 — Docker**
```bash
docker compose up --build
```

Then visit `http://localhost:8080/swagger-ui.html` to explore the API.

## Running tests

```bash
mvn test
```

Covers service-layer logic (mocked repository) and full HTTP request/response cycles (MockMvc), including validation-error and not-found paths.

## Project structure

```
src/main/java/com/portfolio/taskmanager/
├── controller/     REST endpoints
├── service/        Business logic
├── repository/     Data access (Spring Data JPA)
├── model/          JPA entities & enums
├── dto/            Request/response objects with validation
└── exception/      Centralized error handling
```

## Roadmap

- [ ] Deploy to Azure App Service / AWS free tier
- [ ] Add pagination and sorting to the list endpoint
- [ ] Swap H2 for PostgreSQL with a docker-compose service
- [ ] Add AI-powered task categorization (LLM integration)

## License

MIT
