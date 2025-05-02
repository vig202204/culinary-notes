# Culinary Notes Application Improvement Plan

## Introduction
This document outlines a comprehensive plan for developing the Culinary Notes application, a digital recipe book designed for storing, organizing, and sharing family recipes. The plan is based on the requirements specified in `requirements.md` and follows the development guidelines established for the project.

## 1. Architecture and Project Structure

### Current State
The application is currently in a very early stage with only the basic Spring Boot skeleton in place. It uses Spring Boot 3.4.4 with Java 21, Thymeleaf for templates, and is configured for WAR deployment.

### Proposed Improvements
1. **Implement Package-by-Feature Structure**
   - **Rationale**: Package-by-feature improves modularity, makes code navigation easier, and reduces coupling between unrelated components as specified in the guidelines.
   - **Implementation**: Organize code into the following feature packages:
     ```
     edada.com.ua.culinarynotes
     ├── recipe                  # Recipe feature
     │   ├── RecipeController.java
     │   ├── RecipeService.java
     │   ├── RecipeRepository.java
     │   ├── Recipe.java
     │   └── dto
     │       ├── RecipeCreateRequest.java
     │       └── RecipeResponse.java
     ├── category                # Category feature
     │   ├── CategoryController.java
     │   ├── CategoryService.java
     │   ├── CategoryRepository.java
     │   ├── Category.java
     │   └── dto
     │       └── CategoryDto.java
     ├── user                    # User management feature
     │   ├── UserController.java
     │   ├── UserService.java
     │   ├── UserRepository.java
     │   ├── User.java
     │   └── dto
     │       ├── UserDto.java
     │       └── UserRegistrationRequest.java
     ├── security                # Security feature
     │   ├── SecurityConfig.java
     │   ├── JwtTokenProvider.java
     │   └── UserDetailsServiceImpl.java
     ├── common                  # Shared components
     │   ├── exception
     │   │   ├── GlobalExceptionHandler.java
     │   │   ├── ResourceNotFoundException.java
     │   │   └── ApiError.java
     │   └── config
     │       └── WebConfig.java
     └── CulinaryNotesApplication.java
     ```

2. **Database Configuration**
   - **Rationale**: PostgreSQL is specified in both requirements and guidelines for data storage.
   - **Implementation**: 
     - Add PostgreSQL dependencies to pom.xml
     - Configure database connection in application.yml
     - Set up Flyway for database migrations

3. **Application Configuration**
   - **Rationale**: Guidelines recommend using application.yml over properties files.
   - **Implementation**: 
     - Convert application.properties to application.yml
     - Structure configuration by environment (dev, test, prod)

## 2. Data Model Design

### Proposed Data Model
1. **Recipe Entity**
   - **Rationale**: Core entity representing a recipe with all required attributes from the requirements.
   - **Implementation**:
     ```java
     @Entity
     @Table(name = "recipes")
     public class Recipe {
         @Id
         @GeneratedValue(strategy = GenerationType.IDENTITY)
         private Long id;
         
         private String title;
         private String description;
         
         @Column(columnDefinition = "TEXT")
         private String ingredients;
         
         @Column(columnDefinition = "TEXT")
         private String instructions;
         
         private Integer prepTimeMinutes;
         private Integer cookTimeMinutes;
         private Integer servings;
         private String difficultyLevel;
         
         @ManyToMany
         @JoinTable(
             name = "recipe_categories",
             joinColumns = @JoinColumn(name = "recipe_id"),
             inverseJoinColumns = @JoinColumn(name = "category_id")
         )
         private Set<Category> categories = new HashSet<>();
         
         @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
         private List<RecipeImage> images = new ArrayList<>();
         
         private String notes;
         
         @ManyToOne
         @JoinColumn(name = "user_id")
         private User createdBy;
         
         private LocalDateTime createdAt;
         private LocalDateTime updatedAt;
     }
     ```

2. **Category Entity**
   - **Rationale**: Enables recipe categorization and filtering as required.
   - **Implementation**:
     ```java
     @Entity
     @Table(name = "categories")
     public class Category {
         @Id
         @GeneratedValue(strategy = GenerationType.IDENTITY)
         private Long id;
         
         @Column(unique = true)
         private String name;
         
         private String description;
         
         @ManyToMany(mappedBy = "categories")
         private Set<Recipe> recipes = new HashSet<>();
     }
     ```

3. **User Entity**
   - **Rationale**: Supports user management requirements including authentication and authorization.
   - **Implementation**:
     ```java
     @Entity
     @Table(name = "users")
     public class User {
         @Id
         @GeneratedValue(strategy = GenerationType.IDENTITY)
         private Long id;
         
         @Column(unique = true)
         private String username;
         
         @Column(unique = true)
         private String email;
         
         private String password;
         
         @Enumerated(EnumType.STRING)
         private Role role;
         
         private String firstName;
         private String lastName;
         
         @OneToMany(mappedBy = "createdBy")
         private List<Recipe> recipes = new ArrayList<>();
     }
     ```

