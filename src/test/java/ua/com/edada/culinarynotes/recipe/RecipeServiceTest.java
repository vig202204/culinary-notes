package ua.com.edada.culinarynotes.recipe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe recipe1;
    private Recipe recipe2;

    @BeforeEach
    void setUp() {
        // Create test recipes
        recipe1 = Recipe.builder()
                .id(1L)
                .title("Chocolate Cake")
                .description("Delicious chocolate cake")
                .instructions("Mix and bake")
                .preparationTimeMinutes(15)
                .cookingTimeMinutes(30)
                .servings(8)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        recipe2 = Recipe.builder()
                .id(2L)
                .title("Vanilla Cake")
                .description("Classic vanilla cake")
                .instructions("Mix and bake slowly")
                .preparationTimeMinutes(20)
                .cookingTimeMinutes(35)
                .servings(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllRecipes_ShouldReturnAllRecipes() {
        // Arrange
        when(recipeRepository.findAll()).thenReturn(Arrays.asList(recipe1, recipe2));

        // Act
        List<Recipe> recipes = recipeService.getAllRecipes();

        // Assert
        assertThat(recipes).hasSize(2);
        assertThat(recipes).contains(recipe1, recipe2);
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    void getRecipeById_WithExistingId_ShouldReturnRecipe() {
        // Arrange
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe1));

        // Act
        Optional<Recipe> result = recipeService.getRecipeById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(recipe1);
        verify(recipeRepository, times(1)).findById(1L);
    }

    @Test
    void getRecipeById_WithNonExistingId_ShouldReturnEmpty() {
        // Arrange
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<Recipe> result = recipeService.getRecipeById(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(recipeRepository, times(1)).findById(999L);
    }

    @Test
    void searchRecipesByTitle_ShouldReturnMatchingRecipes() {
        // Arrange
        when(recipeRepository.findByTitleContainingIgnoreCase("Chocolate"))
                .thenReturn(List.of(recipe1));

        // Act
        List<Recipe> result = recipeService.searchRecipesByTitle("Chocolate");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result).contains(recipe1);
        verify(recipeRepository, times(1)).findByTitleContainingIgnoreCase("Chocolate");
    }

    @Test
    void saveRecipe_ShouldSaveAndReturnRecipe() {
        // Arrange
        Recipe newRecipe = Recipe.builder()
                .title("New Recipe")
                .description("New description")
                .build();
        
        when(recipeRepository.save(any(Recipe.class))).thenReturn(newRecipe);

        // Act
        Recipe savedRecipe = recipeService.saveRecipe(newRecipe);

        // Assert
        assertThat(savedRecipe).isNotNull();
        assertThat(savedRecipe.getTitle()).isEqualTo("New Recipe");
        verify(recipeRepository, times(1)).save(newRecipe);
    }

    @Test
    void deleteRecipe_ShouldCallRepositoryDeleteById() {
        // Arrange
        doNothing().when(recipeRepository).deleteById(anyLong());

        // Act
        recipeService.deleteRecipe(1L);

        // Assert
        verify(recipeRepository, times(1)).deleteById(1L);
    }
}