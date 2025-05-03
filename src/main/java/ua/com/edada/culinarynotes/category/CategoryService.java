package ua.com.edada.culinarynotes.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.edada.culinarynotes.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        log.debug("Getting all categories");
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        log.debug("Getting category with id: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {
        log.debug("Getting category with name: {}", name);
        return categoryRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<Category> searchCategoriesByName(String name) {
        log.debug("Searching categories with name containing: {}", name);
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Category createCategory(Category category) {
        log.debug("Creating new category: {}", category.getName());
        
        if (categoryRepository.existsByName(category.getName())) {
            log.error("Category name already exists: {}", category.getName());
            throw new IllegalArgumentException("Category name already exists");
        }
        
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        log.debug("Updating category with id: {}", id);
        
        Category category = getCategoryById(id);
        
        if (!category.getName().equals(categoryDetails.getName()) && 
                categoryRepository.existsByName(categoryDetails.getName())) {
            log.error("Category name already exists: {}", categoryDetails.getName());
            throw new IllegalArgumentException("Category name already exists");
        }
        
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.debug("Deleting category with id: {}", id);
        
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", "id", id);
        }
        
        categoryRepository.deleteById(id);
    }
}