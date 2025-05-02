Guidelines.md :

# Junie Guidelines for Spring Boot Development

This file contains guidelines for Junie to follow when working on this Spring Boot project. Adhering to these standards ensures consistence

## Core Technologies & Versions

*   **Java:** Use the latest Long-Term Support (LTS) version of Java (e.g., Java 21 or later) unless project constraints dictate otherwise.
*   **Spring Boot:** Always use the latest stable release of Spring Boot 3.X (or the latest major stable version available) for new features
*   **Build Tool:** Use Maven as the build tool. Ensure the 'pom.Xml' uses the latest stable Spring Boot parent POM and compatible plugin versions
*   **DB:** Use PostgreSQL For Large objects preferred use BLOB`PostgreSQL Large Objects`
*   **Lombok:** Use latest compatible with Spring Boot version
*   **Docker Compose support:** Use docker with last PostgreSQL. Configuration is a Docker Compose file named `compose.yaml`

## Project Structure

*   **Packaging:** Strongly prefer a **package-by-feature** structure over package-by-layer. This means grouping all code related to a specification
*   **Why Package-by-Feature?** It improves modularity, makes navigating code related to a single feature easier, reduces coupling betwen

* **Example:**
  **PREFER THIS (Package-by-Feature):**
```
com.example.application
— recipe                                            # Feature: Recipe
  - RecipeController.java.                          # Controller for Recipe
  - RecipeService. java                             # Service logic for Recipe
  - RecipeRepository. java.                         # Data access for Recipe
  - Recipe. java.                                   # Domain/Entity for Recipe
  - dto                                             # Data Transfer Objects specific to Recipe
    - RecipeCreateRequest. java
    - RecipeUpdateRequest. java
 
— users                                             # Feature: Users
  - UserController. java                            # Controller for User
  - UserService. java                               # Service logic for User
  - UserRepository. java.                           # Data access for User
  - User. java.                                     # Domain/Entity for User
  - dto                                             # Data Transfer Objects specific to User
    - UserCreateRequest. java
    - UserUpdateRequest. java

ー exception
  - GlobalExceptionHandler                          # Extends ResponseEntityExceptionHandler
  - ResourceNotFoundException.java.                 # Optional: Truly shared utilities/config

```

**AVOID THIS (Package-by-Layer):**
```
com.culinary-notes
-   controller
    - RecipeController. java
    - UserController. java
- service
    - RecipeService. java
    - UserService. java
- repository
    - PostRepository. java
    - UserRepository.java
- model (or domain/entity)
- Recipe.java
- User.java
```

## Data Access

*   **Simple Applications/Queries:** For applications primarily dealing with straightforward, single-table CRUD operations or when direct SQL
*   **Standard/Complex Applications:** For applications with domain models involving relationships, complex queries, or where JPA features (c
*   **Default:** If unsure, lean towards Spring Data JPA for typical application development, but use 'JabcClient' when the overhead of JPA i

## HTTP Clients

* **Outgoing HTTP Requests:** Use the Spring Framework 6+ **'RestClient ** for making synchronous or asynchronous HTTP calls. // Avoid using t

## Java Language Features

*   **Data Carriers:** Use Java **Records** ('record') for immutable data transfer objects (DTOs), value objects, or simple data aggregates w
*   **Immutability:** Favor immutability for objects where appropriate, especially for DTOs and configuration properties.

## Spring Framework Best Practices

*   **Dependency Injection:** Use **constructor injection** for mandatory dependencies. Avoid field injection.
*   **Configuration:** Use 'application properties' or 'application.yml' (preffered) for application configuration. Leverage Spring Boot's externalized c
*   **Error Handling:** Implement consistent exception handling, potentially using '@ControllerAdvice' and custom exception classes. Provide
*   **Logging:** Use SLF4j with a suitable backend (like Logback, included by default in Spring Boot starters) for logging. Write clear and i


## Testing

*   **Unit Tests:** Write unit tests for services and components using JUnit 5 and Mockito.
*   **Integration Tests:** Write integration tests using '@SpringBootTest'. For database interactions, consider using Testcontainers or an in
*   **Test Location:** Place tests in the standard 'src/test/java' directory, mirroring the source package structure.

## General Code Quality

*   **Readability:** Write clean, readable, and maintainable code.
*   **Comments:** Add comments only where necessary to explain complex logic or non-obvious decisions. Code should be self-documenting where
*   **API Design:** Design RESTful APIs with clear resource naming, proper HTTP methods, and consistent request/response formats.
