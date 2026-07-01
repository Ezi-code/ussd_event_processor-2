# USSD Event Processor

A Spring Boot service for ingesting, parsing, and querying Call Detail Records (CDRs) from pipe-delimited log files into PostgreSQL.

## Features

- **File Ingestion** — polls a configured watch folder, parses 33-field pipe-delimited CDR files, and batches inserts (1,000 records per batch).
- **Async Processing** — multiple files processed concurrently via a `ThreadPoolTaskExecutor`.
- **Duplicate Prevention** — checks `cdr_log` before processing; unique constraint on filename.
- **Audit Logging** — per-file processing history with success/failure counts and timestamps.
- **REST Query API** — `POST /api/cdrs/query` to search records by date range with optional MSISDN/IMSI filters.
- **Swagger UI** — interactive API docs at `/swagger-ui/index.html`.
- **Flyway Migrations** — version-controlled schema management.

## Architecture

- **Java 26**, **Spring Boot 4.1.0**
- **Spring Data JPA + Hibernate**
- **PostgreSQL**
- **Flyway**
- **Lombok**
- **springdoc-openapi** (Swagger UI)

## REST API

### Query CDR Records

**Sample 1: Query by Date Range and Identifiers**

```bash
curl -X POST http://localhost:8080/api/cdrs/query \
  -H "Content-Type: application/json" \
  -d '{
    "record_date_start": "2023-10-27 10:00:00,000",
    "record_date_end": "2026-07-01",
    "msisdn": "573103154393",
    "imsi": "732101647793504"
  }'
```

**Response:**
```json
[
  {
    "RECORD_DATE": "2023-10-27 10:00:00",
    "MSISDN": "573103154393",
    "IMSI": "732101647793504"
  }
]
```

**Sample 2: Query by Date Range only**

```bash
curl -X POST http://localhost:8080/api/cdrs/query \
  -H "Content-Type: application/json" \
  -d '{
    "record_date_start": "2023-01-01",
    "record_date_end": "2023-12-31"
  }'
```

**Response:**
```json
[
  {
    "RECORD_DATE": "2023-10-27 10:00:00",
    "MSISDN": "573103154393",
    "IMSI": "732101647793504"
  },
  {
    "RECORD_DATE": "2023-10-27 10:05:00",
    "MSISDN": "573103154394",
    "IMSI": "732101647793505"
  }
]
```

**Request fields:**
- `record_date_start`, `record_date_end` — required; accepts 7 datetime formats (space-delimited with comma-millis, ISO 8601 with/without millis/Z, date-only).
- `msisdn`, `imsi` — optional filters (omit or null to skip).

Returns `400 Bad Request` if dates are missing or unparseable.

Swagger UI available at `/swagger-ui/index.html`.

## Configuration

```properties
cdr.watch-folder=data/ussd/incoming
cdr.processed-folder=data/ussd/processed
cdr.poll-rate-ms=60000

spring.datasource.url=jdbc:postgresql://localhost:5432/ussd
spring.datasource.username=postgres
spring.datasource.password=admin
```

## Database Schema

### `ussd.call_detail_records`

Stores parsed CDR data. Key columns: `record_date`, `msisdn`, `imsi`, `status`, `type`, `tstamp`, `transaction_id`, `dialog_duration`, `ussd_string`, and structured telecom addressing fields (`l_spc`, `l_ssn`, `r_spc`, `or_digits`, `de_digits`, `vlr_digits`, etc.). File tracking is handled by the `cdr_log` table.

### `cdr_log`

Tracks file processing history: `file_name` (unique), `upload_start_time`, `upload_end_time`, `records_loaded`, `records_failed`.

## Getting Started

1. Ensure PostgreSQL is running and create the `ussd` database.
2. Update credentials in `application.properties`.
3. Ensure `data/ussd/incoming` and `data/ussd/processed` directories exist.
4. Run:
   ```bash
   mvn spring-boot:run
   ```
   Flyway applies migrations automatically on startup.

Place `.log` files in `data/ussd/incoming` for automatic ingestion.

## Testing

```bash
mvn test
```

Includes unit tests (`CdrMapperTest`, `DateTimeParserTest`), web layer tests (`CdrQueryControllerTest`, `OpenApiTest`), mock-based service tests (`FileWatcherServiceTest`), and integration tests (`FileProcessingIntegrationTest`, `DuplicateProcessingTest`).