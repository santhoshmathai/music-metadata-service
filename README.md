# Music Metadata Service

A Spring Boot API for artist and track metadata with a concurrency-safe, cyclical Artist of the Day. The complete runtime—including PostgreSQL, Prometheus, and a provisioned Grafana dashboard—runs in Docker.

## Architecture

- Java 21 and Spring Boot 3.5
- PostgreSQL 18 with Flyway-managed schema
- Spring Data JPA and Jakarta Validation
- Prometheus metrics through Spring Boot Actuator and Micrometer
- Grafana dashboard provisioned from source control
- Testcontainers integration tests against real PostgreSQL

The service is a modular monolith. Artist, track, and featured-artist features remain separate packages while sharing one transactionally consistent database.

## Run the complete platform

Docker Desktop or a compatible Docker Engine with Compose is the only prerequisite.

```bash
cp .env.example .env
# Change the passwords in .env, then run:
docker compose up --build -d
docker compose ps
```

Services:

| Service | URL | Purpose |
|---|---|---|
| API | http://localhost:8080 | Music metadata REST API |
| Swagger UI | http://localhost:8080/swagger-ui.html | Interactive API documentation |
| OpenAPI JSON | http://localhost:8080/api-docs | Machine-readable OpenAPI 3 document |
| Health | http://localhost:8080/actuator/health | Liveness/readiness |
| Prometheus | http://localhost:9090 | Metrics and queries |
| Grafana | http://localhost:3000 | Provisioned operational dashboard |

Grafana credentials come from `.env`. The example defaults are `admin` / `change-me`. Open **Dashboards → Music Platform → Music Metadata Service**.

Stop the platform without deleting data:

```bash
docker compose down
```

To deliberately delete all local database and monitoring volumes:

```bash
docker compose down --volumes
```

## API examples

The Swagger UI documents request validation, response schemas, pagination, and error responses for every API operation. The raw OpenAPI document is available from `/api-docs` for client generation and API tooling.

A standalone specification is available at [`docs/openapi.yaml`](docs/openapi.yaml). It can be shared without running the service or pasted directly into [Swagger Editor](https://editor.swagger.io/) to browse and test the API contract.

Create an artist:

```bash
curl -i -X POST http://localhost:8080/api/v1/artists \
  -H 'Content-Type: application/json' \
  -d '{"name":"Daft Punk","aliases":["Darlin’"]}'
```

Add a track, replacing `{artistId}` with the returned UUID:

```bash
curl -i -X POST http://localhost:8080/api/v1/artists/{artistId}/tracks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Around the World","genre":"House","durationMs":429533,"releaseDate":"1997-03-17","albumName":"Homework"}'
```

Rename an artist while preserving the previous name as an alias:

```bash
curl -i -X PATCH http://localhost:8080/api/v1/artists/{artistId} \
  -H 'Content-Type: application/json' \
  -d '{"name":"Daft Punk Robots","preservePreviousNameAsAlias":true}'
```

Fetch tracks with bounded pagination:

```bash
curl 'http://localhost:8080/api/v1/artists/{artistId}/tracks?page=0&size=50'
```

Fetch Artist of the Day:

```bash
curl http://localhost:8080/api/v1/artists/artist-of-the-day
```

## Artist-of-the-Day guarantees

Artists receive an immutable database rotation position. The service stores the featured artist by UTC date and uses a PostgreSQL transaction-scoped advisory lock so concurrent application instances cannot assign competing artists. It advances by the number of elapsed calendar days, wraps at the end of the catalogue, and returns the persisted result for repeated requests on the same date. New artists join at the end of the rotation.

## Monitoring dashboard

The version-controlled Grafana dashboard includes:

- service availability;
- request throughput and endpoint breakdown;
- p50, p95, and p99 response latency;
- HTTP 5xx error ratio;
- active PostgreSQL connection-pool usage;
- JVM heap usage;
- artist creation, rename, and track creation rates;
- Artist-of-the-Day request rate.

Prometheus retains 15 days of local metrics. PostgreSQL, Prometheus, and Grafana data use named Docker volumes.

## Development and tests

Run all tests with Maven and an available Docker Engine:

```bash
mvn verify
```

Integration tests use Testcontainers and are automatically skipped when Docker is unavailable. The container build runs `mvn verify` before producing the runtime image.

## Design decisions

- **PostgreSQL instead of a document database:** artist-track relationships, uniqueness, audit history, transactions, and rotation locking are relational concerns.
- **Milliseconds for duration:** avoids rounding and ambiguous formatted strings.
- **UUID API identifiers:** safe to generate across horizontally scaled instances.
- **Aliases stored separately:** renames retain searchable historical identity rather than destroying it.
- **Pagination:** prevents unbounded responses for large catalogues; page size is capped at 200.
- **Flyway migrations:** schema changes are explicit and reproducible; Hibernate only validates them.
- **UTC daily boundary:** every deployment and user observes one consistent featured artist per date.

## Production notes

Replace all example credentials, terminate TLS at an ingress or load balancer, restrict Actuator access, and use managed PostgreSQL with backups. Add authentication/authorization before exposing write endpoints publicly. Secrets should be provided by a secret manager rather than committed `.env` files.
