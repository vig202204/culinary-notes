package ua.com.edada.culinarynotes.recipe;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.edada.culinarynotes.recipe.dto.RecipeCreateRequest;
import ua.com.edada.culinarynotes.recipe.dto.RecipeUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Slf4j
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        log.info("REST request to get all recipes");
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        log.info("REST request to get recipe with id: {}", id);
        return recipeService.getRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam String title) {
        log.info("REST request to search recipes with title containing: {}", title);
        return ResponseEntity.ok(recipeService.searchRecipesByTitle(title));
    }

    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody RecipeCreateRequest request) {
        log.info("REST request to create a new recipe: {}", request.title());
        
        Recipe recipe = Recipe.builder()
                .title(request.title())
                .description(request.description())
                .instructions(request.instructions())
                .preparationTimeMinutes(request.preparationTimeMinutes())
                .cookingTimeMinutes(request.cookingTimeMinutes())
                .servings(request.servings())
                .build();
        
        return new ResponseEntity<>(recipeService.saveRecipe(recipe), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody RecipeUpdateRequest request) {
        log.info("REST request to update recipe with id: {}", id);
        
        return recipeService.getRecipeById(id)
                .map(existingRecipe -> {
                    if (request.title() != null) {
                        existingRecipe.setTitle(request.title());
                    }
                    if (request.description() != null) {
                        existingRecipe.setDescription(request.description());
                    }
                    if (request.instructions() != null) {
                        existingRecipe.setInstructions(request.instructions());
                    }
                    if (request.preparationTimeMinutes() != null) {
                        existingRecipe.setPreparationTimeMinutes(request.preparationTimeMinutes());
                    }
                    if (request.cookingTimeMinutes() != null) {
                        existingRecipe.setCookingTimeMinutes(request.cookingTimeMinutes());
                    }
                    if (request.servings() != null) {
                        existingRecipe.setServings(request.servings());
                    }
                    
                    return ResponseEntity.ok(recipeService.saveRecipe(existingRecipe));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        log.info("REST request to delete recipe with id: {}", id);
        
        if (recipeService.getRecipeById(id).isPresent()) {
            recipeService.deleteRecipe(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}