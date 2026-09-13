# Patient Triage API

A Spring Boot REST API for patient intake and urgency-based retrieval.

## Features

- REST endpoints for patient intake, listing, retrieval, and triage
- Priority queue that returns the highest-urgency patient first
- Input validation using Jakarta Validation
- JUnit 5 test suite
- Multi-stage Dockerfile for lightweight production images
- GitHub Actions CI pipeline that runs tests on every push

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Jakarta Validation
- Maven
- JUnit 5
- Docker
- GitHub Actions

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/patients | Add a new patient |
| GET | /api/patients | List all waiting patients |
| GET | /api/patients/next | Get the highest-urgency patient |
| GET | /api/patients/{id} | Get a patient by ID |
| GET | /api/patients/count | Number of patients remaining |

## Running Locally

mvn spring-boot:run

The API will be available at http://localhost:8080.

## Running with Docker

docker build -t patient-triage-api .
docker run -p 8080:8080 patient-triage-api

## Running Tests

mvn test

## Example Request

curl -X POST http://localhost:8080/api/patients -H "Content-Type: application/json" -d '{"id":"P1","name":"Alice","urgency":2}'
