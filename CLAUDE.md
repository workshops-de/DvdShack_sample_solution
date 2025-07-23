# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

**Build & Run:**
- `./mvnw spring-boot:run` - Run the application in development mode
- `./mvnw compile` - Compile the application
- `./mvnw package` - Build JAR file
- `./mvnw clean` - Clean build artifacts

**Testing:**
- `./mvnw test` - Run all tests
- `./mvnw test -Dtest=ClassName` - Run a specific test class
- `./mvnw test -Dtest=ClassName#methodName` - Run a specific test method

**Database:**
- Requires PostgreSQL connection (see application.yml)
- Set `PG_PASSWORD` environment variable for database access
- Uses Liquibase for database migrations in `src/main/resources/db/changelog/`

## Architecture Overview

**Spring Boot Application Structure:**
- Main class: `DvdShackApplication.java` - includes database connection validation
- Application layer: Service classes in `application/` package
- Repository layer: Dual persistence approach with both JPA and JDBC Template repositories
- Entities: JPA entities in `repository/` package with Lombok annotations

**Key Technologies:**
- Spring Boot 3.5.3 with Java 17
- Spring Data JPA with Hibernate
- Spring JDBC Template (for some repositories)
- PostgreSQL (production) / H2 (testing)
- Liquibase for database migrations
- Lombok for boilerplate reduction
- Database Rider and JavaFaker for testing

**Repository Pattern:**
The project demonstrates multiple data access patterns:
- JPA repositories extending Spring Data interfaces
- JDBC Template repositories for direct SQL control
- Both approaches coexist for the same entities (Actor, Film)

**Testing Framework:**
- JUnit 5 for unit testing
- Spring Boot Test with @SpringBootTest
- Mockito for mocking with @MockitoBean
- Database Rider for test data management
- JavaFaker for generating test data

**Database Schema:**
- Based on DVD rental store concept (Sakila-like schema)
- Entities include Actor, Film, Customer, Category, Language, LoyaltyCard
- Custom enum converters for database mapping (Rating types)
- Uses PostgreSQL sequences for ID generation

**Configuration:**
- `application.yml` for Spring configuration
- Database connection requires `PG_PASSWORD` environment variable
- Hibernate SQL logging enabled for development
- Liquibase changesets in YAML format

## Recent Improvements

**Enhanced ActorService (2025-07-23):**
- **Security Fix**: Fixed SQL injection vulnerability in `ActorJdbcTemplateRepository.deleteActor()`
- **Full CRUD Operations**: Added create, update, delete methods with proper validation
- **Comprehensive Error Handling**: Custom exceptions (`ActorNotFoundException`, `ActorValidationException`)
- **Input Validation**: Validates all inputs including length limits and null checks
- **Logging**: Added SLF4J logging at debug, info, and warn levels
- **Transaction Management**: Proper `@Transactional` boundaries for read/write operations
- **Business Logic**: Added search methods, counting, and full-name queries
- **Test Coverage**: Comprehensive test suite with 25+ test methods covering all scenarios
- **REST Controller**: New `ActorController` with full CRUD REST API and ProblemDetail error handling

**Fixed Issues:**
- Updated `RatingEnumValueConverter` to use JPA `AttributeConverter` instead of deprecated Hibernate APIs
- Resolved compilation errors and warnings
- Fixed `RatingType` class deprecation issues