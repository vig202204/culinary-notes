package ua.com.edada.culinarynotes.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ua.com.edada.culinarynotes.category.Category;
import ua.com.edada.culinarynotes.category.CategoryService;
import ua.com.edada.culinarynotes.ingredient.Ingredient;
import ua.com.edada.culinarynotes.ingredient.IngredientService;
import ua.com.edada.culinarynotes.recipe.Recipe;
import ua.com.edada.culinarynotes.recipe.RecipeService;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RecipeService recipeService;
    private final CategoryService categoryService;
    private final IngredientService ingredientService;

    @Bean
    @Profile("!test") // Don't run in test profile
    public CommandLineRunner initData() {
        return args -> {
            // Check if we already have data
            List<Recipe> existingRecipes = recipeService.getAllRecipes();
            List<Category> existingCategories = categoryService.getAllCategories();
            List<Ingredient> existingIngredients = ingredientService.getAllIngredients();

            if (!existingRecipes.isEmpty() && !existingCategories.isEmpty() && !existingIngredients.isEmpty()) {
                return; // Skip initialization if we already have data
            }

            // Initialize categories if needed
            if (existingCategories.isEmpty()) {
                initializeCategories();
            }

            // Initialize ingredients if needed
            if (existingIngredients.isEmpty()) {
                initializeIngredients();
            }

            // Sample recipe 1: Ukrainian Borsch
            Recipe borsch = Recipe.builder()
                    .title("Ukrainian Borsch")
                    .description("Traditional Ukrainian beet soup with a rich and hearty flavor. Perfect for cold winter days!")
                    .instructions("""
                            1. In a large pot, sauté onions and carrots until soft.
                            2. Add beets and cook for 5 minutes.
                            3. Add potatoes, cabbage, and tomato paste, then pour in beef broth.
                            4. Simmer for 25-30 minutes until vegetables are tender.
                            5. Season with salt, pepper, and dill.
                            6. Serve hot with a dollop of sour cream and fresh bread.""")
                    .preparationTimeMinutes(20)
                    .cookingTimeMinutes(40)
                    .servings(6)
                    .build();

            recipeService.saveRecipe(borsch);

            // Sample recipe 2: Chicken Kyiv
            Recipe chickenKyiv = Recipe.builder()
                    .title("Chicken Kyiv")
                    .description("Classic Ukrainian dish of chicken breast pounded and rolled around herb butter, then breaded and fried.")
                    .instructions("""
                            1. Mix softened butter with chopped herbs, garlic, salt, and pepper.
                            2. Form into small logs and freeze for 30 minutes.
                            3. Pound chicken breasts until thin.
                            4. Place a butter log in the center of each breast and roll tightly.
                            5. Dip each roll in flour, then beaten egg, then breadcrumbs.
                            6. Fry in hot oil until golden brown and cooked through, about 8-10 minutes.
                            7. Serve hot with mashed potatoes and vegetables.""")
                    .preparationTimeMinutes(45)
                    .cookingTimeMinutes(15)
                    .servings(4)
                    .build();

            recipeService.saveRecipe(chickenKyiv);

            System.out.println("Sample recipes have been initialized!");
        };
    }

    private void initializeCategories() {
        System.out.println("Initializing categories...");

        // Create categories
        Category ukrainianCuisine = Category.builder()
                .name("Ukrainian Cuisine")
                .description("Traditional dishes from Ukraine")
                .build();

        Category soups = Category.builder()
                .name("Soups")
                .description("Warm and hearty soups for any occasion")
                .build();

        Category mainDishes = Category.builder()
                .name("Main Dishes")
                .description("Substantial dishes that form the centerpiece of a meal")
                .build();

        // Save categories
        categoryService.createCategory(ukrainianCuisine);
        categoryService.createCategory(soups);
        categoryService.createCategory(mainDishes);

        System.out.println("Categories have been initialized!");
    }

    private void initializeIngredients() {
        System.out.println("Initializing ingredients...");

        // Create ingredients for Ukrainian Borsch
        Ingredient beets = Ingredient.builder()
                .name("Beets")
                .description("Red root vegetable, essential for borsch")
                .unit("pieces")
                .build();

        Ingredient potatoes = Ingredient.builder()
                .name("Potatoes")
                .description("Starchy tubers used in many Ukrainian dishes")
                .unit("pieces")
                .build();

        Ingredient cabbage = Ingredient.builder()
                .name("Cabbage")
                .description("Leafy green or purple vegetable")
                .unit("grams")
                .build();

        Ingredient carrots = Ingredient.builder()
                .name("Carrots")
                .description("Orange root vegetable")
                .unit("pieces")
                .build();

        Ingredient onions = Ingredient.builder()
                .name("Onions")
                .description("Pungent bulb vegetable")
                .unit("pieces")
                .build();

        // Create ingredients for Chicken Kyiv
        Ingredient chicken = Ingredient.builder()
                .name("Chicken Breast")
                .description("Boneless, skinless chicken breast")
                .unit("pieces")
                .build();

        Ingredient butter = Ingredient.builder()
                .name("Butter")
                .description("Dairy product made from milk fat")
                .unit("grams")
                .build();

        Ingredient breadcrumbs = Ingredient.builder()
                .name("Breadcrumbs")
                .description("Dried, ground bread used for coating")
                .unit("grams")
                .build();

        Ingredient herbs = Ingredient.builder()
                .name("Fresh Herbs")
                .description("Mix of parsley, dill, and other herbs")
                .unit("grams")
                .build();

        // Save ingredients
        ingredientService.createIngredient(beets);
        ingredientService.createIngredient(potatoes);
        ingredientService.createIngredient(cabbage);
        ingredientService.createIngredient(carrots);
        ingredientService.createIngredient(onions);
        ingredientService.createIngredient(chicken);
        ingredientService.createIngredient(butter);
        ingredientService.createIngredient(breadcrumbs);
        ingredientService.createIngredient(herbs);

        System.out.println("Ingredients have been initialized!");
    }
}
