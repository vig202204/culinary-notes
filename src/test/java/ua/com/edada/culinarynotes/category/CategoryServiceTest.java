package ua.com.edada.culinarynotes.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.edada.culinarynotes.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        // Create test categories
        category1 = Category.builder()
                .id(1L)
                .name("Desserts")
                .description("Sweet treats and desserts")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        category2 = Category.builder()
                .id(2L)
                .name("Main Courses")
                .description("Main dishes for lunch and dinner")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        // Act
        List<Category> categories = categoryService.getAllCategories();

        // Assert
        assertThat(categories).hasSize(2);
        assertThat(categories).contains(category1, category2);
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoryById_WithExistingId_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));

        // Act
        Category result = categoryService.getCategoryById(1L);

        // Assert
        assertThat(result).isEqualTo(category1);
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.getCategoryById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id: '999'");
        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    void getCategoryByName_WithExistingName_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findByName("Desserts")).thenReturn(Optional.of(category1));

        // Act
        Optional<Category> result = categoryService.getCategoryByName("Desserts");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(category1);
        verify(categoryRepository, times(1)).findByName("Desserts");
    }

    @Test
    void getCategoryByName_WithNonExistingName_ShouldReturnEmpty() {
        // Arrange
        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.getCategoryByName("Nonexistent");

        // Assert
        assertThat(result).isEmpty();
        verify(categoryRepository, times(1)).findByName("Nonexistent");
    }

    @Test
    void searchCategoriesByName_ShouldReturnMatchingCategories() {
        // Arrange
        when(categoryRepository.findByNameContainingIgnoreCase("Dessert"))
                .thenReturn(List.of(category1));

        // Act
        List<Category> result = categoryService.searchCategoriesByName("Dessert");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result).contains(category1);
        verify(categoryRepository, times(1)).findByNameContainingIgnoreCase("Dessert");
    }

    @Test
    void createCategory_WithUniqueName_ShouldCreateCategory() {
        // Arrange
        Category newCategory = Category.builder()
                .name("Appetizers")
                .description("Starters and small bites")
                .build();
        
        when(categoryRepository.existsByName("Appetizers")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        // Act
        Category createdCategory = categoryService.createCategory(newCategory);

        // Assert
        assertThat(createdCategory).isNotNull();
        assertThat(createdCategory.getName()).isEqualTo("Appetizers");
        verify(categoryRepository, times(1)).existsByName("Appetizers");
        verify(categoryRepository, times(1)).save(newCategory);
    }

    @Test
    void createCategory_WithExistingName_ShouldThrowException() {
        // Arrange
        Category newCategory = Category.builder()
                .name("Desserts")
                .description("New desserts description")
                .build();
        
        when(categoryRepository.existsByName("Desserts")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categoryService.createCategory(newCategory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category name already exists");
        verify(categoryRepository, times(1)).existsByName("Desserts");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_WithValidChanges_ShouldUpdateCategory() {
        // Arrange
        Category categoryDetails = Category.builder()
                .name("Updated Desserts")
                .description("Updated desserts description")
                .build();
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.existsByName("Updated Desserts")).thenReturn(false);
        
        Category updatedCategory = Category.builder()
                .id(1L)
                .name("Updated Desserts")
                .description("Updated desserts description")
                .createdAt(category1.getCreatedAt())
                .updatedAt(category1.getUpdatedAt())
                .build();
        
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        // Act
        Category result = categoryService.updateCategory(1L, categoryDetails);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Desserts");
        assertThat(result.getDescription()).isEqualTo("Updated desserts description");
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByName("Updated Desserts");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_WithExistingName_ShouldThrowException() {
        // Arrange
        Category categoryDetails = Category.builder()
                .name("Main Courses")  // This name belongs to category2
                .description("Updated description")
                .build();
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.existsByName("Main Courses")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categoryService.updateCategory(1L, categoryDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category name already exists");
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByName("Main Courses");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_WithExistingId_ShouldDeleteCategory() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCategory_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(categoryRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> categoryService.deleteCategory(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id: '999'");
        verify(categoryRepository, times(1)).existsById(999L);
        verify(categoryRepository, never()).deleteById(anyLong());
    }
}