## 3. API Design

### REST API Endpoints
1. **Recipe API**
   - **Rationale**: Provides CRUD operations for recipes as required.
   - **Implementation**:
     - `GET /api/recipes` - List all recipes with pagination and filtering
     - `GET /api/recipes/{id}` - Get recipe details
     - `POST /api/recipes` - Create new recipe
     - `PUT /api/recipes/{id}` - Update recipe
     - `DELETE /api/recipes/{id}` - Delete recipe
     - `GET /api/recipes/search?query={query}` - Search recipes

2. **Category API**
   - **Rationale**: Enables recipe categorization management.
   - **Implementation**:
     - `GET /api/categories` - List all categories
     - `GET /api/categories/{id}` - Get category details
     - `POST /api/categories` - Create new category
     - `PUT /api/categories/{id}` - Update category
     - `DELETE /api/categories/{id}` - Delete category
     - `GET /api/categories/{id}/recipes` - Get recipes by category

3. **User API**
   - **Rationale**: Supports user management requirements.
   - **Implementation**:
     - `POST /api/auth/register` - Register new user
     - `POST /api/auth/login` - User login
     - `GET /api/users/me` - Get current user profile
     - `PUT /api/users/me` - Update user profile

## 4. Frontend Implementation

### Thymeleaf Templates
1. **Layout Structure**
   - **Rationale**: Provides consistent UI across all pages.
   - **Implementation**:
     - Create base layout template with common header, footer, and navigation
     - Implement responsive design using Bootstrap or similar framework

2. **Recipe Views**
   - **Rationale**: Supports recipe management requirements.
   - **Implementation**:
     - Recipe list page with filtering and search
     - Recipe detail page with print-friendly option
     - Recipe creation/edit form with rich text editor for instructions
     - Image upload functionality

3. **User Interface**
   - **Rationale**: Ensures good user experience as required.
   - **Implementation**:
     - Responsive design for all device sizes
     - Intuitive navigation
     - Form validation with helpful error messages
     - Optimized for both desktop and mobile use

## 5. Security Implementation

### Authentication and Authorization
1. **Spring Security Configuration**
   - **Rationale**: Ensures application security as required.
   - **Implementation**:
     - Configure Spring Security for form-based authentication
     - Implement role-based access control (admin, contributor, reader)
     - Secure API endpoints with proper authorization

2. **Password Management**
   - **Rationale**: Ensures secure user authentication.
   - **Implementation**:
     - Use BCrypt password encoder
     - Implement password reset functionality

## 6. Testing Strategy

### Test Implementation
1. **Unit Tests**
   - **Rationale**: Ensures code quality and follows guidelines.
   - **Implementation**:
     - Write unit tests for all service classes using JUnit 5 and Mockito
     - Aim for high test coverage of business logic

2. **Integration Tests**
   - **Rationale**: Verifies components work together correctly.
   - **Implementation**:
     - Use @SpringBootTest for testing controllers with MockMvc
     - Implement database integration tests with Testcontainers

3. **End-to-End Tests**
   - **Rationale**: Validates complete user workflows.
   - **Implementation**:
     - Implement key user journey tests
     - Test critical paths like recipe creation and user registration

## 7. DevOps and Deployment

### Docker Configuration
1. **Docker Setup**
   - **Rationale**: Supports containerization requirement.
   - **Implementation**:
     - Create Dockerfile for the application
     - Set up docker-compose.yml with PostgreSQL and application services

2. **CI/CD Pipeline**
   - **Rationale**: Ensures reliable deployment process.
   - **Implementation**:
     - Configure GitHub Actions or similar CI/CD tool
     - Automate testing, building, and deployment

## 8. Implementation Phases

### Phase 1: Foundation
1. Set up project structure and architecture
2. Implement data model and database configuration
3. Create basic repository and service layers

### Phase 2: Core Functionality
1. Implement recipe management features
2. Create category management
3. Develop basic UI templates

### Phase 3: User Management
1. Implement security and authentication
2. Create user profile management
3. Set up role-based access control

### Phase 4: Enhanced Features
1. Add search and filtering functionality
2. Implement image upload and management
3. Create print-friendly views

### Phase 5: Finalization
1. Comprehensive testing
2. Performance optimization
3. Documentation
4. Deployment configuration

## Conclusion
This improvement plan provides a comprehensive roadmap for developing the Culinary Notes application according to the specified requirements and guidelines. By following this plan, we will create a robust, user-friendly digital recipe book that meets all the functional and technical requirements while adhering to best practices for Spring Boot development.