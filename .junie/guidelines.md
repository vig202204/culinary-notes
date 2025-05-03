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
  **PREFERRED  THIS (Package-by-Feature):**
```
ua.com.edada.culinarynotes
  ├── recipe/
  │    ├── RecipeController.java.          # Controller for Recipe
  │    ├── RecipeService.java              # Service logic for Recipe
  │    ├── RecipeRepository.java           # Data access for Recipe
  │    ├── Recipe.java
  │    └── dto/                            # Data Transfer Objects specific to Recipe
  │          ├── RecipeCreateRequest.java
  │          └── RecipeUpdateRequest.java
  ├── user/                                 # Feature: Users
  │    ├── UserController.java              # Controller for User
  │    ├── UserService. java                # Service logic for User
  │    ├── UserRepository. java.            # Data access for User
  │    ├── User. java.                      # Domain/Entity for User
  │    └── dto/                             # Data Transfer Objects specific to Recipe
  │          ├── UserCreateRequest.java
  │          └── UserUpdateRequest.java  
  ├── ingredient/
  │    ├── IngredientController.java
  │    └── ...
  ├── category/
  │    ├── CategoryController.java
  │    └── ...
  ├── user/
  │    ├── UserController.java
  │    └── ...
  ├── file/
  │    ├── FileStorageService.java
  │    └── ...
  ├── common/
  │    ├── config/
  │    ├── exception/
  │    └── util/
  └── exception
       ├── GlobalExceptionHandler                          # Extends ResponseEntityExceptionHandler
       └── ResourceNotFoundException.java.                 # Optional: Truly shared utilities/config
```

[//]: # (Для проєкту системи керування кулінарними рецептами AI рекомендує **підхід Package-by-Feature** з наступних причин:)
[//]: # (1. **Доменна орієнтованість**: Кулінарні рецепти — це чітка доменна область з природнім поділом на функціональні компоненти &#40;рецепти, інгредієнти, категорії, користувачі тощо&#41;.)
[//]: # (2. **Масштабованість**: Якщо ваш проєкт розвиватиметься, додаючи нові функції &#40;наприклад, планування меню, покупки продуктів&#41;, їх легше додавати як окремі функціональні модулі.)
[//]: # (3. **Підтримуваність коду**: Коли вам потрібно буде змінити логіку роботи з рецептами, всі відповідні класи будуть зібрані в одному місці.)
[//]: # (4. **Простота навігації**: Розробникам буде простіше зрозуміти, де шукати код, пов'язаний з конкретною функціональністю.)


**AVOID THIS (Package-by-Layer):**
```
ua.com.edada.culinarynotes
- controller
  - RecipeController. java
  - UserController. java
- service
  - RecipeService. java
  - UserService. java
- repository
  - RecipeRepository. java
  - UserRepository.java
- model (or domain/entity)
  - dto
```

## Data Access

*   **Simple Applications/Queries:** For applications primarily dealing with straightforward, single-table CRUD operations or when direct SQL
*   **Standard/Complex Applications:** For applications with domain models involving relationships, complex queries, or where JPA features (c
*   **Default:** If unsure, lean towards Spring Data JPA for typical application development, but use 'JabcClient' when the overhead of JPA i

## HTTP Clients

* **Outgoing HTTP Requests:** Use the Spring Framework 6+ **RestClient** for making synchronous or asynchronous HTTP calls. Avoid using the **RestTemplate**.

## Java Language Features

*   **Data Carriers:** Use Java **Records** ('record') for immutable data transfer objects (DTOs), value objects, or simple data aggregates w
*   **Immutability:** Favor immutability for objects where appropriate, especially for DTOs and configuration properties.

## Spring Framework Best Practices

*   **Dependency Injection:** Use **constructor injection** for mandatory dependencies. Avoid field injection.
*   **Configuration:** Use 'application properties' or 'application.yml' (preffered) for application configuration. Leverage Spring Boot's externalized c
*   **Error Handling:** Implement consistent exception handling, potentially using '@ControllerAdvice' and custom exception classes. Provide

## Logging
*  **Use SLF4j** with a suitable backend (like Logback, included by default in Spring Boot starters) for logging.
* Use Mapped Diagnostic Context (MDC).
* Write clear and parametrized informative log-message including:
    1. Use id's (ID operation, ID user and etc.).
    2. Add time.
    3. Add context operation. Check level of log before create log-message ex `if (log.isDebugEnabled()){}`

## Testing

*   **Unit Tests:** Write unit tests for services and components using JUnit 5 and Mockito.
*   **Integration Tests:** Write integration tests using '@SpringBootTest'. For database interactions, consider using Testcontainers or an in
*   **Test Location:** Place tests in the standard 'src/test/java' directory, mirroring the source package structure.

## General Code Quality

*   **Readability:** Write clean, readable, and maintainable code.
*   **Comments:** Add comments only where necessary to explain complex logic or non-obvious decisions. Code should be self-documenting where
*   **API Design:** Design RESTfull APIs with clear resource naming, proper HTTP methods, and consistent request/response formats.
