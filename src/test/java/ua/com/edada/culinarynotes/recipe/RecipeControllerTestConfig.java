package ua.com.edada.culinarynotes.recipe;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecipeControllerTestConfig {

    @Bean
    public RecipeService recipeService() {
        return Mockito.mock(RecipeService.class);
    }
}