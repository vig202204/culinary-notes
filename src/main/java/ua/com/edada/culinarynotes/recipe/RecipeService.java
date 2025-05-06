package ua.com.edada.culinarynotes.recipe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Transactional(readOnly = true)
    public List<Recipe> getAllRecipes() {
        log.debug("Getting all recipes");
        return recipeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Recipe> getRecipeById(Long id) {
        log.debug("Getting recipe with id: {}", id);
        return recipeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Recipe> searchRecipesByTitle(String title) {
        log.debug("Searching recipes with title containing: {}", title);
        return recipeRepository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional
    public Recipe saveRecipe(Recipe recipe) {
        log.debug("Saving recipe: {}", recipe.getTitle());
        return recipeRepository.save(recipe);
    }

    @Transactional
    public void deleteRecipe(Long id) {
        log.debug("Deleting recipe with id: {}", id);
        recipeRepository.deleteById(id);
    }
}