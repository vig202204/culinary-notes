package ua.com.edada.culinarynotes.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.edada.culinarynotes.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "getAllCategories");

        if (log.isDebugEnabled()) {
            log.debug("Getting all categories. OperationId: {}, Time: {}", 
                    operationId, LocalDateTime.now().format(FORMATTER));
        }

        List<Category> categories = categoryRepository.findAll();

        if (log.isDebugEnabled()) {
            log.debug("Found {} categories. OperationId: {}", 
                    categories.size(), operationId);
        }

        MDC.clear();
        return categories;
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("categoryId", String.valueOf(id));
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "getCategoryById");

        if (log.isDebugEnabled()) {
            log.debug("Getting category with id: {}. OperationId: {}, Time: {}", 
                    id, operationId, LocalDateTime.now().format(FORMATTER));
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    if (log.isErrorEnabled()) {
                        log.error("Category not found with id: {}. OperationId: {}", 
                                id, operationId);
                    }
                    MDC.clear();
                    return new ResourceNotFoundException("Category", "id", id);
                });

        if (log.isDebugEnabled()) {
            log.debug("Category found: {}. OperationId: {}", 
                    category.getName(), operationId);
        }

        MDC.clear();
        return category;
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("categoryName", name);
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "getCategoryByName");

        if (log.isDebugEnabled()) {
            log.debug("Getting category with name: {}. OperationId: {}, Time: {}", 
                    name, operationId, LocalDateTime.now().format(FORMATTER));
        }

        Optional<Category> category = categoryRepository.findByName(name);

        if (log.isDebugEnabled()) {
            log.debug("Category found: {}. OperationId: {}", 
                    category.isPresent(), operationId);
        }

        MDC.clear();
        return category;
    }

    @Transactional(readOnly = true)
    public List<Category> searchCategoriesByName(String name) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("searchName", name);
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "searchCategoriesByName");

        if (log.isDebugEnabled()) {
            log.debug("Searching categories with name containing: {}. OperationId: {}, Time: {}", 
                    name, operationId, LocalDateTime.now().format(FORMATTER));
        }

        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(name);

        if (log.isDebugEnabled()) {
            log.debug("Found {} categories matching name: {}. OperationId: {}", 
                    categories.size(), name, operationId);
        }

        MDC.clear();
        return categories;
    }

    @Transactional
    public Category createCategory(Category category) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("categoryName", category.getName());
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "createCategory");

        if (log.isDebugEnabled()) {
            log.debug("Creating new category: {}. OperationId: {}, Time: {}", 
                    category.getName(), operationId, LocalDateTime.now().format(FORMATTER));
        }

        if (categoryRepository.existsByName(category.getName())) {
            if (log.isErrorEnabled()) {
                log.error("Category name already exists: {}. OperationId: {}", 
                        category.getName(), operationId);
            }
            MDC.clear();
            throw new IllegalArgumentException("Category name already exists");
        }

        Category savedCategory = categoryRepository.save(category);

        if (log.isDebugEnabled()) {
            log.debug("Category created with id: {}. OperationId: {}", 
                    savedCategory.getId(), operationId);
        }

        MDC.clear();
        return savedCategory;
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("categoryId", String.valueOf(id));
        MDC.put("categoryName", categoryDetails.getName());
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "updateCategory");

        if (log.isDebugEnabled()) {
            log.debug("Updating category with id: {}. OperationId: {}, Time: {}", 
                    id, operationId, LocalDateTime.now().format(FORMATTER));
        }

        Category category = getCategoryById(id);

        if (!category.getName().equals(categoryDetails.getName()) && 
                categoryRepository.existsByName(categoryDetails.getName())) {
            if (log.isErrorEnabled()) {
                log.error("Category name already exists: {}. OperationId: {}", 
                        categoryDetails.getName(), operationId);
            }
            MDC.clear();
            throw new IllegalArgumentException("Category name already exists");
        }

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());

        Category updatedCategory = categoryRepository.save(category);

        if (log.isDebugEnabled()) {
            log.debug("Category updated: {}. OperationId: {}", 
                    updatedCategory.getName(), operationId);
        }

        MDC.clear();
        return updatedCategory;
    }

    @Transactional
    public void deleteCategory(Long id) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("categoryId", String.valueOf(id));
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "deleteCategory");

        if (log.isDebugEnabled()) {
            log.debug("Deleting category with id: {}. OperationId: {}, Time: {}", 
                    id, operationId, LocalDateTime.now().format(FORMATTER));
        }

        if (!categoryRepository.existsById(id)) {
            if (log.isErrorEnabled()) {
                log.error("Category not found with id: {}. OperationId: {}", 
                        id, operationId);
            }
            MDC.clear();
            throw new ResourceNotFoundException("Category", "id", id);
        }

        categoryRepository.deleteById(id);

        if (log.isDebugEnabled()) {
            log.debug("Category deleted. OperationId: {}", operationId);
        }

        MDC.clear();
    }
}
