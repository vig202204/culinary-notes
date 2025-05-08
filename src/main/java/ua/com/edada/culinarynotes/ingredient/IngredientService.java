package ua.com.edada.culinarynotes.ingredient;

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
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    @Transactional(readOnly = true)
    public List<Ingredient> getAllIngredients() {
        log.debug("Getting all ingredients");
        return ingredientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Ingredient getIngredientById(Long id) {
        log.debug("Getting ingredient with id: {}", id);
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));
    }

    @Transactional(readOnly = true)
    public Optional<Ingredient> getIngredientByNameAndUnit(String name, String unit) {
        log.debug("Getting ingredient with name: {} and unit: {}", name, unit);
        return ingredientRepository.findByNameAndUnit(name, unit);
    }

    @Transactional(readOnly = true)
    public List<Ingredient> searchIngredientsByName(String name) {
        log.debug("Searching ingredients with name containing: {}", name);
        return ingredientRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Ingredient createIngredient(Ingredient ingredient) {
        log.debug("Creating new ingredient: {} ({})", ingredient.getName(), ingredient.getUnit());

        if (ingredientRepository.existsByNameAndUnit(ingredient.getName(), ingredient.getUnit())) {
            log.error("Ingredient already exists with name: {} and unit: {}",
                    ingredient.getName(), ingredient.getUnit());
            throw new IllegalArgumentException("Ingredient already exists with this name and unit");
        }

        return ingredientRepository.save(ingredient);
    }

    @Transactional
    public Ingredient updateIngredient(Long id, Ingredient ingredientDetails) {
        log.debug("Updating ingredient with id: {}", id);

        Ingredient ingredient = getIngredientById(id);

        if (!ingredient.getName().equals(ingredientDetails.getName()) ||
                !ingredient.getUnit().equals(ingredientDetails.getUnit())) {
            if (ingredientRepository.existsByNameAndUnit(
                    ingredientDetails.getName(), ingredientDetails.getUnit())) {
                log.error("Ingredient already exists with name: {} and unit: {}",
                        ingredientDetails.getName(), ingredientDetails.getUnit());
                throw new IllegalArgumentException("Ingredient already exists with this name and unit");
            }
        }

        ingredient.setName(ingredientDetails.getName());
        ingredient.setDescription(ingredientDetails.getDescription());
        ingredient.setUnit(ingredientDetails.getUnit());

        return ingredientRepository.save(ingredient);
    }

    @Transactional
    public void deleteIngredient(Long id) {
        log.debug("Deleting ingredient with id: {}", id);

        if (!ingredientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ingredient", "id", id);
        }

        ingredientRepository.deleteById(id);
    }
}