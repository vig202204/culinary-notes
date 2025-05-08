package ua.com.edada.culinarynotes.recipe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(readOnly = true)
    public List<Recipe> getAllRecipes() {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "getAllRecipes");

        if (log.isDebugEnabled()) {
            log.debug("Getting all recipes. OperationId: {}, Time: {}", 
                    operationId, LocalDateTime.now().format(FORMATTER));
        }

        List<Recipe> recipes = recipeRepository.findAll();

        if (log.isDebugEnabled()) {
            log.debug("Found {} recipes. OperationId: {}", 
                    recipes.size(), operationId);
        }

        MDC.clear();
        return recipes;
    }

    @Transactional(readOnly = true)
    public Optional<Recipe> getRecipeById(Long id) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("recipeId", String.valueOf(id));
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "getRecipeById");

        if (log.isDebugEnabled()) {
            log.debug("Getting recipe with id: {}. OperationId: {}, Time: {}", 
                    id, operationId, LocalDateTime.now().format(FORMATTER));
        }

        Optional<Recipe> recipe = recipeRepository.findById(id);

        if (log.isDebugEnabled()) {
            log.debug("Recipe found: {}. OperationId: {}", 
                    recipe.isPresent(), operationId);
        }

        MDC.clear();
        return recipe;
    }

    @Transactional(readOnly = true)
    public List<Recipe> searchRecipesByTitle(String title) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("searchTitle", title);
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "searchRecipesByTitle");

        if (log.isDebugEnabled()) {
            log.debug("Searching recipes with title containing: {}. OperationId: {}, Time: {}", 
                    title, operationId, LocalDateTime.now().format(FORMATTER));
        }

        List<Recipe> recipes = recipeRepository.findByTitleContainingIgnoreCase(title);

        if (log.isDebugEnabled()) {
            log.debug("Found {} recipes matching title: {}. OperationId: {}", 
                    recipes.size(), title, operationId);
        }

        MDC.clear();
        return recipes;
    }

    @Transactional
    public Recipe saveRecipe(Recipe recipe) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("recipeTitle", recipe.getTitle());
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "saveRecipe");

        if (log.isDebugEnabled()) {
            log.debug("Saving recipe: {}. OperationId: {}, Time: {}", 
                    recipe.getTitle(), operationId, LocalDateTime.now().format(FORMATTER));
        }

        Recipe savedRecipe = recipeRepository.save(recipe);

        if (log.isDebugEnabled()) {
            log.debug("Recipe saved with id: {}. OperationId: {}", 
                    savedRecipe.getId(), operationId);
        }

        MDC.clear();
        return savedRecipe;
    }

    @Transactional
    public void deleteRecipe(Long id) {
        String operationId = UUID.randomUUID().toString();
        MDC.put("operationId", operationId);
        MDC.put("recipeId", String.valueOf(id));
        MDC.put("time", LocalDateTime.now().format(FORMATTER));
        MDC.put("operation", "deleteRecipe");

        if (log.isDebugEnabled()) {
            log.debug("Deleting recipe with id: {}. OperationId: {}, Time: {}", 
                    id, operationId, LocalDateTime.now().format(FORMATTER));
        }

        recipeRepository.deleteById(id);

        if (log.isDebugEnabled()) {
            log.debug("Recipe deleted. OperationId: {}", operationId);
        }

        MDC.clear();
    }
}
