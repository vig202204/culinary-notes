package ua.com.edada.culinarynotes.ingredient;

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
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService;

    private Ingredient ingredient1;
    private Ingredient ingredient2;

    @BeforeEach
    void setUp() {
        // Create test ingredients
        ingredient1 = Ingredient.builder()
                .id(1L)
                .name("Flour")
                .description("All-purpose flour")
                .unit("cups")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ingredient2 = Ingredient.builder()
                .id(2L)
                .name("Sugar")
                .description("Granulated sugar")
                .unit("grams")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllIngredients_ShouldReturnAllIngredients() {
        // Arrange
        when(ingredientRepository.findAll()).thenReturn(Arrays.asList(ingredient1, ingredient2));

        // Act
        List<Ingredient> ingredients = ingredientService.getAllIngredients();

        // Assert
        assertThat(ingredients).hasSize(2);
        assertThat(ingredients).contains(ingredient1, ingredient2);
        verify(ingredientRepository, times(1)).findAll();
    }

    @Test
    void getIngredientById_WithExistingId_ShouldReturnIngredient() {
        // Arrange
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient1));

        // Act
        Ingredient result = ingredientService.getIngredientById(1L);

        // Assert
        assertThat(result).isEqualTo(ingredient1);
        verify(ingredientRepository, times(1)).findById(1L);
    }

    @Test
    void getIngredientById_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(ingredientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> ingredientService.getIngredientById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ingredient not found with id: '999'");
        verify(ingredientRepository, times(1)).findById(999L);
    }

    @Test
    void getIngredientByNameAndUnit_WithExistingNameAndUnit_ShouldReturnIngredient() {
        // Arrange
        when(ingredientRepository.findByNameAndUnit("Flour", "cups")).thenReturn(Optional.of(ingredient1));

        // Act
        Optional<Ingredient> result = ingredientService.getIngredientByNameAndUnit("Flour", "cups");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(ingredient1);
        verify(ingredientRepository, times(1)).findByNameAndUnit("Flour", "cups");
    }

    @Test
    void getIngredientByNameAndUnit_WithNonExistingNameAndUnit_ShouldReturnEmpty() {
        // Arrange
        when(ingredientRepository.findByNameAndUnit(anyString(), anyString())).thenReturn(Optional.empty());

        // Act
        Optional<Ingredient> result = ingredientService.getIngredientByNameAndUnit("Nonexistent", "unit");

        // Assert
        assertThat(result).isEmpty();
        verify(ingredientRepository, times(1)).findByNameAndUnit("Nonexistent", "unit");
    }

    @Test
    void searchIngredientsByName_ShouldReturnMatchingIngredients() {
        // Arrange
        when(ingredientRepository.findByNameContainingIgnoreCase("Flour"))
                .thenReturn(List.of(ingredient1));

        // Act
        List<Ingredient> result = ingredientService.searchIngredientsByName("Flour");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result).contains(ingredient1);
        verify(ingredientRepository, times(1)).findByNameContainingIgnoreCase("Flour");
    }

    @Test
    void createIngredient_WithUniqueNameAndUnit_ShouldCreateIngredient() {
        // Arrange
        Ingredient newIngredient = Ingredient.builder()
                .name("Salt")
                .description("Table salt")
                .unit("teaspoons")
                .build();
        
        when(ingredientRepository.existsByNameAndUnit("Salt", "teaspoons")).thenReturn(false);
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(newIngredient);

        // Act
        Ingredient createdIngredient = ingredientService.createIngredient(newIngredient);

        // Assert
        assertThat(createdIngredient).isNotNull();
        assertThat(createdIngredient.getName()).isEqualTo("Salt");
        assertThat(createdIngredient.getUnit()).isEqualTo("teaspoons");
        verify(ingredientRepository, times(1)).existsByNameAndUnit("Salt", "teaspoons");
        verify(ingredientRepository, times(1)).save(newIngredient);
    }

    @Test
    void createIngredient_WithExistingNameAndUnit_ShouldThrowException() {
        // Arrange
        Ingredient newIngredient = Ingredient.builder()
                .name("Flour")
                .description("New flour description")
                .unit("cups")
                .build();
        
        when(ingredientRepository.existsByNameAndUnit("Flour", "cups")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> ingredientService.createIngredient(newIngredient))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ingredient already exists with this name and unit");
        verify(ingredientRepository, times(1)).existsByNameAndUnit("Flour", "cups");
        verify(ingredientRepository, never()).save(any(Ingredient.class));
    }

    @Test
    void updateIngredient_WithValidChanges_ShouldUpdateIngredient() {
        // Arrange
        Ingredient ingredientDetails = Ingredient.builder()
                .name("Whole Wheat Flour")
                .description("Whole wheat flour for baking")
                .unit("cups")
                .build();
        
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient1));
        when(ingredientRepository.existsByNameAndUnit("Whole Wheat Flour", "cups")).thenReturn(false);
        
        Ingredient updatedIngredient = Ingredient.builder()
                .id(1L)
                .name("Whole Wheat Flour")
                .description("Whole wheat flour for baking")
                .unit("cups")
                .createdAt(ingredient1.getCreatedAt())
                .updatedAt(ingredient1.getUpdatedAt())
                .build();
        
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(updatedIngredient);

        // Act
        Ingredient result = ingredientService.updateIngredient(1L, ingredientDetails);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Whole Wheat Flour");
        assertThat(result.getDescription()).isEqualTo("Whole wheat flour for baking");
        assertThat(result.getUnit()).isEqualTo("cups");
        verify(ingredientRepository, times(1)).findById(1L);
        verify(ingredientRepository, times(1)).existsByNameAndUnit("Whole Wheat Flour", "cups");
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    void updateIngredient_WithExistingNameAndUnit_ShouldThrowException() {
        // Arrange
        Ingredient ingredientDetails = Ingredient.builder()
                .name("Sugar")  // This name belongs to ingredient2
                .description("Updated description")
                .unit("grams")  // This unit belongs to ingredient2
                .build();
        
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient1));
        when(ingredientRepository.existsByNameAndUnit("Sugar", "grams")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> ingredientService.updateIngredient(1L, ingredientDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ingredient already exists with this name and unit");
        verify(ingredientRepository, times(1)).findById(1L);
        verify(ingredientRepository, times(1)).existsByNameAndUnit("Sugar", "grams");
        verify(ingredientRepository, never()).save(any(Ingredient.class));
    }

    @Test
    void deleteIngredient_WithExistingId_ShouldDeleteIngredient() {
        // Arrange
        when(ingredientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ingredientRepository).deleteById(1L);

        // Act
        ingredientService.deleteIngredient(1L);

        // Assert
        verify(ingredientRepository, times(1)).existsById(1L);
        verify(ingredientRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteIngredient_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(ingredientRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> ingredientService.deleteIngredient(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ingredient not found with id: '999'");
        verify(ingredientRepository, times(1)).existsById(999L);
        verify(ingredientRepository, never()).deleteById(anyLong());
    }
}