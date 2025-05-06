1. Examine the current project structure to understand what we're working with
2. Check the src directory to see what files exist
- Check src/main
- Check src/main/java
- Check src/main/java/ua
- Check src/main/java/ua/com
- Check src/main/java/ua/com/edada
- Check src/main/java/ua/com/edada/culinarynotes
- Check src/main/resources
- Check src/test
- Check src/test/resources
3. Update the pom.xml to include missing dependencies:
- PostgreSQL
- Spring Data JPA
- Testcontainers for testing
4. Create Docker Compose file for PostgreSQL
5. Create application configuration (application.yml)
6. Create the main application class
7. Create the ServletInitializer class
8. Create the test class for the main application
9. Implement the package-by-feature structure as specified
- Create recipe feature package
- Create recipe feature files
- Recipe entity
- RecipeRepository
- RecipeService
- DTO classes
- RecipeCreateRequest
- RecipeUpdateRequest
- RecipeController
- Create exception package
- ResourceNotFoundException
- GlobalExceptionHandler
- Create common package
- config
- WebConfig
- util
- DateUtils
- Create file feature package
- FileStorageService
- Create user feature package
- User entity
- UserRepository
- UserService
- Create category feature package
- Category entity
- CategoryRepository
- CategoryService
- Create ingredient feature package
10. Set up the core technologies:
- Java 21 (or latest LTS)
- Spring Boot 3.x
- Maven configuration
- PostgreSQL support
- Lombok
- Docker Compose for PostgreSQL
11. Create basic components for the culinary notes application:
- Recipe feature
- User feature
- Category feature
- Ingredient feature
- File storage feature
- Common utilities and exception handling
12. Configure logging with SLF4j
13. Set up testing infrastructure