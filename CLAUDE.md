# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
SivaLabs Blog is a Spring Boot blog application built with Java 24, Spring Modulith, and Thymeleaf. It features both a public blog interface and an admin dashboard for content management.

## Build Commands

### Maven Commands
- **Full build with tests**: `./mvnw clean verify`
- **Run application**: `./mvnw spring-boot:run`
- **Code formatting**: `./mvnw spotless:apply`
- **Docker image build**: `./mvnw -Prelease clean compile spring-boot:build-image -DskipTests`

### Task Commands (using Taskfile)
- **Default (test)**: `task`
- **Run tests**: `task test`
- **Format code**: `task format`
- **Build Docker image**: `task build_image`
- **Start application**: `task start`
- **Stop application**: `task stop`

### Frontend Build Commands
- **Development build**: `npm run build`
- **Production build**: `npm run build-prod`
- **Watch for changes**: `npm run watch`
- **Live reload with browser-sync**: `npm run watch:serve`

### Test Commands
- **Unit tests only**: `./mvnw test`
- **Integration tests only**: `./mvnw failsafe:integration-test`
- **Single test class**: `./mvnw test -Dtest=ClassName`
- **Test with coverage**: `./mvnw clean verify` (includes JaCoCo coverage with 70% minimum)

## Architecture

### Spring Modulith Structure
The application uses Spring Modulith to organize code into logical modules:
- **admin**: Administrative functionality (posts, users, settings, etc.)
- **blog**: Public blog features (posts, comments, subscriptions)
- **shared**: Cross-cutting concerns (entities, DTOs, utilities)
- **auth**: Authentication and security
- **notification**: Email services

Each module has a `package-info.java` file with `@ApplicationModule` annotations. The `shared` module is marked as `Type.OPEN` to allow cross-module access.

### Technology Stack
- **Backend**: Spring Boot 3.5.3, Spring Data JPA, Spring Security, Spring Modulith
- **Database**: PostgreSQL with Flyway migrations
- **Frontend**: Thymeleaf, HTMX, Alpine.js, Tailwind CSS
- **Testing**: JUnit 5, Testcontainers, Spring Boot Test
- **Build**: Maven with frontend-maven-plugin for Node.js integration

### Key Components
- **Entities**: Located in `shared/entities/` (Post, Comment, User, Category, Tag, etc.)
- **DTOs**: Located in `shared/models/` for data transfer
- **Repositories**: Follow Spring Data JPA conventions
- **Services**: Business logic layer in each module
- **Controllers**: Separate admin and public controllers

## Development Setup

### Prerequisites
- JDK 24
- Docker and Docker Compose
- Node.js (managed by frontend-maven-plugin)

### Local Development
1. Start with `local` profile: `./mvnw spring-boot:run -Dspring-boot.run.profiles=local`
2. Run frontend build: `npm run build && npm run watch`
3. Access application at http://localhost:8080 (or http://localhost:3000 with browser-sync)

### Database
- Uses PostgreSQL in production
- Flyway migrations in `src/main/resources/db/migration/`
- Test data initialization through `DataInitializer.java`

## Code Quality
- **Formatting**: Spotless with Palantir Java Format
- **Coverage**: JaCoCo with 70% minimum line coverage
- **Testing**: Comprehensive unit and integration tests using Testcontainers

## Configuration
- **Application Properties**: `ApplicationProperties.java` with `@ConfigurationProperties`
- **Profiles**: `local`, `test` configurations available
- **Environment Variables**: Prefix `CM_` for application properties

## Common Patterns
- **Mappers**: Use dedicated mapper classes for entity-DTO conversion
- **Services**: Business logic with repository injection
- **Controllers**: Separate admin (`/admin/*`) and public endpoints
- **Error Handling**: Global exception handler in `GlobalExceptionHandler.java`
- **Security**: Role-based access control with USER, AUTHOR and ADMIN roles