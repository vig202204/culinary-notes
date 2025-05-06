package ua.com.edada.culinarynotes.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.edada.culinarynotes.exception.GlobalExceptionHandler;
import ua.com.edada.culinarynotes.recipe.dto.RecipeCreateRequest;
import ua.com.edada.culinarynotes.recipe.dto.RecipeUpdateRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController recipeController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Recipe recipe1;
    private Recipe recipe2;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders
                .standaloneSetup(recipeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

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
    void getAllRecipes_ShouldReturnAllRecipes() throws Exception {
        // Arrange
        when(recipeService.getAllRecipes()).thenReturn(Arrays.asList(recipe1, recipe2));

        // Act & Assert
        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Chocolate Cake")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Vanilla Cake")));

        verify(recipeService, times(1)).getAllRecipes();
    }

    @Test
    void getRecipeById_WithExistingId_ShouldReturnRecipe() throws Exception {
        // Arrange
        when(recipeService.getRecipeById(1L)).thenReturn(Optional.of(recipe1));

        // Act & Assert
        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Chocolate Cake")))
                .andExpect(jsonPath("$.description", is("Delicious chocolate cake")))
                .andExpect(jsonPath("$.instructions", is("Mix and bake")))
                .andExpect(jsonPath("$.preparationTimeMinutes", is(15)))
                .andExpect(jsonPath("$.cookingTimeMinutes", is(30)))
                .andExpect(jsonPath("$.servings", is(8)));

        verify(recipeService, times(1)).getRecipeById(1L);
    }

    @Test
    void getRecipeById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(recipeService.getRecipeById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/recipes/999"))
                .andExpect(status().isNotFound());

        verify(recipeService, times(1)).getRecipeById(999L);
    }

    @Test
    void searchRecipes_ShouldReturnMatchingRecipes() throws Exception {
        // Arrange
        when(recipeService.searchRecipesByTitle("Chocolate")).thenReturn(Arrays.asList(recipe1));

        // Act & Assert
        mockMvc.perform(get("/api/recipes/search").param("title", "Chocolate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Chocolate Cake")));

        verify(recipeService, times(1)).searchRecipesByTitle("Chocolate");
    }

    @Test
    void createRecipe_WithValidData_ShouldCreateRecipe() throws Exception {
        // Arrange
        RecipeCreateRequest request = new RecipeCreateRequest(
                "New Recipe",
                "New description",
                "New instructions",
                10,
                20,
                4
        );

        Recipe newRecipe = Recipe.builder()
                .id(3L)
                .title("New Recipe")
                .description("New description")
                .instructions("New instructions")
                .preparationTimeMinutes(10)
                .cookingTimeMinutes(20)
                .servings(4)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(recipeService.saveRecipe(any(Recipe.class))).thenReturn(newRecipe);

        // Act & Assert
        mockMvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("New Recipe")))
                .andExpect(jsonPath("$.description", is("New description")))
                .andExpect(jsonPath("$.instructions", is("New instructions")))
                .andExpect(jsonPath("$.preparationTimeMinutes", is(10)))
                .andExpect(jsonPath("$.cookingTimeMinutes", is(20)))
                .andExpect(jsonPath("$.servings", is(4)));

        verify(recipeService, times(1)).saveRecipe(any(Recipe.class));
    }

    @Test
    void createRecipe_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        RecipeCreateRequest request = new RecipeCreateRequest(
                "", // Invalid: title is required and must be at least 3 characters
                "New description",
                "New instructions",
                -5, // Invalid: preparation time cannot be negative
                20,
                0 // Invalid: servings must be at least 1
        );

        // Act & Assert
        mockMvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(recipeService, never()).saveRecipe(any(Recipe.class));
    }

    @Test
    void updateRecipe_WithValidData_ShouldUpdateRecipe() throws Exception {
        // Arrange
        RecipeUpdateRequest request = new RecipeUpdateRequest(
                "Updated Recipe",
                "Updated description",
                "Updated instructions",
                25,
                40,
                6
        );

        Recipe updatedRecipe = Recipe.builder()
                .id(1L)
                .title("Updated Recipe")
                .description("Updated description")
                .instructions("Updated instructions")
                .preparationTimeMinutes(25)
                .cookingTimeMinutes(40)
                .servings(6)
                .createdAt(recipe1.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(recipeService.getRecipeById(1L)).thenReturn(Optional.of(recipe1));
        when(recipeService.saveRecipe(any(Recipe.class))).thenReturn(updatedRecipe);

        // Act & Assert
        mockMvc.perform(put("/api/recipes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Recipe")))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.instructions", is("Updated instructions")))
                .andExpect(jsonPath("$.preparationTimeMinutes", is(25)))
                .andExpect(jsonPath("$.cookingTimeMinutes", is(40)))
                .andExpect(jsonPath("$.servings", is(6)));

        verify(recipeService, times(1)).getRecipeById(1L);
        verify(recipeService, times(1)).saveRecipe(any(Recipe.class));
    }

    @Test
    void updateRecipe_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        RecipeUpdateRequest request = new RecipeUpdateRequest(
                "Updated Recipe",
                "Updated description",
                "Updated instructions",
                25,
                40,
                6
        );

        when(recipeService.getRecipeById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/recipes/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(recipeService, times(1)).getRecipeById(999L);
        verify(recipeService, never()).saveRecipe(any(Recipe.class));
    }

    @Test
    void deleteRecipe_WithExistingId_ShouldDeleteRecipe() throws Exception {
        // Arrange
        when(recipeService.getRecipeById(1L)).thenReturn(Optional.of(recipe1));
        doNothing().when(recipeService).deleteRecipe(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/recipes/1"))
                .andExpect(status().isNoContent());

        verify(recipeService, times(1)).getRecipeById(1L);
        verify(recipeService, times(1)).deleteRecipe(1L);
    }

    @Test
    void deleteRecipe_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(recipeService.getRecipeById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/recipes/999"))
                .andExpect(status().isNotFound());

        verify(recipeService, times(1)).getRecipeById(999L);
        verify(recipeService, never()).deleteRecipe(anyLong());
    }
}
