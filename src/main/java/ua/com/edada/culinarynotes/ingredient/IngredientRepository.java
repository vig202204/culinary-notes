package ua.com.edada.culinarynotes.ingredient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByNameAndUnit(String name, String unit);
    List<Ingredient> findByNameContainingIgnoreCase(String name);
    boolean existsByNameAndUnit(String name, String unit);
}