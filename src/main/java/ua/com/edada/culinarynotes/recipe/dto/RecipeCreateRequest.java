package ua.com.edada.culinarynotes.recipe.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecipeCreateRequest(
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    String title,

    String description,

    String instructions,

    @Min(value = 0, message = "Preparation time cannot be negative")
    Integer preparationTimeMinutes,

    @Min(value = 0, message = "Cooking time cannot be negative")
    Integer cookingTimeMinutes,

    @Min(value = 1, message = "Servings must be at least 1")
    Integer servings
) {}